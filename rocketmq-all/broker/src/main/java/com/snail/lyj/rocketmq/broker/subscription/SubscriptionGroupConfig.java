package com.snail.lyj.rocketmq.broker.subscription;

import lombok.Data;

/**
 * @author liuyajie
 * @date 2023/03/28/12:45 下午
 */
@Data
public class SubscriptionGroupConfig {

    private String groupName;

    private int retryQueueNums = 1;

    private int retryMaxTimes = 16;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getRetryQueueNums() {
        return retryQueueNums;
    }

    public void setRetryQueueNums(int retryQueueNums) {
        this.retryQueueNums = retryQueueNums;
    }

    public int getRetryMaxTimes() {
        return retryMaxTimes;
    }

    public void setRetryMaxTimes(int retryMaxTimes) {
        this.retryMaxTimes = retryMaxTimes;
    }
}
