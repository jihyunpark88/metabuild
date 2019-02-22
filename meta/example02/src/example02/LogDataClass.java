package example02;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogDataClass {	
	
	public List<LogData> getDataListForFile1(String filePath) {
		
		//찾을 문자열 입력
		String textArray[] = {"eclipse.galileo-bean-thread-", "##galileo_bean start.", "ESB_TRAN_ID : ", "Content-Length:", "#galileo call time:", "StopWatch", "##galileo_bean end."};
		
		//data를 collecting하기 위한 map
		Map<String, LogData> logDataMap = new LinkedHashMap<String, LogData>();

		//collecting이 완료된 data를 넣기 위한 list(return)
		List<LogData> logDataList = new ArrayList<LogData>();	
		
		//파일열기
		File file = new File(filePath);
		BufferedReader bufferedReader = null;
		//로그파일을 한줄씩 읽을 String
		String currentLineText = new String();
		
		try {
			bufferedReader = new BufferedReader(new FileReader(file));					
			
			//한줄씩 읽기
			while((currentLineText = bufferedReader.readLine())!=null) {
				
				//thread명이 존재하지 않으면 continue; (while문 제일 마지막으로);
				if(!currentLineText.contains(textArray[0])) {
					continue;
				}				
				//(thread명이 존재할 경우만 진행됨) thread명을 threadName에 저장
				String threadName = findThreadNumber(currentLineText, textArray[0]);

				//line에 "##galileo_bean start."가 존재하는 경우 : 신규 LogData객체 생성 (thread명 setting) 후, Map에 put (key=thread명, value=LogData)
				if(currentLineText.contains(textArray[1])) {
					LogData newLogData = new LogData(currentLineText.substring(1, 18));
					logDataMap.put(threadName, newLogData);
					continue;
				}
				//해당thread명을 key로하는 logData가 Map에 존재하지 않으면 continue; (while문 제일 마지막으로);
				if(logDataMap.get(threadName)==null) {
					continue;
				}
				
				//line에 "ESB_TRAN_ID : " 또는 "Content-Length:" 또는 "#galileo call time:"가 존재하는 경우
				//(Map에서 해당 thread를 key값으로 하는 value(LogData)를 찾아 해당값 setting)
				if(currentLineText.contains(textArray[2])||currentLineText.contains(textArray[3])||currentLineText.contains(textArray[4])) {
					checkCurrentLineText(textArray, currentLineText, logDataMap.get(threadName));
					continue;
				//line에 "StopWatch"가 존재하는 경우 (그다음 7줄을 더 읽어와서 각 데이터를 추출)
				} else if(currentLineText.contains(textArray[5])){
					String currentLineStr = new String();
					for(int temp2=0; temp2<7; temp2++) {
						currentLineStr = bufferedReader.readLine();
						checkCurrentLineStr(currentLineStr, logDataMap.get(threadName));
					}//for문 끝
					continue;
				//line에 "##galileo_bean end."가 존재하는 경우 (Map에서 해당 thread를 key값으로 하는 value(LogData)를 찾아 해당값 setting)
				} else if(currentLineText.contains(textArray[6])){
					logDataMap.get(threadName).setEndTime(currentLineText.substring(1, 18));
					//logDataList에 추가하고 logDataMap에서 제거함
					logDataList.add(logDataMap.get(threadName));
					logDataMap.remove(threadName);
					continue;
				}

			}//한줄씩 읽기 while문 끝			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			//bufferedReader 닫기
			if(bufferedReader!=null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return logDataList;
	}
	
	/**
	 * currentLineText에 원하는 찾는 문자열이 존재하는 경우
	 * Map에서 해당 threadName을 key값으로 하는 value(LogData)를 찾아 해당값을 setting
	 * @param textArray
	 * @param currentLineText
	 * @param logData
	 */
	private void checkCurrentLineText(String[] textArray, String currentLineText, LogData logData) {
		
		//line에 "ESB_TRAN_ID : "가 존재하는 경우 (Map에서 해당 thread를 key값으로 하는 value(LogData)를 찾아 해당값 setting)
		if(currentLineText.contains(textArray[2])) {
			logData.setEsbTranId(findData(currentLineText, textArray[2]));
		//line에 "Content-Length:"가 존재하는 경우 (Map에서 해당 thread를 key값으로 하는 value(LogData)를 찾아 해당값 setting)
		} else if(currentLineText.contains(textArray[3])) {
			logData.setContentLength(findData(currentLineText, textArray[3]));
		//line에 "#galileo call time:"가 존재하는 경우 (Map에서 해당 thread를 key값으로 하는 value(LogData)를 찾아 해당값 setting)
		} else if(currentLineText.contains(textArray[4])) {
			String arr[] = findData(currentLineText, textArray[4]).split("ms");
			logData.setGalileoCallTime(arr[0].trim());			
		}
	}
	
	/**
	 * currentLineStr에 원하는 찾는 문자열이 존재하는 경우
	 * Map에서 해당 threadName을 key값으로 하는 value(LogData)를 찾아 해당값을 setting
	 * @param currentLineStr
	 * @param logData
	 */
	private void checkCurrentLineStr(String currentLineStr, LogData logData) {		
		//찾을 문자열 입력
		String[] textStr = {"1. Before Marshalling", "2. Marshalling", "3. Invoking galileo", "4. Unmarshalling and Send to CmmMod Server"};
		
		for(int temp=0; temp<textStr.length; temp++) {
			if(currentLineStr.contains(textStr[0])) {
				logData.setBeforeMarshalling(currentLineStr.substring(0, 5));
			} else if(currentLineStr.contains(textStr[1])) {
				logData.setMarshalling(currentLineStr.substring(0, 5));
			} else if(currentLineStr.contains(textStr[2])) {
				logData.setInvokingGalileo(currentLineStr.substring(0, 5));
			} else if(currentLineStr.contains(textStr[3])) {
				logData.setUnmarshallingAndSendToCmmModServer(currentLineStr.substring(0, 5));
			}
		}
	}

	/**
	 * threadNumber 가져오기
	 * @param currentText
	 * @param splitText
	 * @return threadNumber
	 */
	private String findThreadNumber(String currentText, String splitText) {
		String array[] = currentText.split(splitText);		
		return array[1].substring(0, 8);
	}
	
	/**
	 * 특정 text 찾아오기
	 * (CurrentText를 keyword를 기준으로 잘라 그 뒤쪽의 문자열을 가져옴)
	 * @param CurrentText
	 * @param keyword
	 * @return data
	 */
	private String findData(String CurrentText, String keyword) {
		String data = null;
		String split[] = CurrentText.split(keyword);
		if(split.length!=1) {
			data = split[1];
		}
		return data;
	}
	
	/**
	 * logDataList를 이용하여 file1 작성하기
	 * @param logDataList
	 */
	public void makeFile1(String filePath, List<LogData> logDataList) {
		
		//logDataList의 logData중 null값이 있는지 체크하기
		checkEntireList(logDataList);
		//logDataList를 startTime순서로 정렬하기
		Collections.sort(logDataList);
		
		File file = new File(filePath);
		BufferedWriter bufferedWriter = null;
		
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			
			for(int temp=0; temp<logDataList.size(); temp++) {
				//canBeUsed값이 true인것만 작성
				if(logDataList.get(temp).getCanBeUsed()==true) {
					
					bufferedWriter.write(logDataList.get(temp).getStartTime());
					bufferedWriter.write(", ");
					bufferedWriter.write(logDataList.get(temp).getEndTime());
					bufferedWriter.write(", ");
					bufferedWriter.write(logDataList.get(temp).getEsbTranId());
					bufferedWriter.write(", ");
					bufferedWriter.write(logDataList.get(temp).getContentLength());
					bufferedWriter.write(", ");
					bufferedWriter.write(logDataList.get(temp).getGalileoCallTime());
					bufferedWriter.write(", ");
					bufferedWriter.write(logDataList.get(temp).getBeforeMarshalling());
					bufferedWriter.write(", ");
					bufferedWriter.write(logDataList.get(temp).getMarshalling());
					bufferedWriter.write(", ");
					bufferedWriter.write(logDataList.get(temp).getInvokingGalileo());
					bufferedWriter.write(", ");
					bufferedWriter.write(logDataList.get(temp).getUnmarshallingAndSendToCmmModServer());
					bufferedWriter.write("\n");					
				}
			}
			bufferedWriter.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bufferedWriter!=null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}
	}
	
	/**
	 * logDataList에서 LogData의 변수에 null값이 있을 경우
	 * canBeUsed값을 false로 setting
	 * @param logDataList
	 */
	private void checkEntireList(List<LogData> logDataList) {
		//모든 logDataList를 체크
		for(int temp=0; temp<logDataList.size(); temp++) {				
			Object obj= logDataList.get(temp);
			int cnt = 0;
			
			//각 logDataList의 모든 field를 체크
			for(Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				try {
					if(field.get(obj)==null) {
						break;
					}
					//value가 null이 아니면 cnt++
					cnt++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}//개선된 for문 끝
			
			//모든 field값이 null이 아니면 setCanBeUsed값을 true로 변경
			if(cnt==obj.getClass().getDeclaredFields().length) {
				logDataList.get(temp).setCanBeUsed(true);				
			}
		}//for문 끝
	}
	
	/**
	 * File2를 작성하기 위한 Map생성
	 * @param logDataList
	 * @return
	 */
	public Map<String, LogDataByMinute> getDataMapForFile2(List<LogData> logDataList){
		
		//canBeUsed가 false인 것은 List에서 제거
		for(int temp = 0; temp<logDataList.size(); temp++) {
			if(logDataList.get(temp).getCanBeUsed()==false) {
				logDataList.remove(temp);
			}
		}
		
		//return을 위한 logDataByMinuteMap
		Map<String, LogDataByMinute> logDataByMinuteMap = new LinkedHashMap<String, LogDataByMinute>();
		
		//logDataList에서 나온 logData객체의 현재값과 기존에 Map에 들어있던 객체의 값을 비교하기 위한 변수
		int minutes = 0;
		String time = null;
		int totalCount = 0;
		long totalDuration = 0;
		int totalSize = 0;
		
		//logDataList에 각각의 logData객체를 비교 (해당List는 시작시간 순으로 정렬되어있음)
		for(LogData logData: logDataList) {
			try {
				DateFormat dateFormat = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
				Date startDate = dateFormat.parse(logData.getStartTime());
				Date endDate = dateFormat.parse(logData.getEndTime());
				
				//시작의 '분'이 기존과 다르다면 : LogDataByMinute객체 생성 및 값 setting (아직 값이 한개라 최대/최소 등의 값은 모두 최초 처음값과 동일) -- Map에 저장
				if(minutes!=startDate.getMinutes()) {
					minutes = startDate.getMinutes();
					time = logData.getStartTime().substring(0, 14);
					totalCount = 1;
					totalDuration = (endDate.getTime() - startDate.getTime());
					totalSize = Integer.parseInt(logData.getContentLength());
					logDataByMinuteMap.put(time, new LogDataByMinute(time, totalCount, totalDuration/totalCount, totalDuration, totalDuration, totalSize/totalCount, totalSize, totalSize));
				//시작의 '분'이 기존과 같다면 : 기존의 LogDataByMinute객체에 값 setting (total값은 기존값과 더하고, min, max는 기존값과 현재값을 다시 비교해서 setting)
				} else {
					totalCount++;
					totalDuration = totalDuration + (endDate.getTime() - startDate.getTime());
					totalSize = totalSize + Integer.parseInt(logData.getContentLength());
					logDataByMinuteMap.get(time).setTotalCount(totalCount);
					logDataByMinuteMap.get(time).setAvgDuration(totalDuration/totalCount);
					logDataByMinuteMap.get(time).setMinDuration(Math.min((endDate.getTime() - startDate.getTime()), logDataByMinuteMap.get(time).getMinDuration()));
					logDataByMinuteMap.get(time).setMaxDuration(Math.max((endDate.getTime() - startDate.getTime()), logDataByMinuteMap.get(time).getMaxDuration()));
					logDataByMinuteMap.get(time).setAvgSize(totalSize/totalCount);
					logDataByMinuteMap.get(time).setMinSize(Math.min(Integer.parseInt(logData.getContentLength()), logDataByMinuteMap.get(time).getMinSize()));
					logDataByMinuteMap.get(time).setMaxSize(Math.max(Integer.parseInt(logData.getContentLength()), logDataByMinuteMap.get(time).getMaxSize()));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return logDataByMinuteMap;
	}
	
	/**
	 * logDataByMinuteMap를 이용하여 file2 작성하기
	 * @param filePath
	 * @param logDataByMinuteMap
	 */
	public void makeFile2(String filePath, Map<String, LogDataByMinute> logDataByMinuteMap) {

		File file = new File(filePath);
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			
			Set<String> keySet = logDataByMinuteMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			
			while(iterator.hasNext()){				
				String key = iterator.next();
				
				bufferedWriter.write(logDataByMinuteMap.get(key).getTime());
				bufferedWriter.write(", ");
				bufferedWriter.write(Integer.toString(logDataByMinuteMap.get(key).getTotalCount()));
				bufferedWriter.write(", ");
				bufferedWriter.write(Long.toString(logDataByMinuteMap.get(key).getAvgDuration()));
				bufferedWriter.write(", ");
				bufferedWriter.write(Long.toString(logDataByMinuteMap.get(key).getMinDuration()));
				bufferedWriter.write(", ");
				bufferedWriter.write(Long.toString(logDataByMinuteMap.get(key).getMaxDuration()));
				bufferedWriter.write(", ");
				bufferedWriter.write(Integer.toString(logDataByMinuteMap.get(key).getAvgSize()));
				bufferedWriter.write(", ");
				bufferedWriter.write(Integer.toString(logDataByMinuteMap.get(key).getMinSize()));
				bufferedWriter.write(", ");
				bufferedWriter.write(Integer.toString(logDataByMinuteMap.get(key).getMaxSize()));
				bufferedWriter.write("\n");
				
				}
			bufferedWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bufferedWriter!=null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}

