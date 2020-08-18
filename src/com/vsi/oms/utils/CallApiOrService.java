package com.vsi.oms.utils;
import java.util.Enumeration;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;

public class CallApiOrService 
{
	public Properties localprop= null;
	public void setProperties(Properties prop) throws Exception 
	{
		localprop =prop;
	}
	public Document execute(YFSEnvironment env, Document inDoc) throws Exception
	{
		String APIName=null;
		String ServiceName=null;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document outDoc  = dBuilder.newDocument();
		YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
		if(localprop != null)
		{
			Enumeration e = localprop.propertyNames();
			while(e.hasMoreElements())
			{
				String name = (String)e.nextElement();
				if(name.equals("APIName"))
				{
					APIName =localprop.getProperty("APIName");
				}
				if(name.equals("ServiceName"))
				{
					ServiceName =localprop.getProperty("ServiceName");
				}
			}
			if((ServiceName==null)&&(APIName==null))
			{
				Node Comment =inDoc.createComment("***Mandatory Parameters for this Operation Are Missing,Ensure that Service Name or API is given in the Input Criteria************************");
				Node DocEle =inDoc.getDocumentElement();
				inDoc.insertBefore(Comment, DocEle);
				return inDoc;
			}
			if(ServiceName==null)
			{
				outDoc= callApi.invoke(env, APIName, inDoc);
			}
			else
			{
				outDoc= callApi.executeFlow(env, ServiceName,inDoc);
			}
		}
		return outDoc;
	}
}
