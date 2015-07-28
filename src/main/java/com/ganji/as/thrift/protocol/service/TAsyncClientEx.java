/**
 * 
 */
package com.ganji.as.thrift.protocol.service;

import org.apache.thrift.async.AsyncMethodCallback;


/**
 * @author yikangfeng
 * @date 2015年7月23日
 */
public interface TAsyncClientEx<REQ>{
	void sendRequest(final REQ __request__,final AsyncMethodCallback<?> asyncMethodCallback) throws Throwable;
	Exception getError();
}
