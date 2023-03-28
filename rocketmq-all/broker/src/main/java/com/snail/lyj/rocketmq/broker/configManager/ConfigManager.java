package com.snail.lyj.rocketmq.broker.configManager;

import com.snail.lyj.rocketmq.logging.InternalLogger;
import com.snail.lyj.rocketmq.logging.InternalLoggerFactory;

import java.io.IOException;

/**
 * @author liuyajie
 * @date 2023/03/28/12:59 下午
 */
public abstract class ConfigManager {

    private static final InternalLogger log = InternalLoggerFactory.getLogger("RocketmqBroker");

    /**
     * 数据加载
     * 流程：
     * 1、从源文件加载数据 如果存在数据，则返回，如果不存在，则从备份原件获取数据
     *
     */
    public boolean load() {
        String fileName = getConfigFilePath();
        String jsonString = null;
        try {
            jsonString = MixAll.file2String(fileName);
            if (jsonString != null && jsonString.length() > 0) {
                this.decode(jsonString);
                return true;
            } else {
                return loadBak();
            }
        } catch (IOException e) {
            log.error("load " + fileName + " failed, and try to load backup file", e);
            return loadBak();
        }
    }

    private boolean loadBak() {
        String fileName = getConfigFilePath();
        String jsonString = null;
        try {
            jsonString = MixAll.file2String(fileName + ".bak");
            if (jsonString != null && jsonString.length() > 0) {
                this.decode(jsonString);
                return true;
            }
        } catch (IOException e) {
            log.error("load failed, fileName: " + fileName + ".bak", e);
        }
        return false;
    }


    /**
     * 持久化
     */
    public void persist() {
        // 1、获取数据
        String jsonString = encode(true);
        // 2、持久化数据
        if (jsonString != null && jsonString.length() > 0) {
            String filePath = getConfigFilePath();
            try {
                MixAll.string2File(jsonString, filePath);
            } catch (IOException e) {
                log.error("persist fileName: " + filePath +  " failed", e);
            }
        }

    }

    public abstract String getConfigFilePath();

    public abstract String encode(boolean prettyFormat);

    public abstract String encode();

    public abstract void decode(String str);

}
