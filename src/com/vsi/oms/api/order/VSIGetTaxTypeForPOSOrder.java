package com.vsi.oms.api.order;


import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIGetTaxTypeForPOSOrder implements VSIConstants{

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetTaxTypeForPOSOrder.class);
	public Document vsiGetTaxType(YFSEnvironment env, Document inXML)
			throws Exception {
		
		if(inXML != null){
			
			Element ordEle = inXML.getDocumentElement();
			NodeList lineTaxNL = ordEle.getElementsByTagName("LineTax");
			NodeList hrdTaxNL = ordEle.getElementsByTagName("HeaderTax");
			int noOfEle = lineTaxNL.getLength() ;
			int noOfHrdTx = hrdTaxNL.getLength();
			String zoneId = null;
			String grpId = null;
			String taxZoneId = null;
			String taxType = null;
			for (int n = 0; n < noOfHrdTx; n++) {
				Element hrdTaxEle = (Element) hrdTaxNL.item(n);
				Element extnEle = null;
				if (hrdTaxEle.hasChildNodes())
					extnEle = (Element) hrdTaxEle.getElementsByTagName("Extn")
							.item(0);
				if (extnEle != null) {
					String tax = hrdTaxEle.getAttribute("Tax");
					double dTax = 0.0;
					if (tax != null && !tax.trim().equalsIgnoreCase("")) {
						dTax = Double.parseDouble(tax);
					}
					zoneId = extnEle.getAttribute("ZoneId");
					grpId = extnEle.getAttribute("GroupId");
					if (zoneId != null && grpId != null) {
						zoneId = formatValue(zoneId);
						grpId = formatValue(grpId);
						taxZoneId = zoneId + grpId;
						taxType = getTaxType(env, taxZoneId);
						if (taxType != null && taxType.equalsIgnoreCase("0") && dTax > 0) {
							taxType = "1";
						}
						if (taxType != null && taxType.equalsIgnoreCase("0")) {
							taxType = "NonTaxable";
						} else if (taxType != null
								&& taxType.equalsIgnoreCase("1")) {
							taxType = "Standard";
						} else if (taxType != null
								&& taxType.equalsIgnoreCase("2")) {
							taxType = "Alternate";
						}else{
							if(dTax > 0)taxType = "Standard";
							else taxType = "NonTaxable";
						}
					}
					extnEle.setAttribute("TaxType", taxType);
				}
			}
			for(int i= 0; i < noOfEle; i++){
				Element lineTaxEle = (Element) lineTaxNL.item(i);
				Element extnEle = null;
				if(lineTaxEle.hasChildNodes())
				extnEle = (Element) lineTaxEle.getElementsByTagName(
						"Extn").item(0);
				if(extnEle != null){
					String tax = lineTaxEle.getAttribute("Tax");
					double dTax = 0.0;
					if (tax != null && !tax.trim().equalsIgnoreCase("")) {
						dTax = Double.parseDouble(tax);
					}
					zoneId = extnEle.getAttribute("ZoneId");
					grpId = extnEle.getAttribute("GroupId");
					if(zoneId != null && grpId != null){
						zoneId = formatValue(zoneId);
						grpId = formatValue(grpId);
						taxZoneId =zoneId+grpId ;
						taxType = getTaxType(env,taxZoneId);
						if (taxType != null && taxType.equalsIgnoreCase("0") && dTax > 0) {
							taxType = "1";
						}
						if(taxType != null && taxType.equalsIgnoreCase("0")){
							taxType = "NonTaxable";
						} else if (taxType != null
								&& taxType.equalsIgnoreCase("1")) {
							taxType = "Standard";
						} else if (taxType != null
								&& taxType.equalsIgnoreCase("2")) {
							taxType = "Alternate";
						}else{
							if(dTax > 0)taxType = "Standard";
							else taxType = "NonTaxable";
						}
					}
					
					extnEle.setAttribute("TaxType", taxType);
				}
				
				
				
			}
			
		}
		
		
				return inXML;
		
	}
	
	private String formatValue(String extnTaxProductCode) {
		int strLgth = extnTaxProductCode.length();
		if(strLgth < 5){
			while(strLgth < 5){
				extnTaxProductCode = "0"+extnTaxProductCode;
				strLgth = extnTaxProductCode.length();
			}
		}
		return extnTaxProductCode;
	}
	private String getTaxType(YFSEnvironment env, String taxZoneId) {
		String taxType = null;
		if(taxZoneId != null){
			try {
				Document getTobTaxZoneLstIp = XMLUtil
						.createDocument("TobTaxZone");
				Element rootEle = getTobTaxZoneLstIp.getDocumentElement();
				rootEle.setAttribute("TaxZoneId", taxZoneId);
				if(log.isDebugEnabled()){
					log.info("Input To getobTaxZoneList : \n"
							+ XMLUtil.getXMLString(getTobTaxZoneLstIp));
				}
				Document getTobTaxZoneLstOp = VSIUtils.invokeService(env,
						"VSIGetTobTaxZoneList", getTobTaxZoneLstIp);
				if(log.isDebugEnabled()){
					log.info("getTobTaxZoneLstOp : \n"
							+ XMLUtil.getXMLString(getTobTaxZoneLstOp));
				}
				if(getTobTaxZoneLstOp != null){
					 Element opRootEle = getTobTaxZoneLstOp.getDocumentElement();
					 if(opRootEle.hasChildNodes()){
						Element tobTaxZoneEle = (Element) opRootEle
								.getElementsByTagName("TobTaxZone").item(0);
						 if(tobTaxZoneEle != null)
							 taxType = tobTaxZoneEle.getAttribute("TaxType");
					 }
				 }
				if(log.isDebugEnabled()){
					log.info("TaxType: "+taxType);
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return taxType;
	}

	
}
