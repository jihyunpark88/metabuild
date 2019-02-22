package example02;

import java.util.List;
import java.util.Map;

public class MainApp {

	public static void main(String[] args) {		
		//시작시간
		long start = System.currentTimeMillis();
		
		LogDataClass logDataClass = new LogDataClass();
		
		//log파일경로
		String filePath = "C:\\\\Users\\\\meta\\\\Desktop\\\\2.로그파일분석/galileo.log";
		
		//log파일을 이용하여 File1작성을 위한 List추출
		List<LogData> logDataList = logDataClass.getDataListForFile1(filePath);
		
		//파일1로 만들기
		String newFile1Path = "C:\\Users\\meta\\Desktop\\2.로그파일분석/file1.txt";
		logDataClass.makeFile1(newFile1Path, logDataList);
		
		//File2작성을 위한 Map작성
		Map<String, LogDataByMinute> logDataByMinuteMap = logDataClass.getDataMapForFile2(logDataList);
		
		//파일2로 만들기
		String newFile2Path = "C:\\Users\\meta\\Desktop\\2.로그파일분석/file2.txt";
		logDataClass.makeFile2(newFile2Path, logDataByMinuteMap);
		
		//종료시간
		long end = System.currentTimeMillis();
		
		//걸린시간
		System.out.println("소요시간: " + (end-start) + "ms");
		//메모리 사용량 조회		
		System.out.println("Used Memory : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024 + "Kbyte");
	}
}
