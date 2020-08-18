package com.vsi.oms.api.order;


import java.util.HashMap;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;





import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


/**
* @author ashish.keshri
*
*/

public class VSIESWorkOrderList {
                private YFCLogCategory log = YFCLogCategory.instance(VSIESWorkOrderList.class);
                YIFApi api;
                public Document vsiESWorkOrderList(YFSEnvironment env, Document inXML)
                                                throws Exception {
                                
                	if(log.isDebugEnabled()){
                		log.info("================Inside vsiESWorkOrderList================================");
                		log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
                	}
                                Document vsiESOrderWorkListOutput = XMLUtil.createDocument("OrderList");//Creating OrderListdoc
                                Element eleOrderList = vsiESOrderWorkListOutput.getDocumentElement();
                               
                        	           Element rootElement = inXML.getDocumentElement();
                        	           
                        	           // Getting the value of ShipNode coming in the Input
                        	           Element ordLine = (Element)inXML.getElementsByTagName("OrderLine").item(0);
                        	           
                        	           if (null != ordLine && !VSIUtils.isNullOrEmpty(ordLine.getAttribute("ShipNode"))) {
                                	        	                                   	           
                                	           rootElement.setAttribute("FromStatus","1100.200");
											   rootElement.setAttribute("StatusQryType","BETWEEN");
                                	           rootElement.setAttribute("DocumentType", "0001");
                                	           rootElement.setAttribute("DraftOrderFlag", "N");
                                	           rootElement.setAttribute("MaximumRecords", "500");
                                	           rootElement.setAttribute("ReadFromHistory", "N");
                                	           
                                	    try{
                                	        	   
                                                env.setApiTemplate("getOrderList", "/global/template/api/VSIGetOrderList.xml");
                                                YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
                                                
                                                Document outDoc = callApi.invoke(env,"getOrderList",inXML);
                                                env.clearApiTemplate("getOrderList");

                                                /* Changes done for getting the list of Orders in Restock and appending it to the OutPut document of above getOrderList */
                                                
                                               // Creating input for getOrderList API for getting the List of orders which are in Restock status
                                        		Document docRestock = XMLUtil.createDocument("Order");
                                        		Element eleRootElement = docRestock.getDocumentElement();
                                        		eleRootElement.setAttribute("DocumentType", "0001");
                                        		eleRootElement.setAttribute("DraftOrderFlag", "N");
                                        		eleRootElement.setAttribute("MaximumRecords", "500");
                                        		eleRootElement.setAttribute("ReadFromHistory", "N");
                                        		eleRootElement.setAttribute("Status", "9000.100");
                                        		Element eleOrderLine = docRestock.createElement("OrderLine");
                                        		eleRootElement.appendChild(eleOrderLine);
                                        		
                                        		// Getting the value of ShipNode coming in the Input
                                        		Element eleInputOrderLine = (Element) rootElement.getElementsByTagName("OrderLine").item(0);
                                        		String sShipNode = eleInputOrderLine.getAttribute("ShipNode");
                                        		                                        		
                                        		//Setting the ShipNode Value
                                        		eleOrderLine.setAttribute("ShipNode", sShipNode);
                                        		
                                        		env.setApiTemplate("getOrderList",
                                        		"/global/template/api/VSIGetOrderList.xml");

                                        		Document docRestockOut = callApi.invoke(env, "getOrderList", docRestock);
                                        		env.clearApiTemplate("getOrderList");
                                        		
                                        		//importing all the Order nodes to 1st getOrderList output doc
                                        		                                        		
                                        		Element eleRootDicRestockOut = docRestockOut.getDocumentElement();
                                        		NodeList nlOrder = eleRootDicRestockOut.getElementsByTagName("Order");
                                        		int iLength = nlOrder.getLength();
                                        
                                        		for(int i=0;i<iLength;i++){
                                        			
                                        			Node nOrder = nlOrder.item(i);
                                        			Node nCopy = outDoc.importNode(nOrder, true);
                                        			outDoc.getDocumentElement().appendChild(nCopy);
                                        		}
                                        		
                                          /* ****************Changes End*********** */
                                                
                                                NodeList orderList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER);
                                                int iOrderLength = orderList.getLength();
                                                                                             
                                                for(int i=0;i<iOrderLength;i++){
                                                                Element OrderElement = (Element)orderList.item(i);
                                                                String sCustomerFirstName = OrderElement.getAttribute(VSIConstants.ATTR_CUSTOMER_FIRST_NAME);
                                                                String sCustomerLastName = OrderElement.getAttribute(VSIConstants.ATTR_CUSTOMER_LAST_NAME);
                                                                String sOrderedDate = OrderElement.getAttribute(VSIConstants.ATTR_ORDER_DATE);
                                                                String sSellingOutletID = OrderElement.getAttribute(VSIConstants.ATTR_SELLING_OUTLET);
                                                                NodeList orderLineList = OrderElement.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
                                                                int iLineLength = orderLineList.getLength();
                                                                HashMap<String, String> orderTotalKey = new HashMap<String, String>();
                                                                HashMap<String, String> orderStatusKey = new HashMap<String, String>();
                                                                HashMap<String, String> orderLineTypeKey = new HashMap<String, String>();
                                                                for(int j=0;j<iLineLength;j++){
                                                                                Element OrderLineElement = (Element) OrderElement.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(j);
                                                                                Element LinePriceInfoElement = (Element) OrderElement.getElementsByTagName(VSIConstants.ELE_LINE_PRICE).item(j);
                                                                                String sLineType = OrderLineElement.getAttribute(VSIConstants.ATTR_LINE_TYPE);
                                                                                if ((sLineType.equalsIgnoreCase(VSIConstants.LINETYPE_STS))||(sLineType.equalsIgnoreCase(VSIConstants.LINETYPE_PUS))){ //for Line type SHIP_TO_HOME, no action required
                                                                                                String sCustPoNo = OrderLineElement.getAttribute("CustomerPONo"); //getting CustomerPO No for first line
                                                                                                String sLineTotal = LinePriceInfoElement.getAttribute(VSIConstants.ATTR_LINE_TOTAL); //getting Line total for the line
                                                                                                String sLineStatus = OrderLineElement.getAttribute(VSIConstants.ATTR_STATUS);
                                                                                                orderLineTypeKey.put(sCustPoNo, sLineType);
                                                                                                                if (!orderTotalKey.containsKey(sCustPoNo)){
                                                                                                                                orderTotalKey.put(sCustPoNo, sLineTotal);
                                                                                                                                } else
                                                                                                                                {
                                                                                                                String stLineTotal=orderTotalKey.get(sCustPoNo);
                                                                                                                Double dtLineTotal = Double.parseDouble(stLineTotal);
                                                                                                                Double dLineTotal = Double.parseDouble(sLineTotal);
                                                                                                                Double dSumLineTotal = dtLineTotal + dLineTotal;
                                                                                                                sLineTotal = String.valueOf(dSumLineTotal);
                                                                                                                orderTotalKey.put(sCustPoNo, sLineTotal);
                                                                                                                                }
                                                                                                                if((sLineStatus.equalsIgnoreCase(VSIConstants.STATUS_PICKED))||(sLineStatus.equalsIgnoreCase(VSIConstants.STATUS_RESTOCK))||(sLineStatus.equalsIgnoreCase(VSIConstants.STATUS_NOACTION))||(sLineStatus.equalsIgnoreCase(VSIConstants.STATUS_ACKNOWLEDGE))||(sLineStatus.equalsIgnoreCase(VSIConstants.STATUS_AWAIT_CUST)) ||(sLineStatus.equalsIgnoreCase(VSIConstants.STATUS_STORE_ACK)))
																														{ //Check for Status
                                                                                                                                if (!orderStatusKey.containsKey(sCustPoNo)){
                                                                                                                                orderStatusKey.put(sCustPoNo, sLineStatus);
                                                                                                                                } 
                                                                                                                                else {
                                                                                                                                                String stLineStatus=orderStatusKey.get(sCustPoNo);
                                                                                                                                                if(!sLineStatus.equalsIgnoreCase(stLineStatus)){ //Check for partial status in an Order
                                                                                                                                                                orderStatusKey.put(sCustPoNo,"StatusMismatch"); 

                                                                                                                }                                                                                                                              
                                                                                                }
                                                                                                
                                                                                }
                                                                                                                else if(!sLineStatus.equalsIgnoreCase(VSIConstants.STATUS_CANCELLED)){
                                                                                                                                orderStatusKey.put(sCustPoNo,"StatusMismatch"); 
                                                                                                                }
                                                                                }
                                                                }
                                                                //int iHasMapSize = orderKey.size();
                                                                for(String key: orderStatusKey.keySet()){
                                                                                String stStatus = orderStatusKey.get(key);
                                                                                if(!stStatus.equalsIgnoreCase("StatusMismatch")){
                                                                                Element eleOrder = vsiESOrderWorkListOutput.createElement("Order"); //Creating Order Element
                                                                                eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO,key);
                                                                                eleOrder.setAttribute(VSIConstants.ATTR_ORDER_TOTAL,orderTotalKey.get(key));
                                                                                eleOrder.setAttribute(VSIConstants.ATTR_STATUS,orderStatusKey.get(key));
                                                                                eleOrder.setAttribute(VSIConstants.ATTR_LINE_TYPE,orderLineTypeKey.get(key));
                                                                                eleOrder.setAttribute(VSIConstants.ATTR_CUSTOMER_FIRST_NAME,sCustomerFirstName);
                                                                                eleOrder.setAttribute(VSIConstants.ATTR_CUSTOMER_LAST_NAME,sCustomerLastName);
                                                                                eleOrder.setAttribute(VSIConstants.ATTR_ORDER_DATE,sOrderedDate);
                                                                                eleOrder.setAttribute("SellingDivisionID","0"); 
                                                                                eleOrder.setAttribute(VSIConstants.ATTR_SELLING_OUTLET,sSellingOutletID); 
                                                                                eleOrderList.appendChild(eleOrder);//Append child to OrderList
                                                                                }
                                                                }
                                                                
                                                                                                
                                                                                
                                                                }
                                                                
                                	           }                                
                                	           catch (Exception e){
                                                   e.printStackTrace();
                                               }
               
                                                                
                    	           		}
            	           				else {
            	           					throw new YFSException(
            	           							"EXTN_ERROR",
            	           							"INVALID_SHIPNODE",
            	           					"Ship Node cannot be blank");
            	           				}
                                                    
                                                    
                                    
                                

                
                                
                                return vsiESOrderWorkListOutput;
                }
                
}
