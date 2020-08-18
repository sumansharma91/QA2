package com.vsi.oms.utils;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class XMLHandler implements YIFCustomApi {
	public Properties localprop = null;
	XPathFactory factory = XPathFactory.newInstance();
	XPath xPath = factory.newXPath();

	public void setProperties(Properties prop) throws Exception {

		localprop = prop;
	}
	
	
	
	public Vector<String> sort(Properties p) {
		Vector<String> v = new Vector<String>();
		Enumeration<String> e = (Enumeration<String>) p.propertyNames();
		while (e.hasMoreElements()) {
			v.add((String) e.nextElement());
		}
		Collections.sort(v);
		return  v;
	}

	public NodeList getNodeList(Document doc,String xpath) throws XPathExpressionException{
		
		
		NodeList Nlist = (NodeList) xPath.evaluate(xpath, doc,
				XPathConstants.NODESET);
		return Nlist;
		
	}
	
	public Integer parse(String s,Properties p){
		
		return Integer.parseInt(p.getProperty(s));
	}

	public Document execute(YFSEnvironment env, Document doc) throws Exception {
	
		int AddElementcount = 0;
		int AddAttributecount = 0;
		int ReplaceAttributeCount = 0;
		int DeleteElemntCount=0;
		int DeleteAttributeCount=0;
		String ReplaceRootElement="";
		String AddNewRootElement="";
		if (localprop != null) {
		
		Vector<String> v=sort(localprop);
		Enumeration<String> e1 =v.elements();
			
			while (e1.hasMoreElements()) {
				String name = (String) e1.nextElement();
				if (name.equals("Elementcount")) 
				AddElementcount=parse("Elementcount",localprop);
				if (name.equals("AttributeCountReplace")) 
				ReplaceAttributeCount=parse("AttributeCountReplace",localprop);
				if (name.equals("AttributeCountAdd")) 
				AddAttributecount = parse("AttributeCountAdd",localprop);
				if(name.equals("DeleteElementCount"))
				DeleteElemntCount = parse("DeleteElementCount",localprop);
				if(name.equals("DeleteAttributeCount"))
				DeleteAttributeCount = parse("DeleteAttributeCount",localprop);
				if(name.equals("ReplaceRootElemnt"))
					ReplaceRootElement = localprop.getProperty("ReplaceRootElemnt");
				if(name.equals("AddNewRootElement"))
					AddNewRootElement = localprop.getProperty("AddNewRootElement");		
			}
		}
		
		if(AddNewRootElement!=""){
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document aDoc2  = dBuilder.newDocument();
			org.w3c.dom.Element root = aDoc2.createElement(AddNewRootElement);
			aDoc2.appendChild(root);
			doc=aDoc2;
			
		}
		
		org.w3c.dom.Document w3cDoc1 = doc;
		
		if (AddElementcount > 0) {
			
			for (int AE = 1; AE <=AddElementcount; AE++) {
				
				String AElement = localprop.getProperty("AddElement"+AE);
				String[] AEArray = AElement.split(",");
				String rootElement = AEArray[0];
				String ElementXpath = AEArray[1];
				org.dom4j.Document docAE = DocumentHelper.createDocument();
				Element c = (Element) DocumentHelper.makeElement(docAE,
						ElementXpath);
				org.w3c.dom.Document w3cDoc = new DOMWriter().write(docAE);
				getNodeList(w3cDoc1, rootElement);
				NodeList nll = getNodeList(w3cDoc1, rootElement);
				org.dom4j.Document document = null;
				for (int iy = 0; iy < nll.getLength(); iy++) {
					org.w3c.dom.Node ngg = nll.item(iy);
					org.w3c.dom.Node n = (org.w3c.dom.Node) w3cDoc
							.getDocumentElement();
					org.w3c.dom.Node x = w3cDoc1.importNode(
							(org.w3c.dom.Node) n, true);
					org.w3c.dom.Node z = ngg.appendChild(x);
				}
			}
		}

		if (AddAttributecount > 0) {

			for (int AA = 1; AA <= AddAttributecount; AA++) {
				String AAlement = localprop.getProperty("AddAttribute"+AA);
				String[] AAArray = AAlement.split(",");
				String ElementXpathAA = AAArray[0];
				String AttributeNameAA = AAArray[1];
				String AttributeValueAA = AAArray[2];
				NodeList nllAA =getNodeList(w3cDoc1, ElementXpathAA);
				for (int iaa = 0; iaa < nllAA.getLength(); iaa++) {
					org.w3c.dom.Node nggaa = nllAA.item(iaa);
					org.w3c.dom.Element e1 = (org.w3c.dom.Element) nggaa;
					e1.setAttribute(AttributeNameAA, AttributeValueAA);
				}
			}
		}


		if (ReplaceAttributeCount > 0) {
			for (int RA = 1; RA <=ReplaceAttributeCount; RA++) {
				String RAlement = localprop.getProperty("ReplaceAttribute"+RA);
				String[] RAArray = RAlement.split(",");
				String ElementXpathRR1 = RAArray[0];
				//System.out.println(ElementXpathRR1);
				String ElementXpathRR2 = RAArray[1];
				//System.out.println(ElementXpathRR2);
				NodeList nodeList = getNodeList(w3cDoc1, ElementXpathRR1);
				NodeList nodeList1 =getNodeList(w3cDoc1, ElementXpathRR2);
			
				if (nodeList.getLength() > 0
						&& nodeList.getLength() == nodeList1.getLength()) {
					for (int ira = 0; ira < nodeList1.getLength(); ira++) {
						org.w3c.dom.Node ngg = nodeList.item(ira);
						org.w3c.dom.Node ngg1 = nodeList1.item(ira);
						String s = ngg.getNodeValue();
						ngg1.setNodeValue(s);
					}
				} else {
					org.w3c.dom.Node ngg = nodeList.item(0);
					for (int iraa = 0; iraa < nodeList1.getLength(); iraa++) {
						org.w3c.dom.Node ngg1 = nodeList1.item(iraa);
						String s = ngg.getNodeValue();
						ngg1.setNodeValue(s);
					}
				}

			}

		}
		
		
		if(DeleteElemntCount>0){
			
			for (int DE = 1; DE <= DeleteElemntCount; DE++) {
				String DElement = localprop.getProperty("DeleteElement"+DE);
				NodeList nodeList = getNodeList(w3cDoc1, DElement);
				for (int de = 0; de < nodeList.getLength(); de++){
					
					org.w3c.dom.Node DelE = nodeList.item(de);
					DelE.getParentNode().removeChild(DelE);
				}
			
			
		}
		}
		
		if(DeleteAttributeCount>0){
			for (int DA = 1; DA <=DeleteAttributeCount; DA++) {
				String DAElement =localprop.getProperty("DeleteAttribute"+DA);
				String[] RAArray = DAElement.split(",");
				String ElementXpathDA1 = RAArray[0];
				String AttributeName = RAArray[1];
			NodeList DAList =getNodeList(w3cDoc1, ElementXpathDA1);
			for(int i=0;i<DAList.getLength();i++){
				
				org.w3c.dom.Node DelA = DAList.item(i);
				

				DelA.getAttributes().removeNamedItem(AttributeName);
				
			}
			
		}
		
		}
	
		if(ReplaceRootElement!=""){
			org.w3c.dom.Element element = w3cDoc1.getDocumentElement();
			org.w3c.dom.Element element2 = w3cDoc1.createElement(ReplaceRootElement);
			NamedNodeMap attrs = element.getAttributes();
			for (int i=0; i<attrs.getLength(); i++) {
				org.w3c.dom.Attr attr2 = (org.w3c.dom.Attr)w3cDoc1.importNode(attrs.item(i), true);
			    element2.getAttributes().setNamedItem(attr2);
			}
			while (element.hasChildNodes()) {
			    element2.appendChild(element.getFirstChild());
			}
			element.getParentNode().replaceChild(element2, element);

			
			
		}
	
		
		
		return w3cDoc1;
	}
}
