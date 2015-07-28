/**
 * 
 */
package com.ganji.as.thrift.protocol.client.request;

/**
 * @author yikangfeng
 * @date   2015年7月24日 
 */
public interface ThriftClientInvocation {
     byte[] getMessage();
     boolean getOneWay();
}
