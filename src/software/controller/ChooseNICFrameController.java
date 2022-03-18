package software.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import software.Main;
import software.beans.property.NICInfoTableData;
import software.service.NICService;
import software.utils.FileUtil;
import software.utils.SimpleUtil;

import java.io.IOException;

public class ChooseNICFrameController {

    @FXML
    private Stage NICStage;

    public void setNICStage(Stage stage) {
        this.NICStage = stage;
    }

    @FXML
    private AnchorPane rootPane;

    @FXML
    private BorderPane nicPane;

    @FXML
    private TextField tf_chosen;

    @FXML
    private TableView<NICInfoTableData> tv_NIC;

    @FXML
    private TableColumn<NICInfoTableData, String> tc_num;

    @FXML
    private TableColumn<NICInfoTableData, String> tc_nicName;

    @FXML
    private TableColumn<NICInfoTableData, String> tc_nicIP;

    @FXML
    private TableColumn<NICInfoTableData, String> tc_nicMAC;

    private final NICService nicService = new NICService();
    private final SimpleUtil simpleUtil = new SimpleUtil();
    public static NetworkInterface device;

    public void initialize() throws IOException {
        nicService.getNetworkInterface();
        //将数据添加到表格控件中
        nicService.setNICInfoTableViewData(tv_NIC, nicService.getNICInfoTableViewData(), tc_num, tc_nicName, tc_nicIP, tc_nicMAC);
        nicService.chooseNIC(tf_chosen, tv_NIC);
        FileUtil.clearFile("Packet.data");
    }


    @FXML
    void bt_chooseEvent() {
        device = JpcapCaptor.getDeviceList()[Integer.parseInt(tf_chosen.getText()) - 1];
        simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "提示", "网卡选择完毕！", "可以进入下一步操作");
        System.out.println("选择了网卡" + Integer.parseInt(tf_chosen.getText()));
    }

    @FXML
    void chooseNICEvent() {
        rootPane.getChildren().clear();
        rootPane.getChildren().add(nicPane);
    }

    @FXML
    void analysisEvent() {
        Pane pane = new Main().initPacketFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void scanHostEvent() {
        Pane pane = new Main().initScanHostFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void arpAttackEvent() {
        Pane pane = new Main().initARPAttackFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void closeEvent() {
        FileUtil.clearFile("Host.data");
        FileUtil.clearFile("Packet.data");
        NICStage.close();
    }

    @FXML
    void ddosAttackEvent() {
        Pane pane = new Main().initDDoSAttackFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void extendEvent() {
        Pane pane = new Main().initExtendFrame();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);
    }

    @FXML
    void minusEvent() {
        NICStage.setIconified(true);
    }
}
