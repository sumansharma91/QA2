package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

import org.w3c.dom.*;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;

/**
 * When Sterling OMS receives status updates from ES, the input message will only contain the CustomerPONo or orderNo will be Sterling's CustomerPoNo(i.e. ES Order number).
 * This class will call getOrderList using the CustomerPoNo and get the original orderNo finally calling changeOrderStatus
 * @author nish.pingle
 * 
 *
 */
public class VSIGetOrderNumber implements YIFCustomApi {
	@SuppressWarnings("unused")
	private Properties properties;
	private static YFCLogCategory logger;
	YIFApi api;
	
	static {
		logger = YFCLogCategory
				.instance(com.vsi.oms.api.VSIGetOrderNumber.class);
	}

	/**
	 * This method sets the properties passed from the service
	 * 
	 * @param apiArguments
	 * @throws Exception
	 */
	public void setProperties(Properties apiArguments) throws Exception {
		this.properties = apiArguments;
	}
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
	public Document vsiGetOrderNumber(YFSEnvironment env, Document inXML)
			throws Exception {
		if(logger.isDebugEnabled()){
			logger.info("================Inside vsiGetOrderNumber================================");
			logger.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		//YFCDocument inputDoc = YFCDocument.getDocumentFor(inXML);
		
		try{
		Element rootElement = inXML.getDocumentElement();

		//get CustomerPONo from Input XML
		String customerPoNo = rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String eCode=VSIConstants.VSICOM_ENTERPRISE_CODE;
		String dType=VSIConstants.DOCUMENT_TYPE;
		if(logger.isDebugEnabled()){
			logger.debug("CustomerPONo: "+ customerPoNo);
		}
				//System.out.println("CustomerPONo: "+ customerPoNo);
		
				Document getOrderListInput = XMLUtil.createDocument("Order");
				Element eleOrder = getOrderListInput.getDocumentElement();
				eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
				eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eCode);
				eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
		//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
					api = YIFClientFactory.getInstance().getApi();
					Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
					
					Element orderElement = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
					if(null != orderElement){
					String orderNo=orderElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
					
		rootElement.setAttribute("OrderNo", orderNo);
		//System.out.println(" Printing Output XML"+XmlUtils.getString(inXML));
		//Invoking changeOrderStatus API
		Document changeOrderStatusDoc = api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,inXML);
		//System.out.println("changeOrderStatus Output"+ changeOrderStatusDoc);
		
					}
					else{
						throw new YFSException(
								"EXTN_ERROR",
								"EXTN_ERROR",
								"order No. not found");
					}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Parse Error");
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Client Creation Error");
		} catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"YFS Exception");
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Remote Exception");
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Exception");
		}
		
		return inXML;
	}//End vsiGetOrderNumber

}// End Class
