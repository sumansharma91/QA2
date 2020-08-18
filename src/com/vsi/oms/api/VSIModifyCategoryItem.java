package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class checks whether the given item(s) is associated with a category with OrganizationCode="VSI-Cat"
 * and CategoryPath="/VSI-CatMasterCatalog/ALL_PRODS". If not, it will add this category.
 * 
 * @author IBM
 *
 */
public class VSIModifyCategoryItem {

	private YFCLogCategory log = YFCLogCategory.instance(VSIModifyCategoryItem.class);
	
	/**
	 * From API output, loop through CategoryList Nodes.
 	 * If no record found with OrganizationCode="VSI-Cat" and CategoryPath="/VSI-CatMasterCatalog/ALL_PRODS",
     * call modifyCategoryItem API to add item to ALL_PRODS category. 
	 * 
	 * * Input: <ItemList><Item ItemID="" UnitOfMeasure="" OrganizationCode="" /></ItemList>
	 * 
     * getItemList output template: 
     *	<ItemList>
     *		<Item ItemID="">
     *			<CategoryList>
	 *				<Category CategoryPath="" OrganizationCode=""/>
	 *			</CategoryList>
	 *		</Item>
     *	</ItemList>
     *
	 * @param env
	 * @param docInput
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws YIFClientCreationException
	 */
	public Document modifyCategoryItem(YFSEnvironment env, Document docInput) 
			throws YFSException, RemoteException, YIFClientCreationException{
		
		if (log.isVerboseEnabled()) {
			log.verbose(" VSIModifyCategoryItem.modifyCategoryItem() : \n" + XMLUtil.getXMLString(docInput));
		}
		
		Element eleItemListIn = docInput.getDocumentElement();
		Document docGetItemList = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
		
		Element eleGetItemList = docGetItemList.getDocumentElement();
		//Start: Modified for JIRA SU-96
		ArrayList<Element> alItemList = SCXmlUtil.getChildren(eleItemListIn, VSIConstants.ELE_ITEM);
		
		for (Element eleItemIn : alItemList) {
			eleGetItemList.setAttribute(VSIConstants.ATTR_ITEM_ID, eleItemIn.getAttribute(VSIConstants.ATTR_ITEM_ID));
			eleGetItemList.setAttribute(VSIConstants.ATTR_ORG_CODE, eleItemIn.getAttribute(VSIConstants.ATTR_ORG_CODE));
			eleGetItemList.setAttribute(VSIConstants.ATTR_UOM, eleItemIn.getAttribute(VSIConstants.ATTR_UOM));
			Document docItemList = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ITEM_LIST_VSIModifyCategoryItem, VSIConstants.API_GET_ITEM_LIST, docGetItemList);
			
			if (log.isVerboseEnabled()) {
				log.verbose(" VSIModifyCategoryItem.modifyCategoryItem() : \n" + XMLUtil.getXMLString(docItemList));
			}
			
			Element eleItemList = docItemList.getDocumentElement();
			Element eleItem = SCXmlUtil.getChildElement(eleItemList, VSIConstants.ELE_ITEM);
			Element eleCategoryList = SCXmlUtil.getChildElement(eleItem, VSIConstants.ELE_CategoryList);
			boolean boolHasCategory = false;
			
			if(eleCategoryList.hasChildNodes()){
		
				ArrayList<Element> alCategory = SCXmlUtil.getChildren(eleCategoryList, VSIConstants.ELE_CAT);
				
				for(Element eleCategory: alCategory ){
					if(eleCategory.getAttribute(VSIConstants.ATTR_ORG_CODE).equals(VSIConstants.ENT_VSI_CAT)  && 
					   eleCategory.getAttribute(VSIConstants.ATTR_CAT_PATH).equals(VSIConstants.VSI_CAT_PATH)) {
						boolHasCategory = true; //If we find Organization Code="VSI-Cat" and CategoryPath="/VSI-CatMasterCatalog/ALL_PRODS" 	
						break;
					}			
				} 
			} 
			if(!boolHasCategory) {
				callModifyCategoryItem(env, eleItemIn);
			}
		}
		//End: Modified for JIRA SU-96
		return docInput;	
				
	} //End of modifyCategoryItem method
	
	/**
	 * Adds a Category with Organization Code="VSI-Cat" and CategoryPath = "/VSI-CatMasterCatalog/ALL_PRODS"
	 * 
	 * @param env
	 * @param eleItemIn - Item Element containing the ItemID
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws YIFClientCreationException
	 */
	public void callModifyCategoryItem(YFSEnvironment env, Element eleItemIn) throws YFSException, RemoteException, YIFClientCreationException {
		
		if (log.isVerboseEnabled()) {
			log.verbose(" VSIModifyCategoryItem.callModifyCategoryItem() : \n" + XMLUtil.getElementXMLString(eleItemIn));
		}
		
		Document docModifyCategoryItem = SCXmlUtil.createDocument(VSIConstants.ELE_MOD_CAT_ITEM);
		Element eleModifyCategory = docModifyCategoryItem.getDocumentElement();
		Element eleCat = SCXmlUtil.createChild(eleModifyCategory, VSIConstants.ELE_CAT);	
		eleCat.setAttribute(VSIConstants.ATTR_CAT_PATH, VSIConstants.VSI_CAT_PATH);
		eleCat.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ENT_VSI_CAT);
		Element eleCatItemList = SCXmlUtil.createChild(eleCat, VSIConstants.ELE_CAT_ITEM_LIST);
		Element eleCatItem = SCXmlUtil.createChild(eleCatItemList, VSIConstants.ELE_CAT_ITEM);	
		eleCatItem.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CREATE);
		eleCatItem.setAttribute(VSIConstants.ATTR_ITEM_ID, eleItemIn.getAttribute(VSIConstants.ATTR_ITEM_ID));
		//Start: Modified for JIRA SU-96
		eleCatItem.setAttribute(VSIConstants.ATTR_ORG_CODE, eleItemIn.getAttribute(VSIConstants.ATTR_ORG_CODE));
		eleCatItem.setAttribute(VSIConstants.ATTR_UOM, eleItemIn.getAttribute(VSIConstants.ATTR_UOM));
		//End: Modified for JIRA SU-96
		
		/*
	     * Input: 
	     *<ModifyCategoryItems CallingOrganizationCode="">
	     *	<Category CategoryPath="/VSI-CatMasterCatalog/ALL_PRODS" OrganizationCode="VSI-Cat">
	     *		<CategoryItemList>
	     *			<CategoryItem Action="Create" ItemID="<ItemID>" OrganizationCode="VSI-Cat" UnitOfMeasure="<UnitOfMeasure>"/>
	     *		</CategoryItemList>
	     *	</Category>
	     *</ModifyCategoryItems>
	     */
		if (log.isVerboseEnabled()) {
			log.verbose(" VSIModifyCategoryItem.callModifyCategoryItem() : \n" + XMLUtil.getXMLString(docModifyCategoryItem));
		}
	     VSIUtils.invokeAPI(env, VSIConstants.API_MODIFY_CATEGORY_ITEM, docModifyCategoryItem);
	} // End of callModifyCategoryItem
	
} //End of VSIModifyCategoryItem Class
