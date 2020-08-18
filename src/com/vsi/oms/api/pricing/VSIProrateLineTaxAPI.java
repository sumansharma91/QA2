package com.vsi.oms.api.pricing;

import java.util.HashSet;
import java.util.Set;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIProrateLineTaxAPI {

  private YFCLogCategory log = YFCLogCategory.instance(VSIProrateLineTaxAPI.class);

  public Document prorateLineTax(YFSEnvironment env, Document docInput) throws Exception {
    log.beginTimer("VSIProrateLineTaxAPI.prorateLineTax - BEGIN");

    Element eleOrder = docInput.getDocumentElement();

    String stringOLK = SCXmlUtil.getXpathAttribute(eleOrder, "//HeaderCharges/@OrderLineKey");

    Set<Element> targetElements = new HashSet<Element>();

    // Creating the change Order Document
    Document docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
    Element eleChangeOrder = docChangeOrder.getDocumentElement();
    eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    eleChangeOrder.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");

    Element noteEle = (Element) XPathAPI.selectSingleNode(eleOrder, "//Order/Notes");
    SCXmlUtil.importElement(eleChangeOrder, noteEle);

    boolean isChangereq = false;

    NodeList hrdChargeNL = eleOrder.getElementsByTagName(VSIConstants.ELE_HEADER_CHARGE);
    if (hrdChargeNL.getLength() > 0) {

      for (int i = 0; i < hrdChargeNL.getLength(); i++) {

        Element eleHeaderCharge = (Element) hrdChargeNL.item(i);

        if (!SCUtil.isVoid(eleHeaderCharge)) {
          String chrgeCatgry = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
          String chrgeName = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME);

          if (chrgeCatgry.equalsIgnoreCase("Adjustments")
              && (chrgeName.equalsIgnoreCase("Tax issue")
                  || chrgeName.equalsIgnoreCase("Shipping Appeasement"))) {

            isChangereq = true;

            Document docGetOrderList = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
            Element eleGetOrderList = docGetOrderList.getDocumentElement();
            eleGetOrderList.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
                eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
            Document docOrderList = VSIUtils.invokeAPI(env,
                VSIConstants.TEMPLATE_GET_ORDER_LIST_PRORATE_LINE_TAXES,
                VSIConstants.API_GET_ORDER_LIST, docGetOrderList);

            Element eleOrderList = docOrderList.getDocumentElement();
            Element eleOrderDetails = SCXmlUtil.getChildElement(eleOrderList,
                VSIConstants.ELE_ORDER);

            NodeList orderLineNL = null;

            if (!SCUtil.isVoid(stringOLK)) {

              orderLineNL = SCXmlUtil.getXpathNodes(eleOrderDetails,
                  "/OrderList/Order/OrderLines/OrderLine[@OrderLineKey='" + stringOLK + "']");

              // create Order line for the change Order Document
              Element eleOrderLines = SCXmlUtil.createChild(eleChangeOrder,
                  VSIConstants.ELE_ORDER_LINES);
              Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines,
                  VSIConstants.ELE_ORDER_LINE);
              eleOrderLine.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
              eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, stringOLK);

              if (!SCUtil.isVoid(orderLineNL)) {

                for (int m = 0; m < orderLineNL.getLength(); m++) {

                  Element orderDetailLine = (Element) orderLineNL.item(m);

                  Element lineTax = SCXmlUtil.getChildElement(orderDetailLine,
                      VSIConstants.ELE_LINE_TAXES);

                  double chrAmt = SCXmlUtil.getDoubleAttribute(eleHeaderCharge,
                      VSIConstants.ATTR_CHARGE_AMOUNT, 0.00);

                  NodeList nlInputLineTax = lineTax.getElementsByTagName("LineTax");

                  boolean isChargeNotFound = true;

                  if (nlInputLineTax.getLength() > 0) {

                    for (int f = 0; f < nlInputLineTax.getLength(); f++) {

                      Element lineTaxEle = (Element) nlInputLineTax.item(f);

                      String chrCtg = lineTaxEle.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
                      String chrNme = lineTaxEle.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
                      double previousTaxAmt = SCXmlUtil.getDoubleAttribute(lineTaxEle,
                          VSIConstants.ATTR_TAX, 0.00);

                      if (chrCtg.equalsIgnoreCase("Adjustments") && !SCUtil.isVoid(chrCtg)
                          && chrNme.equalsIgnoreCase("Tax issue") && !SCUtil.isVoid(chrNme)) {

                        previousTaxAmt = previousTaxAmt + chrAmt;

                        lineTaxEle.setAttribute(VSIConstants.ATTR_TAX,
                            Double.toString(previousTaxAmt));

                        isChargeNotFound = false;

                      } // end of if

                    } // end of line tax for

                  } // end of if tax elements

                  if (isChargeNotFound) {

                    Element newAdjLineTax = SCXmlUtil.createChild(lineTax,
                        VSIConstants.ELE_LINE_TAX);
                    newAdjLineTax.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY, "Adjustments");
                    newAdjLineTax.setAttribute(VSIConstants.ATTR_CHARGE_NAME, "Tax issue");
                    newAdjLineTax.setAttribute(VSIConstants.ATTR_TAX_NAME, "Adjusted Sales Tax");
                    newAdjLineTax.setAttribute(VSIConstants.ATTR_TAX, Double.toString(chrAmt));
                    newAdjLineTax.setAttribute(VSIConstants.ATTR_ACTION, "CREATE");

                  } // end of line tax if else

                  // import the Line taxes to the change Order document
                  SCXmlUtil.importElement(eleOrderLine, lineTax);

                }

              }

            } else {
              orderLineNL = eleOrderDetails.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
              double chrAmt = SCXmlUtil.getDoubleAttribute(eleHeaderCharge,
                  VSIConstants.ATTR_CHARGE_AMOUNT, 0.00);
              Element overAllTotalTaxes = SCXmlUtil.getChildElement(eleOrderDetails,
                  "OverallTotals");

              double grandTaxAmt = SCXmlUtil.getDoubleAttribute(overAllTotalTaxes, "GrandTax",
                  0.00);
              double hdrTaxAmt = SCXmlUtil.getDoubleAttribute(overAllTotalTaxes, "HdrTax", 0.00);
              double hdrChrgAmt = SCXmlUtil.getDoubleAttribute(overAllTotalTaxes, "HdrCharges",
                  0.00);

              double overAlltaxAmt = grandTaxAmt - hdrTaxAmt;

              if (chrgeCatgry.equalsIgnoreCase("Adjustments")
                  && (chrgeName.equalsIgnoreCase("Tax issue"))) {

                // Added Order lines for Change order Document
                Element eleOrderLines = SCXmlUtil.createChild(eleChangeOrder,
                    VSIConstants.ELE_ORDER_LINES);

                if (orderLineNL.getLength() > 0) {

                  for (int m = 0; m < orderLineNL.getLength(); m++) {

                    Element orderDetailLine = (Element) orderLineNL.item(m);
                    String olk = orderDetailLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);

                    // create Order line for the change Order Document

                    Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines,
                        VSIConstants.ELE_ORDER_LINE);
                    eleOrderLine.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
                    eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, olk);

                    Element overAllTotal = SCXmlUtil.getChildElement(orderDetailLine,
                        "LineOverallTotals");

                    double overAllLineTaxAmt = SCXmlUtil.getDoubleAttribute(overAllTotal,
                        VSIConstants.ATTR_TAX, 0.00);

                    if (overAllLineTaxAmt > 0) {

                      double txIssueAmt = (overAllLineTaxAmt / overAlltaxAmt) * chrAmt;

                      Element lineTaxes = SCXmlUtil.getChildElement(orderDetailLine,
                          VSIConstants.ELE_LINE_TAXES);

                      NodeList nlInputLineTax = lineTaxes.getElementsByTagName("LineTax");

                      boolean isChargeNotFound = true;

                      for (int f = 0; f < nlInputLineTax.getLength(); f++) {

                        Element lineTaxEle = (Element) nlInputLineTax.item(f);

                        String chrCtg = lineTaxEle.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
                        String chrNme = lineTaxEle.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
                        double previousTaxAmt = SCXmlUtil.getDoubleAttribute(lineTaxEle,
                            VSIConstants.ATTR_TAX, 0.00);

                        if (chrCtg.equalsIgnoreCase("Adjustments") && !SCUtil.isVoid(chrCtg)
                            && chrNme.equalsIgnoreCase("Tax issue") && !SCUtil.isVoid(chrNme)) {

                          previousTaxAmt = previousTaxAmt + txIssueAmt;

                          lineTaxEle.setAttribute(VSIConstants.ATTR_TAX,
                              Double.toString(previousTaxAmt));

                          isChargeNotFound = false;

                        } // end of if

                      } // end of for loop

                      if (isChargeNotFound) {

                        Element newAdjLineTax = SCXmlUtil.createChild(lineTaxes,
                            VSIConstants.ELE_LINE_TAX);
                        newAdjLineTax.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY,
                            "Adjustments");
                        newAdjLineTax.setAttribute(VSIConstants.ATTR_CHARGE_NAME, "Tax issue");
                        newAdjLineTax.setAttribute(VSIConstants.ATTR_TAX_NAME,
                            "Adjusted Sales Tax");
                        newAdjLineTax.setAttribute(VSIConstants.ATTR_TAX,
                            Double.toString(txIssueAmt));
                        newAdjLineTax.setAttribute(VSIConstants.ATTR_ACTION, "CREATE");

                      } // end of line tax if else

                      // import the Line taxes to the change Order document
                      SCXmlUtil.importElement(eleOrderLine, lineTaxes);

                    } // end of Overalltax if

                  } // end of for loop of each element of orderline

                } // end of if loop for more elements

              } // end of if of adjustment and tax issue

              else if (chrgeCatgry.equalsIgnoreCase("Adjustments")
                  && (chrgeName.equalsIgnoreCase("Shipping Appeasement"))) {

                if (hdrChrgAmt > 0) {

                  Element hdrChrges = SCXmlUtil.getChildElement(eleOrderDetails, "HeaderCharges");
                  NodeList nlInputHdrChrg = hdrChrges.getElementsByTagName("HeaderCharge");
                  
                  if (nlInputHdrChrg.getLength() > 0) {
	            	Element eleHeaderShippingCharge = SCXmlUtil.getXpathElement(hdrChrges,"//HeaderCharge[@ChargeName='"+chrgeName+"']");
	            	if(!YFCObject.isVoid(eleHeaderShippingCharge)){
	                    eleHeaderShippingCharge.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT,
	                        Double.toString(chrAmt));
	            	}else{
	            		Element newAdjHdrChrg = SCXmlUtil.createChild(hdrChrges, "HeaderCharge");
	                    newAdjHdrChrg.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY, "Adjustments");
	                    newAdjHdrChrg.setAttribute(VSIConstants.ATTR_CHARGE_NAME,
	                        "Shipping Appeasement");

	                    newAdjHdrChrg.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT,
	                        Double.toString(chrAmt));
	                    newAdjHdrChrg.setAttribute(VSIConstants.ATTR_ACTION, "CREATE");
	            	}

                    // import the Header Charges to the change Order document
                    SCXmlUtil.importElement(eleChangeOrder, hdrChrges);

                  } // end of if

                }

              }

            } // end of else part

            targetElements.add(eleHeaderCharge);

          } // end of if statment - Adj and tax issue

        } // end of element header charge if loop

      } // end of for loop

    } // end of main if loop

    for (Element e : targetElements) {
      e.getParentNode().removeChild(e);

    }    
    if (isChangereq) {

      VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_ORDER_HEADER_KEY,
          VSIConstants.API_CHANGE_ORDER, docChangeOrder);
    }
    return docInput;

  }

}