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
		
		//T_BASEFILE_TB.xml��������
		Document baseDocument = getParsedFile(filePath + "/T_BASEFILE_TB.xml");
		
		//baseFile�� FILE_ID tag�� node�� nodeList�� ����
		Map<String, NodeList> baseFileMap = getNodeLists(baseDocument, "FILE_ID");

		//T_BASEFILE_TB.xml�� {FILE_ID}���� ����
		for(int temp=0; temp<baseFileMap.get("FILE_ID").getLength(); temp++) {	
			
			String fileId = baseFileMap.get("FILE_ID").item(temp).getTextContent();
			
			//F_{FILE_ID}_TB.xml ��������
			Document fDocument = getParsedFile(filePath + "/F_" + fileId + "_TB.xml");			
			//fFile�� SIMILAR_RATE, P_ID, COMMENT tag�� nodeList�� Map�� ����
			Map<String, NodeList> fFileMap = getNodeLists(fDocument, "SIMILAR_RATE", "P_ID", "COMMENT");
			
			//P_{FILE_ID}_TB.xml ��������
			Document pDocument = getParsedFile(filePath + "/P_" + fileId + "_TB.xml");			
			//pFile�� P_ID, LICENSE_ID tag�� nodeList�� Map�� ����
			Map<String,NodeList> pFileMap = getNodeLists(pDocument, "P_ID", "LICENSE_ID");			
			
			changeNodeValue(fFileMap, pFileMap);
			
			//tFile�� ����
			File tFile = new File(filePath+"/T_"+fileId+"_TB.xml");			

			//�����fDcoument�� tFile�� ����
			saveXMLFile(fDocument, tFile);
		}//for�� ��	
	}
	
	/**
	 * fFile�� SIMILAR_RATE�� 100���� �������� 15���� ū ���
	 * fFile�� ������ P_ID�� ���� pFile�� LICENSE_ID��
	 * fFile�� COMMENT�� ������ ����
	 * @param fFileMap
	 * @param pFileMap
	 */
	private void changeNodeValue(Map<String,NodeList> fFileMap, Map<String,NodeList> pFileMap) {
		
		//F_{FILE_ID}_TB.xml�� SIMILAR_RATE���� ����
		for(int temp=0; temp<fFileMap.get("SIMILAR_RATE").getLength(); temp++) {
			
			String similarRate = fFileMap.get("SIMILAR_RATE").item(temp).getTextContent();
			//fFile�� SIMILAR_RATE�� 100���� �������� 15���� ũ�� & P_ID�� �����ϴ� ���
			if(Integer.parseInt(similarRate)/100>15 && !fFileMap.get("P_ID").item(temp).getTextContent().equals("")) {
				//�ش� fFile�� P_ID���� pIdFromF������ ����
				String pIdFromF = fFileMap.get("P_ID").item(temp).getTextContent();
				
				//pIdFromF���� ���� pFile�� P_ID�� LICENSE_ID�� ���� ����
				Node licenseIdNode = searchNodeList(pIdFromF, pFileMap.get("P_ID"), pFileMap.get("LICENSE_ID"));
				
				//fFile�� COMMENT�� ���� licenseId������ ����
				if(!licenseIdNode.getTextContent().equals("")) {
					/*getNodeLists(document, "COMMENT").get("COMMENT").item(temp).setTextContent("11");*/					
					fFileMap.get("COMMENT").item(temp).setTextContent(licenseIdNode.getTextContent());
				}
			}//if�� ��
		}//for�� ��
	}

	/**
	 * �ش� ������ ������ �Ľ��Ͽ� document�� return
	 * @param filePath
	 * @return document
	 */
	private Document getParsedFile(String filePath) {
		//�ش� ������ ������
		File file = new File(filePath);		
			//xmlFile�� �Ľ����ִ� ��ü�� ����
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
	 * �ش� document�� tagNames�� �´� nodeList�� return(XPath ���)
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
	 * �ش� targetNodeList���� searchkey�� ������ nodeValue�� ���� node�� �ٸ� tag(returnNodeList)�� node�� return  
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
	 * �ش� document�� XMLfile�� ����
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
