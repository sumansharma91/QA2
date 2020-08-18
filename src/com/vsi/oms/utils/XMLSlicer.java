package com.vsi.oms.utils;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import javax.xml.transform.stream.*; 
public class XMLSlicer implements YIFCustomApi 
{
	StringWriter sw = new StringWriter();
	StreamResult result = new StreamResult(sw);
	public Properties localprop= null;
	public void setProperties(Properties prop) throws Exception 
	{
		localprop =prop;
	}
	public Document execute(YFSEnvironment env, Document doc) throws Exception
	{
		List<Node> lop1 = new ArrayList<Node>();
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document aDoc2  = dBuilder.newDocument();
		String Xpath="";
		String ServiceName=null;
		String API=null;
		String Merge="";
		String so="Root";
		if(localprop != null)
		{
			Enumeration e = localprop.propertyNames();
			while(e.hasMoreElements())
			{
				String name = (String)e.nextElement();
				if(name.equals("Xpath"))
				{
					Xpath =localprop.getProperty("Xpath");
				}
				if(name.equals("ServiceName"))
				{
					ServiceName =(localprop.getProperty("ServiceName"));
				}
				if(name.equals("API")) 
				{
					API =(localprop.getProperty("API"));
				}
				if(name.equals("Merge"))
				{
					Merge =localprop.getProperty("Merge");
				}

			}
			if(Xpath=="")
			{
				Node Comment =doc.createComment("*************************Mandatory Parameters for this Operation Are Missing,Ensure that Xpath is given in the Input Criteria*********************************");
				Node DocEle =doc.getDocumentElement();
				doc.insertBefore(Comment, DocEle);
				//System.out.println("******************************************************************Mandatory Parameters for this Operation Are Missing***************************************************************");
				return doc;
			}
			if((ServiceName==null)&&(API==null))
			{
				Node Comment =doc.createComment("*****************************Mandatory Parameters for this Operation Are Missing,Ensure that Service Name or API is given in the Input Criteria************************");
				Node DocEle =doc.getDocumentElement();
				doc.insertBefore(Comment, DocEle);
				//System.out.println("******************************************************************Mandatory Parameters for this Operation Are Missing***************************************************************");
				return doc;
			}
		}
		NodeList nodeList = (NodeList) xPath.evaluate(Xpath,doc, XPathConstants.NODESET);
		int count=nodeList.getLength();
		for (int i=0;i<count;i++) 
		{
			Node node=nodeList.item(i);
			Element element = (Element) node;
			Document aDoc = dBuilder.newDocument();
			Node root = aDoc.importNode(element, true);
			aDoc.appendChild(root);
			YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
			Document d;
			if(ServiceName==null)
			{
				d= callApi.invoke(env, API, aDoc);
			}
			else
			{
				d= callApi.executeFlow(env, ServiceName,aDoc);
			}
			Element  kl= d.getDocumentElement();
			Document aDoc1  = dBuilder.newDocument();
			Node xy = aDoc1.importNode(kl, true);
			so=xy.getNodeName();
			lop1.add(xy);
		}
		int count1=lop1.size();
		if(Merge.equals("Y"))
		{
			for(int k=0;k<count1;k++)
			{            
			Node nodexy=lop1.get(k);
			//System.out.println(nodexy.getNodeName());
			Node y =doc.importNode(nodexy, true);
			Node  nodeL =nodeList.item(k);
			Node x =nodeL.appendChild(y);
			}
			return doc;
		}
		else if(Merge.equals("R"))
		{
			so=so+"List";
			Element root = aDoc2.createElement(so);
			aDoc2.appendChild(root);
			//System.out.println(aDoc2.getDocumentElement());
			for(int k=0;k<count1;k++)
			{              
			Node nodexy=lop1.get(k);
			Node copyNode = aDoc2.importNode(nodexy, true);        
			root.appendChild(copyNode); 
			}
			return aDoc2;
		}
		else
		{
			return doc;
		}
	}
}


