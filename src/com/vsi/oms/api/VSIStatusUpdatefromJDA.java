package com.vsi.oms.api;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIStatusUpdatefromJDA {

	private YFCLogCategory log = YFCLogCategory.instance(VSIStatusUpdatefromJDA.class);
	YIFApi api;
	
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
	 *   
	 *   inXML: <Container ContainerNo="1" TrackingNo="u1234" Status="CREATE" EnterpriseCode="VSI.com" OrderType="WEB" ShipNode="391" TransferNo="" StoreID="">
			<ContainerDetails>
			<ContainerDetail Quantity="1">
			<ShipmentLine DocumentType="0001"  ItemID="TESTITEM03" OrderNo="1234567"  ProductClass="GOOD"  UnitOfMeasure="EACH">
			</ShipmentLine>	
			</ContainerDetail>
			</ContainerDetails>
			</Container>

	 *   Call changeShipment API after getting the shipmentKey
	 *   
	 *   
	 *   
	 *   
	 *   
	 */
	public void vsiStatusUpdatefromJDA(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside vsiTransferOrderShipmentCreate================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}

		try{
		Element rootElement = inXML.getDocumentElement();
		/*Element containerDetailsElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAILS).item(0);
		Element containerDetailElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL).item(0);
		Element shipmentLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE).item(0);*/
		
		String statusAttribute = rootElement.getAttribute(VSIConstants.ATTR_STATUS);
		if(statusAttribute.equals("UPDATE")){
			
			String containerNumber = rootElement.getAttribute(VSIConstants.ATTR_CONTAINER_NO);
			String transferOrderNo = rootElement.getAttribute(VSIConstants.ATTR_TRANSFER_NO);
			
				Document getShipmentListInput = XMLUtil.createDocument("Shipment");
				Element shipmentElement = getShipmentListInput.getDocumentElement();
			
				shipmentElement.setAttribute(VSIConstants.ATTR_ORDER_NO, transferOrderNo);
    			Element containersElement = XMLUtil.appendChild(getShipmentListInput, shipmentElement, VSIConstants.ELE_CONTAINERS, "");
    			XMLUtil.appendChild(getShipmentListInput, shipmentElement, VSIConstants.ELE_CONTAINERS, "");
    			Element containerElement = XMLUtil.appendChild(getShipmentListInput, containersElement, VSIConstants.ELE_CONTAINER, "");
    			containerElement.setAttribute(VSIConstants.ATTR_CONTAINER_NO, containerNumber);
			
    		api = YIFClientFactory.getInstance().getApi();
			Document outDoc = api.invoke(env, VSIConstants.API_GET_SHIPMENT_LIST,getShipmentListInput);
			Element shipmentListRoot = outDoc.getDocumentElement();
			String shipmentKey = shipmentListRoot.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY);
			
			
			Document changeShipmentInput = XMLUtil.createDocument("Shipment");
			Element changeShipmentElement = changeShipmentInput.getDocumentElement();
			changeShipmentElement.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, shipmentKey);
			Element containers = changeShipmentInput.createElement(VSIConstants.ELE_CONTAINERS);
			Element container = changeShipmentInput.createElement(VSIConstants.ELE_CONTAINER);
			/*Element containerDetails = changeShipmentInput.createElement(VSIConstants.ELE_CONTAINER_DETAILS);
			Element containerDetail = changeShipmentInput.createElement(VSIConstants.ELE_CONTAINER_DETAIL);
			Element shipmentLine = changeShipmentInput.createElement(VSIConstants.ELE_SHIPMENT_LINE);*/
			 
			
			changeShipmentElement.appendChild(containers);
			
			XMLUtil.copyElement(changeShipmentInput, rootElement, container);
			containers.appendChild(container);
			
			/*XMLUtil.copyElement(changeShipmentInput, containerDetailsElement, containerDetails);
		//container.appendChild(containerDetails);
			
			XMLUtil.copyElement(changeShipmentInput, containerDetailElement, containerDetail);
			//containerDetails.appendChild(containerDetail);
			
			XMLUtil.copyElement(changeShipmentInput, shipmentLineElement, shipmentLine);
			//containerDetail.appendChild(shipmentLine);*/
			
			
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CHANGE_SHIPMENT,changeShipmentInput);
			
		}//if condition
		else{
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Input Status error");
		}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
			
	}
	
	
}
