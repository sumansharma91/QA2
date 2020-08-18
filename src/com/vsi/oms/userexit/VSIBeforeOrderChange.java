package com.vsi.oms.userexit;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIBeforeOrderChange implements VSIConstants{
	public Properties prop = null;
	private YFCLogCategory log = YFCLogCategory.instance(VSIBeforeOrderChange.class);
	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	
	public Document beforeOrderChange(YFSEnvironment env, Document inXML) throws YFSException,Exception{
		
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside beforeOrderChange================================");
		}
		String	poBoxPatternString = ".*[Pp][ ]*[.]?[ ]*[Oo][ ]*[-.]?[ ]*([Bb][Oo][Xx])+.*";
		Element elePersonInfoshipToOutput =  null;
		Element eleOrder = inXML.getDocumentElement();
		//OMS-1767-Start
		//eleOrder.setAttribute("SCAC", "UPSN");
		//OMS-1767-End
		updatePipeline(env, inXML);
		if(eleOrder.hasAttribute("OptmizationType"))
		{
			eleOrder.removeAttribute("OptmizationType");
		}
		Element orderElement = null;
		if(!YFCObject.isNull(eleOrder)){
			
			//creating input for getOrderList API call
			Document outdoc = null;
			Document getOrderListInput = null;
			getOrderListInput=SCXmlUtil.createDocument("Order");
			boolean isPOBox = false;
			Element eleOrderElement = getOrderListInput.getDocumentElement();
			eleOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
			eleOrderElement.setAttribute(VSIConstants.ATTR_MAX_RECORDS, "1");
			
			outdoc = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_BEFOREORDERCHANGE_GETORDERLIST, VSIConstants.API_GET_ORDER_LIST, getOrderListInput);
			String orderType = SCXmlUtil.getXpathAttribute(outdoc.getDocumentElement(), "/OrderList/Order/@OrderType");
			//OMS-1799 start
			if(!YFCCommon.isStringVoid(orderType)&&!(VSIConstants.WHOLESALE).equalsIgnoreCase(orderType)){
				eleOrder.setAttribute("SCAC", "UPSN");
			}
				//OMS-1799 End
			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
			if(!YFCObject.isNull(eleOrderLines)){
				Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
				ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
				String sDeliveryMethod = SCXmlUtil.getAttribute(eleOrderLine, VSIConstants.ATTR_DELIVERY_METHOD);
				String sLineType = SCXmlUtil.getAttribute(eleOrderLine,VSIConstants.ATTR_LINE_TYPE);
				String sShipNode = SCXmlUtil.getAttribute(eleOrderLine,VSIConstants.ATTR_SHIP_NODE);
				if(!YFCObject.isVoid(eleOrderLine)){
						//getting personinfoshipto from OrderLine and stamping it on OrderLevel
				Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
						if(sDeliveryMethod.equals("SHP")){
							//Checking for personInfoShipTo being void
							if(!YFCObject.isVoid(elePersonInfoShipTo)){
								
								String sCountry = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_COUNTRY);
								//checking for country being void
								if(!YFCObject.isVoid(sCountry)){
									//creating input to call getCommonCodelist API
									Document docgetCommonCodeInput = SCXmlUtil.createDocument("CommonCode");
						    		Element eleCommonCodeElement = docgetCommonCodeInput.getDocumentElement();
						    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_RESTRICT_COUNTRY");
						    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, sCountry);
						    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_ORG_CODE, "DEFAULT");
						    		
						    		try {
						    			
										Document docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
										Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
										if(commonCodeListElement.hasChildNodes()){
											Element commonCodeElement = SCXmlUtil.getChildElement(commonCodeListElement, VSIConstants.ELE_COMMON_CODE);
											String sCodeValue = SCXmlUtil.getAttribute(commonCodeElement, VSIConstants.ATTR_CODE_VALUE);
											if(!YFCObject.isVoid(sCodeValue) && sCodeValue.equals(sCountry)){
											
												 throw new YFSException(
															"EXTN_ERROR",
															"EXTN_ERROR",
															"Invalid Ship To Country");
												 
												//throw new YFSUserExitException("Invalid Ship To Country");
											
											}//end of if for checking country is restricted or npot
											
										}//checking for CommonCodeList has child nodes or not
										
										
									} catch (YIFClientCreationException e) {
										
										e.printStackTrace();
										throw new YFSException("EXTN_ERROR",
												"EXTN_ERROR",
												"System error");
									}//end of catch
									
						    		
						    		if(!sCountry.equals("US")){
						    			SCXmlUtil.setAttribute(eleOrder, VSIConstants.ATTR_SOURCING_CLASSIFICATION, "INTERNATIONAL");
						    		}// end of if for checking US
						    		else if(sCountry.equals("US")){
						    			
						    			String sZipCode = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ZIPCODE);
						    			String addressLine1 = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDRESS1);
								 		String addressLine2 = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDRESS2);
								 		// If AddressLine1 or AddressLine2 contain "PO Box", set AddressLine6 = Y
								 		if(Pattern.matches(poBoxPatternString, addressLine1)
				 								|| Pattern.matches(poBoxPatternString, addressLine2)){
								 			isPOBox = true; 
								 		}
								 		
						    			eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_APO_FPO_ZIP");
							    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, sZipCode);
							    		
							    		try {
											Document docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
											Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
											if(commonCodeListElement.hasChildNodes()){
												
												SCXmlUtil.setAttribute(eleOrder, VSIConstants.ATTR_SOURCING_CLASSIFICATION, "APO_FPO");
											}//end of if for checking commonCodeListElement has Child Nodes
											
										} catch (YFSException | RemoteException | YIFClientCreationException e) {
											
											e.printStackTrace();
											throw new YFSException();
										}
						    		}//end of else if
						    		else{
						    			SCXmlUtil.setAttribute(eleOrder, VSIConstants.ATTR_SOURCING_CLASSIFICATION, " ");
						    		}//end of else
									
								}//end of checking wether country is void
								
								
								
							}//end of checking PersonInfoShipTo
							
							
						}//end of if for checking Delivery Method 
						
						String sState = null;
						String sCountry = null;
						String sZipCode = null;
						String shipToKey = null;
						if(!YFCObject.isNull(outdoc)){
						Element orderListElement = outdoc.getDocumentElement();
						orderElement = SCXmlUtil.getChildElement(orderListElement, VSIConstants.ELE_ORDER);
						shipToKey = orderElement.getAttribute("ShipToKey");
						elePersonInfoshipToOutput =  SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
						if(!YFCObject.isNull(elePersonInfoshipToOutput)){
							sState = elePersonInfoshipToOutput.getAttribute(VSIConstants.ATTR_STATE);
							sCountry = elePersonInfoshipToOutput.getAttribute(VSIConstants.ATTR_COUNTRY);
							sZipCode = elePersonInfoshipToOutput.getAttribute(VSIConstants.ATTR_ZIPCODE);
						}//end of checking output personinfoshipto				
					
					//Checking for changeAddress
					//OMS-1647 start
					if(sDeliveryMethod.equals("PICK") && (VSIConstants.LINETYPE_PUS.equals(sLineType)||VSIConstants.LINETYPE_STS.equals(sLineType))){
						Document getShipNodeListIn = null;
						getShipNodeListIn=SCXmlUtil.createDocument("ShipNode");
						Element eleShipNodeEle = getShipNodeListIn.getDocumentElement();
						eleShipNodeEle.setAttribute(VSIConstants.ATTR_SHIP_NODE,sShipNode);
						eleShipNodeEle.setAttribute(VSIConstants.ATTR_MAX_RECORDS, "1");
						
						Document shipNodeDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetShipNodeList.xml","getShipNodeList", getShipNodeListIn);
						String strState = "";
						String strCountry = "";
						String strZipCode = "";
						if(!YFCObject.isNull(shipNodeDoc)){
							Element eleShipNode = SCXmlUtil.getChildElement(shipNodeDoc.getDocumentElement(), "ShipNode");
							// get the ship node address and check for restriction for each of the item

							Element eleStorePersonInfo = SCXmlUtil.getChildElement(eleShipNode, VSIConstants.ELE_SHIP_NODE_PERSON);
							
							if(!YFCObject.isVoid(eleStorePersonInfo)){
								strState = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_STATE);
								strCountry = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_COUNTRY);
								strZipCode = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_ZIPCODE);
							}
						}
					
						
						Element eleOrderLinesOutput = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);

						Element eleOrderLineOutput = SCXmlUtil.getChildElement(eleOrderLinesOutput, VSIConstants.ELE_ORDER_LINE);
							if(!YFCObject.isNull(eleOrderLineOutput)){
								ArrayList<Element> alOrderLinesOutput = SCXmlUtil.getChildren(eleOrderLinesOutput, VSIConstants.ELE_ORDER_LINE);
									 StringBuffer sBuffer = new StringBuffer();
									 int i = 0;
									for(Element eleOrderLineOutput1:alOrderLinesOutput){
										i++;
													Element ItemElement = SCXmlUtil.getChildElement(eleOrderLineOutput1, VSIConstants.ELE_ITEM);
													String itemid = ItemElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
													if(log.isDebugEnabled()){
														log.verbose("Printing itemid XML :" + itemid);
														log.debug("Printing itemid XML :" + itemid);
													}
													
													Document inDocItemList = createInputForGetItemList(itemid);
													if(!YFCCommon.isVoid(strState) && !YFCCommon.isVoid(strCountry)
															&& !YFCCommon.isVoid(strZipCode)){
//													Document outdoc1 = vsiRestrictedItemCheck(inDocItemList, env,strState,strCountry,strZipCode,itemid);//Checking for Restricted Item
												     if(vsiRestrictedItemCheck(inDocItemList, env,strState,strCountry,strZipCode,itemid)){
												    	 sBuffer.append(itemid);
												    	 if(i<alOrderLines.size())
												         {
												    		 sBuffer.append(',');
												         } 
												        												         
												     }//end of checking output of restricted item..
												}
								        }//end of for for looping through output OrderLine	
							         if(sBuffer.length() > 0){
							        	 
							        	 throw new YFSException(
													"EXTN_ERROR",
													"EXTN_ERROR",
													"Store cannot be selected due to following state law restricted item(s): " +sBuffer.toString());
								         
							         }//end of checking string Buffer										
									
								}// end of checking input and output state and country
							//end of checking output shipTo address
						}//end of checking Output OrderLine
						//OMS-1647 end
					if(sDeliveryMethod.equals("PICK") && VSIConstants.LINETYPE_STS.equals(sLineType))
					{												
						//Element eleOrderLinesOutput = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
						
						//creating input for getOrderList API call
						//Document shipNodeListIn = null;
						Document getShipNodeListIn = null;
						getShipNodeListIn=SCXmlUtil.createDocument("ShipNode");
						Element eleShipNodeEle = getShipNodeListIn.getDocumentElement();
						eleShipNodeEle.setAttribute(VSIConstants.ATTR_SHIP_NODE,sShipNode);
						eleShipNodeEle.setAttribute(VSIConstants.ATTR_MAX_RECORDS, "1");
						
						Document shipNodeDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetShipNodeList.xml","getShipNodeList", getShipNodeListIn);
						String strState = "";
						String strCountry = "";
						String strZipCode = "";
						if(!YFCObject.isNull(shipNodeDoc)){
							Element eleShipNode = SCXmlUtil.getChildElement(shipNodeDoc.getDocumentElement(), "ShipNode");
							// get the ship node address and check for restriction for each of the item
							//Element eleStore = SCXmlUtil.getChildElement(eleOrderLineOutput, "Shipnode");
							Element eleStorePersonInfo = SCXmlUtil.getChildElement(eleShipNode, VSIConstants.ELE_SHIP_NODE_PERSON);
							
							if(!YFCObject.isVoid(eleStorePersonInfo)){
								strState = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_STATE);
								strCountry = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_COUNTRY);
								strZipCode = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_ZIPCODE);
							}
						}
						StringBuffer strBuffer = new StringBuffer();
						int i=0;
						 //int i = 0;
						for(Element eleOrderLine1:alOrderLines){
							 i++;
							//String strMinLineStatus = eleOrderLineOutput1.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
							//if(!YFCObject.isNull(strMinLineStatus) && !strMinLineStatus.equals("9000")){
									//String sAction = eleOrderLineOutput1.getAttribute(VSIConstants.ATTR_ACTION);
									//if(!sAction.equals("DELETE") || !sAction.equals("CANCEL")){
										
										//Element ItemElement = SCXmlUtil.getChildElement(eleOrderLine1, VSIConstants.ELE_ITEM);
										String itemid = eleOrderLine1.getAttribute(VSIConstants.ATTR_ITEM_ID);
										if(!YFCCommon.isVoid(itemid))
										{
											Document inDocItemList = createInputForGetItemList(itemid);
											if(!YFCCommon.isVoid(strState) && !YFCCommon.isVoid(strCountry)
													&& !YFCCommon.isVoid(strZipCode)){
//												Document outdoc1 = vsiRestrictedItemCheck(inDocItemList, env,strState,strCountry,strZipCode,itemid);//Checking for Restricted Item
											     if(vsiRestrictedItemCheck(inDocItemList, env,strState,strCountry,strZipCode,itemid)){
											    	 strBuffer.append(itemid);
											         if(i<alOrderLines.size())
											         {
											        	 strBuffer.append(',');
											         } 
											     }//end of checking output of restricted item.
											}
										}
										
									//}//end of if checking for Action
							//}//end of check strMinLineStatus
						}//end of for for looping through output OrderLine	
				         if(strBuffer.length() > 0){
				        	 
				        	 throw new YFSException(
										"EXTN_ERROR",
										"EXTN_ERROR",
										"Store cannot be selected due to following state law restricted item(s):" +strBuffer.toString());
					         
				         }//end of checking string Buffer														
					}
						//Checking for changeAddress
					if(sDeliveryMethod.equals("SHP")){
						String strState = null;
						String strCountry = null;
						String strZipCode = null;
						Element eleOrderLinesOutput = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
						
						
						if(!YFCObject.isNull(eleOrderLinesOutput)){
							Element eleOrderLineOutput = SCXmlUtil.getChildElement(eleOrderLinesOutput, VSIConstants.ELE_ORDER_LINE);
								if(!YFCObject.isNull(eleOrderLineOutput)){
								if(!YFCObject.isVoid(elePersonInfoShipTo)){
									strState = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_STATE);
									strCountry = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_COUNTRY);
									strZipCode = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_ZIPCODE);
								}
								
									if((!YFCObject.isNull(strState) && !sState.equals(strState)) || (!YFCObject.isNull(strCountry) && !sCountry.equals(strCountry) 
											|| (!YFCObject.isNull(strZipCode) && !sZipCode.equals(strZipCode)))){
										ArrayList<Element> alOrderLinesOutput = SCXmlUtil.getChildren(eleOrderLinesOutput, VSIConstants.ELE_ORDER_LINE);
										 StringBuffer sBuffer = new StringBuffer();
										 int i = 0;
										for(Element eleOrderLineOutput1:alOrderLinesOutput){
											i++;
											//String strMinLineStatus = eleOrderLineOutput1.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
											//if(!YFCObject.isNull(strMinLineStatus) && !strMinLineStatus.equals("9000")){
													//String sAction = eleOrderLineOutput1.getAttribute(VSIConstants.ATTR_ACTION);
													//if(!sAction.equals("DELETE") || !sAction.equals("CANCEL")){
														
														Element ItemElement = SCXmlUtil.getChildElement(eleOrderLineOutput1, VSIConstants.ELE_ITEM);
														String itemid = ItemElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
														
														Document inDocItemList = createInputForGetItemList(itemid);
//														Document outdoc1 = vsiRestrictedItemCheck(inDocItemList, env,strState,strCountry,strZipCode,itemid);//Checking for Restricted Item
													     if(vsiRestrictedItemCheck(inDocItemList, env,strState,strCountry,strZipCode,itemid)){
													    	 sBuffer.append(itemid);
													    	 if(i<alOrderLines.size())
													         {
													    		 sBuffer.append(',');
													         } 
													        // sBuffer.append(',');
													         
													     }//end of checking output of restricted item..
													//}//end of if checking for Action
											//}//end of check strMinLineStatus
										}//end of for for looping through output OrderLine	
								         if(sBuffer.length() > 0){
								        	 
								        	 throw new YFSException(
														"EXTN_ERROR",
														"EXTN_ERROR",
														"Address cannot be added due to following state law restricted item(s) " +sBuffer.toString());
									         
								         }//end of checking string Buffer										
										
									}// end of checking input and output state and country
								//end of checking output shipTo address
							}//end of checking Output OrderLine
						}//end of checking Output OrderLines
						//Checking for addition of new OrderLine
					StringBuffer strBuffer = new StringBuffer();
					int i=0;
					for(Element eleOrderLine1:alOrderLines){
						i++;
						if(log.isDebugEnabled()){
							log.debug("Inside input OrderLines");
						}
						//String strDeliveryMethod = eleOrderLine1.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
						double dOrderedQuantity = SCXmlUtil.getDoubleAttribute(eleOrderLine1, VSIConstants.ATTR_ORD_QTY);
						String sAction = eleOrderLine1.getAttribute(VSIConstants.ATTR_ACTION);
						
						Element ItemElement = SCXmlUtil.getChildElement(eleOrderLine1, VSIConstants.ELE_ITEM);
						if(!YFCObject.isNull(ItemElement)){
						String itemid = ItemElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
						
						
						
						if(dOrderedQuantity > 0){
							//System.out.println("outisde cancel"+sAction);
							if(!YFCObject.isNull(itemid) && !sAction.equals("CANCEL") && !sAction.equals("REMOVE"))
							{
								//System.out.println("Inside cancel");
								Document inDocItemList = createInputForGetItemList(itemid);
								Document outDoc = null;
								Document outputRst = null;
								outDoc = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ITEM_LIST, VSIConstants.API_GET_ITEM_LIST, inDocItemList);
								 Element eleItemList = outDoc.getDocumentElement();
									
								 Element eleItem1 = SCXmlUtil.getChildElement(eleItemList, VSIConstants.ELE_ITEM);	
								 Element eleExtn = SCXmlUtil.getChildElement(eleItem1, VSIConstants.ELE_EXTN);			  
								 String strExtnIsRestrcitedItem = eleExtn.getAttribute("ExtnIsRestrictedItem");
								 String strExtnIsSignReqdItem = eleExtn.getAttribute("ExtnIsSignReqdItem");
								 
								if(!YFCObject.isNull(strExtnIsRestrcitedItem) && strExtnIsRestrcitedItem.equals("Y")){
									 Document docRestrictedItem = XMLUtil.createDocument("VSIShipRestrictedItem");
									 Element eleRestricted=docRestrictedItem.getDocumentElement();
									 eleRestricted.setAttribute(VSIConstants.ATTR_STATE, sState);
									 eleRestricted.setAttribute(VSIConstants.ATTR_COUNTRY, sCountry);
									 eleRestricted.setAttribute(VSIConstants.ATTR_ZIPCODE, sZipCode);
									 eleRestricted.setAttribute(VSIConstants.ATTR_ITEM_ID, itemid);
									 
//									 outputRst= VSIUtils.invokeService(env,"VSIGetShipRestrictedItem", docRestrictedItem);
								     
									 if(vsiRestrictedItemCheck(inDocItemList, env,sState,sCountry,sZipCode,itemid)){
								         strBuffer.append(itemid);
								         if(i<alOrderLines.size())
								         {
								        	 strBuffer.append(',');
								         } 
								     }//end of checking output of restricted item..
								 }//end of checking strExtnIsRestrcitedItem
	
									else if(!YFCObject.isNull(strExtnIsSignReqdItem) && strExtnIsSignReqdItem.equals("Y")){
											Document docVSISignatureRequiredItemListOutput = null;
											
											//creating document to invoke VSISignatureRequiredItemList service 
											//String sState = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_STATE);
											Document docVSISignatureRequiredItemInput  = SCXmlUtil.createDocument("VSISignatureRequiredItem");
											Element eleVSISignatureRequiredItem = docVSISignatureRequiredItemInput.getDocumentElement();
											SCXmlUtil.setAttribute(eleVSISignatureRequiredItem, VSIConstants.ATTR_ITEM_ID, itemid);
											SCXmlUtil.setAttribute(eleVSISignatureRequiredItem, VSIConstants.ATTR_ORG_CODE, "VSI-Cat" );		
											SCXmlUtil.setAttribute(eleVSISignatureRequiredItem, VSIConstants.ATTR_STATE, sState);
											
											
											docVSISignatureRequiredItemListOutput = VSIUtils.invokeService(env,"VSISignatureRequiredItemList", docVSISignatureRequiredItemInput);
											Element eleVSISignatureRequiredItemList = docVSISignatureRequiredItemListOutput.getDocumentElement();
											if(!YFCObject.isNull(eleVSISignatureRequiredItemList)){
												Element eleVSISignatureRequiredItem1 = SCXmlUtil.getChildElement(eleVSISignatureRequiredItemList, "VSISignatureRequiredItem");
												String sExtn21SignReqd = SCXmlUtil.getAttribute(eleVSISignatureRequiredItem1, "Extn21SignReqd");
												String sExtn18SignReqd = SCXmlUtil.getAttribute(eleVSISignatureRequiredItem1, "Extn18SignReqd");
												
												Element extnElement1 = SCXmlUtil.createChild(eleOrderLine1, VSIConstants.ELE_EXTN); 
												if(!YFCObject.isVoid(sExtn21SignReqd) && sExtn21SignReqd.equals("Y")){
													
													SCXmlUtil.setAttribute(extnElement1, "ExtnSignatureType", "A");
													
												}//end of checking sExtn21SignReqd
												else if(!YFCObject.isVoid(sExtn18SignReqd) && sExtn18SignReqd.equals("Y")){
													
													SCXmlUtil.setAttribute(extnElement1, "ExtnSignatureType", "Y");
													
												}//end of checking sExtn18SignReqd
											}//end of if for checking eleVSISignatureRequiredItemList
										 
									 }//end of checking signatureReqItem
							}//end of checking itemid and action in input
						}//end of if for checking OrderedQty
						}//end of input ItemElement Check
					}//end of for looping through input OrderLines
					//end of checking string Buffer	
					if(strBuffer.length()> 0){
			        	 throw new YFSException(
								"EXTN_ERROR",
								"EXTN_ERROR",
								"Item(s) " + strBuffer.toString() + " cannot be shipped to this location due to state law.");
			         }
				}//end of ship to home
			}//check for outdoc
				
				boolean isSTH = false;
				boolean isPick = false;
				boolean importAddressToHeader = false;
				for(Element eleOrderLine1:alOrderLines){
					if(log.isDebugEnabled()){
						log.debug("Inside input OrderLines");
					}
					String strDeliveryMethod = eleOrderLine1.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
					String strFulfillmentType = eleOrderLine1.getAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE);
					elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine1, "PersonInfoShipTo");
					if(!YFCCommon.isVoid(elePersonInfoShipTo))
					{
						if(isPOBox)
						{
							elePersonInfoShipTo.setAttribute("AddressLine6", "Y");
						}
						importAddressToHeader = true;
						
					}					
					//String lineType = eleOrderLine1.getAttribute(VSIConstants.ATTR_LINE_TYPE);
					// Only if input has @DeliveryMethod, set isSTH or isPick flag values
					if (strDeliveryMethod.equalsIgnoreCase("SHP"))
					{
						
						isSTH = true;
						eleOrderLine1.setAttribute(VSIConstants.ATTR_LINE_TYPE, "SHIP_TO_HOME");
						eleOrderLine1.setAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE, "SHIP_TO_HOME");
						
					}else if(strDeliveryMethod.equalsIgnoreCase("PICK")){
						isPick = true;
						if(!"SHIP_TO_STORE".equals(strFulfillmentType))
						{
							eleOrderLine1.setAttribute(VSIConstants.ATTR_LINE_TYPE, "PICK_IN_STORE");
							eleOrderLine1.setAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE, "PICK_IN_STORE");
						}
						
					}				
					
					Element ItemElement = SCXmlUtil.getChildElement(eleOrderLine1, VSIConstants.ELE_ITEM);
					if(!YFCObject.isNull(ItemElement)){
					String itemid = ItemElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
					if (strDeliveryMethod.equalsIgnoreCase("SHP") && !YFCCommon.isVoid(eleOrderLine1.getAttribute("TransactionalLineId"))){
						
						if(!YFCCommon.isVoid(shipToKey))
						{
							eleOrderLine1.setAttribute("ShipToKey", shipToKey);
						}
						
						if(log.isDebugEnabled()){
							log.debug("Inside GC Line Capture");
						}
						Document docgetItemListInput = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
						SCXmlUtil.setAttribute(docgetItemListInput.getDocumentElement(), VSIConstants.ATTR_ITEM_ID, itemid);
						//eleOrderLine1.setAttribute("CarrierServiceCode","STANDARD");
						Document docgetItemListOutput = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_GET_ITEM_LIST,VSIConstants.API_GET_ITEM_LIST, docgetItemListInput);
						if (docgetItemListOutput.getDocumentElement().hasChildNodes()==true){
						String attrExtnItemType = SCXmlUtil.getChildElement(SCXmlUtil.getChildElement(docgetItemListOutput.getDocumentElement(), VSIConstants.ELE_ITEM), VSIConstants.ELE_EXTN).getAttribute(VSIConstants.ATTR_EXTN_ITEM_TYPE);
							if(attrExtnItemType.equalsIgnoreCase(VSIConstants.GIFT_CARD)||attrExtnItemType.equalsIgnoreCase(VSIConstants.GIFT_CARD_VAR)){
								eleOrderLine1.setAttribute(VSIConstants.ATTR_SHIP_NODE,VSIConstants.SHIP_NODE_CC);
								eleOrderLine1.setAttribute(VSIConstants.ATTR_IS_FIRM_PRE_NODE, VSIConstants.FLAG_Y);
							}
						}
						
						//stamping SCAC at line level
						String sSCAC = orderElement.getAttribute("SCAC");
						if(!YFCObject.isVoid(sSCAC)){
							eleOrderLine1.setAttribute("SCAC",sSCAC);
							eleOrder.setAttribute("SCAC","");
							}
						else{
							Element eleOrderLinesOutput = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
							if(!YFCObject.isVoid(eleOrderLinesOutput)){
								if(eleOrderLinesOutput.hasChildNodes()){
									
									Element eleOrderLineOutput = SCXmlUtil.getChildElement(eleOrderLinesOutput, VSIConstants.ELE_ORDER_LINE);
									String strSCAC = eleOrderLineOutput.getAttribute("SCAC");
									eleOrderLine1.setAttribute("SCAC",strSCAC);
								}
							}
						}
						
						
					}//End of GC Line Capture 
					
					
					}
				}
				
				if(importAddressToHeader)
				{
					//stamping personinfoshipTo from OrderLine to Order Level
					SCXmlUtil.importElement(eleOrder, elePersonInfoShipTo);
				}
				
				// Checking values of both flags to reset AllocationRuleID
				if(isSTH)
				{					
					if(!YFCObject.isVoid(elePersonInfoshipToOutput)){
						sCountry = elePersonInfoshipToOutput.getAttribute(VSIConstants.ATTR_COUNTRY);
					}
					
					//checking for country being void
					if(!YFCObject.isVoid(sCountry)){
						//creating input to call getCommonCodelist API
						Document docgetCommonCodeInput = SCXmlUtil.createDocument("CommonCode");
			    		Element eleCommonCodeElement = docgetCommonCodeInput.getDocumentElement();
			    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_RESTRICT_COUNTRY");
			    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, sCountry);
			    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_ORG_CODE, "DEFAULT");
			    		if(!sCountry.equals("US")){
			    			SCXmlUtil.setAttribute(eleOrder, VSIConstants.ATTR_SOURCING_CLASSIFICATION, "INTERNATIONAL");
			    		}// end of if for checking US
			    		else if(sCountry.equals("US")){
			    			
			    			String sZipCode1 = SCXmlUtil.getAttribute(elePersonInfoshipToOutput, VSIConstants.ATTR_ZIPCODE);
			    			String addressLine1 = SCXmlUtil.getAttribute(elePersonInfoshipToOutput, VSIConstants.ATTR_ADDRESS1);
					 		String addressLine2 = SCXmlUtil.getAttribute(elePersonInfoshipToOutput, VSIConstants.ATTR_ADDRESS2);
					 		// If AddressLine1 or AddressLine2 contain "PO Box", set AddressLine6 = Y
					 		if(Pattern.matches(poBoxPatternString, addressLine1)
	 								|| Pattern.matches(poBoxPatternString, addressLine2))
					 		{
					 			elePersonInfoshipToOutput.setAttribute("AddressLine6", "Y");
					 			SCXmlUtil.importElement(eleOrder, elePersonInfoshipToOutput);
					 		}
					 		
			    			eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_APO_FPO_ZIP");
				    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, sZipCode1);
				    		
				    		try {
								Document docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
								Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
								if(commonCodeListElement.hasChildNodes()){
									
									SCXmlUtil.setAttribute(eleOrder, VSIConstants.ATTR_SOURCING_CLASSIFICATION, "APO_FPO");
								}//end of if for checking commonCodeListElement has Child Nodes
								
							} catch (YFSException | RemoteException | YIFClientCreationException e) {
								
								e.printStackTrace();
								throw new YFSException();
							}
			    		}//end of else if
			    		else{
			    			SCXmlUtil.setAttribute(eleOrder, VSIConstants.ATTR_SOURCING_CLASSIFICATION, " ");
			    		}//end of else
			    		try {
			    			
							Document docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
							Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
							if(commonCodeListElement.hasChildNodes()){
								Element commonCodeElement = SCXmlUtil.getChildElement(commonCodeListElement, VSIConstants.ELE_COMMON_CODE);
								String sCodeValue = SCXmlUtil.getAttribute(commonCodeElement, VSIConstants.ATTR_CODE_VALUE);
								
								
								
								if(sCodeValue.equals(sCountry)){
								
									 throw new YFSException(
												"EXTN_ERROR",
												"EXTN_ERROR",
												"Invalid Ship To Country");
									 
									//throw new YFSUserExitException("Invalid Ship To Country");
								
								}//end of if for checking country is restricted or npot
								
							}//checking for CommonCodeList has child nodes or not
							
							
						}catch (YIFClientCreationException e) {
							
							e.printStackTrace();
							throw new YFSException("EXTN_ERROR",
									"EXTN_ERROR",
									"System error");
						}//end of catch
					}
			    	//Start OMS-1464
					if(!sCountry.equals("US"))
						SCXmlUtil.setAttribute(eleOrder, VSIConstants.ATTR_ALLOCATION_RULE_ID,"VSI_INT");
 					else if(!orderType.equalsIgnoreCase(VSIConstants.WHOLESALE))
 						SCXmlUtil.setAttribute(eleOrder, VSIConstants.ATTR_ALLOCATION_RULE_ID, "VSI_STH");
 					//End OMS-1464		
					//eleOrder.setAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID, "VSI_STH");
				}
				else if(isPick)
				{
					eleOrder.setAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID, "");
				}
			}//end of orderLine
			}	//end of checking OrderLines
			boolean	isPaypalOrder=false;
			boolean	hasActiveCreditCard=false;
			boolean	hasLiabilityTender=false;
			boolean hasVoucher=false;
			boolean hasGC=false;
			if(YFCObject.isVoid(orderElement)){
				orderElement = SCXmlUtil.getChildElement(outdoc.getDocumentElement(), VSIConstants.ELE_ORDER);
			}
			Element elePaymentMethods = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PAYMENT_METHODS);
			if(!YFCObject.isNull(elePaymentMethods)){
					Element paymentMethodsElement = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_PAYMENT_METHODS);
					Map<String, Element> mapPaymentMethod = new HashMap<>();
					if(!YFCObject.isNull(paymentMethodsElement)){
						ArrayList<Element> alPaymentMethodsElement = SCXmlUtil.getChildren(paymentMethodsElement, VSIConstants.ELE_PAYMENT_METHOD);
						//Looping through Payment methods of output of getOrderList
						for(Element PaymentMethodElement:alPaymentMethodsElement){
							String strPaymentType = PaymentMethodElement.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
							String strSuspendAnyMoreCharges = PaymentMethodElement.getAttribute(VSIConstants.ATTR_SUSPEND_ANYMORE_CHARGES);
							mapPaymentMethod.put(PaymentMethodElement.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE), PaymentMethodElement);
							if(!YFCObject.isNull(strPaymentType) && strPaymentType.equals(VSIConstants.STR_PAYPAL) && !"Y".equals(strSuspendAnyMoreCharges)){
								isPaypalOrder = true;
							}//end of if for paypal
							if(!YFCObject.isNull(strPaymentType) && strPaymentType.equals(VSIConstants.STR_CREDIT_CARD) && !"Y".equals(strSuspendAnyMoreCharges)){
								hasActiveCreditCard = true;
							}//end of if for ActiveCreditCard
							if(!YFCObject.isNull(strPaymentType) 
									&& ((strPaymentType.equals(VSIConstants.STR_CASH) || strPaymentType.equals(VSIConstants.STR_CHECK) || strPaymentType.equals(VSIConstants.STR_AR_CREDIT))
											&& !"Y".equals(strSuspendAnyMoreCharges))){
								hasLiabilityTender = true;
							}//end of if for hasLiabilityTender
							if(!YFCObject.isNull(strPaymentType) && strPaymentType.equals(VSIConstants.STR_VOUCHERS)
									&& !"Y".equals(strSuspendAnyMoreCharges)){
								hasVoucher = true;
							}//end of if for hasLiabilityTender
							if(!YFCObject.isNull(strPaymentType) && (strPaymentType.equals(VSIConstants.GIFT_CARD) 
									|| strPaymentType.equals("ONLINE_GIFT_CARD")) && !"Y".equals(strSuspendAnyMoreCharges)){
								hasGC = true;
							}//end of if for hasLiabilityTender
							
							
						}//end of for for Payment method from output of getOrderList
					}//end of if checking paymentMethods from outDoc 
					
					//Looping through Payment methods of inputXML
					ArrayList<Element> alPaymentMethods = SCXmlUtil.getChildren(elePaymentMethods, VSIConstants.ELE_PAYMENT_METHOD);
					for(Element elePaymentMethod:alPaymentMethods){
						String strOperation = elePaymentMethod.getAttribute(VSIConstants.ATTR_OPERATION);
						String strEditPaymentMethod = elePaymentMethod.getAttribute("EditPaymentMethod");
						elePaymentMethod.removeAttribute("EditPaymentMethod");
						//System.out.println("EditPaymentMethod:::"+strEditPaymentMethod);
						if(!YFCObject.isNull(strOperation) && !strOperation.equals("Delete") && !"Y".equals(strEditPaymentMethod)){
							
							String strInputPaymentType = elePaymentMethod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
							if(!YFCObject.isNull(strInputPaymentType)){
									
									if(VSIConstants.STR_CREDIT_CARD.equals(strInputPaymentType)){
										if(hasActiveCreditCard == true){
											throw new YFSException("EXTN_ERROR",
													"EXTN_ERROR","Only one active credit card is allowed on an order. Please remove / suspend the existing credit card before adding a new card.");
										}
										if(isPaypalOrder == true){
											throw new YFSException("EXTN_ERROR",
													"EXTN_ERROR","Credit card cannot be added to Paypal order");
										}//end of isPaypalOrder
										if(hasLiabilityTender == true){
											throw new YFSException("EXTN_ERROR",
													"EXTN_ERROR","A credit card cannot be added to an order with Cash, Check or AR_Credit tenders");
										}//end of hasLiabilityTender
									}//end of creditcard
									if(VSIConstants.STR_CASH.equals(strInputPaymentType) 
											|| VSIConstants.STR_CHECK.equals(strInputPaymentType) 
											|| VSIConstants.STR_AR_CREDIT.equals(strInputPaymentType)){
										if(isPaypalOrder == true || hasLiabilityTender == true || hasActiveCreditCard == true || hasVoucher == true){
											throw new YFSException("EXTN_ERROR",
													"EXTN_ERROR","This tender cannot be added to this order");
										}//end of 
										
									}//end of CASH, CHECK, AR_CREDIT
									if(VSIConstants.STR_VOUCHERS.equals(strInputPaymentType))
									{
										//START - Commented for loyalty changes. Allowing multiple VOUCHERS
										/*
										if(!hasActiveCreditCard && !hasGC
												isPaypalOrder || hasLiabilityTender || hasVoucher ){
											throw new YFSException("EXTN_ERROR",
													"EXTN_ERROR","Vouchers cannot be added to this order");
										}
										*/
										//END
									}//end of VOUCHERS
									if(VSIConstants.GIFT_CARD.equals(strInputPaymentType)){
										if(hasLiabilityTender == true){
											throw new YFSException("EXTN_ERROR",
													"EXTN_ERROR","A gift card cannot be added to an order with Cash, Check or AR_Credit tenders");
											
										}//end of check hasLiabilityTender
										
									}//end of GIFT_CARD
							}//end of if for checking input PaymentType
						}//end of if for strOperation
						
					}//end of for input PaymentMethod
						
				}//end of if checking PaymentMethods has Child nodes
		}//end of if for checking Order
		
		return inXML;
		
	}//end of method
	
	/**
	 * 
	 * @param itemID
	 * @return
	 */
	private Document createInputForGetItemList(String itemID){
		// Create a new document with root element as OrderInvoiceDetail
				Document docInput = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
				Element eleItem = docInput.getDocumentElement();
				eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID,itemID);
				eleItem.setAttribute("OrganizationCode","VSI-Cat");
				eleItem.setAttribute("UnitOfMeasure","EACH");
				return docInput;
	}
	
	/**
	 * 
	 * @param inDocItemList
	 * @param env
	 * @param sState
	 * @param sCountry
	 * @param itemid
	 * @param sZipCode
	 * @return
	 * @throws Exception
	 */
	
	private boolean vsiRestrictedItemCheck(Document inDocItemList,
			YFSEnvironment env, String sState, String sCountry,
			String sZipCode, String itemid) throws Exception {
		Document outDoc = null;

		Document itemXML = XMLUtil.createDocument(VSIConstants.ELE_ITEM);
		Element eleItemInput = itemXML.getDocumentElement();
		eleItemInput.setAttribute(VSIConstants.ITEM_ID, itemid);
		eleItemInput.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, VSIConstants.ENT_VSI_CAT);
		eleItemInput.setAttribute(VSIConstants.ATTR_UOM, VSIConstants.UOM_EACH);
		eleItemInput.setAttribute(VSIConstants.ATTR_ITEM_GROUP_CODE, VSIConstants.ITEM_GROUP_CODE_PROD);

		outDoc = VSIUtils.invokeService(env, VSIConstants.SERVICE_GET_ITEM_LIST_WITH_SHIP_RESTRICTIONS, itemXML);
		Element eleItemList = outDoc.getDocumentElement();
		
		if(eleItemList.hasChildNodes()){
			Element eleItem = SCXmlUtil.getChildElement(eleItemList, VSIConstants.ELE_ITEM);
			Element eleExtn = SCXmlUtil.getChildElement(eleItem, VSIConstants.ELE_EXTN);
			if(!YFCObject.isVoid(eleExtn) && "Y".equals(eleExtn.getAttribute("ExtnIsRestrictedItem"))){
				
				Element eleRestrictionList = SCXmlUtil.getChildElement(eleExtn, 
						VSIConstants.ELE_VSI_SHIP_RESTRICTED_ITEM_LIST);
				if(!YFCObject.isVoid(eleRestrictionList)){
					
					if(eleRestrictionList.hasChildNodes()){
						// Get the state and country of the pick up store
						if(!YFCObject.isVoid(sState) && !YFCObject.isVoid(sCountry)){
							
							NodeList nlItemRestriction = eleRestrictionList.getChildNodes();
							for(int x = 0; x < nlItemRestriction.getLength(); x++){
								
								Element eleItemRestriction = (Element) nlItemRestriction.item(x);
								String restrictedState = eleItemRestriction.getAttribute(VSIConstants.ATTR_STATE);
								String restrictedCountry = eleItemRestriction.getAttribute(VSIConstants.ATTR_COUNTRY);
								//If (State = input state and Country = input country) OR (State = "" and Country = input country)
								if((YFCObject.isVoid(restrictedState) && restrictedCountry.equalsIgnoreCase(sCountry)) || 
										(restrictedState.equalsIgnoreCase(sState) && restrictedCountry.equalsIgnoreCase(sCountry))){
									return true;
								}
							}
						}
					}
				}
			}
		}// end of checking strExtnIsRestrcitedItem
		return false;
	}
	
	public void setProperties(Properties properties){
		prop = properties;
		
	}
	private void updatePipeline(YFSEnvironment env, Document inXml){		
		
		NodeList nlOrderLines = inXml.getDocumentElement().getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		if(!YFSObject.isVoid(nlOrderLines)){
			for(int i=0;i<nlOrderLines.getLength();i++){
				Element eleOrderLine = (Element) nlOrderLines.item(i);
				String strDeliveryMethod = "";
				//System.out.println("SHP_PIPELINE_ID: "+ prop.getProperty("SHP_PIPELINE_ID"));
				//System.out.println("PICK_PIPELINE_ID: "+ prop.getProperty("PICK_PIPELINE_ID"));
				strDeliveryMethod = eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
				if(VSIConstants.ATTR_DEL_METHOD_PICK.equals(strDeliveryMethod)){
					eleOrderLine.setAttribute(VSIConstants.ATTR_PIPELINE_ID, prop.getProperty("PICK_PIPELINE_ID"));
					//OMS-1821 : Start
					eleOrderLine.setAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE, "");
					//OMS-1821 : End
					eleOrderLine.setAttribute(ATTR_CONDITION_VARIBALE1,FLAG_N);
				}else if(VSIConstants.ATTR_DEL_METHOD_SHP.equals(strDeliveryMethod)){
					eleOrderLine.setAttribute(VSIConstants.ATTR_PIPELINE_ID, prop.getProperty("SHP_PIPELINE_ID"));
				}
			}
		}
	}

}//end of class