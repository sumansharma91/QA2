package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIGetCancelReasonCode {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetCancelReasonCode.class);

	public Document getCancelReasonCode(YFSEnvironment env, Document inDoc) {

		Document multiAPiOutputDoc = null;
		try {
			YFCDocument docIn = YFCDocument.getDocumentFor(inDoc);
			if(log.isDebugEnabled()){
				log
				.info("Inside VSIGetCancelReasonCode : getCancelReasonCode method : Input XML is: "
						+ docIn);
			}
			YFCElement root = docIn.getDocumentElement();
			String orderLineKey = null;
			YFCNodeList<YFCElement> orderLines = root
					.getElementsByTagName("OrderLine");
			YFCDocument multiAPiInput = YFCDocument.createDocument("MultiApi");
			YFCElement multiApiRoot = multiAPiInput.getDocumentElement();
			for (YFCElement orderLine : orderLines) {
				orderLineKey = orderLine.getAttribute("OrderLineKey");
				YFCElement api = multiApiRoot.createChild("API");
				api.setAttribute("Name", "getOrderAuditList");
				YFCElement input = api.createChild("Input");
				YFCElement auditInput = input.createChild("OrderAudit");
				auditInput.setAttribute("OrderLineKey", orderLineKey);
			}

			if(log.isDebugEnabled()){
				log
				.info("Inside VSIGetCancelReasonCode : getCancelReasonCode method : Input to multiApi: "
						+ multiAPiInput);
			}
			
			multiAPiOutputDoc = VSIUtils.invokeAPI(env, "multiApi",
					multiAPiInput.getDocument());

			if(log.isDebugEnabled()){
				log
				.info("Inside VSIGetCancelReasonCode : getCancelReasonCode method : Output of multiApi: "
						+ YFCDocument.getDocumentFor(multiAPiOutputDoc));
			}
			
			Document getCommonCodeOutput = getCommonCodeList(env);

			XPathFactory xpathfactory = XPathFactory.newInstance();
			XPath xpath = xpathfactory.newXPath();
			XPathExpression multiApiExp = xpath
					.compile("/MultiApi/API/Output/OrderAuditList/OrderAudit/OrderAuditLevels/OrderAuditLevel[@ModificationLevel = 'ORDER_LINE']/ModificationTypes/ModificationType[@Name='CANCEL']");
			Object result = multiApiExp.evaluate(multiAPiOutputDoc,
					XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			XPathExpression commonCodeExp = null;
			Object evaluateResult = null;
			NodeList nodeList = null;
			String reasonCode = null;
			String codeValueExp = null;
			int size = 0;
			String nodeName = null;
			int length = nodes.getLength();
			for (int i = 0; i < length; i++) {
				Element node = (Element) nodes.item(i);
				nodeName = node.getNodeName();
				while (!nodeName.equalsIgnoreCase("OrderAudit")) {
					if (nodeName.equalsIgnoreCase("OrderAuditLevel")) {
						orderLineKey = node.getAttribute("OrderLineKey");
					}
					node = (Element) node.getParentNode();
					nodeName = node.getNodeName();

				}
				reasonCode = node.getAttribute("ReasonCode");
				codeValueExp = "/CommonCodeList/CommonCode[@CodeValue='"
						+ reasonCode + "']";

				if(log.isDebugEnabled()){
					log
					.info("getCommonCode CodeValue Expression : "
							+ codeValueExp);
				}
				commonCodeExp = xpath.compile(codeValueExp);
				evaluateResult = commonCodeExp.evaluate(getCommonCodeOutput,
						XPathConstants.NODESET);
				nodeList = (NodeList) evaluateResult;
				size = nodeList.getLength();
				if (size > 0) {
					for (int j = 0; j < size; j++) {
						Element element = (Element) nodeList.item(j);
						String reasonDesc = element
								.getAttribute("CodeLongDescription");
						if(log.isDebugEnabled()){
							log.info("Code Long Description is : " + reasonDesc);
						}
						if (null == reasonDesc
								|| "".equalsIgnoreCase(reasonDesc)) {
							if(log.isDebugEnabled()){
								log.info("Code Long Description is empty or null." );
							}
							reasonDesc = reasonCode;
						}
						if(log.isDebugEnabled()){
							log.info("Code Long Description is : " + reasonDesc);
						}
						addCancelReasonCode(inDoc, orderLineKey, xpath,
								reasonDesc);
					}
				} else {
					if(log.isDebugEnabled()){
						log.info("The reason code list length is zero. The Cancel Reason Code is : " + reasonCode);
					}
					addCancelReasonCode(inDoc, orderLineKey, xpath,
							reasonCode);
				}
			}
			
			if(log.isDebugEnabled()){
				log
				.info("Inside VSIGetCancelReasonCode : getCancelReasonCode method : Output document: "
						+ inDoc);
			}
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		}

		return inDoc;
	}

	private void addCancelReasonCode(Document inDoc, String orderLineKey,
			XPath xpath, String reasonDesc) throws XPathExpressionException {
		String orderLinesExp;
		XPathExpression orderLineExp;
		Object orderLineResult;
		orderLinesExp = "/OrderLineStatusList/OrderStatus/OrderLine[@OrderLineKey='"
				+ orderLineKey + "']";
		if(log.isDebugEnabled()){
			log.info("OrderLineKey Expression : " + orderLinesExp);
		}
		orderLineExp = xpath.compile(orderLinesExp);
		orderLineResult = orderLineExp.evaluate(inDoc,
				XPathConstants.NODESET);
		Element orderLine = (Element) ((NodeList) orderLineResult)
				.item(0);
		orderLine.setAttribute("CancelReasonDesc", reasonDesc);
	}

	private Document getCommonCodeList(YFSEnvironment env) {

		if(log.isDebugEnabled()){
			log
			.info("Inside VSIGetCancelReasonCode : getCommonCodeList method : Start ");
		}
		YFCDocument getCommonCodeInput = YFCDocument
				.createDocument("CommonCode");
		YFCElement commonCodeInputRoot = getCommonCodeInput
				.getDocumentElement();
		commonCodeInputRoot.setAttribute("CodeType", "RETURN_CODE_DESC");
		commonCodeInputRoot.setAttribute("OrganizationCode", "DEFAULT");
		if(log.isDebugEnabled()){
			log
			.info("Inside VSIGetCancelReasonCode : getCancelReasonCode method : Input to getCommonCodeList: "
					+ getCommonCodeInput);
		}
		Document getCommonCodeOutput = null;
		try {
			getCommonCodeOutput = VSIUtils.invokeAPI(env, "getCommonCodeList",
					getCommonCodeInput.getDocument());
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		}
		if(log.isDebugEnabled()){
			log
			.info("Inside VSIGetCancelReasonCode : getCommonCodeList method : Output of getCommonCodeList: "
					+ YFCDocument.getDocumentFor(getCommonCodeOutput));
			log
			.info("Inside VSIGetCancelReasonCode : getCommonCodeList method : End ");
		}
		return getCommonCodeOutput;
	}
}
