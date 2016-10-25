/**
 * 
 */
package com.songwie.util.disSeq;

/**
 * @brief
 * @details
 * @author songwie
 * @date 2016年8月22日
 */
public class DisSequnceUtil {
	private final long workerId;
	private final static long twepoch = 1361753741828L;
	private long sequence = 0L;
	private final static long workerIdBits = 4L;
	public final static long maxWorkerId = -1L ^ -1L << workerIdBits;
	private final static long sequenceBits = 10L;
	private final static long workerIdShift = sequenceBits;
	private final static long timestampLeftShift = sequenceBits + workerIdBits;
	public final static long sequenceMask = -1L ^ -1L << sequenceBits;
	private long lastTimestamp = -1L;

	public DisSequnceUtil(final long workerId) {
		super();
		if (workerId > this.maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
					String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
		}
		this.workerId = workerId;
	}
	public synchronized long nextId() {
		long timestamp = this.timeGen();
		if (this.lastTimestamp == timestamp) {
			this.sequence = (this.sequence + 1) & this.sequenceMask;
			if (this.sequence == 0) {
				timestamp = this.tilNextMillis(this.lastTimestamp);
			}
		} else {
			this.sequence = 0;
		}
		if (timestamp < this.lastTimestamp) {
			try {
				throw new Exception(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
						this.lastTimestamp - timestamp));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.lastTimestamp = timestamp;
		long nextId = ((timestamp - twepoch << timestampLeftShift)) | (this.workerId << this.workerIdShift)
				| (this.sequence);
		return nextId;
	}

	private long tilNextMillis(final long lastTimestamp) {
		long timestamp = this.timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = this.timeGen();
		}
		return timestamp;
	}

	private long timeGen() {
		return System.currentTimeMillis();
	}

	/*public static void main(String[] args) {
		DisSequnceUtil worker2 = new DisSequnceUtil(2);
		Map<Long, Long> data = new java.util.HashMap<>();
		
		for(int i=0;i<10;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true){
						long idd = worker2.nextId();
						if(data.containsKey(idd)){
							System.err.println("冲突：" + idd);
						}
						if(data.size() % 100000 == 0){
							System.err.println("+ 1000000" );
						}
						data.put(idd, idd);
					}
				}
			}).start();
		}
	    
	    
	}*/
}
