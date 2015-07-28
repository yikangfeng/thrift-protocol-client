package com.ganji.as.thrift.protocol.client.test;


import org.apache.thrift.async.AsyncMethodCallback;

public class MethodCallback implements AsyncMethodCallback {
	Object response = null;

	public Object getResult() {
		// 返回结果值
		if (this.response == null) {
			synchronized (this) {
				while (this.response == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return this.response;
	}

	// 处理服务返回的结果值
	@Override
	public void onComplete(Object response) {
		this.response = response;
		synchronized(this)
		{
			notifyAll();
		}
		
	}

	@Override
	public void onError(Exception exception) {
		// TODO Auto-generated method stub

	}
}