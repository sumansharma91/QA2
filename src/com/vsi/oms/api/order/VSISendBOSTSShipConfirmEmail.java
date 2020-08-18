package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSISendBOSTSShipConfirmEmail {
	 
	private YFCLogCategory log = YFCLogCategory.instance(VSISendBOSTSShipConfirmEmail.class);
	
	public Document vsiGetAddtnlDetailsForBOSTSShipConfEmail(YFSEnvironment env, Document inXML) throws YFSException
    {
		if(log.isDebugEnabled()){	
		log.info("Inside VSISendBOSTSShipConfirmEmail :vsiGetAddtnlDetailsForBOSTSShipConfEmail:  inXML \n"+XMLUtil.getXMLString(inXML));
		}
		if(inXML != null){

			Element rootEle = inXML.getDocumentElement();
			Element orderele = (Element) inXML.getElementsByTagName("Order").item(0);
			if(!YFCObject.isVoid(orderele)){
				String strCustPO = orderele.getAttribute("CustomerPONo");
				if(!YFCObject.isVoid(strCustPO)){
					Document emailDoc = getEmailContent(env,"CustomerPONo",strCustPO);
					try {
						emailDoc = setEmailQty(env, emailDoc);	/*OMS-791*/
						//OMS-1813 start if there is no order line in email doc, then dont send email
						Element eleEmaildoc=emailDoc.getDocumentElement();
						NodeList nlchangeOrderLineList = eleEmaildoc
								.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

						if (nlchangeOrderLineList.getLength() > 0){
						VSIUtils.invokeService(env,"VSISendBOSTSShipConfEmail", emailDoc);
						}
						//oms-1813 end

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return inXML;
    }

	
	
	public static Document getEmailContent(YFSEnvironment env, String attrName, String attrValue) {
		Document getOrdLineLstInputDoc = null;
		Document getOrdLineLstOutputDoc = null;
		try {
			getOrdLineLstInputDoc = XMLUtil.createDocument("OrderLine");
			getOrdLineLstInputDoc.getDocumentElement().setAttribute(attrName, attrValue);
			getOrdLineLstOutputDoc = VSIUtils.invokeService(env, "VSIGetOrderLineDetails", getOrdLineLstInputDoc);
			
			getOrdLineLstOutputDoc = reconstructXML(env,getOrdLineLstOutputDoc);
			
			getOrdLineLstOutputDoc = addCalendarDetails(env,getOrdLineLstOutputDoc );
			
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getOrdLineLstOutputDoc;
	}
//OMS-1884 : Start
	private static Document addCalendarDetails(YFSEnvironment env,
			Document getOrdLineLstOutputDoc) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		
		Element orderEle = getOrdLineLstOutputDoc.getDocumentElement();
		Element orderLineEle = (Element) orderEle.getElementsByTagName("OrderLine").item(0);
		if(orderLineEle != null){
			String shipnode = orderLineEle.getAttribute("ShipNode");
		
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
			
			if(shipnode !=null && !shipnode.trim().equalsIgnoreCase("")){
				try {
					
					Document getCalInDoc = XMLUtil.createDocument("Calendar");
					Element rootEle = getCalInDoc.getDocumentElement();
					rootEle.setAttribute("CalendarId", shipnode);
					rootEle.setAttribute("OrganizationCode",shipnode);
					
					Document getCalOutDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetCalendarDetails.xml", "getCalendarList", getCalInDoc);
					if(getCalOutDoc != null){
						NodeList calListNL = getCalOutDoc.getElementsByTagName("Calendar");
						int calCnt = calListNL.getLength();
						if(calCnt > 0){
							Element calEle = (Element) calListNL.item(0);
						Node copyCalNode = getOrdLineLstOutputDoc.importNode(calEle, true);
						
						((Element) copyCalNode).setAttribute(VSIConstants.ATTR_ORG_NAME,strShipNodeDesc1);
						
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
		return getOrdLineLstOutputDoc;
	}

//OMS-1884-END
	private static Document reconstructXML(YFSEnvironment env, Document getOrdLineLstOutputDoc) throws Exception {
		Document reconstXML = null;

		Map <String, String> object = (Map<String, String>) env.getTxnObject("EnvShipQty");
		Element orderEle = (Element) getOrdLineLstOutputDoc.getElementsByTagName("Order").item(0);
		Element orderLinesEle = null;
		if(orderEle != null){
			reconstXML = XMLUtil.getDocumentForElement(orderEle);
			orderLinesEle = reconstXML.createElement("OrderLines");
			reconstXML.getDocumentElement().appendChild(orderLinesEle);
		}
		NodeList orderLinesNL = getOrdLineLstOutputDoc.getElementsByTagName("OrderLine");
		int length = orderLinesNL.getLength();
		String strPaymentRuleId = null;
		for(int m= 0 ; m < length; m++){
			Element orderLineEle = (Element) orderLinesNL.item(m);
			// Fix to correct ship emails in case of partial shipments.
			String sStatus = orderLineEle.getAttribute("Status");
			String strItemID = SCXmlUtil.getChildElement(orderLineEle, VSIConstants.ELE_ITEM).getAttribute(VSIConstants.ATTR_ITEM_ID);
			if(YFCObject.isVoid(strPaymentRuleId)){
				strPaymentRuleId = ((Element)orderLineEle.
						getElementsByTagName(VSIConstants.ELE_ORDER).item(0)).getAttribute(VSIConstants.ATTR_PAYMENT_RULE_ID);
			}
			if((sStatus.equalsIgnoreCase("STS Complete") || sStatus.equalsIgnoreCase("Partially STS Complete"))
					&& object.containsKey(strItemID)){
				Node ordNde = orderLineEle.getElementsByTagName("Order").item(0);
				orderLineEle.removeChild(ordNde);
				Node cpyNde = reconstXML.importNode(orderLineEle, true);
				orderLinesEle.appendChild(cpyNde);
				/*ARE-380 fix-Start*/
				if(!("DEFAULT".equals(strPaymentRuleId))){
					Element eleOrderLineSchedules = SCXmlUtil.getChildElement(orderLineEle, VSIConstants.ELE_SCHEDULES);
					ArrayList<Element> alScheduleList = SCXmlUtil.getChildren(eleOrderLineSchedules, VSIConstants.ELE_SCHEDULE);
					String strExpectedDeliveryDate=null;
					for(int i=0;i<alScheduleList.size();i++){
						Element eleSchedule = alScheduleList.get(i);
						strExpectedDeliveryDate = eleSchedule.getAttribute(VSIConstants.ATTR_EXPECTED_DELIVERY_DATE);
					}				
					Element eleExtn = SCXmlUtil.getChildElement((Element)cpyNde, VSIConstants.ELE_EXTN);
					eleExtn.setAttribute("ExtnLastPickDate", strExpectedDeliveryDate);
				}
				/*ARE-380 fix-End*/
			}
		}
		return reconstXML;
	}

	/**OMS-791*/
	public Document setEmailQty(YFSEnvironment env, Document outDoc) {

		Map <String, String> object = (Map<String, String>) env.getTxnObject("EnvShipQty");
		
		if (!YFCObject.isVoid(object)) {
			NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			
			for (int i = 0; i < orderLineList.getLength(); i++) 
			{
				Element orderLineEle = (Element) orderLineList.item(i);
				String sOrderedQty = orderLineEle.getAttribute("OrderedQty");
				Element itemEle = (Element) orderLineEle.getElementsByTagName("Item").item(0);
				String sItem = itemEle.getAttribute(VSIConstants.ATTR_ITEM_ID);
				String sQty = "";
				if (!YFCObject.isVoid(object.get(sItem))) {
					sQty = object.get(sItem).toString();
					
					if (Integer.parseInt(sOrderedQty) < Integer.parseInt(sQty))	{
						orderLineEle.setAttribute("EmailQty", sOrderedQty);
						object.put(sItem, Integer.toString(Integer.parseInt(sQty) - Integer.parseInt(sOrderedQty)));
					}
					else {
						if (!"0".equals(sQty)) {
							orderLineEle.setAttribute("EmailQty", sQty);
							object.put(sItem, "0");
						}
					}
				}	    
	
			}
			
		}
		
		return outDoc;
	}

}
