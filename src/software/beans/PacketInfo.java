package software.beans;

public class PacketInfo {
    private final String time;
    private final String source;
    private final String destination;
    private final String protocol;
    private final String length;
    private final String info;

    public PacketInfo(String time, String source, String destination, String protocol, String length, String info) {
        this.time = time;
        this.source = source;
        this.destination = destination;
        this.protocol = protocol;
        this.length = length;
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getLength() {
        return length;
    }

    public String getInfo() {
        return info;
    }
}
