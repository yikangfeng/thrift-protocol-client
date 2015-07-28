/**
 * 
 */
package com.ganji.as.thrift.protocol.service;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.async.TAsyncMethodCall;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingTransport;
import org.slf4j.Logger;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;
import com.ganji.as.thrift.protocol.client.future.Future;
import com.ganji.as.thrift.protocol.client.intf.ThriftProtocolClientRetryPolicy;
import com.ganji.as.thrift.protocol.client.request.ThriftClientRequest;
import com.ganji.as.thrift.protocol.client.socket.async.pool.SocketConnection;
import com.ganji.as.thrift.protocol.client.socket.async.pool.SocketConnectionPool;
import com.ganji.as.thrift.protocol.client.socket.async.pool.SocketConnectionPoolProvider;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolFunction;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolService;
import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.cluster.load.balance.LoadBalance;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 * @copyright yikangfeng
 */
public class ThriftProtocolServe<REQ, REP> implements
		ThriftProtocolService<REQ, REP> {
	static final private TransferQueue<RunnableFuture<?>> concurrentSessionQueue_ = new LinkedTransferQueue<>();
	static final private int threadPoolCoreSize_ = (int) Math.pow(Runtime
			.getRuntime().availableProcessors(), 2);// io busy.
	static final private ExecutorService executor = new ThreadPoolExecutor(
			threadPoolCoreSize_, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
			new LinkedTransferQueue<Runnable>());
	final private TAsyncClientManager asyncClientManager_ = new TAsyncClientManager();// thread
	// safe
	final private ClientBuildingConfig clientBuildingConfig_;
	final private LoadBalance loadBalance_;
	final private ThriftProtocolClientRetryPolicy retryPolicy_;
	final private SocketConnectionPool clientSocketConnectionPool_;
	final private TProtocolFactory protocolFactory_;
	final private Logger LOGGER;

	static {// first one init.
		int threadPoolCoreSize = threadPoolCoreSize_;
		do {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (!Thread.currentThread().isInterrupted()) {

						while (concurrentSessionQueue_.isEmpty()) {
							LockSupport.parkNanos(1000000L);
						}

						final RunnableFuture<?> runnableTask = concurrentSessionQueue_
								.poll();
						runnableTask.run();
					}
				}

			});
		} while (--threadPoolCoreSize > 0);// false exit.
	}

	public ThriftProtocolServe(final ClientBuildingConfig clientBuildingConfig)
			throws Throwable {
		this.clientBuildingConfig_ = clientBuildingConfig;
		LOGGER = clientBuildingConfig_.getLogger();
		clientSocketConnectionPool_ = SocketConnectionPoolProvider.FACTORY
				.createSocketConnectionPool(clientBuildingConfig);
		protocolFactory_ = clientBuildingConfig.getCodec();
		loadBalance_ = clientBuildingConfig.getLoadBalancePolicy();
		this.retryPolicy_ = clientBuildingConfig.getRetryPolicy();
	}

	@SuppressWarnings("rawtypes")
	static private TAsyncClientFactoryEx<TAsyncClientExtender> ASYNC_CLIENT_FACTORY = new TAsyncClientFactoryEx<TAsyncClientExtender>() {
		@Override
		public TAsyncClientExtender getAsyncClient(
				final TProtocolFactory protocolFactory,
				final TAsyncClientManager asyncClientManager,
				final TNonblockingTransport transport) {
			return new TAsyncClientExtender(protocolFactory,
					asyncClientManager, transport);
		}

	};

	static private class TAsyncMethodCallEx extends
			TAsyncMethodCall<TAsyncMethodCallEx> {
		final private ThriftClientRequest __request__;

		protected TAsyncMethodCallEx(final ThriftClientRequest _request_,
				TAsyncClient client, TProtocolFactory protocolFactory,
				TNonblockingTransport transport,
				AsyncMethodCallback<TAsyncMethodCallEx> callback,
				boolean isOneway) {
			super(client, protocolFactory, transport, callback, isOneway);
			this.__request__ = _request_;
		}

		@Override
		protected void write_args(TProtocol protocol) throws TException {
			// TODO Auto-generated method stub
			final ThriftClientRequest clientRequest = (ThriftClientRequest) __request__;
			if (clientRequest.message == null
					|| clientRequest.message.length <= 0)
				throw new TException("The request data is not legal.");

			for (final byte _byte : clientRequest.message) {
				protocol.writeByte(_byte);
			}
		}

		@Override
		protected ByteBuffer getFrameBuffer() {
			// TODO Auto-generated method stub
			return super.getFrameBuffer();
		}
	}

	static private class TAsyncClientExtender<REQ, REP> extends TAsyncClient
			implements TAsyncClientEx<REQ> {
		private TProtocolFactory protocolFactory_;
		private TNonblockingTransport transport_;

		public TAsyncClientExtender(TProtocolFactory protocolFactory,
				TAsyncClientManager manager, TNonblockingTransport transport) {
			// TODO Auto-generated constructor stub
			super(protocolFactory, manager, transport);
			this.protocolFactory_ = protocolFactory;
			this.transport_ = transport;
		}

		@Override
		protected void checkReady() {
			// TODO Auto-generated method stub
			super.checkReady();
		}

		@Override
		public Exception getError() {
			// TODO Auto-generated method stub
			return super.getError();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void sendRequest(final REQ __request__,
				final AsyncMethodCallback<?> asyncMethodCallback)
				throws Throwable {
			// TODO Auto-generated method stub
			this.checkReady();

			___manager
					.call(new TAsyncMethodCallEx(
							(ThriftClientRequest) __request__,
							this,
							this.protocolFactory_,
							this.transport_,
							(AsyncMethodCallback<TAsyncMethodCallEx>) asyncMethodCallback,
							false));
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Future<REP> apply(final REQ clientRequest) {
		// TODO Auto-generated method stub
		return new Future<REP>() {
			private ThriftProtocolFunction<REQ, ?> callbackFunction_;
			private REQ clientRequest_ = clientRequest;
			private java.util.concurrent.RunnableFuture<REQ> futureSession_;
			private long startRequestTime_;

			@Override
			public ThriftProtocolFunction<?, ?> getCallbackFunction() {
				// TODO Auto-generated method stub
				return this.callbackFunction_;
			}

			@Override
			public java.util.concurrent.RunnableFuture<REQ> getFutureSession() {
				return this.futureSession_;
			}

			@Override
			public <INPUT, OUTPUT> OUTPUT flatMap(
					final ThriftProtocolFunction<INPUT, OUTPUT> function) {
				// TODO Auto-generated method stub
				this.callbackFunction_ = (ThriftProtocolFunction<REQ, ?>) function;
				this.futureSession_ = createFutureSession(clientRequest_);
				this.startRequestTime_ = System.currentTimeMillis();
				try {
					concurrentSessionQueue_.put(futureSession_);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// logger
					if (!(LOGGER == null)) {
						if (LOGGER.isInfoEnabled())
							LOGGER.info(String
									.format("Failed to apply the client [%s] request, the reasons:%s",
											(clientBuildingConfig_
													.getClientName() == null ? ""
													: clientBuildingConfig_
															.getClientName()),
											e));
					}
					throw new IllegalStateException(e);
				}
				return (OUTPUT) this;
			}

			@Override
			public REP get() throws Throwable {
				// TODO Auto-generated method stub
				if (this.callbackFunction_ == null)
					throw new NullPointerException(
							"The callback function not registered.");

				REQ response = null;
				try {
					response = futureSession_.get();
				} catch (final Throwable t) {
					if (LOGGER != null) {
						if (LOGGER.isInfoEnabled())
							LOGGER.info(String
									.format("By async method call service response time is:%d (ms)",
											(System.currentTimeMillis() - this.startRequestTime_)));
					}
					// retry
					retryPolicy_
							.retry(this, clientBuildingConfig_.getRetries());

					try {
						response = this.futureSession_.get();
					} catch (final Throwable _t) {

					}
					if (LOGGER != null) {
						if (LOGGER.isInfoEnabled())
							LOGGER.info("Retry end.");
					}
				}

				final Object ret = this.callbackFunction_.apply(response);

				if (ret == null)
					throw new NullPointerException(
							"Remote server response result is null.");

				if (Future.class.isInstance(ret)) {// Nested future

					final Future<Throwable> futureThrowable = ((Future<Throwable>) ret);
					if (!(futureThrowable.get() == null)
							&& Throwable.class
									.isInstance(futureThrowable.get()))
						throw futureThrowable.get();

					return (REP) ((Future<?>) ret).get();
				}

				return (REP) ret;
			}

		};

	}

	@SuppressWarnings("unchecked")
	private FutureTask<REQ> createFutureSession(final REQ clientRequest) {
		return new FutureTask<REQ>(new Callable<REQ>() {
			@Override
			public REQ call() throws Exception {
				// TODO Auto-generated method stub
				SocketConnection socketConnectionProxy = null;
				try {
					// Because it is a multi host
					socketConnectionProxy = clientSocketConnectionPool_
							.getSocketConnection(loadBalance_,
									(ThriftClientInvocation) clientRequest);

					final TAsyncClientEx<REQ> client = ASYNC_CLIENT_FACTORY
							.getAsyncClient(protocolFactory_,
									asyncClientManager_,
									socketConnectionProxy.get());
					AsyncMethodCallbackEx<TAsyncMethodCallEx> asyncMethodCallback;
					client.sendRequest(
							clientRequest,
							asyncMethodCallback = new AsyncMethodCallbackEx<TAsyncMethodCallEx>() {
								private TAsyncMethodCallEx response_;
								private Throwable exception_;
								private final AtomicBoolean hasError_ = new AtomicBoolean(
										false);

								@Override
								public void onComplete(
										TAsyncMethodCallEx response) {
									// TODO Auto-generated method stub
									synchronized (this) {
										this.response_ = response;
										this.notify();
									}
								}

								@Override
								public void onError(Exception exception) {
									// TODO Auto-generated method stub
									hasError_.getAndSet(true);
									synchronized (this) {
										this.notify();
									}
									this.exception_ = exception;
									this.response_ = null;
								}

								@Override
								public Throwable getException() {
									// TODO Auto-generated method stub
									return this.exception_;
								}

								@Override
								public TAsyncMethodCallEx getResponse() {
									if (this.response_ == null) {
										synchronized (this) {
											while (this.response_ == null
													&& !hasError_.get()) {
												try {
													this.wait();
												} catch (InterruptedException ignored) {
													// TODO
													// Auto-generated
													// catch block
													ignored.printStackTrace();
												}
											}
										}
									}
									return this.response_;
								}
							});

					if (client.getError() != null)
						throw client.getError();

					return (REQ) asyncMethodCallback.getResponse()
							.getFrameBuffer().array();// byte array.
				} catch (final Throwable e) {
					if (!(LOGGER == null)) {
						if (LOGGER.isInfoEnabled())
							LOGGER.info(String
									.format("Gets the server response to the error, reasons:%s",
											e));
					}
					socketConnectionProxy.setAlive(false);
					socketConnectionProxy.close();
					clientSocketConnectionPool_
							.removeSocketConnection(socketConnectionProxy);
					throw (Exception) e;
				} finally {
					if (socketConnectionProxy != null)// Must be close
						socketConnectionProxy.close();
				}
			}

		});
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (this.clientSocketConnectionPool_ != null)
			this.clientSocketConnectionPool_.destory();
		if (executor != null)
			executor.shutdownNow();
		if (concurrentSessionQueue_ != null)
			concurrentSessionQueue_.clear();
		if (this.asyncClientManager_ != null)
			this.asyncClientManager_.stop();
	}
}
