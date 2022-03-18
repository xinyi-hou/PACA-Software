package software.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NetUtil {


    /**
     * 将字符串形式的MAC地址转换成存放在byte数组内的MAC地址
     *
     * @param str 字符串形式的MAC地址，如：AA-AA-AA-AA-AA
     * @return 保存在byte数组内的MAC地址
     */

    public static byte[] strToMac(String str) {
        byte[] mac = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        String[] s1 = str.split("-");
        for (int x = 0; x < s1.length; x++) {
            mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);
        }
        return mac;
    }

    /**
     * mac字节转字符串的方法
     */
    public static String macToStr(byte[] bytes) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            str.append(Integer.toString(bytes[i] & 0xff, 16));
            if (i < bytes.length - 1) {
                str.append("-");
            }
        }
        return str.toString();
    }

    /**
     * 判断IP地址的格式是否正确
     */
    public boolean judgeIP(String ip) {
        String regExp = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /**
     * 判断MAC地址的格式是否正确
     */
    public boolean judgeMAC(String mac) {
        String regExp = "([0-9A-Fa-f]{2})([-:][0-9A-Fa-f]{2}){5}";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(mac);
        return matcher.matches();
    }
}
