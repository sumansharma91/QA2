package com.vsi.oms.api;

import org.w3c.dom.Document;

import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import java.rmi.RemoteException;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSUserExitException;

public class VSILoadCountyData {
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSILoadCountyData.class);
	
	//Create or Modify County uploads through csv file
	public void  loadCountyData (YFSEnvironment yfsEnv, Document docInXML) 
			throws  YFSUserExitException, YFSException, RemoteException, YIFClientCreationException{
		if(log.isDebugEnabled()){
			log.info("*** Inside Method loadCountyData");
			log.debug("***Updated UE Docuement"+SCXmlUtil.getString(docInXML));
		}
		//remove county attribute for getCountyDataList api
		Element eleVSICountyData= docInXML.getDocumentElement();
		String attrCounty = eleVSICountyData.getAttribute(VSIConstants.ATTR_EXTN_COUNTY);
		XMLUtil.removeAttribute(eleVSICountyData,VSIConstants.ATTR_EXTN_COUNTY);

		try{
			//Check County existence
			Document getVSICountList=VSIUtils.invokeService(yfsEnv,VSIConstants.SERVICE_VSI_GET_COUNTY_DATA,docInXML); 
			Boolean hasChildUser = getVSICountList.getDocumentElement().hasChildNodes();
			eleVSICountyData.setAttribute(VSIConstants.ATTR_EXTN_COUNTY,attrCounty );
			if(hasChildUser==true){
				if(log.isDebugEnabled()){
					log.debug("***Updated UE Docuement"+SCXmlUtil.getString(getVSICountList));
				}
			eleVSICountyData.setAttribute(VSIConstants.ATTR_EXTN_CITY_COUNTY_KEY, SCXmlUtil.getChildElement(getVSICountList.getDocumentElement(),VSIConstants.ELE_VSI_COUNTY_DATA).getAttribute(VSIConstants.ATTR_EXTN_CITY_COUNTY_KEY));
			//Element VSICountyData  null check
			if(log.isDebugEnabled()){	
				log.info("***County Exist: Modifying Existing County");
			}
					VSIUtils.invokeService(yfsEnv,VSIConstants.SERVICE_VSI_MODIFY_COUNTY_DATA,docInXML); 
			}
			else{
				if(log.isDebugEnabled()){
					log.info("***County Does not Exist: Creating new County");
				}
				VSIUtils.invokeService(yfsEnv,VSIConstants.SERVICE_VSI_CREATE_COUNTY_DATA,docInXML);
			}
		}
		 catch (RemoteException e) {
				e.printStackTrace();
				throw new YFSException(
						"EXTN001",
						"County Upload Failure",
						e.getMessage());
		}
		 catch (YIFClientCreationException e) {
				e.printStackTrace();
				throw new YFSException(
						"EXTN001",
						"County Upload Failure",
						e.getMessage());
		}
		 catch (YFSException e) {
				e.printStackTrace();
				throw new YFSException();
		}
		 catch (Exception e) {
				e.printStackTrace();
				throw new YFSException();
		}

		
		
		
		
	
		
	}
}
