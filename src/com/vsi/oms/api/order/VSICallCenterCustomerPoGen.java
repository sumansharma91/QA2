package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient, Inc.
 *
 */
public class VSICallCenterCustomerPoGen {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSICallCenterCustomerPoGen.class);
	YIFApi api;

	 /**
	 * @param env
	 * @param inXML
	 * @return
	 * @throws YFSException
	 */
	public Document vsiCallCenterCustomerPoGen(YFSEnvironment env, Document inXML) throws YFSException
	    {
	    	
	    	int iShip = 0;
	    	int iPick = 0;
	    	
	    	if(log.isDebugEnabled()){
	    		log.verbose("Printing Input XML :" + XmlUtils.getString(inXML));
	    		log.info("================Inside VSICallCenterCustomerPoGen================================");
	    	}
			try {
			
				if(null!=inXML.getDocumentElement() && null!=inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)){
				Element rootElement = inXML.getDocumentElement();
				NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				int orderLineLength = orderLineList.getLength();
				//String orderType = rootElement.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
				String entryType = rootElement.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
			if(null!=rootElement){
				if(entryType.equalsIgnoreCase("Call Center")){
				for(int i=0;i< orderLineLength;i++){
				Element orderLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
				String sLineType = orderLineElement.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				
				if(sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
					
					iShip++;
					//orderLineElement.setAttribute(VSIConstants.ATTR_PROCURE_FROM_NODE,"9001");
					
					
				}else
					iPick++;
				
				}
		if(iShip==0 || iPick==0){
					
				rootElement.setAttribute(VSIConstants.ATTR_ORDER_TYPE, "WEB");
					String generatedCustomerPoNo = generateOrderNo(env,inXML);
					for(int j=0;j<orderLineLength;j++){
						Element orderLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(j);
						orderLineElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO,generatedCustomerPoNo);
						
					}
		}
				
		else {
			String genCustomerPoNo1 = generateOrderNo(env,inXML);
			/*int custPoNo1 = Integer.parseInt(genCustomerPoNo1);
			custPoNo1 = custPoNo1 + 1;
			
			int custPoNo2 = custPoNo1 + 1; 
			
			String shipPoNo = Integer.toString(custPoNo1);
			String PickPoNo = Integer.toString(custPoNo2);	*/
			
			
		String custPoNo1=genCustomerPoNo1;
		String custPoNo2 = genCustomerPoNo1.substring(0, genCustomerPoNo1.length()-1)+ "1";
		
		//System.out.println("custPoNo1"+custPoNo1+"\n custPoNo2"+ custPoNo2);
			
			rootElement.setAttribute(VSIConstants.ATTR_ORDER_TYPE, "WEB");
			
			for(int k=0;k<orderLineLength;k++){
				
				Element orderLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(k);
				String sLineType = orderLineElement.getAttribute(VSIConstants.ATTR_LINE_TYPE);

				if(sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
				
					//System.out.println("SHIP TO STORE");
					orderLineElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO,custPoNo2);
				//	orderLineElement.setAttribute(VSIConstants.ATTR_PROCURE_FROM_NODE,"9001");
					////System.out.println("Printing Input XML AGAIN :" + XmlUtils.getString(inXML));
				}
				else{
				
					//System.out.println("PICK IN STORE");
					orderLineElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO,custPoNo1);
					////System.out.println("Printing Input XML AGAIN :" + XmlUtils.getString(inXML));
					
				
				}
				}//end for
			
		}
				}
				}//end if ordertype check
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new YFSException(
						"EXTN_ERROR",
						"EXTN_ERROR",
						"Exception");
			}
			
			    	return inXML;
	    }

		
	    
	    private String generateOrderNo(YFSEnvironment env, Document inXML) throws Exception {
			Element rootElement = inXML.getDocumentElement();
			String tranDate = rootElement.getAttribute(VSIConstants.ATTR_ORDER_DATE);
			
			
			tranDate = tranDate.replaceAll("\\-", "");//Removing the "-" from the input OrderDate.
			tranDate = tranDate.substring(2,6);//Getting YYMM from the input
			 Element orderLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
	    	 	String shipNode = orderLineElement.getAttribute(VSIConstants.ATTR_SHIP_NODE);//enteredBy
	    	 	String shipNodePadded = ("00000" + shipNode).substring(shipNode.length());//Adding Leading Zeros
	    
						
	    	 	String tranNumber = "";
	    	 	String seqNum ="VSI_SEQ_"+ shipNode;
	    	 	tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
	    	 	String tranNumberPadded = ("00000" + tranNumber).substring(tranNumber.length()); //Adding Leading Zeros
	    	 	String tranNumberCustCustPO = ("000000" + tranNumber).substring(tranNumber.length());
	    	 	if(log.isDebugEnabled()){
	    	 		log.info("=====>Printing Padded Transaction Number=======" + tranNumberPadded);
	    	 	}
	    	 	
		String regNumber = VSIConstants.REG_NUMBER;
		String grpNumber = VSIConstants.GROUP_NUMBER;
		String itemStatusNumber = VSIConstants.LINE_ITEM_STATUS_NUMBER;
		
		String customerPoNo = shipNodePadded + tranDate + regNumber + tranNumberPadded + grpNumber + itemStatusNumber;
		
		//Also need to set another attribute for transaction number TBD for now stored in CustCustPoNo
		rootElement.setAttribute(VSIConstants.ATTR_CUST_CUST_PO_NO, tranNumberCustCustPO);
				
		return customerPoNo;				
		}// end generateOrderNo


}
