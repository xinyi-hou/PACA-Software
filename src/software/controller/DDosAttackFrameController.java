package software.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import software.service.DDoSService;
import software.utils.FileUtil;
import software.utils.NetUtil;
import software.utils.SimpleUtil;

public class DDosAttackFrameController {

    @FXML
    private TextField tf_desIP;

    @FXML
    private TextArea attackArea;

    private final DDoSService dDoSService = new DDoSService();
    private final NetUtil netUtil = new NetUtil();
    private final SimpleUtil simpleUtil = new SimpleUtil();

    public void initialize() {
        System.out.println("进入DDosAttackFrameController");
        // dDoSService.setTextArea(attackArea);
        FileUtil.clearFile("DDoS.data");
    }

    @FXML
    void startEvent() {
        dDoSService.setThreadStop(false);
        if (simpleUtil.isTextEmpty(tf_desIP.getText())) {
            if (netUtil.judgeIP(tf_desIP.getText())) {
                dDoSService.setDesIP(tf_desIP.getText());
                new Thread(dDoSService).start();
                attackArea.setText("DDoS攻击成功");
                // dDoSService.setTextArea(attackArea);
                simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "提示", "攻击成功！", "请进入虚拟机查看攻击效果");

            }else
                simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "IP地址格式有误", "请检查输入");
        } else
            simpleUtil.informationDialog(Alert.AlertType.WARNING, "警告", "IP地址不能为空！", "请检查输入");
        dDoSService.setThreadStop(true);
    }

    @FXML
    void stopEvent() {
        dDoSService.setThreadStop(true);
        simpleUtil.informationDialog(Alert.AlertType.INFORMATION, "提示", "攻击停止！", "");
    }

}
