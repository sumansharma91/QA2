package com.vsi.som.shipment;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIShipmentContainerWeight implements VSIConstants
{
	private YFCLogCategory log = YFCLogCategory.instance(VSIShipmentContainerWeight.class);
	public Document shipmentContainerWeightCalculation(YFSEnvironment env,Document inXml)
	{
		if(log.isDebugEnabled())
			log.debug("Input for VSIShipmentContainerWeight.shipmentContainerWeightCalculation => "+XMLUtil.getXMLString(inXml));
		try
		{
		NodeList containerNode = inXml.getElementsByTagName(ELE_CONTAINER);
		for (int i = 0; i < containerNode.getLength(); i++) 
		{
			Element containerElem = (Element) inXml.getElementsByTagName(ELE_CONTAINER).item(i);
			Document getContainerInXml=XMLUtil.createDocument(ELE_CONTAINER);
			Element containerEle = getContainerInXml.getDocumentElement();
			containerEle.setAttribute(ATTR_CONTAINER_NO,containerElem.getAttribute(ATTR_CONTAINER_NO));
			Document getContainerDtlsOutXML = VSIUtils.invokeAPI(env, TEMPLATE_GET_SHIPMENT_CONTAINER_LIST, API_GET_SHIPMENT_CONTAINER_LIST, getContainerInXml);
			Element containerElement = (Element) getContainerDtlsOutXML.getElementsByTagName(ELE_CONTAINER).item(0);
			Element eleExtn = (Element) containerElement.getElementsByTagName(ELE_EXTN).item(0);
			String extnParcelType = eleExtn.getAttribute(ATTR_EXTN_PARCEL_TYPE);
			log.info("extnParcelType => " +extnParcelType);
			if(!extnParcelType.equalsIgnoreCase(""))
			{
				ArrayList<Element> parcelWeight = VSIUtils.getCommonCodeList(env, PARCEL_WEIGHT_COMMON_CODE_TYPE , extnParcelType, ATTR_DEFAULT);
				ArrayList<Element> parcelVolume = VSIUtils.getCommonCodeList(env, PARCEL_VOLUME_COMMON_CODE_TYPE , extnParcelType, ATTR_DEFAULT);
				ArrayList<Element> parcelLength = VSIUtils.getCommonCodeList(env, PARCEL_LENGTH_COMMON_CODE_TYPE , extnParcelType, ATTR_DEFAULT);
				ArrayList<Element> parcelWidth = VSIUtils.getCommonCodeList(env, PARCEL_WIDTH_COMMON_CODE_TYPE , extnParcelType, ATTR_DEFAULT);
				ArrayList<Element> parcelHeight = VSIUtils.getCommonCodeList(env, PARCEL_HEIGHT_COMMON_CODE_TYPE , extnParcelType, ATTR_DEFAULT);
			if(!parcelWeight.isEmpty() && !parcelVolume.isEmpty())
			{
				Element parcelEle=parcelWeight.get(0);
				String parcelWeightValue = parcelEle.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
				Element parcelElem=parcelVolume.get(0);
				String parcelVolumeValue = parcelElem.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
				Element containerDtlsEle = (Element) containerElem.getElementsByTagName(ELE_CONTAINER_DETAILS).item(0);
				NodeList containerDtlNode = containerDtlsEle.getElementsByTagName(ELE_CONTAINER_DETAIL);
				int quantity;
				double itemWeight, netWeight=0.0; 
				for (int j = 0; j < containerDtlNode.getLength(); j++) 
				{
					Element containerDtlElem = (Element) containerDtlNode.item(j);
					quantity = Integer.parseInt(containerDtlElem.getAttribute(ATTR_QUANTITY));
					Element shipmentLineEle = (Element) containerDtlElem.getElementsByTagName(ELE_SHIPMENT_LINE).item(0);
					Document docGetItemListInput = SCXmlUtil.createDocument(ELE_ITEM);
					SCXmlUtil.setAttribute(docGetItemListInput.getDocumentElement(), ATTR_ITEM_ID, shipmentLineEle.getAttribute(ATTR_ITEM_ID));
					Document docGetItemListOutput = VSIUtils.invokeAPI(env,TEMPLATE_GET_ITEM_LIST,API_GET_ITEM_LIST, docGetItemListInput);
					Element primaryInfoEle = (Element) docGetItemListOutput.getElementsByTagName(ELE_PRIMARY_INFORMATION).item(0);
					itemWeight = Double.parseDouble(primaryInfoEle.getAttribute(ATTR_UNIT_WEIGHT));
					netWeight = netWeight + (itemWeight * quantity);	
				}
				containerElem.setAttribute(ATTR_CONTAINER_NET_WEIGHT, Double.toString(netWeight));
				Double grossWeight = netWeight + Double.parseDouble(parcelWeightValue);
				containerElem.setAttribute(ATTR_CONTAINER_GROSS_WEIGHT, Double.toString(grossWeight));
				Element elemExtn = SCXmlUtil.createChild(containerElem,ELE_EXTN);
				elemExtn.setAttribute(ATTR_EXTN_CONTAINER_VOLUME, parcelVolumeValue);
				containerElem.setAttribute(ATTR_ACTUAL_WEIGHT, Double.toString(grossWeight));
				log.info("Net => " +containerElem.getAttribute(ATTR_CONTAINER_NET_WEIGHT) +"Gross => "+containerElem.getAttribute(ATTR_CONTAINER_GROSS_WEIGHT)+"Volume => "+eleExtn.getAttribute(ATTR_EXTN_CONTAINER_VOLUME)+"Actual => "+containerElem.getAttribute(ATTR_ACTUAL_WEIGHT));
				if(!parcelLength.isEmpty())
				{
					Element parcelLengthEle=parcelLength.get(0);
					containerElem.setAttribute(ATTR_CONTAINER_LENGTH, parcelLengthEle.getAttribute(ATTR_CODE_SHORT_DESCRIPTION));
				}
				if(!parcelWidth.isEmpty())
				{
					Element parcelWidthEle=parcelWidth.get(0);
					containerElem.setAttribute(ATTR_CONTAINER_WIDTH, parcelWidthEle.getAttribute(ATTR_CODE_SHORT_DESCRIPTION));
				}
				if(!parcelHeight.isEmpty())
				{
					Element parcelHeightEle=parcelHeight.get(0);
					containerElem.setAttribute(ATTR_CONTAINER_HEIGHT, parcelHeightEle.getAttribute(ATTR_CODE_SHORT_DESCRIPTION));
				}
				}
			}		
		}
		VSIUtils.invokeAPI(env,API_CHANGE_SHIPMENT, inXml);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return inXml;
	}
}
