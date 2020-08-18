package com.vsi.oms.api;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIStampTOBOnOrder implements VSIConstants{
	private YFCLogCategory log = YFCLogCategory.instance(VSIStampTOBOnOrder.class);
	
	public Document vsiStampTOBOnOrder(YFSEnvironment env, Document inXML)
			throws Exception {
		
		if(log.isDebugEnabled()){
			log.info(" ** Entering Class: VSIStampTOBOnOrder Entering method: vsiStampTOBOnOrder");

			log.info("  ***** Printing Input XML : "+XMLUtil.getXMLString(inXML));
		}
		
		Document changeOrdIp = null;
		if(inXML != null){
			String strVertexEngine = "";
			inXML = VSIUtils.invokeService(env, "VSIAppendValues", inXML);
			
			if(log.isDebugEnabled()){
				log.info(" ** Invoked service VSIAppendValues successfully ");
				log.debug(" ** Printing XML output from VSIAppendValues "+XMLUtil.getXMLString(inXML));
			}
			
			Element inOrderEle = inXML.getDocumentElement();
			
			
			NodeList inOrderLineNL = inXML.getElementsByTagName(ELE_ORDER_LINE);
			String ordTotal = "";
			
			Element overallTotal = (Element) inOrderEle.getElementsByTagName("OverallTotals").item(0);
			if(overallTotal !=null){
				ordTotal = overallTotal.getAttribute("GrandTotal");
			}
			String ordCustPONo = "";
			String ordType = "";
			boolean isNewLineAdded = false;
			if(inOrderEle.hasAttribute("IsNewLineAdded") && inOrderEle.getAttribute("IsNewLineAdded").equalsIgnoreCase("Y")){
				isNewLineAdded = true;
			}
			if(inOrderEle.hasAttribute(VSIConstants.ATTR_CUST_CUST_PO_NO))
			ordCustPONo = inOrderEle.getAttribute(VSIConstants.ATTR_CUST_CUST_PO_NO);
			if(inOrderEle.hasAttribute(VSIConstants.ATTR_ORDER_TYPE))
				ordType = inOrderEle.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
			String inCoupId = "";
			boolean isCouponApplied = false;
			String lineType = null;
			changeOrdIp = XMLUtil.createDocument(ELE_ORDER);
			Element orderEle = changeOrdIp.getDocumentElement();
			
			
			
			orderEle.setAttribute("Action", "MODIFY");
			//BOP399
			orderEle.setAttribute("EntryType", inOrderEle.getAttribute("EntryType"));
			
			orderEle.setAttribute("BillToID", inOrderEle.getAttribute("BillToID"));
			orderEle.setAttribute("OrderDate", inOrderEle.getAttribute("OrderDate"));
			orderEle.setAttribute("EnteredBy", inOrderEle.getAttribute("EnteredBy"));
			orderEle.setAttribute(ATTR_ORDER_HEADER_KEY, inOrderEle.getAttribute(ATTR_ORDER_HEADER_KEY));
			
			Element extnEle = changeOrdIp.createElement("Extn");
			orderEle.appendChild(extnEle);
			
			Element inExtnEle = (Element) inOrderEle.getElementsByTagName("Extn").item(0);
			if(inExtnEle != null && inExtnEle.hasAttribute("ExtnOrderDate"))
			extnEle.setAttribute("ExtnOrderDate",inExtnEle.getAttribute("ExtnOrderDate") );
		
			NodeList inOrdDtNL = inOrderEle.getElementsByTagName("OrderDate");
			for(int k=0; k < inOrdDtNL.getLength(); k ++){
				Element inOrdDtEle = (Element) inOrdDtNL.item(k);
				if(inOrdDtEle.getAttribute("DateTypeId").equalsIgnoreCase("ExtnOrderDate")){
					Element newOrdDtsEle = changeOrdIp.createElement("OrderDates");
					orderEle.appendChild(newOrdDtsEle);
					Node cpyOrdDtEle = changeOrdIp.importNode(inOrdDtEle, true);
					newOrdDtsEle.appendChild(cpyOrdDtEle);
				}
				
			}
			
			
			Element vsiItemTOBList = changeOrdIp.createElement("VSIItemTOBList");
			extnEle.appendChild(vsiItemTOBList);
			
			Element orderLinesEle = changeOrdIp.createElement(ELE_ORDER_LINES);
			orderEle.appendChild(orderLinesEle);
			
			
			if(!isNewLineAdded){
				orderEle.setAttribute(VSIConstants.ATTR_CUST_CUST_PO_NO, ordCustPONo);
				orderEle.setAttribute(VSIConstants.ATTR_ORDER_TYPE, ordType);
				Element inPerInfBillToEle = (Element) inOrderEle.getElementsByTagName("PersonInfoBillTo").item(0);
				if(inPerInfBillToEle != null){
					orderEle.setAttribute("CustomerFirstName", inPerInfBillToEle.getAttribute("FirstName"));
					orderEle.setAttribute("CustomerLastName", inPerInfBillToEle.getAttribute("LastName"));
				}
				
				Element inHdrTaxsEle = (Element) inOrderEle.getElementsByTagName("HeaderTaxes").item(0);
				if(inHdrTaxsEle != null){
					Element hdrTaxesEle = changeOrdIp.createElement("HeaderTaxes");
					orderEle.appendChild(hdrTaxesEle);
					
					NodeList inHdrTaxNL = inHdrTaxsEle.getElementsByTagName("HeaderTax");
					for(int m= 0 ; m < inHdrTaxNL.getLength(); m++){
						Element inHdrTaxEle = (Element) inHdrTaxNL.item(m);
						
						Node inHdrTaxNode = inHdrTaxNL.item(m);
						Node hdrtaxCpyNde = changeOrdIp.importNode(inHdrTaxNode, true);
						Element inExtnHTEle =  (Element) inHdrTaxEle.getElementsByTagName("Extn").item(0);
						
						Element hdrTaxEle = (Element) hdrtaxCpyNde;
						hdrTaxesEle.appendChild(hdrTaxEle);
						
						if(inExtnHTEle != null && inExtnHTEle.hasAttribute("TaxType")){
							if(inExtnHTEle.getAttribute("TaxType").equalsIgnoreCase("Standard")){
								hdrTaxEle.setAttribute("Reference_2", "Y");
							}else{
								hdrTaxEle.setAttribute("Reference_2", "N");
							}
							
						}
						
						
						
						if(inHdrTaxEle.hasAttribute("TaxPercentage")){
							hdrTaxEle.setAttribute("Reference_1", inHdrTaxEle.getAttribute("TaxPercentage"));
						}else{
						hdrTaxEle.setAttribute("Reference_1", "");
						}
					}
				}
				
				Element inPromotionsEle = (Element) inOrderEle.getElementsByTagName("Promotions").item(0);
				if(inPromotionsEle != null){
					Element inPromotionEle = (Element) inPromotionsEle.getElementsByTagName("Promotion").item(0);
					if(inPromotionEle != null){
						Element inExPromEle = (Element) inPromotionEle.getElementsByTagName("Extn").item(0);
						if(inExPromEle != null && inExPromEle.hasAttribute("ExtnCouponID")){
							inCoupId = inExPromEle.getAttribute("ExtnCouponID");
							if(inCoupId != null && !inCoupId.trim().equalsIgnoreCase(""))
								{
								isCouponApplied = true;
								}
						}
					}
				}
				Element vsiOrderTOB = null;	
				Element vsiOrderTOBList = null;
				NodeList vsiOrderTOBListNL = inOrderEle.getElementsByTagName("VSIOrderTOB");
				
				
				vsiOrderTOBList = changeOrdIp.createElement("VSIOrderTOBList");
				extnEle.appendChild(vsiOrderTOBList);
				if(vsiOrderTOBListNL.getLength() > 0){
					vsiOrderTOB = (Element) vsiOrderTOBListNL.item(0);
					Node vsiOrderTOBNd = changeOrdIp.importNode(vsiOrderTOB, true);
					vsiOrderTOBList.appendChild(vsiOrderTOBNd);
					vsiOrderTOB = (Element) vsiOrderTOBNd;
				
				}else{
					vsiOrderTOB = changeOrdIp.createElement("VSIOrderTOB");
				vsiOrderTOBList.appendChild(vsiOrderTOB);
				vsiOrderTOB.setAttribute("ClientID", "WkStn.7001.1");
				vsiOrderTOB.setAttribute("OrigAmt", ordTotal);
				vsiOrderTOB.setAttribute("Status", inOrderEle.getAttribute("Status"));
				vsiOrderTOB.setAttribute("AcctStatus", "Y");
				vsiOrderTOB.setAttribute("IsAbort", "N");
				vsiOrderTOB.setAttribute("PickupTob", " ");
				vsiOrderTOB.setAttribute("IsTobDirty", "Y");
				String seqeNum ="VSI_TOB_POS_TRAN";
			 	String tranNo = VSIDBUtil.getNextSequence(env, seqeNum);
			 	vsiOrderTOB.setAttribute("Tran", tranNo);
			 	vsiOrderTOB.setAttribute("Coupon", inCoupId);
				}
				
				NodeList notesNL = XMLUtil.getNodeListByXpath(inXML, "Order/Notes/Note");
				for(int m=0;m < notesNL.getLength();m++){
					Element noteEle = (Element) notesNL.item(0);
					vsiOrderTOB.setAttribute("OrderComments", noteEle.getAttribute("NoteText"));
				}
				
			}
			
			extnEle.setAttribute("ExtnTotalPrice", ordTotal);
			
			
			
			
			//STamping Orderline attrbs
			if(inOrderLineNL.getLength() > 0){
				boolean isNewLine = false;
				
				for(int i = 0 ; i < inOrderLineNL.getLength() ; i++){
					
					Element inOrderLine = (Element) inOrderLineNL.item(i);
					if(inOrderLine.hasAttribute("IsNewLine") && inOrderLine.getAttribute("IsNewLine").equalsIgnoreCase("Y")){
						isNewLine = true;
					}else if(!inOrderLine.hasAttribute("IsNewLine")){
						isNewLine = true;
					}
					
					if( isNewLine){
						
					
					Element orderLineEle = changeOrdIp.createElement(ELE_ORDER_LINE);
					orderLinesEle.appendChild(orderLineEle);
					
					Element inOrdDatesEle = (Element) inOrderLine.getElementsByTagName("OrderDates").item(0);
					if(!YFCObject.isVoid(inOrdDatesEle)){
						Node cpyOrdDatesEle = changeOrdIp.importNode(inOrdDatesEle, true);
						orderLineEle.appendChild(cpyOrdDatesEle);
					}
					orderLineEle.setAttribute("Action", "MODIFY");
					orderLineEle.setAttribute("OrderedQty", inOrderLine.getAttribute("OrderedQty"));
					orderLineEle.setAttribute("PrimeLineNo", inOrderLine.getAttribute("PrimeLineNo"));
					
					Element itemEle = changeOrdIp.createElement("Item");
					orderLineEle.appendChild(itemEle);
					Element inItemELe = (Element) inOrderLine.getElementsByTagName("Item").item(0);
					itemEle.setAttribute("ItemID", inItemELe.getAttribute("ItemID"));
					Element extnOLEle = changeOrdIp.createElement("Extn");
					orderLineEle.appendChild(extnOLEle);
					Element inOLExtnEle = (Element) inOrderLine.getElementsByTagName("Extn").item(0);
					if(inOLExtnEle != null && inOLExtnEle.hasAttribute("ExtnPickDate") && inOLExtnEle.hasAttribute("ExtnLastPickDate")){
						extnOLEle.setAttribute("ExtnPickDate", inOLExtnEle.getAttribute("ExtnPickDate"));
						extnOLEle.setAttribute("ExtnLastPickDate", inOLExtnEle.getAttribute("ExtnLastPickDate"));
					}
					
					String lineCustPONo = "";
					if(inOrderLine.hasAttribute(VSIConstants.ATTR_CUST_PO_NO)){
						lineCustPONo = inOrderLine.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
					}
					lineType = inOrderLine.getAttribute("LineType");
					String extnTaxProductCode = "";
					Element inItemEle = (Element) inOrderLine.getElementsByTagName("ItemDetails").item(0);
					if(inItemEle != null){
						Element inItemExtnEle = (Element) inItemEle.getElementsByTagName("Extn").item(0);
						if(inItemExtnEle != null && inItemExtnEle.hasAttribute("ExtnTaxProductCode") ){
							extnTaxProductCode = inItemExtnEle.getAttribute("ExtnTaxProductCode");
						}
					}
					
					
					//get the Alias Value for UPC from Order
					Element itemAliasEle = (Element) inOrderLine.getElementsByTagName("ItemAlias").item(0);
					String sUPCValue = "";
					if(itemAliasEle != null  && itemAliasEle.getAttribute("AliasName").equalsIgnoreCase("UPC")){
						sUPCValue = itemAliasEle.getAttribute("AliasValue");
					}
					itemEle.setAttribute("UPCCode", sUPCValue);
					
					orderLineEle.setAttribute(ATTR_ORDER_LINE_KEY, inOrderLine.getAttribute(ATTR_ORDER_LINE_KEY));
					orderLineEle.setAttribute(VSIConstants.ATTR_CUST_PO_NO,lineCustPONo);
					
					Element lineTotalsEle = (Element) inOrderLine.getElementsByTagName("LineOverallTotals").item(0);
					
					extnOLEle.setAttribute("ExtnLineTotal", lineTotalsEle.getAttribute("LineTotal"));
					
					Element vsiOrdItTOBLst = changeOrdIp.createElement("VSIOrderItemTOBList");
					extnOLEle.appendChild(vsiOrdItTOBLst);
					
					Element vsiOrdItTOB = changeOrdIp.createElement("VSIOrderItemTOB");
					vsiOrdItTOBLst.appendChild(vsiOrdItTOB);
					vsiOrdItTOB.setAttribute("OrigExtPrice", lineTotalsEle.getAttribute("UnitPrice"));
					vsiOrdItTOB.setAttribute("OrigQty", inOrderLine.getAttribute(ATTR_ORD_QTY));
					
					Element vsiItemTOB = changeOrdIp.createElement("VSIItemTOB");
					vsiItemTOBList.appendChild(vsiItemTOB);
					vsiItemTOB.setAttribute("Idx", "0");
					vsiItemTOB.setAttribute("FinalPrice", lineTotalsEle.getAttribute("LineTotal"));
					vsiItemTOB.setAttribute("Status", inOrderLine.getAttribute("Status"));
					vsiItemTOB.setAttribute("Qty", inOrderLine.getAttribute(ATTR_ORD_QTY));
					
					
					NodeList ipLineChrgNL = inOrderLine.getElementsByTagName("LineCharge");
					if(ipLineChrgNL.getLength() > 0){
						
						Element lineChargsEle = changeOrdIp.createElement("LineCharges");
						orderLineEle.appendChild(lineChargsEle);
						
						Element vsiIDTOBLst = changeOrdIp.createElement("VSIItemDiscountTOBList");
						extnEle.appendChild(vsiIDTOBLst);
						String isManualFlag = "Y";
						for(int k=0; k < ipLineChrgNL.getLength(); k++){
							
							Node ipLineChrg =  ipLineChrgNL.item(k);
							Element ipLineChrgEle = (Element) ipLineChrg;
							if(ipLineChrgEle.hasAttribute("IsManual"))
							isManualFlag = ipLineChrgEle.getAttribute("IsManual");
							
							if(isManualFlag.equalsIgnoreCase("Y")){
							Node copyNode = changeOrdIp.importNode(ipLineChrg, true);
							lineChargsEle.appendChild(copyNode);
							Element lineChargEle = (Element) copyNode;
							Element extnLCEle = (Element) lineChargEle.getElementsByTagName("Extn").item(0);
							
							
							String seqNum ="VSI_TOB_POS_TAG";
						 	String tagNo = VSIDBUtil.getNextSequence(env, seqNum);
						 	extnLCEle.setAttribute("Tag", tagNo);
						 	extnLCEle.setAttribute("Num", "1");
						 	extnLCEle.setAttribute("Idx", "1");
						 	extnLCEle.setAttribute("PromoNum", "");
						 	extnLCEle.setAttribute("ReasonCode", "");
						 	extnLCEle.setAttribute("ReasonDesc", "");
						 	extnLCEle.setAttribute("ReasonType", "");
						 	
						 	
						 	Element ipLineChrgeEle = (Element) ipLineChrgNL.item(k);
						 	
						 	//Double dChrgPerLine = Double.parseDouble(ipLineChrgeEle.getAttribute("ChargePerLine"));
						 	//Double dLineQty = Double.parseDouble( inOrderLine.getAttribute(ATTR_ORD_QTY));
						 	//Double dChrgPerUnit = dChrgPerLine / dLineQty;
						 	//lineChargEle.setAttribute("ChargePerUnit", dChrgPerUnit.toString());
						 	
						 	//OMS- 236: overriding ChargeNameKey in the lineChargEle with the value from ipLineChrgeEle
						 	//Done for fixing issue with difference in the ChargeNameKey. Possible change in value while copying.s
						 	lineChargEle.setAttribute("ChargeNameKey", ipLineChrgeEle.getAttribute("ChargeNameKey"));
						 	
						 	Element ipLCExtnEle = (Element) ipLineChrgeEle.getElementsByTagName("Extn").item(0);
						 	String coupNo = "";
						 	boolean isCoupApplied = false;
						 	if(ipLCExtnEle !=null && ipLCExtnEle.hasAttribute("CouponNumber")){
						 		coupNo = ipLCExtnEle.getAttribute("CouponNumber");
						 		if(coupNo != null && !coupNo.trim().equalsIgnoreCase("")){
						 			isCoupApplied = true;
						 		}
						 		
						 	}
						 	
						 	extnLCEle.setAttribute("CouponNumber", coupNo);
						 	if(isCoupApplied) {
						 		extnLCEle.setAttribute("PromoDesc", ipLineChrgeEle.getAttribute("ChargeName"));
						 		extnLCEle.setAttribute("PromoName", ipLineChrgeEle.getAttribute("ChargeName"));
						 	}else{
						 		extnLCEle.setAttribute("PromoDesc", "");
						 		extnLCEle.setAttribute("PromoName", "");
						 	}
						 	
						 	extnLCEle.setAttribute("Price", ipLineChrgeEle.getAttribute("ChargeAmount"));
						 	
						 	
						 	Element ipExtnLCEle = (Element) ipLineChrgeEle.getElementsByTagName("Extn").item(0);
						 	
						 	Element vsiIDTOB = changeOrdIp.createElement("VSIItemDiscountTOB");
						 	vsiIDTOBLst.appendChild(vsiIDTOB);
						 	vsiIDTOB.setAttribute("IsDiscount", ipLineChrgeEle.getAttribute("IsDiscount"));
						 	vsiItemTOB.setAttribute("IsDiscount", ipLineChrgeEle.getAttribute("IsDiscount"));
						 	vsiIDTOB.setAttribute("Amt", ipLineChrgeEle.getAttribute("ChargePerLine"));
						 	vsiIDTOB.setAttribute("Desc", ipLineChrgeEle.getAttribute("ChargeName"));
						 	vsiIDTOB.setAttribute("IsEmpDisc", ipExtnLCEle.getAttribute("IsEmpDisc"));
						 	vsiIDTOB.setAttribute("IsEmpDisc", ipExtnLCEle.getAttribute("IsEmpDisc"));
						 	vsiIDTOB.setAttribute("IsItemLevel", ipLineChrgeEle.getAttribute("IsDiscount"));
						 	
						 	vsiIDTOB.setAttribute("IsRealDisc", ipExtnLCEle.getAttribute("IsRealDisc"));
						 	vsiIDTOB.setAttribute("IsTranLevel", ipExtnLCEle.getAttribute("IsTranLevel"));
						 	vsiIDTOB.setAttribute("Num", ipExtnLCEle.getAttribute("Num"));
						 	vsiIDTOB.setAttribute("Percent", ipExtnLCEle.getAttribute("Percent"));
						 	vsiIDTOB.setAttribute("Price", ipLineChrgeEle.getAttribute("ChargeAmount"));
						 	vsiIDTOB.setAttribute("PromoIndicator", ipExtnLCEle.getAttribute("PromoIndicator"));
						 	vsiIDTOB.setAttribute("Reduction", ipExtnLCEle.getAttribute("ReducedAmt"));
						 	vsiIDTOB.setAttribute("Tag", tagNo);
							}
						 	
						 	
						 	
						}
						
					}
					NodeList ipInstructionNL = inOrderLine.getElementsByTagName("Instruction");
					Element instructsEle = changeOrdIp.createElement("Instructions");
					orderLineEle.appendChild(instructsEle);
					String insText = "IsVAT=False;TaxAuthNum=1,TaxGroupID="+extnTaxProductCode+",,,,,";
					
					Element insEle = null;
					if(ipInstructionNL.getLength() > 0){
						for(int m=0; m < ipInstructionNL.getLength(); m++){
							Node ipIns = ipInstructionNL.item(m);
							Node insCpy = changeOrdIp.importNode(ipIns, true);
							instructsEle.appendChild(insCpy);
							insEle = (Element) insCpy;
							insEle.setAttribute("InstructionType", "Comments");
							insEle.setAttribute("InstructionText", insText);
							
						}
					}else{
						insEle = changeOrdIp.createElement("Instruction");
						instructsEle.appendChild(insEle);
						insEle.setAttribute("InstructionType", "Comments");
						insEle.setAttribute("InstructionText", insText);
					}
					
					String strTaxableFlag = "N";
					
					NodeList ipLineTaxNL = inOrderLine.getElementsByTagName("LineTax");
					if(ipLineTaxNL.getLength() > 0){
						String zoneId = null;
						String taxType = "";
						String taxZoneId = null;
						Element lineTaxesEle = changeOrdIp.createElement("LineTaxes");
						orderLineEle.appendChild(lineTaxesEle);
						for(int j=0; j < ipLineTaxNL.getLength(); j++){
							Node ipLineTax = ipLineTaxNL.item(j);
							Node lineTaxCpy = changeOrdIp.importNode(ipLineTax, true);
							lineTaxesEle.appendChild(lineTaxCpy);
							Element lineTaxEle = (Element) lineTaxCpy;
							strTaxableFlag = lineTaxEle.getAttribute("TaxableFlag");
							Element extnLTEle = (Element) lineTaxEle.getElementsByTagName("Extn").item(0);
							strVertexEngine = extnLTEle.getAttribute("ExtnVertexEngine");
							extnLTEle.setAttribute("GroupId", extnTaxProductCode);
							/*zoneId = extnLTEle.getAttribute("ZoneId");
							if(zoneId !=null && extnTaxProductCode !=null){
								zoneId = formatValue(zoneId);
								extnTaxProductCode = formatValue(extnTaxProductCode);
								taxZoneId =zoneId+extnTaxProductCode ;
								taxType = getTaxType(env,taxZoneId);
								if(taxType != null && taxType.equalsIgnoreCase("0")){
									taxType = "NonTaxable";
								}else if(taxType != null && taxType.equalsIgnoreCase("1")){
									taxType = "Standard";
								}else if (taxType != null && taxType.equalsIgnoreCase("2")){
									taxType = "Alternate";
								}
							}
							extnLTEle.setAttribute("TaxType", taxType);*/
							
						}
					}
					Element eleLinePriceInfo = changeOrdIp.createElement("LinePriceInfo");	
					orderLineEle.appendChild(eleLinePriceInfo);
					Element inLinePriceInfo = (Element) inOrderLine.getElementsByTagName("LinePriceInfo").item(0);
					eleLinePriceInfo.setAttribute("LineTotal",inLinePriceInfo.getAttribute("LineTotal")) ;
					if("Y".equalsIgnoreCase(strTaxableFlag)){
											
						eleLinePriceInfo.setAttribute("TaxableFlag", "Y");
						orderLineEle.setAttribute("TaxableFlag", "Y");
						
					}
					
					
					
				}
					
					
					
				}
			}
			if(!YFCObject.isVoid(strVertexEngine)){
				extnEle.setAttribute("ExtnVertexEngine", strVertexEngine);
			}
			if(lineType != null && lineType.equalsIgnoreCase("PICK_IN_STORE")){
				extnEle.setAttribute("OrderType", "pickup");
			}else{
				extnEle.setAttribute("OrderType", "shipping");
			}
			
			
			
			
		}
		return changeOrdIp;
	}
	private String formatValue(String extnTaxProductCode) {
		int strLgth = extnTaxProductCode.length();
		if(strLgth < 5){
			while(strLgth < 5){
				extnTaxProductCode = "0"+extnTaxProductCode;
				strLgth = extnTaxProductCode.length();
			}
		}
		return extnTaxProductCode;
	}
	private String getTaxType(YFSEnvironment env, String taxZoneId) {
		String taxType = null;
		if(taxZoneId != null){
			try {
				Document getTobTaxZoneLstIp = XMLUtil.createDocument("TobTaxZone");
				Element rootEle = getTobTaxZoneLstIp.getDocumentElement();
				rootEle.setAttribute("TaxZoneId", taxZoneId);
				if(log.isDebugEnabled()){
					log.info("Input To getobTaxZoneList : \n"+XMLUtil.getXMLString(getTobTaxZoneLstIp));
				}
				Document getTobTaxZoneLstOp = VSIUtils.invokeService(env, "VSIGetTobTaxZoneList", getTobTaxZoneLstIp);
				if(log.isDebugEnabled()){
					log.info("getTobTaxZoneLstOp : \n"+XMLUtil.getXMLString(getTobTaxZoneLstOp));
				}
				if(getTobTaxZoneLstOp != null){
					 Element opRootEle = getTobTaxZoneLstOp.getDocumentElement();
					 if(opRootEle.hasChildNodes()){
						 Element tobTaxZoneEle = (Element) opRootEle.getElementsByTagName("TobTaxZone").item(0);
						 if(tobTaxZoneEle != null)
							 taxType = tobTaxZoneEle.getAttribute("TaxType");
					 }
				 }
				if(log.isDebugEnabled()){
					log.info("TaxType: "+taxType);
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return taxType;
	}
}
