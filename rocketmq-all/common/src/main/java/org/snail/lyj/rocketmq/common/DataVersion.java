package org.snail.lyj.rocketmq.common;

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

    /**
     * 将dataVersion中的数据拷贝至当前对象
     *
     * @param dataVersion
     */
    public void assignNewOne(DataVersion dataVersion) {
        this.timestamp = dataVersion.getTimestamp();
        this.counter.set(dataVersion.getCounter().get());
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
