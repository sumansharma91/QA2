package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICancelOrderEmail {
	
	private  YFCLogCategory log = YFCLogCategory.instance(VSICancelOrderEmail.class);
	
	public Document vsiGetOrderDetails(YFSEnvironment env, Document inXML) throws YFSException
    {
		if(log.isDebugEnabled()){
			log.info("Inside VSICancelOrderEmail: vsiGetOrderDetails: inXML: \n"+XMLUtil.getXMLString(inXML));
		}
		if(inXML != null){
			String canReasonCode = "";
			Element orderLinesEle = (Element) inXML.getElementsByTagName("OrderLines").item(0);
			Element orderAuditEle = (Element) inXML.getElementsByTagName("OrderAudit").item(0);
			if(orderAuditEle != null){
				if(orderAuditEle.hasAttribute("ReasonCode"))
				canReasonCode = orderAuditEle.getAttribute("ReasonCode");
				if(log.isDebugEnabled()){
					log.info("canReasonCode: "+canReasonCode);
				}
			}
			if(orderLinesEle != null){
				NodeList orderLineNL = inXML.getElementsByTagName("OrderLine");
				int length = orderLineNL.getLength();
				if(length > 0){
					HashMap cpoMap = new HashMap();
					for(int m= 0 ; m < length; m ++){
						Element orderLineEle = (Element) orderLineNL.item(m);
						String custPONo = orderLineEle.getAttribute("CustomerPONo");
						if(custPONo !=null && !custPONo.trim().equals("")){
							
							if(cpoMap.size() == 0 || !cpoMap.containsKey(custPONo)){
								cpoMap.put(custPONo, "");
								Document emailXML = getOrderLineDetails(env,custPONo);
								Element rootEle = emailXML.getDocumentElement();
								rootEle.setAttribute("CancellationReasonCode", canReasonCode);
								String messageType="CANCEL";
								String emailType="CANCEL";
								if(canReasonCode != null && !canReasonCode.trim().equals("")){
									if(canReasonCode.equals("Customer Changed Mind")){
										emailType = "CANCEL";
									}else if(canReasonCode.equals("No Longer In Stock")){
										emailType = "NOINVCANCEL";
									}else if(canReasonCode.equals("AUTH_CANCEL_ORDER")){
										emailType = "AUTHFAIL";
									}else if(canReasonCode.equals("NO_STORE_PICK")){
										emailType = "NO_STORE_PICK";
									}else if(canReasonCode.equals("NO_CUSTOMER_PICK")){
										emailType = "NO_CUSTOMER_PICK";
									}
									
								}
								if(log.isDebugEnabled()){
									log.info("EmailType: "+emailType);
								}
								rootEle.setAttribute("MessageType", messageType);
								rootEle.setAttribute("EmailType", emailType);
								if(messageType != null && !messageType.trim().equals("") ){
									try {
										
										VSIUtils.invokeService(env, "VSISendCancelEmail", emailXML);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
							}
						}
					}
				}
			}
			
			
		}
		return inXML;
		
    }

	private Document getOrderLineDetails(YFSEnvironment env, String custPONo) {
		if(log.isDebugEnabled()){
			log.info("Inside VSICancelOrderEmail: getOrderLineDetails: custPONo: "+custPONo);
		}
		Document getOrderLineListIp = null;
		Document getOrderLineListOp = null;
		
		try {
			getOrderLineListIp = XMLUtil.createDocument("OrderLine");
			getOrderLineListIp.getDocumentElement().setAttribute("CustomerPONo", custPONo);
			if(log.isDebugEnabled()){
				log.info("Inside VSICancelOrderEmail: getOrderLineDetails: getOrderLineListIp: "+XMLUtil.getXMLString(getOrderLineListIp));
			}
			getOrderLineListOp = VSIUtils.invokeAPI(env, "global/template/api/VSICancelOrderEmailTemplate.xml", "getOrderLineList", getOrderLineListIp);
			if(log.isDebugEnabled()){
				log.info("Inside VSICancelOrderEmail: getOrderLineDetails: getOrderLineListOp: "+XMLUtil.getXMLString(getOrderLineListOp));
			}
			getOrderLineListOp = reconstructXML(getOrderLineListOp);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YFSException e) {
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
		
		return getOrderLineListOp;
	}
	
	private  Document reconstructXML(Document getOrdLineLstOutputDoc) throws Exception {
		if(log.isDebugEnabled()){
			log.info("Inside VSICancelOrderEmail: reconstructXML");
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
				String orgOrdQty = orderLineEle.getAttribute("OriginalOrderedQty");
				String orderedQty = orderLineEle.getAttribute("OrderedQty");
				if(log.isDebugEnabled()){
					log.info("orgOrdQty: "+orgOrdQty);
					log.info("orderedQty: "+orderedQty);
				}
				if(!orgOrdQty.equalsIgnoreCase(orderedQty)){
					if(orderedQty !=null && !orderedQty.trim().equals("") && orgOrdQty !=null && !orgOrdQty.trim().equals("")){
						int iOrgOrdQty = Integer.parseInt(orgOrdQty);
						int iOrdQty = Integer.parseInt(orderedQty);
						iOrdQty = iOrgOrdQty - iOrdQty;
						orderedQty = String.valueOf(iOrdQty);
						if(log.isDebugEnabled()){
							log.info("cancelled orderedQty: "+orderedQty);
						}
						orderLineEle.setAttribute("OrderedQty", orderedQty);
						
					}
					orderLineEle.removeChild(ordNde);
					Node cpyNde = reconstXML.importNode(orderLineEle, true);
					orderLinesEle.appendChild(cpyNde);
				}
				
			
		}
		return reconstXML;
		
		
	}

}
