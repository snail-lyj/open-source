package com.snail.lyj.rocketmq.broker;

import com.alibaba.fastjson.JSON;
import com.snail.lyj.rocketmq.broker.subscription.SubscriptionGroupConfig;
import com.snail.lyj.rocketmq.broker.subscription.SubscriptionGroupManager;

/**
 * @author liuyajie
 * @date 2023/03/29/7:54 下午
 */
public class SubscriptionGroupManagerTest {


    public static void main(String[] args) {
        SubscriptionGroupManager manager = new SubscriptionGroupManager();
        /*SubscriptionGroupConfig config = new SubscriptionGroupConfig();
        config.setGroupName("lyjtest");
        manager.updateSubscriptionGroupConfig(config);

        SubscriptionGroupConfig config2 = new SubscriptionGroupConfig();
        config2.setGroupName("lyjtest2");
        manager.updateSubscriptionGroupConfig(config2);*/
        manager.load();
        SubscriptionGroupConfig lyjtest = manager.findSubscriptionGroupConfig("lyjtest");
        System.out.println(JSON.toJSON(lyjtest));

        manager.deleteSubscriptionGroupConfig("lyjtest");

    }
}
