package com.vsi.scc.oms.pca.extensions.returnOrderSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCExtendedCellModifier;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTblClmBindingData;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class ReturnOrderSummaryWizardExtensionBehavior extends YRCWizardExtensionBehavior {
	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.returnSummary.wizards.YCDReturnOrderSummaryWizard";
	private YRCExtendedCellModifier tblCellModifier1 = null;
	OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
	XMLSerializer serializer = new XMLSerializer(System.out,of);
	public IYRCComposite createPage(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void pageBeingDisposed(String arg0) {
		// TODO Auto-generated method stub
		super.initPage(arg0);	
	}
    
	public void initPage(String arg0) {
		// TODO Auto-generated method stub
		super.initPage(arg0);		
	}
	
	public void postSetModel(String arg0) {
		// TODO Auto-generated method stub
		super.postSetModel(arg0);
		
		if(arg0.equals("getCompleteOrderDetails_output")){
			
			Element eleRODetails = getModel("getCompleteOrderDetails_output");
			
			String orderStatus = eleRODetails.getAttribute("Status");
			if(eleRODetails!= null && ((orderStatus.equalsIgnoreCase("Partially Receipt Closed") 
					||orderStatus.equalsIgnoreCase("Partially Received")))){
				
			
			
				
				NodeList nlROOrderLines = eleRODetails.getElementsByTagName("OrderLine");
				String ohk = eleRODetails.getAttribute("OrderHeaderKey");
				if (getExtentionModel("extn_getReceiptLineList_output") == null) {
					callGetReceiptLineList(ohk);
				}
				 Element receiptLineList = getExtentionModel("extn_getReceiptLineList_output");
				 
				 Document receiptLineListDoc = receiptLineList.getOwnerDocument();
		        
				 
				 
		          if (receiptLineList != null) {
		        	  
		        	  NodeList nlReceiptOrderLineList = receiptLineList.getElementsByTagName("OrderLine");
		        	  NodeList nlReceiptLineList = null;
		        	 String strOrdrdQty = null;
		        	 HashMap receivableOrderLinesMap = new HashMap();
		        	 HashMap receivedOrderLinesMap = new HashMap();
		        	 double receivedQty = 0;
		        	 String attrOrderLineKey = null;
		        	 String strXPATHOrderLineKey = null;
		        	 //System.out.println("** nlReceiptOrderLineList length     "+nlReceiptOrderLineList.getLength());
		        	 
		        	  for (int i = 0; i < nlReceiptOrderLineList.getLength(); i++) {
		        		  Element eleOrderLine = (Element)nlReceiptOrderLineList.item(i);
		        		  attrOrderLineKey = eleOrderLine.getAttribute("OrderLineKey");
		        		  if (eleOrderLine.getAttribute("Status").equalsIgnoreCase("Partially Receipt Closed") && 
		        				  !receivableOrderLinesMap.containsKey(attrOrderLineKey)) {
		        			//strOrdrdQty = eleOrderLine.getAttribute("OrderedQty");
		        			 
		        			  strXPATHOrderLineKey = VSIXmlUtils
			      				.formXPATHWithOneCondition(
			      						"/ReceiptLineList/ReceiptLine",
			      						"OrderLineKey", attrOrderLineKey);
		        			  
		        			  try {
									nlReceiptLineList = VSIXmlUtils.getNodeListByXpath(
											  receiptLineListDoc, strXPATHOrderLineKey);
									
										for(int k=0;k < nlReceiptLineList.getLength();k++){
											Element eleReceiptLine = (Element) nlReceiptLineList.item(k);
											
											if(receivableOrderLinesMap.size() > 0 && receivableOrderLinesMap.containsKey(attrOrderLineKey)){
							        			  receivedQty = Double.parseDouble(receivableOrderLinesMap.get(attrOrderLineKey).toString());
							        			  }
											receivedQty = receivedQty + Double.parseDouble(eleReceiptLine.getAttribute("Quantity"));
							        			  receivableOrderLinesMap.put(attrOrderLineKey, receivedQty);
							        			  receivedQty = 0;
											
										}
										
										
									
								} catch (ParserConfigurationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (TransformerException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
		        		  }
		        	  }
		        			  
		        			
		        			  
		        		  
		        	  
		        	  if(receivableOrderLinesMap.size() > 0){
		        		  NodeList returnLineNL = eleRODetails.getElementsByTagName("OrderLine");
		        		 String returnOrderLineKey = null;
		        		  double receivableQty = 0;
		        		  //System.out.println("** returnLineNL  length  "+returnLineNL.getLength());
		        		  int returnLineLength = returnLineNL.getLength();
		        		  for(int k = 0; k < returnLineLength;k++){
		        			  Element returnOrderLine = (Element) returnLineNL.item(k);
		        			 // System.out.println("** Before printing returnOrderLine    "+VSIXmlUtils.getElementXMLString(returnOrderLine));
		        			  returnOrderLineKey = returnOrderLine.getAttribute("OrderLineKey");
		        			  if(receivableOrderLinesMap.containsKey(returnOrderLineKey)){
		        				 receivableQty = Double.parseDouble(returnOrderLine.getAttribute("OrderedQty")) - 
		        				  Double.parseDouble(receivableOrderLinesMap.get(returnOrderLineKey).toString());
		        				  returnOrderLine.setAttribute("OrderedQty",String.valueOf(receivableQty));
		        				  
		        				  
		        			  }else{
		        				  receivedOrderLinesMap.put(returnOrderLineKey, "");
		        			  }
		        			  
		        			  
		        			  
		        		  }
		        		  Iterator itr = receivedOrderLinesMap.entrySet().iterator();
		        		  String conditionXPath = null;
		        		  NodeList orderLineNL = null;
		        		  Element orderLineEle = null;
		        		  Element orderLinesEle = (Element) eleRODetails.getElementsByTagName("OrderLines").item(0);
		        		  while (itr.hasNext()) {
		        		        Map.Entry pairs = (Map.Entry)itr.next();
		        		        //System.out.println(pairs.getKey() + " = " + pairs.getValue());
		        		        conditionXPath = VSIXmlUtils
			      				.formXPATHWithOneCondition(
			      						"/Order/OrderLines/OrderLine",
			      						"OrderLineKey",pairs.getKey().toString());
		        		        try {
		        		        	orderLineNL = VSIXmlUtils.getNodeListByXpath(
											  eleRODetails.getOwnerDocument(), conditionXPath);
								} catch (ParserConfigurationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TransformerException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		        		        
								orderLineEle = (Element) orderLineNL.item(0);
								
								orderLinesEle.removeChild(orderLineEle);
		        		        itr.remove(); 
		        		    }
		        		 
		        		
		        		  	  
		                  
		        	  }
		          
		        
		          //setExtentionModel("extn_ReturnOrderOutputModel",eleRODetails);
		          
		         // setDirty(false);
		          //repopulateModel("getCompleteOrderDetails_output");
			  	}
			
				}
			
			setExtentionModel("extn_ReturnOrderOutputModel",eleRODetails);
			
			setDirty(false);
		
	}
	}
	 public YRCExtendedTableBindingData getExtendedTableBindingData(String tableName, ArrayList tableColumnNames) {
			
		    Element E = getModel("getCompleteOrderDetails_output");
		    Element orderLineElem = null;
			String sItemIsInHand=null;
			if(E!=null){
			NodeList orderLineList = E.getElementsByTagName("OrderLine");
			for(int i=0;i<orderLineList.getLength();i++){
				orderLineElem = (Element)orderLineList.item(i);
				if(!YRCPlatformUI.isVoid(orderLineElem))
					sItemIsInHand = orderLineElem.getAttribute("ItemIsInHand");
			}
			}
			
			if (tableName.equalsIgnoreCase("itemOrderLine")) {
				YRCExtendedTableBindingData extendedTableBindingData = new YRCExtendedTableBindingData("tblOrderLines");			
				//extendedTableBindingData.
				tblCellModifier1 = new TblExtCellModifier();
				
				HashMap tableClmBindingDataMap = new HashMap();		
				if(tableColumnNames.contains("extn_clmQtytoAdd")) {			 
				YRCTblClmBindingData clmDataExtn = new YRCTblClmBindingData();
	            if("N".equals(sItemIsInHand)){
	            clmDataExtn.setVisible(false);
				}else{
				clmDataExtn.setAttributeBinding("@QtyToAdd");
				clmDataExtn.setTargetAttributeBinding("@QtyToAdd");
				clmDataExtn.setName("RO_Qty_To_Add");
				clmDataExtn.setColumnBinding("QtyToAdd");
			    }	
				//clmData4.setCellEditorTheme(YRCConstants.YRC_TEXT_BOX_CELL_EDITOR);
	         	tableClmBindingDataMap.put(tableColumnNames.get(0),clmDataExtn);
				extendedTableBindingData.setTableColumnBindingsMap(tableClmBindingDataMap);			
				}
				extendedTableBindingData.setCellModifier(tblCellModifier1);
				HashMap hmCellType=new HashMap();
				hmCellType.put("extn_clmQtytoAdd", YRCConstants.YRC_TEXT_BOX_CELL_EDITOR);
				extendedTableBindingData.setCellTypes(hmCellType);
				return extendedTableBindingData;
			}
		    
			return super.getExtendedTableBindingData(tableName, tableColumnNames);
		}
	  
	  
	  public class TblExtCellModifier extends YRCExtendedCellModifier {
			public boolean allowModify(String property, String value,Element element) {
				double dblQtyToAdd=YRCXmlUtils.getDoubleAttribute(element, "ReturnableQuantity");
				if(dblQtyToAdd!=0)
					return true;
				else
					return false;
			}

			public String getModifiedValue(String property, String value, Element element) {			
				return value;
			}
			
			public YRCValidationResponse validateModifiedValue(String property, String value, Element element) {
				YRCValidationResponse yrcValidationResponse = new YRCValidationResponse();
				if(property.equalsIgnoreCase("@QtyToAdd")) {
					YRCPlatformUI.setMessage("");
					removeFromError(element, "@QtyToAdd");
					 double dblReturnableQty=YRCXmlUtils.getDoubleAttribute(element, "ReturnableQuantity") ;
			        double val=Double.parseDouble(value);
			        if(!(val<=dblReturnableQty)){
			        	addToError(element, "@QtyToAdd", YRCPlatformUI.getFormattedString("Entered Quantity is greater than the returnable quantity",value));
			        	return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR, YRCPlatformUI.getFormattedString("Entered Quantity is greater than the returnable quantity",value));
			        }else if(val<1){
			        	addToError(element, "@QtyToAdd", YRCPlatformUI.getFormattedString("Entered Quantity should be an integer",value));
			        	return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR, YRCPlatformUI.getFormattedString("Entered Quantity should be an integer",value));
			        
			        }
			   }
				if(property.equalsIgnoreCase("@OrderedQty")) {
					//System.out.println("Inside check for Qty");
					YRCPlatformUI.setMessage("");
					removeFromError(element, "@OrderedQty");
					 double dblReturnableQty=YRCXmlUtils.getDoubleAttribute(element, "OrderedQty") ;
			        double val=Double.parseDouble(value);
			        if(!(val<=dblReturnableQty)){
			        	yrcValidationResponse.setStatusCode(YRCValidationResponse.YRC_VALIDATION_ERROR);
			        	addToError(element, "@OrderedQty", YRCPlatformUI.getFormattedString("Entered Quantity is greater than the returnable quantity",value));
			        	YRCPlatformUI.showError("Error", "Entered Quantity is greater than the returnable quantity");
			        	return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR, YRCPlatformUI.getFormattedString("Entered Quantity is greater than the returnable quantity",value));
			        }else if(val<1){
			        	yrcValidationResponse.setStatusCode(YRCValidationResponse.YRC_VALIDATION_ERROR);
			        	addToError(element, "@OrderedQty", YRCPlatformUI.getFormattedString("Entered Quantity should be an integer",value));
			        	YRCPlatformUI.showError("Error", "Entered Quantity should be an integer");
			        	return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR, YRCPlatformUI.getFormattedString("Entered Quantity should be an integer",value));
			        
			        }
			   }
				Element eOrderDetails = getModel("getCompleteOrderDetails_output");
				setExtentionModel("extn_ReturnOrderOutputModel",eOrderDetails);
				
				yrcValidationResponse.setStatusCode(YRCValidationResponse.YRC_VALIDATION_OK);
		        return yrcValidationResponse;
			}
			
		}
 

		
	public Element getExtentionModel(String arg0) {
		// TODO Auto-generated method stub
		return super.getExtentionModel(arg0);
	}
	@Override
	public void postCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub
		super.postCommand(arg0);
	}
	
	public void handleApiCompletion(YRCApiContext ctxApi) {
		if (ctxApi.getApiName().equals("getReceiptList")) {
			Document receipts = ctxApi.getOutputXml();
			callGetReceiptDetails(receipts);
		}
		if (ctxApi.getApiName().equals("getReceiptDetails")) {
			Document receiptList = ctxApi.getOutputXml();
			setExtentionModel("extn_getReceiptDetails_output", receiptList.getDocumentElement());
			postSetModel("getCompleteOrderDetails_output");
		}
		if (ctxApi.getApiName().equals("getReceiptLineList")) {
			Document receiptLineList = ctxApi.getOutputXml();
			setExtentionModel("extn_getReceiptLineList_output", receiptLineList.getDocumentElement());
			postSetModel("getCompleteOrderDetails_output");
		}
		super.handleApiCompletion(ctxApi);
	}
	
	
	private void callGetReceiptLineList(String ohk) {
		Element receiptLineInputEle = YRCXmlUtils.createFromString("<ReceiptLine />").getDocumentElement();
		receiptLineInputEle.setAttribute("OrderHeaderKey", ohk);
		Document receiptLineInput = receiptLineInputEle.getOwnerDocument();
		
		YRCApiContext context = new YRCApiContext();
		context.setApiName("getReceiptLineList");
		context.setFormId(Wizard_Id);
		context.setInputXml(receiptLineInput);
		callApi(context);
	}
	
	private void callGetReceiptDetails(Document receiptList) {
		// Call getReceiptDetails for each receipt in the list and add the lines to it
		NodeList nl = receiptList.getElementsByTagName("Receipt");
		for (int i = 0; i < nl.getLength(); i++) {
			Element receipt = (Element)nl.item(i);
			String rhk = receipt.getAttribute("ReceiptHeaderKey");
			Element inputEle = YRCXmlUtils.createFromString("<ReceiptDetail />").getDocumentElement();
			inputEle.setAttribute("ReceiptHeaderKey", rhk);
			YRCApiContext context = new YRCApiContext();
			context.setApiName("getReceiptDetails");
			context.setFormId(Wizard_Id);
			context.setInputXml(inputEle.getOwnerDocument());
			callApi(context);
		}
	}
}