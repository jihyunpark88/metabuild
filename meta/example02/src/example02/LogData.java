package example02;

public class LogData implements Comparable<LogData> {
	
	private String startTime;
	private String endTime;
	private String esbTranId;
	private String contentLength;
	private String galileoCallTime;
	private String beforeMarshalling;
	private String marshalling;
	private String invokingGalileo;
	private String unmarshallingAndSendToCmmModServer;
	private Boolean canBeUsed = false;
	
	public LogData() {	}

	public LogData(String startTime) {
		this.startTime = startTime;
	}

	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEsbTranId() {
		return esbTranId;
	}
	public void setEsbTranId(String esbTranId) {
		this.esbTranId = esbTranId;
	}
	public String getContentLength() {
		return contentLength;
	}
	public void setContentLength(String contentLength) {
		this.contentLength = contentLength;
	}
	public String getGalileoCallTime() {
		return galileoCallTime;
	}
	public void setGalileoCallTime(String galileoCallTime) {
		this.galileoCallTime = galileoCallTime;
	}
	public String getBeforeMarshalling() {
		return beforeMarshalling;
	}
	public void setBeforeMarshalling(String beforeMarshalling) {
		this.beforeMarshalling = beforeMarshalling;
	}
	public String getMarshalling() {
		return marshalling;
	}
	public void setMarshalling(String marshalling) {
		this.marshalling = marshalling;
	}
	public String getInvokingGalileo() {
		return invokingGalileo;
	}
	public void setInvokingGalileo(String invokingGalileo) {
		this.invokingGalileo = invokingGalileo;
	}
	public String getUnmarshallingAndSendToCmmModServer() {
		return unmarshallingAndSendToCmmModServer;
	}
	public void setUnmarshallingAndSendToCmmModServer(String unmarshallingAndSendToCmmModServer) {
		this.unmarshallingAndSendToCmmModServer = unmarshallingAndSendToCmmModServer;
	}
	public Boolean getCanBeUsed() {
		return canBeUsed;
	}
	public void setCanBeUsed(Boolean canBeUsed) {
		this.canBeUsed = canBeUsed;
	}

	@Override
	public int compareTo(LogData logData) {
		return startTime.compareTo(logData.getStartTime());
	}

}
