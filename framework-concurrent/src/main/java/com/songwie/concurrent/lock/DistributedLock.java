package com.songwie.concurrent.lock;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributedLock extends IDistributedLock{
	public static final Logger LOGGER = LoggerFactory.getLogger(DistributedLock.class);
	
	
	public static long value = 1;
	public static String servers = "192.168.30.107:2181";
	
	private static CuratorFramework curator = null;
	private static zkListener listener = null;
	private static ThreadLocal<InterProcessMutex> lockLocal = new ThreadLocal<InterProcessMutex>();
	
	
	public synchronized static void init(String servers,String pwd){
		if(curator==null){
			curator = CuratorFrameworkFactory.builder()
					.sessionTimeoutMs(30000)  
			        .connectionTimeoutMs(30000)  
					.retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE)).connectString(servers).build();
			listener = new zkListener();
		    curator.getConnectionStateListenable().addListener(listener) ;  
		    curator.start();
		}
	}
	public static boolean acquire(String key){
		try {
			InterProcessMutex lock = new InterProcessMutex(DistributedLock.curator, key);
			lock.acquire(3,TimeUnit.SECONDS);
		    lockLocal.set(lock);
		}catch (IllegalMonitorStateException e) {
			LOGGER.warn("DistributedLock acquire error",e);
			return false;
		}catch (Exception e) {
			LOGGER.warn("DistributedLock acquire error",e);
			return false;
		}
		return true;
	}
	public static boolean acquire(String key, long timeout){
		try {
			InterProcessMutex lock = new InterProcessMutex(DistributedLock.curator, key);
			lock.acquire(timeout,TimeUnit.SECONDS);
		    lockLocal.set(lock);
		} catch (Exception e) {
			LOGGER.warn("DistributedLock acquire error",e);
			return false;
		}
		return true;
	}
	public static void release(String key) {
		try {
			InterProcessMutex lock = lockLocal.get();
            lock.release();
        } catch (Exception e) {
			LOGGER.warn("DistributedLock release error",e);
        }
	}
	
	
	/*public static void main (String[] args) {
		DistributedLock.init("192.168.30.107:2181","");
		
        
	    final long start = System.currentTimeMillis();
	    Executor pool = Executors.newFixedThreadPool(10);
	    for (int i = 0; i < 300; i ++) {
	        pool.execute(new Runnable() {
	            public void run() {
	            	while(true){
	            		boolean getLock = false;
	            		try {
	            			if(DistributedLock.acquire("/lock", 2)){
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
			                	DistributedLock.release("/lock");
		                	}
		                }
	            	}
	            }
	        });
	    }
	}*/
}

class zkListener implements ConnectionStateListener{
	public static final Logger LOGGER = LoggerFactory.getLogger(DistributedLock.class);

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState state) {  
        if (state == ConnectionState.LOST) {  
            //连接丢失  
			LOGGER.warn("DistributedLock lost session with zookeeper");

        } else if (state == ConnectionState.CONNECTED) {  
            //连接新建  
        	System.out.println("connected with zookeeper");  
			LOGGER.warn("DistributedLock connected with zookeeper");
        } else if (state == ConnectionState.RECONNECTED) {  
			LOGGER.warn("DistributedLock reconnected with zookeeper");
              
        }  
    } 
	
}
