package com.vsi.oms.agent;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.allocation.api.VSIProcessRelease;
import com.vsi.oms.api.web.VSIInvokeJDAWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ycp.japi.util.YCPBaseTaskAgent;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This will be a task q based agent. It will fetch all the records which are to be picked up by send release transaction
 * 
 * @author IBM
 *
 */
public class VSISendRelease extends YCPBaseTaskAgent implements VSIConstants {
	
	
	public static final String ATTR_CHAINED_FROM_ORDER_LINE_KEY = "ChainedFromOrderLineKey";
	private YFCLogCategory log = YFCLogCategory.instance(VSISendRelease.class);

	
	@Override
	public Document executeTask(YFSEnvironment env, Document docInput) throws Exception {
		log.beginTimer("VSISendRelease.executeJob : START");
		//get orderReleaseList from taskqueue		
		VSIUtils.invokeService(env, "VSISendRelease", docInput);		
		log.endTimer("VSISendRelease.executeJob : END");
		return docInput;
	}

	/*
	 * Release to WMS is sent in three steps-
	 * 1. Get Release Details And Massage it to add attributes.
	 * 2. Call VSIProcessRelease Service to Call JDA and add Transfer Number
	 * 3. Call VSISendReleaseTo WMS to transmit release message to WMS.
	 */
	public Document sendRelease(YFSEnvironment env, Document docInput)
			throws ParserConfigurationException, YIFClientCreationException, RemoteException, Exception {
		
		Element rootEleInDoc = docInput.getDocumentElement();
		String attrDataKey = null;
		if (rootEleInDoc.hasAttribute(ATTR_DATA_KEY)){
			attrDataKey = rootEleInDoc.getAttribute("DataKey");
			getReleaseDetailsAndProcess(env, attrDataKey, rootEleInDoc.getAttribute(ATTR_TASKQKEY));
		}else{
			//getReleaseList
			Document docGetOrderReleaseInput = SCXmlUtil.createDocument(ELE_ORDER_RELEASE);
			String strOrderHeaderKey = rootEleInDoc.getAttribute(ATTR_ORDER_HEADER_KEY);
			Element eleGetOrderReleaseInput = docGetOrderReleaseInput.getDocumentElement();
			eleGetOrderReleaseInput.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			eleGetOrderReleaseInput.setAttribute(ATTR_SALES_ORDER_NO, rootEleInDoc.getAttribute(ATTR_ORDER_NO));
			eleGetOrderReleaseInput.setAttribute(ATTR_DOCUMENT_TYPE, rootEleInDoc.getAttribute(ATTR_DOCUMENT_TYPE));
			eleGetOrderReleaseInput.setAttribute(ATTR_ENTERPRISE_CODE, rootEleInDoc.getAttribute(ATTR_ENTERPRISE_CODE));
			
			
			Document docOrderReleaseListTemplate = SCXmlUtil.createDocument(ELE_ORDER_RELEASE_LIST);
			Element eleOrderReleaseListTemplate = docOrderReleaseListTemplate.getDocumentElement();
			Element eleOrderReleaseTemplate = SCXmlUtil.createChild(eleOrderReleaseListTemplate, ELE_ORDER_RELEASE);
			eleOrderReleaseTemplate.setAttribute(ATTR_ORDER_RELEASE_KEY, "");
			//call processRelease for eachRelease
			
			Document docOrderReleaseListOutput = VSIUtils.invokeAPI(env, docOrderReleaseListTemplate, API_GET_ORDER_RELEASE_LIST,docGetOrderReleaseInput);
			Element eleOrderReleaseListOutput = docOrderReleaseListOutput.getDocumentElement();
			ArrayList<Element> arlOrderRelease = SCXmlUtil.getChildren(eleOrderReleaseListOutput, ELE_ORDER_RELEASE);
			for(int i=0;i<arlOrderRelease.size();i++){
				Element eleOrderRelease = arlOrderRelease.get(i);
				String strOrderReleaseKey = eleOrderRelease.getAttribute(ATTR_ORDER_RELEASE_KEY);
				getReleaseDetailsAndProcess(env,strOrderReleaseKey, rootEleInDoc.getAttribute(ATTR_TASKQKEY));
			}
		}
		return docInput;
	}


