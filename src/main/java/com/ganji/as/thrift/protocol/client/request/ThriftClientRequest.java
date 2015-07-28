/**
 * 
 */
package com.ganji.as.thrift.protocol.client.request;

/**
 * @author yikangfeng
 * @date 2015年7月23日
 */
public class ThriftClientRequest implements ThriftClientInvocation {
	public byte[] message;
	public boolean oneway;

	public ThriftClientRequest(final byte[] message, final boolean oneway) {
		this.message = message;
		this.oneway = oneway;
	}

	@Override
	public byte[] getMessage() {
		// TODO Auto-generated method stub
		return this.message;
	}

	@Override
	public boolean getOneWay() {
		// TODO Auto-generated method stub
		return this.oneway;
	}
}
