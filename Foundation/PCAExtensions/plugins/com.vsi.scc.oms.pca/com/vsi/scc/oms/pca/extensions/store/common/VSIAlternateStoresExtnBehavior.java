package com.vsi.scc.oms.pca.extensions.store.common;

/**
 * Created on Jan 10,2014
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIApiNames;
import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.extensions.orderEntry.VSIOrderEntryWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXPathUtils;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * @author admin © Copyright IBM Corp. All Rights Reserved.
 */
public class VSIAlternateStoresExtnBehavior extends YRCExtentionBehavior {

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
	
		// TODO: Write behavior init here.
	}

	public static final String Wizard_ID = "com.yantra.pca.ycd.rcp.tasks.common.screens.YCDAlternateStores";

	public static Element eleNodeList = null;

	

	@Override
	public boolean preCommand(YRCApiContext apiContext) {

		// get the Api Name.
		final String strApiName = apiContext.getApiName();

		// check if Api invoked is verifyAddress
		if (VSIApiNames.API_GET_SURROUNDING_NODE_LIST
				.equalsIgnoreCase(strApiName)) {

			Element eleInputToGetSurroundingNodeList = apiContext.getInputXml()
					.getDocumentElement();

			String enterprise = eleInputToGetSurroundingNodeList
					.getAttribute(VSIConstants.A_ORGANIZATION_CODE);

			if (enterprise.endsWith(VSIConstants.V_DOT_COM)) {
				enterprise = enterprise.substring(0, enterprise.length()
						- VSIConstants.V_DOT_COM.length());
			}

			eleInputToGetSurroundingNodeList.setAttribute(
					VSIConstants.A_ORGANIZATION_CODE, enterprise);
			eleInputToGetSurroundingNodeList.setAttribute("NodeType", "Store");
			eleInputToGetSurroundingNodeList.setAttribute("FulfillmentType", "CUTOVERSTORES");
			apiContext.setInputXml(eleInputToGetSurroundingNodeList
					.getOwnerDocument());
			// CallFindInventoryForDC()

		} else if ("findInventory".equalsIgnoreCase(strApiName)) {
			Document[] docInputToFindInventory = apiContext.getInputXmls();
			for (int i = 0; i < docInputToFindInventory.length; i++) {
				Element eleInputToFindInventory = docInputToFindInventory[i]
						.getDocumentElement();
				if (!YRCPlatformUI.isVoid(eleInputToFindInventory)) {
					eleInputToFindInventory.setAttribute("AllocationRuleID",
							"COM");
				}
			}
		}

		return super.preCommand(apiContext);
	}

	@Override
	public void postCommand(YRCApiContext arg0) {
		final String strApiName = arg0.getApiName();

		if ("findInventory".equalsIgnoreCase(strApiName)) {
			Document[] docOutputToFindInventory = arg0.getOutputXmls();
			Document[] docInputToFindInventory = arg0.getInputXmls();
			
			 HashMap<String, String> itemAvailabilityTemp = VSIOrderEntryWizardExtensionBehavior.itemAvailability ;
			
			for (int i = 0; i < docOutputToFindInventory.length; i++) {

				Element eleOutputToFindInventory = docOutputToFindInventory[i]
						.getDocumentElement();
				Element eleInputToFindInventory = docInputToFindInventory[i]
						.getDocumentElement();
				// NodeList ndlOutputPromiseLinelist =
				// eleOutputToFindInventory.getElementsByTagName("PromiseLine");
				NodeList ndlInputPromiseLinelist = eleInputToFindInventory
						.getElementsByTagName("PromiseLine");

				for (int iInputPromiseLinelist = 0; iInputPromiseLinelist < ndlInputPromiseLinelist
						.getLength(); iInputPromiseLinelist++) {

					Element eleInputPromiseLine = (Element) eleInputToFindInventory
							.getElementsByTagName("PromiseLine").item(
									iInputPromiseLinelist);

					String strItemID = eleInputPromiseLine
							.getAttribute("ItemID");
					String strLineID1 = eleInputPromiseLine
					.getAttribute("LineId");
					String xpath = "/Promise/SuggestedOption/Option/PromiseLines/PromiseLine[@LineId=\""
							+strLineID1+ "\"]"; 
					Element elePromiseLine = (Element) YRCXPathUtils.evaluate(
							eleOutputToFindInventory, xpath,
							XPathConstants.NODE);
					//Element elePromiseLine = null;
					NodeList ndlPromiseLines = eleOutputToFindInventory.getElementsByTagName("PromiseLine");
					for(int iLine = 0; iLine < ndlPromiseLines.getLength(); iLine++){
						Element elePromise = (Element)ndlPromiseLines.item(iLine);
						String strLineIDOut = elePromise.getAttribute("LineId");
						if(strLineIDOut.equalsIgnoreCase(strLineID1)){
							elePromiseLine = elePromise;
//							System.out.println(strItemID);
						}
					}
					

					String strIsDCInvAvail = itemAvailabilityTemp.get(strItemID);
					if(YRCPlatformUI.isVoid(strIsDCInvAvail))
						strIsDCInvAvail = "Y";

					int intReqdQty = 0;
					int intNodeQty = 0;
					double dblQtyAvailable = 0.0;
					double dblQtyUnAvailable = 0.0;

					if (!YRCPlatformUI.isVoid(elePromiseLine)) {
						NodeList ndlAssignment = elePromiseLine
								.getElementsByTagName("Assignment");
						for (int j = 0; j < ndlAssignment.getLength(); j++) {
							Element eleAssignment = (Element) ndlAssignment
									.item(j);
							if (!YRCPlatformUI.isVoid(eleAssignment)) {
								String strShipNode = eleAssignment
										.getAttribute("ShipNode");

								if (YRCPlatformUI.isVoid(strShipNode)) {
									dblQtyUnAvailable = dblQtyUnAvailable
											+ YRCXmlUtils.getDoubleAttribute(
													eleAssignment, "Quantity");
								} else {
									dblQtyAvailable = dblQtyAvailable
											+ YRCXmlUtils.getDoubleAttribute(
													eleAssignment, "Quantity");
								}
							}
						}
						intNodeQty = (int) dblQtyAvailable;
					}

					if (!YRCPlatformUI.isVoid(eleInputPromiseLine)) {

						intReqdQty = (int) YRCXmlUtils.getDoubleAttribute(
								eleInputPromiseLine, "RequiredQty");

					}

					if ((YRCPlatformUI.isVoid(elePromiseLine) || intNodeQty != intReqdQty)
							&& "Y".equalsIgnoreCase(strIsDCInvAvail)) {

						String strShipNode = "";
						String strRequiredQty = "";
						String strProductClass = "";

						String strUnitOfMeasure = "";
						String strLineID = "";

						if (!YRCPlatformUI.isVoid(eleInputPromiseLine)) {
							strShipNode = eleInputPromiseLine
									.getAttribute("ShipNode");
							strRequiredQty = eleInputPromiseLine
									.getAttribute("RequiredQty");
							strProductClass = eleInputPromiseLine
									.getAttribute("ProductClass");
							strUnitOfMeasure = eleInputPromiseLine
									.getAttribute("UnitOfMeasure");
							strItemID = eleInputPromiseLine
									.getAttribute("ItemID");
							strLineID = eleInputPromiseLine
									.getAttribute("LineId");
						}

						Element eleSuggestedOption = (Element) eleOutputToFindInventory
								.getElementsByTagName("SuggestedOption")
								.item(0);
						if (!YRCPlatformUI.isVoid(eleSuggestedOption)) {
							// if(!YRCPlatformUI.isVoid(eleOutputOption)){
							// eleSuggestedOption.removeChild(eleOutputOption);
							// }
							Element eleOption = YRCXmlUtils.getChildElement(
									eleSuggestedOption, "Option", true);
							Element elePromiseLines = YRCXmlUtils
									.getChildElement(eleOption, "PromiseLines",
											true);
							Element eleNewPromiseLine = YRCXmlUtils
									.createChild(elePromiseLines, "PromiseLine");
							Element eleAssignments = YRCXmlUtils.createChild(
									eleNewPromiseLine, "Assignments");
							Element eleAssignment = YRCXmlUtils.createChild(
									eleAssignments, "Assignment");

							Date dt = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd'T'HH:mm:ss'Z'");
							String strTodaysDate = sdf.format(dt);

							eleOption.setAttribute(
									"AvailableFromUnplannedInventory", "N");
							eleOption.setAttribute("FirstDate", strTodaysDate);
							eleOption.setAttribute("HasAnyUnavailableQty", "N");
							eleOption.setAttribute("LastDate", strTodaysDate);
							eleOption.setAttribute("LeastPriority", "0.00");
							eleOption.setAttribute("MaxPriority", "0.00");
							eleOption.setAttribute("NodeQty", strRequiredQty);
							eleOption.setAttribute("OptionNo", "1");
							eleOption.setAttribute("TotalShipments", "1");

							eleNewPromiseLine.setAttribute(
									"IsSubOnOrderAllowed", "N");
							eleNewPromiseLine.setAttribute("ItemID", strItemID);
							eleNewPromiseLine.setAttribute("LineId", strLineID);
							eleNewPromiseLine.setAttribute("ProductClass",
									strProductClass);
							eleNewPromiseLine.setAttribute("RequiredQty",
									strRequiredQty);
							eleNewPromiseLine.setAttribute("ShipNode",
									strShipNode);
							eleNewPromiseLine.setAttribute("UnitOfMeasure",
									strUnitOfMeasure);

							eleAssignment.setAttribute(
									"AvailableFromUnplannedInventory", "N");
							eleAssignment.setAttribute("ProductAvailDate",
									strTodaysDate);
							eleAssignment.setAttribute("Quantity",
									strRequiredQty);
							eleAssignment.setAttribute("ReservedQty", "0.00");
							eleAssignment.setAttribute("ShipDate",
									strTodaysDate);
							eleAssignment.setAttribute("ShipNode", strShipNode);

							eleNewPromiseLine.setAttribute("IsDCInv", "Y");
							if("296".equalsIgnoreCase(strShipNode))
							eleNewPromiseLine.setAttribute("NodeInv", String
									.valueOf(intNodeQty));
							eleNewPromiseLine.setAttribute("NodeInv", String
									.valueOf(intNodeQty));

							// eleAssignments.appendChild(eleAssignment);
							// eleNewPromiseLine.appendChild(eleAssignments);
							// elePromiseLines.appendChild(eleNewPromiseLine);
							// eleOption.appendChild(elePromiseLines);
							// eleSuggestedOption.appendChild(eleOption);

							// eleOption.appendChild(elePromiseLines);

						}
					} else {
						if(!YRCPlatformUI.isVoid(elePromiseLine)){
							elePromiseLine.setAttribute("IsDCInv", "N");
							elePromiseLine.setAttribute("NodeInv", String
									.valueOf(intNodeQty));
						}
					}

				}
			}
		}

		// TODO Auto-generated method stub
		super.postCommand(arg0);
	}

	/**
	 * Method for validating the text box.
	 */
	public YRCValidationResponse validateTextField(String fieldName,
			String fieldValue) {
		// TODO Validation required for the following controls.

		// TODO Create and return a response.
		return super.validateTextField(fieldName, fieldValue);
	}

	/**
	 * Method for validating the combo box entry.
	 */
	public void validateComboField(String fieldName, String fieldValue) {
		// TODO Validation required for the following controls.

		// TODO Create and return a response.
		super.validateComboField(fieldName, fieldValue);
	}

	/**
	 * Method called when a button is clicked.
	 */
	public YRCValidationResponse validateButtonClick(String fieldName) {
		// TODO Validation required for the following controls.

		// Control name: btnSearch

		// TODO Create and return a response.
		return super.validateButtonClick(fieldName);
	}

	/**
	 * Method called when a link is clicked.
	 */
	public YRCValidationResponse validateLinkClick(String fieldName) {
		// TODO Validation required for the following controls.

		// TODO Create and return a response.
		return super.validateLinkClick(fieldName);
	}

	/**
	 * Create and return the binding data for advanced table columns added to
	 * the tables.
	 */
	public YRCExtendedTableBindingData getExtendedTableBindingData(
			String tableName, ArrayList tableColumnNames) {
		// Create and return the binding data definition for the table.

		// The defualt super implementation does nothing.
		return super.getExtendedTableBindingData(tableName, tableColumnNames);
	}

	public static Element returnSurroundingNodeList() {

		Element eleSurroundingNodeList = eleNodeList;
		return eleSurroundingNodeList;
	}

	public void postSetModel(String arg0) {
		// TODO Auto-generated method stub
		if ("getSurroundingNodeList".equalsIgnoreCase(arg0)) {
			eleNodeList = getModel("getSurroundingNodeList");
		} else if ("Input".equalsIgnoreCase(arg0)) {
			Element eleModel = getModel(arg0);
			if (!YRCPlatformUI.isVoid(eleModel)) {
				
					Element eleBillTo = (Element)eleModel.getElementsByTagName("PersonInfoBillTo").item(0);
					if(!YRCPlatformUI.isVoid(eleBillTo) ){
						String	strZipCode = eleBillTo.getAttribute("ZipCode"); 
						int length = strZipCode.length();
						if(length > 5){
							strZipCode = strZipCode.substring(0,5);
							eleBillTo.setAttribute("ZipCode", strZipCode);
						}
						repopulateModel("Input");
					}
								
			}
		}

		super.postSetModel(arg0);
	}

	@Override
	public void handleApiCompletion(YRCApiContext ctxApi) {
		// TODO Auto-generated method stub
		

		super.handleApiCompletion(ctxApi);
	}
}
