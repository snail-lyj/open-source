package org.snail.lyj.rocketmq.remoting.protocol;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author liuyajie
 * @date 2023/03/28/7:23 下午
 * json序列化
 */
public class RemotingSerializable {

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public static byte[] encode(final Object obj) {
        String json = toJson(obj, false);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public static String toJson(Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }

    public static <T> T decode(final String str, Class<T> clazz) {
        return fromJson(str, clazz);
    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        return JSON.parseObject(str, clazz);
    }
}
