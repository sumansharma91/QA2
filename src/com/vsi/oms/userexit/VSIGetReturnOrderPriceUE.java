/************************************************************************************
 * File Name    : VSIGetReturnOrderPriceUE.java
 *
 * Organization     : IBM
 *
 * Description      : The purpose of this class is to complete processes on the 
 *              input retrieved from OMPGetReturnOrderPriceUE. This UE is
 *            is implemented as a Java class used to calculate charges 
 *            and taxes for the Return order.
 * 
 * Modification Log  :
 * -------------------------------------------------------------------------
 * Ver #      Date             Author                Remarks
 * ------------------------------------------------------------------------
 * 0.1      03-Mar-2017       IBM        Initial Version
 * 
 * -------------------------------------------------------------------------
 *************************************************************************************/
package com.vsi.oms.userexit;

import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.OMPGetReturnOrderPriceUE;

/**
 * This class processes with the input coming to the method. It apply changes to the input based on
 * the Return Reason calculating Charges and Taxes to the response.
 * 
 *
 */

public class VSIGetReturnOrderPriceUE implements OMPGetReturnOrderPriceUE {
  private static YFCLogCategory logger = (YFCLogCategory) YFCLogCategory
      .getLogger(VSIGetReturnOrderPriceUE.class.getName());

  /**
   * This method receive input and apply changes conditionally for charges and taxes.
   * 
   * @param env
   * @param inputXML
   * @return inputXML
   */
  public Document getReturnOrderPrice(YFSEnvironment env, Document inputXML)
      throws YFSUserExitException {

    String reasonCodeFroReturnStr = null;
    Map<String, List<Element>> mapLineCharges = new HashMap<String, List<Element>>();
    // ARE-517 : Moved to the loop which traverses the lines/line charges
    //List<Element> lsLineCharges = new ArrayList<Element>();
    
    if(logger.isDebugEnabled()){
    	logger.debug("************* inputXML*************** " + XMLUtil.getXMLString(inputXML));
    }
    try {

      /**
       * Add Null Check.
       */
      if (SCUtil.isVoid(inputXML)) {
        throw new YFSException("VSIGetReturnOrderPriceUE : Input document is null ");
      }

      Element eleInput = inputXML.getDocumentElement();
      NodeList orderLineNl = eleInput.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
      Document dSalesOrderIP = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
      Element eSalesOrderIP = dSalesOrderIP.getDocumentElement();
      eSalesOrderIP.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ((Element) orderLineNl.item(0))
          .getAttribute(VSIConstants.ATTR_DERIVED_FROM_ORDER_HEADER_KEY));

      if(logger.isDebugEnabled()){
      logger.debug(
          "******* Input to getOrderList  API for SO:: " + XMLUtil.getXMLString(dSalesOrderIP));
      }
      
      Document getOrderListForSODoc = invokeGetOrderList(env, dSalesOrderIP);

      String returnOrderType = eleInput.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
      double dbTotalOrderedQty = 0.00;
        Element eleSalesOrder = (Element) XPathAPI.selectSingleNode(getOrderListForSODoc,
            VSIConstants.XPATH_ORDERLIST_ORDER);
      if (YFCObject.isVoid(returnOrderType)) {

        // Copy the OrderType and EntryType on Return Order only if its not present already.

        String orderType = eleSalesOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);

        Document changeOrderDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
        Element changeOrderEle = changeOrderDoc.getDocumentElement();
        changeOrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
            eleInput.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
        changeOrderEle.setAttribute(VSIConstants.ATTR_ORDER_TYPE, orderType);
        changeOrderEle.setAttribute("BillToKey", eleSalesOrder.getAttribute("BillToKey"));
        changeOrderEle.setAttribute("ShipToKey", eleSalesOrder.getAttribute("ShipToKey"));

        String virtualStoreNo = getVirtualStore(env, "Call Center",
            VSIConstants.VIRTUAL_STORE_COM_CODE);
        changeOrderEle.setAttribute(VSIConstants.ATTR_ENTERED_BY, virtualStoreNo);

        // Create OrderLines for ChangeOrder
        Element changeOrderLineEles = SCXmlUtil.createChild(changeOrderEle,
            VSIConstants.ELE_ORDER_LINES);
        NodeList returnOrderLineList = eleInput.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

        // Add Order Line to the Change Order.
        for (int z = 0; z < returnOrderLineList.getLength(); z++) {
          Element returnOrderLineEle = (Element) returnOrderLineList.item(z);
          // ARE-517 : Moved here to correctly set the line charges for each line
          List<Element> lsLineCharges = new ArrayList<Element>();

          String derOrderLineKey = returnOrderLineEle.getAttribute("DerivedFromOrderLineKey");

          // Create OrderLine for ChangeOrder and set Shipnode and OrderLineKey
          Element changeOrderLineEle = SCXmlUtil.createChild(changeOrderLineEles,
              VSIConstants.ELE_ORDER_LINE);
          changeOrderLineEle.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
              returnOrderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));

          NodeList salesOrderLineList = eleSalesOrder
              .getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

