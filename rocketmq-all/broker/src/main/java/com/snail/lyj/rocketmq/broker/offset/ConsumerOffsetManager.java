package com.snail.lyj.rocketmq.broker.offset;

import java.util.HashMap;
import java.util.Map;

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
 *
 *
 *
 *
 *
 */
public class ConsumerOffsetManager {


    private Map<String, Map<Integer, Long>> offsetTable = new HashMap<>(1024);


    public ConsumerOffsetManager(Map<String, Map<Integer, Long>> offsetTable) {
        this.offsetTable = offsetTable;
    }
}
