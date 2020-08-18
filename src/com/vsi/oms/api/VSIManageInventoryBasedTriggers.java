package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIManageInventoryBasedTriggers implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIManageInventoryBasedTriggers.class);
	public void vsiManageInvBasedTriggers(YFSEnvironment env, Document inXML)
			throws Exception {
		Element eleInXML = inXML.getDocumentElement();
		String strInventoryOrganizationCode = eleInXML.getAttribute("InventoryOrganizationCode");
		/*Input--
		 * <Supply 
		 * EnterpriseCode="" InventoryItemKey="" InventoryOrganizationCode="" ItemID="" ProductClass="" Quantity="" ShipNode=""  SupplyType="" UnitOfMeasure=""/>
		 */
		
			
			
			//Create Inventory Activity for MCL and VSI.com if organization code is
			//VSIINV or MCLINV. For DTCINV/ADPINV create activity for VSI.com only
			
			//Sample createInventoryActivity Message
			
			/*<InventoryActivityList>
			 * <InventoryActivity CreateForInvItemsAtNode="N" DistributionRuleId="MCL_DC" ItemID="1550458" 
			 Node="" OrganizationCode="MCL" ProcessedFlag="" ProductClass="GOOD" UnitOfMeasure="EACH"/>
			 </InventoryActivityList
			*/
			/*OMS-1137 RTAM changes : Start
			Document docCreateInventoryActivityList = SCXmlUtil.createDocument("InventoryActivityList");
			Element eleCreateInventoryActivityList = docCreateInventoryActivityList.getDocumentElement();
			
			String strInventoryOrganizationCode = eleInXML.getAttribute("InventoryOrganizationCode");
			if(strInventoryOrganizationCode.equalsIgnoreCase("MCLINV")||strInventoryOrganizationCode.equalsIgnoreCase("VSIINV")) {
					
				Element eleMCLInventoryActivity = SCXmlUtil.createChild(eleCreateInventoryActivityList, "InventoryActivity");
				
				eleMCLInventoryActivity.setAttribute(ATTR_ITEM_ID, eleInXML.getAttribute(ATTR_ITEM_ID));
				eleMCLInventoryActivity.setAttribute(ATTR_PRODUCT_CLASS, eleInXML.getAttribute(ATTR_PRODUCT_CLASS));
				eleMCLInventoryActivity.setAttribute(ATTR_UOM, eleInXML.getAttribute(ATTR_UOM));
				eleMCLInventoryActivity.setAttribute(ATTR_DISTRIBUTION_RULE_ID, "MCL_DC");
				eleMCLInventoryActivity.setAttribute(ATTR_ORGANIZATION_CODE, "MCL");
				
				Element eleVSIInventoryActivity = SCXmlUtil.createChild(eleCreateInventoryActivityList, "InventoryActivity");
				eleVSIInventoryActivity.setAttribute(ATTR_ITEM_ID, eleInXML.getAttribute(ATTR_ITEM_ID));
				eleVSIInventoryActivity.setAttribute(ATTR_PRODUCT_CLASS, eleInXML.getAttribute(ATTR_PRODUCT_CLASS));
				eleVSIInventoryActivity.setAttribute(ATTR_UOM, eleInXML.getAttribute(ATTR_UOM));
				eleVSIInventoryActivity.setAttribute(ATTR_DISTRIBUTION_RULE_ID, "VSI_DC");
				eleVSIInventoryActivity.setAttribute(ATTR_ORGANIZATION_CODE, "VSI.com");
				
			}
			
			if(strInventoryOrganizationCode.equalsIgnoreCase("DTCINV")) {
				Element eleVSIInventoryActivity = SCXmlUtil.createChild(eleCreateInventoryActivityList, "InventoryActivity");
				eleVSIInventoryActivity.setAttribute(ATTR_ITEM_ID, eleInXML.getAttribute(ATTR_ITEM_ID));
				eleVSIInventoryActivity.setAttribute(ATTR_PRODUCT_CLASS, eleInXML.getAttribute(ATTR_PRODUCT_CLASS));
				eleVSIInventoryActivity.setAttribute(ATTR_UOM, eleInXML.getAttribute(ATTR_UOM));
				eleVSIInventoryActivity.setAttribute(ATTR_DISTRIBUTION_RULE_ID, "VSI_DC");
				eleVSIInventoryActivity.setAttribute(ATTR_ORGANIZATION_CODE, "VSI.com");
			}
			
			if(eleCreateInventoryActivityList.hasChildNodes()) {
				VSIUtils.invokeAPI(env, "createInventoryActivityList", docCreateInventoryActivityList);
			}
			
			OMS-1137 RTAM changes : End*/
			//For VSIINV if onhand change is positive or any kind change in PO, 
			//create an IBA entry 
			String strSupplyType = eleInXML.getAttribute("SupplyType");
			if(strSupplyType.equalsIgnoreCase("PO_PLACED")) {
				if(strInventoryOrganizationCode.equalsIgnoreCase("VSIINV")) {
					
					//OMS-1295 : Start
					Set<String> setOrgCode=fetchOrganizationCodeListFromCommonCode(env);
					for (String strOrgCode : setOrgCode) {
						if(log.isDebugEnabled()){
							log.debug("OrganizationCode from commoncode "+strOrgCode);
						}
						//Check if Demand exists for item/node/supply type combination.
						/*
						<DemandDetails DemandType=""  DocumentType="" ItemID="Required"  
						OrganizationCode="Required" ProductClass=""  ShipNode="" UnitOfMeasure=""/>
						*/
						Document docGetDemandDetails = SCXmlUtil.createDocument("DemandDetails");
						Element eleDemandDetails = docGetDemandDetails.getDocumentElement();
						
						eleDemandDetails.setAttribute("DocumentType","0001");
						eleDemandDetails.setAttribute("OrganizationCode",strOrgCode);
						
						eleDemandDetails.setAttribute("DemandType","ALLOCATED");
						eleDemandDetails.setAttribute("DemandTypeQryType","NE");
						eleDemandDetails.setAttribute("ShipNode",eleInXML.getAttribute("ShipNode"));
						
						eleDemandDetails.setAttribute(ATTR_ITEM_ID,eleInXML.getAttribute(ATTR_ITEM_ID));
						eleDemandDetails.setAttribute(ATTR_PRODUCT_CLASS, eleInXML.getAttribute(ATTR_PRODUCT_CLASS));
						eleDemandDetails.setAttribute(ATTR_UOM, eleInXML.getAttribute(ATTR_UOM));
						
						eleDemandDetails.setAttribute("DemandTypeQryType","NE");
						
						DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD);
						Date date = new Date();
						String strCurrentDate = sdf.format(date);
						
						eleDemandDetails.setAttribute("DemandShipDate",strCurrentDate);
						eleDemandDetails.setAttribute("DemandShipDateQryType","GT");
						
						
						Document docOutGetDemandDetails = VSIUtils.invokeAPI(env, "getDemandDetailsList", docGetDemandDetails);
						Element eleOutGetDemandDetails = docOutGetDemandDetails.getDocumentElement();
						
						if(eleOutGetDemandDetails.hasChildNodes()) {
							
							//Create IBA trigger
							/*
							 * <ItemBasedAllocationTrigger  IBARunRequired="Y" ItemID="1973304"  Node="9004" Operation="" OrganizationCode="VSI.com"
							 *   ProductClass="GOOD" UnitOfMeasure="EACH"/>
							 */
							
							Document docIBA = SCXmlUtil.createDocument("ItemBasedAllocationTrigger");
							Element eleIBA = docIBA.getDocumentElement();
							eleIBA.setAttribute("IBARunRequired", "Y");
							eleIBA.setAttribute(ATTR_ITEM_ID,eleInXML.getAttribute(ATTR_ITEM_ID));
							eleIBA.setAttribute(ATTR_PRODUCT_CLASS, eleInXML.getAttribute(ATTR_PRODUCT_CLASS));
							eleIBA.setAttribute(ATTR_UOM, eleInXML.getAttribute(ATTR_UOM));
							eleIBA.setAttribute("Node", eleInXML.getAttribute("ShipNode"));
							eleIBA.setAttribute("OrganizationCode", strOrgCode);
							VSIUtils.invokeAPI(env, "manageItemBasedAllocationTrigger", docIBA);
						}
					}
				}
			}
		}
	
	/**
	 * 
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 */
	private Set<String> fetchOrganizationCodeListFromCommonCode(YFSEnvironment env) throws ParserConfigurationException, 
	YFSException, RemoteException, YIFClientCreationException {
		HashSet<String> setOrgCode=new HashSet<String>();
		Document docInput=XMLUtil.createDocument(ELE_COMMON_CODE);
		Element eleCommonCode = docInput.getDocumentElement();
		eleCommonCode.setAttribute(ATTR_CODE_TYPE, "VSI_IBA_ORG_LIST");
		
		if(log.isDebugEnabled()){
			log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInput));
		}
		Document docCommonCodeListOP=VSIUtils.invokeAPI(env,API_COMMON_CODE_LIST, docInput);
		NodeList nlCommonCode = docCommonCodeListOP
				.getElementsByTagName(ELE_COMMON_CODE);
		int iCount=nlCommonCode.getLength();
		for (int i = 0; i < iCount; i++) {
			Element eleCommonCodeOutput=((Element) nlCommonCode.item(i));
			String strShortDesc=eleCommonCodeOutput.getAttribute(ATTR_CODE_VALUE);
			setOrgCode.add(strShortDesc);
		}
		return setOrgCode;
	}
}
