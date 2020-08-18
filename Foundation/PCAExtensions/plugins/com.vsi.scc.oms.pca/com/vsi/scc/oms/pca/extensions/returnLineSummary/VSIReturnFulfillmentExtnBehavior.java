package com.vsi.scc.oms.pca.extensions.returnLineSummary;

/**
 * Created on Jul 29,2014
 *
 */

import java.util.ArrayList;
import com.vsi.scc.oms.pca.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIApiNames;
import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.util.VSIPcaUtils;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * @author Admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
public class VSIReturnFulfillmentExtnBehavior extends YRCExtentionBehavior {

	/**
	 * This method initializes the behavior class.
	 */
	boolean CCKFLAG = true;

	public void init() {
		//TODO: Write behavior init here.
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
		if(!CCKFLAG){
			YRCPlatformUI.showError("Error","Customer Cannot Keep the items!");
            return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Customer Cannot Keep the items!");
		}else{
		// TODO Create and return a response.
		return super.validateButtonClick(fieldName);}
	}

	public void postSetModel(String namespace) {
		// TODO Validation required for the following controls.
		if (namespace.equalsIgnoreCase("panelOrderLines")) {
			Element eleOrderLines = getModel(namespace);
			if (!YRCPlatformUI.isVoid(eleOrderLines)) {
				NodeList nlOrderLine = eleOrderLines
						.getElementsByTagName("OrderLine");
				double totalROLineAmount = 0;
				double unitPrice = 0;
				double orderedQty = 0;
				double lineTotal = 0;
				double rCCKThreshold = 0;

				if (nlOrderLine.getLength() > 0) {

					for (int i = 0; i < nlOrderLine.getLength(); i++) {
						Element eleOrderLine = (Element) nlOrderLine.item(i);

						orderedQty = Double.parseDouble(eleOrderLine
								.getAttribute("OrderedQty"));
						Element eleLinePriceInfo = (Element) eleOrderLine
								.getElementsByTagName("LinePriceInfo").item(0);
						unitPrice = Double.parseDouble(eleLinePriceInfo
								.getAttribute("UnitPrice"));
						lineTotal = orderedQty * unitPrice;
						totalROLineAmount = totalROLineAmount + lineTotal;
					}
					rCCKThreshold = getCCKThresholdFromCommonCode();
					if (totalROLineAmount > rCCKThreshold) {
						super.disableField("bttnChangeToCreditWithoutReciept");
						super.disableField("bttnCheckAll");
						//Fix for OMS - 230
						//super.disableField("tblClmnSelection");
						super.setColumnVisible("tblOrderLines", "tblClmnSelection", false);
						CCKFLAG = false;
					}

				}

			}

		}
		super.postSetModel(namespace);
	}

	private double getCCKThresholdFromCommonCode() {

		double rCCKThresholdOrderTotal = 0;

		Element eleCommonCode = YRCXmlUtils.createDocument("CommonCode")
				.getDocumentElement();
		eleCommonCode.setAttribute("OrganizationCode", "VSI");
		eleCommonCode.setAttribute("CodeType", "CCK_MIN_ORDER_TOTAL");
		Document output = VSIPcaUtils.invokeApi(
				VSIApiNames.API_GET_COMMON_CODE_LIST, eleCommonCode
						.getOwnerDocument(), getFormId());
		if (!YRCPlatformUI.isVoid(output)) {
			Element eleOPCommonCode = (Element) output.getElementsByTagName(
					"CommonCode").item(0);
			if (eleOPCommonCode.getAttribute("CodeValue") != null) {
				rCCKThresholdOrderTotal = Double.parseDouble(eleOPCommonCode
						.getAttribute("CodeValue"));
			}
		}
		//rCCKThresholdOrderTotal = 10; literal added to test the flow since invoking the API is not working
		return rCCKThresholdOrderTotal;

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
	 * Create and return the binding data for advanced table columns added to the tables.
	 */
	public YRCExtendedTableBindingData getExtendedTableBindingData(
			String tableName, ArrayList tableColumnNames) {
		// Create and return the binding data definition for the table.

		// The defualt super implementation does nothing.
		return super.getExtendedTableBindingData(tableName, tableColumnNames);
	}
}
//TODO Validation required for a Button control: bttnChangeToCreditWithoutReciept