package com.snail.lyj.rocketmq.broker.configManager;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liuyajie
 * @date 2023/03/28/1:01 下午
 */
public class DataVersion {
    private long timestamp = System.currentTimeMillis();

    private AtomicLong counter = new AtomicLong(0);
    
    
    public void nextVersion() {
        this.timestamp = System.currentTimeMillis();
        // AtomicLong 保证原子性
        counter.incrementAndGet();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public AtomicLong getCounter() {
        return counter;
    }

    public void setCounter(AtomicLong counter) {
        this.counter = counter;
    }
}
