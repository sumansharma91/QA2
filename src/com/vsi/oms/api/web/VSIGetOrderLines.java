package com.vsi.oms.api.web;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIGetOrderLines implements YIFCustomApi {

  @Override
  public void setProperties(Properties arg0) throws Exception {
    // TODO Auto-generated method stub

  }

  /**
   * Logger for the Class.
   */
  private static final YFCLogCategory LOG = (YFCLogCategory) YFCLogCategory
      .getLogger(VSIGetOrderLines.class.getName());

  /**
   * getOrderLinesToAcknowledge is a Entry method to get the Order lines of an sales/return order to
   * acknowledge.
   *
   * @param env
   *          the YFSEnvironment Object
   * @param inputDocument
   *          the input document
   *          <OrderList> <Order OrderHeaderKey="2017022813354346620" /> </OrderList>
   * 
   * @return the document. the input document.
   * @throws TransformerException
   * @throws YIFClientCreationException
   * @throws RemoteException
   * @throws YFSException
   * 
   * @throws Exception
   *           the exception
   */

  public Document getOrderLinesToAcknowledge(YFSEnvironment env, Document inputDoc)
      throws YFCException, TransformerException, YFSException, RemoteException,
      YIFClientCreationException {

    LOG.beginTimer("VSIGetOrderLines.getOrderLinesToAcknowledge");
    
    Document outputDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
    HashMap<String, String> hmWhOrderLine = null;
    
    try{
    
    Boolean isWholesale = false;
    
    String documentType = null;
    if (!SCUtil.isVoid(inputDoc)) {
    	if(LOG.isDebugEnabled()){
    		LOG.debug(" *** Entering Method : getOrderLinesToAcknowledge Input Doc is: \n "
    				+ XMLUtil.getXMLString(inputDoc));
    	}

      // get the Order tag and then fetch Document type from the Order tag
      Element eleOrder = (Element) XPathAPI.selectSingleNode(inputDoc,
          VSIConstants.XPATH_ORDERLIST_ORDER);

      if (!SCUtil.isVoid(eleOrder)) {
        documentType = eleOrder.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
        String orderType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_TYPE);
        String entryType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ENTRY_TYPE);
        if(orderType.equalsIgnoreCase(VSIConstants.WHOLESALE) || entryType.equalsIgnoreCase(VSIConstants.WHOLESALE)){
        	isWholesale = true;
        }
        // If Wholesale and 0001, return blank. User should use return order no. to receive
        if(isWholesale && documentType.equals(VSIConstants.ATTR_DOCUMENT_TYPE_SALES)){
        	return outputDoc;
        }else if (isWholesale){
        	
        	hmWhOrderLine = new HashMap<>();
        	NodeList nlReturnLine = inputDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
        	for(int i = 0; i < nlReturnLine.getLength(); i++){
        		
        		Element eleReturnLine = (Element) nlReturnLine.item(i);
        		String strReturnOrderLine = SCXmlUtil.getAttribute(eleReturnLine, VSIConstants.ATTR_ORDER_LINE_KEY);
        		String strReturnQty = SCXmlUtil.getAttribute(eleReturnLine, VSIConstants.ATTR_ORD_QTY);
        		if(LOG.isDebugEnabled()){
        			LOG.info("returnLineKey: " + strReturnOrderLine);
        			LOG.info("returnQty: "+ strReturnQty);
        		}
        		hmWhOrderLine.put(strReturnOrderLine.trim(), strReturnQty.trim());
        	}
        }
        NodeList extnOrderList = inputDoc.getDocumentElement().getElementsByTagName(VSIConstants.ELE_EXTN);
        Element extnOrderEle = (Element) extnOrderList.item(0);
        String isMigrated = extnOrderEle.getAttribute(VSIConstants.ATTR_EXTN_IS_MIGRATED);
        
        if(isMigrated.equalsIgnoreCase("Y")){
          outputDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_EXTN_IS_MIGRATED, "Y");
          return outputDoc;
          
        }

        // Create input for getCompleteOrderDetails API from inputdocument
        Document docCreateCompleteOrderInput = this.createInputForGetCompleteOrder(inputDoc,documentType, isWholesale);

        // Create Output Template for getCompleteorderDetails API

        Document docCreateCompleteOrderTemplate = this
            .createTemplateForGetCompleteOrder();

      

          // Create Output Template for getOrderList API
          Document docCreateGetOrderListTemplateForSale = this
              .createTemplateForGetOrderListForSales();

          // Create input for getOrderList API from inputdocument
          Document docCreateGetOrderListInputForSale = this
              .createInputForGetOrderListForSales(docCreateCompleteOrderInput);

          // invoke getOrderList API by Passing the template
          Document outputDocGetOrderListForSale = this.invokeGetOrderList(env,
              docCreateGetOrderListInputForSale, docCreateGetOrderListTemplateForSale,
              VSIConstants.API_GET_ORDER_LIST);

          // Check for DraftOrderFlag and delete the draft Return Orders
          this.checkDraftReturnOrderAndDelete(env, outputDocGetOrderListForSale);

     

        // invoke getCompleteorderDetails API by passing the template

        Document outputDocGetCompleteOrder = this.invokeGetCompleteOrder(env,
            docCreateCompleteOrderInput, docCreateCompleteOrderTemplate,
            VSIConstants.API_GET_COMPLETE_ORDER_DETAILS);

        // Call a method to eliminate unwanted lines of the Order
        if(isWholesale){
        	outputDocGetCompleteOrder = this.filterWholesaleOrderLines(env, outputDocGetCompleteOrder, hmWhOrderLine);
        }
        			
        outputDoc = this.filterOrderLinesOfOrder(env, outputDocGetCompleteOrder, "0001");
        
      //  outputDoc = outputDocGetCompleteOrder;

      }
    }
    
  	}catch(Exception e){
  		
  		LOG.error(e);
  		throw new YFSException(e.getMessage());
  	}finally{
  		
  		if(!YFCObject.isVoid(hmWhOrderLine)){
  			hmWhOrderLine.clear();
  			hmWhOrderLine = null;
  		}
  	}
    
    return outputDoc;

  }

  private Document filterWholesaleOrderLines(YFSEnvironment env, Document outputDocGetCompleteOrder, HashMap<String, String> hmWhOrderLine) {
	  // TODO Auto-generated method stub

	  Element eleSalesOrder = outputDocGetCompleteOrder.getDocumentElement();
	  Element eleSalesOrderLines = SCXmlUtil.getChildElement(eleSalesOrder, VSIConstants.ELE_ORDER_LINES);
	  NodeList nlSalesOrderLine = eleSalesOrderLines.getChildNodes();
	  
	  for (int i = 0; i < nlSalesOrderLine.getLength(); i++){
		  
		  Boolean removeSalesOrderLine = true;
		  Element eleSalesOrderLine = (Element) nlSalesOrderLine.item(i);
		  if(!YFCObject.isVoid(eleSalesOrderLine)){
			  
			  Element eleReturnOrderLines = SCXmlUtil.getChildElement(eleSalesOrderLine, VSIConstants.ELE_RETURN_ORDER_LINES);
			  if(!YFCObject.isVoid(eleReturnOrderLines)){
				  
				  NodeList nlReturnOrderLine = eleReturnOrderLines.getChildNodes();
				  for(int j = 0; j < nlReturnOrderLine.getLength(); j++){
					  
					  Element eleReturnOrderLine = (Element) nlReturnOrderLine.item(j);
					  
					  if(!YFCObject.isVoid(eleReturnOrderLine)){

						  if(hmWhOrderLine.containsKey(SCXmlUtil.getAttribute(eleReturnOrderLine, VSIConstants.ATTR_ORDER_LINE_KEY).trim())){

							  removeSalesOrderLine = false;
							  String strOrderedQty = hmWhOrderLine.get(SCXmlUtil.getAttribute(eleReturnOrderLine, VSIConstants.ATTR_ORDER_LINE_KEY).trim());
							  SCXmlUtil.setAttribute(eleSalesOrderLine, VSIConstants.ATTR_ORD_QTY, strOrderedQty);
							  SCXmlUtil.setAttribute(eleSalesOrderLine, VSIConstants.ATTR_ORIGINAL_ORDERED_QTY, strOrderedQty);
							  SCXmlUtil.setAttribute(eleSalesOrderLine, VSIConstants.ATTR_REMAINING_QTY, strOrderedQty);
							  SCXmlUtil.setAttribute(eleSalesOrderLine, VSIConstants.ATTR_RETURNABLE_QTY, strOrderedQty);
							  SCXmlUtil.setAttribute(eleSalesOrderLine, VSIConstants.ATTR_SHIPPED_QTY, strOrderedQty);
							  SCXmlUtil.setAttribute(eleSalesOrderLine, VSIConstants.ATTR_STATUS_QTY, strOrderedQty);
						  }else{

							  eleReturnOrderLine.getParentNode().removeChild(eleReturnOrderLine);
							  j--;
						  }
					  }
				  }
			  }
			  
			  if(removeSalesOrderLine){
				  
				  eleSalesOrderLine.getParentNode().removeChild(eleSalesOrderLine);
				  i--;
			  }
		  }
	  }
	  
	  if(LOG.isDebugEnabled()){
		  LOG.debug("Output of filterWholesaleOrderLines(): " + SCXmlUtil.getString(outputDocGetCompleteOrder));
	  }
	  return outputDocGetCompleteOrder;
}

