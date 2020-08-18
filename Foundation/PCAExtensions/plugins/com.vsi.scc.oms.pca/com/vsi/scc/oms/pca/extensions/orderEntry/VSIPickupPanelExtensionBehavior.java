package com.vsi.scc.oms.pca.extensions.orderEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * Extension Behavior for the Store selection
 * 
 * @author skiran
 * 
 */
public class VSIPickupPanelExtensionBehavior extends YRCExtentionBehavior {

	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.common.fulfillmentSummary.screens.YCDPickupPanel";

	

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");

	public IYRCComposite createPage(String arg0) {
		return null;
	}

	public void pageBeingDisposed(String arg0) {

	}

	@Override
	public void init() {
	
		super.init(); 
	}

	
	@Override
	public boolean preCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub
		return super.preCommand(arg0);
	}

	@Override
	public YRCValidationResponse validateButtonClick(String arg0) {
		// TODO Auto-generated method stub
		return super.validateButtonClick(arg0);
	}

	@Override
	public YRCValidationResponse validateTextField(String arg0, String arg1) {
		// TODO Auto-generated method stub

		return super.validateTextField(arg0, arg1);
	}

	@Override
	public void postCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub
		super.postCommand(arg0);
	}
	
	@Override
	public void postSetModel(String arg0) {
		// TODO Auto-generated method stub

		if ("Order".equalsIgnoreCase(arg0)) {
			Element eleModel = getModel(arg0);
			if(!YRCPlatformUI.isVoid(eleModel)){ 
				Element eleOrderLines = (Element)eleModel.getElementsByTagName("OrderLines").item(0);
				if(!YRCPlatformUI.isVoid(eleOrderLines)){
					//NodeList ndlOrderLines = eleModel.getElementsByTagName("OrderLine");
					Iterator itrOrderLines=YRCXmlUtils.getChildren(eleOrderLines);
					Calendar cal = Calendar.getInstance();
					String strCurrentDate = sdf.format(cal.getTime());
					String strEnterpriseCode = eleModel
					.getAttribute("EnterpriseCode");
					while(itrOrderLines.hasNext()){
					//for (int i = 0; i < ndlOrderLines.getLength(); i++) {
						//Element eleOrderLine = (Element) ndlOrderLines.item(i);
						Element eleOrderLine=(Element)itrOrderLines.next();
						String strDeliveryMethod = eleOrderLine
						.getAttribute("DeliveryMethod");
						
						if ("PICK".equalsIgnoreCase(strDeliveryMethod)) {
							String strHasAnyUnavailableQty = eleOrderLine.getAttribute("HasAnyUnavailableQty");
							if("Y".equalsIgnoreCase(strHasAnyUnavailableQty)){
								double dblQtyAvailable = 0.0;
								double dblQtyUnAvailable = 0.0;
								Element elePromiseLine = (Element)eleOrderLine.getElementsByTagName("PromiseLine").item(0);
								if(!YRCPlatformUI.isVoid(elePromiseLine)){
									NodeList ndlAssignment = elePromiseLine.getElementsByTagName("Assignment");
									for(int j = 0; j<ndlAssignment.getLength(); j++){
										Element eleAssignment = (Element)ndlAssignment.item(j);
										if(!YRCPlatformUI.isVoid(eleAssignment)){
											String strShipNode = eleAssignment.getAttribute("ShipNode");

											if(YRCPlatformUI.isVoid(strShipNode)){
												dblQtyUnAvailable = dblQtyUnAvailable + YRCXmlUtils.getDoubleAttribute(eleAssignment, "Quantity");
											}else{
												dblQtyAvailable = dblQtyAvailable + YRCXmlUtils.getDoubleAttribute(eleAssignment, "Quantity");
											}
										}
									}
								}else{
									dblQtyUnAvailable = YRCXmlUtils.getDoubleAttribute(eleOrderLine, "OrderedQty");
								}

								if(dblQtyAvailable != 0){
									eleOrderLine.setAttribute("OrderedQty", String.valueOf(dblQtyAvailable));
									eleOrderLine.setAttribute("Quantity", String.valueOf(dblQtyAvailable));
									eleOrderLine.setAttribute("FulfillmentType", "PICK_IN_STORE");
									eleOrderLine.setAttribute("LineType", "PICK_IN_STORE");
									eleOrderLine.setAttribute("HasAnyUnavailableQty", "N");
									eleOrderLine.setAttribute("IsReturnable","Y");
									
									eleOrderLine.setAttribute("EarliestProductShipDate",
											strCurrentDate);
									eleOrderLine.setAttribute("ReqDeliveryDate",
											strCurrentDate);
									eleOrderLine.setAttribute("ReqShipDate",
											strCurrentDate);
									Element eleOrderLineTranQty = (Element)eleOrderLine.getElementsByTagName("OrderLineTranQuantity").item(0);
									if(!YRCPlatformUI.isVoid(eleOrderLineTranQty)){
										eleOrderLineTranQty.setAttribute("OrderedQty", String.valueOf(dblQtyAvailable));
									}

									if(dblQtyUnAvailable != 0){
										Element orderLineTemp = YRCXmlUtils.getCopy(eleOrderLine);
										//Element newOrderLineElem1 = eleOrderLines.getOwnerDocument().createElement("OrderLine");
										orderLineTemp.setAttribute("Action", "CREATE");
										orderLineTemp.setAttribute("OrderedQty", String.valueOf(dblQtyUnAvailable));
										orderLineTemp.setAttribute("FulfillmentType", "SHIP_TO_STORE");
										orderLineTemp.setAttribute("LineType", "SHIP_TO_STORE");
										orderLineTemp.setAttribute("ProcureFromNode","9001");
										orderLineTemp.setAttribute("OrderLineKey", "");
										orderLineTemp.setAttribute("Quantity", String.valueOf(dblQtyUnAvailable));
										eleOrderLine.setAttribute("HasAnyUnavailableQty", "N");
										Element eleOrderLineTempTranQty = (Element)orderLineTemp.getElementsByTagName("OrderLineTranQuantity").item(0);
										if(!YRCPlatformUI.isVoid(eleOrderLineTranQty)){
											eleOrderLineTempTranQty.setAttribute("OrderedQty", String.valueOf(dblQtyUnAvailable));
										}
										YRCXmlUtils.importElement(eleOrderLines, orderLineTemp);
										getStoreRespondByDate(strEnterpriseCode,
												eleOrderLine);
									}
								}else{

									eleOrderLine.setAttribute("FulfillmentType", "SHIP_TO_STORE");
									eleOrderLine.setAttribute("LineType", "SHIP_TO_STORE");
									eleOrderLine.setAttribute("ProcureFromNode","9001");
									eleOrderLine.setAttribute("HasAnyUnavailableQty", "N");
									eleOrderLine.setAttribute("IsReturnable","Y");
									
									
									getStoreRespondByDate(strEnterpriseCode,
											eleOrderLine);

								}
							}else{
								if(YRCPlatformUI.isVoid(eleOrderLine.getAttribute("FulfillmentType"))){
									eleOrderLine.setAttribute("FulfillmentType", "PICK_IN_STORE");
									eleOrderLine.setAttribute("LineType", "PICK_IN_STORE");
									
									eleOrderLine.setAttribute("EarliestProductShipDate",
											strCurrentDate);
									eleOrderLine.setAttribute("ReqDeliveryDate",
											strCurrentDate);
									eleOrderLine.setAttribute("ReqShipDate",
											strCurrentDate);
									//eleOrderLine.setAttribute("IsReturnable","Y");
								}
							}
						}
					}
				}


			}
			repopulateModel(arg0);
		}
		super.postSetModel(arg0);
	}

	private Element getStoreRespondByDate(String strEnterpriseCode,
			Element eleOrderLine) {

		Element eleShipNode = (Element) eleOrderLine.getElementsByTagName(
				"Shipnode").item(0);
		Calendar cal = Calendar.getInstance();
		if (!YRCPlatformUI.isVoid(eleShipNode)) {
			String strShipNode = "";
			strShipNode = eleShipNode.getAttribute("ShipNode");
			Document getStoreRespDtInDoc = YRCXmlUtils.createDocument("Organization");
			
			Element inEle = getStoreRespDtInDoc.getDocumentElement();
			//inEle.setAttribute("OrganizationCode",strEnterpriseCode); 
			
			inEle.setAttribute("OrganizationCode", "9001");
			
			inEle.setAttribute("CurrentDate", sdf.format(cal.getTime()));
			inEle.setAttribute("OrderLineKey", eleOrderLine
					.getAttribute("OrderLineKey"));
			YRCApiContext context = new YRCApiContext();
			context.setUserData("CodeName", "VSIPromisedDates");
			context.setApiName("VSIPromisedDates");
			context.setInputXml(getStoreRespDtInDoc);
			context.setFormId(Wizard_Id);
			callApi(context);
		} else {

			eleOrderLine.setAttribute("EarliestProductShipDate", currentDate
					.format(cal.getTime()));
			eleOrderLine.setAttribute("ReqDeliveryDate", currentDate
					.format(cal.getTime()));
			eleOrderLine.setAttribute("ReqShipDate", currentDate
					.format(cal.getTime()));

		}
		return eleOrderLine;
	}

	
	public void handleApiCompletion(YRCApiContext ctxApi) {
		try {
			if (ctxApi.getInvokeAPIStatus() < 0) {
				YRCPlatformUI.showError("API call Failed", "API call Failed");
			} else if ("VSIPromisedDates".equals((String) ctxApi
					.getUserData("CodeName"))) {

				Element eleOutput = ctxApi.getOutputXml().getDocumentElement();
				String strOrderLineKey = eleOutput.getAttribute("OrderLineKey");
				String strStoreRespondByDt = eleOutput
						.getAttribute("PromisedDate");
				Calendar cal = Calendar.getInstance();
				String strCurrentDate = sdf.format(cal.getTime());
							
				Element eleModel = getModel("Order");
				// eleModel.setAttribute("StoreResponseDate",
				// strStoreRespondByDt);
				NodeList ndlOrderLines = eleModel
						.getElementsByTagName("OrderLine");
				for (int i = 0; i < ndlOrderLines.getLength(); i++) {
					Element eleOrderLine = (Element) ndlOrderLines.item(i);
					String strstrOrderLineKeyLine = eleOrderLine
							.getAttribute("OrderLineKey");
					String strAction = eleOrderLine.getAttribute("Action");
					String strLineType = eleOrderLine.getAttribute("LineType");
					if ((strstrOrderLineKeyLine
							.equalsIgnoreCase(strOrderLineKey) || "CREATE".equalsIgnoreCase(strAction)) && "SHIP_TO_STORE".equalsIgnoreCase(strLineType)) {

						eleOrderLine.setAttribute("EarliestProductShipDate",
								strStoreRespondByDt);
						eleOrderLine.setAttribute("ReqDeliveryDate",
								strStoreRespondByDt);
//						eleOrderLine.setAttribute("ReqShipDate",
//								strStoreRespondByDt);				
						
						
					}
					else{
//						eleOrderLine.setAttribute("EarliestProductShipDate",
//								strCurrentDate);
//						eleOrderLine.setAttribute("ReqDeliveryDate",
//								strCurrentDate);
//						eleOrderLine.setAttribute("ReqShipDate",
//								strCurrentDate);
					}
				}
				// SetModel
				repopulateModel("Order");
				// YRCXmlUtils.getD

			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.handleApiCompletion(ctxApi);
	}

}