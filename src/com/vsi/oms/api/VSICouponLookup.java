package com.vsi.oms.api;

import org.w3c.dom.Document;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICouponLookup {
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSICouponLookup.class);
	YIFApi api;
	public Document couponLookup(YFSEnvironment env, Document inXML) throws NumberFormatException {
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + XMLUtil.getXMLString(inXML));
			log.info("================Inside couponLookup================================");
		}

		Document outDoc=null;
	//	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				String xml=	"<coupon_request>"+
								"<request>LU</request>"+
								"<store>391</store>"+
								"<register>001</register>"+
								"<transaction>1234566</transaction>"+
								"<tran_date>2014-07-16</tran_date>"+
								"<customer>100000045</customer>"+
								"<coupon_discounts>"+
									"<coupon_code>"+
										"<coupon></coupon>"+
										"<action>VA</action>"+
										"<override>N</override>"+
									"</coupon_code>"+
								"</coupon_discounts>"+
							"</coupon_request>";
		
		
		YFCElement couponIn=YFCDocument.getDocumentFor(inXML).getDocumentElement();
		String coup=couponIn.getAttribute("CouponID");
		String store=couponIn.getAttribute("ShipNode");
		String trans=couponIn.getAttribute("OrderNo");
		String transDate=couponIn.getAttribute("OrderDate");
		String custID=couponIn.getAttribute("CustomerID");
		
		YFCDocument couInput = YFCDocument.getDocumentFor(xml); 
	    YFCElement couInputEle = couInput.getDocumentElement();
	    couInputEle.getChildElement("store").setNodeValue(store);
	    couInputEle.getChildElement("transaction").setNodeValue(trans);
	    couInputEle.getChildElement("tran_date").setNodeValue(transDate);
	    couInputEle.getChildElement("customer").setNodeValue(custID);
	    couInputEle.getChildElement("coupon_discounts").getChildElement("coupon_code").getChildElement("coupon").setNodeValue(coup);
	    
	    String url=YFSSystem.getProperty("COUPONS_URL");
	    ////System.out.println("URL from Customer override:" + url);
	    //String url="http://10.3.2.140:8051/cgi-bin/cgictlpgm.pgm"+"?callpgm=cpn1001r&request=";
	   try {
			
			outDoc=InvokeWebservice.invokeURL(url, XMLUtil.serialize(couInput.getDocument(), "iso-8859-1" ,false)  );
			if(log.isDebugEnabled()){
	    		log.debug("outDoc"  + SCXmlUtil.getString(outDoc));
			}
	   } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		//System.out.print(e);
		
		YFCDocument.getDocumentFor(inXML).getDocumentElement().setAttribute("IsCallSuccessfull", "N");
		return inXML;
	} 
			
			
	   YFCElement result=YFCDocument.getDocumentFor(outDoc).getDocumentElement();
	   //String couponid= result.getChildElement("coupon").getNodeValue();
	   String isVal= result.getChildElement("valid").getNodeValue();
	   if(isVal.compareToIgnoreCase("N")==0)
	   {
		    YFCDocument.getDocumentFor(inXML).getDocumentElement().setAttribute("IsCallSuccessfull", "N");
		   	return inXML;
	   }
	   String promoCode= result.getChildElement("promotion").getNodeValue();
	   String promodesc= result.getChildElement("promo_dsc").getNodeValue();
	   String startDate= result.getChildElement("start_date").getNodeValue();
	   String endDate= result.getChildElement("end_date").getNodeValue();
	   
	   
	   YFCDocument.getDocumentFor(inXML).getDocumentElement().setAttribute("IsCouponValid", isVal);
	   YFCDocument.getDocumentFor(inXML).getDocumentElement().setAttribute("IsCallSuccessfull", "Y");
	   YFCDocument.getDocumentFor(inXML).getDocumentElement().setAttribute("StartDate", startDate);
	   YFCDocument.getDocumentFor(inXML).getDocumentElement().setAttribute("EndDate", endDate);
	   
	   //IsCouponValid=\"Y\" CouponID=\"\"
	   String op="<Promotions>"+
        "<Promotion PromotionId=\"\" PromotionDescription=\"\"/>"+
    "</Promotions>"; 

	   
	   YFCDocument o= YFCDocument.getDocumentFor(inXML);
	   YFCElement inxml=o.getDocumentElement();
	   inxml.addXMLToNode(op);
	   //System.out.print("Appended inXML is: "+ inxml);
	   YFCDocument couOP = YFCDocument.getDocumentFor(op); 
	   //System.out.print("API OutDoc: " + couOP);
	   
	   YFCDocument.getDocumentFor(inXML).getDocumentElement().getChildElement("Promotions").getChildElement("Promotion").setAttribute("PromotionId", promoCode);
	   YFCDocument.getDocumentFor(inXML).getDocumentElement().getChildElement("Promotions").getChildElement("Promotion").setAttribute("PromotionDescription", promodesc);
	  /* YFCElement couponOP=couOP.getDocumentElement().getChildElement("Promotion"); 
	  // couponOP.setAttribute("IsCouponValid", isVal);  
	   //couponOP.setAttribute("CouponID", couponid);
	   couponOP.setAttribute("PromotionId", promoCode); */
	   
	   
	   //.getChildElement("soapenv:Body").getChildElement("cu:searchCustomerResponse").getChildElement("results");
		//return couOP.getDocument();
	   

		return inXML;
		
	}

}