	public Document getReleaseDetailsAndProcess(YFSEnvironment env, String attrDataKey, String taskQKeyValue)
			throws ParserConfigurationException, YIFClientCreationException, RemoteException, Exception {
		Document docOrderReleaseListInput  = XMLUtil.createDocument(ELE_ORDER_RELEASE);
		Element eleOrderReleaseListInput = docOrderReleaseListInput.getDocumentElement();
		eleOrderReleaseListInput.setAttribute(ATTR_ORDER_RELEASE_KEY, attrDataKey);
		env.setApiTemplate(API_GET_ORDER_RELEASE_LIST, "global/template/api/getOrderReleaseList_VSISendRelease.xml");
		Document docOrderReleaseList = VSIUtils.invokeAPI(env, API_GET_ORDER_RELEASE_LIST, docOrderReleaseListInput);
		
		updateAdditionalAttributesForWholesaleOrder(env, docOrderReleaseList);
		try
		{
		Element eleOrderReleaseList = docOrderReleaseList.getDocumentElement();
		ArrayList<Element> alOrderReleaseList = SCXmlUtil.getChildren(eleOrderReleaseList, VSIConstants.ELE_ORDER_RELEASE);
		
		for (Element eleOrderRelease : alOrderReleaseList) {
			//OMS-965 :Start
			String strStatus=eleOrderRelease.getAttribute(VSIConstants.ATTR_STATUS);
			if(null!=strStatus && !STATUS_CANCELLED.equals(strStatus))
			{	
			//Defect-fix for ARE-241-Start
			String strShipNode = eleOrderRelease.getAttribute(ATTR_SHIP_NODE);
			
			if (strShipNode.equalsIgnoreCase(VSIConstants.SHIP_NODE_CC))
			{
				continue;
			}
			//Defect-fix for ARE-241-Close
			String sDocumentType = eleOrderRelease.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
			// Set STSShipNode at OrderRelease level
			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrderRelease, VSIConstants.ELE_ORDER_LINES);
			//Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
			ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLines, ELE_ORDER_LINE);
			
			String strSignatureType = null;
			//OMS-875:Start
			String strSignatureTypeRelease=null;
			//OMS-875:End
			
			
			if(sDocumentType.equalsIgnoreCase(VSIConstants.DOC_TYPE_TO)){
					String strReceivingNode = eleOrderRelease.getAttribute(ATTR_RECEIVING_NODE);
			//		String strShipNode = eleOrderRelease.getAttribute(ATTR_SHIP_NODE);
					
					eleOrderRelease.setAttribute(ATTR_SHIP_NODE, strReceivingNode);
					
					//Stamp ShipNode's person info ship to as release's person info ship to
					
					//form input for getOrganizationHierarchy
					Document docOrganization = SCXmlUtil.createDocument(ELE_ORGANIZATION);
					Element eleOrganization = docOrganization.getDocumentElement();
					eleOrganization.setAttribute(ATTR_ORGANIZATION_CODE, strReceivingNode);
					
					//form template for getOrganizationHierarchy
					Document docOrgTemplate = SCXmlUtil.createDocument(ELE_ORGANIZATION);
					Element eleOrgTemplate = docOrgTemplate.getDocumentElement();
					eleOrgTemplate.setAttribute(ATTR_ORGANIZATION_CODE, ATTR_EMPTY);
					SCXmlUtil.createChild(eleOrgTemplate, "CorporatePersonInfo");
					
					
					//invoke getOrganizationHierarchy
					Document docOrganizationOut = VSIUtils.invokeAPI(env, docOrgTemplate, API_GET_ORG_HIERARCHY, docOrganization);
					Element eleOrgaizationOut = docOrganizationOut.getDocumentElement();
					Element elePersonInfoShipOut = SCXmlUtil.getChildElement(eleOrgaizationOut, "CorporatePersonInfo");
					Element eleOrderReleasePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderRelease, ELE_PERSON_INFO_SHIP_TO);
					//replace node in release
					SCXmlUtil.removeNode(eleOrderReleasePersonInfoShipTo);
					Element eleReleasePersonInfoShipToOld = SCXmlUtil.createChild(eleOrderRelease, "PersonInfoShipTo");
					if(!YFCCommon.isVoid(elePersonInfoShipOut)){
						SCXmlUtil.setAttributes(elePersonInfoShipOut, eleReleasePersonInfoShipToOld);
					}
					//XMLUtil.importElement(eleOrderRelease, elePersonInfoShipOut);
					//SCXmlUtil.importElement(eleOrderRelease, elePersonInfoShipOut);
					
					
					
					eleOrderRelease.setAttribute(ATTR_STS_SHIP_NODE, strShipNode);
			}
			
			
			String strCustomerPONo = null;
			String orderType = null;
			for(Element eleOrderLine: arrOrderLines){
				Document docGetOrderLineList = SCXmlUtil.createDocument(ELE_ORDER_LINE);
				Element eleGetOrderLineList = docGetOrderLineList.getDocumentElement();
				eleOrderLine.setAttribute(ATTR_ORD_QTY, eleOrderLine.getAttribute(ATTR_STATUS_QTY));
				Element eleOrderLineExtn = SCXmlUtil.getChildElement(eleOrderLine, ELE_EXTN);
				if(!YFCCommon.isVoid(eleOrderLineExtn)){
					//OMS-875:Start
					//if(!YFCCommon.isVoid(strSignatureType)){
						strSignatureType = eleOrderLineExtn.getAttribute(ATTR_EXTN_SIGNATURE_TYPE);
						if(!YFCCommon.isVoid(strSignatureType)&& (YFCCommon.isVoid(strSignatureTypeRelease)||(FLAG_N).equals(strSignatureTypeRelease))){
							strSignatureTypeRelease=strSignatureType;
						}
						else if(!YFCCommon.isVoid(strSignatureType)&& (FLAG_Y).equals(strSignatureTypeRelease)){
							strSignatureTypeRelease=(FLAG_A).equals(strSignatureType)?strSignatureType:strSignatureTypeRelease;
						}
						
					//}
					//OMS-875:End	
				}
				
				if(sDocumentType.equalsIgnoreCase(VSIConstants.DOC_TYPE_TO)){
					
					//Set CustomerPO No from sales order line
					//call getOrderLineDetails for sales orderline as Customer PO No is not stamped on transfer order.
					
					String strDerivedFromOrderLineKey = eleOrderLine.getAttribute(ATTR_CHAINED_FROM_ORDER_LINE_KEY);
					if(!YFCCommon.isVoid(strDerivedFromOrderLineKey)){
						eleGetOrderLineList.setAttribute(ATTR_ORDER_LINE_KEY,strDerivedFromOrderLineKey );
						docGetOrderLineList = VSIUtils.invokeAPI(env, "global/template/api/getOrderLineList_VSISendRelease.xml", API_GET_ORDER_LINE_LIST, docGetOrderLineList);
						eleGetOrderLineList = docGetOrderLineList.getDocumentElement();
					
						Element eleGetOrderLine = SCXmlUtil.getChildElement(eleGetOrderLineList, ELE_ORDER_LINE);
						eleOrderLine.setAttribute(ATTR_CUST_PO_NO, eleGetOrderLine.getAttribute(ATTR_CUST_PO_NO));
						strCustomerPONo = eleGetOrderLine.getAttribute(ATTR_CUST_PO_NO);
						Element eleGetOrder = SCXmlUtil.getChildElement(eleGetOrderLine, ELE_ORDER);
						orderType = eleGetOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
						
						//get personInfobill to from sales order
						Element elePersonInfoBillTo = SCXmlUtil.getChildElement(eleGetOrder, ELE_PERSON_INFO_BILL_TO);
						Element eleReleasePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderRelease, ELE_PERSON_INFO_SHIP_TO);
						//copy it on to order release ship to
						if(!YFCCommon.isVoid(eleReleasePersonInfoShipTo)&&!YFCCommon.isVoid(elePersonInfoBillTo)){
							eleReleasePersonInfoShipTo.setAttribute(ATTR_FIRST_NAME, elePersonInfoBillTo.getAttribute(ATTR_FIRST_NAME));
							eleReleasePersonInfoShipTo.setAttribute(ATTR_LAST_NAME, elePersonInfoBillTo.getAttribute(ATTR_LAST_NAME));
						}
						
					}	
				}
			}
			
