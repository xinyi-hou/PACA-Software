package software.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import software.beans.property.HostInfoTableData;
import software.service.ARPService;
import software.service.HostService;
import software.service.NICService;
import software.utils.NetUtil;
import software.utils.SetNumUtil;
import software.utils.SimpleUtil;

public class ARPAttackFrameController {

    @FXML
    private TableView<HostInfoTableData> tv_host;

    @FXML
    private TableColumn<HostInfoTableData, String> tc_num;

    @FXML
    private TableColumn<HostInfoTableData, String> tc_ip;

    @FXML
    private TableColumn<HostInfoTableData, String> tc_mac;

    @FXML
    private TextField tf_desIP;

    @FXML
    private TextField tf_srcIP;

    @FXML
    private TextField tf_srcMAC;

    @FXML
    private TextField tf_attIP;

    @FXML
    private Text sending;

    private final ARPService arpService = new ARPService();
    private static final HostService hostService = new HostService();
    private final SimpleUtil simpleUtil = new SimpleUtil();
    private final NetUtil netUtil = new NetUtil();

    public void initialize() {
        System.out.println("进入ARPAttackFrameController");
        hostService.setHostInfoTableViewData(tv_host, hostService.getHostInfoTableViewData(), tc_ip, tc_mac);
        tc_num.setCellFactory(new SetNumUtil<>());
        sending.setVisible(false);
        arpService.getHostIP(tf_desIP,tv_host);
        arpService.setNetworkInterface(NICService.getDevice());
    }

    @FXML
    void startAttackEvent() {
        if (simpleUtil.isTextEmpty(tf_desIP.getText())) {
            if (simpleUtil.isTextEmpty(tf_srcIP.getText())) {
                if (simpleUtil.isTextEmpty(tf_srcMAC.getText())) {
                    if (simpleUtil.isTextEmpty(tf_attIP.getText())) {
                        if (netUtil.judgeIP(tf_desIP.getText()) & netUtil.judgeIP(tf_srcIP.getText())
                                & netUtil.judgeIP(tf_attIP.getText())) {
                            if (netUtil.judgeMAC(tf_srcMAC.getText())) {
                                arpService.setDesIP(tf_desIP.getText());
                                arpService.setSrcIP(tf_srcIP.getText());
                                arpService.setSrcMAC(tf_srcMAC.getText());
                                arpService.setAttIP(tf_attIP.getText());
                                arpService.setThreadStop(false);

                                new Thread(arpService).start();

                                simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "提示", "攻击成功！", "请进入虚拟机查看攻击效果");
                                sending.setVisible(true);
                            } else
                                simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "MAC地址格式有误", "请检查输入");
                        } else
                            simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "IP地址格式有误", "请检查输入");
                    } else
                        simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "本机(攻击方)IP地址不能为空！", "请检查输入");
                } else
                    simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "源MAC地址不能为空！", "请检查输入");
            } else
                simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "源IP地址不能为空！", "请检查输入");
        } else
            simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "目的(被攻击)主机IP地址不能为空！", "请检查输入");

    }

    @FXML
    void startAttackEvent2() {
        if (simpleUtil.isTextEmpty(tf_desIP.getText())) {
            if (simpleUtil.isTextEmpty(tf_srcIP.getText())) {
                if (simpleUtil.isTextEmpty(tf_srcMAC.getText())) {
                    if (simpleUtil.isTextEmpty(tf_attIP.getText())) {
                        if (netUtil.judgeIP(tf_desIP.getText()) & netUtil.judgeIP(tf_srcIP.getText())
                                & netUtil.judgeIP(tf_attIP.getText())) {
                            if (netUtil.judgeMAC(tf_srcMAC.getText())) {
                                arpService.setDesIP(tf_desIP.getText());
                                arpService.setSrcIP(tf_srcIP.getText());
                                arpService.setSrcMAC(tf_srcMAC.getText());
                                arpService.setAttIP(tf_attIP.getText());
                                arpService.setThreadStop(false);
                                arpService.setIfTwoWay(true);

                                new Thread(arpService).start();

                                simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "提示", "攻击成功！", "请进入虚拟机查看攻击效果");
                                sending.setVisible(true);
                            } else
                                simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "MAC地址格式有误", "请检查输入");
                        } else
                            simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "IP地址格式有误", "请检查输入");
                    } else
                        simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "本机(攻击方)IP地址不能为空！", "请检查输入");
                } else
                    simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "源MAC地址不能为空！", "请检查输入");
            } else
                simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "源IP地址不能为空！", "请检查输入");
        } else
            simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "目的(被攻击)主机IP地址不能为空！", "请检查输入");
    }

    @FXML
    void stopAttackEvent() {
        arpService.setDesIP("0.0.0.0");
        arpService.setThreadStop(true);
        simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "提示", "攻击结束！", "请进入虚拟机进行查看");
        sending.setVisible(false);
    }

}
