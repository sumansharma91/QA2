package com.vsi.oms.api.web;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

public class VSIVoidGCForWEB implements YIFCustomApi {

  @Override
  public void setProperties(Properties arg0) throws Exception {
    // TODO Auto-generated method stub
    
  }

  
  /**
   * Logger for the Class.
   */
  private static final YFCLogCategory LOG = (YFCLogCategory) YFCLogCategory
      .getLogger(VSIResolveApplyScheduleHold.class.getName());
  
  
  public void voidGCForWEB(YFSEnvironment env, Document inputDoc)
      throws Exception {
    
    HashMap<String, String> hmGCmap = null;

    if(!YFCCommon.isVoid(env.getTxnObject("GC_VOID_REQUEST"))){
    	hmGCmap = (HashMap<String, String>) env.getTxnObject("GC_VOID_REQUEST");
    	
    	Iterator it = hmGCmap.entrySet().iterator();
        while (it.hasNext()) 
        {
            Map.Entry pair = (Map.Entry)it.next();
            Document gcDoc = (Document)pair.getValue();
            String orderHeaderKey = inputDoc.getDocumentElement().getAttribute("OrderHeaderKey");
            recordPayments(env, orderHeaderKey, VSIUtils.getDocumentXMLString(gcDoc), "Void Request");

			YIFApi api = YIFClientFactory.getInstance().getApi();
			env.setTxnObject("strOrderHeaderKey", orderHeaderKey);
			env.setTxnObject("chargeType", "Void Request");
			//OMS-868 :Start
			String strUserName=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_USER_NAME");
			String strPassword=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_PASSWORD");
			String strMerchantNumber=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_MERCHANT_NUMBER");
			String strRoutingID=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_ROUTING_ID");
			Element GCElement=gcDoc.getDocumentElement();
			GCElement.setAttribute("Username", strUserName);
			GCElement.setAttribute("Password", strPassword);
			GCElement.setAttribute("merchantNumber", strMerchantNumber);
			GCElement.setAttribute("routingID", strRoutingID);
			//OMS-86 :End
			Document outDoc = api.executeFlow(env, "VSISVSSettlement", gcDoc);
			String response = VSIUtils.getDocumentXMLString(outDoc);
			////System.out.println("Printing Response in UE:"+response);
			//JIRA 608
			recordPayments(env, orderHeaderKey, response, "Void Response");
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
  }
  
  /*
   * JIRA 608
   * This Method is used to capture all the payment request and response in VSI_Payment_Records Table 
   */
  public void recordPayments(YFSEnvironment env, String strOrderHeaderKey, String record, String chargeType) throws ParserConfigurationException {
		 Document doc = XMLUtil.createDocument("PaymentRecords");
	  		Element elePayRecrds = doc.getDocumentElement();
	  		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
	  				strOrderHeaderKey);
	  		elePayRecrds.setAttribute("Record", record);
	  		elePayRecrds.setAttribute("ChargeType", chargeType);
	  		YIFApi api;
	  		try {
				api = YIFClientFactory.getInstance().getApi();
		  		api.executeFlow(env,"VSIPaymentRecords", doc);
			} catch (YIFClientCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (YFSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