          for (int y = 0; y < salesOrderLineList.getLength(); y++) {
            Element salesOrderLineEle = (Element) salesOrderLineList.item(y);
            String salesOrderLineKey = salesOrderLineEle
                .getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
            
            String soDelMethod = salesOrderLineEle
                    .getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
            
            String shipNode = null;

            if (salesOrderLineKey.equalsIgnoreCase(derOrderLineKey)) 
            {

             if("PICK".equals(soDelMethod))
             {
            	 // getting the corresponding DC associated with this Store
            	 // calling getNodeTransferScheduleList API
            	 String storeNo = salesOrderLineEle.getAttribute(VSIConstants.ATTR_SHIP_NODE);
            	 shipNode = getDCNo(env, storeNo);
             }
             else
             {
            	NodeList salesOrderLineScheduleList = salesOrderLineEle
                         .getElementsByTagName("Schedule");
                Element scheduleEle = (Element) salesOrderLineScheduleList.item(0);
                shipNode = scheduleEle.getAttribute(VSIConstants.ATTR_SHIP_NODE);
                    
             }
             
             changeOrderLineEle.setAttribute(VSIConstants.ATTR_SHIP_NODE, shipNode);
             if(!YFCCommon.isVoid(env.getTxnObject("ReceivingNodeForWhseReturn")))
    		 {
            	 changeOrderLineEle.setAttribute(VSIConstants.ATTR_SHIP_NODE, (String)env.getTxnObject("ReceivingNodeForWhseReturn"));
    		 }
             
             Element eleLineCharges = SCXmlUtil.getChildElement(salesOrderLineEle, VSIConstants.ELE_LINE_CHARGES);
 			 ArrayList<Element> alLineChargeList = SCXmlUtil.getChildren(eleLineCharges, VSIConstants.ELE_LINE_CHARGE);
             for(Element eleLineCharge : alLineChargeList){
	   	         double attrOrderedQtySales = SCXmlUtil.getDoubleAttribute(salesOrderLineEle, VSIConstants.ATTR_ORD_QTY);
	   	         double attrReturnableQtySales = SCXmlUtil.getDoubleAttribute(returnOrderLineEle, VSIConstants.ATTR_RETURNABLE_QTY);
	   	         double attrOrderedQtyReturn = SCXmlUtil.getDoubleAttribute(returnOrderLineEle, VSIConstants.ATTR_ORD_QTY);
	   	         double attrChargePerUnit = SCXmlUtil.getDoubleAttribute(((Element) eleLineCharge), VSIConstants.ATTR_CHARGE_PER_UNIT);
	   	         double attrChargePerLine = SCXmlUtil.getDoubleAttribute(((Element) eleLineCharge), VSIConstants.ATTR_CHARGE_PER_LINE);
	   	         
	   	         lsLineCharges.add(prorateLineCharges(eleLineCharge, attrOrderedQtySales, attrChargePerLine, attrChargePerUnit,attrOrderedQtyReturn,attrReturnableQtySales));
             }
             mapLineCharges.put(returnOrderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY), lsLineCharges);
             
              break;
            }
          }
        }

        // Change Order Template
        Document changeOrderTemplateDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
        changeOrderTemplateDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
            "");

        // Invoke Change order API
        VSIUtils.invokeAPI(env, changeOrderTemplateDoc,
            "changeOrder", changeOrderDoc);

      }
      double dbRetTotalQty = 0.00;
      for (int k = 0; k < orderLineNl.getLength(); k++) {
        Element orderLineEle = (Element) orderLineNl.item(k);
        reasonCodeFroReturnStr = orderLineEle.getAttribute(VSIConstants.ATTR_RETURN_REASON);
        String OrderedQtyStr = orderLineEle.getAttribute(VSIConstants.ATTR_ORD_QTY);
        double dOrderdQty = 0.0;

        if (!YFCObject.isVoid(OrderedQtyStr)) {
          dOrderdQty = Double.parseDouble(OrderedQtyStr);
        }

        dbRetTotalQty = dbRetTotalQty + dOrderdQty;
        if (!YFCObject.isVoid(reasonCodeFroReturnStr)) {
          Element lineChargesEle = (Element) orderLineEle
              .getElementsByTagName(VSIConstants.ELE_LINE_CHARGES).item(0);
          recalculateLineTax(getOrderListForSODoc, orderLineEle, dOrderdQty);
          List<Element> lineChargesList = mapLineCharges.get(orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
          if(!YFCObject.isVoid(lineChargesList) && lineChargesList.size() > 0){
        	  for(Element eleLineCharge : lineChargesList){
        		  String strChargeName = eleLineCharge.getAttribute("ChargeName");
        		  String strChargeXPath = "//OrderLine[@OrderLineKey='"+orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY)+"']/LineCharges/LineCharge[@ChargeName='"+strChargeName+"']";
        		  Element eleExistingCharge = SCXmlUtil.getXpathElement(orderLineEle,strChargeXPath);
        		  if(!YFCObject.isVoid(eleExistingCharge)){
        			  inputXML.adoptNode(eleLineCharge);
        			  lineChargesEle.replaceChild(eleLineCharge, eleExistingCharge);
        		  }else{
        			  inputXML.adoptNode(eleLineCharge);
                	  lineChargesEle.appendChild(eleLineCharge);  
        		  }
        	  }
          }
        }
      }

      NodeList salesOrderLineList = eleSalesOrder
              .getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
      for (int y = 0; y < salesOrderLineList.getLength(); y++) {
		Element salesOrderLineEle = (Element) salesOrderLineList.item(y);
		double dbOrderedQty = SCXmlUtil.getDoubleAttribute(salesOrderLineEle, VSIConstants.ATTR_ORD_QTY);
		dbTotalOrderedQty = dbTotalOrderedQty + dbOrderedQty;  
      }
      modifyHeaderCharges(env, getOrderListForSODoc, inputXML, reasonCodeFroReturnStr,dbTotalOrderedQty,dbRetTotalQty);

    } catch (Exception e) {
      logger.error("Exception in VSIGetReturnOrderPriceUE.getReturnOrderPrice ", e);
      throw new YFSException(
          "Exception in VSIGetReturnOrderPriceUE.getReturnOrderPrice " + e.getMessage());
    }

    if(logger.isDebugEnabled()){
    	logger.debug("************* output " + XmlUtils.getString(inputXML));
    }
    return inputXML;
  }
  
  private Element prorateLineCharges(Element eleLineCharge, double attrOrderedQty, double attrChargePerLine,
			double attrChargePerUnit, double attrReturnedQty, double attrReturnableQtySales){
	  Element eleCloneLineCharge = (Element) eleLineCharge.cloneNode(true);
	  if((YFCCommon.isDoubleVoid(attrChargePerUnit)|| !(attrChargePerUnit>0)) && attrChargePerLine > 0.00){
			DecimalFormat dfRMDown = new DecimalFormat(VSIConstants.DEC_FORMAT); 
			dfRMDown.setRoundingMode(RoundingMode.DOWN);

			DecimalFormat dfRMUp = new DecimalFormat(VSIConstants.DEC_FORMAT);
			
			double costperunit = attrChargePerLine/attrOrderedQty;
			String strCostperunit= dfRMDown.format(costperunit);
			
			eleCloneLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_UNIT,strCostperunit);
			eleCloneLineCharge.removeAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
			eleCloneLineCharge.removeAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);
			eleCloneLineCharge.removeAttribute("AmountFromAddnlLinePrices");
			eleCloneLineCharge.removeAttribute("InvoicedChargeAmount");
			eleCloneLineCharge.removeAttribute("InvoicedChargePerLine");
			eleCloneLineCharge.removeAttribute("InvoicedChargePerUnit");
			eleCloneLineCharge.removeAttribute("RemainingChargeAmount");
			eleCloneLineCharge.removeAttribute("RemainingChargePerLine");
			eleCloneLineCharge.removeAttribute("RemainingChargePerUnit");
			
			Double dbChargePerLine= (Double.valueOf(strCostperunit)) * attrReturnedQty;
			
			if(dbChargePerLine>0 && (YFCObject.isDoubleVoid(attrReturnableQtySales) || attrReturnableQtySales < 0)){
				eleCloneLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_LINE,dfRMUp.format(dbChargePerLine));
			}
		}
	  return eleCloneLineCharge;
  }

  /**
   * This method the entered by value which need to apply on Return Order from common codes
   * 
   * @param env
   * @param codeValue
   * @param codetype
   * @return virtualStore
   */
  private String getVirtualStore(YFSEnvironment env, String codeValue, String codetype) {

    String virtualStore = null;
    try {
      Document docForGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);

      Element eleCommonCode = docForGetCommonCodeList.getDocumentElement();
      eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, codeValue);
      eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, codetype);

      Document docAfterGetCommonCodeList = VSIUtils.invokeAPI(env,
          VSIConstants.API_COMMON_CODE_LIST, docForGetCommonCodeList);

      if (docAfterGetCommonCodeList.getDocumentElement().hasChildNodes()) {
        Element eleOutCommonCode = (Element) docAfterGetCommonCodeList
            .getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE).item(0);
        virtualStore = eleOutCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    //// System.out.println("***remorsePrd " +remorsePrd);

    // TODO Auto-generated method stub
    return virtualStore;
  }

  /**
   * This method calculate Line level tax which need to apply on Return Order.
   * 
   * @param getOrderListForSODoc
   * @param orderLineEle
   * @param dReturnOrderdQty
   * @return void
   */
  private void recalculateLineTax(Document getOrderListForSODoc, Element orderLineEle,
      double dReturnOrderdQty) {

    String strDerivedFromOrderLineKey = orderLineEle
        .getAttribute(VSIConstants.ATTR_DERIVED_FROM_ORDER_LINE_KEY);
    Element eleSalesOrderList = getOrderListForSODoc.getDocumentElement();
    Element eleOrderLines = (Element) eleSalesOrderList
        .getElementsByTagName(VSIConstants.ELE_ORDER_LINES).item(0);

    ArrayList<Element> orderLineList = SCXmlUtil.getChildren(eleOrderLines,
        VSIConstants.ELE_ORDER_LINE);

    Element orderLineSalesEle = null;
    Iterator<Element> itrOrderLine = orderLineList.iterator();

    while (itrOrderLine.hasNext()) {
      orderLineSalesEle = itrOrderLine.next();

      String strOrderLineKey = orderLineSalesEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);

      String stSalesOrderQty = orderLineSalesEle.getAttribute(VSIConstants.ATTR_ORD_QTY);
      double dSalesOrderdQty = 0.0;

      if (!YFCObject.isVoid(stSalesOrderQty)) {
        dSalesOrderdQty = Double.parseDouble(stSalesOrderQty);
      }

      if (strOrderLineKey.equalsIgnoreCase(strDerivedFromOrderLineKey) && dSalesOrderdQty != 0) {

        Element eleReturnLineTaxes = SCXmlUtil.getChildElement(orderLineEle,
            VSIConstants.ELE_LINE_TAXES);

        if (!YFCObject.isVoid(eleReturnLineTaxes)) {
          SCXmlUtil.removeNode(eleReturnLineTaxes);
        }

        Element eleSalesLineTaxes = SCXmlUtil.getChildElement(orderLineSalesEle,
            VSIConstants.ELE_LINE_TAXES);
        Element newTaxes = eleSalesLineTaxes;
        ArrayList<Element> salesLineTaxList = SCXmlUtil.getChildren(newTaxes,
            VSIConstants.ELE_LINE_TAX);
        Element eleSalesLineTax = null;
        Iterator<Element> itrOrderLineTax = salesLineTaxList.iterator();

        boolean bProrated = false;

        while (itrOrderLineTax.hasNext()) {
          eleSalesLineTax = itrOrderLineTax.next();
          String strTax = eleSalesLineTax.getAttribute(VSIConstants.ATTR_TAX);
          double dSalesTax = 0.0;

          if (!YFCObject.isVoid(strTax)) {
            dSalesTax = Double.parseDouble(strTax);
          }

          double newTax = 0.0;
          newTax = (dSalesTax * dReturnOrderdQty) / dSalesOrderdQty;
          newTax = (double) Math.round(newTax*100)/100;
        		  //YFCDoubleUtils.roundOff(newTax, 2);
          eleSalesLineTax.setAttribute(VSIConstants.ATTR_TAX, String.valueOf(newTax));
          eleSalesLineTax.removeAttribute(VSIConstants.ATTR_INVOICED_TAX);
          eleSalesLineTax.removeAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);
          bProrated = true;
        }

        if (bProrated) {
          SCXmlUtil.importElement(orderLineEle, newTaxes);

        }

      }
    }
  }
  
  private String getDCNo(YFSEnvironment env, String storeNo)
	      throws YFSException, RemoteException, YIFClientCreationException {

	  	String returnDC = "9004";
	  	Document ntsInp = SCXmlUtil.createDocument("NodeTransferSchedule");
	    Document ntsOp = VSIUtils.invokeAPI(env,
	        "global/template/api/getNodeTransferScheduleList_VSIGetReturnOrderPriceUE.xml", "getNodeTransferScheduleList",
	        ntsInp);
	    
	    if(ntsOp.getDocumentElement().hasChildNodes())
	    {
	    	Element nts = SCXmlUtil.getChildElement(ntsOp.getDocumentElement(), "NodeTransferSchedule");
	    	returnDC  = nts.getAttribute("FromNode");
	    }
	    
	    return returnDC;
	  }

  /**
   * invokeGetOrderList Method generates template and retrieve getOrderList API.
   * 
   * @param env
   * @param dSalesOrderIP
   * @return getOrderListOPDoc
   * @throws YIFClientCreationException
   * @throws RemoteException
   * @throws YFSException
   */
  private Document invokeGetOrderList(YFSEnvironment env, Document dSalesOrderIP)
      throws YFSException, RemoteException, YIFClientCreationException {

    /*
     * YFCDocument yfcGetOrdListTemplateDoc =
     * YFCDocument.createDocument(VSIConstants.ELE_ORDER_LIST); YFCElement yfcGetOrdListEle =
     * yfcGetOrdListTemplateDoc.getDocumentElement(); YFCElement yfcOrderEle =
     * yfcGetOrdListEle.createChild(VSIConstants.ELE_ORDER);
     * yfcOrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, "");
     * yfcOrderEle.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "");
     * yfcOrderEle.setAttribute(VSIConstants.ATTR_ORDER_TYPE, "");
     * yfcOrderEle.setAttribute(VSIConstants.ATTR_SELLING_OUTLET, "");
     * yfcOrderEle.setAttribute(VSIConstants.ATTR_ENTRY_TYPE, ""); YFCElement yfcOrderLinesEle =
     * yfcOrderEle.createChild(VSIConstants.ELE_ORDER_LINES); YFCElement yfcOrderLineEle =
     * yfcOrderLinesEle.createChild(VSIConstants.ELE_ORDER_LINE);
     * yfcOrderLineEle.setAttribute(VSIConstants.ATTR_DELIVERY_METHOD, "");
     * yfcOrderLineEle.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, "");
     * yfcOrderLineEle.setAttribute(VSIConstants.ATTR_ORIGINAL_ORDERED_QTY, "");
     * yfcOrderLineEle.setAttribute(VSIConstants.ATTR_ORD_QTY, "");
     * yfcOrderLineEle.setAttribute(VSIConstants.ATTR_RETURN_REASON, "");
     * yfcOrderLineEle.setAttribute(VSIConstants.ATTR_RETURNABLE_QTY, "");
     * yfcOrderLineEle.setAttribute("ShippedQuantity", ""); YFCElement yfcItemEle =
     * yfcOrderLineEle.createChild(VSIConstants.ELE_ITEM);
     * yfcItemEle.setAttribute(VSIConstants.ATTR_ITEM_ID, "");
     * yfcItemEle.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, "");
     * yfcItemEle.setAttribute(VSIConstants.ATTR_UOM, ""); YFCElement yfcItemDetailsEle =
     * yfcOrderLineEle.createChild(VSIConstants.ELE_ITEM_DETAILS_INFO);
     * 
     * yfcItemDetailsEle.setAttribute(VSIConstants.ATTR_ITEM_ID, ""); YFCElement yfcPrimaryInfoEle =
     * yfcItemDetailsEle .createChild(VSIConstants.ELE_PRIMARY_INFORMATION);
     * yfcPrimaryInfoEle.setAttribute(VSIConstants.ATTR_IS_RETURNABLE, ""); YFCElement
     * yfcLineChargesEle = yfcOrderLineEle.createChild(VSIConstants.ELE_LINE_CHARGES); YFCElement
     * yfcLineChargeEle = yfcLineChargesEle.createChild(VSIConstants.ELE_LINE_CHARGE); YFCElement
     * yfcLineTaxesEle = yfcOrderLineEle.createChild(VSIConstants.ELE_LINE_TAXES); YFCElement
     * yfcLineTaxEle = yfcLineTaxesEle.createChild(VSIConstants.ELE_LINE_TAX);
     * 
     * YFCElement yfcHeaderChargesEle = yfcOrderEle.createChild("HeaderCharges"); YFCElement
     * yfcHeaderChargeEle = yfcHeaderChargesEle.createChild("HeaderCharge");
     * 
     * YFCElement yfcTaxesEle = yfcOrderEle.createChild("HeaderTaxes"); YFCElement yfcTaxeEle =
     * yfcHeaderChargesEle.createChild("HeaderTax"); logger.debug(
     * "*************** GetOrderList Template:: " +
     * XMLUtil.getXMLString(yfcGetOrdListTemplateDoc.getDocument()));
     * 
     */

    Document getOrderListOPDoc = VSIUtils.invokeAPI(env,
        "global/template/api/getOrderList_VSIGetReturnOrderPriceUE.xml", "getOrderList",
        dSalesOrderIP);

    return getOrderListOPDoc;
  }

  /**
   * This method will validate if return reason code applicable for refunding shipping charges to
   * customer
   * 
   * @param env
   * @param reason
   * @return boolean
   */

  private boolean validateReturnReason(YFSEnvironment env, String reason, String enterpriseCode, String orderType) {

    try {
      Document commonCodeInput = XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
      commonCodeInput.getDocumentElement().setAttribute(VSIConstants.ATTR_CODE_TYPE,
          "RETURN_REASON");
      
      if(orderType.equalsIgnoreCase(VSIConstants.WHOLESALE)){
    	  commonCodeInput.getDocumentElement().setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, enterpriseCode);
      }else{
    	  commonCodeInput.getDocumentElement().setAttribute(VSIConstants.ATTR_CALLING_ORGANIZATION_CODE, enterpriseCode);
      }
      // RETURN_REASON This is being used to retrieve reason codes.

      if(logger.isDebugEnabled()){
    	  logger.debug("Common Code Input " + XMLUtil.getXMLString(commonCodeInput));
      }
      
      // Create Template Document for CommondCode API.
      Document commonCodeTemplate = SCXmlUtil.createDocument("CommonCodeList");
      Element commonCodeTemplateele = commonCodeTemplate.getDocumentElement();

      Element commonCode = SCXmlUtil.createChild(commonCodeTemplateele,
          VSIConstants.ELEMENT_COMMON_CODE);

      commonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, "");
      commonCode.setAttribute(VSIConstants.ATTR_CODE_LONG_DESC, "");

      if(logger.isDebugEnabled()){
    	  logger.debug("Common Code Template " + XMLUtil.getXMLString(commonCodeTemplate));
      }
      
      Document commonCodeOutput = VSIUtils.invokeAPI(env, commonCodeTemplate,
          VSIConstants.API_COMMON_CODE_LIST, commonCodeInput);

      if(logger.isDebugEnabled()){
    	  logger.debug("Common Code Ouptut " + XMLUtil.getXMLString(commonCodeOutput));
      }
      
      NodeList list = commonCodeOutput.getElementsByTagName(VSIConstants.ELE_COMMON_CODE);

      for (int i = 0; i < list.getLength(); i++) {
        Element ele = (Element) list.item(i);
        String codeValue = ele.getAttribute(VSIConstants.ATTR_CODE_VALUE);
        String codeValueDesc = ele.getAttribute(VSIConstants.ATTR_CODE_LONG_DESC);

        if (codeValue.equalsIgnoreCase(reason) && codeValueDesc.equalsIgnoreCase("Y"))
          return true;
      }

    }

    catch (Exception e) {
      logger.error("Exception in VSIGetReturnOrderPriceUE.validateReturnReason ", e);

    }

    return false;
  }

  /**
   * This method will determine whether header charges needs to be added to the return order or not,
   * by checking the open, shipped and return quantities of sales order
   * 
   * @param inDoc
   *          of Sales Order
   * @return boolean
   */

  private boolean IsAddHeaderCharges(Document inDoc) {

    try {
      NodeList orderLineList = inDoc.getDocumentElement().getElementsByTagName("OrderLine");

      int counter = 0;

      for (int i = 0; i < orderLineList.getLength(); i++) {
        Element orderLine = (Element) orderLineList.item(i);
        String olk = orderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);

        Node isReturnable = XPathAPI.selectSingleNode(orderLine,
            "ItemDetails/PrimaryInformation[@IsReturnable='N']");

        if (isReturnable != null) {
        	if(logger.isDebugEnabled()){
        		logger.debug("A line with IsReturnable flag found, OLK : " + olk);
        	}
          continue;
        }

        int ordQty = (int) Double.parseDouble(orderLine.getAttribute("OrderedQty"));
        int retQty = (int) Double.parseDouble(orderLine.getAttribute("ReturnableQty"));
        int shippedQty = (int) Double.parseDouble(orderLine.getAttribute("ShippedQuantity"));

        if(logger.isDebugEnabled()){
        	logger.debug("OLK : " + olk + " OrderedQty : " + ordQty + " ReturnableQty : " + retQty
        			+ " ShippedQuantity : " + shippedQty);
        }
        // if (ordQty > shippedQty)
        // return false;
        // else
        if (retQty > 0)
          return false;
        else {
        	if(logger.isDebugEnabled()){
        		logger.debug("OLK : " + olk + " incremented the header charges return logic counter");
        	}
          counter++;
        }

      }

      if(logger.isDebugEnabled()){
    	  logger.debug("Number of lines which are shipped and returned completely : " + counter);
      }
      
      return true;

    }

    catch (TransformerException e) {
      logger.error(
          "VSIGetReturnOrderPriceUE.IsAddHeaderCharges() exception occured " + e.getMessage());
      return false;
    }

  }

  /**
   * This method will call isAddHeaderCharges() and based on the result, it will negate the header
   * charges or add the header charges to the return order
   * 
   * @param env
   * 
   * @param salesOrder
   * @param retOrder
   * @param reasonCodeFroReturnStr
 * @throws YIFClientCreationException 
 * @throws RemoteException 
 * @throws YFSException 
 * @throws ParserConfigurationException 
   */

  private void modifyHeaderCharges(YFSEnvironment env, Document salesOrder, Document retOrder,
      String reasonCodeFroReturnStr, double dbTotalOrderedQty, double dbRetTotalQty) throws YFSException, RemoteException, YIFClientCreationException, ParserConfigurationException {

	int sOrderLineLength = salesOrder.getDocumentElement().getElementsByTagName(VSIConstants.ELE_ORDER_LINE).getLength();
	int rOrderLineLength = retOrder.getDocumentElement().getElementsByTagName(VSIConstants.ELE_ORDER_LINE).getLength();
	String strEnterpriseCode = retOrder.getDocumentElement().getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
	String strOrderType = (SCXmlUtil.getFirstChildElement(salesOrder.getDocumentElement())).getAttribute(VSIConstants.ATTR_ORDER_TYPE);
	if(logger.isDebugEnabled()){
		logger.debug("OrderType of Sales Order is: " + strOrderType);
	}
    NodeList headerChargeList = salesOrder.getDocumentElement()
        .getElementsByTagName("HeaderCharge");
    NodeList retHeaderChargeList = retOrder.getDocumentElement()
        .getElementsByTagName("HeaderCharge");

    NodeList headerTaxList = salesOrder.getDocumentElement().getElementsByTagName("HeaderTax");
   // NodeList retHeaderTaxList = retOrder.getDocumentElement().getElementsByTagName("HeaderTaxes");

    /* Check, if there are any header charges applied on sales order or not */
    if (headerChargeList.getLength() <= 0)
      return;

    boolean flag = IsAddHeaderCharges(salesOrder);
    boolean shippingFlag = validateReturnReason(env, reasonCodeFroReturnStr, strEnterpriseCode, strOrderType);

    if(logger.isDebugEnabled()){
    	logger.debug("IsAddHeaderCharges function returned flag " + flag);
    	logger.debug("validateReturnReason function returned flag " + shippingFlag);
    }
  //OMS-1863 start
  	Element eleOrder=salesOrder.getDocumentElement();
  	Element eleOverallTotals = SCXmlUtil.getXpathElement(eleOrder, "/OrderList/Order/OverallTotals");
  	String strGrandTotal=eleOverallTotals.getAttribute("GrandTotal");
  	double dGrandTotal=0.00;
  	if(!YFCObject.isVoid(strGrandTotal))
  		 dGrandTotal=Double.parseDouble(strGrandTotal);
  	if(dGrandTotal==0.00 && !strOrderType.equalsIgnoreCase(VSIConstants.WHOLESALE)){
  		 for (int i = 0; i < retHeaderChargeList.getLength(); i++) {
  		        Element charge = (Element) retHeaderChargeList.item(i);

  		        if (!SCUtil.isVoid(charge)) {
  		        	SCXmlUtil.setAttribute(charge, VSIConstants.ATTR_CHARGE_AMOUNT, VSIConstants.ZERO_DOUBLE);
  		        	charge.removeAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);
  	    			charge.removeAttribute("InvoicedChargeAmount");
  		          logger.debug("Header Charge Name : " + charge.getAttribute("ChargeName")
  		              + " charge amount has been made zero");
  		        } 
  		      }
  		 Element retHeaderTaxEle = (Element) retOrder.getDocumentElement()
  		          .getElementsByTagName("HeaderTax").item(0);

  		      if (!SCUtil.isVoid(retHeaderTaxEle)) {

  		        retHeaderTaxEle.setAttribute("Tax", "0");

  		        if(logger.isDebugEnabled()){
  		        	logger.debug("Header Tax Name : " + retHeaderTaxEle.getAttribute("TaxName")
  		        	+ " Tax amount has been made zero");
  		        }
  		      }
  		 return;
  	}
      //OMS-1863 end
    if(strOrderType.equalsIgnoreCase(VSIConstants.WHOLESALE)){

    	for (int i = 0; i < retHeaderChargeList.getLength(); i++) {
    		Element charge = (Element) retHeaderChargeList.item(i);

    		if (!SCUtil.isVoid(charge)) {
    			
    			if(flag){    				
    				if(!shippingFlag){
    					SCXmlUtil.setAttribute(charge, VSIConstants.ATTR_CHARGE_AMOUNT, VSIConstants.ZERO_DOUBLE);
    				}    				
    			}else{
    				SCXmlUtil.setAttribute(charge, VSIConstants.ATTR_CHARGE_AMOUNT, VSIConstants.ZERO_DOUBLE);
    			}
    			    			
    			charge.removeAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);
    			charge.removeAttribute("InvoicedChargeAmount");
    			
    			if(logger.isDebugEnabled()){
    				logger.debug("Header Charge Name : " + charge.getAttribute(VSIConstants.ATTR_CHARGE_NAME)
    				+ " charge amount has been set to " + charge.getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT));
    			}
    		}
    	}

    	Element retHeaderTaxEle = (Element) retOrder.getDocumentElement()
    			.getElementsByTagName(VSIConstants.ELE_HEADER_TAXES).item(0);

    	if (!SCUtil.isVoid(retHeaderTaxEle)) {

    		retHeaderTaxEle.setAttribute(VSIConstants.ATTR_TAX, VSIConstants.ZERO_DOUBLE);

    		if(logger.isDebugEnabled()){
    			logger.debug("Header Tax Name : " + retHeaderTaxEle.getAttribute(VSIConstants.ATTR_TAX_NAME)
    			+ " Tax amount has been made zero");
    		}
    	}

    	return;

    }
    //OMS-1689 : Start
    Document docInput=null;
	docInput = XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
	Element eleCommonCode = docInput.getDocumentElement();
	eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, VSIConstants.CODETYPE_MARKETPLACE_ENT);
	eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, strEnterpriseCode);
	if(logger.isDebugEnabled()){
		logger.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInput));
	}
	Document docCommonCodeListOP=VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docInput);
	if(logger.isDebugEnabled()){
		logger.debug("Output for getCommonCodeList :" +XMLUtil.getXMLString(docCommonCodeListOP));
	}
	Element commonCodeListElement = docCommonCodeListOP.getDocumentElement();
	String strEntType=null;
	if(commonCodeListElement.hasChildNodes()){
		Element eleCommonCode1 = SCXmlUtil.getChildElement(commonCodeListElement, VSIConstants.ELE_COMMON_CODE);
		strEntType = SCXmlUtil.getAttribute(eleCommonCode1, VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
	}
	
    if (shippingFlag == true && (!YFCObject.isVoid(strEntType) && VSIConstants.ENT_TYPE_1.equals(strEntType)))
	{
    	
		 // Modifying the Header Charges by removing the unwanted attributes
		Element retHeaderChargesEle = (Element) retOrder.getDocumentElement()
		    .getElementsByTagName("HeaderCharges").item(0);
		
		Element retHeaderChargeEle = (Element) retOrder.getDocumentElement()
				.getElementsByTagName("HeaderCharge").item(0);
		if (sOrderLineLength != rOrderLineLength || YFCObject.isVoid(retHeaderChargeEle)) 
		{
			if(!YFCObject.isVoid(retHeaderChargeEle)){
				retHeaderChargesEle.removeChild(retHeaderChargeEle);
			}
			if (!YFCObject.isVoid(retHeaderChargesEle)) {
		
				Element saleHeaderCharge = (Element) headerChargeList.item(0);
				Element retCharge = XMLUtil.importElement(retHeaderChargesEle, saleHeaderCharge);
		
				if (!YFCObject.isVoid(retCharge)) {
					retCharge.removeAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);
					retCharge.removeAttribute("InvoicedChargeAmount");
					if(logger.isDebugEnabled()){
						logger.debug("Charge Name : " + retCharge.getAttribute("ChargeName")
						+ " has been added to return order");
					}
				}
		
				double dbChargeAmount = SCXmlUtil.getDoubleAttribute(retCharge,
						VSIConstants.ATTR_CHARGE_AMOUNT);
				DecimalFormat dfRMDown = new DecimalFormat(VSIConstants.DEC_FORMAT);
				//dfRMDown.setRoundingMode(RoundingMode.DOWN);
				if (dbTotalOrderedQty != 0) {
					double dbChargePerUnit = dbChargeAmount / dbTotalOrderedQty;
					
					retCharge.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT,
							dfRMDown.format(dbRetTotalQty * dbChargePerUnit));
				}
			}
		}
		
		Element retHeaderTaxEle = (Element) retOrder.getDocumentElement()
		          .getElementsByTagName("HeaderTax").item(0);
		
		if (SCUtil.isVoid(retHeaderTaxEle)) {
		    Element retHeaderTaxesEle = (Element) retOrder.getDocumentElement()
		        .getElementsByTagName("HeaderTaxes").item(0);
		
		    if(!YFCObject.isVoid(retHeaderTaxesEle)){
		    	
		    Element saleHeadertax = (Element) headerTaxList.item(0);
		    Element retTax = XMLUtil.importElement(retHeaderTaxesEle, saleHeadertax);
		    DecimalFormat dfRMDown = new DecimalFormat(VSIConstants.DEC_FORMAT);
		        if(!YFCObject.isVoid(retTax))
		        {
		        	String tax = "0.00";
		        	tax = retTax.getAttribute("Tax");
		        	
		        	double dblTax = Double.parseDouble(tax);
		        	double dbTaxPerUnit = dblTax / dbTotalOrderedQty;
		        	retTax.setAttribute("Tax", dfRMDown.format(dbRetTotalQty * dbTaxPerUnit));
			        retTax.removeAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);
			        retTax.removeAttribute("InvoicedTax");
			        if(logger.isDebugEnabled()){
			        	logger
			        	.debug("Tax Name : " + retTax.getAttribute("Tax") + " has been added to return order");
			        }
		        }
		    }
		    Element retHeaderTaxSummaryEle = (Element) retOrder.getDocumentElement()
		            .getElementsByTagName("TaxSummary").item(0);
		
		        if (!SCUtil.isVoid(retHeaderTaxSummaryEle)) {
		          SCXmlUtil.removeNode(retHeaderTaxSummaryEle);
		
		        }
		  }
		
		return;
	}
    else if ((flag == false || shippingFlag == false) 
    		|| (!YFCObject.isVoid(strEntType) && VSIConstants.ENT_TYPE_2.equals(strEntType))) {

      for (int i = 0; i < retHeaderChargeList.getLength(); i++) {
        Element charge = (Element) retHeaderChargeList.item(i);

        if (!SCUtil.isVoid(charge)) {
          if ("Shipping".equalsIgnoreCase(charge.getAttribute("ChargeCategory"))) {
            charge.setAttribute("ChargeAmount", "0");
          }
          if(logger.isDebugEnabled()){
        	  logger.debug("Header Charge Name : " + charge.getAttribute("ChargeName")
        	  + " charge amount has been made zero");
          }
        }
      }

      Element retHeaderTaxEle = (Element) retOrder.getDocumentElement()
          .getElementsByTagName("HeaderTax").item(0);

      if (!SCUtil.isVoid(retHeaderTaxEle)) {

        retHeaderTaxEle.setAttribute("Tax", "0");

        if(logger.isDebugEnabled()){
        	logger.debug("Header Tax Name : " + retHeaderTaxEle.getAttribute("TaxName")
        	+ " Tax amount has been made zero");
        }
      }

      return;

    }
