package com.snail.lyj.rocketmq.broker.offset;

import com.snail.lyj.rocketmq.broker.BrokerPathConfigHelper;
import com.snail.lyj.rocketmq.logging.InternalLogger;
import com.snail.lyj.rocketmq.logging.InternalLoggerFactory;
import org.snail.lyj.rocketmq.common.ConfigManager;
import org.snail.lyj.rocketmq.common.constant.LoggerName;
import org.snail.lyj.rocketmq.remoting.protocol.RemotingSerializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author liuyajie
 * @date 2023/03/29/8:14 下午
 *
 * 消费位点管理
 * 概述：
 *  一个消费者组下， 一个topic的一个队列有一个位点
 *  不同的消费者组可以消费同一个topic
 *
 *  存储涉及
 *  key： 消费者组 + topic
 *  value： Map<Integer, Integer> key为队列索引 value为消费位点
 *
 *
 *  支持持久化
 *
 *  提交位点： 有俩个地方： 1、消费位点实时提交接口 2、消费方拉取消息时也会带消费的位点  3、事务消息
 *
 *
 *
 *
 *
 *
 */
public class ConsumerOffsetManager extends ConfigManager {

    private static final InternalLogger log = InternalLoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);

    private static final String TOPIC_GROUP_SEPARATOR = "@";

    // offsetTable是有状态变量，用HashMap会存在线程安全问题，所以采用
    // private Map<String, Map<Integer, Long>> offsetTable = new HashMap<>(1024);

    private final ConcurrentMap<String, Map<Integer, Long>> offsetTable = new ConcurrentHashMap<>(1024);

    /**
     * 查询位点
     * @param groupName 消费者组
     * @param topic 主题
     * @param queueId 队列id
     * @return
     */
    public long queryOffset(final String groupName, final String topic, final Integer queueId) {
        String key = groupName + TOPIC_GROUP_SEPARATOR + topic;
        Map<Integer, Long> queueMap = offsetTable.get(key);
        if (queueMap != null) {
            Long offset = queueMap.get(queueId);
            if (offset != null) {
                return offset;
            }
        }
        return -1;
    }
    /**
     * 提交位点
     */
    public void commitOffset(final String groupName, final String topic, final int queueId, final long offset) {
        String key = groupName + TOPIC_GROUP_SEPARATOR + topic;
        Map<Integer, Long> queueMap = offsetTable.get(key);
        if (queueMap != null) {
            Long preOffset = queueMap.put(queueId, offset);
            if (preOffset == null) {
                // 新增消费位点
                log.info("create offset success, groupName: {}, topic: {}, queueId: {}, offset: {}", groupName, topic, queueId, offset);
            } else {
                // 修改消费位点
                log.info("update offset success, groupName: {}, topic: {}, queueId: {}, oldOffset: {} newOffset: {}", groupName, topic, queueId, preOffset, offset);
            }
        } else {
            // 该topic的消费位点不存在
            queueMap = offsetTable.putIfAbsent(key, new ConcurrentHashMap<>());
            Long preOffset = queueMap.put(queueId, offset);
            if (preOffset == null) {
                // 新增消费位点
                log.info("create offset success, groupName: {}, topic: {}, queueId: {}, offset: {}", groupName, topic, queueId, offset);
            } else {
                // 修改消费位点
                log.info("update offset success, groupName: {}, topic: {}, queueId: {}, oldOffset: {} newOffset: {}", groupName, topic, queueId, preOffset, offset);
            }
        }
    }
    @Override
    public String configFilePath() {
        String rootDir = ".";
        return BrokerPathConfigHelper.getConsumerOffsetPath(rootDir);
    }

    @Override
    public String encode(boolean prettyFormat) {
        return RemotingSerializable.toJson(this, prettyFormat);
    }

    @Override
    public String encode() {
        return encode(true);
    }

    @Override
    public void decode(String str) {
        if (str != null) {
            ConsumerOffsetManager consumerOffsetManager = RemotingSerializable.fromJson(str, ConsumerOffsetManager.class);
            if (consumerOffsetManager != null) {
                this.offsetTable.putAll(consumerOffsetManager.getOffsetTable());
                this.printLoadDataWhenFirstBoot();
            }
        }
    }


    private void printLoadDataWhenFirstBoot() {
        Iterator<Map.Entry<String, Map<Integer, Long>>> it = this.offsetTable.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Map<Integer, Long>> next = it.next();
            log.info("load key: {} value: {}", next.getKey(), next.getValue().toString());
        }
    }

    public ConcurrentMap<String, Map<Integer, Long>> getOffsetTable() {
        return offsetTable;
    }

}
