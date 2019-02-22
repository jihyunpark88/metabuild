package example01;

public class MainApp {
	
	public static void main(String[] args) {
		//시작시간
		long startTime = System.currentTimeMillis();
		
		XMLDataClass xmlDataClass = new XMLDataClass();
		
		//파일을 가져와서 tFile만들기
		String filePath = "C:\\Users\\meta\\Desktop\\1.XML 파일 분석";
		xmlDataClass.createTFile(filePath);

		//종료시간
		long endTime = System.currentTimeMillis();

		//걸린시간
		System.out.println("소요시간: " + (endTime-startTime) + "ms");
		//메모리 사용량 조회		
		System.out.println("Used Memory : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024 + "Kbyte");
	}
}
