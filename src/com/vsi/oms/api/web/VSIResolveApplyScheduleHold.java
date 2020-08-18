package com.vsi.oms.api.web;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

public class VSIResolveApplyScheduleHold implements YIFCustomApi {

  @Override
  public void setProperties(Properties arg0) throws Exception {
    // TODO Auto-generated method stub
    
  }

  
  /**
   * Logger for the Class.
   */
  private static final YFCLogCategory LOG = (YFCLogCategory) YFCLogCategory
      .getLogger(VSIResolveApplyScheduleHold.class.getName());
  
  
  public void resolveScheduleHoldForWebOrders(YFSEnvironment env, Document inputDoc)
      throws Exception {

    LOG.beginTimer("VSIResolvePaymentAuthHold.resolvePaymentAuthHoldForWebOrders");
    
    if (!SCUtil.isVoid(inputDoc)) {
    	if(LOG.isDebugEnabled()){
    		LOG.debug(" *** Entering Method : resolvePaymentAuthHoldForWebOrders Input Doc is: \n "
    				+ XMLUtil.getXMLString(inputDoc));
    	}
      
      //get OrderHeader Key
      String orderHeaderKey = inputDoc.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
      
      
      
      String strXPATHHold = XMLUtil.formXPATHWithOneCondition    		  
						("Order/OrderHoldTypes/OrderHoldType",
						"HoldType","VSI_SCHEDULE_HOLD");
			  
			  try {
					NodeList nlHoldTypeList =XMLUtil.getNodeListByXpath(
							inputDoc, strXPATHHold);
					
					if(nlHoldTypeList.getLength() > 0)
					{
						Element eleHoldType = (Element) nlHoldTypeList.item(0);
						if("1100".equals(eleHoldType.getAttribute("Status")))
						{
							  // Create Input for ChangeOrder (To Resolve the Hold)
						      Document inputChangeOrderDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
						      Element inputChangeOrderEle = inputChangeOrderDoc.getDocumentElement();
						      inputChangeOrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
						      
						      // Create Order HoldTypes Child element
						      Element eleOrderHoldTypes = SCXmlUtil.createChild(inputChangeOrderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
						      
						      // Create Order HoldType Child element
						      Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
						      
						      eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,"VSI_SCHEDULE_HOLD");
						      eleOrderHoldType.setAttribute("ResolverUserId","PaymentCollection");
						      eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS,"1300");
						      
						      if(LOG.isDebugEnabled()){
						    		LOG.debug("The Input XML for Change Order"+ SCXmlUtil.getString(inputChangeOrderDoc.getDocumentElement()));
						      }
						      
						      //Create template for ChangeOrder
						      Document templateChangeOrderDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
						      Element templateChangeOrderEle = templateChangeOrderDoc.getDocumentElement();
						      templateChangeOrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
						      
						      // Invoke Change order API
						      VSIUtils.invokeAPI(env, templateChangeOrderDoc,
						          "changeOrder", inputChangeOrderDoc);
						}
					}
	
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
      
    }
  }
    
public void applyScheduleHoldForWebOrders(YFSEnvironment env, Document inputDoc)
	      throws Exception {

	    LOG.beginTimer("VSIResolvePaymentAuthHold.resolvePaymentAuthHoldForWebOrders");
	    
	    if (!SCUtil.isVoid(inputDoc)) {
	    	if(LOG.isDebugEnabled()){
	    		LOG.debug(" *** Entering Method : resolvePaymentAuthHoldForWebOrders Input Doc is: \n "
	    				+ XMLUtil.getXMLString(inputDoc));
	    	}
	      
	      //get OrderHeader Key
	      String orderHeaderKey = inputDoc.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
	    
		// Create Input for ChangeOrder (To Resolve the Hold)
	      Document inputChangeOrderDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
	      Element inputChangeOrderEle = inputChangeOrderDoc.getDocumentElement();
	      inputChangeOrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
	      
	      // Create Order HoldTypes Child element
	      Element eleOrderHoldTypes = SCXmlUtil.createChild(inputChangeOrderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
	      
	      // Create Order HoldType Child element
	      Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
	      
	      eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,"VSI_SCHEDULE_HOLD");
	      //eleOrderHoldType.setAttribute("ResolverUserId","PaymentCollection");
	      eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS,"1100");
	      
	      if(LOG.isDebugEnabled()){
	    		LOG.debug("The Input XML for Change Order"+ SCXmlUtil.getString(inputChangeOrderDoc.getDocumentElement()));
	      }
	      
	      //Create template for ChangeOrder
	      Document templateChangeOrderDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
	      Element templateChangeOrderEle = templateChangeOrderDoc.getDocumentElement();
	      templateChangeOrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
	      
	      // Invoke Change order API
	      VSIUtils.invokeAPI(env, templateChangeOrderDoc,
	          "changeOrder", inputChangeOrderDoc);
				

			
	    }
  }
  
}
