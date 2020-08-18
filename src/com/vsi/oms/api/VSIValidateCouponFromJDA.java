package com.vsi.oms.api;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIValidateCouponFromJDA implements YIFCustomApi{

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIValidateCouponFromJDA.class);
	YIFApi api;
    String url=YFSSystem.getProperty("COUPONS_URL");

	public Document invoke(YFSEnvironment env, Document inXML) throws NumberFormatException {
		Document outDoc=null;
	//	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				String xml=	"<coupon_request>"+
								"<request>VA</request>"+
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
	    
	    ////System.out.println("URL from Customer override:" + url);
	    //String url="http://10.3.2.140:8051/cgi-bin/cgictlpgm.pgm"+"?callpgm=cpn1001r&request=";
	   try {
			
			outDoc=InvokeWebservice.invokeURL(url, XMLUtil.serialize(couInput.getDocument(), "iso-8859-1" ,false)  );

	   } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		//System.out.print(e);
		
		YFCDocument.getDocumentFor(inXML).getDocumentElement().setAttribute("IsCallSuccessfull", "N");
		return inXML;
	   }
	   
		YFCElement result = YFCDocument.getDocumentFor(outDoc)
				.getDocumentElement();
		// String couponid= result.getChildElement("coupon").getNodeValue();
		String isVal = result.getChildElement("valid").getNodeValue();
		String isError = result.getChildElement("errcode").getNodeValue();
		if (isVal.compareToIgnoreCase("N") == 0 && !"003".equals(isError)) {
			YFCDocument.getDocumentFor(inXML).getDocumentElement()
					.setAttribute("IsCallSuccessfull", "N");
			return inXML;
		}
		if ("003".equals(isError)) {
			YFCDocument.getDocumentFor(inXML).getDocumentElement()
					.setAttribute("IsExpired", "Y");
		}
		String promoCode = result.getChildElement("promotion").getNodeValue();

		YFCDocument.getDocumentFor(inXML).getDocumentElement()
				.setAttribute("IsCouponValid", isVal);
		YFCDocument.getDocumentFor(inXML).getDocumentElement()
				.setAttribute("IsCallSuccessfull", "Y");

		// IsCouponValid=\"Y\" CouponID=\"\"
		String op = "<Promotions>" + "<Promotion PromotionId=\"\" />"
				+ "</Promotions>";

		YFCDocument o = YFCDocument.getDocumentFor(inXML);
		YFCElement inxml = o.getDocumentElement();
		inxml.addXMLToNode(op);
		// System.out.print("Appended inXML is: "+ inxml);
		YFCDocument couOP = YFCDocument.getDocumentFor(op);
		// System.out.print("API OutDoc: " + couOP);

		YFCDocument.getDocumentFor(inXML).getDocumentElement()
				.getChildElement("Promotions").getChildElement("Promotion")
				.setAttribute("PromotionId", promoCode);
		/*
		 * YFCElement
		 * couponOP=couOP.getDocumentElement().getChildElement("Promotion"); //
		 * couponOP.setAttribute("IsCouponValid", isVal);
		 * //couponOP.setAttribute("CouponID", couponid);
		 * couponOP.setAttribute("PromotionId", promoCode);
		 */

		// .getChildElement("soapenv:Body").getChildElement("cu:searchCustomerResponse").getChildElement("results");
		// return couOP.getDocument();

		return inXML;
	}
	
	public Document couponLookupJDA(YFSEnvironment env, Document inXML) throws SocketTimeoutException, IOException, ParserConfigurationException, SAXException {

		Document outDoc = null;
		String xml = "<coupon_request>" +
				"<request>LU</request>" +
				"<coupon></coupon>" +
				"</coupon_request>";
		
		String strVoucher = inXML.getDocumentElement().getAttribute("CertNo");
		YFCDocument lookupInput = YFCDocument.getDocumentFor(xml); 
	    YFCElement couInputEle = lookupInput.getDocumentElement();
	    couInputEle.getChildElement("coupon").setNodeValue(strVoucher);

		outDoc = InvokeWebservice.invokeURL(url, XMLUtil.serialize(
				lookupInput.getDocument(), "iso-8859-1", false));

		return outDoc;
	}
	
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}