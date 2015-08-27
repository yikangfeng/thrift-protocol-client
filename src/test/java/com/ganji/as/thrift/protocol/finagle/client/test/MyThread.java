package com.ganji.as.thrift.protocol.finagle.client.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.thrift.TException;

import com.ganji.as.thrift.protocol.client.test.AchieveResult;
import com.ganji.as.thrift.protocol.client.test.OperationException;
import com.ganji.as.thrift.protocol.client.test.ParamterException;


public class MyThread implements Runnable{
	private SyncFinagleClient syncClient;
	private String token="";
	private String name="";
	
	public MyThread(SyncFinagleClient syncClient,String token,String name){
		this.syncClient=syncClient;
		this.token=token;
		this.name = name;
	}

	@Override
	public void run() {
//		syncClient.init("192.168.2.119", 8888);
		String end_time="2015-07-31 23:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date end_date = null;
		try {
			end_date = sdf.parse(end_time);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		long end = end_date.getTime();
		String param="{\"biztype\":\"feedcomment\",\"opetype\":\"get\",\"data\":{\"type\":\"feedcomment\",\"userid\":\"784529\",\"ip\":\"3232238870\",\"cookie\":\"2549519450401865457677-654644261\",\"content\":\"老乡说抽取习近平调试\",\"clienttype\":\"801\",\"useragent\":\"\"}}";
		// TODO 循环执行一天
		for(long a=0;a<end;){
			System.out.println("time========================"+name);
			Date d= new Date();
			long time = d.getTime();
			a=time;
			try {
				AchieveResult asr= syncClient.get(param, token);
				syncClient.clear();
				System.out.println("==="+param+" isshot="+asr.isIsShot()+" result===="+asr.getDetail());
			} catch (ParamterException e) {
				e.printStackTrace();
			} catch (OperationException e) {
				e.printStackTrace();
			} catch (TException e) {
				e.printStackTrace();
			}
							
		}
		
	}
	

}
