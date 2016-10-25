package com.songwie.concurrent.lock;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DistributedLockRedis extends IDistributedLock{
	private static Logger logger = LoggerFactory.getLogger(DistributedLockRedis.class);
	public static long value = 1;

	private static Config config = new Config();
	private static Redisson redisson = null;
	
	private static final long DEFAULT_TIMEOUT = 3; 
	
	public synchronized static void init(String host, String password) {
		if(redisson==null){
    		config.useSingleServer().setAddress(host);
            config.useSingleServer().setPassword(password);
    		redisson = Redisson.create(config);
    	}
		/*config.useClusterServers()
	    .setScanInterval(2000) // cluster state scan interval in milliseconds
	    .addNodeAddress(host)
	    .setPassword(password);*/
		
		redisson = Redisson.create(config);
	}
	public static boolean acquire(String key)  {
		logger.info("try to lock : " + key);
		try {
			RLock mylock = redisson.getLock(key);
			mylock.lock(DEFAULT_TIMEOUT, TimeUnit.SECONDS);//10s超时

			logger.info("try to lock : " + key +" success...");
    		return true;
		} catch (Exception e) {
			logger.warn("DistributedLock try lock :" + key +"error:" + e.getMessage());
			return false;
		}
	}
	public static boolean acquire(String key, long timeout)  {
		logger.info("try to lock : " + key);
		try {
			RLock mylock = redisson.getLock(key);
			mylock.lock(timeout, TimeUnit.SECONDS);//10s超时

			logger.info("try to lock : " + key +" success...");
    		return true;
		} catch (Exception e) {
			logger.warn("DistributedLock try lock :" + key +"error:" + e.getMessage());
			return false;
		}
	}
	
	public static void release(String key) {
		logger.info("try to release lock : " + key);
		RLock mylock = redisson.getLock(key);
        mylock.unlock();		
	}
	
	/*public static void main (String[] args) {
		DistributedLockRedis.init("192.168.30.246:6379","");
		
        
	    long start = System.currentTimeMillis();
	    Executor pool = Executors.newFixedThreadPool(10);
	    for (int i = 0; i < 300; i ++) {
	        pool.execute(new Runnable() {
	            public void run() {
	            	while(true){
	            		boolean getLock = false;
	            		try {
	            			if(DistributedLockRedis.acquire("/lock", 2)){
	            				getLock = true;
	            				value++;
			                    System.out.println(Thread.currentThread().getName()+","+value);
			                    //Thread.sleep(1);
			            	    long end = System.currentTimeMillis();
			            	    long tps = value/((end-start)/1000);
			                    System.out.println("tps count/s : "+tps);
	            			}
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }finally{
		                	if(getLock){
		                		DistributedLockRedis.release("/lock");
		                	}
		                }
	            	}
	            }
	        });
	    }
	}*/
}
