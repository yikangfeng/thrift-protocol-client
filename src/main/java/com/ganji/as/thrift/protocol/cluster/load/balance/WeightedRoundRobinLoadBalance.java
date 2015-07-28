/**
 * 
 */
package com.ganji.as.thrift.protocol.cluster.load.balance;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNode;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNodeInfo;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServiceEndpoint;

/**
 * @author yikangfeng
 * @date 2015年7月22日
 */
public class WeightedRoundRobinLoadBalance extends AbstractLoadBalance {
	private int currentIndex = -1;// 上一次选择的服务器
	private int currentWeight = 0;// 当前调度的权值
	private int maxWeight = 0; // 最大权重
	private int gcdWeight = 0; // 所有服务器权重的最大公约数
	private int serverCount = 0; // 服务器数量
	private List<ServerNode> serverNodes_; // 服务器集合

	public WeightedRoundRobinLoadBalance() {
		currentIndex = -1;
		currentWeight = 0;
	}

	/**
	 * 返回最大公约数
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static int gcd(int a, int b) {
		BigInteger b1 = new BigInteger(String.valueOf(a));
		BigInteger b2 = new BigInteger(String.valueOf(b));
		BigInteger gcd = b1.gcd(b2);
		return gcd.intValue();
	}

	/**
	 * 返回所有服务器权重的最大公约数 37 * @param serverList 38 * @return 39
	 */
	private static int getGCDForServers(List<ServerNode> serverList) {
		int w = 0;
		for (int i = 0, len = serverList.size(); i < len - 1; i++) {
			if (w == 0) {
				w = gcd(serverList.get(i).getWeight(), serverList.get(i + 1)
						.getWeight());
			} else {
				w = gcd(w, serverList.get(i + 1).getWeight());
			}
		}
		return w;
	}

	/**
	 * 返回所有服务器中的最大权重
	 * 
	 * @param serverList
	 * @return
	 */
	public static int getMaxWeightForServers(List<ServerNode> serverList) {
		int w = 0;
		for (int i = 0, len = serverList.size(); i < len - 1; i++) {
			if (w == 0) {
				w = Math.max(serverList.get(i).getWeight(),
						serverList.get(i + 1).getWeight());
			} else {
				w = Math.max(w, serverList.get(i + 1).getWeight());
			}
		}
		return w;
	}

	/**
	 * 算法流程： 假设有一组服务器 S = {S0, S1, …, Sn-1} 有相应的权重，变量currentIndex表示上次选择的服务器
	 * 权值currentWeight初始化为0，currentIndex初始化为-1 ，当第一次的时候返回 权值取最大的那个服务器， 通过权重的不断递减
	 * 寻找 适合的服务器返回，直到轮询结束，权值返回为0
	 */
	public ServerNode getAvailableServerNode() {
		while (true) {
			currentIndex = (currentIndex + 1) % serverCount;
			if (currentIndex == 0) {
				currentWeight = currentWeight - gcdWeight;
				if (currentWeight <= 0) {
					currentWeight = maxWeight;
					if (currentWeight == 0)
						return null;
				}
			}
			if (serverNodes_.get(currentIndex).getWeight() >= currentWeight) {
				return serverNodes_.get(currentIndex);
			}
		}
	}

	@Override
	ServerNode doSelect(final List<ServerNode> serverNodes,final ThriftClientInvocation clientInvocation) {
		// TODO Auto-generated method stub
		synchronized (this) {
			this.serverNodes_ = serverNodes;
			this.serverCount = this.serverNodes_.size();
			this.maxWeight = getMaxWeightForServers(this.serverNodes_);
			this.gcdWeight = getGCDForServers(this.serverNodes_);
			return this.getAvailableServerNode();
		}

	}

	static public void main(String[] args) {
		WeightedRoundRobinLoadBalance loadBalance = new WeightedRoundRobinLoadBalance();

		List<ServerNode> serverNodes = new ArrayList<>();

		ServerNodeInfo serverNodeInfo1 = new ServerNodeInfo();
		ServiceEndpoint serviceEndpoint1 = new ServiceEndpoint();
		serviceEndpoint1.setHost("host1");
		serviceEndpoint1.setPort(8081);
		serverNodeInfo1.setServiceEndpoint(serviceEndpoint1);

		ServerNodeInfo serverNodeInfo2 = new ServerNodeInfo();
		ServiceEndpoint serviceEndpoint2 = new ServiceEndpoint();
		serviceEndpoint2.setHost("host2");
		serviceEndpoint2.setPort(8082);
		serverNodeInfo2.setServiceEndpoint(serviceEndpoint2);

		ServerNodeInfo serverNodeInfo3 = new ServerNodeInfo();
		ServiceEndpoint serviceEndpoint3 = new ServiceEndpoint();
		serviceEndpoint3.setHost("host3");
		serviceEndpoint3.setPort(8083);
		serverNodeInfo3.setServiceEndpoint(serviceEndpoint3);

		serverNodes.add(serverNodeInfo1);
		serverNodes.add(serverNodeInfo2);
		serverNodes.add(serverNodeInfo3);
		for (int i = 0; i < 1000; ++i) {
			try {
				System.out.println(loadBalance.select(serverNodes,null));
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
