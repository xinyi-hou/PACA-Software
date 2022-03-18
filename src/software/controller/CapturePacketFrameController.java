package software.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import software.beans.property.PacketInfoTableData;
import software.service.PacketService;
import software.utils.FileUtil;
import software.utils.SetNumUtil;
import software.utils.SimpleUtil;

public class CapturePacketFrameController {

    @FXML
    private AnchorPane packetPane;

    @FXML
    private TableView<PacketInfoTableData> tv_packet;

    @FXML
    private TextField tf_filter;

    @FXML
    private TableColumn<PacketInfoTableData, String> tc_num;

    @FXML
    private TableColumn<PacketInfoTableData, String> tc_time;

    @FXML
    private TableColumn<PacketInfoTableData, String> tc_source;

    @FXML
    private TableColumn<PacketInfoTableData, String> tc_des;

    @FXML
    private TableColumn<PacketInfoTableData, String> tc_pro;

    @FXML
    private TableColumn<PacketInfoTableData, String> tc_length;

    @FXML
    private TableColumn<PacketInfoTableData, String> tc_info;

    @FXML
    private TextArea infoArea;

    private final PacketService packetService = new PacketService();
    private final SimpleUtil simpleUtil = new SimpleUtil();

    public void initialize() {
        System.out.println("进入CapturePacketFrameController");
        tc_num.setCellFactory(new SetNumUtil<>());
        packetService.setPacketInfoTableViewData(tv_packet, packetService.getPacketInfoTableViewData(),
                tc_time, tc_source, tc_des, tc_pro, tc_length, tc_info);
        packetService.getPacketInfo(infoArea,tv_packet);
    }


    @FXML
    void clearEvent(ActionEvent event) {
        tv_packet.getItems().clear();
        infoArea.clear();
    }

    @FXML
    void okEvent() {
         packetService.setFilter(tf_filter.getText());
        tv_packet.setItems(packetService.getPacketInfoTableViewData());
    }

    @FXML
    void stopEvent() {
        packetService.setThreadStop(true);
        simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "提示", "已停止抓包！", "");
    }

    @FXML
    void refreshEvent() {
        tv_packet.setItems(packetService.getPacketInfoTableViewData());
    }

    @FXML
    void startEvent() {
        new Thread(packetService).start();
    }

}
