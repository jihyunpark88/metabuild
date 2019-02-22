package example02;

public class LogDataByMinute {
	
	private String time;
	private int totalCount;
	private long avgDuration;
	private long minDuration;
	private long maxDuration;
	private int avgSize;
	private int minSize;
	private int maxSize;

	public LogDataByMinute() {	}

	public LogDataByMinute(String time, int totalCount, long avgDuration, long minDuration, long maxDuration,
			int avgSize, int minSize, int maxSize) {
		this.time = time;
		this.totalCount = totalCount;
		this.avgDuration = avgDuration;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.avgSize = avgSize;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public long getAvgDuration() {
		return avgDuration;
	}
	public void setAvgDuration(long avgDuration) {
		this.avgDuration = avgDuration;
	}
	public long getMinDuration() {
		return minDuration;
	}
	public void setMinDuration(long minDuration) {
		this.minDuration = minDuration;
	}
	public long getMaxDuration() {
		return maxDuration;
	}
	public void setMaxDuration(long maxDuration) {
		this.maxDuration = maxDuration;
	}
	public int getAvgSize() {
		return avgSize;
	}
	public void setAvgSize(int avgSize) {
		this.avgSize = avgSize;
	}
	public int getMinSize() {
		return minSize;
	}
	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

}
