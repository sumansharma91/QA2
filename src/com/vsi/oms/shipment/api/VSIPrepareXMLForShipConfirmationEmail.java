package com.vsi.oms.shipment.api;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This component is responsible for sending shipment confirmation mail to the customer on confirm shipment
 * 
 * Input: CONFIRM_SHIPMENT.ON_SUCCESS
 * 
 * Output:
 * 
 * 	<Shipment  EnterpriseCode="" ShipNode=""  PartialShipConfirmStatus="" OrderNo="" SCAC="" ScacAndService=""  CarrierServiceCode="" ActualShipmentDate="">
 *	  <Containers>
 *		<Container ContainerNo="" TrackingNo=""  ContainerGrossWeight="" ContainerGrossWeightUOM="">
 *			<ContainerDetails>
 *				<ContainerDetail Quantity="">
 *					<ShipmentLine  DocumentType="" ItemID="" PrimeLineNo="" SubLineNo="" Quantity="" UnitOfMeasure="" ProductClass="">
 *					</ShipmentLine>
 *				</ContainerDetail>
 *			</ContainerDetails>
 *		</Container>
 *	</Containers>
 *	<ShipmentLines>
 *		<ShipmentLine  DocumentType="" ItemID="" PrimeLineNo="" SubLineNo="" Quantity="" UnitOfMeasure="" ProductClass="" UserCanceledQuantity=""/>
 *	</ShipmentLines>
 * </Shipment>
 * 
 * @author IBM
 *
 */
public class VSIPrepareXMLForShipConfirmationEmail extends VSIBaseCustomAPI {

