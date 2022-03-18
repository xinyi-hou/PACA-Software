package software.beans.property;

import javafx.beans.property.SimpleStringProperty;

public class PacketInfoTableData {
    private final SimpleStringProperty time;
    private final SimpleStringProperty source;
    private final SimpleStringProperty destination;
    private final SimpleStringProperty protocol;
    private final SimpleStringProperty length;
    private final SimpleStringProperty info;

    public PacketInfoTableData(String time, String source, String destination, String protocol, String length, String info) {
        this.time = new SimpleStringProperty(time);
        this.source = new SimpleStringProperty(source);
        this.destination = new SimpleStringProperty(destination);
        this.protocol =new SimpleStringProperty( protocol);
        this.length = new SimpleStringProperty(length);
        this.info = new SimpleStringProperty(info);
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public String getSource() {
        return source.get();
    }

    public SimpleStringProperty sourceProperty() {
        return source;
    }

    public String getDestination() {
        return destination.get();
    }

    public SimpleStringProperty destinationProperty() {
        return destination;
    }

    public String getProtocol() {
        return protocol.get();
    }

    public SimpleStringProperty protocolProperty() {
        return protocol;
    }

    public String getLength() {
        return length.get();
    }

    public SimpleStringProperty lengthProperty() {
        return length;
    }

    public String getInfo() {
        return info.get();
    }

    public SimpleStringProperty infoProperty() {
        return info;
    }
}
