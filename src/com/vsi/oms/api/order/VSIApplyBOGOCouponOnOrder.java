package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIApplyBOGOCouponOnOrder {
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIApplyBOGOCouponOnOrder.class);

	public void vsiApplyBOGO(YFSEnvironment env, Document inOrderDoc)
			throws YFSException, RemoteException, ParserConfigurationException,
			YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.info("*** Inside VSIApplyBOGOCouponOnOrder ***");
			log.debug("*** Printing Input XML to VSIApplyBOGOCouponOnOrder:\n "
					+ XMLUtil.getXMLString(inOrderDoc));
		}
		if(inOrderDoc != null){
			try {
				Element inOrderEle = inOrderDoc.getDocumentElement();
				NodeList inOrdLineNL = inOrderEle
						.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				int noOfLines = inOrdLineNL.getLength();
				String ordHdrKey = inOrderEle
						.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
				String ordQty = null;
				String ordLineKey = null;
				String itemId = null;
				String unitPrice = null;
			TreeMap<String, String> itemMap = new TreeMap<String, String>();
			TreeMap<String, String> qtyMap = new TreeMap<String, String>();
				HashMap<String, Double> itemPriceMap = new HashMap<String, Double>();
			HashMap<String, String> itemDiscMap = new HashMap<String, String>();
			TreeMap<String, String> lineDiscMap = new TreeMap<String, String>();
			String ruleDesc = null;
			String ruleName = null;
			String disctPrct = null;
			Double dDisctPrct = 0.0;
			HashMap<String, String> disLineMap = new HashMap<String, String>();
				HashMap<Double, String> bogoMap = new HashMap<Double, String>();
				for(int k=0; k < noOfLines; k++){
					Element inOrdLine = (Element) inOrdLineNL.item(k);
					ordLineKey = inOrdLine
							.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
					ordQty = inOrdLine.getAttribute(VSIConstants.ATTR_ORD_QTY);
					qtyMap.put(ordLineKey, ordQty);
					Element itemEle = (Element) inOrdLine.getElementsByTagName(
							VSIConstants.ELE_ITEM).item(0);
					itemId = itemEle.getAttribute(VSIConstants.ATTR_ITEM_ID);
					itemMap.put(ordLineKey, itemId);
					Element linePriceEle = (Element) inOrdLine
							.getElementsByTagName(VSIConstants.ELE_LINE_PRICE)
							.item(0);
					unitPrice = linePriceEle.getAttribute("UnitPrice");
					itemPriceMap.put(itemId, Double.parseDouble(unitPrice));
					
				}
				if(log.isDebugEnabled()){
					log.debug("Printing itemId: "+itemId);
					log.debug("Printing itemMap: "+itemMap);
					log.debug("Printing itemPriceMap: "+itemPriceMap);
					log.debug("Printing qtyMap: "+qtyMap);
				}
				Document pricingRuleListOp = VSIUtils.invokeService(env,
						"VSIGetPricingRuleList", inOrderDoc);
				if(log.isDebugEnabled()){
					log.debug("Printing pricingRuleListOp: \n"
							+ XMLUtil.getXMLString(pricingRuleListOp));
				}
				if(pricingRuleListOp != null){
					Element rootEle = pricingRuleListOp.getDocumentElement();
					NodeList pricingRuleEle = rootEle
							.getElementsByTagName("PricingRule");
					int noOfRules = pricingRuleEle.getLength();
					if(log.isDebugEnabled()){
						log.debug("Printing rule count: "+noOfRules);
					}
					for(int j=0; j < noOfRules; j ++){
						Element prcgRuleEle = (Element) pricingRuleEle.item(j);
						ruleDesc = prcgRuleEle.getAttribute("Description");
						ruleDesc = ruleDesc.toUpperCase();
						ruleName = prcgRuleEle.getAttribute("PricingRuleName");
						if(log.isDebugEnabled()){
							log.debug("ruleDesc "+ruleDesc);
						}
						if(ruleDesc != null && ruleDesc.contains("BOGO")){
							dDisctPrct = 0.0;
							disctPrct = "";
							if(log.isDebugEnabled()){
								log.debug("dDisctPrct " + dDisctPrct);
								log.debug("Indise BOGO ");
							}
							int strtIdx = ruleDesc.indexOf("-");
							int endIdx = ruleDesc.indexOf("%");
							if(strtIdx > 0 && endIdx > 0 )
								disctPrct = ruleDesc.substring(strtIdx + 1,
										endIdx);
						if(disctPrct !=null)
							disctPrct = disctPrct.trim();
							if (disctPrct != null
									&& !disctPrct.equalsIgnoreCase(""))
							dDisctPrct = Double.parseDouble(disctPrct);
							if(log.isDebugEnabled()){
								log.debug("dDisctPrct "+dDisctPrct);
							}
							if(dDisctPrct > 0){
								
								NodeList prcgRuleItemNL = prcgRuleEle
										.getElementsByTagName("PricingRuleItem");
								String disctdItemId = null;
								
								for(int n = 0; n < prcgRuleItemNL.getLength(); n ++ ){
									Element prcgRuleItmEle = (Element) prcgRuleItemNL
											.item(n);
									disctdItemId = prcgRuleItmEle
											.getAttribute(VSIConstants.ATTR_ITEM_ID);
								if(itemMap.containsValue(disctdItemId)){
									
										itemDiscMap.put(disctdItemId,
												dDisctPrct.toString());
										if (bogoMap.size() > 0
												&& bogoMap
														.containsKey(dDisctPrct)) {
											disctdItemId = bogoMap
													.get(dDisctPrct)
													+ ";"
													+ disctdItemId;
										}
										disctdItemId=disctdItemId+":"+ruleName;
										bogoMap.put(dDisctPrct, disctdItemId);
								}
							}
						}
					}
				}
			}
				if(log.isDebugEnabled()){

					log.debug("itemDiscMap "+itemDiscMap);

					log.debug("bogoMap " + bogoMap);
				}					
				Iterator bogoItr = bogoMap.entrySet().iterator();
				String itemsStr = null;
				String strRuleName = null;
				String itemID = null;
									Double dQty = 0.0;
									Double dUnitPrice = 0.0;
									Double dDiscPerUnit = 0.0;
									Double dDiscPerLine = 0.0;
									int iDisc = 0;
				while (bogoItr.hasNext()) {
					String[] itemsArray = null;
					HashMap<String, Double> itemRemainder = new HashMap<String, Double>();
					HashMap<String, Double> itemBogoMap = new HashMap<String, Double>();
					Map.Entry discItemsKey = (Map.Entry) bogoItr.next();
					dDisctPrct = (Double) discItemsKey.getKey();
					itemsStr = (String) discItemsKey.getValue();
					if (itemsStr.contains(";")) {
						itemsArray = itemsStr.split(";");
					} else {
						itemsStr = itemsStr + ";";
						itemsArray = itemsStr.split(";");
					}
					for (int n = 0; n < itemsArray.length; n++) {
						itemID = itemsArray[n];
						if (itemPriceMap.containsKey(itemID.split(":")[0])) {
							itemBogoMap.put(itemID, itemPriceMap.get(itemID.split(":")[0]));
						}
					}
					Set<Entry<String, Double>> set = itemBogoMap.entrySet();
					List<Entry<String, Double>> sortedPriceList = new ArrayList<Entry<String, Double>>(
							set);
					Collections.sort(sortedPriceList,
							new Comparator<Map.Entry<String, Double>>() {
								public int compare(
										Map.Entry<String, Double> o1,
										Map.Entry<String, Double> o2) {
									return (o2.getValue()).compareTo(o1
											.getValue());
								}
							});
					if(log.isDebugEnabled()){
						log.debug("sortedPriceList" + sortedPriceList);
					}
					for (Map.Entry itemDisc : sortedPriceList) {
						itemID = (String) itemDisc.getKey();
						strRuleName = itemID.split(":")[1];
						itemID = itemID.split(":")[0];
						if(log.isDebugEnabled()){
							log.debug("itemID " + itemID+" strRuleName "+strRuleName);
						}
						Iterator itemItr = itemMap.entrySet().iterator();
								    while (itemItr.hasNext()) {
								        Map.Entry itemOLKey = (Map.Entry)itemItr.next();
								        ordLineKey = (String) itemOLKey.getKey();
								        itemId = (String) itemOLKey.getValue();
							
								        if(log.isDebugEnabled()){
								        	log.debug("*********itemId " + itemId);
								        }
						    if (itemID.equalsIgnoreCase(itemId)) {
								        	ordQty = qtyMap.get(ordLineKey);
								        	dQty = Double.parseDouble(ordQty);
								        	if(log.isDebugEnabled()){
								        		log.debug("*********dQty " + dQty);
								        	}
								dUnitPrice = itemPriceMap.get(itemId);
								if(log.isDebugEnabled()){
									log.debug("*********dUnitPrice " + dUnitPrice);
								}
								        	if(dQty > 0 && dUnitPrice > 0){
								        		dDiscPerUnit = (dDisctPrct * dUnitPrice)/100;
									if (itemRemainder.size() > 0) {
								        			dQty = dQty+1;
										itemRemainder.clear();
								        		}
								        		if(dQty % 2 != 0){
										itemRemainder.put(itemID, 1.0);
								        			dQty = dQty -1;
								        		}
								        		if(dQty > 0){
								        			dQty = dQty/2;
									        		dDiscPerLine = dDiscPerUnit * dQty;
									        		if(log.isDebugEnabled()){
									        			log.debug("dDiscPerLine: "
									        					+ dDiscPerLine);
									        		}
										disLineMap.put(ordLineKey,
												dDiscPerLine.toString());
									        		iDisc = dDisctPrct.intValue();
										lineDiscMap.put(ordLineKey,
												String.valueOf(iDisc)+":"+strRuleName);
								        		}
								        		
								        	}
								        }
						}
								        
								    }
								
				}
							
				if(log.isDebugEnabled()){
					log.debug("disLineMap: "+disLineMap);
					log.debug("lineDiscMap: "+lineDiscMap);
				}
						
					
				//end of for loop - looping thru every PricingRule Element
				
				if(disLineMap.size() > 0){
					
					Document changeOrderIpDoc = XMLUtil
							.createDocument(VSIConstants.ELE_ORDER);
					Element orderEle = changeOrderIpDoc.getDocumentElement();
					orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
							ordHdrKey);
					Element ordLinesEle = changeOrderIpDoc
							.createElement(VSIConstants.ELE_ORDER_LINES);
					orderEle.appendChild(ordLinesEle);
					String chrgCtgy = null;
					String chrgName = null;
					for(int i=0;i < noOfLines; i++){
						Element inOrdLineEle = (Element) inOrdLineNL.item(i);
						ordLineKey = inOrdLineEle
								.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							if(disLineMap.containsKey(ordLineKey)){
							NodeList inLineChrgNL = inOrdLineEle
									.getElementsByTagName("LineCharge");
								String discPerLine = disLineMap.get(ordLineKey);
							Element orderLineEle = changeOrderIpDoc
									.createElement(VSIConstants.ELE_ORDER_LINE);
								ordLinesEle.appendChild(orderLineEle);
							orderLineEle.setAttribute(
									VSIConstants.ATTR_ORDER_LINE_KEY,
									ordLineKey);
								
							Element lineChrgsEle = changeOrderIpDoc
									.createElement("LineCharges");
								orderLineEle.appendChild(lineChrgsEle);
								boolean isBOGOSet = false;
								for(int s= 0 ; s < inLineChrgNL.getLength();s++){
								Element inLineChargeEle = (Element) inLineChrgNL
										.item(s);
								chrgCtgy = inLineChargeEle
										.getAttribute("ChargeCategory");
								chrgName = inLineChargeEle
										.getAttribute("ChargeName");
								Element extnEle = null;
								if (chrgCtgy.equalsIgnoreCase("Discount")
										&& chrgName.contains("BOGO")) {
									Element copyLineChrgeEle = (Element) changeOrderIpDoc
											.importNode(inLineChargeEle, true);
										lineChrgsEle.appendChild(copyLineChrgeEle);
									copyLineChrgeEle.setAttribute(
											"ChargePerLine", discPerLine);
									if(copyLineChrgeEle.hasChildNodes()){
										 extnEle = (Element) copyLineChrgeEle.getElementsByTagName("Extn").item(0);
										 if(extnEle == null){
											 extnEle = changeOrderIpDoc
														.createElement("Extn");
												copyLineChrgeEle.appendChild(extnEle);
										 }
									}else{
										extnEle = changeOrderIpDoc
												.createElement("Extn");
										copyLineChrgeEle.appendChild(extnEle);
									}
									extnEle.setAttribute("ExtnDiscType", "MXMH");
									extnEle.setAttribute("ExtnType", "MXMH");
									//START - Fix for UUAT-3
									extnEle.setAttribute("CouponNumber", "");
									extnEle.setAttribute("ExtnChargeId", lineDiscMap.get(ordLineKey).split(":")[1]);

									/* Commenting below code for SUH-39 : BEGIN
									Element elePromotions = changeOrderIpDoc.createElement("Promotions");
									orderEle.appendChild(elePromotions);
									
									Element elePromotion = changeOrderIpDoc.createElement("Promotion");
									elePromotions.appendChild(elePromotion);
									
									elePromotion.setAttribute("PromotionApplied", "Y");
									elePromotion.setAttribute("PromotionId", lineDiscMap.get(ordLineKey).split(":")[1]);
									Element eleExtnPromotion = changeOrderIpDoc.createElement(VSIConstants.ELE_EXTN);
									elePromotion.appendChild(eleExtnPromotion);
									eleExtnPromotion.setAttribute("ExtnCouponID", "");
									eleExtnPromotion.setAttribute("ExtnDescription", chrgName);
									//END - Fix for UUAT-3
									 SUH-39 : END */
										isBOGOSet = true;
										break;
									}
								}// end of looping the line charges of input Order Element
								
								if(!isBOGOSet){
								Element lineChrgEle = changeOrderIpDoc
										.createElement("LineCharge");
									lineChrgsEle.appendChild(lineChrgEle);
								lineChrgEle.setAttribute("ChargePerLine",
										discPerLine);
								lineChrgEle.setAttribute("ChargeCategory",
										"Discount");
								chrgName = "BOGO-"
										+ lineDiscMap.get(ordLineKey).split(":")[0];
								lineChrgEle
										.setAttribute("ChargeName", chrgName);
								Element extnEle = changeOrderIpDoc
										.createElement("Extn");
								lineChrgEle.appendChild(extnEle);
								extnEle.setAttribute("ExtnDiscType", "MXMH");
								extnEle.setAttribute("ExtnType", "MXMH");
								//START - Fix for UUAT-3
								extnEle.setAttribute("CouponNumber", "");
								extnEle.setAttribute("ExtnChargeId", lineDiscMap.get(ordLineKey).split(":")[1]);

								/* Commenting below code for SUH-39 : BEGIN
								Element elePromotions = changeOrderIpDoc.createElement("Promotions");
								orderEle.appendChild(elePromotions);
								
								Element elePromotion = changeOrderIpDoc.createElement("Promotion");
								elePromotions.appendChild(elePromotion);
								
								elePromotion.setAttribute("PromotionApplied", "Y");
								elePromotion.setAttribute("PromotionId", lineDiscMap.get(ordLineKey).split(":")[1]);
								Element eleExtnPromotion = changeOrderIpDoc.createElement(VSIConstants.ELE_EXTN);
								elePromotion.appendChild(eleExtnPromotion);
								eleExtnPromotion.setAttribute("ExtnCouponID", "");
								eleExtnPromotion.setAttribute("ExtnDescription", chrgName);
								//END - Fix for UUAT-3
								 SUH-39 : END */
								}
							}
						}
						if(changeOrderIpDoc != null){
							if(log.isDebugEnabled()){
								log.info("**** Invoking changeOrder API with Input: \n"
										+ XMLUtil.getXMLString(changeOrderIpDoc));
							}
						Document changeOrdOpDoc = VSIUtils
								.invokeAPI(env, VSIConstants.API_CHANGE_ORDER,
										changeOrderIpDoc);
						if(log.isDebugEnabled()){
							log.info("**** Invoked changeOrder API successfully: \n"
									+ XMLUtil.getXMLString(changeOrdOpDoc));
						}
						}
						
						
						
					}
			
				
				
			} catch (Exception e) {
				
				e.printStackTrace();
			
		}
		
	}
	}
	}
