/**
 * 
 */
package com.ganji.as.thrift.protocol.service;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolService;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 */
public class ThriftProtocolServiceProxy<REQ, REP> implements MethodInterceptor {

	final public ThriftProtocolService<REQ, REP> proxy_;
	static final private Enhancer enhancer_ = new Enhancer();

	@SuppressWarnings("unchecked")
	public ThriftProtocolServiceProxy(
			final ClientBuildingConfig clientBuildingConfig) throws Throwable {

		enhancer_.setInterfaces(new Class<?>[] { ThriftProtocolService.class });
		enhancer_.setCallback(this);

		this.proxy_ = (ThriftProtocolService<REQ, REP>) enhancer_.create();

		this.target_ = new ThriftProtocolServe<REQ, REP>(clientBuildingConfig);
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		// TODO Auto-generated method stub
		return proxy.invoke(target_, args);
	}

	final private ThriftProtocolService<REQ, REP> target_;

}
