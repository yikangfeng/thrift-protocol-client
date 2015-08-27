/**
 * 
 */
package com.ganji.as.thrift.protocol.client.future;

import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolFunction;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 */
public abstract class Future<V> {
	abstract public <INPUT, OUTPUT> OUTPUT flatMap(
			final ThriftProtocolFunction<INPUT, OUTPUT> function);

	abstract public java.util.concurrent.RunnableFuture<?> getFutureSession();

	abstract public ThriftProtocolFunction<?, ?> getCallbackFunction();

	abstract public V get() throws Throwable;

	abstract public V get(final long timeout, final TimeUnit unit)
			throws Throwable;

	static public <V> Future<V> value(final V v) {
		return new Future<V>() {

			@Override
			public V get() throws Throwable {
				// TODO Auto-generated method stub
				return v;
			}

			@Override
			public <INPUT, OUTPUT> OUTPUT flatMap(
					ThriftProtocolFunction<INPUT, OUTPUT> function) {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			@Override
			public RunnableFuture<?> getFutureSession() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			@Override
			public ThriftProtocolFunction<?, ?> getCallbackFunction() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			@Override
			public V get(long timeout, TimeUnit unit) throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

	static public <V> Future<V> exception(final Throwable t) {
		return new Future<V>() {

			@SuppressWarnings("unchecked")
			@Override
			public V get() throws Throwable {
				// TODO Auto-generated method stub
				return (V) t;
			}

			@Override
			public <INPUT, OUTPUT> OUTPUT flatMap(
					ThriftProtocolFunction<INPUT, OUTPUT> function) {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			@Override
			public RunnableFuture<?> getFutureSession() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			@Override
			public ThriftProtocolFunction<?, ?> getCallbackFunction() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

			@Override
			public V get(long timeout, TimeUnit unit) throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}
}
