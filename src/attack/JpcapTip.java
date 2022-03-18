package attack;

/*******************
 * JpcapTip.java
 */
import jpcap.PacketReceiver;
import jpcap.JpcapCaptor;
import jpcap.packet.*;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
public class JpcapTip implements PacketReceiver {

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    public void receivePacket(Packet packet) {
        System.out.println("********************************************");
        byte[] l=packet.header;
        int i=0;
        String str="";
        System.out.print("数据链路层帧 : ");
        for (i=0;i<l.length;i++) {
            byte m;
            m=l[i];
            System.out.print(byteToBit(m)+" ");
        }

        System.out.println(" ");
        int d=l.length;
        System.out.println("链路层帧长度 ："+(d*8)+"bit");

        /*分析源IP地址和目的IP地址*/
        /*分析协议类型*/

        if(packet.getClass().equals(IPPacket.class)) {
            IPPacket ipPacket=(IPPacket)packet;
            byte[] iph=ipPacket.option;
            String iphstr=new String(iph);
            System.out.println(iphstr);
        }

        if(packet.getClass().equals(ARPPacket.class))
        {
            System.out.println("协议类型 ：ARP协议");
            try {
                ARPPacket arpPacket = (ARPPacket)packet;
                System.out.println("源网卡MAC地址为 ："+arpPacket.getSenderHardwareAddress());
                System.out.println("源IP地址为 ："+arpPacket.getSenderProtocolAddress());
                System.out.println("目的网卡MAC地址为 ："+arpPacket.getTargetHardwareAddress());
                System.out.println("目的IP地址为 ："+arpPacket.getTargetProtocolAddress());
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
        else
        if(packet.getClass().equals(UDPPacket.class))
        {
            System.out.println("协议类型 ：UDP协议");
            try {
                UDPPacket udpPacket = (UDPPacket)packet;
                System.out.println("源IP地址为 ："+udpPacket.src_ip);
                int tport = udpPacket.src_port;
                System.out.println("源端口为："+tport);
                System.out.println("目的IP地址为 ："+udpPacket.dst_ip);
                int lport = udpPacket.dst_port;
                System.out.println("目的端口为："+lport);
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
        else
        if(packet.getClass().equals(TCPPacket.class)) {
            System.out.println("协议类型 ：TCP协议");
            try {
                TCPPacket tcpPacket = (TCPPacket)packet;
                int tport = tcpPacket.src_port;
                System.out.println("源IP地址为 ："+tcpPacket.src_ip);
                System.out.println("源端口为："+tport);
                System.out.println("目的IP地址为 ："+tcpPacket.dst_ip);
                int lport = tcpPacket.dst_port;
                System.out.println("目的端口为："+lport);
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
        else
        if(packet.getClass().equals(ICMPPacket.class))
            System.out.println("协议类型 ：ICMP协议");
        else
            System.out.println("协议类型 ：GGP、EGP、JGP协议或OSPF协议或ISO的第4类运输协议TP4");
        /*IP数据报文数据*/
        byte[] k=packet.data;
        String str1="";
        System.out.print("数据 : ");
        for(int j=0;j<k.length;j++) {
            int m=0;
            m=k[j];
            m=m<<24;
            m=m>>>24;
            str1=str+Integer.toHexString(m);
            str1 = new String(k);
            str1=str1+k[j];
            System.out.print("     ***     "+k[j]);
        }
        System.out.println(str1);
        System.out.println("数据报类型 : "+packet.getClass());
        System.out.println("********************************************");
    }

    public static void main(String[] args) throws Exception{
        //数组中每一NetworkInterface元素对象代表一个网络接口
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();        //.getDeviceList();.
        int a=0;
        /*本地网络信息*/
        byte[] b=devices[3].mac_address; //网卡物理地址
        System.out.print("网卡MAC : ");
        for (int j=0;j<b.length;j++){
            //byte变成int时，根据最高位进行填充，如1100 1100变成1111...1100 1100,即cc变成-52
            a=b[j];
            a=a<<24;
            a=a>>>24;
            if(a==b[5]) {
                System.out.print(Integer.toHexString(a));
            }else {
                System.out.print(Integer.toHexString(a)+"-");
            }
        }
        System.out.println();

        NetworkInterfaceAddress[] k=devices[3].addresses;
        for(int n=0;n<k.length;n++) {
            System.out.println("本机IP地址 : "+k[n].address);     //本机IP地址
            System.out.println("子网掩码   : "+k[n].subnet);      //子网掩码
        }
        System.out.println("网络连接类型 : "+devices[3].datalink_description);
        //}
        NetworkInterface deviceName = devices[3];
        /*将网卡设为混杂模式下用网络设备3，设置抓取数据包的最大长度2000字节，混杂模式选否，超时值1*/
        JpcapCaptor jpcap =JpcapCaptor.openDevice(deviceName, 2000, false, 1);           //openDevice(deviceName,1028,false,1);
        //调用loopPacket方法处理数据包，-1代表捕获的最大包数没有限制，
        jpcap.loopPacket(-1,new JpcapTip());
    }
}
