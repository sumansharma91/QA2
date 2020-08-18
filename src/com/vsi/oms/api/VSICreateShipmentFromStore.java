package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
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
 * When Sterling OMS receives create Shipment from ES, the input message will only contain the CustomerPONo or orderNo will be Sterling's CustomerPoNo(i.e. ES Order number).
 * This class will call getOrderList using the CustomerPoNo and get the original orderNo finally calling createShipment with confirmShip="Y"
 * @author nish.pingle
 * 
 *
 */
public class VSICreateShipmentFromStore implements YIFCustomApi {
	@SuppressWarnings("unused")
	private Properties properties;
	private static YFCLogCategory logger;
	YIFApi api;
	
	static {
		logger = YFCLogCategory
				.instance(com.vsi.oms.api.VSICreateShipmentFromStore.class);
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
	public void vsiGetOrderNumberForShipment (YFSEnvironment env, Document inXML)
			throws Exception {
		if(logger.isDebugEnabled()){
			logger.info("================Inside vsiGetOrderNumberForShipment================================");
			logger.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		//YFCDocument inputDoc = YFCDocument.getDocumentFor(inXML);
		
		try{
		Element rootElement = inXML.getDocumentElement();

		//get CustomerPONo from Input XML
		String customerPoNo = rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String eCode=VSIConstants.VSICOM_ENTERPRISE_CODE;
		String dType=VSIConstants.DOCUMENT_TYPE;
		//logger.debug("CustomerPONo: "+ customerPoNo);
			//	System.out.println("CustomerPONo: "+ customerPoNo);
		//
				Document getOrderListInput = XMLUtil.createDocument("Order");
				Element eleOrder = getOrderListInput.getDocumentElement();
				eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
				eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eCode);
				eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
		//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
					api = YIFClientFactory.getInstance().getApi();
					env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIGetOrderListTemplateForShipment.xml");
					Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
					env.clearApiTemplate(VSIConstants.API_GET_ORDER_LIST);
					Element orderElement = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
					Element orderLineEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
					if(null != orderElement){
					String orderNo=orderElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
					
					
		//Setting rootElement OrderNo of inXML			
		rootElement.setAttribute("OrderNo", orderNo);
		
	
		
		NodeList shipmentLineList = inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE);
		int test=shipmentLineList.getLength();
		Map<Integer, String> orderLinekeyMap = new HashMap<Integer, String>();
		String orderLineKeyTemp = null;
		//System.out.println("Length ShipmentLine:"+test);
		for (int i = 0; i < shipmentLineList.getLength(); i++) {
			Element shipmentLineElement = (Element) shipmentLineList.item(i);
			String item = shipmentLineElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
			String Quantity = shipmentLineElement.getAttribute(VSIConstants.ATTR_QUANTITY);
			Double dQty = Double.parseDouble(Quantity);
			NodeList orderLineList	= outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				for (int j=0; j< orderLineList.getLength(); j++){
					Element orderLineElement = (Element) orderLineList.item(j);
					String minLineStatus = orderLineElement.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
					String intQuantity = orderLineElement.getAttribute("OrderedQty");
					Double dIntQty = Double.parseDouble(intQuantity);
					String lineType=orderLineEle.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				    //System.out.println("LineTYPE::::"+lineType);
					String status = getStatus(lineType);
					Element itemElement = (Element) orderLineElement.getElementsByTagName(VSIConstants.ELE_ITEM).item(0);
						String itemID = itemElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
						
						int dval = Double.compare(dQty,dIntQty);
						
						//System.out.println("minLine"+minLineStatus);
						
						if(itemID.equals(item) && minLineStatus.equals(status) && dval==0){
							
							

							if (status.equals("1100.500") || status.equals("1100.100")){
								
								String orderLineKey = orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
								//System.out.println("condition"+orderLinekeyMap.containsValue(orderLineKey));
								if (!orderLinekeyMap.containsValue(orderLineKey)){
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineKey);
									orderLineKeyTemp=orderLineKey;
									
									break;
								}
								//Setting the attributes in the inXML
								
							}else{
								
								
								NodeList orderStatusList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_STATUS);
								Element orderStatusElement = (Element) orderStatusList.item(j);
								String releaseKey = orderStatusElement.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY);
								//System.out.println("ReleaseKEY"+releaseKey);
								String orderLineKey = orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
								//System.out.println("condition"+orderLinekeyMap.containsValue(orderLineKey));
								if (!orderLinekeyMap.containsValue(orderLineKey)){
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineKey);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY, releaseKey);
									orderLineKeyTemp=orderLineKey;
									//System.out.println("orderLineKey is "+orderLineKey);
									
									break;
									

									
								}
								//Setting the attributes in the inXML
								
							}
							
							
							
						}
						
						
					}
				orderLinekeyMap.put(i, orderLineKeyTemp);
				//System.out.println("map" +orderLinekeyMap);
				}
		
		//System.out.println(" Printing Output XML"+XmlUtils.getString(inXML));
		//Invoking CreateShipmentAPI
		Document createShipmentDoc = api.invoke(env, VSIConstants.API_CREATE_SHIPMENT,inXML);
		//System.out.println("createShipment Output"+ createShipmentDoc);
		

					}else{
						throw new YFSException(
								"EXTN_ERROR",
								"EXTN_ERROR",
								"Order No. not found");
						}
		}catch (ParserConfigurationException e) {
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
					"Order No. Not found");
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Remote Exception");
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Exception");

		}

		
	}//End vsiGetOrderNumberForShipment

	
	public String getStatus(String lineType){
		
		if(lineType.equals("SHIP_TO_STORE"))
		{
			String status="3200.500";
			return status;
			
		}else if(lineType.equals("PICK_IN_STORE")) {
			
			String status="1100.500";
			return status;
			
		}else{
			
			String status="1100.100";
			return status;
		}
		
		
	}
	
	
}// End Class