			Element transferOrderEle = SCXmlUtil.getChildElement(eleOrderRelease, VSIConstants.ELE_ORDER);
			if(!YFCCommon.isVoid(orderType))
			{
				transferOrderEle.setAttribute(VSIConstants.ATTR_ORDER_TYPE, orderType);
			}
			//set signature type as Order Release level
			//OMS-875:Start
			if(!YFCCommon.isVoid(strSignatureTypeRelease)){
				eleOrderRelease.setAttribute(ATTR_SIGNATURE_TYPE, strSignatureTypeRelease);
			////OMS-875:End	
			}else{
				eleOrderRelease.setAttribute(ATTR_SIGNATURE_TYPE, FLAG_N);
			}
			
			if(!YFCCommon.isVoid(strCustomerPONo)){
				eleOrderRelease.setAttribute("CustomerPoNo", strCustomerPONo);
			}
			//Set ExtnIsPOBox
			Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderRelease, ELE_PERSON_INFO_SHIP_TO);
			if(!YFCCommon.isVoid(elePersonInfoShipTo)){
				String strAddressLine6 = elePersonInfoShipTo.getAttribute(ATTR_ADDRESS6);
				Element eleShipToExtn = SCXmlUtil.getChildElement(elePersonInfoShipTo, ELE_EXTN);
				if(YFCCommon.isVoid(eleShipToExtn)){
					eleShipToExtn = SCXmlUtil.createChild(elePersonInfoShipTo, ELE_EXTN);
				}
				if(YFCCommon.isVoid(strAddressLine6)){
					
					eleShipToExtn.setAttribute(ATTR_EXTN_IS_PO_BOX, FLAG_N);
				}else{
					eleShipToExtn.setAttribute(ATTR_EXTN_IS_PO_BOX, strAddressLine6);
				}
			}
			
			// Call VSIProcessRelease with the order release document to stamp JDA Transfer number on the release.
			Document docOrderRelease = SCXmlUtil.createFromString(SCXmlUtil.getString(eleOrderRelease));
			//OMS-1767:START
			Element eleOrd = SCXmlUtil.getChildElement(docOrderRelease.getDocumentElement(), ELE_ORDER);
			String strOrderType = eleOrd.getAttribute(ATTR_ORDER_TYPE);
			if(DOCUMENT_TYPE.equalsIgnoreCase(sDocumentType) && !WHOLESALE.equals(strOrderType)){
			docOrderRelease.getDocumentElement().setAttribute(ATTR_SCAC,SCAC_UPSN);
			}
			//OMS-1767:END
			Element eleExtn = SCXmlUtil.getChildElement(docOrderRelease.getDocumentElement(), ELE_EXTN);
			String strTransferNo = eleExtn.getAttribute(ATTR_EXTN_TRANSFER_NO);
						
			log.info("docOrderRelease before new logic => " +XMLUtil.getXMLString(docOrderRelease));
			Element orderReleaseElement = (Element) docOrderRelease.getElementsByTagName(ELE_ORDER_RELEASE).item(0);
			String shipNodeAttr = orderReleaseElement.getAttribute(ATTR_SHIP_NODE);
			log.info("shipNodeAttr => "+shipNodeAttr);
			if((!shipNodeAttr.equalsIgnoreCase(SHIP_NODE_9004_VALUE) && !shipNodeAttr.equalsIgnoreCase(SHIP_NODE_9005_VALUE)) && (sDocumentType.equalsIgnoreCase(DOCUMENT_TYPE)))
			{
				if(YFCCommon.isVoid(strTransferNo))
					{
						docOrderRelease.getDocumentElement().setAttribute(ATTR_SFS_ALLOCATION, FLAG_Y);
						docOrderRelease = VSIUtils.invokeService(env, SERVICE_JDA_FORCE_ALLOCATION_WEB_SERVICE, docOrderRelease);
						log.info("Post SFS JDA docOrderRelease => " +XMLUtil.getXMLString(docOrderRelease));
						createShipmentOnSendRelease(env,docOrderRelease);
						log.info("Post createShipment");
						manageTaskQ(env, taskQKeyValue);
						log.info("Post manageTaskQ");
					}
			}
			else
			{
				if(YFCCommon.isVoid(strTransferNo))
					docOrderRelease = VSIUtils.invokeService(env, SERVICE_PROCESS_RELEASE, docOrderRelease);
				
				log.info("docOrderRelease => " +XMLUtil.getXMLString(docOrderRelease));
			// Send release to WMS
			try {				
					VSIUtils.invokeService(env, SERVICE_SEND_RELEASE_TO_WMS, docOrderRelease);
				}
			catch(Exception e)
			{
				//Invoke JDA Service to reverse the force allocation.
				log.info("Posting to WMS Queue - Error as below");
				e.printStackTrace();
				docOrderRelease.getDocumentElement().setAttribute(ATTR_REVERSE_ALLOCATION, FLAG_Y);
				docOrderRelease.getDocumentElement().setAttribute(ATTR_SEND_RELEASE, FLAG_Y);
				if(log.isDebugEnabled())
					log.debug("Release XML"+SCXmlUtil.getString(docOrderRelease));
				VSIUtils.invokeService(env, SERVICE_PROCESS_RELEASE, docOrderRelease);
				
				String orderHeaderKey= docOrderRelease.getDocumentElement().getAttribute(ATTR_ORDER_HEADER_KEY).toString();
				NodeList orderLines = docOrderRelease.getElementsByTagName(ELE_ORDER_LINE);
				String itemId="";
				for (int i = 0; i < orderLines.getLength(); i++) 
				{
					Element orderLineEle = (Element) docOrderRelease.getElementsByTagName(ELE_ORDER_LINE).item(i);
					Element itemEle = (Element) orderLineEle.getElementsByTagName(ELE_ITEM).item(0);
						if(orderLines.getLength() == 1)
						{
							itemId=itemEle.getAttribute(ATTR_ITEM_ID).toString();
							break;
						}
						else
							itemId=itemEle.getAttribute(ATTR_ITEM_ID).toString()+" "+itemId;
				}
				log.info("WMS catch - itemId =>  "+ itemId);
				if(itemId.length() >= 31)
					itemId = itemId.substring(0,31).concat("...");
				log.info("Truncated/Entire WMS catch - itemId =>  "+ itemId);
				Document getOrderListInXML=XMLUtil.createDocument(ELE_ORDER);
				Element getOrderListEle = getOrderListInXML.getDocumentElement();
				getOrderListEle.setAttribute(ATTR_ORDER_HEADER_KEY,orderHeaderKey);
				Document getOrderListOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_LIST, getOrderListInXML);
				Element orderEle = (Element) getOrderListOutXML.getElementsByTagName(ELE_ORDER).item(0);
				String orderNo = orderEle.getAttribute(ATTR_ORDER_NO);
				VSIInvokeJDAWebservice jda = new VSIInvokeJDAWebservice();
				jda.alertForJDASendReleaseFailure(env, itemId, orderNo, orderHeaderKey, ALERT_SEND_RELEASE_JDA_WMS_EXCEPTION_TYPE, ALERT_SEND_RELEASE_JDA_WMS_DETAIL_DESCRIPTION, ALERT_SEND_RELEASE_JDA_WMS_QUEUE);
				VSIProcessRelease processReleaseObj = new VSIProcessRelease();
				processReleaseObj.invokeChangeRelease(env, docOrderRelease, "");
				throw new YFSException(JDA_EXCEPTION_ERROR_CODE,JDA_EXCEPTION_ERROR_CODE,JDA_EXCEPTION_ERROR_MSG);
			}
			
			log.info("Post WMS Success");
			Element extnEle = (Element) docOrderRelease.getElementsByTagName(ELE_EXTN).item(0);
			String transferNoValue = extnEle.getAttribute(ATTR_EXTN_TRANSFER_NO);
			VSIProcessRelease processReleaseObj = new VSIProcessRelease();
			processReleaseObj.invokeChangeRelease(env, docOrderRelease, transferNoValue);
			
			log.info("Post Change Release");
			// Change status to SentToNode (3200.600)
			Document docChngOrdStat = SCXmlUtil.createDocument(ELE_ORDER_STATUS_CHANGE);
			Element eleChngOrdStat = docChngOrdStat.getDocumentElement();
			eleChngOrdStat.setAttribute(ATTR_ORDER_HEADER_KEY,
					eleOrderRelease.getAttribute(ATTR_ORDER_HEADER_KEY));
			eleChngOrdStat.setAttribute(ATTR_ORDER_RELEASE_KEY,
					eleOrderRelease.getAttribute(ATTR_ORDER_RELEASE_KEY));
			eleChngOrdStat.setAttribute(ATTR_BASE_DROP_STATUS, STATUS_CODE_SENT_TO_NODE);
			if(sDocumentType.equalsIgnoreCase(DOC_TYPE_TO)){
				eleChngOrdStat.setAttribute(ATTR_TRANSACTION_ID, TRANS_SEND_RELEASE_TO);
			}else{
				eleChngOrdStat.setAttribute(ATTR_TRANSACTION_ID, TRANS_SEND_RELEASE);
			}
			VSIUtils.invokeAPI(env, TEMPLATE_ORDER_STATUS_CHANGE_ORDER_RELEASE_KEY, API_CHANGE_ORDER_STATUS,
					docChngOrdStat);
			
			log.info("Post Change Order Status");
			manageTaskQ(env, taskQKeyValue);
				
			}
			//OMS-965 : end
		}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return docOrderReleaseList;
	}
	
	private void updateAdditionalAttributesForWholesaleOrder(YFSEnvironment env, Document inXML) throws Exception{
		
		Element eleOrder = SCXmlUtil.getXpathElement(inXML.getDocumentElement(), "/OrderReleaseList/OrderRelease/Order");
		if(!YFCObject.isVoid(eleOrder)){
			
			String strOrderType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_TYPE);
			String enterpriseCode = SCXmlUtil.getAttribute(eleOrder, ATTR_ENTERPRISE_CODE);
			if(strOrderType.equalsIgnoreCase(VSIConstants.WHOLESALE)){
				
				if(!YFCObject.isVoid(enterpriseCode)){
					
					//Calling getCommonCodeList API to check Case Qty Allocation for Enterprise
					Element eleCommonCode = (Element) (VSIUtils.getCommonCodeList(env, 
							VSIConstants.VSI_WH_ALLOC_CASEQTY, enterpriseCode, VSIConstants.ATTR_DEFAULT).get(0));
					if(!YFCCommon.isVoid(eleCommonCode)) {
						
						String strCodeShortDesc = eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
						//String strCodeLongDesc = eleCommonCode.getAttribute(ATTR_CODE_LONG_DESCRIPTION);
						if(log.isDebugEnabled()){

							log.debug("VSISendRelease.updateCaseQuantityForWholesaleOrder: CodeShortdesc is " + strCodeShortDesc);
							//log.debug("VSISendRelease.updateCaseQuantityForWholesaleOrder: CodeLongdesc is " + strCodeLongDesc);
						}
						
						if(!YFCObject.isVoid(strCodeShortDesc) && strCodeShortDesc.equalsIgnoreCase(FLAG_Y)){
							
							NodeList nlOrderLine = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
							for(int i = 0; i < nlOrderLine.getLength(); i++){
								
								Integer caseQty = 1;
								String strCaseQty = ONE;
								String strExpiryDays = "";
								Element eleOrderLine = (Element) nlOrderLine.item(i);
								Element eleOrderLineExtn = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_EXTN);
								if(YFCObject.isVoid(eleOrderLineExtn)){
									
									eleOrderLineExtn = SCXmlUtil.createChild(eleOrderLine, VSIConstants.ELE_EXTN);
								}
								
								Element eleItemDetails = SCXmlUtil.getChildElement(eleOrderLine, ELE_ITEM_DETAILS);
								if(!YFCObject.isVoid(eleItemDetails)){

									Boolean partnerCaseQtyExists = false;
									Element eleItemExtn = SCXmlUtil.getChildElement(eleItemDetails, ELE_EXTN);
									NodeList nlAdditionalAttribute = eleItemDetails.getElementsByTagName(ELE_ADDNL_ATTRIBUTE);
									for(int j = 0; j < nlAdditionalAttribute.getLength(); j++){

										Element eleAdditionalAttribute = (Element) nlAdditionalAttribute.item(j);
										String attrDomainID = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_ATTRIBUTE_DOMAIN_ID);
										String attrGroupID = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_ATTRIBUTE_GROUP_ID);
										String name = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_NAME);
										if(attrDomainID.equals(ITEM_ATTRIBUTE) && attrGroupID.equals(WHOLESALE_ORG_LEVEL_CASE_QTY)
												&& name.contains(enterpriseCode)){

											strCaseQty = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_VALUE);
											if(YFCObject.isVoid(strCaseQty)){
												strCaseQty = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_INTEGER_VALUE);
											}

											if(!YFCObject.isVoid(strCaseQty)){
												caseQty = Double.valueOf(strCaseQty).intValue();
												if(caseQty > 0){
													partnerCaseQtyExists = true;
												}
											}
										}
										if(attrDomainID.equals(ITEM_ATTRIBUTE) && attrGroupID.equals(WHOLESALE_ORG_ITEM_EXPIRY_DAYS)
												&& name.contains(enterpriseCode)){
											
											strExpiryDays = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_VALUE);
										}
									}

									if(!partnerCaseQtyExists){
										if(!YFCObject.isVoid(eleItemExtn)){

											strCaseQty = SCXmlUtil.getAttribute(eleItemExtn, ATTR_EXTN_CASE_QTY);
											if(!YFCObject.isVoid(strCaseQty)){

												caseQty = Double.valueOf(strCaseQty).intValue();
												if(caseQty < 1) caseQty = 1;
											}
										}
									}
									
									SCXmlUtil.setAttribute(eleOrderLineExtn, ATTR_EXPIRY_DAYS, strExpiryDays);
									SCXmlUtil.setAttribute(eleOrderLineExtn, ATTR_EXTN_CASE_QTY, String.valueOf(caseQty));
								}
							}
						}else{
							
							NodeList nlOrderLine = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
							for(int i = 0; i < nlOrderLine.getLength(); i++){
								// Set Default value of "1"
								Element eleOrderLine = (Element) nlOrderLine.item(i);
								Element eleOrderLineExtn = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_EXTN);
								String strExpiryDays = "";
								NodeList nlAdditionalAttribute = eleOrderLine.getElementsByTagName(ELE_ADDNL_ATTRIBUTE);
								for(int j = 0; j < nlAdditionalAttribute.getLength(); j++){
									
									Element eleAdditionalAttribute = (Element) nlAdditionalAttribute.item(j);
									String attrDomainID = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_ATTRIBUTE_DOMAIN_ID);
									String attrGroupID = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_ATTRIBUTE_GROUP_ID);
									String name = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_NAME);
									if(attrDomainID.equals(ITEM_ATTRIBUTE) && attrGroupID.equals(WHOLESALE_ORG_ITEM_EXPIRY_DAYS)
											&& name.contains(enterpriseCode)){
										
										strExpiryDays = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_VALUE);
										break;
									}
									
									SCXmlUtil.setAttribute(eleOrderLineExtn, ATTR_EXPIRY_DAYS, strExpiryDays);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void createShipmentOnSendRelease(YFSEnvironment env, Document inXml)
    {
        try
        {
        Element orderReleaseElement = inXml.getDocumentElement();
        String orderHeaderKey = orderReleaseElement.getAttribute(ATTR_ORDER_HEADER_KEY);
        String documentType = orderReleaseElement.getAttribute(ATTR_DOCUMENT_TYPE);
        NodeList orderLineNode = orderReleaseElement.getElementsByTagName(ELE_ORDER_LINE);
        Document orderInput = XMLUtil.createDocument(ELE_ORDER);
        Element orderEle = orderInput.getDocumentElement();
        orderEle.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
        orderEle.setAttribute(ATTR_DOCUMENT_TYPE, documentType);
        Document orderListOutputDoc = VSIUtils.invokeAPI(env,TEMPLATE_GET_ORDER_LIST_CREATE_SHIPMENT,API_GET_ORDER_LIST, orderInput);
        Element orderListEle = (Element) orderListOutputDoc.getDocumentElement().getElementsByTagName(ELE_ORDER).item(0);
        Document createShipmentInput = XMLUtil.createDocument(ELE_SHIPMENT);
        Element eleShipment = createShipmentInput.getDocumentElement();
        eleShipment.setAttribute(ATTR_DOCUMENT_TYPE, orderReleaseElement.getAttribute(ATTR_DOCUMENT_TYPE));
        eleShipment.setAttribute(ATTR_ORDER_NO, orderListEle.getAttribute(ATTR_ORDER_NO));
        eleShipment.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
        eleShipment.setAttribute(ATTR_SCAC, orderReleaseElement.getAttribute(ATTR_SCAC));
        eleShipment.setAttribute(ATTR_SHIP_NODE, orderReleaseElement.getAttribute(ATTR_SHIP_NODE));
        Element eleShipmentLines = createShipmentInput.createElement(ELE_SHIPMENT_LINES);
        for(int i=0; i< orderLineNode.getLength(); i++)
        {
            Element orderLineElem = (Element) orderReleaseElement.getElementsByTagName(ELE_ORDER_LINE).item(i);
            Element itemEle = (Element) orderLineElem.getElementsByTagName(ELE_ITEM).item(0);
            Element orderStatusesEle = (Element) orderLineElem.getElementsByTagName(ELE_ORDER_STATUSES).item(0);
            Element orderStatusEle = (Element) orderStatusesEle.getElementsByTagName(ELE_ORDER_STATUS).item(0);
            Element eleShipmentLine = SCXmlUtil.createChild(eleShipmentLines, ELE_SHIPMENT_LINE);
            eleShipmentLine.setAttribute(ATTR_ITEM_ID, itemEle.getAttribute(ATTR_ITEM_ID));
            eleShipmentLine.setAttribute(ATTR_ORDER_LINE_KEY, orderLineElem.getAttribute(ATTR_ORDER_LINE_KEY));
            eleShipmentLine.setAttribute(ATTR_PRIME_LINE_NO, orderLineElem.getAttribute(ATTR_PRIME_LINE_NO));
            eleShipmentLine.setAttribute(ATTR_SUB_LINE_NO, orderLineElem.getAttribute(ATTR_SUB_LINE_NO));
            eleShipmentLine.setAttribute(ATTR_QUANTITY, orderStatusEle.getAttribute(ATTR_STATUS_QUANTITY));           
            eleShipmentLine.setAttribute(ATTR_ORDER_RELEASE_KEY, orderReleaseElement.getAttribute(ATTR_ORDER_RELEASE_KEY));           
            eleShipmentLine.setAttribute(ATTR_RELEASE_NO, orderReleaseElement.getAttribute(ATTR_RELEASE_NO));
            eleShipmentLines.appendChild(eleShipmentLine);
        }
        eleShipment.appendChild(eleShipmentLines);
        log.info("createShipmentInput => " +XMLUtil.getXMLString(createShipmentInput));
        Document docCreateShipment = VSIUtils.invokeAPI(env, API_CREATE_SHIPMENT,createShipmentInput);
        log.info("docCreateShipment => " +XMLUtil.getXMLString(docCreateShipment));
        }
        catch(Exception e)
        {
            log.info("Exception in createShipmentOnSendRelease() as below => ");
            e.printStackTrace();
        }
    }
	public void manageTaskQ(YFSEnvironment env, String taskQKeyValue)
	{
		try
		{
		Document regInput = SCXmlUtil.createDocument(ELE_REGISTER_PROCESS_COMPLETION_INPUT);
		Element regInputEle = regInput.getDocumentElement();
		regInputEle.setAttribute(ATTR_KEEPTASKOPEN, FLAG_N);
		Element currentTask = regInput.createElement(ATTR_CURRENTTASK);
		currentTask.setAttribute(ATTR_TASKQKEY, taskQKeyValue);
		regInputEle.appendChild(currentTask);
		VSIUtils.invokeAPI(env, API_REGISTER_PROCESS_COMPLETION, regInput);
		}
		catch(Exception e)
		{
			log.info("Exception in manageTaskQ() as below => ");
			e.printStackTrace();
		}
	}
}