package com.vsi.scc.oms.pca.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xpath.internal.CachedXPathAPI;

public class VSIXmlUtils {
	

	
	/**
	 * <Description>
	 * This method is used to get the xpath element from the document.
	 * xpath can contain conditions also.
	 * 
	 * @param inXML
	 * @param XPath
	 * @return
	 * @throws XPathExpressionException
	 */
	public static Element getElementByXPath(final Document inXML,final String XPath) throws XPathExpressionException {
		
		XPath myXpath = XPathFactory.newInstance().newXPath();
	    XPathExpression xpath = myXpath.compile(XPath);
		return (Element) (xpath.evaluate(inXML, XPathConstants.NODE));
	}

	public static void copyAttributes(Element src, Element dest) {
	      NamedNodeMap attributes = src.getAttributes();
	      for (int i = 0; i < attributes.getLength(); i++) {
	          Attr node = (Attr) attributes.item(i);
	          dest.setAttributeNS(node.getNamespaceURI(), node.getName(), node.getValue());
	      }
	  }
	
	public static void removeAttributes(Element src) {
	      NamedNodeMap attributes = src.getAttributes();
	      
	      for (int i = 0; i < attributes.getLength(); i++) {
	          Attr node = (Attr) attributes.item(i);
	          src.removeAttribute(node.getName()); 
	          i--;
	      }
	  }

	/**
	* Creates a child element under the parent element with given child name. Returns the newly created child element.
	* This method returns null if either parent is null or child name is void.
	*/
    public static Element createChild(Element parentEle, String childName) {
		Element child = null;
		if (parentEle != null && !isVoid(childName)) {
			child = parentEle.getOwnerDocument().createElement(childName);
			parentEle.appendChild(child);
		}
		return child;
	}
    
    /**
     * Utility method to check if a given string is null or empty (length is zero
     * after trim call).
     * <p></p>
     * @param inStr String for void check.
     * @return true if the given string is void.
     */
    public static boolean isVoid(String inStr) {
    	return (inStr == null) ? true : (inStr.trim().length() == 0) ? true : false;
    }

    public static String getElementXMLString(Element element)
	{
		return serialize(element);
	}
    
    public static String serialize(Node node)
	{
		return serialize(node, "iso-8859-1", true);
	}
    
    public static String serialize(Node node, String encoding, boolean indenting)
	{
		OutputFormat outFmt = null;
		StringWriter strWriter = null;
		XMLSerializer xmlSerializer = null;
		String retVal = null;
		try
		{
			outFmt = new OutputFormat("xml", encoding, indenting);
			outFmt.setOmitXMLDeclaration(true);
			strWriter = new StringWriter();
			xmlSerializer = new XMLSerializer(strWriter, outFmt);
			short ntype = node.getNodeType();
			switch(ntype)
			{
			case 11: // '\013'
				xmlSerializer.serialize((DocumentFragment)node);
				break;

			case 9: // '\t'
				xmlSerializer.serialize((Document)node);
				break;

			case 1: // '\001'
				xmlSerializer.serialize((Element)node);
				break;

			default:
				throw new IOException("Can serialize only Document, DocumentFragment and Element type nodes");
			}
			retVal = strWriter.toString();
		}
		catch(IOException e)
		{
			retVal = e.getMessage();
		}
		finally
		{
			try
			{
				strWriter.close();
			}
			catch(IOException ie)
			{
				//oBaseLog.debug("Use the base log functionality");
			}
		}
		return retVal;
	}
    /**
	 * Method to create XPath based on a condition
	 * @param String strXPath1
	 * 		  String strConditionAttribute1
	 * 		  String strConditionValue1
	 * @return String - XPath formed
	 */
	public static String formXPATHWithOneCondition(String strXPath1, String strConditionAttribute1,
			String strConditionValue1) {

		StringBuffer sbFinalXPath = new StringBuffer(strXPath1);
		sbFinalXPath.append("[@").append(strConditionAttribute1).append("='").append(strConditionValue1).append("']");
		
		return sbFinalXPath.toString();
	}
	public static NodeList getNodeListByXpath(Document inXML, String XPath)
	throws ParserConfigurationException, TransformerException
	{
		NodeList nodeList = null;
		CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
		nodeList = aCachedXPathAPI.selectNodeList(inXML, XPath);
		return nodeList;
	}
	public static Document convertStringToDocument(String xmlStr) {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder builder;  
	        try 
	        {  
	            builder = factory.newDocumentBuilder();  
	            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
	            return doc;
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } 
	        return null;
	    }
	 
}
