package com.snail.lyj.rocketmq.broker.configManager;

import java.io.*;

/**
 * @author liuyajie
 * @date 2023/03/28/2:40 下午
 */
public class MixAll {

    /**
     * 将数据优雅写入文件。 引入临时文件、备份文件、目标文件
     * 流程：
     * 1、将数据写入临时文件
     * 2、备份源文件
     * 3、删除源文件
     * 4、将临时文件命名为原文件
     *
     */
    public static void string2File(String jsonString, String fileName) throws IOException {
        // 1、将数据写入临时文件
        String tmpFile = fileName + ".tmp";
        string2FileNotSafe(jsonString, tmpFile);
        // 2、备份源文件
        String preContent = file2String(fileName);
        if (preContent != null) {
            String bakFile = fileName + ".bak";
            string2FileNotSafe(jsonString, bakFile);
        }
        // 3、删除源文件
        File file = new File(fileName);
        file.delete();
        // 4、将临时文件命名为原文件
        file = new File(tmpFile);
        file.renameTo(new File(fileName));
    }

    /**
     * 向文件中写入字符串
     * 输出流采用： FileWriter。 该输出流可以直接写字符串
     * @param jsonString 数据
     * @param fileName 文件
     */
    private static void string2FileNotSafe(String jsonString, String fileName) throws IOException {
        // 文件可能不存在
        File file = new File(fileName);
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        // 1、获取输出流
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(jsonString);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 获取文件中的数据
     * 采用FileInputStream
     * @param fileName 文件
     * @return
     */
    public static String file2String(String fileName) throws IOException {
        return file2String(new File(fileName));
    }

    /**
     * 从文件中读取数据
     * @param file
     * @return
     * @throws IOException
     */
    private static String file2String(File file) throws IOException {
        if (file.exists()) {
            boolean success = false;
            // 获取输入流
            FileInputStream inputStream = null;
            byte[] data = new byte[(int) file.length()];
            try {
                inputStream = new FileInputStream(file);
                // 读不完的情况
                int readLen = inputStream.read(data);
                // readLen == data.length表示读完了
                success = readLen == data.length;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            if (success) {
                return new String(data);
            }
        }
        return null;
    }
}
