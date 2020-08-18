package com.vsi.oms.userexit;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;

public class BeforeCreateMarketplaceReturn implements YFSBeforeCreateOrderUE{
	private YFCLogCategory log = YFCLogCategory.instance(BeforeCreateMarketplaceReturn.class);
	
/** invoking the service BeforeCreateMarketplaceReturn
 * 
 * @param env
 * @param inXML
 * @return
 * @throws YFSException
 * @throws RemoteException
 * @throws ParserConfigurationException
 * @throws YIFClientCreationException
 */
	
	 public Document beforeCreateReturn(YFSEnvironment env, Document inXML)throws YFSException, RemoteException, ParserConfigurationException,
		YIFClientCreationException {
		 
		 if(log.isDebugEnabled()){
			 log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			 log.info("================Inside beforeCreateReturn================================");
		 }
		 
         Document outputGetOrderList=null;
         
         Element eleInXML = inXML.getDocumentElement();
         Document inputGetOrderList = SCXmlUtil.createDocument("Order");
             //Element eleOrderLines2 = SCXmlUtil.createChild(eleRootElement, "OrderLines");
         Element eleInputGetOrderList = inputGetOrderList.getDocumentElement();
         eleInputGetOrderList.setAttribute("OrderNo", eleInXML.getAttribute("OrderNo"));
         
         outputGetOrderList = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST, VSIConstants.API_GET_ORDER_LIST, inputGetOrderList);
         Element eleOrderListOutput = outputGetOrderList.getDocumentElement();
         Element eleOrderOutput=SCXmlUtil.getChildElement(eleOrderListOutput, "Order");
         
         String strCustomerPoNo = eleOrderOutput.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
         eleInXML.setAttribute("CustomerPONo", strCustomerPoNo);
         
         Element eleOrderLinesInput = SCXmlUtil.getChildElement(eleInXML, VSIConstants.ELE_ORDER_LINES);
         ArrayList<Element> alOrderLinesInput = SCXmlUtil.getChildren(eleOrderLinesInput, VSIConstants.ELE_ORDER_LINE);
         //Looping through input orderLines
         for(Element eleOrderLineInput:alOrderLinesInput){
        	 //Element eleDerivedFromInput = SCXmlUtil.getChildElement(eleOrderLineInput, "DerivedFrom");
        	 
        	 String orderLineKey = null;
        	 Element eleDerivedFromInput = SCXmlUtil.getChildElement(eleOrderLineInput, "DerivedFrom");
        	 
        	 if(log.isDebugEnabled()){
        		 log.debug("eleDerivedFromInput has Child Nodes" +SCXmlUtil.getString(eleDerivedFromInput));
        	 }
        	 
        	 if(!YFCObject.isVoid(eleDerivedFromInput)){
        		 String orderLineDerivedFromOrderLineKey = SCXmlUtil.getAttribute(eleDerivedFromInput, "DerivedFromOrderLineKey");
        		 if(YFCObject.isVoid(orderLineDerivedFromOrderLineKey)){
        		 orderLineKey = SCXmlUtil.getAttribute(eleOrderLineInput, "DerivedFromOrderLineKey");
        		 }
        	 }//end of if for checking void
        	 else{
        		 orderLineKey = SCXmlUtil.getAttribute(eleOrderLineInput, VSIConstants.ATTR_ORDER_LINE_KEY);
        	 }//end of else
        	 
        	 Element eleOrderLinesOutput = SCXmlUtil.getChildElement(eleOrderOutput, VSIConstants.ELE_ORDER_LINES);
 			 ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLinesOutput, VSIConstants.ELE_ORDER_LINE);
        	 //Looping through output OrderLines
 			 for(Element outputOL:alOrderLines){
 				String orderLineKeyOutput = SCXmlUtil.getAttribute(outputOL, VSIConstants.ATTR_ORDER_LINE_KEY);
 				
 				if(orderLineKey.equalsIgnoreCase(orderLineKeyOutput)){
 					Element eleExtnOutput = SCXmlUtil.getChildElement(outputOL, "Extn");
 					
                    //getting the ExtnChannelItemId from output and copying it to input
 					 String strExtnChannelItemIdOutput = eleExtnOutput.getAttribute(VSIConstants.ATTR_EXTN_CHANNEL_ITEM_ID); 
 					
 					 Element eleExtnInput = SCXmlUtil.getChildElement(eleOrderLineInput, "Extn");
 					 eleExtnInput.setAttribute(VSIConstants.ATTR_EXTN_CHANNEL_ITEM_ID, strExtnChannelItemIdOutput);
 					 
 					 //copying lineCharges from output to input
 					 Element eleExtnLC  = SCXmlUtil.getChildElement(outputOL, "LineCharges");
 					 
                     SCXmlUtil.importElement(eleOrderLineInput, eleExtnLC);
                     
 				}
 			 }
         }

         return inXML;

	 }//end of method beforeCreateReturn

@Override
public String beforeCreateOrder(YFSEnvironment env, String inputString) throws YFSUserExitException {

	Document outDoc = null;
	String strOutput = null;
	try {
		 outDoc = beforeCreateReturn(env, SCXmlUtil.createFromString(inputString));
		 strOutput = SCXmlUtil.getString(outDoc);
	} catch (YFSException | RemoteException | ParserConfigurationException | YIFClientCreationException e) {
	 e.printStackTrace();
	 throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in BeforeCreateMarketplaceReturn.beforeCreateReturn()");
	}
	
	return strOutput;
}

@Override
public Document beforeCreateOrder(YFSEnvironment env, Document inXML) throws YFSUserExitException {
	
	Document outDoc = null;
	try {
		 outDoc = beforeCreateReturn(env, inXML);
	} catch (YFSException | RemoteException | ParserConfigurationException | YIFClientCreationException e) {
	e.printStackTrace();
	throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in BeforeCreateMarketplaceReturn.beforeCreateReturn()");
	}
	
	return outDoc;
}


}//end of class BeforeCreateMarketplaceReturn
