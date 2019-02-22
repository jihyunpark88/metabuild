package example02;

import java.util.List;
import java.util.Map;

public class MainApp {

	public static void main(String[] args) {		
		//���۽ð�
		long start = System.currentTimeMillis();
		
		LogDataClass logDataClass = new LogDataClass();
		
		//log���ϰ��
		String filePath = "C:\\\\Users\\\\meta\\\\Desktop\\\\2.�α����Ϻм�/galileo.log";
		
		//log������ �̿��Ͽ� File1�ۼ��� ���� List����
		List<LogData> logDataList = logDataClass.getDataListForFile1(filePath);
		
		//����1�� �����
		String newFile1Path = "C:\\Users\\meta\\Desktop\\2.�α����Ϻм�/file1.txt";
		logDataClass.makeFile1(newFile1Path, logDataList);
		
		//File2�ۼ��� ���� Map�ۼ�
		Map<String, LogDataByMinute> logDataByMinuteMap = logDataClass.getDataMapForFile2(logDataList);
		
		//����2�� �����
		String newFile2Path = "C:\\Users\\meta\\Desktop\\2.�α����Ϻм�/file2.txt";
		logDataClass.makeFile2(newFile2Path, logDataByMinuteMap);
		
		//����ð�
		long end = System.currentTimeMillis();
		
		//�ɸ��ð�
		System.out.println("�ҿ�ð�: " + (end-start) + "ms");
		//�޸� ��뷮 ��ȸ		
		System.out.println("Used Memory : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024 + "Kbyte");
	}
}
