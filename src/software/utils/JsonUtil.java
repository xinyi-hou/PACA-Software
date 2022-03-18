package software.utils;

import com.google.gson.*;

/**
 * 将json字符串和对象转换的工具类
 *
 * @author 侯心怡
 * @class 软信1902
 * @StudentID 20195782
 * @date 2022-03-06
 */
public class JsonUtil {

    /**
     * 将object对象转换成json格式字符串
     */
    public static String toJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }

    /**
     * 将json格式字符串对象转换成object（针对文件中一行是一个对象的情况）
     */
    public static Object toObject(String fd, Class<?> C) {
        JsonParser parser = new JsonParser();
        //将JSON的String转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(fd).getAsJsonArray();
        Gson gson = new Gson();
        Object object = null;
        for (JsonElement item : jsonArray) {
            object = gson.fromJson(item, C);
        }
        return object;
    }

}