//OMS-1689: End
    else if ((flag == true && shippingFlag == true)) 
    {
      // Modifying the Header Charges by removing the unwanted attributes
      Element retHeaderChargesEle = (Element) retOrder.getDocumentElement()
          .getElementsByTagName("HeaderCharges").item(0);
      retHeaderChargesEle.removeAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);
      retHeaderChargesEle.removeAttribute("InvoicedChargeAmount");
      if(logger.isDebugEnabled()){
    	  logger.debug("Header Charge Name : " + retHeaderChargesEle.getAttribute("ChargeName")
    	  + " has been added to return order");
      }
      
      // Modifying Header Tax element to Return Order

      Element retHeaderTaxEle = (Element) retOrder.getDocumentElement()
          .getElementsByTagName("HeaderTax").item(0);

      if (SCUtil.isVoid(retHeaderTaxEle)) {
        Element retHeaderTaxesEle = (Element) retOrder.getDocumentElement()
            .getElementsByTagName("HeaderTaxes").item(0);

        if(!YFCObject.isVoid(retHeaderTaxesEle)){
        Element saleHeadertax = (Element) headerTaxList.item(0);
        Element retTax = XMLUtil.importElement(retHeaderTaxesEle, saleHeadertax);

            if(!YFCObject.isVoid(retTax)){
        retTax.removeAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);
        retTax.removeAttribute("InvoicedTax");
        if(logger.isDebugEnabled()){
        	logger
        	.debug("Tax Name : " + retTax.getAttribute("Tax") + " has been added to return order");
        }
            }
        }
      }

      Element retHeaderTaxSummaryEle = (Element) retOrder.getDocumentElement()
          .getElementsByTagName("TaxSummary").item(0);

      if (!SCUtil.isVoid(retHeaderTaxSummaryEle)) {
        SCXmlUtil.removeNode(retHeaderTaxSummaryEle);

      }
      
      return;
    }
  }
}