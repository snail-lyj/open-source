package org.snail.lyj.rocketmq.remoting.netty;

import org.snail.lyj.rocketmq.remoting.RemotingClient;
import org.snail.lyj.rocketmq.remoting.protocol.RemotingCommand;

/**
 * @author liuyajie
 * @date 2023/03/28/11:12 上午
 */
public class NettyRemotingClient implements RemotingClient {

    @Override
    public void start() {

    }

    @Override
    public void shutDown() {

    }

    @Override
    public RemotingCommand invokeSync(String addr, RemotingCommand request, long timeoutMillis) {
        return null;
    }


}
