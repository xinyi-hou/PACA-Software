package software.beans;

public class HostInfo {
    private final String hostIP;
    private final String hostMAC;

    public HostInfo(String hostIP, String hostMAC) {
        this.hostIP = hostIP;
        this.hostMAC = hostMAC;
    }

    public String getHostIP() {
        return hostIP;
    }

    public String getHostMAC() {
        return hostMAC;
    }
}
