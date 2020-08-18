package com.vsi.oms.api;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
public class VSIDeleteCustPONoRecords {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIDeleteCustPONoRecords.class);
	YIFApi api;
	//YIFApi newApi;
	//YIFApi shipApi;
	/**
	 * 
	 * @param env
	 * 	Required. Environment handle returned by the createEnvironment
	 * 	API. This is required for the user exit to either raise errors	
	 * 	or access environment information.
	 * @param inXML
	 * 	Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public void DeleteCustRecords(YFSEnvironment env, Document inXML)
			throws Exception {
		try{
			YFCIterable<YFCElement> orderLineItr = YFCDocument.getDocumentFor(inXML).getDocumentElement().getChildElement("OrderLines").getChildren("OrderLine");
	             while (orderLineItr.hasNext()){
		            YFCElement orderLine = orderLineItr.next();
			        String orderHeaderKey=orderLine.getAttribute("OrderHeaderKey");
					//System.out.println("OrderHeaderKey is "+orderHeaderKey);

				 String customerPONo=orderLine.getAttribute("CustomerPONo");
				 if(!YFCObject.isVoid(orderHeaderKey)&&!YFCObject.isVoid(customerPONo))
				 
					//System.out.println("Customer PoNo is "+customerPONo);
					if(!orderHeaderKey.equalsIgnoreCase("null") && !customerPONo.equalsIgnoreCase("null")){
				    Document getCustPoRecords = XMLUtil.createDocument("CustomerPoNoRecords");
					Element eleOrder = getCustPoRecords.getDocumentElement();
					eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
					eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPONo);
					api = YIFClientFactory.getInstance().getApi();
					Document ouctDocCustPoRecords = api.executeFlow(env,"getCustomerPoNoRecordsList", getCustPoRecords);
					NodeList custPoRecordList = ouctDocCustPoRecords.getElementsByTagName("CustomerPoNoRecords");
					
					 for(int k = 0; k < custPoRecordList.getLength(); k++){
						Element eleCustPoRecords = (Element) custPoRecordList.item(k);
						String custPoNoKey=eleCustPoRecords.getAttribute("CustPoNoKey");
						//System.out.println("CustPoNoKey is "+custPoNoKey);

						Document deleteCustPoRecords = XMLUtil.createDocument("CustomerPoNoRecords");
						Element eleCustoPo = deleteCustPoRecords.getDocumentElement();
						eleCustoPo.setAttribute("CustPoNoKey",custPoNoKey);
						
						api = YIFClientFactory.getInstance().getApi();
						Document deleteCustRecords = api.executeFlow(env,"VSIDeleteCustomerPoNo", deleteCustPoRecords);
					 }
					}

			 }
		}
		catch(Exception Ex){
			
		}
}
}
