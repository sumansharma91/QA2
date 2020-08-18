package com.vsi.oms.agent;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.ibm.icu.util.Calendar;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VSICancelDraftOrder extends YCPBaseAgent {

	private static YFCLogCategory log = YFCLogCategory
			.instance(VSICancelDraftOrder.class.getName());

	/*
	 * Cancel draft order
	 */
	public final List<Document> getJobs(final YFSEnvironment env,
			final Document inDoc, final Document lastMsg) throws Exception {

		if(log.isDebugEnabled()){
			log.debug("getJobs method :- Begins");
		}
		/** **Prepare the input XML for getOrderList API.************* */
		List<Document> OrderList = new ArrayList<Document>();
		
		if(null == lastMsg){

			Document getOrderListInDoc = XMLUtil.createDocument("Order");
			Element elegetOrderListInDoc = getOrderListInDoc.getDocumentElement();
			elegetOrderListInDoc.setAttribute("Status", "1000");
			elegetOrderListInDoc.setAttribute("DraftOrderFlag", "Y");
			/** ****Common code for finding retention days********** */
			String RetentionDays = "DraftOrderRetention";
			String sRetentionDays = getCommonCodeValue(env, RetentionDays, "", "",
					true);

			if (!YFCObject.isVoid(sRetentionDays)) {
				int nRetentionDays = Integer.parseInt(sRetentionDays);

				/** **************************************************** */

				/** *********Fetching two days back date from current date********* */
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				if(log.isDebugEnabled()){
					log.debug(dateFormat.format(date)); // 20161116
				}
				String currentdate = dateFormat.format(date);
				if(log.isDebugEnabled()){
					log.debug("currentdates" + currentdate);
					
				}
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -nRetentionDays);
				/** **Retention days************ */
				Date Twodaysbackdate = cal.getTime();
				String sRequireddate = dateFormat.format(Twodaysbackdate);
				//System.out.println("RequiredDate" + sRequireddate);

				/** ******************complex query****************8************ */
				elegetOrderListInDoc.setAttribute("OrderDateQryType", "LT");
				elegetOrderListInDoc.setAttribute("OrderDate", sRequireddate);

				/** ************complex query********************************* */

				Document getTranListDoc = XMLUtil.createDocument("Transaction");
				Element elegetTranListDoc = getTranListDoc.getDocumentElement();
				elegetTranListDoc
						.setAttribute("Tranname", "VSIDraftOrderCancellation");

				Document sGetTransListTemplate = XMLUtil
						.createDocument("TransactionList");
				Element eleTemplate = sGetTransListTemplate.getDocumentElement();
				Element EleChildElement = XMLUtil.appendChild(
						sGetTransListTemplate, eleTemplate, "Transaction", "");
				EleChildElement.setAttribute("TransactionKey", "");
				EleChildElement.setAttribute("Tranname", "");

				if (log.isVerboseEnabled()) {
					log.verbose("sGetTransListTemplate  : \n"
							+ XMLUtil.getXMLString(sGetTransListTemplate));
				}
				
				YIFApi api = YIFClientFactory.getInstance().getApi();
				env.setApiTemplate("getTransactionList", sGetTransListTemplate);
				Document getTranListOutDoc = api.invoke(env, "getTransactionList",
						getTranListDoc);
				env.clearApiTemplate("getTransactionList");
				
				if (log.isVerboseEnabled()) {
					log.verbose("getTranListOutDoc output : \n"
							+ XMLUtil.getXMLString(getTranListOutDoc));
				}
				Element eleTranListOutDoc = (Element) getTranListOutDoc
						.getElementsByTagName("Transaction").item(0);
				String sTransactionKey = eleTranListOutDoc
						.getAttribute("TransactionKey");

				Document sgetAgentCriteriaList = XMLUtil
						.createDocument("AgentCriteria");
				Element eleAgentTemplate = sgetAgentCriteriaList
						.getDocumentElement();
				eleAgentTemplate.setAttribute("TransactionKey", sTransactionKey);
				String NullTemplate = null;
				env.setApiTemplate("getAgentCriteriaList", NullTemplate);
				Document getAgentCriteriaListDoc = api.invoke(env,
						"getAgentCriteriaList", sgetAgentCriteriaList);
				env.clearApiTemplate("getAgentCriteriaList");
				
				if (log.isVerboseEnabled()) {
					log.verbose("getAgentCriteriaListDoc output : \n"
							+ XMLUtil.getXMLString(getAgentCriteriaListDoc));
				}
				Element eleOutDoc = (Element) getAgentCriteriaListDoc
						.getElementsByTagName("Attribute").item(0);
				if (log.isVerboseEnabled()) {
					log.verbose("eleOutDoc output : \n"
							+ XMLUtil.getElementXMLString(eleOutDoc));
				}
				
				String sDocumentType = eleOutDoc.getAttribute("Value");
				
				
				
				elegetOrderListInDoc.setAttribute("DocumentType", sDocumentType);
				if (log.isVerboseEnabled()) {
					log.verbose("getOrderList input : \n"
							+ XMLUtil.getXMLString(getOrderListInDoc));
				}
				YIFApi api1 = YIFClientFactory.getInstance().getApi();
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/GetDraftOrderList.xml");
				Document getOrderListOutDoc = api1.invoke(env,
						VSIConstants.API_GET_ORDER_LIST, getOrderListInDoc);
				
				 
				env.clearApiTemplate(VSIConstants.API_GET_ORDER_LIST);
				
				if (log.isVerboseEnabled()) {
					log.verbose("getOrderList output : \n"
							+ XMLUtil.getXMLString(getOrderListOutDoc));
				}

				OrderList.add(getOrderListOutDoc);
			}
		}
		if(log.isDebugEnabled()){
			log.debug("getJobs method :- Ends");
		}
		return OrderList;
	}

	/**
	 * @param env
	 * @param strCodeType
	 * @param strCodeValue
	 * @param strOrganizationCode
	 * @param isShortDescRequired
	 * @return
	 * @throws Exception
	 */
	public static String getCommonCodeValue(YFSEnvironment env,
			String strCodeType, String strCodeValue,
			String strOrganizationCode, boolean isShortDescRequired)
			throws Exception {

		Element eleCommonCodeOutputXml = getCommonCodeElement(env, strCodeType,
				strCodeValue, strOrganizationCode, "");
		if (log.isVerboseEnabled()) {
			log.debug("eleCommonCodeOutputXml", XmlUtils
					.getString(eleCommonCodeOutputXml));

		}
		String strDescription = "";
		if (!XmlUtils.isVoid(eleCommonCodeOutputXml)) {
			Element eleCommonCode = XmlUtils.getChildElement(
					eleCommonCodeOutputXml, "CommonCode");
			if (log.isVerboseEnabled()) {
				log.debug("eleCommonCode", XmlUtils.getString(eleCommonCode));

			}
			if (!XmlUtils.isVoid(eleCommonCode)) {
				{
					strDescription = eleCommonCode
							.getAttribute("CodeLongDescription");
				}
			}
		}

		return strDescription;
	}

	/**
	 * @param env
	 * @param strCodeType
	 * @param strCodeValue
	 * @param strOrganizationCode
	 * @param shortDescription
	 * @return
	 * @throws Exception
	 */
	private static Element getCommonCodeElement(YFSEnvironment env,
			String strCodeType, String strCodeValue,
			String strOrganizationCode, String shortDescription)
			throws Exception {

		Document docCommonCode = XmlUtils.createDocument("CommonCode");
		Element eCommonCode = docCommonCode.getDocumentElement();
		eCommonCode.setAttribute("CodeType", strCodeType);
		if (!XmlUtils.isVoid(strCodeValue)) {
			eCommonCode.setAttribute("CodeValue", strCodeValue);
		}
		if (!XmlUtils.isVoid(strCodeValue)) {
			eCommonCode.setAttribute("OrganizationCode", strOrganizationCode);
		}
		if (!XmlUtils.isVoid(shortDescription)) {
			eCommonCode.setAttribute("CodeShortDescription", shortDescription);
		}
		YIFApi api2 = YIFClientFactory.getInstance().getApi();
		if (log.isVerboseEnabled()) {
			log.verbose("docCommonCode : \n"
					+ XMLUtil.getXMLString(docCommonCode));
		}
		Document commoncodedoc = api2.invoke(env, "getCommonCodeList",
				docCommonCode);
		if (log.isVerboseEnabled()) {
			log.verbose("docCommonCode : \n"
					+ XMLUtil.getXMLString(commoncodedoc));
		}
		Element EleCommoncode = commoncodedoc.getDocumentElement();
		return EleCommoncode;

	}

	public void executeJob(YFSEnvironment env, Document docInput)
			throws Exception

	{

		Element EleInput = docInput.getDocumentElement();
		NodeList lNode = EleInput.getElementsByTagName("Order");
	
		for (int i = 0; i < lNode.getLength(); i++) {
			Element EleOrder = (Element) lNode.item(i);
			String sOrderHeaderKey = EleOrder.getAttribute("OrderHeaderKey");
			/** **********preparing document For changeOrder************** */
			Document changeOrderDoc = XMLUtil.createDocument("Order");
			Element elechangeOrderDoc = changeOrderDoc.getDocumentElement();
			elechangeOrderDoc.setAttribute("Action", "CANCEL");
			elechangeOrderDoc.setAttribute("Override", "Y");
			elechangeOrderDoc.setAttribute("OrderHeaderKey", sOrderHeaderKey);
			elechangeOrderDoc.setAttribute("ModificationReasonCode",
					"DraftOrder");

			if (log.isVerboseEnabled()) {
				log.verbose("changeOrder input : \n"
						+ XMLUtil.getXMLString(changeOrderDoc));
			}

			YIFApi api2 = YIFClientFactory.getInstance().getApi();
			Document changeOrderDocOutDoc = api2.invoke(env, "changeOrder",
					changeOrderDoc);
			if (log.isVerboseEnabled()) {
				log.verbose("changeOrder output : \n"
						+ XMLUtil.getXMLString(changeOrderDocOutDoc));
			}
			if(log.isDebugEnabled()){
				log.debug("executeJob method :- Ends");
			}
		}

	}

}
