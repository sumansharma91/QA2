package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient, Inc.
 * 
 */
public class VSICallCenterCustomerPoGenNew {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSICallCenterCustomerPoGenNew.class);
	YIFApi api;

	/**
	 * @param env
	 * @param inXML
	 * @return
	 * @throws YFSException
	 */
	public Document vsiCallCenterCustomerPoGen(YFSEnvironment env,
			Document inXML) throws YFSException {

		int iShip = 0;
		int iPick = 0;
		int iNewShip = 0;
		int iNewPick = 0;
		//log.info("Printing Input XML :" + XmlUtils.getString(inXML));
		if(log.isDebugEnabled()){
			log.info("================Inside VSICallCenterCustomerPoGen================================");
		}
		try {

			if (null != inXML.getDocumentElement()
					&& null != inXML
							.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)) {
				Element rootElement = inXML.getDocumentElement();
				NodeList orderLineList = inXML
						.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				int orderLineLength = orderLineList.getLength();
				// String orderType =
				// rootElement.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
				//String entryType = rootElement.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
				if (null != rootElement) {
					// if(entryType.equalsIgnoreCase("Call Center")){
					int noOfLinesWithoutCustPONo = 0;
					boolean isNewLineAdded = false;
					for (int i = 0; i < orderLineLength; i++) {
						Element orderLineElement = (Element) inXML
								.getElementsByTagName(
										VSIConstants.ELE_ORDER_LINE).item(i);
						String sLineType = orderLineElement
								.getAttribute(VSIConstants.ATTR_LINE_TYPE);
						String sFLType = orderLineElement
								.getAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE);
						String custPONo = orderLineElement
								.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
						if (custPONo == null || (custPONo != null && custPONo.trim().equals(""))) {
							noOfLinesWithoutCustPONo++;
							if (sLineType.equalsIgnoreCase("SHIP_TO_STORE") || sFLType.equalsIgnoreCase("SHIP_TO_STORE")) {

								iNewShip++;
								// orderLineElement.setAttribute(VSIConstants.ATTR_PROCURE_FROM_NODE,"9001");

							} else
								iNewPick++;
						}

						if (sLineType.equalsIgnoreCase("SHIP_TO_STORE") || sFLType.equalsIgnoreCase("SHIP_TO_STORE")) {

							iShip++;
							// orderLineElement.setAttribute(VSIConstants.ATTR_PROCURE_FROM_NODE,"9001");

						} else
							iPick++;

					}
					
					if(log.isDebugEnabled()){
						log.debug("noOfLinesWithoutCustPONo : " +noOfLinesWithoutCustPONo);
					}
					
					if (noOfLinesWithoutCustPONo > 0
							&& noOfLinesWithoutCustPONo != orderLineLength) {
						isNewLineAdded = true;
					}
					if(log.isDebugEnabled()){
						log.debug("isNewLineAdded : " +isNewLineAdded);
					}
					
					if (iShip == 0 || iPick == 0) {

						if (!isNewLineAdded) {
							rootElement.setAttribute(
									VSIConstants.ATTR_ORDER_TYPE, "WEB");
							String generatedCustomerPoNo = generateOrderNo(env,
									inXML, isNewLineAdded);
							for (int j = 0; j < orderLineLength; j++) {
								Element orderLineElement = (Element) inXML
										.getElementsByTagName(
												VSIConstants.ELE_ORDER_LINE)
										.item(j);
								orderLineElement.setAttribute(
										VSIConstants.ATTR_CUST_PO_NO,
										generatedCustomerPoNo);

							}
						} else {
							rootElement.setAttribute(
									"IsNewLineAdded", "Y");
							Element firstLine = (Element) inXML
									.getElementsByTagName(
											VSIConstants.ELE_ORDER_LINE)
									.item(0);
							String custPONo = firstLine
									.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
							if(log.isDebugEnabled()){
								log.debug("firstLine : " +XMLUtil.getElementXMLString(firstLine));
								log.debug("custPONo : " +custPONo);
							}
							
							if (custPONo != null) {
								for (int j = 0; j < orderLineLength; j++) {
									Element orderLineElement = (Element) inXML
											.getElementsByTagName(
													VSIConstants.ELE_ORDER_LINE)
											.item(j);
									String custPoNo = orderLineElement
											.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
									if (custPoNo == null || (custPoNo != null && custPoNo.trim().equals(""))) {
										
										orderLineElement.setAttribute(
												VSIConstants.ATTR_CUST_PO_NO,
												custPONo);
										orderLineElement.setAttribute(
												"IsNewLine",
												"Y");
										
									}else{
										orderLineElement.setAttribute(
												"IsNewLine",
												"N");
									}
										

								}
							}
						}

					}

					else {

						if (!isNewLineAdded) {

							String genCustomerPoNo1 = generateOrderNo(env,
									inXML, isNewLineAdded);
							/*
							 * int custPoNo1 =
							 * Integer.parseInt(genCustomerPoNo1); custPoNo1 =
							 * custPoNo1 + 1;
							 * 
							 * int custPoNo2 = custPoNo1 + 1;
							 * 
							 * String shipPoNo = Integer.toString(custPoNo1);
							 * String PickPoNo = Integer.toString(custPoNo2);
							 */

							String custPoNo1 = genCustomerPoNo1;
							String custPoNo2 = genCustomerPoNo1.substring(0,
									genCustomerPoNo1.length() - 1) + "1";

							// System.out.println("custPoNo1"+custPoNo1+"\n custPoNo2"+
							// custPoNo2);

							rootElement.setAttribute(
									VSIConstants.ATTR_ORDER_TYPE, "WEB");

							for (int k = 0; k < orderLineLength; k++) {

								Element orderLineElement = (Element) inXML
										.getElementsByTagName(
												VSIConstants.ELE_ORDER_LINE)
										.item(k);
								String sLineType = orderLineElement
										.getAttribute(VSIConstants.ATTR_LINE_TYPE);
								
								String sFLType = orderLineElement
										.getAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE);

								if (sLineType.equalsIgnoreCase("SHIP_TO_STORE") || sFLType.equalsIgnoreCase("SHIP_TO_STORE") ) {

									// System.out.println("SHIP TO STORE");
									orderLineElement.setAttribute(
											VSIConstants.ATTR_CUST_PO_NO,
											custPoNo2);
									// orderLineElement.setAttribute(VSIConstants.ATTR_PROCURE_FROM_NODE,"9001");
									// //System.out.println("Printing Input XML AGAIN :"
									// + XmlUtils.getString(inXML));
								} else {

									// System.out.println("PICK IN STORE");
									orderLineElement.setAttribute(
											VSIConstants.ATTR_CUST_PO_NO,
											custPoNo1);
									// //System.out.println("Printing Input XML AGAIN :"
									// + XmlUtils.getString(inXML));

								}
							}// end for
						}else{
							rootElement.setAttribute(
									"IsNewLineAdded", "Y");
							String newCustomerPoNo = generateOrderNo(env,
									inXML, isNewLineAdded);
							//First case -- Say if the new added line is making the Order Mixed - In this case generate new Customer PO No 
							//Identify which line Type is added.
							if(noOfLinesWithoutCustPONo == iNewShip || noOfLinesWithoutCustPONo == iNewPick){
								
								if(log.isDebugEnabled()){
									log.debug("newCustomerPoNo: "+newCustomerPoNo);
								}
								for (int j = 0; j < orderLineLength; j++) {
									Element orderLineElement = (Element) inXML
											.getElementsByTagName(
													VSIConstants.ELE_ORDER_LINE)
											.item(j);
									String custPoNo = orderLineElement
											.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
									if (custPoNo == null || (custPoNo != null && custPoNo.trim().equals(""))) {
										orderLineElement.setAttribute(
												"IsNewLine",
												"Y");
										orderLineElement.setAttribute(
												VSIConstants.ATTR_CUST_PO_NO,
												newCustomerPoNo);
									}else{
										orderLineElement.setAttribute(
												"IsNewLine",
												"N");
									}
										

								}
								
								
							}else {
								NodeList nlOrderLines = null;
								String STSCondition =  VSIGeneralUtils
										.formXPATHWithOneCondition(
												"/Order/OrderLines/OrderLine",
												"LineType", "SHIP_TO_STORE");
								nlOrderLines = XMLUtil.getNodeListByXpath(
										inXML, STSCondition);
								String custPOForSTS = "";
								String custPOForPICK = "";
								if(nlOrderLines.getLength() > 0){
									Element lineSTSEle = null;
									for(int m= 0; m < nlOrderLines.getLength() ; m++ ){
										lineSTSEle = (Element) nlOrderLines.item(m);
										custPOForSTS = lineSTSEle.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
										if(custPOForSTS != null && !custPOForSTS.trim().equals("")) break;
									}
																	
									
									if(log.isDebugEnabled()){
										log.debug("custPOForSTS: "+custPOForSTS);
									}
									if(custPOForSTS == null || (custPOForSTS != null && custPOForSTS.trim().equals(""))){
										custPOForSTS = newCustomerPoNo;
									}
									if(log.isDebugEnabled()){
										log.debug("custPOForSTS: "+custPOForSTS);
									}
								}
								
								String PICKCondition =  VSIGeneralUtils
										.formXPATHWithOneCondition(
												"/Order/OrderLines/OrderLine",
												"LineType", "PICK_IN_STORE");
								nlOrderLines = XMLUtil.getNodeListByXpath(
										inXML, PICKCondition);
								if(nlOrderLines.getLength() > 0){
									for(int k=0; k < nlOrderLines.getLength(); k++ ){
										Element linePICKEle = (Element) nlOrderLines.item(k);
										custPOForPICK = linePICKEle.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
										if(custPOForPICK != null && !custPOForPICK.trim().equalsIgnoreCase("")) break;
									}
									
									if(log.isDebugEnabled()){
										log.debug("custPOForPICK: "+custPOForPICK);
									}
									if(custPOForPICK == null || (custPOForPICK != null && custPOForPICK.trim().equals(""))){
										custPOForPICK = newCustomerPoNo;
									}
									if(log.isDebugEnabled()){
										log.debug("custPOForPICK: "+custPOForPICK);
									}
								}
								
								for (int j = 0; j < orderLineLength; j++) {
									Element orderLineElement = (Element) inXML
											.getElementsByTagName(
													VSIConstants.ELE_ORDER_LINE)
											.item(j);
									String custPoNo = orderLineElement
											.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
									if (custPoNo == null || (custPoNo != null && custPoNo.trim().equals(""))) {
									
									
										orderLineElement.setAttribute(
												"IsNewLine",
												"Y");
										if(orderLineElement.getAttribute("LineType").equalsIgnoreCase("SHIP_TO_STORE") || 
												orderLineElement.getAttribute("FulfillmentType").equalsIgnoreCase("SHIP_TO_STORE")){
											
											orderLineElement.setAttribute(
													VSIConstants.ATTR_CUST_PO_NO,
													custPOForSTS);
										}else {
											orderLineElement.setAttribute(
													VSIConstants.ATTR_CUST_PO_NO,
													custPOForPICK);
										}
										
										}else{
											orderLineElement.setAttribute(
													"IsNewLine",
													"N");
										}

								}
								
								
								
							}
							
						}
					}
					// }
				}// end if ordertype check
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException("EXTN_ERROR", "EXTN_ERROR", "Exception");
		}
		if(log.isDebugEnabled()){
			log.debug("return inXML : "+XMLUtil.getXMLString(inXML));
		}
		return inXML;
	}

	private String generateOrderNo(YFSEnvironment env, Document inXML, boolean isNewLineAdded)
			throws Exception {
		Element rootElement = inXML.getDocumentElement();
		String tranDate = rootElement
				.getAttribute(VSIConstants.ATTR_ORDER_DATE);
					
		//Added code to generate CustPONo appended with WEB Order No for Web Orders
		String ordNo = 	rootElement
				.getAttribute(VSIConstants.ATTR_ORDER_NO);
				
		String ordType = 	rootElement
				.getAttribute(VSIConstants.ATTR_ORDER_TYPE);

		String entryType = 	rootElement
				.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);			

		tranDate = tranDate.replaceAll("\\-", "");// Removing the "-" from the
													// input OrderDate.
		tranDate = tranDate.substring(2, 6);// Getting YYMM from the input
		Element orderLineElement = (Element) inXML.getElementsByTagName(
				VSIConstants.ELE_ORDER_LINE).item(0);
		String shipNode = orderLineElement
				.getAttribute(VSIConstants.ATTR_SHIP_NODE);// enteredBy
		String shipNodePadded = ("00000" + shipNode).substring(shipNode
				.length());// Adding Leading Zeros

		String tranNumber = "";
		String seqNum = "VSI_SEQ_" + shipNode;
		tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
		String tranNumberPadded = ("00000" + tranNumber).substring(tranNumber
				.length()); // Adding Leading Zeros
		String tranNumberCustCustPO = ("000000" + tranNumber)
				.substring(tranNumber.length());
		if(log.isDebugEnabled()){
			log.info("=====>Printing Padded Transaction Number======="
					+ tranNumberPadded);
		}
		
		String regNumber = VSIConstants.REG_NUMBER;
		String grpNumber = VSIConstants.GROUP_NUMBER;
		String itemStatusNumber = VSIConstants.LINE_ITEM_STATUS_NUMBER;

		String customerPoNo = shipNodePadded + tranDate + regNumber
				+ tranNumberPadded + grpNumber + itemStatusNumber;

		// Also need to set another attribute for transaction number TBD for now
		// stored in CustCustPoNo
		if(!isNewLineAdded)
		rootElement.setAttribute(VSIConstants.ATTR_CUST_CUST_PO_NO,
				tranNumberCustCustPO);
				
		//For Web orders the order no should be appended to the CPO
		if ((ordType != null && ordType.equalsIgnoreCase("WEB"))
				&& (entryType != null && entryType.equalsIgnoreCase("WEB"))) {

			if (customerPoNo != null && customerPoNo.length() > 10) {
				customerPoNo = customerPoNo
						.substring(customerPoNo.length() - 10);
			}

			customerPoNo = ordNo + customerPoNo;
		}


		return customerPoNo;
	}// end generateOrderNo

}
