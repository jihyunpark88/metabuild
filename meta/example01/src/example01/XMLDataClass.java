package example01;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLDataClass {
	
	public void createTFile(String filePath) {		
		
		//T_BASEFILE_TB.xml가져오기
		Document baseDocument = getParsedFile(filePath + "/T_BASEFILE_TB.xml");
		
		//baseFile의 FILE_ID tag의 node를 nodeList로 저장
		Map<String, NodeList> baseFileMap = getNodeLists(baseDocument, "FILE_ID");

		//T_BASEFILE_TB.xml의 {FILE_ID}별로 진행
		for(int temp=0; temp<baseFileMap.get("FILE_ID").getLength(); temp++) {	
			
			String fileId = baseFileMap.get("FILE_ID").item(temp).getTextContent();
			
			//F_{FILE_ID}_TB.xml 가져오기
			Document fDocument = getParsedFile(filePath + "/F_" + fileId + "_TB.xml");			
			//fFile의 SIMILAR_RATE, P_ID, COMMENT tag의 nodeList를 Map에 저장
			Map<String, NodeList> fFileMap = getNodeLists(fDocument, "SIMILAR_RATE", "P_ID", "COMMENT");
			
			//P_{FILE_ID}_TB.xml 가져오기
			Document pDocument = getParsedFile(filePath + "/P_" + fileId + "_TB.xml");			
			//pFile의 P_ID, LICENSE_ID tag의 nodeList를 Map에 저장
			Map<String,NodeList> pFileMap = getNodeLists(pDocument, "P_ID", "LICENSE_ID");			
			
			changeNodeValue(fFileMap, pFileMap);
			
			//tFile을 생성
			File tFile = new File(filePath+"/T_"+fileId+"_TB.xml");			

			//변경된fDcoument를 tFile로 저장
			saveXMLFile(fDocument, tFile);
		}//for문 끝	
	}
	
	/**
	 * fFile의 SIMILAR_RATE을 100으로 나눈값이 15보다 큰 경우
	 * fFile과 동일한 P_ID를 갖는 pFile의 LICENSE_ID를
	 * fFile의 COMMENT의 값으로 세팅
	 * @param fFileMap
	 * @param pFileMap
	 */
	private void changeNodeValue(Map<String,NodeList> fFileMap, Map<String,NodeList> pFileMap) {
		
		//F_{FILE_ID}_TB.xml의 SIMILAR_RATE별로 진행
		for(int temp=0; temp<fFileMap.get("SIMILAR_RATE").getLength(); temp++) {
			
			String similarRate = fFileMap.get("SIMILAR_RATE").item(temp).getTextContent();
			//fFile의 SIMILAR_RATE를 100으로 나눈값이 15보다 크고 & P_ID가 존재하는 경우
			if(Integer.parseInt(similarRate)/100>15 && !fFileMap.get("P_ID").item(temp).getTextContent().equals("")) {
				//해당 fFile의 P_ID값을 pIdFromF변수에 저장
				String pIdFromF = fFileMap.get("P_ID").item(temp).getTextContent();
				
				//pIdFromF값과 같은 pFile의 P_ID의 LICENSE_ID의 값을 저장
				Node licenseIdNode = searchNodeList(pIdFromF, pFileMap.get("P_ID"), pFileMap.get("LICENSE_ID"));
				
				//fFile의 COMMENT의 값을 licenseId값으로 변경
				if(!licenseIdNode.getTextContent().equals("")) {
					/*getNodeLists(document, "COMMENT").get("COMMENT").item(temp).setTextContent("11");*/					
					fFileMap.get("COMMENT").item(temp).setTextContent(licenseIdNode.getTextContent());
				}
			}//if문 끝
		}//for문 끝
	}

	/**
	 * 해당 파일을 가져와 파싱하여 document를 return
	 * @param filePath
	 * @return document
	 */
	private Document getParsedFile(String filePath) {
		//해당 파일을 가져옴
		File file = new File(filePath);		
			//xmlFile을 파싱해주는 객체를 생성
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;
			Document document = null;			
			try {
				documentBuilder = factory.newDocumentBuilder();
				//file parsing
				document = documentBuilder.parse(file);
				document.getDocumentElement().normalize();				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return document;
	}
	
	/**
	 * 해당 document의 tagNames에 맞는 nodeList를 return(XPath 사용)
	 * @param document
	 * @param tagNames
	 * @return nodeList
	 */
	private Map<String, NodeList> getNodeLists(Document document, String... tagNames){
		Map<String, NodeList> nodeListsMap = new HashMap<String, NodeList>();
		
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
        	for(int temp=0; temp<tagNames.length; temp++) {
        		NodeList nodeList = (NodeList) xpath.evaluate("TABLE/ROWS/ROW/"+tagNames[temp], document, XPathConstants.NODESET);
        		nodeListsMap.put(tagNames[temp], nodeList);
        	}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeListsMap;
	}

	/**
	 * 해당 targetNodeList에서 searchkey와 동일한 nodeValue를 가진 node의 다른 tag(returnNodeList)의 node를 return  
	 * @param searchKey
	 * @param targetNodeList
	 * @param returnNodeList
	 * @return node
	 */
	private Node searchNodeList(String searchKey, NodeList targetNodeList, NodeList returnNodeList) {
		Node node = null;		
		for(int temp=0; temp<targetNodeList.getLength(); temp++) {
			if(searchKey.equals(targetNodeList.item(temp).getTextContent())) {
				node = returnNodeList.item(temp);
			}
		}		
		return node;
	}
	
	/**
	 * 해당 document를 XMLfile로 생성
	 * @param document
	 * @param file
	 */
	private void saveXMLFile(Document document, File file) {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");			
			transformer.transform(new DOMSource(document), new StreamResult(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
