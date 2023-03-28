package com.snail.lyj.rocketmq.logging;

/**
 * 
 * @author liuyajie
 * @date 2023/03/28/2:13 下午
 */
public class InternalLoggerFactory {

    public static final String LOGGER_INNER = "inner";

    public static final String LOGGER_SLF4J = "slf4j";
    
    public static final String DEFALULT_LOGGER = LOGGER_SLF4J;

    private static String loggerType = LOGGER_SLF4J;

    public static InternalLogger getLogger(String name) {
        return new Slf4jLogger(name);
    }

    public static InternalLogger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }


}
