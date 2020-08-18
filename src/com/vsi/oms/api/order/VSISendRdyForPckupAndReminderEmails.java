package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.comergent.api.xml.XMLUtils;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSISendRdyForPckupAndReminderEmails {
	
	private static YFCLogCategory log = YFCLogCategory.instance(VSISendRdyForPckupAndReminderEmails.class);
	
	public Document vsiGetAddtnlDetailsForRdyForPckEmail(YFSEnvironment env, Document inXML) throws YFSException
    {
		if(log.isDebugEnabled()){
			log.info("Inside VSISendRdyForPckupAndReminderEmails :vsiGetAddtnlDetailsForRdyForPckEmail:  inXML \n"+XMLUtil.getXMLString(inXML));
		}
		if(inXML != null){
			
			Element rootEle = inXML.getDocumentElement();
			String strPickupEmailFlag = null;
			String strCPO = null;
			HashMap cpomap = new HashMap();
			NodeList orderAuditLvlNL = rootEle.getElementsByTagName("OrderAuditLevel");
			int orderAuditLvl = orderAuditLvlNL.getLength();
			for(int j=0; j < orderAuditLvl; j++ ){
				Element orderAuditLvlEle = (Element) orderAuditLvlNL.item(j);
				Element orderLineEle = (Element) orderAuditLvlEle.getElementsByTagName("OrderLine").item(0);
				strCPO = orderLineEle.getAttribute("CustomerPONo");
				
				// Changes for VSI Pickup flag
				Document docGetOrderLineListDocIn = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER_LINE);
				Element eleOrderLine = docGetOrderLineListDocIn.getDocumentElement();
				Element eleOrder =null;
				String strOrderLineKey= orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY); 
				String strOrderHeaderKey= null;
				eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
				Document docGetOrderLineListDocOp = null;
				try {
					log.info("Invoking get order list API using Input :"+SCXmlUtil.getString(docGetOrderLineListDocIn));
					
					docGetOrderLineListDocOp = VSIUtils.invokeAPI(env, VSIConstants.ORDER_LINE_LIST_PICKUP_TEMPLATE, VSIConstants.API_GET_ORDER_LINE_LIST, docGetOrderLineListDocIn);
					log.info("Invoked Get Order Line List.. Output from API is"+SCXmlUtil.getString(docGetOrderLineListDocOp) );					
					eleOrder = (Element) docGetOrderLineListDocOp.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
				    strOrderHeaderKey = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
				    Element eleOrderLineExtn = (Element) docGetOrderLineListDocOp.getElementsByTagName(VSIConstants.ELE_EXTN).item(0);				
					strPickupEmailFlag = eleOrderLineExtn.getAttribute(VSIConstants.ATTR_PICKUP_EMAIL_FLAG);
					log.info("strOrderHeaderKey"+strOrderHeaderKey+"strPickupEmailFlag"+strPickupEmailFlag);		
				} catch (YFSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (YIFClientCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (strPickupEmailFlag != null && strPickupEmailFlag.equals("N")) {	
					
					log.info("pickup Flag condition is met. Inside If block of PickupEmailFlag condition check");		
					log.info("Value of strCPO" + strCPO);	
				
				if(strCPO != null && !strCPO.trim().equals("")){
					log.info("First IF Block for CPO: strCPO != null && !strCPO.trim().equals(\"\")");	
					if(cpomap.size() == 0 || !cpomap.containsKey(strCPO)){
						log.info("Second IF Block for CPO: cpomap.size() == 0 || !cpomap.containsKey(strCPO)");	
						Document emailDoc = getEmailContent(env,"CustomerPONo",strCPO);
						try {
													
							
							
							VSIUtils.invokeService(env,"VSISendRdyForPckupEmail", emailDoc);
							
							//Update Pickup Email Flag to Y--Start
							log.info("Inside Try block of updating Pickup Flag");
							
							log.info("Calling code to update Pickup email flag Input :"+SCXmlUtil.getString(emailDoc));
							
							Document docChangeOrderIn = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
							Element  eleChangeOrderIn = docChangeOrderIn.getDocumentElement();
							eleChangeOrderIn.setAttribute(VSIConstants.ATTR_OVERRIDE, "Y");
							eleChangeOrderIn.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);								
							Element eleOrderLinesChangeIn = SCXmlUtil.createChild(eleChangeOrderIn, VSIConstants.ELE_ORDER_LINES);
							
							NodeList nlEleOrderLineEmailIp = emailDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
							int orderLine = nlEleOrderLineEmailIp.getLength();
							log.info(" Total no. of order lines to update:  "+orderLine);
							
							for(int k=0; k < orderLine; k++ ){
								
								Element eleOrderLn = (Element) nlEleOrderLineEmailIp.item(k);
								Element eleOrderLineChangeIn = SCXmlUtil.createChild(eleOrderLinesChangeIn, VSIConstants.ELE_ORDER_LINE);
								log.info(" Order Line key:  "+eleOrderLn.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
								eleOrderLineChangeIn.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, eleOrderLn.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
								Element eleExtn = SCXmlUtil.createChild(eleOrderLineChangeIn, VSIConstants.ELE_EXTN);
								eleExtn.setAttribute(VSIConstants.ATTR_PICKUP_EMAIL_FLAG, "Y");	
							}
							
							//Update Pickup Email Flag to Y--End
							log.info("Invoking change Order using API using Input :"+SCXmlUtil.getString(docChangeOrderIn));	
							Document docChangeOrderOp = VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrderIn) ;
							log.info("Invoked Change Order.. Output from API is"+SCXmlUtil.getString(docChangeOrderOp) );
								
								
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						cpomap.put(strCPO, ""); 
					}
				}
				
				
			}
			}
			
			
		}// inXML null check
		return inXML;
		
    }

	
	public Document vsiGetAddtnlDetlsForPckupReminderEmail(YFSEnvironment env, Document inXML) throws YFSException
    {
		if(log.isDebugEnabled()){
			log.info("Inside VSISendRdyForPckupAndReminderEmails :vsiGetAddtnlDetlsForPckupReminderEmail: inXML \n"+XMLUtil.getXMLString(inXML));
		}
		if(inXML != null){
			
			Element rootEle = inXML.getDocumentElement();
			String olk = null;
			String cpo = null;
			NodeList orderStatusNL = rootEle.getElementsByTagName("OrderStatus");
			String strMonitorConsolidation = rootEle.getAttribute("MonitorConsolidationId");
			String strMessageType = "AWAIT1";
			if(!YFCObject.isVoid(strMonitorConsolidation)){
				if(strMonitorConsolidation.equalsIgnoreCase("VSI_AWAIT_PICK_REMAINDER1")){
					strMessageType = "AWAIT1";
				}else if(strMonitorConsolidation.equalsIgnoreCase("VSI_AWAIT_PICK_REMAINDER2")){
					strMessageType = "AWAIT2";
				}
				else if(strMonitorConsolidation.equalsIgnoreCase("VSI_AWAIT_PICK_REMAINDER3")){
					strMessageType = "AWAIT3";
				}
			}
			
			Map<String, String> CustomerPOMap = new HashMap<String, String>();
			
			int length = orderStatusNL.getLength();
			for(int j=0; j < length; j++ ){
				Element orderStatusEle = (Element) orderStatusNL.item(j);
				olk = orderStatusEle.getAttribute("OrderLineKey");
				Document getOrderLineLstIp;
				try {
					getOrderLineLstIp = XMLUtil.createDocument("OrderLine");
				    getOrderLineLstIp.getDocumentElement().setAttribute("OrderLineKey", olk);
				    Document getOrderLineLstOp = VSIUtils.invokeAPI(env, "global/template/api/VSIGetOrdLineLstTmplForReminderEmail.xml", "getOrderLineList", getOrderLineLstIp);
				    if(getOrderLineLstOp != null){
					if(getOrderLineLstOp.getElementsByTagName("OrderLine").getLength() > 0){
						Element orderLineEle = (Element) getOrderLineLstOp.getElementsByTagName("OrderLine").item(0);
						cpo = orderLineEle.getAttribute("CustomerPONo");
						if(cpo != null && !cpo.trim().equals("")){
							CustomerPOMap.put(cpo, olk);
							if(log.isDebugEnabled()){
								log.verbose("CustomerPONO  is "+cpo);
							}
						}
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
                 /******SU-98-START***************/
			for (Entry<String, String> s : CustomerPOMap.entrySet()) 
			{
            String CustomerPONO =s.getKey();
            
            if(log.isDebugEnabled()){
            	log.verbose("CustomerPONo in Map is "+CustomerPONO);
            }
		    Document emailDoc = getEmailContent(env,"CustomerPONo",CustomerPONO);
			if(!YFCObject.isVoid(strMessageType))
			emailDoc.getDocumentElement().setAttribute("MessageType", strMessageType);
			try {
				if(log.isDebugEnabled()){
					log.verbose("Calling VSISendPickupReminderEmail Service on the basis of Customer PO Number ");
				}
				VSIUtils.invokeService(env,"VSISendPickupReminderEmail", emailDoc);
			    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	     		}
			  
			    }
		        }// inXML null check
		      return inXML;

              /******SU-98-END***************/
		
    }
	
	public static Document getEmailContent(YFSEnvironment env, String attrName, String attrValue) {
		Document getOrdLineLstInputDoc = null;
		Document getOrdLineLstOutputDoc = null;
		try {
			getOrdLineLstInputDoc = XMLUtil.createDocument("OrderLine");
			getOrdLineLstInputDoc.getDocumentElement().setAttribute(attrName, attrValue);
			getOrdLineLstOutputDoc = VSIUtils.invokeService(env, "VSIGetOrderLineDetails", getOrdLineLstInputDoc);
			
			getOrdLineLstOutputDoc = reconstructXML(getOrdLineLstOutputDoc);
			
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

	private static Document addCalendarDetails(YFSEnvironment env,
			Document getOrdLineLstOutputDoc) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.debug("addCalendarDetails input : "+ XMLUtil.getXMLString(getOrdLineLstOutputDoc));
		}
		Element orderEle = getOrdLineLstOutputDoc.getDocumentElement();
		Element orderLineEle = (Element) orderEle.getElementsByTagName("OrderLine").item(0);
		if(orderLineEle != null){
			String shipnode = orderLineEle.getAttribute("ShipNode");
		//OMS-1572:Start
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
			//OMS-1572:End
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
		if(log.isDebugEnabled()){
			log.debug("addCalendarDetails output : "+ XMLUtil.getXMLString(getOrdLineLstOutputDoc));
		}
		return getOrdLineLstOutputDoc;
	}

	private static Document reconstructXML(Document getOrdLineLstOutputDoc) throws Exception {
		
		if(log.isDebugEnabled()){
			log.debug("reconstructXML  :input "+ XMLUtil.getXMLString(getOrdLineLstOutputDoc));
		}
		Document reconstXML = null;
		
		Element orderEle = (Element) getOrdLineLstOutputDoc.getElementsByTagName("Order").item(0);
		Element orderLinesEle = null;
		if(orderEle != null){
			reconstXML = XMLUtil.getDocumentForElement(orderEle);
			orderLinesEle = reconstXML.createElement("OrderLines");
			reconstXML.getDocumentElement().appendChild(orderLinesEle);
		}
		NodeList orderLinesNL = getOrdLineLstOutputDoc.getElementsByTagName("OrderLine");
		int length = orderLinesNL.getLength();
		for(int m= 0 ; m < length; m++){
			Element orderLineEle = (Element) orderLinesNL.item(m);
			Node ordNde = orderLineEle.getElementsByTagName("Order").item(0);
				orderLineEle.removeChild(ordNde);
				Node cpyNde = reconstXML.importNode(orderLineEle, true);
				orderLinesEle.appendChild(cpyNde);
			
		}
		
		if(log.isDebugEnabled()){
			log.debug("reconstructXML  :output  "+ XMLUtil.getXMLString(getOrdLineLstOutputDoc));
		}
		return reconstXML;
		
		
	}

	

}
