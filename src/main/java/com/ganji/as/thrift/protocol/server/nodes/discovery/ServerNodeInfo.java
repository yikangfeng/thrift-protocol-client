/**
 * 
 */
package com.ganji.as.thrift.protocol.server.nodes.discovery;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public class ServerNodeInfo implements ServerNode {
	private String status;
	private int shard;
	private int weight = 3;
	private ServiceEndpoint serviceEndpoint;

	@Override
	public String getHost() {
		return serviceEndpoint.getHost();
	}

	@Override
	public int getPort() {
		return serviceEndpoint.getPort();
	}

	public ServiceEndpoint getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(ServiceEndpoint serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getShard() {
		return shard;
	}

	public void setShard(int shard) {
		this.shard = shard;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder().append("shard").append("=")
				.append(this.getShard()).append(" ").append("status")
				.append("=").append(this.getStatus()).append(" ")
				.append("weight").append("=").append(this.getWeight())
				.append(" ").append("endpoint").append("=")
				.append(this.getServiceEndpoint().toString()).toString();
	}

}
