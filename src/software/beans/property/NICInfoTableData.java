package software.beans.property;

import javafx.beans.property.SimpleStringProperty;

public class NICInfoTableData {
    private final SimpleStringProperty nicNum;
    private final SimpleStringProperty nicName;
    private final SimpleStringProperty nicIP;
    private final SimpleStringProperty nicMAC;

    public NICInfoTableData(String nicNum, String nicName, String nicIP, String nicMAC) {
        this.nicNum = new SimpleStringProperty(nicNum);
        this.nicName = new SimpleStringProperty(nicName);
        this.nicIP = new SimpleStringProperty(nicIP);
        this.nicMAC = new SimpleStringProperty(nicMAC);
    }

    public String getNicNum() {
        return nicNum.get();
    }

    public SimpleStringProperty nicNumProperty() {
        return nicNum;
    }

    public SimpleStringProperty nicNameProperty() {
        return nicName;
    }

    public SimpleStringProperty nicIPProperty() {
        return nicIP;
    }

    public SimpleStringProperty nicMACProperty() {
        return nicMAC;
    }
}
