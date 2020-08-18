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
import com.yantra.yfs.japi.ue.INVGetDemandCorrectionsUE;

public class VSIGetDemandCorrectionsUE implements INVGetDemandCorrectionsUE,VSIConstants {

	@Override
	public Document getDemandCorrections(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		
		Element eleInXML = inXML.getDocumentElement();
		String strCallingOrganizationCode = eleInXML.getAttribute(ATTR_CALL_ORG_CODE);
		if(YFCCommon.isVoid(strCallingOrganizationCode)) {
			strCallingOrganizationCode="VSI.com";
		}
		ArrayList <Element> arrItems = SCXmlUtil.getChildren(eleInXML, ELE_ITEM);
		for(Element eleItem:arrItems) {
			String strOrgCode=eleItem.getAttribute(ATTR_ORG_CODE);
			if(strOrgCode.equalsIgnoreCase(strCallingOrganizationCode)) {
				Element eleInDemands = SCXmlUtil.getChildElement(eleItem, "Demands");
				Document docGetInventorySupply = SCXmlUtil.createDocument("InventorySupply");
				Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
				eleGetInventorySupply.setAttribute(ATTR_ITEM_ID, eleItem.getAttribute(ATTR_ITEM_ID));
				eleGetInventorySupply.setAttribute(ATTR_ORG_CODE, "VSIINV");
				eleGetInventorySupply.setAttribute(ATTR_PRODUCT_CLASS, eleItem.getAttribute(ATTR_PRODUCT_CLASS));
				eleGetInventorySupply.setAttribute(ATTR_UOM, eleItem.getAttribute(ATTR_UOM));
				eleGetInventorySupply.setAttribute("SupplyType","ONHAND");
			
				try {
					Document docGetInventorySupplyOutput = VSIUtils.invokeAPI(env, "getInventorySupply", docGetInventorySupply);
					Element eleGetInventorySupplyOutput = docGetInventorySupplyOutput.getDocumentElement();
					Element eleSupplies = SCXmlUtil.getChildElement(eleGetInventorySupplyOutput,"Supplies");
					if(!YFCCommon.isVoid(eleSupplies)) {
						ArrayList<Element> arrSupplies = SCXmlUtil.getChildren(eleSupplies, "InventorySupply");
						for(Element eleInventorySupply:arrSupplies) {
							Double dbQuantity = SCXmlUtil.getDoubleAttribute(eleInventorySupply, ATTR_QUANTITY);
							if(dbQuantity<0) {
								Element eleTempDemand = SCXmlUtil.createChild(eleInDemands, "Demand");
								eleTempDemand.setAttribute("DemandType", "ALLOCATED");
								eleTempDemand.setAttribute("OwnerKey", strCallingOrganizationCode);
								int quantity = -(SCXmlUtil.getIntAttribute(eleInventorySupply, ATTR_QUANTITY));
								eleTempDemand.setAttribute("Quantity",Integer.toString(quantity) );
								eleTempDemand.setAttribute("ShipNode",SCXmlUtil.getAttribute(eleInventorySupply, ATTR_SHIP_NODE));
							}
						}
						
					}
					
				} catch (YFSException | RemoteException | YIFClientCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return inXML;
	}

}
