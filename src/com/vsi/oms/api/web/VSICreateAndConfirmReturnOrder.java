package com.vsi.oms.api.web;

import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSICommonServiceUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICreateAndConfirmReturnOrder implements YIFCustomApi {

  @Override
  public void setProperties(Properties arg0) throws Exception {
    // TODO Auto-generated method stub

  }

  /**
   * Logger for the Class.
   */
  private static final YFCLogCategory LOG = (YFCLogCategory) YFCLogCategory
      .getLogger(VSICreateAndConfirmReturnOrder.class.getName());

  /**
   * createAndConfirmReturnOrder is a Entry method to create a return order to acknowledge.
   *
   * @param env
   *          the YFSEnvironment Object
   * @param inputDocument
   * 
   * 
   * @return the document. the input document.
   * @throws Exception
   *           the exception
   */

  public Document createAndConfirmReturnOrder(YFSEnvironment env, Document inputDoc)
      throws Exception {

    LOG.beginTimer("VSICreateAndConfirmReturnOrder.createAndConfirmReturnOrder");

    Document outputDoc = null;
    String salesOrderHeaderKey = null;
    if (!SCUtil.isVoid(inputDoc)) {
    
    	if(LOG.isDebugEnabled()){
    		LOG.debug(" *** Entering Method : createAndConfirmReturnOrder Input Doc is: \n "
    				+ XMLUtil.getXMLString(inputDoc));
    	}

      salesOrderHeaderKey = inputDoc.getDocumentElement()
          .getAttribute(VSIConstants.ATTR_SALES_ORDER_HEADER_KEY);
      
      if(LOG.isDebugEnabled()){
  		LOG.debug("VSICreateAndConfirmReturnOrder in Input for inputDoc"
    			  + SCXmlUtil.getString(inputDoc.getDocumentElement()));
      }
      
      
      if (!SCUtil.isVoid(salesOrderHeaderKey)) {
    	  
    	  if(LOG.isDebugEnabled()){
      		LOG.debug("receiving node is"+inputDoc.getDocumentElement().getAttribute("ReceivingNode"));
    	  }
 	  env.setTxnObject("ReceivingNodeForWhseReturn", inputDoc.getDocumentElement().getAttribute("ReceivingNode"));

        // Create Input to call getOrderList
        Document inputGetOrderListDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
        inputGetOrderListDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
            salesOrderHeaderKey);

        // Invoke getOrderListAPI to get the Person Info
        Document outputGetOrderListDoc = VSIUtils.invokeAPI(env,
            "global/template/api/VSIgetOrderListTemplateAck.xml", "getOrderList",
            inputGetOrderListDoc);

        if(LOG.isDebugEnabled()){
    		LOG.debug("Output for getOrderList with custom xml"
            + SCXmlUtil.getString(outputGetOrderListDoc.getDocumentElement()));
        }

        // Create Input to call createOrder for Returns
        Document inputCreateOrderDoc = this.createInputForCreateOrderDoc(inputDoc,
            outputGetOrderListDoc);        

        // Invoke CreateOrder to create draft return Order
        Document outputCreateOrderDoc = VSIUtils.invokeAPI(env,
            "global/template/api/VSICreateReturnOrderTemplateAck.xml", "createOrder",
            inputCreateOrderDoc);

        if(LOG.isDebugEnabled()){
    		LOG.debug("Output for create draft return Order xml"
            + SCXmlUtil.getString(outputCreateOrderDoc.getDocumentElement()));
        }
        
        // Create Input to call processReturnOrder
        Document inputProcessReturnOrderDoc = SCXmlUtil
            .createDocument(VSIConstants.ELE_ORDER);
        inputProcessReturnOrderDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, 
            outputCreateOrderDoc.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
        inputProcessReturnOrderDoc.getDocumentElement().setAttribute("DetermineOrdersForReturn","N");
        inputProcessReturnOrderDoc.getDocumentElement().setAttribute("ExecuteReturnPolicy","N");
        
        // Create OutputTemplate to call processReturnOrder
        Document templateProcessReturnOrderDoc = SCXmlUtil
            .createDocument(VSIConstants.ELE_ORDER);
        templateProcessReturnOrderDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, "");
        
        // Invoke processReturnOrder to Process return Order
        Document outputProcessReturnOrderDoc = VSIUtils.invokeAPI(env,"global/template/api/createReturn_processReturnOrder.xml","processReturnOrder",
            inputProcessReturnOrderDoc);
      
        VSICommonServiceUtil vcsUtil = new VSICommonServiceUtil();
        vcsUtil.massageProcessReturnOrder(env,outputProcessReturnOrderDoc);
               
       
        if(LOG.isDebugEnabled()){
    		LOG.debug("Output of Process return Order xml"
            + SCXmlUtil.getString(outputProcessReturnOrderDoc.getDocumentElement()));
        }

        // Create Input to call confirm Draft Order
        Document inputConfirmDraftOrderDoc = SCXmlUtil
            .createDocument(VSIConstants.ELE_CONFIRM_DRAFT_ORDER);

        Element eleOrder = SCXmlUtil.getChildElement(inputDoc.getDocumentElement(), VSIConstants.ELE_ORDER);
        Element inputConfirmDraftOrderele = inputConfirmDraftOrderDoc.getDocumentElement();
        inputConfirmDraftOrderele.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eleOrder.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE));
        inputConfirmDraftOrderele.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, VSIConstants.RETURN_DOCUMENT_TYPE);
        inputConfirmDraftOrderele.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
            outputCreateOrderDoc.getDocumentElement()
                .getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));

        if(LOG.isDebugEnabled()){
    		LOG.debug("Input for create draft return Order xml"
            + SCXmlUtil.getString(inputConfirmDraftOrderDoc.getDocumentElement()));
        }

        // Invoke ConfirmDraftOrder to confirm draft return Order
        Document outputConfirmDraftOrderDoc = VSIUtils.invokeAPI(env,
            "global/template/api/VSIConfirmReturnOrderTemplateAck.xml", "confirmDraftOrder",
            inputConfirmDraftOrderDoc);
        
        env.setTxnObject("DraftOrderConfirm", "Y");

        if(LOG.isDebugEnabled()){
    		LOG.debug("Output for create draft return Order xml"
            + SCXmlUtil.getString(outputConfirmDraftOrderDoc.getDocumentElement()));
        }        
        

        // Create Input to call ReceiveOrder for Returns
        Document inputReceiveReturnDoc = this.createInputForReceiveReturnDoc(inputDoc,
            outputConfirmDraftOrderDoc);

        if(LOG.isDebugEnabled()){
    		LOG.debug("Input for receive return Order xml"
            + SCXmlUtil.getString(inputReceiveReturnDoc.getDocumentElement()));
        }

        outputDoc = VSIUtils.invokeService(env, "VSIReceiveReturnsForAckSyncService",
            inputReceiveReturnDoc);

      }

    }

    return outputDoc;

  }

  private Document createInputForReceiveReturnDoc(Document inputDoc,
      Document outputConfirmDraftOrderDoc) throws TransformerException {

    LOG.beginTimer("VSICreateAndConfirmReturnOrder.createInputForReceiveReturnDoc");
    if(LOG.isDebugEnabled()){
    	LOG.debug(" *** Entering Method : createInputForReceiveReturnDoc Input Doc is: \n "
    			+ XMLUtil.getXMLString(inputDoc));
    }

    // get the Order tag
    Element eleOrder = (Element) XPathAPI.selectSingleNode(inputDoc, VSIConstants.XPATH_ORDER);

    eleOrder.getParentNode().removeChild(eleOrder);

    Element inputEle = inputDoc.getDocumentElement();

    inputEle.removeAttribute(VSIConstants.ATTR_SALES_ORDER_HEADER_KEY);

    Element outputConfirmDraftOrdeEle = outputConfirmDraftOrderDoc.getDocumentElement();

    // get the Shipment tag
    Element eleShipment = (Element) XPathAPI.selectSingleNode(inputDoc,
        VSIConstants.XPATH_RECEIPT_SHIPMENT);
    eleShipment.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        outputConfirmDraftOrdeEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));

    // get the Receipt line tag
    Element eleReceiptLine = (Element) XPathAPI.selectSingleNode(inputDoc,
        VSIConstants.XPATH_RECEIPT_RECEIPTLINES_RECEIPTLINE);

    // get the Order tag
    Element eleOrderLine = (Element) XPathAPI.selectSingleNode(outputConfirmDraftOrderDoc,
        VSIConstants.XPATH_ORDER_LINE);

    eleReceiptLine.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        outputConfirmDraftOrdeEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
        eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_SUB_LINE_NO,
        eleOrderLine.getAttribute(VSIConstants.ATTR_SUB_LINE_NO));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO,
        eleOrderLine.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO));

    if(LOG.isDebugEnabled()){
		LOG.debug(
        "Input for Receiving Return Order" + SCXmlUtil.getString(inputDoc.getDocumentElement()));
    }

    return inputDoc;

  }

  private Document createInputForCreateOrderDoc(Document inputDoc, Document outputGetOrderListDoc)
      throws TransformerException {

    LOG.beginTimer("VSICreateAndConfirmReturnOrder.createInputForCreateOrderDoc");
    if(LOG.isDebugEnabled()){
    	LOG.debug(" *** Entering Method : createInputForCreateOrderDoc Input Doc is: \n "
    			+ XMLUtil.getXMLString(inputDoc));
    }

    // get the Order tag
    Element eleOrder = (Element) XPathAPI.selectSingleNode(inputDoc, VSIConstants.XPATH_ORDER);

    //get Order Type from sales Order
    Element eleSalesOrder = (Element) XPathAPI.selectSingleNode(outputGetOrderListDoc,
        VSIConstants.XPATH_ORDERLIST_ORDER);
    
    //Added and Enter Type
   // String orderType = eleSalesOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
   // String enteredBy = eleSalesOrder.getAttribute(VSIConstants.ATTR_SELLING_OUTLET);
   // eleOrder.setAttribute(VSIConstants.ATTR_ORDER_TYPE, orderType);
    eleOrder.setAttribute(VSIConstants.ATTR_ENTRY_TYPE, "WareHouse");
    eleOrder.setAttribute("ProcessPaymentOnReturnOrder", "Y");
   // eleOrder.setAttribute(VSIConstants.ATTR_SELLING_OUTLET, enteredBy);
    
    
    // get the PersonInfo tags from outputGetOrderListDoc
    Element elePersonInfoShipTo = (Element) XPathAPI.selectSingleNode(outputGetOrderListDoc,
        VSIConstants.XPATH_ORDER_PERSON_INFO_SHIPTO);
    Element elePersonInfoBillTo = (Element) XPathAPI.selectSingleNode(outputGetOrderListDoc,
        VSIConstants.XPATH_ORDER_PERSON_INFO_BILLTO);
    Element elePriceInfo = (Element) XPathAPI.selectSingleNode(outputGetOrderListDoc,
        VSIConstants.XPATH_ORDER_PRICEINFO);

    // Create input Document for createOrder.
    Document docInputForCreateOrder = SCXmlUtil.createFromString(SCXmlUtil.getString(eleOrder));

    SCXmlUtil.importElement(docInputForCreateOrder.getDocumentElement(), elePersonInfoShipTo);
    SCXmlUtil.importElement(docInputForCreateOrder.getDocumentElement(), elePersonInfoBillTo);
    SCXmlUtil.importElement(docInputForCreateOrder.getDocumentElement(), elePriceInfo);
    SCXmlUtil.setAttribute(docInputForCreateOrder.getDocumentElement(), VSIConstants.ATTR_BILL_TO_ID, eleSalesOrder.getAttribute(VSIConstants.ATTR_BILL_TO_ID));

    if(LOG.isDebugEnabled()){
		LOG.debug("Input for Creating a Draft Return Order"
        + SCXmlUtil.getString(docInputForCreateOrder.getDocumentElement()));
    }

    return docInputForCreateOrder;

  }

}
