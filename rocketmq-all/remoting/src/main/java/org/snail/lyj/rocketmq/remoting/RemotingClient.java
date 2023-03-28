package org.snail.lyj.rocketmq.remoting;

import org.snail.lyj.rocketmq.remoting.protocol.RemotingCommand;

/**
 * @author liuyajie
 */
public interface RemotingClient extends RemotingService {

    /**
     * 同步发送
     * @param addr 发送地址
     * @param request 请求
     * @param timeoutMillis 超时时间
     * @return
     */
    RemotingCommand invokeSync(String addr, RemotingCommand request, long timeoutMillis);

}
