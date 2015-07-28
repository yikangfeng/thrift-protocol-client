/**
 * 
 */
package com.ganji.as.thrift.protocol.client.policy.session.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ganji.as.thrift.protocol.client.future.Future;
import com.ganji.as.thrift.protocol.client.intf.ThriftProtocolClientRetryPolicy;

/**
 * @author yikangfeng
 * @date 2015年7月22日
 * @ThreadSafe
 */
public class ThriftProtocolSessionRetryPolicy implements
		ThriftProtocolClientRetryPolicy {
	final private Logger LOGGER_ = LoggerFactory.getLogger(this.getClass());

	public ThriftProtocolSessionRetryPolicy() {

	}

	@Override
	public void retry(final Future<?> future, final int retries) {
		// TODO Auto-generated method stub
		int _retries = 0;
		if (LOGGER_ != null) {
			if (LOGGER_.isInfoEnabled())
				LOGGER_.info(String
						.format("Session failed,begin to try again,config retries=(%d)",
								retries));
		}
		while (_retries < retries) {
			if (LOGGER_ != null) {
				if (LOGGER_.isInfoEnabled())
					LOGGER_.info(String.format("Session failed,try again (%d)",
							++_retries));
			}
			try {
				((Future<?>) future.flatMap(future.getCallbackFunction()))
						.getFutureSession().get();
			} catch (final Throwable _t) {
				continue;// try again.
			}

			if (LOGGER_ != null) {
				if (LOGGER_.isInfoEnabled())
					LOGGER_.info("try successfully.");
			}
			break;// try successfully.
		}
	}

}
