package com.ganji.as.thrift.protocol.cluster.load.balance;

import java.util.List;
import java.util.Random;

import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNode;

/**
 * 
 * @author yikangfeng
 * @date 2015年7月21日
 */
public class RandomLoadBalance extends AbstractLoadBalance {
	final private Random random_ = new Random();

	@Override
	public ServerNode doSelect(final List<ServerNode> serverNodes,final ThriftClientInvocation clientInvocation) {
		// TODO Auto-generated method stub
		return serverNodes.get(random_.nextInt(serverNodes.size()));
	}
}