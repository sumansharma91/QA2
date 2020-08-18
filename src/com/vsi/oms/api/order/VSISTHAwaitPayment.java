package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc.
 * 
 * This code is called on success of ScheduleOrder. 
 * For Ship to home order
 * create Charge Transaction request
 */
public class VSISTHAwaitPayment {

	private YFCLogCategory log = YFCLogCategory.instance(VSISTHAwaitPayment.class);
	YIFApi api;
	public void vsiSTHAwaitPayment(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
					
		
		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element ordIp = getOrderListInput.getDocumentElement();
		Element rootIp = inXML.getDocumentElement();
		ordIp.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, rootIp.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));		
		
		if(log.isDebugEnabled()){
			log.info("**getOrderList input XML :\n"+XMLUtil.getXMLString(getOrderListInput));
		}
		//System.out.println("**getOrderList input XML :\n"+XMLUtil.getXMLString(getOrderListInput));
		
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/releaseOnSucessTemplate.xml");
		inXML = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
		
		if(log.isDebugEnabled()){
			log.info("**getOrderList output XML :\n"+XMLUtil.getXMLString(inXML));
		}
		//System.out.println("**getOrderList output XML :\n"+XMLUtil.getXMLString(inXML));
		
		Element rootElement = (Element) inXML.getElementsByTagName("Order").item(0);
		String OHK = rootElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		
		NodeList hdrChrsNL = rootElement.getElementsByTagName("HeaderCharge");
		NodeList hdrTaxesNL = rootElement.getElementsByTagName("HeaderTax");

		String custPoNo="";
			
		NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int linelength= orderLineList.getLength();
		int validHomeLines = 0;
		Double maxReqAmt=0.0;
		
		for(int i=0;i<linelength;i++){
			
			Element orderLineEle1 = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
			
			//String strOrderLineKey = orderLineEle1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			
			//String minLineStatus = orderLineEle1.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
			//String maxLineStatus = orderLineEle1.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
			String lineType= orderLineEle1.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			//if (minLineStatus.equals("3200") && maxLineStatus.equals("3200") && lineType.equalsIgnoreCase("SHIP_TO_HOME")){
			if (lineType.equalsIgnoreCase("SHIP_TO_HOME")){
				
				Element linePriceInfoEle = (Element) orderLineEle1.getElementsByTagName("LinePriceInfo").item(0);
				String strlineTotal = linePriceInfoEle.getAttribute("LineTotal");
				Double dLineTotal = Double.parseDouble(strlineTotal);
				maxReqAmt=maxReqAmt+dLineTotal;
				
				custPoNo = orderLineEle1.getAttribute(VSIConstants.ATTR_CUST_PO_NO);		
				validHomeLines++;
				
				
			}						
		}
		
		if(validHomeLines>0){
			
			Double dshippingChrg = 0.0;
			Double dheaderTaxes = 0.0;
			for(int m=0; m < hdrChrsNL.getLength(); m++ ){
				Element hdrChrEle = (Element) hdrChrsNL.item(m);
				if(!YFCObject.isVoid(hdrChrEle)){
					String strCharge = hdrChrEle.getAttribute("ChargeAmount");
					if(!YFCObject.isVoid(strCharge))
						dshippingChrg = dshippingChrg + Double.parseDouble(strCharge);
				}
			}
			
			for(int n = 0; n < hdrTaxesNL.getLength(); n++ ){
				Element hdrTaxEle = (Element) hdrTaxesNL.item(n);
				if(!YFCObject.isVoid(hdrTaxEle)){
					String strTax = hdrTaxEle.getAttribute("Tax");
					if(!YFCObject.isVoid(strTax))
						dheaderTaxes = dheaderTaxes + Double.parseDouble(strTax);
				}
			}
			
			
			maxReqAmt = maxReqAmt + dshippingChrg  + dheaderTaxes;
			createCallCTR(env,OHK,custPoNo,maxReqAmt);	
						
		}
		
		
		
		
	}
	private void createCallCTR(YFSEnvironment env, String oHK, String custPoNo, Double maxReqAmt) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {

		try{
		String maxRequestAmount = String.valueOf(maxReqAmt);
	    String ctrSeqId = VSIDBUtil.getNextSequence(env,VSIConstants.SEQ_CC_CTR);
		String ChargeTransactionRequestId = ctrSeqId;
		Document chargeTransactionRequestInput = XMLUtil.createDocument("ChargeTransactionRequestList");
		Element ctrListEle = chargeTransactionRequestInput.getDocumentElement();
		ctrListEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
		Element ctrEle =	chargeTransactionRequestInput.createElement("ChargeTransactionRequest");
		ctrListEle.appendChild(ctrEle);
		ctrEle.setAttribute("MaxRequestAmount", maxRequestAmount);
		ctrEle.setAttribute("ChargeTransactionRequestKey", custPoNo);
		ctrEle.setAttribute("ChargeTransactionRequestId", ChargeTransactionRequestId);
	
		//System.out.println("chargeTransactionRequestInput is ****:"+XMLUtil.getXMLString(chargeTransactionRequestInput));
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_MANAGE_CHARGE_TRAN_REQ,chargeTransactionRequestInput);
		}
		catch (ParserConfigurationException e) {
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
}//end class
