/**
 * 
 */
package com.ganji.as.thrift.protocol.server.nodes.discovery;

/**
 * @author yikangfeng
 * @date 2015年7月22日
 */
public class ServiceEndpoint {
	private String host;
	private int port;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder().append(this.getHost()).append(":").append(this.getPort()).toString();
	}

}
