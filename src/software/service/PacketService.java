package software.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import software.beans.PacketInfo;
import software.beans.property.PacketInfoTableData;
import software.utils.FileUtil;
import software.utils.JsonUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PacketService implements Runnable {

    private final NetworkInterface device = NICService.getDevice();
    private JpcapCaptor jc;
    private boolean threadStop = false;
    private String filter = "";

    public void setThreadStop(boolean threadStop) {
        this.threadStop = threadStop;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public void run() {
        try {
            jc = JpcapCaptor.openDevice(device, 1512, true, 50);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!threadStop) {
            Packet packet = jc.getPacket();
            if (packet instanceof IPPacket && ((IPPacket) packet).version == 4) {
                IPPacket ip = (IPPacket) packet;//强转

                String protocol = "";
                switch ((int) ip.protocol) {
                    case 1:
                        protocol = "ICMP";
                        break;
                    case 2:
                        protocol = "IGMP";
                        break;
                    case 6:
                        protocol = "TCP";
                        break;
                    case 8:
                        protocol = "EGP";
                        break;
                    case 9:
                        protocol = "IGP";
                        break;
                    case 17:
                        protocol = "UDP";
                        break;
                    case 41:
                        protocol = "IPv6";
                        break;
                    case 89:
                        protocol = "OSPF";
                        break;
                    default:
                        break;
                }
                System.out.println("版本：IPv4");
                System.out.println("优先权：" + ip.priority);
                System.out.println("区分服务：最大的吞吐量： " + ip.t_flag);
                System.out.println("区分服务：最高的可靠性：" + ip.r_flag);
                System.out.println("长度：" + ip.length);
                System.out.println("标识：" + ip.ident);
                System.out.println("DF:Don't Fragment: " + ip.dont_frag);
                System.out.println("NF:Nore Fragment: " + ip.more_frag);
                System.out.println("片偏移：" + ip.offset);
                System.out.println("生存时间：" + ip.hop_limit);
                Timestamp timestamp = new Timestamp((packet.sec * 1000) + (packet.usec / 1000));
                StringBuilder tempInfo = new StringBuilder();
                for (int j = 0; j < packet.data.length; j++) {
                    tempInfo.append(packet.data[j]);
                }
                try {
                    PacketInfo packetInfo = new PacketInfo(timestamp.toString(), ip.src_ip.getHostAddress(), ip.dst_ip.getHostAddress(),
                            protocol, String.valueOf(packet.data.length), tempInfo.toString());
                    savePacketInfo(packetInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  }
            }
        }
    }

    /**
     * 过滤器
     */
    public List<PacketInfo> getFiltered() {
        List<PacketInfo> fPackets = new ArrayList<>();
        for (int i = 0; i < getPacketInfoList().size(); i++) {
            PacketInfo p = getPacketInfoList().get(i);
            if (p.getProtocol().equals(filter) || p.getSource().equals(filter) || p.getDestination().equals(filter)) {
                fPackets.add(p);
            }
        }
        return fPackets;
    }

    public static void savePacketInfo(PacketInfo packetInfo) throws IOException {
        String data = JsonUtil.toJson(packetInfo);
        data = data.replace("\n", "");
        FileUtil.writeData(data, "Packet.data", true);
    }

    public List<PacketInfo> getPacketInfoList() {
        List<PacketInfo> packetInfos = new ArrayList<>();
        List<Object> infos = FileUtil.getData("Packet.data", PacketInfo.class);
        for (Object o : infos) {
            PacketInfo p = (PacketInfo) o;
            if (p != null) {
                packetInfos.add(p);
            }
        }
        return packetInfos;
    }

    public ObservableList<PacketInfoTableData> getPacketInfoTableViewData() {
        ObservableList<PacketInfoTableData> packetList = FXCollections.observableArrayList();

        if (!filter.equals("")) {
            for (PacketInfo pi : getFiltered()) {
                PacketInfoTableData piTD = new PacketInfoTableData(pi.getTime(), pi.getSource(), pi.getDestination(), pi.getProtocol(), pi.getLength(), pi.getInfo());
                packetList.add(piTD);
            }

        } else {
            for (PacketInfo pi : getPacketInfoList()) {
                PacketInfoTableData piTD = new PacketInfoTableData(pi.getTime(), pi.getSource(), pi.getDestination(), pi.getProtocol(), pi.getLength(), pi.getInfo());
                packetList.add(piTD);
            }
        }

        return packetList;
    }

    public void setPacketInfoTableViewData(TableView<PacketInfoTableData> tv_packet, ObservableList<PacketInfoTableData> dataList,
                                           TableColumn<PacketInfoTableData, String> tc_time, TableColumn<PacketInfoTableData, String> tc_source,
                                           TableColumn<PacketInfoTableData, String> tc_des, TableColumn<PacketInfoTableData, String> tc_pro,
                                           TableColumn<PacketInfoTableData, String> tc_length, TableColumn<PacketInfoTableData, String> tc_info) {
        tc_time.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        tc_source.setCellValueFactory(cellData -> cellData.getValue().sourceProperty());
        tc_des.setCellValueFactory(cellData -> cellData.getValue().destinationProperty());
        tc_pro.setCellValueFactory(cellData -> cellData.getValue().protocolProperty());
        tc_length.setCellValueFactory(cellData -> cellData.getValue().lengthProperty());
        tc_info.setCellValueFactory(cellData -> cellData.getValue().infoProperty());
        tv_packet.setItems(dataList);
    }

    /**
     * 获取对应行的info
     */
    public void getPacketInfo(TextArea textArea, TableView<PacketInfoTableData> tableView) {

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> textArea.setText(newValue.getInfo())
        );
    }

}
