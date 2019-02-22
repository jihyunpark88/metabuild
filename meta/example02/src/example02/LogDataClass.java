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
		
		//ã�� ���ڿ� �Է�
		String textArray[] = {"eclipse.galileo-bean-thread-", "##galileo_bean start.", "ESB_TRAN_ID : ", "Content-Length:", "#galileo call time:", "StopWatch", "##galileo_bean end."};
		
		//data�� collecting�ϱ� ���� map
		Map<String, LogData> logDataMap = new LinkedHashMap<String, LogData>();

		//collecting�� �Ϸ�� data�� �ֱ� ���� list(return)
		List<LogData> logDataList = new ArrayList<LogData>();	
		
		//���Ͽ���
		File file = new File(filePath);
		BufferedReader bufferedReader = null;
		//�α������� ���پ� ���� String
		String currentLineText = new String();
		
		try {
			bufferedReader = new BufferedReader(new FileReader(file));					
			
			//���پ� �б�
			while((currentLineText = bufferedReader.readLine())!=null) {
				
				//thread���� �������� ������ continue; (while�� ���� ����������);
				if(!currentLineText.contains(textArray[0])) {
					continue;
				}				
				//(thread���� ������ ��츸 �����) thread���� threadName�� ����
				String threadName = findThreadNumber(currentLineText, textArray[0]);

				//line�� "##galileo_bean start."�� �����ϴ� ��� : �ű� LogData��ü ���� (thread�� setting) ��, Map�� put (key=thread��, value=LogData)
				if(currentLineText.contains(textArray[1])) {
					LogData newLogData = new LogData(currentLineText.substring(1, 18));
					logDataMap.put(threadName, newLogData);
					continue;
				}
				//�ش�thread���� key���ϴ� logData�� Map�� �������� ������ continue; (while�� ���� ����������);
				if(logDataMap.get(threadName)==null) {
					continue;
				}
				
				//line�� "ESB_TRAN_ID : " �Ǵ� "Content-Length:" �Ǵ� "#galileo call time:"�� �����ϴ� ���
				//(Map���� �ش� thread�� key������ �ϴ� value(LogData)�� ã�� �ش簪 setting)
				if(currentLineText.contains(textArray[2])||currentLineText.contains(textArray[3])||currentLineText.contains(textArray[4])) {
					checkCurrentLineText(textArray, currentLineText, logDataMap.get(threadName));
					continue;
				//line�� "StopWatch"�� �����ϴ� ��� (�״��� 7���� �� �о�ͼ� �� �����͸� ����)
				} else if(currentLineText.contains(textArray[5])){
					String currentLineStr = new String();
					for(int temp2=0; temp2<7; temp2++) {
						currentLineStr = bufferedReader.readLine();
						checkCurrentLineStr(currentLineStr, logDataMap.get(threadName));
					}//for�� ��
					continue;
				//line�� "##galileo_bean end."�� �����ϴ� ��� (Map���� �ش� thread�� key������ �ϴ� value(LogData)�� ã�� �ش簪 setting)
				} else if(currentLineText.contains(textArray[6])){
					logDataMap.get(threadName).setEndTime(currentLineText.substring(1, 18));
					//logDataList�� �߰��ϰ� logDataMap���� ������
					logDataList.add(logDataMap.get(threadName));
					logDataMap.remove(threadName);
					continue;
				}

			}//���پ� �б� while�� ��			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			//bufferedReader �ݱ�
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
	 * currentLineText�� ���ϴ� ã�� ���ڿ��� �����ϴ� ���
	 * Map���� �ش� threadName�� key������ �ϴ� value(LogData)�� ã�� �ش簪�� setting
	 * @param textArray
	 * @param currentLineText
	 * @param logData
	 */
	private void checkCurrentLineText(String[] textArray, String currentLineText, LogData logData) {
		
		//line�� "ESB_TRAN_ID : "�� �����ϴ� ��� (Map���� �ش� thread�� key������ �ϴ� value(LogData)�� ã�� �ش簪 setting)
		if(currentLineText.contains(textArray[2])) {
			logData.setEsbTranId(findData(currentLineText, textArray[2]));
		//line�� "Content-Length:"�� �����ϴ� ��� (Map���� �ش� thread�� key������ �ϴ� value(LogData)�� ã�� �ش簪 setting)
		} else if(currentLineText.contains(textArray[3])) {
			logData.setContentLength(findData(currentLineText, textArray[3]));
		//line�� "#galileo call time:"�� �����ϴ� ��� (Map���� �ش� thread�� key������ �ϴ� value(LogData)�� ã�� �ش簪 setting)
		} else if(currentLineText.contains(textArray[4])) {
			String arr[] = findData(currentLineText, textArray[4]).split("ms");
			logData.setGalileoCallTime(arr[0].trim());			
		}
	}
	
	/**
	 * currentLineStr�� ���ϴ� ã�� ���ڿ��� �����ϴ� ���
	 * Map���� �ش� threadName�� key������ �ϴ� value(LogData)�� ã�� �ش簪�� setting
	 * @param currentLineStr
	 * @param logData
	 */
	private void checkCurrentLineStr(String currentLineStr, LogData logData) {		
		//ã�� ���ڿ� �Է�
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
	 * threadNumber ��������
	 * @param currentText
	 * @param splitText
	 * @return threadNumber
	 */
	private String findThreadNumber(String currentText, String splitText) {
		String array[] = currentText.split(splitText);		
		return array[1].substring(0, 8);
	}
	
	/**
	 * Ư�� text ã�ƿ���
	 * (CurrentText�� keyword�� �������� �߶� �� ������ ���ڿ��� ������)
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
	 * logDataList�� �̿��Ͽ� file1 �ۼ��ϱ�
	 * @param logDataList
	 */
	public void makeFile1(String filePath, List<LogData> logDataList) {
		
		//logDataList�� logData�� null���� �ִ��� üũ�ϱ�
		checkEntireList(logDataList);
		//logDataList�� startTime������ �����ϱ�
		Collections.sort(logDataList);
		
		File file = new File(filePath);
		BufferedWriter bufferedWriter = null;
		
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			
			for(int temp=0; temp<logDataList.size(); temp++) {
				//canBeUsed���� true�ΰ͸� �ۼ�
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
	 * logDataList���� LogData�� ������ null���� ���� ���
	 * canBeUsed���� false�� setting
	 * @param logDataList
	 */
	private void checkEntireList(List<LogData> logDataList) {
		//��� logDataList�� üũ
		for(int temp=0; temp<logDataList.size(); temp++) {				
			Object obj= logDataList.get(temp);
			int cnt = 0;
			
			//�� logDataList�� ��� field�� üũ
			for(Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				try {
					if(field.get(obj)==null) {
						break;
					}
					//value�� null�� �ƴϸ� cnt++
					cnt++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}//������ for�� ��
			
			//��� field���� null�� �ƴϸ� setCanBeUsed���� true�� ����
			if(cnt==obj.getClass().getDeclaredFields().length) {
				logDataList.get(temp).setCanBeUsed(true);				
			}
		}//for�� ��
	}
	
	/**
	 * File2�� �ۼ��ϱ� ���� Map����
	 * @param logDataList
	 * @return
	 */
	public Map<String, LogDataByMinute> getDataMapForFile2(List<LogData> logDataList){
		
		//canBeUsed�� false�� ���� List���� ����
		for(int temp = 0; temp<logDataList.size(); temp++) {
			if(logDataList.get(temp).getCanBeUsed()==false) {
				logDataList.remove(temp);
			}
		}
		
		//return�� ���� logDataByMinuteMap
		Map<String, LogDataByMinute> logDataByMinuteMap = new LinkedHashMap<String, LogDataByMinute>();
		
		//logDataList���� ���� logData��ü�� ���簪�� ������ Map�� ����ִ� ��ü�� ���� ���ϱ� ���� ����
		int minutes = 0;
		String time = null;
		int totalCount = 0;
		long totalDuration = 0;
		int totalSize = 0;
		
		//logDataList�� ������ logData��ü�� �� (�ش�List�� ���۽ð� ������ ���ĵǾ�����)
		for(LogData logData: logDataList) {
			try {
				DateFormat dateFormat = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
				Date startDate = dateFormat.parse(logData.getStartTime());
				Date endDate = dateFormat.parse(logData.getEndTime());
				
				//������ '��'�� ������ �ٸ��ٸ� : LogDataByMinute��ü ���� �� �� setting (���� ���� �Ѱ��� �ִ�/�ּ� ���� ���� ��� ���� ó������ ����) -- Map�� ����
				if(minutes!=startDate.getMinutes()) {
					minutes = startDate.getMinutes();
					time = logData.getStartTime().substring(0, 14);
					totalCount = 1;
					totalDuration = (endDate.getTime() - startDate.getTime());
					totalSize = Integer.parseInt(logData.getContentLength());
					logDataByMinuteMap.put(time, new LogDataByMinute(time, totalCount, totalDuration/totalCount, totalDuration, totalDuration, totalSize/totalCount, totalSize, totalSize));
				//������ '��'�� ������ ���ٸ� : ������ LogDataByMinute��ü�� �� setting (total���� �������� ���ϰ�, min, max�� �������� ���簪�� �ٽ� ���ؼ� setting)
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
	 * logDataByMinuteMap�� �̿��Ͽ� file2 �ۼ��ϱ�
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

