package com.snail.lyj.rocketmq.broker.subscription;

import com.snail.lyj.rocketmq.broker.BrokerPathConfigHelper;
import com.snail.lyj.rocketmq.logging.InternalLogger;
import com.snail.lyj.rocketmq.logging.InternalLoggerFactory;
import org.snail.lyj.rocketmq.common.ConfigManager;
import org.snail.lyj.rocketmq.common.DataVersion;
import org.snail.lyj.rocketmq.common.MixAll;
import org.snail.lyj.rocketmq.remoting.protocol.RemotingSerializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author liuyajie
 * @date 2023/03/28/12:47 下午
 * 消费者组管理：
 * 1、提供持久化的能力
 * 2、提供版本号机制
 * 3、提供手动创建和自动创建俩种机制。 线上不建议开启自动化创建方式。
 *
 * 消费者组的使用规约：
 *  消费者组是重要资源
 *
 *
 * 使用场景：
 *  1、更新、删除 用在了消费者组的管理
 *  消费者组主要用于消息消费，消息消费涉及： 心跳、消息拉取、重试消息、死信队列
 *  2、查询：
 *      客户端心跳： 用于创建重试主题和客户端发生变更时，通知所有客户端进行重平衡
 *      消息拉取， 消费者组必须存在
 *      重试消息， 消费者组必须存在
 *      死信队列， 消费者组必须存在
 *
 *
 *
 */
public class SubscriptionGroupManager extends ConfigManager {

    private static final InternalLogger log = InternalLoggerFactory.getLogger("RocketmqBroker");

    private final Map<String, SubscriptionGroupConfig> subscriptionGroupTable = new HashMap<>(1024);

    private final DataVersion dataVersion = new DataVersion();

    public SubscriptionGroupManager() {
        // 系统消费者组初始化
        init();
    }

    public void init() {
        {
            SubscriptionGroupConfig subscriptionGroupConfig = new SubscriptionGroupConfig();
            subscriptionGroupConfig.setGroupName(MixAll.TOOLS_CONSUMER_GROUP);
            this.subscriptionGroupTable.put(MixAll.TOOLS_CONSUMER_GROUP, subscriptionGroupConfig);
        }

        {
            SubscriptionGroupConfig subscriptionGroupConfig = new SubscriptionGroupConfig();
            subscriptionGroupConfig.setGroupName(MixAll.FILTERSRV_CONSUMER_GROUP);
            this.subscriptionGroupTable.put(MixAll.FILTERSRV_CONSUMER_GROUP, subscriptionGroupConfig);
        }

        {
            SubscriptionGroupConfig subscriptionGroupConfig = new SubscriptionGroupConfig();
            subscriptionGroupConfig.setGroupName(MixAll.SELF_TEST_CONSUMER_GROUP);
            this.subscriptionGroupTable.put(MixAll.SELF_TEST_CONSUMER_GROUP, subscriptionGroupConfig);
        }

        {
            SubscriptionGroupConfig subscriptionGroupConfig = new SubscriptionGroupConfig();
            subscriptionGroupConfig.setGroupName(MixAll.ONS_HTTP_PROXY_GROUP);
            this.subscriptionGroupTable.put(MixAll.ONS_HTTP_PROXY_GROUP, subscriptionGroupConfig);
        }

        {
            SubscriptionGroupConfig subscriptionGroupConfig = new SubscriptionGroupConfig();
            subscriptionGroupConfig.setGroupName(MixAll.CID_ONSAPI_PULL_GROUP);
            this.subscriptionGroupTable.put(MixAll.CID_ONSAPI_PULL_GROUP, subscriptionGroupConfig);
        }

        {
            SubscriptionGroupConfig subscriptionGroupConfig = new SubscriptionGroupConfig();
            subscriptionGroupConfig.setGroupName(MixAll.CID_ONSAPI_PERMISSION_GROUP);
            this.subscriptionGroupTable.put(MixAll.CID_ONSAPI_PERMISSION_GROUP, subscriptionGroupConfig);
        }

        {
            SubscriptionGroupConfig subscriptionGroupConfig = new SubscriptionGroupConfig();
            subscriptionGroupConfig.setGroupName(MixAll.CID_ONSAPI_OWNER_GROUP);
            this.subscriptionGroupTable.put(MixAll.CID_ONSAPI_OWNER_GROUP, subscriptionGroupConfig);
        }
    }

    public SubscriptionGroupConfig findSubscriptionGroupConfig(final String groupName) {
        SubscriptionGroupConfig subscriptionGroupConfig = subscriptionGroupTable.get(groupName);
        if (subscriptionGroupConfig == null) {
            //todo 提供自动创建消费者组的能力

        }
        return subscriptionGroupConfig;
    }

    public void updateSubscriptionGroupConfig(final SubscriptionGroupConfig config) {
        SubscriptionGroupConfig pre = subscriptionGroupTable.put(config.getGroupName(), config);
        if (pre == null) {
            // 新增
            log.info("create subscrition group config, {}", config);
        } else {
            //修改
            log.info("update subscription group config, old:{}, new:{}", pre, config);
        }
        // 升级版本号
        this.dataVersion.nextVersion();
        // 持久化
        this.persist();
    }


    public void deleteSubscriptionGroupConfig(final String groupName) {
        SubscriptionGroupConfig config = subscriptionGroupTable.remove(groupName);
        if (config != null) {
            // 移除成功
            log.info("delete subscription group config success, subscription group: {}", config);
            this.dataVersion.nextVersion();
            this.persist();
        } else {
            // 组不存在
            log.warn("delete subscription group config failed, subscription groupName: {} not exist", groupName);
        }
    }

    @Override
    public String configFilePath() {
        //todo 可配置
        String rootDir = ".";
        return BrokerPathConfigHelper.getSubscriptionGroupPath(rootDir);
    }

    @Override
    public String encode(boolean prettyFormat) {
        // 序列化是根据getter方法序列化的，只要有方法在，就会生成一个去掉get的属性
        return RemotingSerializable.toJson(this, prettyFormat);
    }

    @Override
    public String encode() {
        return this.encode(true);
    }

    @Override
    public void decode(String str) {
        if (str != null) {
            SubscriptionGroupManager groupManager = RemotingSerializable.fromJson(str, SubscriptionGroupManager.class);
            if (groupManager != null) {
                this.subscriptionGroupTable.putAll(groupManager.getSubscriptionGroupTable());
                this.dataVersion.assignNewOne(groupManager.dataVersion);

                this.printLoadDataWhenFirstBoot(groupManager);
            }
        }
    }


    public Map<String, SubscriptionGroupConfig> getSubscriptionGroupTable() {
        return subscriptionGroupTable;
    }

    public DataVersion getDataVersion() {
        return dataVersion;
    }

    /**
     * 打印从文件中加载的数据
     * @param sgm
     */
    private void printLoadDataWhenFirstBoot(SubscriptionGroupManager sgm) {
        Iterator<Map.Entry<String, SubscriptionGroupConfig>> it = sgm.getSubscriptionGroupTable().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, SubscriptionGroupConfig> next = it.next();
            log.info("load exist subscription group, {}", next.getValue().toString());
        }
    }
}
