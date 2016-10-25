package com.songwie.util.disSeq;

/**
 * @brief 
 * @details 
 * @author songwie
 * @date 2016年8月26日
 */
public class IdCreater {
	private static DisSequnceUtil disSequnceUtil = new DisSequnceUtil(1);
	
	public static long getNextId(){
		return disSequnceUtil.nextId();
	}
	public static long getNextId(int wordId){
		disSequnceUtil = new DisSequnceUtil(wordId);
		return disSequnceUtil.nextId();
	}

}
