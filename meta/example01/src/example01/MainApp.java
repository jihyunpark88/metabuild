package example01;

public class MainApp {
	
	public static void main(String[] args) {
		//���۽ð�
		long startTime = System.currentTimeMillis();
		
		XMLDataClass xmlDataClass = new XMLDataClass();
		
		//������ �����ͼ� tFile�����
		String filePath = "C:\\Users\\meta\\Desktop\\1.XML ���� �м�";
		xmlDataClass.createTFile(filePath);

		//����ð�
		long endTime = System.currentTimeMillis();

		//�ɸ��ð�
		System.out.println("�ҿ�ð�: " + (endTime-startTime) + "ms");
		//�޸� ��뷮 ��ȸ		
		System.out.println("Used Memory : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024 + "Kbyte");
	}
}
