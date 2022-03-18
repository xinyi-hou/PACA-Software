package software.service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import software.beans.NICInfo;
import software.beans.property.NICInfoTableData;
import software.utils.FileUtil;
import software.utils.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static software.utils.NetUtil.macToStr;

public class NICService {

    public void getNetworkInterface() throws IOException {
        /* 枚举网卡 */
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        FileUtil.clearFile("NICInfo.data");
        for (int i = 0; i < devices.length; i++) {
            NICInfo nicInfo = new NICInfo(String.valueOf(i + 1),
                    devices[i].description,
                    devices[i].addresses[1].address.getHostAddress(),
                    macToStr(devices[i].mac_address));
            saveNICInfo(nicInfo);
        }
    }

    /*将网卡信息保存至文件*/
    public static void saveNICInfo(NICInfo nicInfo) throws IOException {
        String data = JsonUtil.toJson(nicInfo);
        data = data.replace("\n", "");
        FileUtil.writeData(data, "NICInfo.data", true);
    }

    public List<NICInfo> getNICInfoList() {
        List<NICInfo> nicInfos = new ArrayList<>();
        List<Object> infos = FileUtil.getData("NICInfo.data", NICInfo.class);
        for (Object o : infos) {
            NICInfo n = (NICInfo) o;
            if (n != null) {
                nicInfos.add(n);
            }
        }
        return nicInfos;
    }

    public void setNICInfoTableViewData(TableView<NICInfoTableData> tableView, ObservableList<NICInfoTableData> dataList,
                                        TableColumn<NICInfoTableData, String> tc_nicNum,
                                        TableColumn<NICInfoTableData, String> tc_nicName,
                                        TableColumn<NICInfoTableData, String> tc_nicIP,
                                        TableColumn<NICInfoTableData, String> tc_nicMAC) {

        tc_nicNum.setCellValueFactory(cellData -> cellData.getValue().nicNumProperty());
        tc_nicName.setCellValueFactory(cellData -> cellData.getValue().nicNameProperty());
        tc_nicIP.setCellValueFactory(cellData -> cellData.getValue().nicIPProperty());
        tc_nicMAC.setCellValueFactory(cellData -> cellData.getValue().nicMACProperty());
        //将数据添加到表格控件中
        tableView.setItems(dataList);

    }

    public ObservableList<NICInfoTableData> getNICInfoTableViewData() {
        ObservableList<NICInfoTableData> nicList = FXCollections.observableArrayList();
        for (NICInfo ni : getNICInfoList()) {
            NICInfoTableData niTD = new NICInfoTableData(ni.getNicNum(), ni.getNicName(), ni.getNicIP(), ni.getNicMAC());
            nicList.add(niTD);
        }
        return nicList;
    }

    public static NetworkInterface device;

    public static NetworkInterface getDevice() {
        return device;
    }

    /**
     * 获取所选网卡对应的序号
     */
    public void chooseNIC(TextField tf_chosen, TableView<NICInfoTableData> tableView) {

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    tf_chosen.setText(newValue.getNicNum());
                    device = JpcapCaptor.getDeviceList()[Integer.parseInt(tf_chosen.getText()) - 1];
                }
        );

    }

}
