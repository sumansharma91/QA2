package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIRegionSchemaUpload {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIManageUser.class);

	public void  regionUpload (YFSEnvironment yfsEnv, Document docInXML)
			throws Exception {
		if(log.isDebugEnabled()){
		log.debug("*** Inside Method regionUpload");
		log.info("***Input XML"+SCXmlUtil.getString(docInXML));
		}
		String firstMessage = docInXML.getDocumentElement().getAttribute("FirstMessage");
		String lastMessage = docInXML.getDocumentElement().getAttribute("LastMessage");
		
		List<String> lSourcingRegion = new ArrayList<>();
		lSourcingRegion.add("Southwest");
		lSourcingRegion.add("Northeast");
		lSourcingRegion.add("Region 3");
		lSourcingRegion.add("Region 4");
		
		// disable enable cache
		if(!YFCCommon.isVoid(firstMessage) || !YFCCommon.isVoid(lastMessage))
		{
			disableEnableCache(firstMessage,lastMessage,yfsEnv);
		}

		if(!YFCCommon.isVoid(lastMessage))
		{
			return;
		}

		Element zipCodeRangeEle = SCXmlUtil.getXpathElement(docInXML.getDocumentElement(), "/Region/ZipCodeRanges/ZipCodeRange");
		String fromZip = zipCodeRangeEle.getAttribute("FromZip");
		String regionName = docInXML.getDocumentElement().getAttribute("RegionName");
		Document getRegionDoc = SCXmlUtil.createDocument("Region");
		getRegionDoc.getDocumentElement().setAttribute("RegionSchemaName", "ALL_US");
		Element elePersonInfo = SCXmlUtil.createChild(getRegionDoc.getDocumentElement(), "PersonInfo");
		elePersonInfo.setAttribute("Country", "US");
		elePersonInfo.setAttribute("ZipCode",fromZip);

		Document getRegionDocOut = VSIUtils.invokeAPI(yfsEnv, "global/template/api/VSIRegionSchemaLoad_getRegionList.xml", "getRegionList",
				getRegionDoc);

		Document docChangeRegionTemplate = SCXmlUtil.createDocument("Region");
		docChangeRegionTemplate.getDocumentElement().setAttribute("RegionName", "");


		if (getRegionDocOut.getDocumentElement().hasChildNodes()) {
			NodeList nRegionList = getRegionDocOut.getElementsByTagName(VSIConstants.ELE_REGION);
			int nRegionListLength = nRegionList.getLength();
			if (nRegionListLength > 0) {
				for (int i = 0; i < nRegionListLength; i++) {
					Element eleRegion = (Element) nRegionList.item(i);
					String strRegionName = eleRegion.getAttribute(VSIConstants.ATTR_REGION_NAME);
					if(regionName.equals(strRegionName))
					{
						return;
					}
					else if ((lSourcingRegion.contains(regionName) && lSourcingRegion.contains(strRegionName))) {
						Document doc1 = VSIUtils.cloneDocument(docInXML);
						doc1.getDocumentElement().setAttribute(VSIConstants.ATTR_REGION_NAME, strRegionName);
						Element zipCodeRangeEle1 = SCXmlUtil.getXpathElement(doc1.getDocumentElement(),
								"/Region/ZipCodeRanges/ZipCodeRange");
						zipCodeRangeEle1.setAttribute("Action", "REMOVE");
						VSIUtils.invokeAPI(yfsEnv, docChangeRegionTemplate, "changeRegion", doc1);
					}

				}


			

			}
		}
		VSIUtils.invokeAPI(yfsEnv, docChangeRegionTemplate, "changeRegion",
				docInXML);
	}

	private void disableEnableCache(String firstMessage, String lastMessage, YFSEnvironment env) throws YFSException, RemoteException, YIFClientCreationException 
	{
		// TODO Auto-generated method stub
		Document cacheGroupsDoc = SCXmlUtil.createDocument("CachedGroups");
		Element cachedGroupEle = SCXmlUtil.createChild(cacheGroupsDoc.getDocumentElement(), "CachedGroup");
		cachedGroupEle.setAttribute("Name", "Database");
		Element cachedObjectsEle = SCXmlUtil.createChild(cachedGroupEle, "CachedObject");
		if(!YFCCommon.isVoid(firstMessage))
		{
			cachedObjectsEle.setAttribute("Enabled", "N");
		}
		if(!YFCCommon.isVoid(lastMessage))
		{
			cachedObjectsEle.setAttribute("Enabled", "Y");
		}

		cachedObjectsEle.setAttribute("Action", "MODIFY");
		cachedObjectsEle.setAttribute("Class", "com.yantra.shared.dbclasses.YFS_Region_DetailDBCacheHome");

		VSIUtils.invokeAPI(env, 
				"modifyCache",
				cacheGroupsDoc);

	}	
}
