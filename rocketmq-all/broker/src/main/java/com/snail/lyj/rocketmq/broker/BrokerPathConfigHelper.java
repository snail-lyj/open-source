package com.snail.lyj.rocketmq.broker;

import java.io.File;

/**
 * @author lyj
 * @date 3/28/23
 * @time 11:41 PM
 */
public class BrokerPathConfigHelper {

    public static String getSubscriptionGroupPath(String rootDir) {
        return rootDir + File.separator + "config" + File.separator +  "subscriptionGroup.json";
    }
}
