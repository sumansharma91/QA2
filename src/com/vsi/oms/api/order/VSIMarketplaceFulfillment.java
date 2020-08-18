package com.vsi.oms.api.order;


import java.rmi.RemoteException;

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

/**
 * triggering the service VSIMarketplaceFulfillment
 * 
 * @author Sindhura
 *
 */
public class VSIMarketplaceFulfillment {
	
	 private YFCLogCategory log = YFCLogCategory.instance(VSIMarketplaceFulfillment.class);
	 
	 public Document update(YFSEnvironment env, Document inXML)throws YFSException, RemoteException, ParserConfigurationException,
				YIFClientCreationException {
		 
		 if(log.isDebugEnabled()){
			 log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			 log.info("================Inside VSIMarketplaceFulfillment.update================================");
		 }
		 
	    Document outDoc=null;
	    Document getOrderlistOutput = null;
		//get the Carrier Name for the CarrierServiceCode by using ScacDesc
        Element eleShipment = inXML.getDocumentElement();
        String strSCAC= eleShipment.getAttribute("SCAC");
        Document docGetScacList = SCXmlUtil.createDocument("Scac");
        Element eleGetScacList= docGetScacList.getDocumentElement();
        eleGetScacList.setAttribute("Scac", strSCAC);
        
        outDoc = VSIUtils.invokeAPI(env, VSIConstants.API_GET_SCAC_LIST, docGetScacList);
        if(!YFCObject.isNull(outDoc)){
	        Element eleScacList= outDoc.getDocumentElement();
	        Element eleSCAC = SCXmlUtil.getChildElement(eleScacList, "Scac");
	        eleShipment.setAttribute("CarrierName",eleSCAC.getAttribute("ScacDesc")); //setting CarrierName at Shipment level
	      }
        
        String sOrderHeaderKey= eleShipment.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
        Document docGetOrderList = SCXmlUtil.createDocument("Order");
        Element eleOrder= docGetOrderList.getDocumentElement();
        eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOrderHeaderKey);
        
        getOrderlistOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_VSI_GET_ORDER_LIST_MARKETPLACE,VSIConstants.API_GET_ORDER_LIST, docGetOrderList);
        
        if(!YFCObject.isNull(getOrderlistOutput)){
        	Element eleOrderList= getOrderlistOutput.getDocumentElement();
        	Element OrderEle = SCXmlUtil.getChildElement(eleOrderList, VSIConstants.ELE_ORDER);
        	if(!YFCObject.isNull(OrderEle)){
        		//appending order element to input xml
        		SCXmlUtil.importElement(eleShipment, OrderEle);
        	}
        }//end of checking output of getorderlist
         
        return inXML;
	 }//end of method update
	
}//end of class VSIMarketplaceFulfillment
