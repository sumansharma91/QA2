package com.vsi.oms.api;

/*
 * author : Perficient
 * 
 * This class is invoked on clicking the close button in the ISSUE REFUND NOW Page
 * Return Order status is changed from Created to Refund Issued once the Issue Refund Now action is performed in Call Center
 * 
 * */
import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

import com.yantra.interop.japi.YIFClientCreationException;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIChangeOrderStatusToRefundIssued {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIChangeOrderStatusToRefundIssued.class);

	public void vsiChangeOrderStatusToRefundIssued(YFSEnvironment env,
			Document inXML) throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIChangeOrderStatusToRefundIssued================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}

		try {

			Element orderElem = (Element) inXML.getElementsByTagName(
					VSIConstants.ELE_ORDER).item(0);
			String orderHeaderKey = orderElem
					.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);

			Document changeOrderStatusInput = XMLUtil
					.createDocument("OrderStatusChange");
			Element changeOrderStatusElement = changeOrderStatusInput
					.getDocumentElement();
			changeOrderStatusElement.setAttribute(
					VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
			changeOrderStatusElement.setAttribute(
					VSIConstants.ATTR_BASE_DROP_STATUS, "1100.10.ex");
			changeOrderStatusElement.setAttribute(
					VSIConstants.ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY, "Y");
			changeOrderStatusElement
					.setAttribute(VSIConstants.ATTR_TRANSACTION_ID,
							"ChangeRefIssued.0003.ex");
			changeOrderStatusElement.setAttribute(
					VSIConstants.ATTR_SELECT_METHOD, "WAIT");

			Document changeOrderStatusOutput = VSIUtils.invokeAPI(env,
					VSIConstants.API_CHANGE_ORDER_STATUS,
					changeOrderStatusInput);

			if(log.isDebugEnabled()){
				log.debug("Printing Output XML :"
						+ XmlUtils.getString(changeOrderStatusOutput));
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
