package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIDuplicateOrder {

	
	private YFCLogCategory log = YFCLogCategory.instance(VSIDuplicateOrder.class);
	
/**invoking the VSIOrderCreate
 * 
 * @param env
 * @param inXML
 * @return
 * @throws YFSException
 * @throws RemoteException
 * @throws ParserConfigurationException
 * @throws YIFClientCreationException
 */
	
	
	public Document checkDuplicateOrder(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException,
			YIFClientCreationException{
		
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside checkDuplicateOrder================================");
		}
		
		Document outdoc = null;
		Element orderElement = inXML.getDocumentElement();
		Document getOrderListInput = null;
		getOrderListInput=SCXmlUtil.createDocument("Order");
		Element eleOrderElement = getOrderListInput.getDocumentElement();
		eleOrderElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, orderElement.getAttribute(VSIConstants.ATTR_CUST_PO_NO));
		if("MCL".equals(orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE)))
		{
			Element eleExtnIp = SCXmlUtil.getChildElement(orderElement, "Extn");
			if(!YFCCommon.isVoid(eleExtnIp))
			{
				String extnMCLOrderShipmentID = eleExtnIp.getAttribute("ExtnMCLOderShipmentID");
				if(!YFCCommon.isVoid(extnMCLOrderShipmentID))
				{
					Element extnEle = SCXmlUtil.createChild(eleOrderElement, "Extn");
					extnEle.setAttribute("ExtnMCLOderShipmentID", extnMCLOrderShipmentID);
				}	
			}
		}
		eleOrderElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE));
		/*String strCustomerPoNo = orderElement.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
		String strEnterpriseCode = orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);*/
		
		 outdoc = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST, VSIConstants.API_GET_ORDER_LIST, getOrderListInput);
		 Element eleOrderList = outdoc.getDocumentElement();
		 
		 // check if order has same customerPoNo and enterprise code
		 if(eleOrderList.hasChildNodes()) {
			 
		 //setting the duplicate variable to Y
		 orderElement.setAttribute(VSIConstants.ATTR_DUPLICATE_ORDER, "Y");
		 
		 Element eleOrder = SCXmlUtil.getChildElement(eleOrderList,"Order");
		  String strOrderNo=eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
		  orderElement.setAttribute("OrderNo", strOrderNo);
		  orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
		  
		  }//end of if
		 
		  return inXML;
		}// end of method checkDuplicateOrder()
	
}// end of class
