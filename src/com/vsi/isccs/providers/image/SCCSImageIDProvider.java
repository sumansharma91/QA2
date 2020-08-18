package com.vsi.isccs.providers.image;

/*This class is invoked for every occurrence of xpaths mentioned in
SCCSDataProviderExtn.xml. It then appends ImageID and Image Location
by reading the item id from Item id attribute of interestedIn element
and suffix and location by reading from yfs.properties file.
*/

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.platform.handlers.ISCUIAdditionalDataProvider;
import com.vsi.oms.utils.GcoXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCConfigurator;

public class SCCSImageIDProvider
  implements ISCUIAdditionalDataProvider,VSIConstants
{
  private static YFCLogCategory logger = YFCLogCategory.instance(SCCSImageIDProvider.class);

  public void addAdditionalData(SCUIContext arg0, Element arg1, Element arg2, Element interestedInElement) {
	try {
		logger.beginTimer("SCCSImageIDProvider.addAdditionalData:interestedInElement is (incoming): "+GcoXmlUtil.getElementString(interestedInElement));
	    
		String strImageIDSuffix= null;
		String strImageLocation= null;
		
		//Read image location and image id from yfs.properties
		strImageLocation = YFCConfigurator.getInstance().getProperty(VSIConstants.IMAGE_LOCATION);
		strImageIDSuffix = YFCConfigurator.getInstance().getProperty(VSIConstants.IMAGE_SUFFIX);
		
		String strImageID = interestedInElement.getAttribute(VSIConstants.ATTR_ITEM_ID).concat(strImageIDSuffix);
		String strNodeName = interestedInElement.getNodeName();
		if(logger.isDebugEnabled()){
			logger.debug("strImageIDSuffix"+strImageIDSuffix);
			logger.debug("strImageLocation"+strImageLocation);
		}
		//Set image id and image location if interestedInElement is not null
		if(!YFCCommon.isVoid(interestedInElement) && (strNodeName.equals(ELE_ITEM) || strNodeName.equals(VSIConstants.ELE_ITEM_DETAILS))){
			Element elePrimaryInformation = GcoXmlUtil.getFirstElementByName(interestedInElement, VSIConstants.ELE_PRIMARY_INFORMATION);	
			if(!YFCCommon.isVoid(elePrimaryInformation)&&!YFCCommon.isVoid(strImageID)&&!YFCCommon.isVoid(strImageLocation)){
				if(logger.isDebugEnabled()){
					logger.debug("Inside if node is Item or ItemDetails and has PrimaryInformation. URL: "+strImageLocation+strImageID);
				}
				elePrimaryInformation.setAttribute(VSIConstants.ATTR_IMAGE_ID,strImageID);
				elePrimaryInformation.setAttribute(VSIConstants.ATTR_IMAGE_LOCATION, strImageLocation);
				
				// SUH-18 : Added to display items when viewing from the product browsing screen : BEGIN
				Element eleAssetList = SCXmlUtil.getChildElement(interestedInElement, "AssetList", true);
				Element eleAsset = GcoXmlUtil.createChild(eleAssetList, "Asset");
				eleAsset.setAttribute("Type", "ITEM_IMAGE_1");
				eleAsset.setAttribute("ContentLocation", strImageLocation);
				eleAsset.setAttribute("ContentID", strImageID);
				// SUH-18 : Added to display items when viewing from the product browsing screen : END
			}
		}
		// SUH-18 : Added to display items when going from fulfillment summary screen to add items screen : BEGIN
		else if (!YFCCommon.isVoid(interestedInElement) && strNodeName.equals(ELE_ORDER_LINE)) {
			Element eleItemDetails = SCXmlUtil.getChildElement(interestedInElement, ELE_ITEM_DETAILS);
			Element elePrimaryInformation = SCXmlUtil.getChildElement(eleItemDetails, ELE_PRIMARY_INFORMATION);
			strImageID = eleItemDetails.getAttribute(VSIConstants.ATTR_ITEM_ID).concat(strImageIDSuffix);
			if(!YFCCommon.isVoid(elePrimaryInformation)&&!YFCCommon.isVoid(strImageID)&&!YFCCommon.isVoid(strImageLocation)){
				if(logger.isDebugEnabled()){
					logger.debug("InterestedInElement root node is OrderLine. URL: "+strImageLocation+strImageID);
				}
				elePrimaryInformation.setAttribute(VSIConstants.ATTR_IMAGE_ID,strImageID);
				elePrimaryInformation.setAttribute(VSIConstants.ATTR_IMAGE_LOCATION, strImageLocation);
			}
		}
		// SUH-18 : Added to display items when going from fulfillment summary screen to add items screen : END
		
		logger.endTimer("SCCSImageIDProvider.addAdditionalData:interestedInElement is (outgoing): "+GcoXmlUtil.getElementString(interestedInElement));
    } catch (IllegalArgumentException e) {
		logger.error(e.getMessage());
		throw e;
		
	} catch (ParserConfigurationException e) {
		logger.error(e.getMessage());
	}
    }
    
}