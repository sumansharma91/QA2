package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSISendConfirmEmail {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSISendConfirmEmail.class);
	
	public Document vsiGetAddtnlDetails(YFSEnvironment env, Document inXML) throws YFSException
    {
		if(log.isDebugEnabled()){
			log.info("Inside VSISendConfirmEmail : inXML \n"+XMLUtil.getXMLString(inXML));
		}
		if(inXML != null){
			
			Element orderEle = inXML.getDocumentElement();
			if(orderEle.hasAttribute("To"))
			{
				orderEle.setAttribute("CustomerEMailID", inXML.getDocumentElement().getAttribute("To"));
			}
				Element orderLineEle = (Element) orderEle.getElementsByTagName("OrderLine").item(0);
				if(orderLineEle != null){
					String shipnode = orderLineEle.getAttribute("ShipNode");
					if(shipnode !=null && !shipnode.trim().equalsIgnoreCase("")){
						try {
							
							Document getCalInDoc = XMLUtil.createDocument("Calendar");
							Element rootEle = getCalInDoc.getDocumentElement();
							rootEle.setAttribute("CalendarId", shipnode);
							//OMS-998 Updating the OrganizationCode to shipnode : Start
							rootEle.setAttribute("OrganizationCode", shipnode);
							//OMS-998 Updating the OrganizationCode to shipnode : End
							////OMS-1572:Start
							Document docGetShipNodeList = XMLUtil.createDocument(VSIConstants.ELE_SHIP_NODE);
							Element eleOrg = docGetShipNodeList.getDocumentElement();
							eleOrg.setAttribute(VSIConstants.ATTR_SHIP_NODE, shipnode);
							Document docGetShipNodeListOut = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_SHIPNODE_LIST, VSIConstants.API_GET_SHIPNODE_LIST, docGetShipNodeList);
							String strShipNodeDesc=null;
							String strShipNodeDesc1=null;
							 if(!YFCObject.isVoid(docGetShipNodeListOut))
							{
							Element eleShipNode=(Element) docGetShipNodeListOut.getElementsByTagName(VSIConstants.ELE_SHIP_NODE).item(0);
							if(null!=eleShipNode)
							{
								strShipNodeDesc=eleShipNode.getAttribute(VSIConstants.ATTR_DESCRIPTION);
							}
							if(!YFCObject.isVoid(strShipNodeDesc) && strShipNodeDesc.contains(","))
							 { 
							     strShipNodeDesc1=VSIConstants.EMAIL_HEADER+strShipNodeDesc.substring(0, strShipNodeDesc.lastIndexOf(","));
							 }
							 else if (!YFCObject.isVoid(strShipNodeDesc) && strShipNodeDesc.contains(" "))
							 {
								 strShipNodeDesc1=VSIConstants.EMAIL_HEADER+strShipNodeDesc.substring(0, strShipNodeDesc.lastIndexOf(" "));
							 }
							 else
								 strShipNodeDesc1=VSIConstants.EMAIL_HEADER+strShipNodeDesc;
							}
							////OMS-1572:End
							Document getCalOutDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetCalendarDetails.xml", "getCalendarList", getCalInDoc);
							if(getCalOutDoc != null){
								NodeList calListNL = getCalOutDoc.getElementsByTagName("Calendar");
								int calCnt = calListNL.getLength();
								if(calCnt > 0){
									Element calEle = (Element) calListNL.item(0);
								Node copyCalNode = inXML.importNode(calEle, true);
								//OMS-1572:Start
								((Element) copyCalNode).setAttribute(VSIConstants.ATTR_ORG_NAME,strShipNodeDesc1);
								//OMS-1572:End
								orderEle.appendChild(copyCalNode);
								}
								
							}
							
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (YIFClientCreationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}// OrderLine ShipNode attribute null check
					
					
				}//OrderLine Element null Check
			
				
			
			
			
			
		}// inXML null check
		return inXML;
		
    }

	

}
