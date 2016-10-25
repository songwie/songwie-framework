package com.songwie.concurrent.lock;

public abstract class IDistributedLock {
	public synchronized static void init(String host, String password){}
	public static boolean acquire(String key){
		return true;
	}
	public static boolean acquire(String key, long timeout){
		return true;
	}
	public static void release(String key) {}
}
