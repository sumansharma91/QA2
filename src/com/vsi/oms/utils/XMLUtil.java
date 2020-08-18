package com.vsi.oms.utils;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
//import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;

/**
 * 
 * @author Perficient
 * 
 */

public class XMLUtil {
	
	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder db;
	
	public static Document getDocumentFromString(String stringXML){
		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(stringXML);
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String formXPATHWithOneCondition(String strXPath1, String strConditionAttribute1,
			String strConditionValue1) {

		StringBuffer sbFinalXPath = new StringBuffer(strXPath1);
		sbFinalXPath.append("[@").append(strConditionAttribute1).append("='").append(strConditionValue1).append("']");
		
		return sbFinalXPath.toString();
	}
	
	
	/**
     *      Create a Document object.
     *
     *      @param none
     *      @throws ParserConfigurationException
     */
	 public static Document newDocument()
     throws ParserConfigurationException {
	     return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	 }

	
	/**
     *      Create a Document object with input as the name of document element.
     *
     *      @param docElementTag: the document element name.
     *      @throws ParserConfigurationException
     */
    public static Document createDocument(String docElementTag)
    throws ParserConfigurationException {

            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbdr = fac.newDocumentBuilder();
        Document doc = dbdr.newDocument();
        Element ele = doc.createElement(docElementTag);
        doc.appendChild(ele);
        return doc;
    }

	public static Document getDocumentFromFile(File fileXML){
		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(fileXML);
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static Document getXmlFromFile(String inputFileName) throws Exception {
		dbf = DocumentBuilderFactory.newInstance();
		try{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document resultDocument = db.parse(new File(inputFileName));
			return resultDocument;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
     * Evaluates the given Xpath and returns the corresponding value as a String.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return String Value of the XPath Execution.
     * @throws Exception exception
     */
    public static String  getString(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        String value = null;
        try {
            XObject xobj = XPathAPI.eval(node, xpath);
            value = xobj.toString();
        } catch (TransformerException e) {
            throw e;
        }
        return value;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return node if found
     * @throws Exception exception
     */
    public static Node getNode(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        Node ret = null;
        try {
            ret = XPathAPI.selectSingleNode(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node list.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeList getNodeList(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        NodeList ret = null;
        try {
            ret = XPathAPI.selectNodeList(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node iterator.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeIterator getNodeIterator(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        NodeIterator ret = null;
        try {
            ret = XPathAPI.selectNodeIterator(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }
    
    /**
     * Returns a formatted XML string for the Node, using encoding 'iso-8859-1'.
     *
     * @param node   a valid document object for which XML output in String form is required.
     *
     * @return the formatted XML string.
     */
    
    public static String serialize( Node node ) {
        return serialize(node, "iso-8859-1", true);
    }
    
    /**
     *	Return a XML string for a Node, with specified encoding and indenting flag.
     *	<p>
     *	<b>Note:</b> only serialize DOCUMENT_NODE, ELEMENT_NODE, and DOCUMENT_FRAGMENT_NODE
     *
     *	@param node the input node.
     *	@param encoding such as "UTF-8", "iso-8859-1"
     *	@param indenting indenting output or not.
     *
     *	@return the XML string
     */
    public static String serialize(Node node, String encoding, boolean indenting) {
        OutputFormat outFmt = null;
        StringWriter strWriter = null;
        XMLSerializer xmlSerializer = null;
        String retVal = null;
        
        try{
            outFmt = new OutputFormat("xml", encoding, indenting);
            outFmt.setOmitXMLDeclaration(true);
            
            strWriter = new StringWriter();
            
            xmlSerializer = new XMLSerializer(strWriter, outFmt);
            
            short ntype = node.getNodeType();
            
            switch(ntype) {
                case Node.DOCUMENT_FRAGMENT_NODE: xmlSerializer.serialize((DocumentFragment)node); break;
                case Node.DOCUMENT_NODE: xmlSerializer.serialize((Document)node); break;
                case Node.ELEMENT_NODE: xmlSerializer.serialize((Element)node); break;
                default: throw new IOException("Can serialize only Document, DocumentFragment and Element type nodes");
            }
            
            retVal = strWriter.toString();
        } catch (IOException e) {
            retVal = e.getMessage();
        } finally{
            try {
                strWriter.close();
            } catch (IOException ie) {}
        }
        
        return retVal;
    }
    
    /**
     *	Return a decendent of first parameter, that is the first one to match the XPath specified in
     *	the second parameter.
     *
     *	@param ele The element to work on.
     *	@param tagName format like "CHILD/GRANDCHILD/GRANDGRANDCHILD"
     *
     *	@return	the first element that matched, null if nothing matches.
     */
    public static Element getFirstElementByName(Element ele, String tagName) {
        StringTokenizer st = new StringTokenizer(tagName, "/");
        Element curr = ele;
        Node node;
        String tag;
        while (st.hasMoreTokens()) {
            tag = st.nextToken();
            node = curr.getFirstChild();
            while (node != null) {
                if (node.getNodeType() == Node.ELEMENT_NODE && tag.equals(node.getNodeName())) {
                    break;
                }
                node = node.getNextSibling();
            }
            
            if (node != null)
                curr = (Element)node;
            else
                return null;
        }
        
        return curr;
    }
    
    /**
     *  For an Element node, return its Text node's value; otherwise return the node's value.
     * @param node
     * @return
     */
    public static String getNodeValue(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.TEXT_NODE)
                    return child.getNodeValue();
                child = child.getNextSibling();
            }
            return null;
        } else
            return node.getNodeValue();
    }
    
    /**
     *	For an Element node, set its Text node's value (create one if it does not have);
     *	otherwise set the node's value.
     * @param node
     * @param val
     */
    public static void setNodeValue(Node node, String val) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.TEXT_NODE)
                    break;
                child = child.getNextSibling();
            }
            if (child == null) {
                child = node.getOwnerDocument().createTextNode(val);
                node.appendChild(child);
            } else
                child.setNodeValue(val);
        } else
            node.setNodeValue(val);
    }
    
    /**
     * Creates an element with the supplied name and attributevalues.
     * @param doc XML Document on which to create the element
     * @param elementName the name of the node element
     * @param hashAttributes usually a Hashtable containing name/value pairs for the attributes of the element.
     */
    public static Element createElement( Document doc, String elementName, Object hashAttributes ) {
        return createElement( doc, elementName, hashAttributes, false );
    }
    
    /**
     * Creates an node text node element with the text node value supplied
     * @param doc the XML document on which this text node element has to be created.
     * @param elementName the name of the element to be created
     * @param textStr should be a String for the value of the text node
     */
    public static Element createTextElement( Document doc, String elementName, Object textStr ) {
        return createElement( doc, elementName, textStr, true );
    }
    


    
    /**
     * Create an element with either attributes or text node.
     * @param doc the XML document on which the node has to be created
     * @param elementName the name of the element to be created
     * @param hashAttributes the value for the text node or the attributes for the node element
     * @param textNodeFlag a flag signifying whether te node to be created is the text node
     */
    public static Element createElement( Document doc, String elementName, Object hashAttributes, boolean textNodeFlag ) {
        Element elem = doc.createElement(elementName);
        if (hashAttributes != null) {
            if (hashAttributes instanceof String) {
                if (textNodeFlag ) {
                    elem.appendChild( doc.createTextNode( (String)hashAttributes ) );
                }
            } else if(hashAttributes instanceof Hashtable) {
                @SuppressWarnings("rawtypes")
				Enumeration e = ((Hashtable)hashAttributes).keys();
                while ( e.hasMoreElements() ) {
                    String attributeName =  (String)e.nextElement();
                    @SuppressWarnings("rawtypes")
					String attributeValue = (String)((Hashtable)hashAttributes).get( attributeName );
                    elem.setAttribute( attributeName, attributeValue );
                }
            }
        }
        return elem;
    }
    
    /**
     * This method is for adding child Nodes to parent node element, the child element has to be created first.
     * @param doc
     * @param      parentElement Parent Element under which the new Element should be present
     * @param      elementName   Name of the element to be created
     * @param      value         Can be either a String ,just the element value if it is a single attribute
     * @return
     */
    public static Element appendChild( Document doc, Element parentElement, String elementName, Object value ) {
        Element childElement = createElement( doc, elementName, value);
        parentElement.appendChild(childElement);
        return childElement;
    }
    
    public static void appendChild( Element parentElement, Element childElement ) {
        parentElement.appendChild(childElement);
    }
    
    /**
     * This method is for setting the attribute of an element
     * @param      objElement     Element where this attribute should be set
     * @param      attributeName  Name of the attribute
     * @param      attributeValue Value of the attribute
     */
    public static void setAttribute(Element objElement, String attributeName, String attributeValue) {
        objElement.setAttribute(attributeName,attributeValue);
    }
    
    /**
     * This method is for removing an attribute from an Element.
     * @param      objElement     Element from where the attribute should be removed.
     * @param      attributeName  Name of the attribute
     */
    public static void removeAttribute( Element objElement, String attributeName ) {
        objElement.removeAttribute(attributeName);
    }
    
    /**
     * This method is for removing the child element of an element
     * @param      parentElement     Element from where the child element should be removed.
     * @param      childElement  Child Element which needs to be removed from the parent
     */
    public static void removeChild( Element parentElement, Element childElement ) {
        parentElement.removeChild(childElement);
    }
    
    /**
     * Method to create a text mode for an element
     * @param doc the XML document on which the node has to be created
     * @param parentElement the element for which the text node has to be created.
     * @param elementValue the value for the text node.
     */
    public static void createTextNode( Document doc, Element parentElement, String elementValue ) {
        parentElement.appendChild( doc.createTextNode( elementValue ) );
    }
    
    /**
     * This method takes Document as input and returns the XML String.
     * @param document   a valid document object for which XML output in String form is required.
     */
    public static String getXMLString( Document document ) {
    	if(YFCObject.isNull(document))
    		return "Null";
    	else
        return serialize(document);
    }
    
    /**
     *
     * This method takes a document Element as input and returns the XML String.
     * @param element   a valid element object for which XML output in String form is required.
     * @return XML String of the given element
     */
    
    public static String getElementXMLString( Element element ) {
        return serialize( element );
    }
    
    /**
     *	Convert the Document to String and write to a file.
     * @param document
     * @param fileName
     * @throws IOException
     */
    public static void flushToAFile( Document document, String fileName )
    throws IOException {
        if( document != null ) {
            OutputFormat oFmt = new OutputFormat( document,"iso-8859-1",true );
            oFmt.setPreserveSpace(true);
            XMLSerializer xmlOP = new XMLSerializer(oFmt);
            FileWriter out = new FileWriter(new File(fileName));
            xmlOP.setOutputCharStream(out);
            xmlOP.serialize(document);
            out.close();
        }
    }
    
    
    /**
     *	Serialize a Document to String and output to a java.io.Writer.
     * @param document
     * @param writer
     * @throws IOException
     */
    public static void flushToAFile( Document document, Writer writer ) throws IOException {
        if( document != null ) {
            OutputFormat oFmt = new OutputFormat( document,"iso-8859-1",true );
            oFmt.setPreserveSpace(true);
            XMLSerializer xmlOP = new XMLSerializer(oFmt);
            xmlOP.setOutputCharStream(writer);
            xmlOP.serialize(document);
            writer.close();
        }
    }
    

    
    /**
     *	Gets the count of sub nodes under one node matching the sub node name
     *	@param parentElement Element under which sub nodes reside
     *   @param subElementName Name of the sub node to look for in the parent node
     */
    public static int getElementsCountByTagName( Element parentElement, String subElementName ) {
        NodeList nodeList = parentElement.getElementsByTagName( subElementName );
        if( nodeList != null )
            return nodeList.getLength();
        else
            return 0;
    }
    
    /**
     *	Augment a destination Element with a source Element. Including the source Element's Attributes and child nodes.
     *	<p>
     *	The behavior is a little inconsistent: attributes in destElem are replaced, but child nodes are added, i.e. no
     *	equality check of child nodes. So the meaningful way to use it is to start with an empty destination Element.
     *	<br>
     *	It's better be replaced by a method with signature: <i>Element copyElement(Document destDoc, Element srcElem)</i>
     *
     *	@param destDoc the Document for destination Element, must be the same as destElem.getDocument().
     *	@param srcElem the source Element.
     *	@param destElem the destination Element.
     */
    
    
    public static void copyElement( Document destDoc, Element srcElem, Element destElem ) {
        NamedNodeMap attrMap = srcElem.getAttributes();
        int attrLength = attrMap.getLength();
        for( int count=0; count<attrLength; count++ ) {
            Node attr = attrMap.item(count);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            destElem.setAttribute( attrName, attrValue );
        }
        
        if( srcElem.hasChildNodes() ) {
            NodeList childList = srcElem.getChildNodes();
            int numOfChildren = childList.getLength();
            for( int cnt=0; cnt<numOfChildren; cnt++ ) {
                Object childSrcNode = childList.item(cnt);
                if( childSrcNode instanceof CharacterData ) {
                    if( childSrcNode instanceof Text ) {
                        String data = ((CharacterData)(childSrcNode)).getData();
                        Node childDestNode = destDoc.createTextNode( data );
                        destElem.appendChild( childDestNode );
                    } else if( childSrcNode instanceof Comment ) {
                        String data = ((CharacterData)(childSrcNode)).getData();
                        Node childDestNode = destDoc.createComment( data );
                        destElem.appendChild( childDestNode );
                    }
                } else {
                    Element childSrcElem = (Element)(childSrcNode);
                    Element childDestElem = appendChild( destDoc, destElem, childSrcElem.getNodeName(), null );
                    copyElement( destDoc, childSrcElem, childDestElem );
                }
            }
        }
    }

	/**
	* Imports an element including the subtree from another document under the parent element. 
	* Returns the newly created child element. This method returns null if either parent or element 
	* to be imported is null.
	*/
	public static Element importElement(Element parentEle, Element ele2beImported) {
		Element child = null;
		if (parentEle != null && ele2beImported != null) {
			child = (Element)parentEle.getOwnerDocument().importNode(ele2beImported, true);
			parentEle.appendChild(child);
		}
		return child;
	}

    /**
    * Imports an element including the subtree from another document under the parent element. 
    * Returns the newly created child element. This method returns null if either parentDoc or element 
    * to be imported is null.
    */
    public static Element importElement(Document parentDoc, Element ele2beImported) {
        Element child = null;
        if (parentDoc != null && ele2beImported != null) {
            child = (Element)parentDoc.importNode(ele2beImported, true);
            parentDoc.appendChild(child);
        }
        return child;
    }
    /**
     * Imports an element based on the flag including the subtree(if flag is true) from another document under the parent element. 
     * Returns the newly created child element. This method returns null if either parentDoc or element 
     * to be imported is null.
     */
    public static Element importElement(Document parentDoc, Element ele2beImported, boolean flag) {
        Element child = null;
        if (parentDoc != null && ele2beImported != null) {
            child = (Element)parentDoc.importNode(ele2beImported, flag);
            parentDoc.appendChild(child);
        }
        return child;
    }

	/**
	* Copy all the attributes from the Xml element fromEle to the Xml element toEle. All extra atributes already existing
	* in toEle are left as is. If either of the params is null it does nothing.
	*/
	public static void setAttributes(Element fromEle, Element toEle) {
		setAttributes(fromEle, toEle, false);
	}

	/**
	* Copy all the attributes from the Xml element fromEle to the Xml element toEle. Depending on the value of clearToEle, existing
	* attributes of toEle are removed before copying to avoid having extra attributes.
	* If either fromEle or toEle is null it does nothing.
	*/
	public static void setAttributes(Element fromEle, Element toEle, boolean clearToEle) {
		if (fromEle != null && toEle != null) {
			if (clearToEle) {
				removeAttributes(toEle);
			}
		
			NamedNodeMap map = fromEle.getAttributes();
			for (int i=0; i<map.getLength(); i++) {
				toEle.setAttribute(map.item(i).getNodeName(), map.item(i).getNodeValue());
			}
		}
	}

	private static void removeAttributes(Element element) {
	    /* Remove all the attributes of an element. Just to be safe, make a copy of
	     * attr names and then delete one by one.
	     */
	    NamedNodeMap attrs = element.getAttributes();
	    String[] names = new String[attrs.getLength()];
	    for (int i=0; i<names.length; i++) {
	        names[i] = attrs.item(i).getNodeName();
	    }
	    for (int i=0; i<names.length; i++) {
	        attrs.removeNamedItem(names[i]);
	    }
	}
	
	
    /**
    *
    * Returns the clone of an XML Document.
    * @param doc Input document to be cloned.
    * @throws java.lang.Exception If uable to clone document.
    * @return Clone of the document.
    */
   public static Document cloneDocument(Document doc) throws Exception {
       return YFCDocument.parse(XMLUtil.getXMLString(doc)).getDocument();
   }
   
   /**
    * Returns the clone of an XML Document.
    * @param doc Input document to be cloned.
    * @throws java.lang.Exception If uable to clone document.
    * @return Clone of the document.
    */
   public static YFCDocument cloneDocument(YFCDocument doc) throws Exception {
       return YFCDocument.parse(doc.getString());
   }

   /**
    * Returns the clone of an XML Document.
    * @param doc Input element for which docment has to be retrned.
    * @throws java.lang.Exception If unable to parse.
    * @return document.
    */
    public static Document getDocumentForElement(Element inElement)
        throws Exception
    {
        DocumentBuilder dbdr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = dbdr.newDocument();
        Element docElement = doc.createElement(inElement.getNodeName());
        doc.appendChild(docElement);
        copyElement(doc, inElement, docElement);
        return doc;
    }
    
    /** Gets the Parser from the input XML **/
	/** Gets the attribute of a node **/
	public static String getAttribute(Object element, String attributeName)
	{
		if(element != null)
		{
			String attributeValue = ((Element)element).getAttribute(attributeName);
			attributeValue = attributeValue != null ? attributeValue.trim() : attributeValue;
			return attributeValue;
		} else
		{
			return null;
		}
	}
	
	/**
	 * Constructs XML String from given element object
	 *
	 * @param inputElement
	 *            Input element
	 * @return XML String
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             incase of any other exception
	 */
	public static String getElementString(Element inputElement)
	throws IllegalArgumentException, Exception
	{
		//Validate input element
		if (inputElement == null)
		{
			throw new IllegalArgumentException(
					"Input element cannot be null in "
					+ "XmlUtils.getElementString method");
		}

		//Import element content and construct Document
		Document document = getDocument(inputElement, true);

		//Convert document as element string
		String xmlString = getXMLString(document);

		//Remove Processing Instruction from xml string if exists
		xmlString = removeProcessingInstruction(xmlString);

		//Return result XML string
		return xmlString;
	}

	/**
	 * Create a new document object with input element as the root.
	 *
	 * @param inputElement
	 *            Input Element object
	 * @param deep
	 *            Include child nodes of this element true/false
	 * @return XML Document object
	 * @throws IllegalArgumentException
	 *             if input is invalid
	 * @throws Exception
	 *             incase of any other exception
	 */
	public static Document getDocument(Element inputElement, boolean deep)
	throws IllegalArgumentException, Exception
	{
		//Validate input element
		if (inputElement == null)
		{
			throw new IllegalArgumentException(
					"Input element cannot be null in "
					+ "XmlUtils.getDocument method");
		}

		//Create a new document
		Document outputDocument = getDocument();

		//Import data from input element and
		//set as root element for output document
		outputDocument.appendChild(
				outputDocument.importNode(inputElement, deep));

		//return output document
		return outputDocument;
	}
	/**
	 * Creates and empty Document object
	 *
	 * @throws Exception
	 *             incase of any exception
	 */
	public static Document getDocument() throws Exception
	{
		//Create a new Document Bilder Factory instance
		//SCR# 2392
		DocumentBuilderFactory documentBuilderFactory =
			new DocumentBuilderFactoryImpl();

		//Create new document builder
		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		//Create and return document object
		return documentBuilder.newDocument();
	}
	
	/**
	 * Removes processing instruction from input XML String. Requirement is
	 * that input XML string should be a valid XML.
	 *
	 * @param xmlString
	 *            XML String thay may contain processing instruction
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @return XML String
	 */
	public static String removeProcessingInstruction(String xmlString)
	throws IllegalArgumentException
	{
		//Validate input XML string
		if (xmlString == null)
		{
			throw new IllegalArgumentException(
					"Input XML string cannot be null in "
					+ "XmlUtils.removeProcessingInstruction method");
		}

		//Is input contains processing instruction
		if ((xmlString.toLowerCase().trim().startsWith("<?xml")))
		{
			//Get the ending index of processing instruction
			int processInstructionEndIndex = xmlString.indexOf("?>");

			//If processing instruction ending found,
			if (processInstructionEndIndex != -1)
			{
				//Remove processing instruction
				xmlString = xmlString.substring(processInstructionEndIndex + 2);
			}
		}

		//Return XML string after update
		return xmlString;
	}
	
	
	public static NodeList getNodeListByXpath(Document inXML, String XPath)
			throws ParserConfigurationException, TransformerException
			{
				NodeList nodeList = null;
				CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
				nodeList = aCachedXPathAPI.selectNodeList(inXML, XPath);
				return nodeList;
			}
   
	public static Element getElementByXPath(Document inXML, String XPath)
			throws TransformerException
			{
				Node node = null;
				Element eleNode = null;
				Element eleRoot = null;
				eleRoot = inXML.getDocumentElement();
				CachedXPathAPI oCachedXPathAPI = new CachedXPathAPI();
				node = oCachedXPathAPI.selectSingleNode(inXML, XPath);
				eleNode = (Element)node;
				return eleNode;
			}
	public static Document getDocumentFromElement(Element element)
			throws ParserConfigurationException, FactoryConfigurationError
			{
				Document doc = null;
				Node nodeImp = null;
				DocumentBuilder dbdr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				doc = dbdr.newDocument();
				nodeImp = doc.importNode(element, true);
				doc.appendChild(nodeImp);
				return doc;
			}
	
	/**
	 * 
	 * @param eleInput
	 * @param XPath
	 * @return
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws FactoryConfigurationError
	 */
	public static List<Element> getElementListByXpath(Element eleInput,
			String XPath) throws ParserConfigurationException,
			TransformerException, FactoryConfigurationError {
		return getElementListByXpath(getDocumentFromElement(eleInput), XPath);
	}

	/**
	 * . getting element list
	 */
	public static List<Element> getElementListByXpath(Document inXML,
			String XPath) throws ParserConfigurationException,
			TransformerException {
		NodeList nodeList = null;
		List elementList = new ArrayList();
		CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
		nodeList = aCachedXPathAPI.selectNodeList(inXML, XPath);
		int iNodeLength = nodeList.getLength();
		for (int iCount = 0; iCount < iNodeLength; iCount++) {
			Node node = nodeList.item(iCount);
			elementList.add(node);
		}
		return elementList;
	}

	/**
	 * 
	 * @param startElement
	 * @param elemName
	 * @return Method to return the element list
	 */

	public static List getElementsByTagName(Element startElement,
			String elemName) {
		NodeList nodeList = startElement.getElementsByTagName(elemName);
		List elemList = new ArrayList();
		for (int count = 0; count < nodeList.getLength(); count++)
			elemList.add(nodeList.item(count));

		return elemList;
	}
	
	/**
	 * Method to return attribute value by XPath
	 * 
	 * @see Need to call like --> XMLUtil.getAttributeFromXPath(Document
	 *      Name, XPath/@AttributeName")
	 * @param inXML
	 * @param xpathExpr
	 * @return Attribute Value
	 * @throws Exception
	 */
	public static String getAttributeFromXPath(Object inNode, String xpathExpr) {
		Node node = null;
		List elementList = new ArrayList();
		CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
		try {
			node = aCachedXPathAPI.selectSingleNode((Node) inNode, xpathExpr);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		if (node == null)
			return null;
		else
			return node.getNodeValue();
	}
	
	public static Document createDocumentFromElement(Element element) throws Exception {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();
		Document doc = dbdr.newDocument();
		Element inElement = (Element) doc.importNode(element, true);
		doc.appendChild(inElement);
		return doc;
	}

}
