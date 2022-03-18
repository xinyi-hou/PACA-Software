package software.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读写工具类（json格式）
 *
 * @author 侯心怡
 * @class 软信1902
 * @StudentID 20195782
 * @date 2022-03-06
 */
public class FileUtil {
    /**
     * 读取文件
     *
     * @param fileName：文件名称 C:类类型
     * @return 对象集合
     */
    public static List<Object> getData(String fileName, Class<?> C) {
        List<Object> ret = new ArrayList<>();
        String fd = "C:\\Users\\10408\\IDEA\\arpAttack\\src\\software\\data\\" + fileName;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fd));
            String line;
            while ((line = br.readLine()) != null) {
                line = "[" + line + "]";
                Object object = software.utils.JsonUtil.toObject(line, C);
                ret.add(object);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 将字符串写入文件
     *
     * @param data:     数据字符串
     * @param fileName: 件名称
     * @param mode:     模式   true:追加模式; false:覆盖模式
     */
    public static void writeData(String data, String fileName, boolean mode) throws IOException {
        //输出流 false == 覆盖
        String fd = "C:\\Users\\10408\\IDEA\\arpAttack\\src\\software\\data\\" + fileName;
        BufferedWriter bw = new BufferedWriter(new FileWriter(fd, mode));
        bw.write(data);
        bw.newLine();
        bw.flush();
        bw.close();
    }

    /*读文件*/
    public static List<String> readFile(String fileName) {
        List<String> ret = new ArrayList<>();
        String fd = "C:\\Users\\10408\\IDEA\\arpAttack\\src\\software\\data\\" + fileName;
        int num = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fd));
            String line;
            while ((line = br.readLine()) != null) {
                ret.add(line+"\n" );
                num++;
                if (num > 30) {
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String ListToStr(ArrayList<String> list) {
        StringBuilder sum = new StringBuilder();
        for (String s : list) {
            sum.append(s).append("\n");
        }
        return sum.toString();
    }

    public static void clearFile(String fileName) {
        File file = new File("C:\\Users\\10408\\IDEA\\arpAttack\\src\\software\\data\\" + fileName);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
