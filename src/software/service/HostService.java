package software.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import software.beans.HostInfo;
import software.beans.property.HostInfoTableData;
import software.utils.FileUtil;
import software.utils.JsonUtil;
import software.utils.SimpleUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static software.utils.NetUtil.macToStr;
import static software.utils.NetUtil.strToMac;

/**
 * 扫描当前局域网存活的主机
 *
 * @author 侯心怡
 * @class 软信1902
 * @StudentID 20195782
 * @date 2022-03-06
 */
public class HostService implements Runnable {
    private NetworkInterface dev;
    private TextArea textArea;

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.dev = networkInterface;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void run() {
        ArrayList<String> hostList = new ArrayList<>();
        System.out.println("进入arpScan");
        JpcapCaptor captor;
        try {
            captor = JpcapCaptor.openDevice(dev, 2000, false, 200);
            // 过滤出第 7 到 7+1 字节值为 2 的 arp 分组(ARP Reply)
            captor.setFilter("arp[7:1] == 2", true);

            Map<String, byte[]> ipAndMac = new HashMap<>();
            JpcapSender sender = captor.getJpcapSenderInstance();
            String netAddress = getGatewayAddress(dev);
            List<String> ips = getAllIP(netAddress);

            for (String ip : ips) {
                ARPPacket packet = createARP(ARPPacket.ARP_REQUEST,
                        dev.addresses[1].address.getHostAddress(),
                        macToStr(dev.mac_address),
                        ip, "00-00-00-00-00-00");
                sender.sendPacket(packet);

                /* 抓取回复 */
                ARPPacket p = (ARPPacket) captor.getPacket();
                if (p == null || !Arrays.equals(p.target_hardaddr, dev.mac_address)) {
                    System.out.println("\"" + ip + "\" isn't exits.");

                    String data1 = "\"" + ip + "\" isn't exits.";
                    hostList.add(0, data1);
                    textArea.setText(FileUtil.ListToStr(hostList));
                } else {
                    System.out.println("\"" + ip + "\"'s mac address is: " + macToStr(p.sender_hardaddr));

                    String data2 = "\"" + ip + "\"'s mac address is: " + macToStr(p.sender_hardaddr);
                    hostList.add(0, data2);
                    textArea.setText(FileUtil.ListToStr(hostList));

                    HostInfo hostInfo = new HostInfo(ip, macToStr(p.sender_hardaddr));
                    saveHostInfo(hostInfo);
                    ipAndMac.put(ip, p.sender_hardaddr);
                }
            }
            System.out.println("\nARP 扫描结束！扫描到局域网内存活主机信息如下：");
            for (String ip : ipAndMac.keySet()) {
                System.out.println("ip: " + ip + "  mac:" + macToStr(ipAndMac.get(ip)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取网络接口设备的网关 ip (只能获取网关是 x.x.x.2的情况)
     *
     * @param device 网络接口设备
     */
    public static String getGatewayAddress(NetworkInterface device) {
        //通过设备 ip 地址获取子网地址，进而获取子网内所有 ip 地址
        String hostIPAddress = device.addresses[1].address.getHostAddress();
        String[] addressFlags = hostIPAddress.split("\\.");
        return addressFlags[0] + "." + addressFlags[1] +
                "." + addressFlags[2] + ".2";
    }

    /**
     * 获取子网内的所有 ip 地址(广播地址和网络地址除外)
     *
     * @param netAddress 网络地址 形如 "192.168.31.0" （默认只能处理.0结尾）
     * @return x.x.x.1~x.x.x.254 地址集合
     */
    public static List<String> getAllIP(String netAddress) {
        List<String> ips = new ArrayList<>();
        String ip = netAddress.substring(0, netAddress.length() - 1);
        for (int i = 1; i < 255; i++) {
            ips.add(ip + i);
        }
        return ips;
    }

    /**
     * 构造一个用以太封装的 ARP 分组
     *
     * @param operation Request=1, Reply=2
     * @param srcIP     点分十进制表示的源 ip 地址
     * @param srcMac    形如 "00-0C-29-95-AF-C8" 的源 mac 地址
     * @param desIp     点分十进制表示的源 ip 地址
     * @param desMac    形如 "00-0C-29-95-AF-C8" 的目的 mac 地址
     * @return 以太封装的 ARP 分组
     * @throws UnknownHostException ip 地址不合法
     */
    public static ARPPacket createARP(short operation, String srcIP, String srcMac, String desIp, String desMac)
            throws UnknownHostException {
        // 构造ARP包
        ARPPacket arpPacket = new ARPPacket();
        InetAddress srcip = InetAddress.getByName(srcIP);
        byte[] srcmac = strToMac(srcMac);
        InetAddress desip = InetAddress.getByName(desIp);
        byte[] desmac = strToMac(desMac);

        arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;   //硬件类型
        arpPacket.prototype = ARPPacket.PROTOTYPE_IP;    //协议类型
        arpPacket.hlen = 6;                              //物理地址长度
        arpPacket.plen = 4;                              //协议地址长度
        arpPacket.operation = operation;                 //包类型：请求或应答
        arpPacket.sender_hardaddr = srcmac;              //ARP包的发送端以太网地址
        arpPacket.sender_protoaddr = srcip.getAddress(); //发送端IP地址
        arpPacket.target_hardaddr = desmac;              //设置目的端的以太网地址
        arpPacket.target_protoaddr = desip.getAddress(); //目的端IP地址

        /* 构造以太帧首部 */
        EthernetPacket ethernetPacket = new EthernetPacket();
        ethernetPacket.frametype = EthernetPacket.ETHERTYPE_ARP;    //帧类型
        ethernetPacket.src_mac = srcmac;                            //源MAC地址
        if (operation == ARPPacket.ARP_REQUEST) {
            ethernetPacket.dst_mac = strToMac("ff-ff-ff-ff-ff-ff"); //以太网目的地址，广播地址
        } else {
            ethernetPacket.dst_mac = desmac;
        }

        arpPacket.datalink = ethernetPacket;  //将arp报文的数据链路层的帧设置为刚刚构造的以太帧

        return arpPacket;
    }

    /**
     * 将存活主机信息保存至文件
     *
     * @param hostInfo HostInfo对象
     */
    public static void saveHostInfo(HostInfo hostInfo) throws IOException {
        String data = JsonUtil.toJson(hostInfo);
        data = data.replace("\n", "");
        FileUtil.writeData(data, "Host.data", true);
    }

    public List<HostInfo> getHostInfoList() {

        List<HostInfo> hostInfos = new ArrayList<>();
        List<Object> infos = FileUtil.getData("Host.data", HostInfo.class);
        for (Object o : infos) {
            HostInfo h = (HostInfo) o;
            if (h != null) {
                hostInfos.add(h);
            }
        }
        return hostInfos;
    }

    public void setHostInfoTableViewData(TableView<HostInfoTableData> tb_host, ObservableList<HostInfoTableData> dataList,
                                         TableColumn<HostInfoTableData, String> tc_hostIP,
                                         TableColumn<HostInfoTableData, String> tc_hostMAC) {
        tc_hostIP.setCellValueFactory(cellData -> cellData.getValue().hostIPProperty());
        tc_hostMAC.setCellValueFactory(cellData -> cellData.getValue().hostMACProperty());
        tb_host.setItems(dataList);
    }

    public ObservableList<HostInfoTableData> getHostInfoTableViewData() {
        ObservableList<HostInfoTableData> hostList = FXCollections.observableArrayList();
        for (HostInfo hi : getHostInfoList()) {
            HostInfoTableData hiTD = new HostInfoTableData(hi.getHostIP(), hi.getHostMAC());
            hostList.add(hiTD);
        }
        return hostList;
    }
}
