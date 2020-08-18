package com.vsi.scc.oms.pca.extensions.orderEntry;

import java.text.SimpleDateFormat;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.extensions.store.common.VSIAlternateStoresExtnBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtendedCellModifier;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * Extension Behavior for the change fulfillment options
 * 
 * @author skiran
 * 
 */
public class VSIChangeFulfillmentOptionsExtensionBehavior extends YRCExtentionBehavior {

	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.common.deliveryOptions.screens.YCDChangeDeliveryOptionsPageComposite";

	private YRCExtendedCellModifier tblCellModifier1 = null;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


	public IYRCComposite createPage(String arg0) {
		return null;
	}

	public void pageBeingDisposed(String arg0) {

	}

	@Override
	public void init() {
		setFocus("radPickup");
		super.init();
	}

	
	@Override
	public boolean preCommand(YRCApiContext apiContext) {
		// TODO Auto-generated method stub
//		if((apiContext.getApiName()).equals("getCustomerDetails") || apiContext.getApiName().equals("VSICustomerLookup")){
//			Document[] docInputgetCustomerDetails = apiContext.getInputXmls();
//			for (int i = 0; i < docInputgetCustomerDetails.length; i++) { 
//
//				Element eleInput = docInputgetCustomerDetails[i]
//				                                              .getDocumentElement();
//				//Element eleInput = apiContext.getInputXml().getDocumentElement();
//				String strCustomerID = eleInput.getAttribute("CustomerID");
//				if(!YRCPlatformUI.isVoid(strCustomerID)){
//					Element eleCustomerDetails = VSIOrderConsumerSearchExtensionBehavior.returnCustomerDetails();
//
//					if(!YRCPlatformUI.isVoid(eleCustomerDetails)){
//						eleCustomerDetails.setAttribute("CustomerKey", strCustomerID);
//						String strCustomer = YRCXmlUtils.getString(eleCustomerDetails);
//
//						Document inDoc = YRCXmlUtils.createFromString(strCustomer);
//						apiContext.setInputXml(inDoc);
//						apiContext.setApiName("VSICustomerLookup");
//					}
//				}
//			}
//		}
		if(("changeOrder").equalsIgnoreCase(apiContext.getApiName())){
			
			
			Element changeOrderInp=apiContext.getInputXml().getDocumentElement();
			
			NodeList nlist=changeOrderInp.getElementsByTagName("OrderLine");
			for(int a=0;a<nlist.getLength();a++){
				Element orderLineELe=(Element) nlist.item(a);


				String strDeliveryMethod = orderLineELe.getAttribute("DeliveryMethod");
				if("PICK".equalsIgnoreCase(strDeliveryMethod)){
					String strShipNode = orderLineELe.getAttribute("ShipNode");
					if(!YRCPlatformUI.isVoid(strShipNode)){
						changeOrderInp.setAttribute("ShipNode", strShipNode);
					}
					changeOrderInp.setAttribute("AllocationRuleID", "COM");
					Element eleSurroundingNodeList = VSIAlternateStoresExtnBehavior.returnSurroundingNodeList();
					if(!YRCPlatformUI.isVoid(eleSurroundingNodeList)){						
						if(!YRCPlatformUI.isVoid(strShipNode)){							
							NodeList ndlNodeList = eleSurroundingNodeList.getElementsByTagName("Node");
							for(int iNode = 0; iNode < ndlNodeList.getLength();iNode++){
								Element eleNode = (Element)ndlNodeList.item(iNode);
								String strSurroundingNode = eleNode.getAttribute("ShipNode");
								if(strShipNode.equalsIgnoreCase(strSurroundingNode)){
									Element eleShipNodeInfo = (Element)eleNode.getElementsByTagName("ShipNodePersonInfo").item(0);
									if(!YRCPlatformUI.isVoid(eleShipNodeInfo)){

										Element eleAdditionalAddress = YRCXmlUtils.createChild(orderLineELe, "PersonInfoShipTo");


										NamedNodeMap personInfoElemAttrs = eleShipNodeInfo.getAttributes();
										for(int h = 0 ; h<personInfoElemAttrs.getLength();h++) 
										{
											Attr a1 = (Attr)personInfoElemAttrs.item(h);
											eleAdditionalAddress.setAttribute(a1.getName(), a1.getValue());
										}	
									}
									break;
								}
							}
						}
					}
				}

			}
			
			apiContext.setInputXml(changeOrderInp.getOwnerDocument());
		}
		return super.preCommand(apiContext);
	}

	@Override
	public YRCValidationResponse validateButtonClick(String arg0) {
		// TODO Auto-generated method stub
		if("chkCheckAll".equalsIgnoreCase(arg0)){
			//setFieldValue("radPickup", true);
			setFocus("radPickup");
		}
		return super.validateButtonClick(arg0);
	}

	@Override
	public YRCValidationResponse validateTextField(String arg0, String arg1) {
		// TODO Auto-generated method stub

		return super.validateTextField(arg0, arg1);
	}

	@Override
	public void postCommand(YRCApiContext apiContext) {
		// TODO Auto-generated method stub
		super.postCommand(apiContext);
	}
	
	@Override
	public void postSetModel(String arg0) {
		// TODO Auto-generated method stub

		
		super.postSetModel(arg0);
	}



	
	public void handleApiCompletion(YRCApiContext ctxApi) {
		try {
			if (ctxApi.getInvokeAPIStatus() < 0) {
				YRCPlatformUI.showError("API call Failed", "API call Failed");
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.handleApiCompletion(ctxApi);
	}
	
	public Element getOrderDetails() {
		
		Element eleOrderModel = getModel("OrderLines");
		if(YRCPlatformUI.isVoid(eleOrderModel)){
			eleOrderModel = getModel("OrginalOrder");
		}		
		eleOrderModel = getModel("getSalesOrderDetails");
		return eleOrderModel;
	}

}