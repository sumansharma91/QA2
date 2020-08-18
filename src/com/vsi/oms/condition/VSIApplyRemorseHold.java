package com.vsi.oms.condition;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIApplyRemorseHold implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSIApplyRemorseHold.class);
	@Override
	/**
	 * Condition to apply VSIRemorseHold
	 */
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		
		boolean applyRemorseHold = false;
		Document docgetCommonCodeOutput = null;
		int iCodeShortDescription = 0;
		int iCodeLongDescription = 0;
		
		Element eleOrder = inXML.getDocumentElement();
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIApplyRemorseHold================================");
		}
		if(!YFCObject.isNull(eleOrder)){
			String sOrderType = eleOrder.getAttribute("OrderType");
		    Element eleExtn = SCXmlUtil.getChildElement(eleOrder,"Extn");
			String extSub=null;
			if(!YFCObject.isNull(eleExtn)){
			extSub = eleExtn.getAttribute("ExtnSubscriptionOrder");//subscricption order should not have remorse hol  Jira OMS-877
			}
	 		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
	 		if(!YFCObject.isNull(eleOrderLines)){
		 		Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
		 		if(!YFCObject.isNull(eleOrderLine)){
			 		String sDeliveryMethod = eleOrderLine.getAttribute("DeliveryMethod");//Delivery Method of first OrderLine
			 		Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
					String sCountry = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_COUNTRY);
					
					//update the condition for ignoring subscription orders from remorse hold  JIRA-OMS-870
					if("SHP".equalsIgnoreCase(sDeliveryMethod) && "WEB".equalsIgnoreCase(sOrderType) && "US".equalsIgnoreCase(sCountry) && !("Y".equalsIgnoreCase(extSub))  ){
						
						String sOrderDate = eleOrder.getAttribute("OrderDate");
						// changing the OrderDate to 24H Format
						 SimpleDateFormat sdf_tz = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						 Date date = null;
							try {
								date = sdf_tz.parse(sOrderDate);
								
							} catch (ParseException pe) {
								
							}
							Calendar cal1 = Calendar.getInstance();
							cal1.setTime(date);
							int hour = cal1.get(Calendar.HOUR_OF_DAY);
							
							
							//creating input to getCommonCodeList API with CodeType = VSI_NO_REMORSE
							Document docgetCommonCodeInput = SCXmlUtil.createDocument("CommonCode");
					    	Element eleCommonCodeElement = docgetCommonCodeInput.getDocumentElement();
					    	eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_NO_REMORSE");
					    	
					    	try {
								docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
								
								Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
					    		if(commonCodeListElement.hasChildNodes()){
					    			if(log.isDebugEnabled()){
					    				log.debug("CommonCodeList has Child Nodes" +SCXmlUtil.getString(commonCodeListElement));
					    			}
				    			
					    			ArrayList<Element> alCommonCode = SCXmlUtil.getChildren(commonCodeListElement, VSIConstants.ELEMENT_COMMON_CODE);
					    			//Looping through commonCode elements
					    			for(Element eleCommonCode:alCommonCode){
					    				//CodeShortDescription CodeLongDescription Integer.parseInt()
					    				String sCodeShortDescription = eleCommonCode.getAttribute("CodeShortDescription");
					    				iCodeShortDescription = Integer.parseInt(sCodeShortDescription);
					    				
					    				String sCodeLongDescription = eleCommonCode.getAttribute("CodeLongDescription");
					    				iCodeLongDescription = Integer.parseInt(sCodeLongDescription);
					    				
					    				if((hour < iCodeShortDescription) || (hour >= iCodeLongDescription )){
					    					
					    					applyRemorseHold = true;
					    					break;
					    				}//end of inner if
					    			}//end of for
					    		}// end of if
					    		if(log.isDebugEnabled()){
					    			log.info("================End of VSIApplyRemorseHold Class================================");
					    		}
					    		
							} catch (YFSException | RemoteException | YIFClientCreationException e) {
								e.printStackTrace();
								throw new YFSException();
								//throw new RemoteException();
								//throw new YIFClientCreationException(sOrderDate);
								
								
							}
			
						
					}// end of if
		 		}
	 		}
		}	
		return applyRemorseHold;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
