	
package com.vsi.scc.oms.pca.extensions.alert;

/**
 * Created on Aug 21,2015
 *
 */
 
import java.util.ArrayList;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * @author Inspiron © Copyright IBM Corp. All Rights Reserved.
 */
public class VSIFilterAlertTypesExtnBehavior extends YRCExtentionBehavior {

	String FORM_ID = "com.yantra.pca.ycd.rcp.tasks.alert.screens.YCDCreateAlert";

	HashSet<String> hAlertTypeNoDisplay = new HashSet<String>();

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		
		Document docCommonCodeListInput =YRCXmlUtils.createFromString("<CommonCode />");
		Element eleCommonCodeListInput = docCommonCodeListInput.getDocumentElement();
		String strOrganizationCode="DEFAULT";
		eleCommonCodeListInput.setAttribute("CodeType","FILTERED_ALERTS");
		YRCApiContext context = new YRCApiContext();
		context.setApiName("getCommonCodeList");
		context.setFormId(FORM_ID);
		context.setInputXml(eleCommonCodeListInput.getOwnerDocument());
		callApi(context);

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

	public void handleApiCompletion(YRCApiContext ctx) {

		// TODO Auto-generated method stub
		if ("getCommonCodeList".equalsIgnoreCase(ctx.getApiName())) {
			Document outDoc = ctx.getOutputXml();
			// XmlUtils.docToXml(outDoc);
			this.setExtentionModel("extnAlertTypesToFilter", outDoc
					.getDocumentElement());
		}
		super.handleApiCompletion(ctx);
	}

	@Override
	public void postSetModel(String namespace) {

		if (namespace.equals("AlertTypeList")) {
			Element alertTypeListModel = null;
			NodeList alertNodeList = null;
			NodeList nlOutputCommonCode = null;
			Element alertNode = null;
			String strPayTypeToNotDisp = null;
			int size = 0;
			int i = 0;

			alertTypeListModel = this.getModel("AlertTypeList");
			alertNodeList = alertTypeListModel
					.getElementsByTagName("ExceptionType");
			if (!YRCPlatformUI.isVoid(alertTypeListModel)) {
				Element eleOutputCommonCode = this.getModel("extnAlertTypesToFilter");
				nlOutputCommonCode = eleOutputCommonCode
						.getElementsByTagName("CommonCode");
				size = nlOutputCommonCode.getLength();
				if (nlOutputCommonCode != null && size > 0) {
					for (i = 0; i < size; i++) {
						if (nlOutputCommonCode.item(i).getNodeType() == Node.ELEMENT_NODE) {
							strPayTypeToNotDisp = ((Element) nlOutputCommonCode
									.item(i)).getAttribute("CodeValue");
							hAlertTypeNoDisplay.add(strPayTypeToNotDisp);
						}
					}
				}
				size = alertNodeList.getLength();
				if (alertNodeList != null && size > 0) {
					for (i = 0; i < size; i++) {
						if (alertNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
							alertNode = (Element) alertNodeList.item(i);
							if (hAlertTypeNoDisplay.contains(alertNode
									.getAttribute("ExceptionType"))) {
								alertTypeListModel.removeChild(alertNode);
								i--;
								alertNodeList = alertTypeListModel
										.getElementsByTagName("ExceptionType");
								size = alertNodeList.getLength();
							}
						}
					}
				}
			}
			repopulateModel(namespace);
		}
		super.postSetModel(namespace);
	}

	@Override
	public void repopulateModel(String model) {
		// TODO Auto-generated method stub
		super.repopulateModel(model);
	}
}
