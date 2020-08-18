package com.vsi.oms.userexit;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.INVGetSupplyCorrectionsUE;
import com.yantra.yfc.log.YFCLogCategory;
import com.vsi.oms.utils.XMLUtil;

public class VSIGetSupplyCorrectionsUE implements INVGetSupplyCorrectionsUE,VSIConstants {
	private static YFCLogCategory logger = (YFCLogCategory) YFCLogCategory
		      .getLogger(VSIGetSupplyCorrectionsUE.class.getName());
	@Override
	public Document getSupplyCorrections(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		
		Element eleInXML = inXML.getDocumentElement();
		String strCallingOrganizationCode = eleInXML.getAttribute(ATTR_CALL_ORG_CODE);
		if(YFCCommon.isVoid(strCallingOrganizationCode)) {
			strCallingOrganizationCode="VSI.com";
		}
		if(logger.isDebugEnabled()){
	    	logger.debug("************* inputXML VSIGetSupplyCorrectionsUE*************** " + XMLUtil.getXMLString(inXML));
	    }
		ArrayList <Element> arrItems = SCXmlUtil.getChildren(eleInXML, ELE_ITEM);
		int quantityFour = 0;
		int quantityFive = 0;
		for(Element eleItem:arrItems) {
			String strOrgCode=eleItem.getAttribute(ATTR_ORG_CODE);
			if(strOrgCode.equalsIgnoreCase(strCallingOrganizationCode)) {
				Element eleInSupplies = SCXmlUtil.getChildElement(eleItem, "Supplies");
				
					
			//Input to get MCLINV inventory	
				Document docGetInventorySupplyMCL = SCXmlUtil.createDocument("InventorySupply");
				Element eleGetInventorySupplyMCL = docGetInventorySupplyMCL.getDocumentElement();
				eleGetInventorySupplyMCL.setAttribute(ATTR_ITEM_ID, eleItem.getAttribute(ATTR_ITEM_ID));
				eleGetInventorySupplyMCL.setAttribute(ATTR_ORG_CODE, "MCLINV");
				eleGetInventorySupplyMCL.setAttribute(ATTR_PRODUCT_CLASS, eleItem.getAttribute(ATTR_PRODUCT_CLASS));
				eleGetInventorySupplyMCL.setAttribute(ATTR_UOM, eleItem.getAttribute(ATTR_UOM));
				eleGetInventorySupplyMCL.setAttribute("SupplyType","ONHAND");
			//Input to get ADPINV inventory		
				Document docGetInventorySupplyADP = SCXmlUtil.createDocument("InventorySupply");
				Element eleGetInventorySupplyADP = docGetInventorySupplyADP.getDocumentElement();
				eleGetInventorySupplyADP.setAttribute(ATTR_ITEM_ID, eleItem.getAttribute(ATTR_ITEM_ID));
				eleGetInventorySupplyADP.setAttribute(ATTR_ORG_CODE, "ADPINV");
				eleGetInventorySupplyADP.setAttribute(ATTR_PRODUCT_CLASS, eleItem.getAttribute(ATTR_PRODUCT_CLASS));
				eleGetInventorySupplyADP.setAttribute(ATTR_UOM, eleItem.getAttribute(ATTR_UOM));
				eleGetInventorySupplyADP.setAttribute("SupplyType","ONHAND");
				
				int quantityMCLFour = 0;
				int quantityMCLFive = 0;
				int quantityADPFour = 0;
				int quantityADPFive = 0;
				
			
				try {
					Document docGetInventorySupplyOutputMCL = VSIUtils.invokeAPI(env, "getInventorySupply", docGetInventorySupplyMCL);
					Element eleGetInventorySupplyOutputMCL = docGetInventorySupplyOutputMCL.getDocumentElement();
					Element eleSuppliesMCL = SCXmlUtil.getChildElement(eleGetInventorySupplyOutputMCL,"Supplies");
					if(!YFCCommon.isVoid(eleSuppliesMCL)) {
						ArrayList<Element> arrSuppliesMCL = SCXmlUtil.getChildren(eleSuppliesMCL, "InventorySupply");
						for(Element eleInventorySupplyMCL:arrSuppliesMCL) {
							Double dbQuantityMCL = SCXmlUtil.getDoubleAttribute(eleInventorySupplyMCL, ATTR_QUANTITY);
							String ShipNodeMCL = SCXmlUtil.getAttribute(eleInventorySupplyMCL, ATTR_SHIP_NODE);
							String SupplyTypeMCL = SCXmlUtil.getAttribute(eleInventorySupplyMCL, "SupplyType");
							if(dbQuantityMCL>0 && ShipNodeMCL.equals("9004") && SupplyTypeMCL.equals("PO_PLACED")) {
								
								quantityMCLFour = SCXmlUtil.getIntAttribute(eleInventorySupplyMCL, ATTR_QUANTITY);
								
							}
							if(dbQuantityMCL>0 && ShipNodeMCL.equals("9005") && SupplyTypeMCL.equals("PO_PLACED")) {
								
								quantityMCLFive = SCXmlUtil.getIntAttribute(eleInventorySupplyMCL, ATTR_QUANTITY);
								
							}
						}
						
					}
					
					Document docGetInventorySupplyOutputADP = VSIUtils.invokeAPI(env, "getInventorySupply", docGetInventorySupplyADP);
					Element eleGetInventorySupplyOutputADP = docGetInventorySupplyOutputADP.getDocumentElement();
					Element eleSuppliesADP = SCXmlUtil.getChildElement(eleGetInventorySupplyOutputADP,"Supplies");
					if(!YFCCommon.isVoid(eleSuppliesADP)) {
						ArrayList<Element> arrSuppliesADP = SCXmlUtil.getChildren(eleSuppliesADP, "InventorySupply");
						for(Element eleInventorySupplyADP:arrSuppliesADP) {
							Double dbQuantityADP = SCXmlUtil.getDoubleAttribute(eleInventorySupplyADP, ATTR_QUANTITY);
							String ShipNodeADP = SCXmlUtil.getAttribute(eleInventorySupplyADP, ATTR_SHIP_NODE);
							String SupplyTypeADP = SCXmlUtil.getAttribute(eleInventorySupplyADP, "SupplyType");
							if(dbQuantityADP>0 && ShipNodeADP.equals("9004") && SupplyTypeADP.equals("PO_PLACED")) {
															
								quantityADPFour = SCXmlUtil.getIntAttribute(eleInventorySupplyADP, ATTR_QUANTITY);
								
							}
							if(dbQuantityADP>0 && ShipNodeADP.equals("9005") && SupplyTypeADP.equals("PO_PLACED")) {
															
								quantityADPFive = SCXmlUtil.getIntAttribute(eleInventorySupplyADP, ATTR_QUANTITY);
								
							}
						}
						
					}
					
					if(!YFCCommon.isVoid(eleInSupplies)) {
						ArrayList<Element> arrSupplies = SCXmlUtil.getChildren(eleInSupplies, "Supply");
						for(Element eleSupply:arrSupplies) {
							Double dbQuantityPO = SCXmlUtil.getDoubleAttribute(eleSupply, ATTR_QUANTITY);
							String ShipNode = SCXmlUtil.getAttribute(eleSupply, ATTR_SHIP_NODE);
							String SupplyType = SCXmlUtil.getAttribute(eleSupply, "SupplyType");
							if(dbQuantityPO>0 && ShipNode.equals("9004") && SupplyType.equals("PO_PLACED")) {
								
								quantityFour = SCXmlUtil.getIntAttribute(eleSupply, ATTR_QUANTITY);
								quantityFour = quantityFour - (quantityADPFour + quantityMCLFour);
								eleSupply.setAttribute("Quantity",Integer.toString(quantityFour) );
							}
							if(dbQuantityPO>0 && ShipNode.equals("9005") && SupplyType.equals("PO_PLACED")) {
								
								quantityFive = SCXmlUtil.getIntAttribute(eleSupply, ATTR_QUANTITY);
								quantityFive = quantityFive - (quantityADPFive + quantityMCLFive);
								eleSupply.setAttribute("Quantity",Integer.toString(quantityFive) );
							}
						}
						
					}
					
					
					
				} catch (YFSException | RemoteException | YIFClientCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(logger.isDebugEnabled()){
	    	logger.debug("************* outputXML VSIGetSupplyCorrectionsUE*************** " + XMLUtil.getXMLString(inXML));
	    }
		return inXML;
	}

}
