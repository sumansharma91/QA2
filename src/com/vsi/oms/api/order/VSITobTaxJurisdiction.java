package com.vsi.oms.api.order;
import java.rmi.RemoteException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSITobTaxJurisdiction {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSITobTaxJurisdiction.class);
	YIFApi api;

	public Document vsiTobTaxJurisdiction(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside vsiTobTaxJurisdiction================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		// Element rootElement = inXML.getDocumentElement();
		api = YIFClientFactory.getInstance().getApi();
		Element eleInputOrderLines = XMLUtil.getElementByXPath(inXML,
				"Order/OrderLines");
		NodeList nlInputOrderLines = eleInputOrderLines
				.getElementsByTagName("OrderLine");
		int iInputOrderLinesLength = nlInputOrderLines.getLength();
		for (int i = 0; i < iInputOrderLinesLength; i++) {
			Element eleInputOrderLine = (Element) nlInputOrderLines.item(i);

			String sLineType = eleInputOrderLine.getAttribute("LineType");
			String sShipNode = "00000"
					+ eleInputOrderLine.getAttribute("ShipNode");

			if (sShipNode.length() > 5) {
				int iExtraNumber = sShipNode.length() - 5;
				sShipNode = sShipNode.substring(iExtraNumber,
						sShipNode.length());

			}

			////System.out.println("ShipNode value is " + sShipNode);

			// TaxType from LineTaxes

			NodeList nlLineTaxes = eleInputOrderLine
					.getElementsByTagName("LineTax");
			////System.out.println("Length of line tax is "
					//+ nlLineTaxes.getLength());
			int iLineTaxes = nlLineTaxes.getLength();

			String sTaxRateCode = "0";
			String sZoneId="";
			String sRuleId="";
			for (int j = 0; j < iLineTaxes; j++) {
				Element eleLineTax = (Element) nlLineTaxes.item(j);
				Element eleExtnTax = (Element) eleLineTax.getElementsByTagName(
						"Extn").item(0);
				String sTaxType = eleExtnTax.getAttribute("TaxType");
				////System.out.println("Tax Type value is " + sTaxType);
				String sZipCode="";
				if (sTaxType.equalsIgnoreCase("Alternate")) {
					sTaxRateCode = "2";
					if (sLineType.equalsIgnoreCase("PICK_IN_STORE")) {
						
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						//Taking TaxRuleId and TaxZoneId from the document
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
								
						
					} else if (sLineType.equalsIgnoreCase("SHIP_TO_STORE")) {
						// Taking ZipCode from
						// Order/OrderLines/OrderLine/ShipNode/ShipNodePersonInfo/@ZipCode
						Element eleShipNode = (Element) eleInputOrderLine
								.getElementsByTagName("Shipnode").item(0);
						if(eleShipNode!=null){
						Element eleShipNodePersonInfo = (Element) eleShipNode
								.getElementsByTagName("ShipNodePersonInfo").item(0);
						sZipCode = eleShipNodePersonInfo.getAttribute("ZipCode");
						////System.out.println("ZipCode value from ShipNodePersonInfo is "
								//+ sZipCode);
						}
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
						
					}
					else if (sLineType.equalsIgnoreCase("SHIP_TO_HOME"))
					{
						Element elePersonInfoShipTo = (Element) eleInputOrderLine
						.getElementsByTagName("PersonInfoShipTo").item(0);
						sZipCode = elePersonInfoShipTo.getAttribute("ZipCode");
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
					}
				} else if (sTaxType.equalsIgnoreCase("Standard")) {
					sTaxRateCode = "1";
					if (sLineType.equalsIgnoreCase("PICK_IN_STORE")) {
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
					} else if (sLineType.equalsIgnoreCase("SHIP_TO_STORE")) {
						// Taking ZipCode from
						// Order/OrderLines/OrderLine/ShipNode/ShipNodePersonInfo/@ZipCode
						Element eleShipNode = (Element) eleInputOrderLine
								.getElementsByTagName("Shipnode").item(0);
						if(eleShipNode!=null){
						Element eleShipNodePersonInfo = (Element) eleShipNode
								.getElementsByTagName("ShipNodePersonInfo").item(0);
						sZipCode = eleShipNodePersonInfo.getAttribute("ZipCode");
						////System.out.println("ZipCode value from ShipNodePersonInfo is "
							//	+ sZipCode);
						}
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
					}
					else if (sLineType.equalsIgnoreCase("SHIP_TO_HOME"))
					{
						Element elePersonInfoShipTo = (Element) eleInputOrderLine
						.getElementsByTagName("PersonInfoShipTo").item(0);
						sZipCode = elePersonInfoShipTo.getAttribute("ZipCode");
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
					}
				} else if(sTaxType.equalsIgnoreCase("NonTaxable") || sTaxType == null || sTaxType.trim().equalsIgnoreCase("")){
					sTaxRateCode = "0";
if (sLineType.equalsIgnoreCase("PICK_IN_STORE")) {
						
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						//Taking TaxRuleId and TaxZoneId from the document
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
								
						
					} else if (sLineType.equalsIgnoreCase("SHIP_TO_STORE")) {
						// Taking ZipCode from
						// Order/OrderLines/OrderLine/ShipNode/ShipNodePersonInfo/@ZipCode
						Element eleShipNode = (Element) eleInputOrderLine
								.getElementsByTagName("Shipnode").item(0);
						if(eleShipNode!=null){
						Element eleShipNodePersonInfo = (Element) eleShipNode
								.getElementsByTagName("ShipNodePersonInfo").item(0);
						sZipCode = eleShipNodePersonInfo.getAttribute("ZipCode");
						////System.out.println("ZipCode value from ShipNodePersonInfo is "
								//+ sZipCode);
						}
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
						
					}
					else if (sLineType.equalsIgnoreCase("SHIP_TO_HOME"))
					{
						Element elePersonInfoShipTo = (Element) eleInputOrderLine
						.getElementsByTagName("PersonInfoShipTo").item(0);
						sZipCode = elePersonInfoShipTo.getAttribute("ZipCode");
						Document docOutPut = fetchTobDetails(env,sShipNode,sTaxRateCode,sZipCode,sLineType,api);
						if(!YFSObject.isNull(docOutPut) && !YFSObject.isVoid(docOutPut)){
							Element eleTobTaxJurisdictionList = docOutPut.getDocumentElement();
							if(!YFSObject.isNull(eleTobTaxJurisdictionList) && !YFSObject.isVoid(eleTobTaxJurisdictionList)){
								NodeList nlTobTaxJurisdiction = eleTobTaxJurisdictionList.getElementsByTagName("TobTaxJurisdiction");
								if(nlTobTaxJurisdiction.getLength()>0)
								{
									Element eleTobTaxJurisdiction = (Element) nlTobTaxJurisdiction.item(0); 
									sZoneId = eleTobTaxJurisdiction.getAttribute("TaxZoneId");
									sRuleId = eleTobTaxJurisdiction.getAttribute("TaxRuleId");
									eleExtnTax.setAttribute("ZoneId", sZoneId);
									eleExtnTax.setAttribute("RuleId", sRuleId);
								}
							}
						}
					}
				}

			}

			}
		if(log.isDebugEnabled()){
    		log.debug("output xml is " + XMLUtil.getXMLString(inXML));
		}
		return inXML;

	}

	/**********************************************************************************
	 * Method Name : fetchTobDetails
	 * 
	 * Description : This method is used for getting the ZoneId and RuleId 
	 * 				 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 10/15/2014 This method is used for getting the ZoneId and RuleId 
	 * 				 	
	 * 
	 * @param api
	 * @throws Exception 
	 **********************************************************************************/
	public Document fetchTobDetails(YFSEnvironment env,
			String sTaxJurisdiction, String sTaxRateCode,String sZipCode,String sLineType, YIFApi api)
			throws Exception {
		Document inputDoc = XMLUtil.createDocument("TobTaxJurisdiction");
		Element eleRootElement = inputDoc.getDocumentElement();
		eleRootElement.setAttribute("TaxJurisdiction", sTaxJurisdiction);
		eleRootElement.setAttribute("TaxRateCode", sTaxRateCode);
		////System.out.println("LineType is " + sLineType);
		if(sLineType.equalsIgnoreCase("SHIP_TO_STORE")||(sLineType.equalsIgnoreCase("SHIP_TO_HOME")))
		{
		eleRootElement.setAttribute("FromPostCode", sZipCode);
		eleRootElement.setAttribute("FromPostCodeQryType", "LE");
		eleRootElement.setAttribute("ToPostCode", sZipCode);
		eleRootElement.setAttribute("ToPostCodeQryType", "GE");
		eleRootElement.setAttribute("TaxJurisdiction", "");
		}
		if(log.isDebugEnabled()){
    		log.debug("input document is " + XMLUtil.getXMLString(inputDoc));
		}
		Document outDoc = VSIUtils.invokeService(env, "FetchTobTaxJurisdictionDetails", inputDoc);
		//Document outDoc = api.executeFlow(env, "FetchTobTaxJurisdictionDetails",inputDoc);
		if(log.isDebugEnabled()){
    		log.debug("output document is " + XMLUtil.getXMLString(outDoc));
		}
		return outDoc;

	}

	
}
