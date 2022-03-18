package software.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import software.beans.property.HostInfoTableData;
import software.service.HostService;
import software.service.NICService;
import software.utils.FileUtil;
import software.utils.SetNumUtil;

public class ScanHostFrameController {

    @FXML
    private TextArea scanArea;

    @FXML
    private TableView<HostInfoTableData> tb_host;

    @FXML
    private TableColumn<HostInfoTableData, String> tc_num;

    @FXML
    private TableColumn<HostInfoTableData, String> tc_hostIP;

    @FXML
    private TableColumn<HostInfoTableData, String> tc_hostMAC;

    private final HostService hostService = new HostService();

    public void initialize() {
        System.out.println("进入ScanHostFrameController");
       hostService.setHostInfoTableViewData(tb_host, hostService.getHostInfoTableViewData(), tc_hostIP, tc_hostMAC);

        tc_num.setCellFactory(new SetNumUtil<>());
    }

    @FXML
    void refreshEvent() {
        tb_host.setItems(hostService.getHostInfoTableViewData());
    }

    @FXML
    void startScanEvent() {
        FileUtil.clearFile("Host.data");
        tb_host.setItems(hostService.getHostInfoTableViewData());
        hostService.setNetworkInterface(NICService.getDevice());
        hostService.setTextArea(scanArea);
        new Thread(hostService).start();
    }


}
