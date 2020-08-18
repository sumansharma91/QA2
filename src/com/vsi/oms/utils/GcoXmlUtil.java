package com.vsi.oms.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import com.yantra.yfc.dom.YFCDocument;

public class GcoXmlUtil implements VSIConstants {

	/**
	 * Method for Getting the root element.
	 */
	public static Element getRootElement(Document doc) {
		return doc.getDocumentElement();
	}

	/**
	 * Creating a Document object with input as the name of document element
	 * 
	 * @param docElementTag
	 *            : the document element name
	 */
	public static Document createDocument(String docElementTag)
			throws ParserConfigurationException {
		Document doc = newDocument();
		Element ele = doc.createElement(docElementTag);
		doc.appendChild(ele);
		return doc;
	}

	/**
	 * Create a new blank XML Document
	 */
	public static Document newDocument() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.newDocument();
	}

	/**
	 * This method takes Document as input and returns the XML String.
	 * 
	 * @param document
	 *            a valid document object for which XML output in String form is
	 *            required.
	 */

	public static String getXMLString(Document document) {
		return serialize(document);
	}

	/**
	 * Returns a formatted XML string for the Node, using encoding 'iso-8859-1'.
	 * 
	 * @param Node
	 *            a valid document object for which XML output in String form is
	 *            required.
	 * 
	 * @return the formatted XML string.
	 */
	public static String serialize(Node node) {
		return serialize(node, "iso-8859-1", true);
	}

	/**
	 * Return a XML string for a Node, with specified encoding and indenting
	 * flag.
	 * <p>
	 * <b>Note:</b> only serialize DOCUMENT_NODE, ELEMENT_NODE, and
	 * DOCUMENT_FRAGMENT_NODE
	 * 
	 * @param node
	 *            the input node.
	 * @param encoding
	 *            such as "UTF-8", "iso-8859-1"
	 * @param indenting
	 *            indenting output or not.
	 * 
	 * @return the XML string
	 */

	public static String serialize(Node node, String encoding, boolean indenting) {
		OutputFormat outFmt = null;
		StringWriter strWriter = null;
		XMLSerializer xmlSerializer = null;
		String retVal = null;

		try {
			outFmt = new OutputFormat("xml", encoding, indenting);
			outFmt.setOmitXMLDeclaration(true);

			strWriter = new StringWriter();

			xmlSerializer = new XMLSerializer(strWriter, outFmt);

			int ntype = node.getNodeType();

			switch (ntype) {
			case Node.DOCUMENT_FRAGMENT_NODE:
				xmlSerializer.serialize((DocumentFragment) node);
				break;
			case Node.DOCUMENT_NODE:
				xmlSerializer.serialize((Document) node);
				break;
			case Node.ELEMENT_NODE:
				xmlSerializer.serialize((Element) node);
				break;
			default:
				throw new IOException(
						"Can serialize only Document, DocumentFragment and Element type nodes");
			}

			retVal = strWriter.toString();
		} catch (IOException e) {
			retVal = e.getMessage();
		} finally {
			try {
				strWriter.close();
			} catch (IOException ie) {
			}
		}

		return retVal;
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
	 * Parse an XML string or a file, to return the Document.
	 * 
	 * @param inXML
	 *            if starts with '&lt;', it is a XML string; otherwise it should
	 *            be an XML file name
	 * 
	 * @return the Document object generated
	 */
	public static Document getDocument(String inXML)
			throws ParserConfigurationException, SAXException, IOException {
		String currentXML = null;
		if (inXML != null && inXML.length() > 0) {
			currentXML = inXML.trim();
			if (currentXML.startsWith("<")) {
				StringReader strReader = new StringReader(currentXML);
				InputSource iSource = new InputSource(strReader);
				return getDocument(iSource);
			}
			// It's a file
			FileReader inFileReader = new FileReader(currentXML);
			Document retVal = null;
			try {
				InputSource iSource = new InputSource(inFileReader);
				retVal = getDocument(iSource);
			} finally {
				inFileReader.close();
			}
			return retVal;
		} else {
			return null;
		}
	}

	/**
	 * Generate a Document object according to InputSource object
	 */
	public static Document getDocument(InputSource inSource)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder dbdr = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		return dbdr.parse(inSource);
	}

	/**
	 * This method is for setting the attribute of an element
	 * 
	 * @param objElement
	 *            Element where this attribute should be set
	 * @param attributeName
	 *            Name of the attribute
	 * @param attributeValue
	 *            Value of the attribute
	 */
	public static void setAttribute(Element objElement, String attributeName,
			String attributeValue) {
		objElement.setAttribute(attributeName, attributeValue);
	}

	/**
	 * Constructs XML String from given element object
	 * 
	 * @param inputElement
	 *            Input element
	 * @return XML String
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws ParserConfigurationException
	 * @throws Exception
	 *             incase of any other exception
	 */
	public static String getElementString(Element inputElement)
			throws IllegalArgumentException, ParserConfigurationException {
		// Validate input element
		if (inputElement == null) {
			throw new IllegalArgumentException(
					"Input element cannot be null in "
							+ "XmlUtils.getElementString method");
		}

		// Import element content and construct Document
		Document document = getDocument(inputElement, true);

		// Convert document as element string
		String xmlString = getXMLString(document);

		// Remove Processing Instruction from xml string if exists
		xmlString = removeProcessingInstruction(xmlString);

		// Return result XML string
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
	 * @throws ParserConfigurationException
	 * @throws Exception
	 *             incase of any other exception
	 */
	public static Document getDocument(Element inputElement, boolean deep)
			throws IllegalArgumentException, ParserConfigurationException {
		// Validate input element
		if (inputElement == null) {
			throw new IllegalArgumentException(
					"Input element cannot be null in XmlUtils.getDocument method");
		}

		// Create a new document
		Document outputDocument = getDocument();

		// Import data from input element and
		// set as root element for output document
		outputDocument.appendChild(outputDocument
				.importNode(inputElement, deep));

		// return output document
		return outputDocument;
	}

	/**
	 * Removes processing instruction from input XML String. Requirement is that
	 * input XML string should be a valid XML.
	 * 
	 * @param xmlString
	 *            XML String thay may contain processing instruction
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @return XML String
	 */
	public static String removeProcessingInstruction(String sDocInp)
			throws IllegalArgumentException {

		String sDocInput = sDocInp;
		// Validate input XML string
		if (sDocInput == null) {
			throw new IllegalArgumentException(
					"Input XML string cannot be null in "
							+ "XmlUtils.removeProcessingInstruction method");
		}

		// Is input contains processing instruction
		if ((sDocInput.toLowerCase().trim().startsWith("<?xml"))) {
			// Get the ending index of processing instruction
			int processInstructionEndIndex = sDocInput.indexOf("?>");

			// If processing instruction ending found,
			if (processInstructionEndIndex != -1) {
				// Remove processing instruction
				sDocInput = sDocInput.substring(processInstructionEndIndex + 2);
			}
		}

		// Return XML string after update
		return sDocInput;
	}

	/**
	 * Creates and empty Document object
	 * 
	 * @throws ParserConfigurationException
	 * 
	 * @throws Exception
	 *             incase of any exception
	 */
	public static Document getDocument() throws ParserConfigurationException {
		// Create a new Document Bilder Factory instance
		// SCR# 2392
		DocumentBuilderFactory documentBuilderFactory = new DocumentBuilderFactoryImpl();

		// Create new document builder
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();

		// Create and return document object
		return documentBuilder.newDocument();
	}

	/** Gets the Parser from the input XML **/
	/** Gets the attribute of a node **/
	public static String getAttribute(Object element, String attributeName) {
		if (element != null) {
			String attributeValue = ((Element) element)
					.getAttribute(attributeName);
			attributeValue = attributeValue != null ? attributeValue.trim()
					: attributeValue;
			return attributeValue;
		} else {
			return null;
		}
	}

	/**
	 * Generate a Document object according to InputSource object
	 */
	public static Document getDocument(InputStream inStream)
			throws ParserConfigurationException, SAXException, IOException {
		Document retDoc = getDocument(new InputSource(new InputStreamReader(
				inStream)));
		inStream.close();
		return retDoc;
	}

	/**
	 * Getting element as List<Element>
	 * 
	 * @return List<Element> object
	 */

	public static List<Element> getElementsAsListObjectByXpath(Document inXML,
			String XPath) throws ParserConfigurationException,
			TransformerException {
		NodeList nodeList = null;
		List elementList = new ArrayList<Element>();
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
	 * Getting document from element.
	 */
	public static Document getDocumentFromElement(Element element)
			throws ParserConfigurationException, FactoryConfigurationError {
		Document doc = null;
		Node nodeImp = null;
		DocumentBuilder dbdr = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		doc = dbdr.newDocument();
		nodeImp = doc.importNode(element, true);
		doc.appendChild(nodeImp);
		return doc;
	}

	/**
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 *             For getting copy of any Doc
	 */
	public static Document cloneDocument(Document doc)
			throws java.lang.Exception {
		return YFCDocument.parse(GcoXmlUtil.getXMLString(doc)).getDocument();
	}

	/**
	 * This method is for adding child Nodes to parent node element.
	 * 
	 * @param parentElement
	 *            Parent Element under which the new Element should be present
	 * @param childElement
	 *            Child Element which should be added.
	 * @author RThallam
	 */
	public static void appendChild(Element parentElement, Element childElement) {
		parentElement.appendChild(childElement);
	}

	/**
	 * 
	 * @param inXML
	 * @param XPath
	 * @return
	 * @throws TransformerException
	 * @author RThallam
	 */
	public static Element getElementByXPath(Document inXML, String XPath)
			throws TransformerException {
		Node node = null;
		Element eleNode = null;
		Element eleRoot = null;
		eleRoot = inXML.getDocumentElement();
		CachedXPathAPI oCachedXPathAPI = new CachedXPathAPI();
		node = oCachedXPathAPI.selectSingleNode(inXML, XPath);
		eleNode = (Element) node;
		return eleNode;
	}

	/**
	 * 
	 * @param eleInput
	 * @param XPath
	 * @return
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 */
	public static Element getElementByXPath(Element eleInput, String XPath)
			throws TransformerException, ParserConfigurationException,
			FactoryConfigurationError {
		return getElementByXPath(getDocumentFromElement(eleInput), XPath);
	}

	/**
	 * This method is for removing the child element of an element
	 * 
	 * @param parentElement
	 *            Element from where the child element should be removed.
	 * @param childElement
	 *            Child Element which needs to be removed from the parent
	 * @author RThallam
	 */
	public static void removeChild(Element parentElement, Element childElement) {
		parentElement.removeChild(childElement);
	}

	/**
	 * Creates an node element with the supplied name and attributevalues
	 * 
	 * @param doc
	 *            XML Document on which to create the element
	 * @param elementName
	 *            the name of the node element
	 * @param hashAttributes
	 *            the attributes for the node element in the Hashtable
	 */
	public static Element createElement(Document doc, String elementName,
			Object hashAttributes) {
		return createElement(doc, elementName, hashAttributes, false);
	}

	/**
	 * Utilty method to create both the node element and the text node element.
	 * 
	 * @param doc
	 *            the XML document on which the node has to be created
	 * @param elementName
	 *            the name of the element to be created
	 * @param hashAttributes
	 *            the value for the text node or the attributes for the node
	 *            element
	 * @param textNodeFlag
	 *            a flag signifying whether te node to be created is the text
	 *            node
	 */
	private static Element createElement(Document doc, String elementName,
			Object hashAttributes, boolean textNodeFlag) {
		Element elem = doc.createElement(elementName);
		if (hashAttributes != null)
			if (hashAttributes instanceof String) {
				if (textNodeFlag)
					elem.appendChild(doc
							.createTextNode((String) hashAttributes));
			} else if (hashAttributes instanceof Hashtable) {
				String attributeName;
				String attributeValue;
				for (Enumeration e = ((Hashtable) hashAttributes).keys(); e
						.hasMoreElements(); elem.setAttribute(attributeName,
						attributeValue)) {
					attributeName = (String) e.nextElement();
					attributeValue = (String) ((Hashtable) hashAttributes)
							.get(attributeName);
				}

			}
		return elem;
	}

	/**
	 * Creates a child element under the parent element with given child name.
	 * Returns the newly created child element. This method returns null if
	 * either parent is null or child name is void.
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
	 * Utility method to check if a given string is null or empty (length is
	 * zero after trim call).
	 * <p>
	 * </p>
	 * 
	 * @param inStr
	 *            String for void check.
	 * @return true if the given string is void.
	 */
	public static boolean isVoid(String inStr) {
		return (inStr == null) ? true : (inStr.trim().length() == 0) ? true
				: false;
	}

	/**
	 * This method takes Document as input and returns the XML String.
	 * 
	 * @param document
	 *            a valid document object for which XML output in String form is
	 *            required.
	 * @author RThallam
	 */
	public static String getElementXMLString(Element element) {
		return serialize(element);
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
	 * Return a decendent of first parameter, that is the first one to match the
	 * XPath specified in the second parameter.
	 * 
	 * @param ele
	 *            The element to work on.
	 * @param tagName
	 *            format like "CHILD/GRANDCHILD/GRANDGRANDCHILD"
	 * 
	 * @return the first element that matched, null if nothing matches.
	 */
	public static Element getFirstElementByName(Element ele, String tagName) {
		StringTokenizer st = new StringTokenizer(tagName, "/");
		Element curr = ele;
		while (st.hasMoreTokens()) {
			String tag = st.nextToken();
			Node node;
			for (node = curr.getFirstChild(); node != null; node = node
					.getNextSibling())
				if (node.getNodeType() == 1 && tag.equals(node.getNodeName()))
					break;

			if (node != null)
				curr = (Element) node;
			else
				return null;
		}
		return curr;
	}

	public static NodeList getNodeListByXpath(Document inXML, String XPath)
			throws ParserConfigurationException, TransformerException {
		NodeList nodeList = null;
		CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
		nodeList = aCachedXPathAPI.selectNodeList(inXML, XPath);
		return nodeList;
	}

	public static Document replaceElementsWithElement(Document inXML,
			Element targetElement, Element sourceElement) throws DOMException {
		Node parentElement = sourceElement.getParentNode();
		parentElement.removeChild(sourceElement);
		Node importedNode = inXML.importNode(targetElement, true);
		parentElement.appendChild(importedNode);
		return inXML;
	}

	public static Document parseFile(String sFileName) throws SAXException,
			IOException {
		Document docOut = null;
		DOMParser oDOMParser = new DOMParser();
		oDOMParser.parse(sFileName);
		docOut = oDOMParser.getDocument();
		return docOut;
	}

	/**
	 * Evaluates the given Xpath and returns the corresponding node.
	 * 
	 * @param node
	 *            document context.
	 * @param xpath
	 *            xpath that has to be evaluated.
	 * @return node if found
	 * @throws Exception
	 *             exception
	 */
	public static Node getNode(Node node, String xpath) throws Exception {
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
	 * Evaluates the given Xpath and returns the corresponding value as a
	 * String.
	 * 
	 * @param node
	 *            document context.
	 * @param xpath
	 *            xpath that has to be evaluated.
	 * @return String Value of the XPath Execution.
	 * @throws Exception
	 *             exception
	 */
	public static String getString(Node node, String xpath) throws Exception {
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
	 * Evaluates the given Xpath and returns the corresponding node list.
	 * 
	 * @param node
	 *            document context
	 * @param xpath
	 *            xpath to be evaluated
	 * @return nodelist
	 * @throws Exception
	 *             exception
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
	 * @param node
	 *            document context
	 * @param xpath
	 *            xpath to be evaluated
	 * @return nodelist
	 * @throws Exception
	 *             exception
	 */
	public static NodeIterator getNodeIterator(Node node, String xpath)
			throws Exception {
		if (null == node) {
			return null;
		}
		NodeIterator ret = null;
		try {
			ret = (NodeIterator) XPathAPI.selectNodeIterator(node, xpath);
		} catch (TransformerException e) {
			throw e;
		}
		return ret;
	}

	/**
	 * @param elName
	 * @return
	 * @throws Exception
	 * @deprecated use XMLUtil.createDocument(String docElementTag)
	 */
	public static Document getEmptyDoc(String elName) throws Exception {
		Document ret = getDocument();
		Element el = ret.createElement(elName);
		ret.appendChild(el);
		return ret;
	}

	/**
	 * Method to return attribute value by XPath
	 * 
	 * @see Need to call like --> GSIXMLUtil.getAttributeFromXPath(Document
	 *      Name, XPath/@AttributeName")
	 * @param inXML
	 * @param xpathExpr
	 * @return Attribute Value
	 * @throws GSIException
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

	/**
	 * @navdeep.kumar - added from XMLUtil Imports an element including the
	 *                subtree from another document under the parent element.
	 *                Returns the newly created child element. This method
	 *                returns null if either parent or element to be imported is
	 *                null.
	 */
	public static Element importElement(Element parentEle,
			Element ele2beImported) {
		Element child = null;
		if (parentEle != null && ele2beImported != null) {
			child = (Element) parentEle.getOwnerDocument().importNode(
					ele2beImported, true);
			parentEle.appendChild(child);
		}
		return child;
	}

	/**
	 * @navdeep.kumar - added from XMLUtil Imports an element including the
	 *                subtree from another document under the parent element.
	 *                Returns the newly created child element. This method
	 *                returns null if either parentDoc or element to be imported
	 *                is null.
	 */
	public static Element importElement(Document parentDoc,
			Element ele2beImported) {
		Element child = null;
		if (parentDoc != null && ele2beImported != null) {
			child = (Element) parentDoc.importNode(ele2beImported, true);
			parentDoc.appendChild(child);
		}
		return child;
	}

	/**
	 * @navdeep.kumar - added from XMLUtil Imports an element based on the flag
	 *                including the subtree(if flag is true) from another
	 *                document under the parent element. Returns the newly
	 *                created child element. This method returns null if either
	 *                parentDoc or element to be imported is null.
	 */
	public static Element importElement(Document parentDoc,
			Element ele2beImported, boolean flag) {
		Element child = null;
		if (parentDoc != null && ele2beImported != null) {
			child = (Element) parentDoc.importNode(ele2beImported, flag);
			parentDoc.appendChild(child);
		}
		return child;
	}

	/**
	 * @kiran.potnuru - added copyAttributes Copies attributes and attributes
	 *                values of src element to dest element
	 */

	public static void copyAttributes(Element src, Element dest) {
		NamedNodeMap attributes = src.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr node = (Attr) attributes.item(i);
			dest.setAttributeNS(node.getNamespaceURI(), node.getName(),
					node.getValue());
		}
	}

	/**
	 * @author navdeep.kumar this will convert the XML document into String
	 * @throws TransformerException
	 */
	public static String convertXMLDocToString(Document xmlDoc)
			throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(xmlDoc), new StreamResult(writer));
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		return output;

	}
}
