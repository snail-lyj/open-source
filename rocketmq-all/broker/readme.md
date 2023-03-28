broker管理的资源：

## 生产者组、消费者组管理
### 消费者组概述：
现状：一个topic会有多个队列。 如果一个topic对应一个消费者，此时这个消费者会分配所有的队列，消息速度慢
引入消费者组的概念，同一个消费者组下的所有消费者，共同消费topic下的队列，会提升消息消费的速度。
消费者组的设计目标： 提升消息消费的速度。
消费者组的使用：
    1、消费者组是rocketmq中重要的资源，因此创建需要有一定审批流程，不允许程序自动创建。因此不用考虑并发问题。
    2、消费者组管理的是一批消费者，需要维护一些消费者的公共信息
    3、一个消费者组会有一个重试主题，用于消息重试
    4、支持持久化。 
消费者组存储设计：
    SubscriptionGroupConfig
    name: 消费者组名称， 全局唯一
    retryQueueNums：重试主题数量
    retryMaxTimes：消息最大重试次数

Map<String, SubscriptionGroupConfig>
    

topic管理
消费者实例管理


## broker集群
集群关系
一个集群中有多个brokerName, 一个brokerName会有一个主节点，多个从节点
不同集群之间brokerName