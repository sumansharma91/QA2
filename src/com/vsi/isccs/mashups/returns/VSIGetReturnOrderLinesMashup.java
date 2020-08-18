package com.vsi.isccs.mashups.returns;

import com.ibm.isccs.mashups.utils.SCCSMashupUtils;
import com.ibm.isccs.order.mashups.SCCSGetBundleComponentOrderLinesPaginationMashup;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class VSIGetReturnOrderLinesMashup extends SCCSGetBundleComponentOrderLinesPaginationMashup
{
	private YFCLogCategory logger = YFCLogCategory.instance(VSIGetReturnOrderLinesMashup.class);
	
  public Element massageInput(Element inputEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext)
  {
	  logger.beginTimer("Start ******* SCCSGetReturnOrderLinesMashup.massageInput ******* Start");
	  if(logger.isDebugEnabled()){
		  logger.info("inside massage input custom");
		  logger.debug("I am inside massage input");
	  }
    inputEl = super.massageInput(inputEl, mashupMetaData, uiContext);
    Element apiInput = SCXmlUtil.getChildElement(inputEl, "API");
    Element pageInput = SCXmlUtil.getChildElement(apiInput, "Input");
    Element getCompleteOrderLineListApiInput = SCXmlUtil.getChildElement(pageInput, "OrderLine");
    
    Element orderElemInInput = SCXmlUtil.getChildElement(getCompleteOrderLineListApiInput, "Order");
    if ((!SCUtil.isVoid(orderElemInInput)) && (!SCUtil.isVoid(orderElemInInput.getAttribute("OrderNo")))) {
      orderElemInInput.removeAttribute("OrderNo");
    }
    Element scControllerInput = (Element)uiContext.getRequest().getAttribute("scControllerInput");
    if ((!SCUtil.isVoid(scControllerInput)) && 
      (!SCUtil.isVoid(scControllerInput.getAttribute("OrderNo"))))
    {
      uiContext.setAttribute("ReturnOrderNo", scControllerInput.getAttribute("OrderNo"));
      scControllerInput.removeAttribute("OrderNo");
    }
    if (!SCUtil.isVoid(getCompleteOrderLineListApiInput.getAttribute("ReturnOrderHeaderKey")))
    {
      uiContext.setAttribute("ReturnOrderHeaderKey", getCompleteOrderLineListApiInput.getAttribute("ReturnOrderHeaderKey"));
      getCompleteOrderLineListApiInput.removeAttribute("ReturnOrderHeaderKey");
    }
    else if ((!SCUtil.isVoid(scControllerInput)) && ("0003".equals(SCXmlUtil.getAttribute(scControllerInput, "DocumentType"))) && (!SCUtil.isVoid(SCXmlUtil.getAttribute(scControllerInput, "OrderHeaderKey"))))
    {
      uiContext.setAttribute("ReturnOrderHeaderKey", SCXmlUtil.getAttribute(scControllerInput, "OrderHeaderKey"));
    }

  Element eOrder = SCXmlUtil.getChildElement(getCompleteOrderLineListApiInput, "Order");
  if ((!SCUtil.isVoid(eOrder)) && (!SCUtil.isVoid(eOrder.getAttribute("EnterpriseCode"))))
  {
    Element eRule = SCCSMashupUtils.getRuleValue(uiContext, "YCD_SHOW_RESHIP_ON_RETURN_ENTRY", eOrder.getAttribute("EnterpriseCode"), null);
    if (SCXmlUtil.getBooleanAttribute(eRule, "RuleSetValue", false)) {
      getCompleteOrderLineListApiInput.setAttribute("ReshipParentLineKeyQryType", "ISNULL");
    }
  }
    return inputEl;
  }
  
  public Element massageOutput(Element outEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext)
  {
	  
	Element eOutput = null;
    try
	{
    	if(logger.isDebugEnabled()){
    		logger.info("inside massage output custom");
    		logger.beginTimer("Start ******* SCCSGetReturnOrderLinesMashup.massageInput ******* Start");
    	}
    	eOutput = super.massageOutput(outEl, mashupMetaData, uiContext);
	    Element eOutputOrderLineList = SCXmlUtil.getChildElement(eOutput, "Output");
	    String sReturnOrderHeaderKey = "";
	   
	    Element orderLineListElem = SCXmlUtil.getChildElement(eOutputOrderLineList, "OrderLineList");
	    List<Element> orderLineElem = SCXmlUtil.getChildren(orderLineListElem, "OrderLine");
	    //System.out.println("Helloooo"+orderLineElem.size());
	    Iterator<Element> itr = orderLineElem.iterator();
	    while (itr.hasNext())
	    {
	      Element eOrderLine = (Element)itr.next();
	      Element eDerivedOrderLine = SCXmlUtil.getChildElement(eOrderLine, "DerivedFromOrderLine");
	      if ((!SCUtil.isVoid(eDerivedOrderLine)) && (SCUtil.isVoid(eDerivedOrderLine.getAttribute("ReturnableQty")))) {
	        SCXmlUtil.setAttribute(eDerivedOrderLine, "ReturnableQty", eDerivedOrderLine.getAttribute("OrderedQty"));
	      } else {
	        SCXmlUtil.setAttribute(eDerivedOrderLine, "ReturnableQty", SCXmlUtil.getDoubleAttribute(eDerivedOrderLine, "ReturnableQty", 0.0D) + SCXmlUtil.getDoubleAttribute(eOrderLine, "OrderedQty", 0.0D));
	      }
	      Element eReturnOrderLines = SCXmlUtil.getChildElement(eOrderLine, "ReturnOrderLines");
	      if (!SCUtil.isVoid(eReturnOrderLines)) {
	        for (Element eReturnOrderLine : SCXmlUtil.getChildrenList(eReturnOrderLines)) {
	          if (!SCUtil.isVoid(sReturnOrderHeaderKey))
	          {
	            if (!SCUtil.equals(eReturnOrderLine.getAttribute("OrderHeaderKey"), sReturnOrderHeaderKey)) {
	              eReturnOrderLines.removeChild(eReturnOrderLine);
	            } else {
	              SCXmlUtil.setAttribute(eOrderLine, "ReturnableQty", SCXmlUtil.getDoubleAttribute(eOrderLine, "ReturnableQty", 0.0D) + SCXmlUtil.getDoubleAttribute(eReturnOrderLine, "OrderedQty", 0.0D));
	            }
	          }
	          else {
	            eReturnOrderLines.removeChild(eReturnOrderLine);
	          }
	        }
	      }
	      processReturnViolations(eOrderLine,uiContext);
	    }
	}
    catch (TransformerException e) {
    	logger.error("TransformerException in massageOutput" + e.getMessage());
		throw new YFSException(new YFCException(e).getXMLErrorBuf());
	} catch (IllegalArgumentException e) {
		logger.error("IllegalArgumentException in massageOutput" + e.getMessage());
		throw new YFSException(new YFCException(e).getXMLErrorBuf());
	} catch (Exception e) {
		logger.error("Exception in massageOutput" + e.getMessage());
		throw new YFSException(new YFCException(e).getXMLErrorBuf());
	}
    if(logger.isDebugEnabled()){
    	logger.info("Updated output is"+SCXmlUtil.getString(eOutput));
    }
    return eOutput;
  }
  
  private  void processReturnViolations(Element eOrderLine, SCUIContext uiContext) throws ParseException, Exception {
	  Element eReturnPolicyViolations = SCXmlUtil.getChildElement(eOrderLine, VSIConstants.E_RETURN_POLICY_VIOLATIONS, true);				
      eReturnPolicyViolations.setAttribute("HasViolations", "N");
      Element eTransactionApproverList = SCXmlUtil.getChildElement(eReturnPolicyViolations, "TransactionApproverList");
      Element eNonApproval = SCXmlUtil.getChildElement(eReturnPolicyViolations, "NonApprovalTransactionViolationList");
        if (!SCUtil.isVoid(eNonApproval) && eNonApproval.hasChildNodes())
        {
          for (Element eTransactionViolation : SCXmlUtil.getChildrenList(eNonApproval)) {
            if ((SCUtil.equals(eTransactionViolation.getAttribute("Status"), "1100")) || (SCUtil.equals(eTransactionViolation.getAttribute("Status"), "1300")) || (SCUtil.equals(eTransactionViolation.getAttribute("Status"), "1500")))
            {
              Element eReturnPolicyViolation = SCXmlUtil.createChild(eReturnPolicyViolations, "ReturnPolicyViolation");
              SCXmlUtil.setAttributes(eTransactionViolation, eReturnPolicyViolation);
              eReturnPolicyViolations.setAttribute("HasViolations", "Y");
            }
          }
          eReturnPolicyViolations.removeChild(eNonApproval);
        }
        else if (!SCUtil.isVoid(eTransactionApproverList) && eTransactionApproverList.hasChildNodes())
        {
        	//logger.info("if 2");
          for (Element eTransactionApprover : SCXmlUtil.getChildrenList(eTransactionApproverList)) {
            for (Element eTransactionApprovalStatus : SCXmlUtil.getChildrenList(SCXmlUtil.getChildElement(eTransactionApprover, "TransactionApprovalStatusList", true))) {
              for (Element eTransactionViolation : SCXmlUtil.getChildrenList(eTransactionApprovalStatus)) {
                if ((SCUtil.equals(eTransactionViolation.getAttribute("Status"), "1100")) || (SCUtil.equals(eTransactionViolation.getAttribute("Status"), "1300")) || (SCUtil.equals(eTransactionViolation.getAttribute("Status"), "1500")))
                {
                  Element eReturnPolicyViolation = SCXmlUtil.createChild(eReturnPolicyViolations, "ReturnPolicyViolation");
                  SCXmlUtil.setAttributes(eTransactionViolation, eReturnPolicyViolation);
                  eReturnPolicyViolations.setAttribute("HasViolations", "Y");
                }
              }
            }
          }
	          eReturnPolicyViolations.removeChild(eTransactionApproverList);
	    }
	    else {
				Element eOrderLineItemDetails = SCXmlUtil.getChildElement(eOrderLine, VSIConstants.ELE_ITEM_DETAILS);
				Element ePrimaryInformation = SCXmlUtil.getChildElement(eOrderLineItemDetails, VSIConstants.ELE_PRIMARY_INFORMATION);
				Element eleOrder = SCXmlUtil.getChildElement(eOrderLine, VSIConstants.ELE_ORDER);
				String orderDate = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_DATE);
				
				//String strProductLine = SCXmlUtil.getAttribute(ePrimaryInformation, "ProductLine");
				
				int iReturnWindow = 0;
				String strReturnPolicyMsg = null;
					Document docCommonCode = SCXmlUtil.createDocument();
					Element eleCommonCodeInput = docCommonCode.createElement("CommonCode");
					docCommonCode.appendChild(eleCommonCodeInput);
					eleCommonCodeInput.setAttribute("CodeType", "VSI_RETURN_POLICY");
					 Element eleCommonCodeOutput = (Element) SCUIMashupHelper
								.invokeMashup("extn_getCommonCode",
										eleCommonCodeInput, uiContext);
					 NodeList nlsCommonCode = eleCommonCodeOutput
								.getElementsByTagName("CommonCode");
						for (int j = 0; j < nlsCommonCode.getLength(); j++) {
							Element eleCommonCode = (Element) nlsCommonCode.item(j);
							String strCodeValue = eleCommonCode.getAttribute("CodeValue");
							if("ReturnWindow".equals(strCodeValue))
							{
								iReturnWindow = Integer.parseInt(eleCommonCode.getAttribute("CodeLongDescription"));
							}
							if("PolicyOverrideMsg".equals(strCodeValue))
							{
								strReturnPolicyMsg = eleCommonCode.getAttribute("CodeLongDescription");
							}
						}
					
					Date dtStatusDate = null, /*dtCurStatusDate = null,*/ dtReturnWinDate = null;
					
					SimpleDateFormat df = new SimpleDateFormat(VSIConstants.DATE_FORMAT2);
					Date dtCurrentDate = new Date();
					df.format(dtCurrentDate);
					dtStatusDate = df.parse(orderDate);

					if(!SCUtil.isVoid(dtStatusDate) && iReturnWindow>0 ){
						dtReturnWinDate = VSIUtils.addToDate(dtStatusDate, Calendar.DAY_OF_MONTH, iReturnWindow);							
						if(dtCurrentDate.after(dtReturnWinDate)){
							eReturnPolicyViolations.setAttribute(VSIConstants.A_HAS_VIOLATIONS, VSIConstants.FLAG_Y);
							eReturnPolicyViolations.setAttribute(VSIConstants.A_SHOW_OVERRIDE, VSIConstants.FLAG_Y);
							eReturnPolicyViolations.setAttribute(VSIConstants.A_OUTSIDE_RET_WINDOW, VSIConstants.FLAG_Y);
							Element eReturnPolicyViolation = SCXmlUtil.getChildElement(eReturnPolicyViolations, VSIConstants.E_RETURN_POLICY_VIOLATION);
							if(SCUtil.isVoid(eReturnPolicyViolation)){
								eReturnPolicyViolation = SCXmlUtil.createChild(eReturnPolicyViolations, VSIConstants.E_RETURN_POLICY_VIOLATION);										
							}
							String strMsgCode = VSIConstants.OUTSIDE_RETURN_WINDOW;
							eReturnPolicyViolation.setAttribute(VSIConstants.A_MESSAGE_CODE, strMsgCode);
							eReturnPolicyViolation.setAttribute(VSIConstants.A_MESSAGE_CODE_DESC, strReturnPolicyMsg);
							//SCXmlUtil.setAttribute(eOrderLine, VSIConstants.A_RETURNABLE_QTY, 0);
						}
					}
			}
			eReturnPolicyViolations.removeChild(eNonApproval);
			eReturnPolicyViolations.removeChild(eTransactionApproverList);
			if(logger.isDebugEnabled()){
				logger.info("Updated order line is"+SCXmlUtil.getString(eOrderLine));
			}
		}
  
  
  		
}