/**
   * This method is used to delete the unwanted order .
   * 
   * @return
   * 
   * @throws TransformerException
   * @throws YIFClientCreationException
   * @throws RemoteException
   * @throws YFSException
   * 
   */

  private void checkDraftReturnOrderAndDelete(YFSEnvironment env,
      Document outputDocGetOrderListForSale)
      throws TransformerException, YFSException, RemoteException, YIFClientCreationException {

    double numberOfOrders = SCXmlUtil.getDoubleAttribute(
        outputDocGetOrderListForSale.getDocumentElement(),
        VSIConstants.ATTR_TOTAL_NUMBER_OF_RECORDS, 0.00);

    if (numberOfOrders > 0.00) {

      Element eleOrder = (Element) XPathAPI.selectSingleNode(
          outputDocGetOrderListForSale.getDocumentElement(), VSIConstants.XPATH_ORDERLIST);

      Set<Document> ordersToDeleteDoc = new HashSet<Document>();

      if (!SCUtil.isVoid(eleOrder)) {

        NodeList eleOrderList = eleOrder.getElementsByTagName(VSIConstants.ELE_ORDER);

        int len = eleOrderList.getLength();

        for (int i = 0; i < len; i++) {

          Element orderEle = (Element) (eleOrderList).item(i);

          String draftFlag = orderEle.getAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG);

          if (draftFlag.equalsIgnoreCase("Y")) {

            Document inputDeleteOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);

            Element inputDeleteOrderEle = inputDeleteOrder.getDocumentElement();

            inputDeleteOrderEle.setAttribute(VSIConstants.ATTR_ACTION, "DELETE");
            inputDeleteOrderEle.setAttribute(VSIConstants.ATTR_OVERRIDE, "Y");
            inputDeleteOrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
                orderEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));

            ordersToDeleteDoc.add(inputDeleteOrder);

          }

        }

        for (Document doc : ordersToDeleteDoc) {

          this.invokeDeleteOrderapi(env, doc, "deleteOrder");

        }

      }

    }

  }

  /**
   * This method is used to invoke DeleteOrder API .
   * 
   * @throws TransformerException
   * 
   */

  private void invokeDeleteOrderapi(YFSEnvironment env, Document docDeleteOrderInput,
      String apiName) throws YFSException, RemoteException, YIFClientCreationException {

    VSIUtils.invokeAPI(env, apiName, docDeleteOrderInput);

  }

  /**
   * This method is used to filter the unwanted order lines from the Order .
   * 
   * @throws TransformerException
   * @throws YIFClientCreationException
   * @throws RemoteException
   * @throws YFSException
   * 
   */

  private Document filterOrderLinesOfOrder(YFSEnvironment env, Document outputDocGetCompleteOrder,
      String documentType)
      throws TransformerException, YFSException, RemoteException, YIFClientCreationException {

    Element eleOrderLines = (Element) XPathAPI.selectSingleNode(
        outputDocGetCompleteOrder.getDocumentElement(), VSIConstants.XPATH_ORDERLINES_ORDER);

    if (!SCUtil.isVoid(eleOrderLines)) {

      NodeList eleOrderLinesList = eleOrderLines.getChildNodes();

      //Set<Element> targetElements = new HashSet<Element>();

      if (!SCUtil.isVoid(eleOrderLinesList)) {

        for (int i = 0; i < eleOrderLinesList.getLength(); i++) {

          Element orderLineEle = null;

          orderLineEle = (Element) (eleOrderLinesList).item(i);

          if (!SCUtil.isVoid(orderLineEle)) {

            String slineStatus = orderLineEle.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
           
            if (!SCUtil.isVoid(slineStatus)) {

            /** String slineStatusAfterReplace = slineStatus.trim().replace(".ex", "");

              double lineStatus = 0.00;

              lineStatus = Double.parseDouble(slineStatusAfterReplace);

              String lineStatusDesc = orderLineEle.getAttribute(VSIConstants.ATTR_STATUS);

              if (documentType.equals(VSIConstants.RETURN_DOCUMENT_TYPE)) {

                String orgOrdQty = orderLineEle.getAttribute(VSIConstants.ATTR_ORIGINAL_ORDERED_QTY)
                    .trim().replace(".00", "");

                orderLineEle.setAttribute(VSIConstants.ATTR_RETURN_CREATED, orgOrdQty);

                orderLineEle.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, documentType);

                if (lineStatus >= 3900.00 || lineStatusDesc.equalsIgnoreCase("Received")) {

                  targetElements.add(orderLineEle);

                } 

              } else {  **/

                orderLineEle.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, documentType);

                double returnOrderLineOrderQty = 0.00;
                double returnOrderLineReceivedQty = 0.00;

                boolean notRemovedChild = true;

               // if (lineStatus < 3700.00 || lineStatusDesc.equalsIgnoreCase("Return Received")
                 //   || lineStatus >= 3700.02 || lineStatusDesc.equalsIgnoreCase("Created")) {

                  //targetElements.add(orderLineEle);

                 // notRemovedChild = false;

                //}

                if (notRemovedChild) {

                  Element eleReturnOrderLines = (Element) orderLineEle.getFirstChild();

                  if (!SCUtil.isVoid(eleReturnOrderLines)) {

                    NodeList eleReturnOrderLinesList = eleReturnOrderLines.getChildNodes();

                    Set<Element> returnOrderElements = new HashSet<Element>();

                    if (!SCUtil.isVoid(eleReturnOrderLinesList)) {

                      for (int j = 0; j < eleReturnOrderLinesList.getLength(); j++) {

                        Element returnOrderorderLineEle = (Element) (eleReturnOrderLinesList)
                            .item(j);

                        Document templateGetCompleteOrderLineDetailsDoc = this
                            .createTemplateForGetCompleteOrderLine();

                        // Below creating a input document to call getCompleteOrderLineDetails API

                        Document inputGetCompleteOrderLineDetailsDoc = SCXmlUtil
                            .createDocument(VSIConstants.ELE_ORDER_LINE_DETAIL);

                        Element inputGetCompleteOrderLineDetailsEle = inputGetCompleteOrderLineDetailsDoc
                            .getDocumentElement();
                        inputGetCompleteOrderLineDetailsEle.setAttribute(
                            VSIConstants.ATTR_ORDER_HEADER_KEY, returnOrderorderLineEle
                                .getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
                        inputGetCompleteOrderLineDetailsEle.setAttribute(
                            VSIConstants.ATTR_ORDER_LINE_KEY,
                            returnOrderorderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));

                        Document outputGetCompleteOrderLineDetailsDoc = this
                            .invokeGetCompleteOrderLineDetailsapi(env,
                                inputGetCompleteOrderLineDetailsDoc,
                                templateGetCompleteOrderLineDetailsDoc,
                                "getCompleteOrderLineDetails");

                        Element outputGetCompleteOrderLineDetailsEle = outputGetCompleteOrderLineDetailsDoc
                            .getDocumentElement();

                        String sreturnlineStatus = outputGetCompleteOrderLineDetailsEle
                            .getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);

                        String sreturnlineStatusAfterReplace = sreturnlineStatus.trim()
                            .replace(".ex", "");

                        double returnlineStatus = 0.00;

                        returnlineStatus = Double.parseDouble(sreturnlineStatusAfterReplace);

                        if (returnlineStatus > 3700) {

                          returnOrderElements.add(returnOrderorderLineEle);

                        }

                        returnOrderorderLineEle.setAttribute(VSIConstants.ATTR_STATUS,
                            outputGetCompleteOrderLineDetailsEle
                                .getAttribute(VSIConstants.ATTR_STATUS));

                        returnOrderorderLineEle.setAttribute(VSIConstants.ATTR_ORD_QTY,
                            outputGetCompleteOrderLineDetailsEle
                                .getAttribute(VSIConstants.ATTR_ORD_QTY));

                        returnOrderorderLineEle.setAttribute(VSIConstants.ATTR_MIN_LINE_STATUS,
                            outputGetCompleteOrderLineDetailsEle
                                .getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS));

                        returnOrderorderLineEle.setAttribute(VSIConstants.ATTR_RECEIVED_QTY,
                            outputGetCompleteOrderLineDetailsEle
                                .getAttribute(VSIConstants.ATTR_RECEIVED_QTY));

                        returnOrderorderLineEle.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO,
                            outputGetCompleteOrderLineDetailsEle
                                .getAttribute(VSIConstants.ATTR_PRIME_LINE_NO));
                        returnOrderorderLineEle.setAttribute(VSIConstants.ATTR_SUB_LINE_NO,
                            outputGetCompleteOrderLineDetailsEle
                                .getAttribute(VSIConstants.ATTR_SUB_LINE_NO));

                        double returnlineOrdQty = SCXmlUtil.getDoubleAttribute(
                            outputGetCompleteOrderLineDetailsEle, VSIConstants.ATTR_ORD_QTY, 0.00);
                        double returnlineReceivedQty = SCXmlUtil.getDoubleAttribute(
                            outputGetCompleteOrderLineDetailsEle, VSIConstants.ATTR_RECEIVED_QTY,
                            0.00);

                        returnOrderLineOrderQty = returnOrderLineOrderQty + returnlineOrdQty;

                        returnOrderLineReceivedQty = returnOrderLineReceivedQty
                            + returnlineReceivedQty;

                      }

                      orderLineEle.setAttribute(VSIConstants.ATTR_RETURN_CREATED,
                          Double.toString(returnOrderLineOrderQty).trim().replace(".00", "")
                              .replace(".0", ""));

                      orderLineEle.setAttribute(VSIConstants.ATTR_RECEIVED_QTY,
                          Double.toString(returnOrderLineReceivedQty).trim().replace(".00", "")
                              .replace(".0", ""));

                      double salesOrdShipQty = SCXmlUtil.getDoubleAttribute(
                          orderLineEle, VSIConstants.ATTR_SHIPPED_QTY, 0.00);
                      
                      double salesOrdReceivableQty = salesOrdShipQty - returnOrderLineReceivedQty;
                      
                      orderLineEle.setAttribute(VSIConstants.ATTR_RETURNABLE_QTY,
                          Double.toString(salesOrdReceivableQty).trim().replace(".00", "")
                              .replace(".0", ""));
                      
                      for (Element ele : returnOrderElements) {
                        ele.getParentNode().removeChild(ele);

                      }

                    }

                  }else{
                	  
                	  // Add logic for wholesale orders here
                  }

                }

        //      } **/

            }

          }

        }

        //for (Element e : targetElements) {
         // e.getParentNode().removeChild(e);

        //}

      }

    }

    return outputDocGetCompleteOrder;

  }

  /**
   * This method is used to invoke API getCompleteOrderDetails .
   * 
   */

  private Document invokeGetCompleteOrderLineDetailsapi(YFSEnvironment env,
      Document docCreateCompleteOrderInput, Document docCreateCompleteOrderTemplate, String apiName)
      throws YFSException, RemoteException, YIFClientCreationException {

    Document outputDocApi = VSIUtils.invokeAPI(env, docCreateCompleteOrderTemplate, apiName,
        docCreateCompleteOrderInput);

    return outputDocApi;
  }

  /**
   * This method is used to invoke API getCompleteOrderDetails .
   * 
   * @param Document
   *          inputDoc as input. Below is the sample Input of getCompleteOrderDetails API
   * 
   *          <Order OrderHeaderKey="" />
   *
   * @return Output of getCompleteOrderDetails API.
   * @throws YIFClientCreationException
   * @throws RemoteException
   * @throws YFSException
   * 
   * 
   * 
   * @throws TransformerException
   * 
   * 
   */

  private Document invokeGetCompleteOrder(YFSEnvironment env, Document docCreateCompleteOrderInput,
      Document docCreateCompleteOrderTemplate, String apiName)
      throws YFSException, RemoteException, YIFClientCreationException {

    Document outputDocApi = VSIUtils.invokeAPI(env, docCreateCompleteOrderTemplate, apiName,
        docCreateCompleteOrderInput);

    return outputDocApi;
  }

  /**
   * This method is used to invoke API getOrderList .
   * 
   * @param Document
   *          inputDoc as input. Below is the sample Input of getOrderList API
   * 
   *          <Order> <OrderLine> <DerivedFrom OrderHeaderKey=""/> </OrderLine> </Order>
   *
   * @return Output of getOrderList API.
   * @throws YIFClientCreationException
   * @throws RemoteException
   * @throws YFSException
   * 
   * 
   * 
   * @throws TransformerException
   * 
   * 
   */

  private Document invokeGetOrderList(YFSEnvironment env,
      Document docCreateGetOrderListInputForSale, Document docCreateGetOrderListTemplateForSale,
      String apiName) throws YFSException, RemoteException, YIFClientCreationException {

    Document outputDocApi = VSIUtils.invokeAPI(env, docCreateGetOrderListTemplateForSale, apiName,
        docCreateGetOrderListInputForSale);

    return outputDocApi;
  }

  /**
   * This method is used to create the template for the API getCompleteOrderDetails .
   * 
   */

  private Document createTemplateForGetCompleteOrder() {

    // Create Template Document for getCompleteOrderDetails.
	  
    Document templateGetCompleteOrderDetails = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
    Element templateGetCompleteOrderDetailsele = templateGetCompleteOrderDetails
        .getDocumentElement();
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_ORDER_NO, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_ORDER_TYPE, "");

    // Create Order Lines Child element
    Element templateOrderLinesele = SCXmlUtil.createChild(templateGetCompleteOrderDetailsele,
        VSIConstants.ELE_ORDER_LINES);

    // Create Order Line Child element
    Element templateOrderLineele = SCXmlUtil.createChild(templateOrderLinesele,
        VSIConstants.ELE_ORDER_LINE);
    templateOrderLineele.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_MAX_LINE_STATUS, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_MIN_LINE_STATUS, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_MAX_LINE_STATUS_DESC, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_ORD_QTY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_ORIGINAL_ORDERED_QTY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_RECEIVED_QTY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_REMAINING_QTY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_SHIPPED_QTY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_STATUS_QTY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_STATUS, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_RETURNABLE_QTY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_DERIVED_FROM_ORDER_HEADER_KEY, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_SUB_LINE_NO, "");
    templateOrderLineele.setAttribute(VSIConstants.ATTR_SHIP_NODE, "");

    // Create Order Line Item Details Child element
    Element templateOrderLineItemele = SCXmlUtil.createChild(templateOrderLineele,
        VSIConstants.ELE_ITEM_DETAILS_INFO);
    templateOrderLineItemele.setAttribute(VSIConstants.ATTR_ITEM_ID, "");

    // Create Primary information child element under Item details
    Element templateOrderLineItemPrimele = SCXmlUtil.createChild(templateOrderLineItemele,
        VSIConstants.ELE_PRIMARY_INFORMATION);

    templateOrderLineItemPrimele.setAttribute(VSIConstants.ATTR_EXTENDED_DSIPLAY_ITEM_DESC, "");

    //if (!documentType.equals("0003")) {

      // Create Order Lines - Return Order lines Child element
      Element templateOrderLineReturnOrderlinesele = SCXmlUtil.createChild(templateOrderLineele,
          VSIConstants.ELE_RETURN_ORDER_LINES);

      // Create Order Lines - Return Order lines Child element
      Element templateOrderLineReturnOrderlinele = SCXmlUtil
          .createChild(templateOrderLineReturnOrderlinesele, VSIConstants.ELE_ORDER_LINE);
      templateOrderLineReturnOrderlinele.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, "");
      templateOrderLineReturnOrderlinele.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, "");

    //}
      if(LOG.isDebugEnabled()) {
    	  LOG.debug("VSIGetOrderLines.createTemplateForGetCompleteOrder - Complete Order Details Template "+XMLUtil.getXMLString(templateGetCompleteOrderDetails));
      }
    return templateGetCompleteOrderDetails;
  }

  /**
   * This method is used to create the input for the API getCompleteOrderDetails .
   * 
   * @param Document
   *          inputDoc as input.
   *
   * @return input to getCompleteOrderDetails API. Below is the sample Input of
   *         getCompleteOrderDetails API
   * 
   *         <Order OrderHeaderKey="" />
   * 
   * @throws TransformerException
   * 
   * 
   */

  private Document createInputForGetCompleteOrder(Document inputDoc , String documentType, Boolean isWholesale) throws TransformerException {

    LOG.beginTimer("VSIGetOrderLines.createInputForGetCompleteOrder");
    if(LOG.isDebugEnabled()){
    	LOG.debug(" *** Entering Method : createInputForGetCompleteOrder Input Doc is: \n "
    			+ XMLUtil.getXMLString(inputDoc));
    }
    
    // Create input Document for GetCompleteOrder.
    Document docInputForGetCompleteOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
    
    // For wholesale returns, return the order lines for the input order no.
    /**if(isWholesale && documentType.equals(VSIConstants.RETURN_DOCUMENT_TYPE)){

    	// get the Order tag
    	Element eleOrder = (Element) XPathAPI.selectSingleNode(inputDoc,
    			VSIConstants.XPATH_ORDERLIST_ORDER);

    	// Add the attributes.
    	Element inputElementGetCompleteOrder = docInputForGetCompleteOrder.getDocumentElement();
    	inputElementGetCompleteOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
    			eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    }
    
    else*/ if (!documentType.equals("0003")) {

    // get the Order tag
    Element eleOrder = (Element) XPathAPI.selectSingleNode(inputDoc,
        VSIConstants.XPATH_ORDERLIST_ORDER);

    // Add the attributes.
    Element inputElementGetCompleteOrder = docInputForGetCompleteOrder.getDocumentElement();
    inputElementGetCompleteOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    } else {
      
      // get the Order tag
      Element eleOrderListOrderLines = (Element) XPathAPI.selectSingleNode(inputDoc,
          VSIConstants.XPATH_ORDERLIST_ORDER_ORDERLINES);
      
      NodeList eleOrderLinesList = eleOrderListOrderLines.getElementsByTagName("OrderLine");
      
      Element eleOrderLine = (Element) (eleOrderLinesList).item(0);
      
      // Add the attributes.
      Element inputElementGetCompleteOrder = docInputForGetCompleteOrder.getDocumentElement();
      inputElementGetCompleteOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
          eleOrderLine.getAttribute(VSIConstants.ATTR_DERIVED_FROM_ORDER_HEADER_KEY));
      
    }
    
    if(LOG.isDebugEnabled()){
    	LOG.debug(" *** Entering Method : createInputForGetCompleteOrder output Doc is: \n "
    			+ XMLUtil.getXMLString(docInputForGetCompleteOrder));
    }

    return docInputForGetCompleteOrder;

  }

  /**
   * This method is used to create the input for the API getOrderList .
   * 
   * @param Document
   *          inputDoc as input.
   *
   * @return input to getOrderList API. Below is the sample Input of getCompleteOrderDetails API
   * 
   *         <Order> <OrderLine> <DerivedFrom OrderHeaderKey=""/> </OrderLine> </Order>
   * 
   * @throws TransformerException
   * 
   * 
   */

  private Document createInputForGetOrderListForSales(Document inputDoc)
      throws TransformerException {

    LOG.beginTimer("VSIGetOrderLines.createInputForGetOrderListForSales");
    if(LOG.isDebugEnabled()){
    	LOG.debug(" *** Entering Method : createInputForGetOrderListForSales Input Doc is: \n "
    			+ XMLUtil.getXMLString(inputDoc));
    }

    // get the Order tag
   // Element eleOrder = (Element) XPathAPI.selectSingleNode(inputDoc,
     //   VSIConstants.XPATH_ORDERLIST_ORDER);

    // Create input Document for GetOrderList.
    Document docInputForGetOrderListForSales = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);

    // Add create child Orderline
    Element OrderLineele = SCXmlUtil.createChild(
        docInputForGetOrderListForSales.getDocumentElement(), VSIConstants.ELE_ORDER_LINE);

    // Add create child DerivedFrom
    Element OrderLineDerivedFromele = SCXmlUtil.createChild(OrderLineele,
        VSIConstants.ELE_DERIVED_FROM);

    // Add the attributes HeaderKey
    OrderLineDerivedFromele.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        inputDoc.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));

    return docInputForGetOrderListForSales;

  }

  /**
   * This method is used to create the template for the API getOrderList .
   * 
   * 
   *
   * @return template to getOrderList API. Below is the sample template of getOrderList API
   * 
   *         <OrderList TotalNumberOfRecords=""> <Order OrderHeaderKey="" OrderNo="" Status=""
   *         DraftOrderFlag="" /> </OrderList>
   * 
   * @throws TransformerException
   * 
   * 
   */

  private Document createTemplateForGetOrderListForSales() throws TransformerException {

    LOG.beginTimer("VSIGetOrderLines.createTemplateForGetOrderListForSales");
    if(LOG.isDebugEnabled()){
    	LOG.debug(" *** Entering Method : createTemplateForGetOrderListForSales Input Doc is: \n ");
    }
    
    // Create template Document for getOrderList .
    Document docTemplateForGetOrderListForSales = SCXmlUtil
        .createDocument(VSIConstants.ELE_ORDER_LIST);
    docTemplateForGetOrderListForSales.getDocumentElement()
        .setAttribute(VSIConstants.ATTR_TOTAL_NUMBER_OF_RECORDS, "");

    // Add create child Order
    Element Orderele = SCXmlUtil.createChild(
        docTemplateForGetOrderListForSales.getDocumentElement(), VSIConstants.ELE_ORDER);

    // Set the attributes
    Orderele.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, "");
    Orderele.setAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG, "");

    return docTemplateForGetOrderListForSales;

  }

  /**
   * This method is used to create the template for the API getCompleteOrderDetails .
   * 
   */

  private Document createTemplateForGetCompleteOrderLine() {

    // Create Template Document for getCompleteOrderDetails.
    Document templateGetCompleteOrderLineDetails = SCXmlUtil
        .createDocument(VSIConstants.ELE_ORDER_LINE);
    Element templateGetCompleteOrderDetailsele = templateGetCompleteOrderLineDetails
        .getDocumentElement();

    // Setting Attributes

    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_MAX_LINE_STATUS, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_MIN_LINE_STATUS, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_MAX_LINE_STATUS_DESC, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_ORD_QTY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_ORIGINAL_ORDERED_QTY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_RECEIVED_QTY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_REMAINING_QTY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_SHIPPED_QTY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_STATUS_QTY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_STATUS, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_RETURNABLE_QTY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_DERIVED_FROM_ORDER_HEADER_KEY,
        "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO, "");
    templateGetCompleteOrderDetailsele.setAttribute(VSIConstants.ATTR_SUB_LINE_NO, "");

    return templateGetCompleteOrderLineDetails;
  }

}