	public Document prepareXMLForShipConfirmationEmail (YFSEnvironment env, Document docInput) {
		Document docConfEmail = SCXmlUtil.createDocument(VSIConstants.ELE_SHIPMENT);
		Element eleConfEmail = docConfEmail.getDocumentElement();
		Element eleShipment = docInput.getDocumentElement();
		
		// Shipment level attributes
		eleConfEmail.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eleShipment.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE));
		eleConfEmail.setAttribute(VSIConstants.ATTR_SHIP_NODE, eleShipment.getAttribute(VSIConstants.ATTR_SHIP_NODE));
		// TODO: Where should this status come from? It's not in the on_success xml
		eleConfEmail.setAttribute(VSIConstants.ATTR_PARTL_SHP_CNF_STATUS, eleShipment.getAttribute(VSIConstants.ATTR_PARTL_SHP_CNF_STATUS));
		eleConfEmail.setAttribute(VSIConstants.ATTR_ORDER_NO, eleShipment.getAttribute(VSIConstants.ATTR_ORDER_NO));
		eleConfEmail.setAttribute(VSIConstants.ATTR_SCAC, eleShipment.getAttribute(VSIConstants.ATTR_SCAC));
		eleConfEmail.setAttribute(VSIConstants.ATTR_SCAC_AND_SERVICE, eleShipment.getAttribute(VSIConstants.ATTR_SCAC_AND_SERVICE));
		eleConfEmail.setAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE, eleShipment.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE));
		eleConfEmail.setAttribute(VSIConstants.ATTR_ACTUAL_SHIP_DATE, eleShipment.getAttribute(VSIConstants.ATTR_ACTUAL_SHIP_DATE));
		
		// Container level attributes
		Element eleConfEmailContainers = SCXmlUtil.createChild(eleConfEmail, VSIConstants.ELE_CONTAINERS);
		Element eleContainers = SCXmlUtil.getChildElement(eleShipment, VSIConstants.ELE_CONTAINERS);
		ArrayList<Element> alContainerList = SCXmlUtil.getChildren(eleContainers, VSIConstants.ELE_CONTAINER);
		for (Element eleContainer : alContainerList) {
			Element eleConfEmailContainer = SCXmlUtil.createChild(eleConfEmailContainers, VSIConstants.ELE_CONTAINER);
			eleConfEmailContainer.setAttribute(VSIConstants.ATTR_CONTAINER_NO, eleContainer.getAttribute(VSIConstants.ATTR_CONTAINER_NO));
			eleConfEmailContainer.setAttribute(VSIConstants.ATTR_TRACKING_NO, eleContainer.getAttribute(VSIConstants.ATTR_TRACKING_NO));
			eleConfEmailContainer.setAttribute(VSIConstants.ATTR_CONTAINER_GROSS_WEIGHT, eleContainer.getAttribute(VSIConstants.ATTR_CONTAINER_GROSS_WEIGHT));
			eleConfEmailContainer.setAttribute(VSIConstants.ATTR_CONTAINER_GROSS_WEIGHT_UOM, eleContainer.getAttribute(VSIConstants.ATTR_CONTAINER_GROSS_WEIGHT_UOM));
			
			Element eleContainerDetails = SCXmlUtil.getChildElement(eleContainer, VSIConstants.ELE_CONTAINER_DETAILS);
			Element eleConfEmailContainerDetails = SCXmlUtil.createChild(eleConfEmailContainer, VSIConstants.ELE_CONTAINER_DETAILS);
			ArrayList<Element> alContainerDetailList = SCXmlUtil.getChildren(eleContainerDetails, VSIConstants.ELE_CONTAINER_DETAIL);
			for (Element eleContainerDetail : alContainerDetailList) {
				Element eleConfEmailContainerDetail = SCXmlUtil.createChild(eleConfEmailContainerDetails, VSIConstants.ELE_CONTAINER_DETAIL);
				eleConfEmailContainerDetail.setAttribute(VSIConstants.ATTR_QUANTITY, eleContainerDetail.getAttribute(VSIConstants.ATTR_QUANTITY));
				Element eleConShipmentLine = SCXmlUtil.getChildElement(eleContainerDetail, VSIConstants.ELE_SHIPMENT_LINE);
				Element eleConfEmailConShipmentLine = SCXmlUtil.createChild(eleConfEmailContainerDetail, VSIConstants.ELE_SHIPMENT_LINE);
				eleConfEmailConShipmentLine.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, eleConShipmentLine.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE));
				eleConfEmailConShipmentLine.setAttribute(VSIConstants.ATTR_ITEM_ID, eleConShipmentLine.getAttribute(VSIConstants.ATTR_ITEM_ID));
				eleConfEmailConShipmentLine.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO, eleConShipmentLine.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO));
				eleConfEmailConShipmentLine.setAttribute(VSIConstants.ATTR_SUB_LINE_NO, eleConShipmentLine.getAttribute(VSIConstants.ATTR_SUB_LINE_NO));
				eleConfEmailConShipmentLine.setAttribute(VSIConstants.ATTR_UOM, eleConShipmentLine.getAttribute(VSIConstants.ATTR_UOM));
				eleConfEmailConShipmentLine.setAttribute(VSIConstants.ATTR_QUANTITY, eleConShipmentLine.getAttribute(VSIConstants.ATTR_QUANTITY));
				eleConfEmailConShipmentLine.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, eleConShipmentLine.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS));
			} // end inner for loop
		} // end outer for loop
		
		// Shipment Line attributes
		Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, VSIConstants.ELE_SHIPMENT_LINES);
		Element eleConfEmailShipmentLines = SCXmlUtil.createChild(eleConfEmail, VSIConstants.ELE_SHIPMENT_LINES);
		ArrayList<Element> alShipmentLineList = SCXmlUtil.getChildren(eleShipmentLines, VSIConstants.ELE_SHIPMENT_LINE);
		for (Element eleShipmentLine : alShipmentLineList) {
			Element eleConfEmailShipmentLine = SCXmlUtil.createChild(eleConfEmailShipmentLines, VSIConstants.ELE_SHIPMENT_LINE);
			eleConfEmailShipmentLine.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, eleShipmentLine.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE));
			eleConfEmailShipmentLine.setAttribute(VSIConstants.ATTR_ITEM_ID, eleShipmentLine.getAttribute(VSIConstants.ATTR_ITEM_ID));
			eleConfEmailShipmentLine.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO, eleShipmentLine.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO));
			eleConfEmailShipmentLine.setAttribute(VSIConstants.ATTR_SUB_LINE_NO, eleShipmentLine.getAttribute(VSIConstants.ATTR_SUB_LINE_NO));
			eleConfEmailShipmentLine.setAttribute(VSIConstants.ATTR_UOM, eleShipmentLine.getAttribute(VSIConstants.ATTR_UOM));
			eleConfEmailShipmentLine.setAttribute(VSIConstants.ATTR_QUANTITY, eleShipmentLine.getAttribute(VSIConstants.ATTR_QUANTITY));
			eleConfEmailShipmentLine.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, eleShipmentLine.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS));
			// TODO: How should we calculate this number or where should we get it?
			eleConfEmailShipmentLine.setAttribute(VSIConstants.ATTR_USER_CANCELED_QTY, eleShipmentLine.getAttribute(VSIConstants.ATTR_USER_CANCELED_QTY));
		}
		
		return docConfEmail;
	} // end prepareXMLForShipConfirmationEmail()
		
} // end class
