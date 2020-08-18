package com.vsi.oms.api.pricing;

import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class is added as part of JIRA SU-53
 * 
 * This class is called from the AddModifyCharges screen in Call Center to pro-rate the header
 * charges to each line
 * 
 * @author IBM
 *
 */
public class VSIProrateLineChargesAPI {

  private YFCLogCategory log = YFCLogCategory.instance(VSIProrateLineChargesAPI.class);

  public Document prorateLineCharges(YFSEnvironment env, Document docInput) throws Exception {
    log.beginTimer("VSIProrateLineChargesAPI.prorateLineCharges - BEGIN");

    DecimalFormat dfRMDown = new DecimalFormat(VSIConstants.DEC_FORMAT); 
	dfRMDown.setRoundingMode(RoundingMode.DOWN);
	DecimalFormat dfRMUp = new DecimalFormat(VSIConstants.DEC_FORMAT);
	
    Element eleOrder = docInput.getDocumentElement();
    Element eleOrderLines = null;
    Set<Element> stDeleteHdrChrgEle = new HashSet<Element>();
    HashMap hmChargeName_IsDiscount = new HashMap();
    
    try {
      Element eleExtn = SCXmlUtil.createChild(eleOrder, VSIConstants.ELE_EXTN);
      String stringOLK = SCXmlUtil.getXpathAttribute(eleOrder, "//HeaderCharges/@OrderLineKey");
      
      Document docGetOrderList = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
      Element eleGetOrderList = docGetOrderList.getDocumentElement();
      eleGetOrderList.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
          eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
      Document docOrderList = VSIUtils.invokeAPI(env,
          VSIConstants.TEMPLATE_GET_ORDER_LIST_PRORATE_LINE_CHARGES,
          VSIConstants.API_GET_ORDER_LIST, docGetOrderList);
      Element eleOrderList = docOrderList.getDocumentElement();
      Element eleOrderDetails = SCXmlUtil.getChildElement(eleOrderList,
          VSIConstants.ELE_ORDER);
      
      Boolean isWholesaleOrder = false;
      String orderType = SCXmlUtil.getAttribute(eleOrderDetails, VSIConstants.ATTR_ORDER_TYPE);
      String entryType = SCXmlUtil.getAttribute(eleOrderDetails, VSIConstants.ATTR_ENTRY_TYPE);
      String documentType = SCXmlUtil.getAttribute(eleOrderDetails, VSIConstants.ATTR_DOCUMENT_TYPE);
      String enterpriseCode = SCXmlUtil.getAttribute(eleOrderDetails, VSIConstants.ATTR_ENTERPRISE_CODE);
      if(orderType.equalsIgnoreCase(VSIConstants.WHOLESALE) || entryType.equalsIgnoreCase(VSIConstants.WHOLESALE)){
    	  isWholesaleOrder = true;
      }
      
      Document getChargeNameListInXML = SCXmlUtil.createDocument(VSIConstants.ATTR_CHARGE_NAME);
      Element eleChargeName = getChargeNameListInXML.getDocumentElement();
      SCXmlUtil.setAttribute(eleChargeName, VSIConstants.ATTR_DOCUMENT_TYPE, documentType);
      SCXmlUtil.setAttribute(eleChargeName, VSIConstants.ATTR_CALL_ORG_CODE, enterpriseCode);
      SCXmlUtil.setAttribute(eleChargeName, VSIConstants.ATTR_CHARGE_CATEGORY, enterpriseCode);
      SCXmlUtil.setAttribute(eleChargeName, VSIConstants.ATTR_CHARGE_CATEGORY.concat(VSIConstants.ATTR_QUERY_TYPE), VSIConstants.LIKE);
      // Document chargeNameListXML = VSIUtils.invokeAPI(env, VSIConstants.API_GET_CHARGE_NAME_LIST, getChargeNameListInXML);
      Document getChargeNameListOutXML = VSIUtils.invokeService(env, VSIConstants.SERVICE_GET_CHARGE_NAME_LIST, getChargeNameListInXML);
      
      NodeList nlChargeName = getChargeNameListOutXML.getElementsByTagName(VSIConstants.ATTR_CHARGE_NAME);
      NodeList hrdChargeNL = eleOrder.getElementsByTagName(VSIConstants.ELE_HEADER_CHARGE);
      
      if(isWholesaleOrder){

    	  for(int a = 0; a < nlChargeName.getLength(); a++){

    		  Element eleChargeNameOut = (Element) nlChargeName.item(a);
    		  String chCatAndName = SCXmlUtil.getAttribute(eleChargeNameOut, VSIConstants.ATTR_CHARGE_CATEGORY_AND_NAME);
    		  hmChargeName_IsDiscount.put(chCatAndName, SCXmlUtil.getAttribute(eleChargeNameOut, VSIConstants.ATTR_IS_DISCOUNT));
    	  }

    	  for(int a = 0; a < hrdChargeNL.getLength(); a++){

    		  Element eleChargeIn = (Element) hrdChargeNL.item(a);
    		  String chCat = SCXmlUtil.getAttribute(eleChargeIn, VSIConstants.ATTR_CHARGE_CATEGORY);
    		  String chName = SCXmlUtil.getAttribute(eleChargeIn, VSIConstants.ATTR_CHARGE_NAME);
    		  if(hmChargeName_IsDiscount.containsKey(chCat.concat("/".concat(chName)))){
    			  
    			  String isDiscount = (String) hmChargeName_IsDiscount.get(chCat.concat("/".concat(chName)));
    			  SCXmlUtil.setAttribute(eleChargeIn, VSIConstants.ATTR_IS_DISCOUNT, isDiscount);
    		  }
    	  }
    	  
    	  if(log.isDebugEnabled()){
    		  log.debug("Input XML after setting discount flag: " + SCXmlUtil.getString(eleOrder));
    	  }
      }
      
      if (hrdChargeNL.getLength() > 0) {

        int hdrChrgeIndex = 0;
        for (int i = 0; i < hrdChargeNL.getLength(); i++) {

          Element eleHeaderCharge = (Element) hrdChargeNL.item(i);

          if (eleHeaderCharge != null) {
            String chrgeCatgry = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
            String chargeName = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
            String isDiscount = eleHeaderCharge.getAttribute(VSIConstants.ATTR_IS_DISCOUNT);

            // Adding reference field for coupon

            String refCoupon = eleHeaderCharge.getAttribute("Reference");

            if ((!isWholesaleOrder && (chrgeCatgry.equals(VSIConstants.DISCOUNT)
            		|| chrgeCatgry.equals(VSIConstants.CUSTOMER_APPEASEMENT)
            		|| chrgeCatgry.equals("Adjustments") || chrgeCatgry.equals("Replacement charge"))) || 
            		(isWholesaleOrder && (/*isDiscount.equalsIgnoreCase(VSIConstants.FLAG_Y) &&*/ !chargeName.toUpperCase().contains(VSIConstants.UPPER_CASE_SHIPPING)))) {
              
              hdrChrgeIndex = i;
              String isNewCharge = null;
              String discountedAmt = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
              
              if (eleHeaderCharge.hasAttribute(VSIConstants.ATTR_IS_NEW_CHARGE)) {

                isNewCharge = eleHeaderCharge.getAttribute(VSIConstants.ATTR_IS_NEW_CHARGE);

              }
              
              HashMap<String, String> prortdLineDiscountMap = prorateHeaderDiscount(eleOrderDetails,
                  discountedAmt, isNewCharge, stringOLK);

              String chargeKey = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY);

              if (eleOrder.getElementsByTagName(VSIConstants.ELE_ORDER_LINES) == null || !(eleOrder
                  .getElementsByTagName(VSIConstants.ELE_ORDER_LINES).getLength() > 0)) {
                eleOrderLines = SCXmlUtil.createChild(eleOrder, VSIConstants.ELE_ORDER_LINES);
              }
              NodeList orderLineNL = null;
              if (!YFCObject.isVoid(stringOLK)) {
                orderLineNL = SCXmlUtil.getXpathNodes(eleOrderDetails,
                    "/OrderList/Order/OrderLines/OrderLine[@OrderLineKey='" + stringOLK + "']");
              } else {
                orderLineNL = eleOrderDetails.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
              }

              if (orderLineNL.getLength() > 0) {

                for (int m = 0; m < orderLineNL.getLength(); m++) {

                  Element orderDetailLine = (Element) orderLineNL.item(m);
                  String olk = orderDetailLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
                  String strOrderedQty = orderDetailLine.getAttribute(VSIConstants.ATTR_ORD_QTY);
                  int intOrderedQty = Integer.parseInt(strOrderedQty);
                  
                  // ARE-437 : Added to skip pro-rating charges to this line, as the qty is 0 : BEGIN
                  if (intOrderedQty == 0) {
                	  continue;
                  }
                  // ARE-437 : Added to skip pro-rating charges to this line, as the qty is 0 : END

                  // START - Fix for SU-109
                  Element eleOrderLine = SCXmlUtil.getXpathElement(eleOrderLines,
                      "//OrderLine[@OrderLineKey='" + olk + "']");
                  if (eleOrderLine == null) {
                    eleOrderLine = SCXmlUtil.createChild(eleOrderLines,
                        VSIConstants.ELE_ORDER_LINE);
                  }
                  // END - Fix for SU-109

                  eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, olk);
                  eleOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_MODIFY);

                  // START - Fix for SU-109
                  Element lineCharges = SCXmlUtil.getChildElement(eleOrderLine,
                      VSIConstants.ELE_LINE_CHARGES);

                  if (lineCharges == null) {
                    lineCharges = SCXmlUtil.createChild(eleOrderLine,
                        VSIConstants.ELE_LINE_CHARGES);
                  }
                  NodeList nlInputLineCharge = lineCharges.getElementsByTagName("LineCharge");

                  // END - Fix for SU-109

                  NodeList lineChargeNL = orderDetailLine
                      .getElementsByTagName(VSIConstants.ELE_LINE_CHARGE);
                  String hdrCtg = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
                  String hdrNme = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME);

                  if (lineChargeNL.getLength() > 0) {

                    for (int f = 0; f < lineChargeNL.getLength(); f++) {

                      Element lineChargeEle = (Element) lineChargeNL.item(f);

                      String chrCtg = lineChargeEle.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
                      String chrNme = lineChargeEle.getAttribute(VSIConstants.ATTR_CHARGE_NAME);

                      if(!VSIConstants.DISCOUNT.equals(hdrCtg)){
                      if (chrCtg.equals(VSIConstants.DISCOUNT)
                          && hdrCtg.equals(VSIConstants.DISCOUNT)
                          && chrNme.equals(VSIConstants.MANUAL_DISCOUNT_$)
                          && hdrNme.equals(VSIConstants.MANUAL_DISCOUNT_$)) {

                        String previousValue = lineChargeEle
                            .getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
                        Double dPreviousValue = new Double("0.0");
                        if (previousValue != null && !previousValue.trim().equals(""))
                          dPreviousValue = Double.parseDouble(previousValue);
                        Double dTotalLineDiscount = Double
                            .parseDouble(prortdLineDiscountMap.get(olk).toString());

                        if (dPreviousValue > 0) {
                          dTotalLineDiscount = dTotalLineDiscount + dPreviousValue;
                        }

                        double costperunit = dTotalLineDiscount/intOrderedQty;
                        String strCostperunit= dfRMDown.format(costperunit);
                        
                        dTotalLineDiscount= dTotalLineDiscount -((Double.valueOf(strCostperunit)) * intOrderedQty);
                        if(log.isDebugEnabled()){
                			log.debug("costperunit"+costperunit);
                        }
                        // Update the line charge
                        lineChargeEle.setAttribute("PreviousValue", dPreviousValue.toString());
                        lineChargeEle.removeAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
                        lineChargeEle.setAttribute(VSIConstants.ATTR_CHARGE_PER_LINE,
                            dfRMUp.format(dTotalLineDiscount));
                        lineChargeEle.setAttribute(VSIConstants.ATTR_CHARGE_PER_UNIT,
                                strCostperunit);
                        Node copyNode = docInput.importNode(lineChargeEle, true);
                        lineCharges.appendChild(copyNode);

                      } else {
                        boolean isNew = true;
                        if (prortdLineDiscountMap.size() > 0
                            && prortdLineDiscountMap.containsKey(olk)) {

                          Element lineCharge = null;
                          Element eleInputLineCharge = null;
                          if (nlInputLineCharge.getLength() > 0) {
                            for (int lcCount = 0; lcCount < nlInputLineCharge
                                .getLength(); lcCount++) {

                              eleInputLineCharge = (Element) nlInputLineCharge.item(lcCount);

                              if (hdrNme.equals(eleInputLineCharge.getAttribute("ChargeName"))) {
                                isNew = false;
                              } else {
                                //System.out.println("DOINGTHIS");
                              }
                            }
                          } else {
                            //System.out.println("DOINGTHAT");
                          }
                          if (isNew) {
                            //System.out.println("HEREadainfdainfd");
                            // Add the line charge
                            lineCharge = SCXmlUtil.createChild(lineCharges,
                                VSIConstants.ELE_LINE_CHARGE);
                            lineCharges.appendChild(lineCharge);
                          } else {
                            //System.out.println("THERE");
                            lineCharge = eleInputLineCharge;
                          }

                          double dTotalLineDiscount = Double.parseDouble(prortdLineDiscountMap.get(olk));
                          double costperunit = dTotalLineDiscount/intOrderedQty;
                          String strCostperunit= dfRMDown.format(costperunit);
                          
                          dTotalLineDiscount= dTotalLineDiscount -((Double.valueOf(strCostperunit)) * intOrderedQty);
                          if(log.isDebugEnabled()){
                  			log.debug("costperunit"+costperunit);
                          }
                          
                          lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY, chrgeCatgry);
                          lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_NAME, hdrNme);
                          lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY, chargeKey);
                          lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_LINE,
                        		  dfRMUp.format(dTotalLineDiscount));
                          lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_UNIT,
                        		  strCostperunit);
                          lineCharge.setAttribute(VSIConstants.ATTR_IS_NEW_CHARGE, "Y");

                          // Adding reference field for Coupon
                          if (!SCUtil.isVoid(refCoupon)) {
                            lineCharge.setAttribute("Reference", refCoupon);
                          }

                          eleExtn.setAttribute("IsTranLevel", "Y");
                          prortdLineDiscountMap.remove(olk);

                        }
                      }
                      }
                    }
                  } else {

                    boolean isNew = true;
                    if (prortdLineDiscountMap.size() > 0
                        && prortdLineDiscountMap.containsKey(olk)) {

                      Element lineCharge = null;
                      Element eleInputLineCharge = null;
                      if (nlInputLineCharge.getLength() > 0) {
                        for (int lcCount = 0; lcCount < nlInputLineCharge.getLength(); lcCount++) {
                          eleInputLineCharge = (Element) nlInputLineCharge.item(lcCount);
                          if (hdrNme.equals(eleInputLineCharge.getAttribute("ChargeName"))) {
                            isNew = false;
                          } else {
                            //System.out.println("DOINGTHIS");
                          }
                        }
                      } else {
                        //System.out.println("DOINGTHAT");
                      }
                      if (isNew) {
                        // Add the line charge
                        lineCharge = SCXmlUtil.createChild(lineCharges,
                            VSIConstants.ELE_LINE_CHARGE);
                        lineCharges.appendChild(lineCharge);
                      } else {
                        lineCharge = eleInputLineCharge;
                      }

                      double dTotalLineDiscount = Double.parseDouble(prortdLineDiscountMap.get(olk));
                      double costperunit = dTotalLineDiscount/intOrderedQty;
                      String strCostperunit= dfRMDown.format(costperunit);
                      
                      dTotalLineDiscount= dTotalLineDiscount -((Double.valueOf(strCostperunit)) * intOrderedQty);
                      if(log.isDebugEnabled()){
              			log.debug("costperunit"+costperunit);
                      }
                      
                      lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY, chrgeCatgry);
                      lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_NAME, hdrNme);
                      lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_NAME_KEY, chargeKey);
                      lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_LINE,
                          dfRMUp.format(dTotalLineDiscount));
                      lineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_UNIT,
                    		  strCostperunit);
                      lineCharge.setAttribute(VSIConstants.ATTR_IS_NEW_CHARGE, "Y");

                      // Adding reference field for Coupon
                      if (!SCUtil.isVoid(refCoupon)) {
                        lineCharge.setAttribute("Reference", refCoupon);
                      }

                      eleExtn.setAttribute("IsTranLevel", "Y");
                      prortdLineDiscountMap.remove(olk);
                    }
                  }
                }
              }
              // Remove the header charge
              Node hdrChrge = eleOrder.getElementsByTagName(VSIConstants.ELE_HEADER_CHARGE)
                  .item(hdrChrgeIndex);
              Element hrdChrgsEle = (Element) eleOrder
                  .getElementsByTagName(VSIConstants.ELE_HEADER_CHARGES).item(0);
              
              stDeleteHdrChrgEle.add(eleHeaderCharge);
            }
          }
        }
      }
      // Save the line charges on the order
      // START - Fix for SU-76

      eleOrder.removeAttribute("changeOrder");
      eleOrder.removeAttribute("IgnoreOrdering");
      eleOrder.setAttribute("DisplayLocalizedFieldInLocale", "en_US_EST");
      eleOrder.setAttribute("Action", "MODIFY");

      for(Element eleDeleteHrdChrg : stDeleteHdrChrgEle){
    	  eleDeleteHrdChrg.getParentNode().removeChild(eleDeleteHrdChrg);
      }

      // END - Fix for SU-76
      if(log.isDebugEnabled()){
    	  log.debug("Call changeOrder with input: " + SCXmlUtil.getString(docInput));
      }

      VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_ORDER_HEADER_KEY,
          VSIConstants.API_CHANGE_ORDER, docInput);

    } catch (RemoteException re) {
      log.error("RemoteException in VSIProrateLineChargesAPI.prorateLineCharges() : ", re);
      throw re;
    } catch (YIFClientCreationException yife) {
      log.error(
          "YIFClientCreationException in VSIProrateLineChargesAPI.prorateLineCharges() : ",
          yife);
      throw yife;
    } catch (YFSException yfse) {
      log.error("YFSException in VSIProrateLineChargesAPI.prorateLineCharges() : ", yfse);
      throw yfse;
    } catch (Exception e) {
      log.error("Exception in VSIProrateLineChargesAPI.prorateLineCharges() : ", e);
      throw e;
    }finally{
    	
    	hmChargeName_IsDiscount.clear();
    	hmChargeName_IsDiscount = null;
    }

    log.endTimer("VSIProrateLineChargesAPI.prorateLineCharges - END");
    return docInput;
  }

  /**
   * This class calculates the pro-rated line charge for each line
   * 
   * @param eleOrderDetail
   * @param discountedAmt
   * @param isNewCharge
   * @return
   */
  private static HashMap<String, String> prorateHeaderDiscount(Element eleOrderDetail,
	      String discountedAmt, String isNewCharge, String stringOLK) {
	   // log.beginTimer("VSIProrateLineChargesAPI.prorateHeaderDiscount - BEGIN");

	    
	    HashMap<String, String> prortdLineDisMap = new HashMap<String, String>();
	    DecimalFormat df = new DecimalFormat(VSIConstants.DEC_FORMAT);
		df.setRoundingMode(RoundingMode.HALF_UP);

	    if (YFCCommon.isVoid(discountedAmt) || (isNewCharge != null && isNewCharge.equalsIgnoreCase("Y")
	        && Double.parseDouble(discountedAmt) == 0)) {
	      return prortdLineDisMap;
	    } else {
	      Double dHdrDiscnt = Double.parseDouble(discountedAmt);
	      
	      Element orderTotalEle = SCXmlUtil.getChildElement(eleOrderDetail,
	          VSIConstants.ELE_OVERALL_TOTALS);
	      Double dOrderTotal = SCXmlUtil.getDoubleAttribute(orderTotalEle,
	          VSIConstants.ATTR_GRAND_TOTAL, 0);
	      
	      
	      Double dHdrTotal = SCXmlUtil.getDoubleAttribute(orderTotalEle,
	    		  "HdrTotal", 0);
	     
	     
	      Double updatedOrderTotal = dOrderTotal - dHdrTotal;
	      Double fmtdLineDiscAmt = 0.0;
	      NodeList orderLineNL = eleOrderDetail.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
	      for (int k = 0; k < orderLineNL.getLength(); k++) {
	        Element orderLineEle = (Element) orderLineNL.item(k);
	        String olk = orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
	        if (YFCObject.isVoid(stringOLK) && dHdrDiscnt > 0) {

	          Element lineTotalEle = (Element) orderLineEle
	              .getElementsByTagName(VSIConstants.ATTR_LINE_OVERALL_TOTALS).item(0);
	          Double dLineTotal = SCXmlUtil.getDoubleAttribute(lineTotalEle,
	              VSIConstants.ATTR_LINE_TOTAL, 0);
	         
	          Double dLineDiscAmt = (dLineTotal / updatedOrderTotal) * dHdrDiscnt;
	          fmtdLineDiscAmt = fmtdLineDiscAmt + Double.valueOf(df.format(dLineDiscAmt));
	          if(k == orderLineNL.getLength() - 1){
	        	  
	        	  if(fmtdLineDiscAmt != dHdrDiscnt){
	        		  
	        		  Double diff = dHdrDiscnt - fmtdLineDiscAmt;
	        		  dLineDiscAmt = dLineDiscAmt + diff;
	        	  }
	          }
	          
	          prortdLineDisMap.put(olk, df.format(dLineDiscAmt).toString());

	        } else if (olk.equals(stringOLK) && dHdrDiscnt > 0) {
	          prortdLineDisMap.put(olk, discountedAmt.toString());
	        }

	      }
	    }
	    //log.endTimer("VSIProrateLineChargesAPI.prorateHeaderDiscount - END");
	    return prortdLineDisMap;
	  }

}
