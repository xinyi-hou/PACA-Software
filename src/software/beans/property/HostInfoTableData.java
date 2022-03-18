package software.beans.property;

import javafx.beans.property.SimpleStringProperty;

public class HostInfoTableData {
    private final SimpleStringProperty hostIP;
    private final SimpleStringProperty hostMAC;

    public HostInfoTableData(String hostIP, String hostMAC) {
        this.hostIP = new SimpleStringProperty(hostIP);
        this.hostMAC = new SimpleStringProperty(hostMAC);
    }

    public String getHostIP() {
        return hostIP.get();
    }

    public SimpleStringProperty hostIPProperty() {
        return hostIP;
    }

    public SimpleStringProperty hostMACProperty() {
        return hostMAC;
    }
}
