package com.vsi.oms.api.web;

import java.rmi.RemoteException;
import java.util.Properties;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

public class VSICheckAndReceiveReturns implements YIFCustomApi {

  @Override
  public void setProperties(Properties arg0) throws Exception {
    // TODO Auto-generated method stub

  }

  /**
   * Logger for the Class.
   */
  private static final YFCLogCategory LOG = (YFCLogCategory) YFCLogCategory
      .getLogger(VSICheckAndReceiveReturns.class.getName());

  /**
   * checkAndReceiveReturns is a Entry method to check for a return order to acknowledge else create
   * a new return Order
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

    LOG.beginTimer("VSICheckAndReceiveReturns.createAndConfirmReturnOrder");

    if (!SCUtil.isVoid(inputDoc)) {
    	if(LOG.isDebugEnabled()){
    		LOG.debug(" *** Entering Method : createAndConfirmReturnOrder Input Doc is: \n "
    				+ XMLUtil.getXMLString(inputDoc));
    	}

      // get the Receipt line tag
      Element eleReceiptLine = (Element) XPathAPI.selectSingleNode(inputDoc,
          VSIConstants.XPATH_RECEIPT_RECEIPTLINES_RECEIPTLINE);

      String enteredQuantity = eleReceiptLine.getAttribute(VSIConstants.ATTR_QUANTITY);
      double denteredQuantity = Double.parseDouble(enteredQuantity);

      Element eleReturnOrderLines = (Element) XPathAPI.selectSingleNode(
          inputDoc.getDocumentElement(), VSIConstants.XPATH_RECEIPT_RETURN_ORDER_LINES);
      
      if(LOG.isDebugEnabled()){
			LOG.debug("receiving node is"+inputDoc.getDocumentElement().getAttribute("ReceivingNode"));
      }
 	  env.setTxnObject("ReceivingNodeForWhseReturn", inputDoc.getDocumentElement().getAttribute("ReceivingNode"));
 	  
 	  String returnOrderHeaderKey = inputDoc.getDocumentElement().getAttribute("ReturnOrderHeaderKey");
 	  
 	  if(!YFCCommon.isVoid(returnOrderHeaderKey))
 	  {
 		 // Create Input to call processReturnOrder
 	      Document inputProcessReturnOrderDoc = SCXmlUtil
 	          .createDocument(VSIConstants.ELE_ORDER);
 	      inputProcessReturnOrderDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, 
 	    		  returnOrderHeaderKey);
 	      
 	     inputProcessReturnOrderDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_OVERRIDE, 
	    		  "Y");
 	     
 	     Element receiptLines = SCXmlUtil.getChildElement(inputDoc.getDocumentElement(), "ReceiptLines");
 	     Element receiptLine = SCXmlUtil.getChildElement(receiptLines, "ReceiptLine");
 	     Element returnOrderLines = SCXmlUtil.getChildElement(receiptLine, "ReturnOrderLines");
 	     Element returnOrderLine = SCXmlUtil.getChildElement(returnOrderLines, "OrderLine");
 	     String orderLineKey = returnOrderLine.getAttribute("OrderLineKey");
 	     
 	     
 	   Element orderLines = SCXmlUtil.createChild(inputProcessReturnOrderDoc.getDocumentElement(),"OrderLines");
 	   Element orderLine = SCXmlUtil.createChild(orderLines,"OrderLine");
 	   orderLine.setAttribute("OrderLineKey", orderLineKey);
 	   orderLine.setAttribute("ShipNode", inputDoc.getDocumentElement().getAttribute("ReceivingNode"));
 	   
 	      
 	  // Change Order Template
       Document changeOrderTemplateDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
       changeOrderTemplateDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
           "");

       // Invoke Change order API
       VSIUtils.invokeAPI(env, changeOrderTemplateDoc,
           "changeOrder", inputProcessReturnOrderDoc);
 	  }
 	
      NodeList eleReturnOrderLinesList = eleReturnOrderLines
          .getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

      for (int i = 0; i < eleReturnOrderLinesList.getLength(); i++) {

        Element returnOrderLineEle = (Element) (eleReturnOrderLinesList).item(i);

        double ordQuantityInReturnOrder = SCXmlUtil.getDoubleAttribute(returnOrderLineEle,
            VSIConstants.ATTR_ORD_QTY, 0.00);
        double receiveQuantityInReturnOrder = SCXmlUtil.getDoubleAttribute(returnOrderLineEle,
            VSIConstants.ATTR_RECEIVED_QTY, 0.00);

        double remainingQtyInReturnOrder = ordQuantityInReturnOrder - receiveQuantityInReturnOrder;

        if (denteredQuantity <= remainingQtyInReturnOrder) {

          // call receive return order service with entered quantity to receive

          // call createInput for service flow "VSIReceiveReturnsForAckSyncService"
          Document inputReceiveReturnsDoc = this.createInputForReceiveReturnsDoc(inputDoc,
              returnOrderLineEle, denteredQuantity);

          Document outputReturnsDoc = VSIUtils.invokeService(env,
              "VSIReceiveReturnsForAckSyncService", inputReceiveReturnsDoc);

          return outputReturnsDoc;

        } else {

          // Call receive return order service with remaining qty in return order to receive

          Document inputReceiveReturnsDoc = this.createInputForReceiveReturnsDoc(inputDoc,
              returnOrderLineEle, remainingQtyInReturnOrder);
          
         

          Document outputReturnsDoc = VSIUtils.invokeService(env,
              "VSIReceiveReturnsForAckSyncService", inputReceiveReturnsDoc);

          denteredQuantity = denteredQuantity - remainingQtyInReturnOrder;

        }

      }

      if (denteredQuantity > 0) {

        // Call create return Order service with remaining of entered qty in return Order to receive
    	

        Document inputCreateAndReceiveReturnsDoc = this
            .createInputForCreateAndReceiveReturnsDoc(env, inputDoc, denteredQuantity);

        Document outputReturnsDoc = VSIUtils.invokeService(env,
            "VSICreateAndReceiveReturnsForAckSyncService", inputCreateAndReceiveReturnsDoc);

        return outputReturnsDoc;

      }

    }

    return inputDoc;

  }

  private Document createInputForCreateAndReceiveReturnsDoc(YFSEnvironment env, Document inputDoc,
      Double denteredQuantity) throws TransformerException {

    LOG.beginTimer("VSICheckAndReceiveReturns.createInputForCreateAndReceiveReturnsDoc");
    if(LOG.isDebugEnabled()){
    	LOG.debug(" *** Entering Method : createInputForCreateAndReceiveReturnsDoc Input Doc is: \n "
    			+ XMLUtil.getXMLString(inputDoc));
    }

    Element inputEle = inputDoc.getDocumentElement();

    // Create Input for Receive
    Document inputReceiveOrderDoc = SCXmlUtil.createDocument(VSIConstants.ATTR_RECEIPT);

    Element inputReceiveOrderele = inputReceiveOrderDoc.getDocumentElement();
    inputReceiveOrderele.setAttribute(VSIConstants.ATTR_RECEIVING_NODE,
        inputEle.getAttribute(VSIConstants.ATTR_RECEIVING_NODE));
    inputReceiveOrderele.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0003");
    inputReceiveOrderele.setAttribute(VSIConstants.ATTR_SALES_ORDER_HEADER_KEY,
        inputEle.getAttribute(VSIConstants.ATTR_SALES_ORDER_HEADER_KEY));

    // Create Shipment Line Child element
    Element eleShipment = SCXmlUtil.createChild(inputReceiveOrderele, VSIConstants.ELE_SHIPMENT);
    eleShipment.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        inputEle.getAttribute(VSIConstants.ATTR_SALES_ORDER_HEADER_KEY));

    // Create ReceiptLines Child element
    Element eleReceiptLines = SCXmlUtil.createChild(inputReceiveOrderele,
        VSIConstants.ELE_RECEIPT_LINES);

    // Create ReceiptLines Child element
    Element eleReceiptLine = SCXmlUtil.createChild(eleReceiptLines, VSIConstants.ELE_RECEIPT_LINE);

    // get the Receipt line tag
    Element eleReceiptLineInput = (Element) XPathAPI.selectSingleNode(inputDoc,
        VSIConstants.XPATH_RECEIPT_RECEIPTLINES_RECEIPTLINE);

    eleReceiptLine.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        inputEle.getAttribute(VSIConstants.ATTR_SALES_ORDER_HEADER_KEY));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_SUB_LINE_NO,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_SUB_LINE_NO));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO));

    eleReceiptLine.setAttribute(VSIConstants.ATTR_DISPOSITION_CODE,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_DISPOSITION_CODE));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_ITEM_ID,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_ITEM_ID));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_QUANTITY, Double.toString(denteredQuantity));
    
    // Create ReceiptLines Extn element Tag
    Element eleReceiptLineExtn = SCXmlUtil.createChild(eleReceiptLine, VSIConstants.ELE_EXTN);
    
    eleReceiptLineExtn.setAttribute("ExtnWHRtnRsnCode",
        eleReceiptLineInput.getAttribute("ReasonCode"));

    // Get the sales order
    Document docGetSalesOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
    Element eleGetSalesOrder = docGetSalesOrder.getDocumentElement();
    eleGetSalesOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, inputEle.getAttribute(VSIConstants.ATTR_SALES_ORDER_HEADER_KEY));
    String strEntCode = "";
    try {
		Document docSalesOrderList = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_VSI_CHECK_RECEIVE_RETURNS,
				VSIConstants.API_GET_ORDER_LIST, docGetSalesOrder);
		Element eleSalesOrderList = docSalesOrderList.getDocumentElement();
		Element eleSalesOrder = SCXmlUtil.getChildElement(eleSalesOrderList, VSIConstants.ELE_ORDER);
		strEntCode = eleSalesOrder.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
	} catch (YFSException | RemoteException | YIFClientCreationException e) {
		LOG.error("Exeception in VSICheckAndReceiveReturns.createInputForCreateAndReceiveReturnsDoc() while calling getOrderList to get the sales order"
				+ "with input: " + SCXmlUtil.getString(docGetSalesOrder), e);
		throw VSIUtils.getYFSException(e);
	}
    // Create Order Child element
    Element eleOrder = SCXmlUtil.createChild(inputReceiveOrderele, VSIConstants.ELE_ORDER);
    eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, VSIConstants.RETURN_DOCUMENT_TYPE);
    eleOrder.setAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG, VSIConstants.FLAG_Y);
    eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, strEntCode);
    eleOrder.setAttribute(VSIConstants.ATTR_ENTRY_TYPE, VSIConstants.ENTRYTYPE_CC);
    eleOrder.setAttribute(VSIConstants.ATTR_SELLER_ORG_CODE, strEntCode);

    // Create Order Child element
    Element eleOrderLines = SCXmlUtil.createChild(eleOrder, VSIConstants.ELE_ORDER_LINES);

    // Create Order Child element
    Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines, VSIConstants.ELE_ORDER_LINE);

    eleOrderLine.setAttribute(VSIConstants.ATTR_REASON_CODE,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_REASON_CODE));
    eleOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY, Double.toString(denteredQuantity));
    eleOrderLine.setAttribute(VSIConstants.ATTR_SHIP_NODE,
        inputEle.getAttribute(VSIConstants.ATTR_RECEIVING_NODE));

    // Create Order Child element
    Element eleOrderLineItem = SCXmlUtil.createChild(eleOrderLine, VSIConstants.ELE_ITEM);

    eleOrderLineItem.setAttribute(VSIConstants.ATTR_ITEM_ID,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_ITEM_ID));

    // Create derived Order Child element
    Element eleOrderLinederivedOrder = SCXmlUtil.createChild(eleOrderLine,
        VSIConstants.ELE_DERIVED_FROM);

    eleOrderLinederivedOrder.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));

    return inputReceiveOrderDoc;

  }

  private Document createInputForReceiveReturnsDoc(Document inputDoc, Element returnOrderLineEle,
      Double denteredQuantity) throws TransformerException {

    LOG.beginTimer("VSICheckAndReceiveReturns.createInputForReceiveReturnsDoc");
    if(LOG.isDebugEnabled()){
    	LOG.debug(" *** Entering Method : createInputForReceiveReturnsDoc Input Doc is: \n "
    			+ XMLUtil.getXMLString(inputDoc));
    }

    Element inputEle = inputDoc.getDocumentElement();

    // Create Input for Receive
    Document inputReceiveOrderDoc = SCXmlUtil.createDocument(VSIConstants.ATTR_RECEIPT);

    Element inputReceiveOrderele = inputReceiveOrderDoc.getDocumentElement();
    inputReceiveOrderele.setAttribute(VSIConstants.ATTR_RECEIVING_NODE,
        inputEle.getAttribute(VSIConstants.ATTR_RECEIVING_NODE));
    inputReceiveOrderele.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0003");

    // Create Shipment Line Child element
    Element eleShipment = SCXmlUtil.createChild(inputReceiveOrderele, VSIConstants.ELE_SHIPMENT);
    eleShipment.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        returnOrderLineEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));

    // Create ReceiptLines Child element
    Element eleReceiptLines = SCXmlUtil.createChild(inputReceiveOrderele,
        VSIConstants.ELE_RECEIPT_LINES);

    // Create ReceiptLines Child element
    Element eleReceiptLine = SCXmlUtil.createChild(eleReceiptLines, VSIConstants.ELE_RECEIPT_LINE);

    // get the Receipt line tag
    Element eleReceiptLineInput = (Element) XPathAPI.selectSingleNode(inputDoc,
        VSIConstants.XPATH_RECEIPT_RECEIPTLINES_RECEIPTLINE);

    eleReceiptLine.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        returnOrderLineEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
        returnOrderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_SUB_LINE_NO,
        returnOrderLineEle.getAttribute(VSIConstants.ATTR_SUB_LINE_NO));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO,
        returnOrderLineEle.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO));

    eleReceiptLine.setAttribute(VSIConstants.ATTR_DISPOSITION_CODE,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_DISPOSITION_CODE));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_ITEM_ID,
        eleReceiptLineInput.getAttribute(VSIConstants.ATTR_ITEM_ID));
    eleReceiptLine.setAttribute(VSIConstants.ATTR_QUANTITY, Double.toString(denteredQuantity));
    
    // Create ReceiptLines Extn element Tag
    Element eleReceiptLineExtn = SCXmlUtil.createChild(eleReceiptLine, VSIConstants.ELE_EXTN);
    
    eleReceiptLineExtn.setAttribute("ExtnWHRtnRsnCode",
        eleReceiptLineInput.getAttribute("ReasonCode"));

    return inputReceiveOrderDoc;

  }

}
