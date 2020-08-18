package com.vsi.som.shipment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSISFSPrintShipmentPackedXML {
	
	private static YFCLogCategory log = YFCLogCategory.instance(VSISFSPrintShipmentPackedXML.class);
	private static final String TAG = VSISFSPrintShipmentPackedXML.class.getSimpleName();
	YIFApi api;
	
	public Document printShipmentPackedXML(YFSEnvironment env, Document inXML){
		
		printLogs("================Inside VSISFSPrintShipmentPackedXML Class and printShipmentPackedXML Method================");
		printLogs("Printing Input XML :"+SCXmlUtil.getString(inXML));
		
		try{
			Element eleShipment=inXML.getDocumentElement();
			String strPackShipFlag=null;						
			String strShipmentKey =eleShipment.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY);
			
			Document docGetShipmentListIn=XMLUtil.createDocument(VSIConstants.ELE_SHIPMENT);
			Element eleGetShipmentList = docGetShipmentListIn.getDocumentElement();
			eleGetShipmentList.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, strShipmentKey);	
			Document docGetShipmentListOut = VSIUtils.invokeAPI(env,VSIConstants.SHIPMENT_LIST_PICK_PACK_TEMPLATE,VSIConstants.API_GET_SHIPMENT_LIST, docGetShipmentListIn);
			Element eleGetShipmentListOut = (Element) docGetShipmentListOut.getElementsByTagName(VSIConstants.ELE_SHIPMENTS).item(0);			
			int intTotalNoOfShipment= Integer.parseInt(eleGetShipmentListOut.getAttribute(VSIConstants.ATTR_TOTAL_NUMBER_OF_RECORDS));
			Element eleShipmentOut=null;
			Element eleShpmntExtn= null;
			String strShipmentStatus = null;
			if (intTotalNoOfShipment > 0) {
				 eleShipmentOut= (Element) docGetShipmentListOut.getElementsByTagName(VSIConstants.ELE_SHIPMENT).item(0);
				 eleShpmntExtn=SCXmlUtil.getChildElement(eleShipmentOut, VSIConstants.ELE_EXTN);
				 if(!YFCCommon.isVoid(eleShpmntExtn)){
					 strPackShipFlag=eleShpmntExtn.getAttribute("ExtnPackShipFlag");
				 }
				 strShipmentStatus= eleShipmentOut.getAttribute(VSIConstants.ATTR_STATUS);
			}
			
			if(VSIConstants.FLAG_Y.equals(strPackShipFlag)){
				printLogs("PickShip Process has already been completed for this Shipment");
				printLogs("Exiting VSISFSPrintShipmentPackedXML Class and printShipmentPackedXML Method");
				return inXML;
			}else{
				
			 if(strShipmentStatus.equalsIgnoreCase("1300")) {
					 
				Document docShipmentPackedXML=XMLUtil.createDocument("CartonData");
				Element eleShipmentPackedXML=docShipmentPackedXML.getDocumentElement();
				Document docSortedShpmntIn = XMLUtil.createDocument(VSIConstants.ELE_SHIPMENT);
				Element eleSortedShpmntIn = docSortedShpmntIn.getDocumentElement();
				eleSortedShpmntIn.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, strShipmentKey);
				
				printLogs("Input to getSortedShipmentDetails API: "+SCXmlUtil.getString(docSortedShpmntIn));

				Document docSortedShpmntOut = VSIUtils.invokeAPI(env,"global/template/api/VSISFSShipmentPacked.xml","getSortedShipmentDetails", docSortedShpmntIn);
				printLogs("Output from getSortedShipmentDetails API: "+SCXmlUtil.getString(docSortedShpmntOut));

				Element eleSortedShpmntOut=docSortedShpmntOut.getDocumentElement();
				String strOrderNumber=eleSortedShpmntOut.getAttribute(VSIConstants.ATTR_ORDER_NO);			
				
				Document docInputgetCommonCodeList=XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
				Element eleCommonCode = docInputgetCommonCodeList.getDocumentElement();
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, "SOM_DOCUMENT_TYPES");
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, "ShipLabel");

				Document docCommonCodeListOP=VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docInputgetCommonCodeList);

				Element eleCCOut=(Element)docCommonCodeListOP
						.getElementsByTagName(VSIConstants.ELE_COMMON_CODE).item(0);
				
				String strDocType=eleCCOut.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				
				putElementValue(eleShipmentPackedXML,"DocumentType", strDocType);
							
				String strShipFromFacilityID=eleSortedShpmntOut.getAttribute(VSIConstants.ATTR_SHIP_NODE);
				String strService=eleSortedShpmntOut.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE);
				
				Element eleFromAddr = SCXmlUtil.getChildElement(eleSortedShpmntOut, "FromAddress");
				String strShipFromAddressLine1=eleFromAddr.getAttribute(VSIConstants.ATTR_ADDRESS1);
				String strShipFromAddressLine2=eleFromAddr.getAttribute(VSIConstants.ATTR_ADDRESS2);
				String strShipFromCity=eleFromAddr.getAttribute(VSIConstants.ATTR_CITY);
				String strShipFromState=eleFromAddr.getAttribute(VSIConstants.ATTR_STATE);
				String strShipFromZip=eleFromAddr.getAttribute(VSIConstants.ATTR_ZIPCODE);
				String strShipFromPhone=eleFromAddr.getAttribute(VSIConstants.ATTR_DAY_PHONE);
				String strShipFromCompany=eleFromAddr.getAttribute(VSIConstants.ATTR_COMPANY);
				String strShipFromCountryCode=eleFromAddr.getAttribute(VSIConstants.ATTR_COUNTRY);
				String strShipFromPerson=eleFromAddr.getAttribute("PersonID");
				Element eleToAddr = SCXmlUtil.getChildElement(eleSortedShpmntOut, "ToAddress");
				String strShipToAddressLine1=eleToAddr.getAttribute(VSIConstants.ATTR_ADDRESS1);
				String strShipToAddressLine2=eleToAddr.getAttribute(VSIConstants.ATTR_ADDRESS2);
				String strShipToCity=eleToAddr.getAttribute(VSIConstants.ATTR_CITY);
				String strShipToState=eleToAddr.getAttribute(VSIConstants.ATTR_STATE);
				String strShipToZip=eleToAddr.getAttribute(VSIConstants.ATTR_ZIPCODE);
				String strShipToPhone=eleToAddr.getAttribute(VSIConstants.ATTR_DAY_PHONE);
				String strShipToPerson=eleToAddr.getAttribute("PersonID");
				String strShipToCountryCode=eleToAddr.getAttribute(VSIConstants.ATTR_COUNTRY);
				Element eleContainers = SCXmlUtil.getChildElement(eleSortedShpmntOut, VSIConstants.ELE_CONTAINERS);
				NodeList nlContainer=eleContainers.getElementsByTagName(VSIConstants.ELE_CONTAINER);
				
				putElementValue(eleShipmentPackedXML,"OrderNumber", strOrderNumber);
				putElementValue(eleShipmentPackedXML,"ContainerType", "VS");
				putElementValue(eleShipmentPackedXML,"Service", strService);
				putElementValue(eleShipmentPackedXML,"OrderType", "STH");
				putElementValue(eleShipmentPackedXML,"ShipFromFacilityID", strShipFromFacilityID);
				putElementValue(eleShipmentPackedXML,"ShipFromCompany", strShipFromCompany);
				putElementValue(eleShipmentPackedXML,"ShipFromAddressLine1", strShipFromAddressLine1);
				putElementValue(eleShipmentPackedXML,"ShipFromAddressLine2", strShipFromAddressLine2);
				putElementValue(eleShipmentPackedXML,"ShipFromCity", strShipFromCity);
				putElementValue(eleShipmentPackedXML,"ShipFromState", strShipFromState);
				putElementValue(eleShipmentPackedXML,"ShipFromZip", strShipFromZip);
				putElementValue(eleShipmentPackedXML,"ShipFromCountryCode", strShipFromCountryCode);
				putElementValue(eleShipmentPackedXML,"ShipFromPhone", strShipFromPhone);
				putElementValue(eleShipmentPackedXML,"ShipFromPerson", strShipFromPerson);
				
				Document docGetNodeTransferScheduleListIn = XMLUtil.createDocument(VSIConstants.ELE_NODE_TFR_SCHEDULE);
				Element eleGetNodeTransferScheduleListIn = docGetNodeTransferScheduleListIn.getDocumentElement();
				eleGetNodeTransferScheduleListIn.setAttribute(VSIConstants.ATTR_TO_NODE, strShipFromFacilityID);
				

				Document docGetNodeTransferScheduleListOut = VSIUtils.invokeAPI(env,"global/template/api/VSISFS_getNodeTransferScheduleList.xml",VSIConstants.API_GET_NODE_TFR_SCHEDULE_LIST, docGetNodeTransferScheduleListIn);

				Element eleGetNodeTransferScheduleListOut = docGetNodeTransferScheduleListOut.getDocumentElement();
				Element eleNodeTransferSchedule=(Element)eleGetNodeTransferScheduleListOut.getElementsByTagName(VSIConstants.ELE_NODE_TFR_SCHEDULE).item(0);
				
				String strReturnToFacilityID="";
				
				if (eleNodeTransferSchedule != null) {
					strReturnToFacilityID= eleNodeTransferSchedule.getAttribute(VSIConstants.ATTR_FROM_NODE);			
					
				}
				else {
					strReturnToFacilityID ="9004";
				}
				Document docGetOrganizationHierarchyIn = XMLUtil.createDocument(VSIConstants.ELE_ORGANIZATION);
				Element eleGetOrganizationHierarchyIn = docGetOrganizationHierarchyIn.getDocumentElement();
				eleGetOrganizationHierarchyIn.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, strReturnToFacilityID);
				

				Document docGetOrganizationHierarchyOut = VSIUtils.invokeAPI(env,"global/template/api/VSISFS_getOrganizationHierarchy.xml",VSIConstants.API_GET_ORG_HEIRARCHY, docGetOrganizationHierarchyIn);

				Element eleGetOrganizationHierarchyOut = docGetOrganizationHierarchyOut.getDocumentElement();
				Element eleContactPersonInfo = (Element)eleGetOrganizationHierarchyOut.getElementsByTagName("ContactPersonInfo").item(0);
				
				String strReturnToCompany=eleContactPersonInfo.getAttribute(VSIConstants.ATTR_COMPANY);
				String strReturnToAddressLine1=eleContactPersonInfo.getAttribute(VSIConstants.ATTR_ADDRESS1);
				String strReturnToAddressLine2=eleContactPersonInfo.getAttribute(VSIConstants.ATTR_ADDRESS2);
				String strReturnToCity=eleContactPersonInfo.getAttribute(VSIConstants.ATTR_CITY);
				String strReturnToState=eleContactPersonInfo.getAttribute(VSIConstants.ATTR_STATE);
				String strReturnToZip=eleContactPersonInfo.getAttribute(VSIConstants.ATTR_ZIPCODE);
				String strReturnToCountryCode=eleContactPersonInfo.getAttribute(VSIConstants.ATTR_COUNTRY);
				String strReturnToPerson="";
				if(VSIConstants.SHIP_NODE_9004_VALUE.equals(strReturnToFacilityID)){
					strReturnToPerson="VADC Shipping";
				}else if(VSIConstants.SHIP_NODE_9005_VALUE.equals(strReturnToFacilityID)){
					strReturnToPerson="AZDC Shipping";
				}
							
				putElementValue(eleShipmentPackedXML,"ReturnToFacilityID", strReturnToFacilityID);
				putElementValue(eleShipmentPackedXML,"ReturnTOCompany", strReturnToCompany);
				putElementValue(eleShipmentPackedXML,"ReturnTOAddressLine1", strReturnToAddressLine1);
				putElementValue(eleShipmentPackedXML,"ReturnTOAddressLine2", strReturnToAddressLine2);
				putElementValue(eleShipmentPackedXML,"ReturnTOCity", strReturnToCity);
				putElementValue(eleShipmentPackedXML,"ReturnTOState", strReturnToState);
				putElementValue(eleShipmentPackedXML,"ReturnTOZip", strReturnToZip);
				putElementValue(eleShipmentPackedXML,"ReturnTOCountryCode", strReturnToCountryCode);
				putElementValue(eleShipmentPackedXML,"ReturnTOPerson", strReturnToPerson);
				
				putElementValue(eleShipmentPackedXML,"ShipToPerson", strShipToPerson);
				putElementValue(eleShipmentPackedXML,"ShipToAddressLine1", strShipToAddressLine1);
				putElementValue(eleShipmentPackedXML,"ShipToAddressLine2", strShipToAddressLine2);
				putElementValue(eleShipmentPackedXML,"ShipToCity", strShipToCity);
				putElementValue(eleShipmentPackedXML,"ShipToState", strShipToState);
				putElementValue(eleShipmentPackedXML,"ShipToZip", strShipToZip);
				putElementValue(eleShipmentPackedXML,"ShipToCountryCode", strShipToCountryCode);
				putElementValue(eleShipmentPackedXML,"ShipToPhone", strShipToPhone);
							
				String strSignatureRequired="";
				String strAdultSignatureRequired="";
				boolean bSignFlagSet=false;
				boolean bAdultSignFlagSet=false;						
				
				Element elePackages=SCXmlUtil.createChild(eleShipmentPackedXML, "Packages");
				String strBoxNumber="";
				int iBoxNumber=0;
				
				String strCartonID="";
				String strContainerSize="";
				String strPackageWeight="";
				String strPackageDimHeight="";
				String strPackageDimWidth="";
				String strPackageDimLength="";
				String strReference1Type="";
				String strReference1Value="";
				String strReference2Type="";
				String strReference2Value="";
				String strReference3Type="";
				String strReference3Value="";
				String strReference4Type="";
				String strReference4Value="";
				
				for(int i=0; i<nlContainer.getLength(); i++){
					Element eleContainer = (Element) nlContainer.item(i);
					Element elePackage=SCXmlUtil.createChild(elePackages, "Package");
					iBoxNumber=iBoxNumber+1;
					strBoxNumber=Integer.toString(iBoxNumber);
					elePackage.setAttribute("BoxNumber", strBoxNumber);
					strCartonID=eleContainer.getAttribute("ContainerNo");
					strPackageWeight=eleContainer.getAttribute("ContainerNetWeight");
					strPackageDimHeight=eleContainer.getAttribute("ContainerHeight");
					strPackageDimWidth=eleContainer.getAttribute("ContainerWidth");
					strPackageDimLength=eleContainer.getAttribute("ContainerLength");
					Element eleContainerExtn=SCXmlUtil.getChildElement(eleContainer, VSIConstants.ELE_EXTN);
					String strParcelType=eleContainerExtn.getAttribute("ExtnParcelType");
					if(!YFCCommon.isVoid(strParcelType)){
						if("Carton".equals(strParcelType)){
							strContainerSize="30";
						}else if("Envelope".equals(strParcelType)){
							strContainerSize="ENV";
						}
					}
					putElementValue(elePackage,"CartonID", strCartonID);
					putElementValue(elePackage,"ContainerSize", strContainerSize);
					putElementValue(elePackage,"PackagingType", "CUSTOM_PACKAGE");
					putElementValue(elePackage,"PackageWeightUOM", "LB");
					putElementValue(elePackage,"PackageWeight", strPackageWeight);		
					putElementValue(elePackage,"PackageDimUOM", "IN");
					putElementValue(elePackage,"PackageDimHeight", strPackageDimHeight);
					putElementValue(elePackage,"PackageDimWidth", strPackageDimWidth);
					putElementValue(elePackage,"PackageDimLength", strPackageDimLength);
					putElementValue(elePackage,"TrackingNo", "");
									
					Element eleOrderLinesOut=SCXmlUtil.createChild(elePackage, VSIConstants.ELE_ORDER_LINES);
					Element eleContainerDtls = SCXmlUtil.getChildElement(eleContainer, VSIConstants.ELE_CONTAINER_DETAILS);
					NodeList nlContainerDtl=eleContainerDtls.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
					for(int j=0; j<nlContainerDtl.getLength(); j++){
						Element eleOrderLineOut=SCXmlUtil.createChild(eleOrderLinesOut, VSIConstants.ELE_ORDER_LINE);
						Element eleContainerDtl=(Element) nlContainerDtl.item(j);
						Element eleCntnrDtlOrdLn=SCXmlUtil.getChildElement(eleContainerDtl, VSIConstants.ELE_ORDER_LINE);
						String strLineNumber=eleCntnrDtlOrdLn.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO);
						eleOrderLineOut.setAttribute("LineNumber", strLineNumber);
						Element eleCntnrDtlOrdLnExtn=SCXmlUtil.getChildElement(eleCntnrDtlOrdLn, VSIConstants.ELE_EXTN);
						String strExtnSignType=eleCntnrDtlOrdLnExtn.getAttribute(VSIConstants.ATTR_EXTN_SIGNATURE_TYPE);
						if(VSIConstants.FLAG_Y.equals(strExtnSignType) && !bSignFlagSet){
							strSignatureRequired=VSIConstants.FLAG_Y;
							bSignFlagSet=true;						
						}else if(VSIConstants.FLAG_A.equals(strExtnSignType) && !bAdultSignFlagSet){
							strAdultSignatureRequired=VSIConstants.FLAG_Y;
							bAdultSignFlagSet=true;
						}
						Element eleItem=SCXmlUtil.getChildElement(eleCntnrDtlOrdLn, VSIConstants.ELE_ITEM);
						String strItemID=eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
						String strItemDesc=eleItem.getAttribute(VSIConstants.ATTR_ITEM_DESC);
						String strQuantity=eleCntnrDtlOrdLn.getAttribute(VSIConstants.ATTR_ORD_QTY);
						Element eleLinePriceInfo=SCXmlUtil.getChildElement(eleCntnrDtlOrdLn, VSIConstants.ELE_LINE_PRICE);
						String strUnitPrice=eleLinePriceInfo.getAttribute(VSIConstants.ATTR_UNIT_PRICE);
						Element eleOrdLnOverallTotals = SCXmlUtil.getChildElement(eleCntnrDtlOrdLn, VSIConstants.ELE_LINE_OVERALL_TOTALS);
						String strExtendedPrice=eleOrdLnOverallTotals.getAttribute(VSIConstants.ATTR_EXTENDED_PRICE);
						String strLnTtl=eleOrdLnOverallTotals.getAttribute(VSIConstants.ATTR_LINE_TOTAL);
						Element eleItemDtls=SCXmlUtil.getChildElement(eleCntnrDtlOrdLn, VSIConstants.ELE_ITEM_DETAILS);
						Element eleItemAliasList=SCXmlUtil.getChildElement(eleItemDtls, VSIConstants.ELE_ITEM_ALIAS_LIST);
						NodeList nlItemAlias=eleItemAliasList.getElementsByTagName(VSIConstants.ELE_ITEM_ALIAS);
						String strUPC="";
						for(int m=0; m<nlItemAlias.getLength(); m++){
							Element eleItemAlias=(Element) nlItemAlias.item(m);
							String strAliasName=eleItemAlias.getAttribute(VSIConstants.ATTR_ALIAS_NAME);
							if(VSIConstants.ATTR_UPC.equals(strAliasName)){
								String strAliasValue=eleItemAlias.getAttribute(VSIConstants.ATTR_ALIAS_VALUE);
								strUPC=strAliasValue;
								break;
							}
						}
						putElementValue(eleOrderLineOut,"ItemID", strItemID);
						putElementValue(eleOrderLineOut,"UPC", strUPC);
						putElementValue(eleOrderLineOut,"Description", strItemDesc);
						putElementValue(eleOrderLineOut,"Quantity", strQuantity);
						putElementValue(eleOrderLineOut,"UnitPrice", strUnitPrice);
						putElementValue(eleOrderLineOut,"ExtendedPrice", strExtendedPrice);
						Element eleLnDiscounts=SCXmlUtil.createChild(eleOrderLineOut, "LineDiscounts");
						Element eleOrdLnChrgs=SCXmlUtil.getChildElement(eleCntnrDtlOrdLn, VSIConstants.ELE_LINE_CHARGES);
						NodeList nlOrdLnChrg=eleOrdLnChrgs.getElementsByTagName(VSIConstants.ELE_LINE_CHARGE);
						for(int n=0; n<nlOrdLnChrg.getLength(); n++){						
							Element eleOrdLnChrg = (Element) nlOrdLnChrg.item(n);
							Element eleLnDiscount=SCXmlUtil.createChild(eleLnDiscounts, "LineDiscount");
							String strChargeName=eleOrdLnChrg.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
							String strChargeAmount=eleOrdLnChrg.getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
							putElementValue(eleLnDiscount,"LineDiscountDescription", strChargeName);
							putElementValue(eleLnDiscount,"LineDiscountAmount", strChargeAmount);						
						}
						putElementValue(eleOrderLineOut,"LineYourPrice", strLnTtl);
					}
					
					putElementValue(elePackage,"Reference1Type", strReference1Type);
					putElementValue(elePackage,"Reference1Value", strReference1Value);
					putElementValue(elePackage,"Reference2Type", strReference2Type);
					putElementValue(elePackage,"Reference2Value", strReference2Value);
					putElementValue(elePackage,"Reference3Type", strReference3Type);
					putElementValue(elePackage,"Reference3Value", strReference3Value);
					putElementValue(elePackage,"Reference4Type", strReference4Type);
					putElementValue(elePackage,"Reference4Value", strReference4Value);
				}
				
				putElementValue(eleShipmentPackedXML,"SignatureRequired", strSignatureRequired);
				putElementValue(eleShipmentPackedXML,"AdultSignatureRequired", strAdultSignatureRequired);
				
				log.info("Printing Shipment Packed XML :"+SCXmlUtil.getString(docShipmentPackedXML));			
				
				Document docMiniSoftOut=invokeMiniSoftWebService(env, docShipmentPackedXML);
				
				processMiniSoftResponse(env,docMiniSoftOut,strShipmentKey);
				
				log.info("================Exiting VSISFSPrintShipmentPackedXML Class and printShipmentPackedXML Method================");
				
				return docShipmentPackedXML;		
			
			}
			 return inXML;
			
			}
		}catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException();
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException();
		}		
	}
		
	private void processMiniSoftResponse(YFSEnvironment env, Document docMiniSoftOut, String strShipmentKey) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		
		printLogs("================Inside processMiniSoftResponse Method================");
		
		Document docChangeShipmentIn=XMLUtil.createDocument(VSIConstants.ELE_SHIPMENT);
		Element eleChangeShipmentIn=docChangeShipmentIn.getDocumentElement();
		
		Element eleShpExtn=SCXmlUtil.createChild(eleChangeShipmentIn, VSIConstants.ELE_EXTN);
		eleShpExtn.setAttribute("ExtnPackShipFlag", VSIConstants.FLAG_Y);
		
		Element eleReturnData=docMiniSoftOut.getDocumentElement();
		Element elePackages=SCXmlUtil.getChildElement(eleReturnData, "Packages");
		if(!YFCCommon.isVoid(elePackages)){
			Element eleFirstPackage=SCXmlUtil.getChildElement(elePackages, "Package");
			if(!YFCCommon.isVoid(eleFirstPackage)){
				Element eleSCAC=SCXmlUtil.getChildElement(eleFirstPackage, VSIConstants.ATTR_SCAC);
				String strSCAC=eleSCAC.getTextContent();	
				Element eleService=SCXmlUtil.getChildElement(eleFirstPackage, "SERVICE");
				String strService=eleService.getTextContent();
				
				eleChangeShipmentIn.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, strShipmentKey);
				//changes for Ship packages issue: 17th Aug 2020- Start
				if(strSCAC.equalsIgnoreCase("UPS")) {
					strSCAC = "UPSN";
				}
				else if (strSCAC.equalsIgnoreCase("FEDEX")) {
					strSCAC = "FEDEX";
				}
				//changes for Ship packages issue: 17th Aug 2020- End
				eleChangeShipmentIn.setAttribute(VSIConstants.ATTR_SCAC, strSCAC);
				eleChangeShipmentIn.setAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE, strService);
				
				Element eleContainers=SCXmlUtil.createChild(eleChangeShipmentIn, VSIConstants.ELE_CONTAINERS);
				NodeList nlPackage=elePackages.getElementsByTagName("Package");
				for(int i=0; i<nlPackage.getLength(); i++){
					Element elePackage=(Element)nlPackage.item(i);
					Element eleContainer=SCXmlUtil.createChild(eleContainers, VSIConstants.ELE_CONTAINER);
					Element eleCartonID=SCXmlUtil.getChildElement(elePackage, "CartonID");
					String strContainerNo=eleCartonID.getTextContent();
					if(!YFCCommon.isVoid(strContainerNo)){
						eleContainer.setAttribute(VSIConstants.ATTR_CONTAINER_NO, strContainerNo);
					}
					Element eleTrackingNumber=SCXmlUtil.getChildElement(elePackage, VSIConstants.ELE_TRACKING_NUMBER);
					String strTrackingNo=eleTrackingNumber.getTextContent();
					if(!YFCCommon.isVoid(strTrackingNo)){
						eleContainer.setAttribute(VSIConstants.ATTR_TRACKING_NO, strTrackingNo);
					}
				}
				
				printLogs("changeShipment api Input: "+XMLUtil.getXMLString(docChangeShipmentIn));
				VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_SHIPMENT, docChangeShipmentIn);
				printLogs("changeShipment api was invoked Successfully");
			}
		}
		printLogs("================Exiting processMiniSoftResponse Method================");
				
	}

	private Document invokeMiniSoftWebService(YFSEnvironment env, Document docShipmentPackedXML) throws Exception {
		
		printLogs("================Inside invokeMiniSoftWebService Method================");
		
		String strMinisoftURL=YFSSystem.getProperty("MINISOFT_SFS_WEBSERVICE_URL");
		
		printLogs("URL obtained from COP file: "+strMinisoftURL);		
		
		String inDocString=XMLUtil.getXMLString(docShipmentPackedXML);
		printLogs("inDocString: "+inDocString);
		
		DataOutputStream wr=null;		
		
		URL url = new URL (strMinisoftURL);
		
		HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("POST");
		byte[] bContent = inDocString.getBytes();	
		connection.setRequestProperty("Content-Type", "application/xml;charset=utf-8");
		connection.setRequestProperty("Content-Length", String.valueOf(bContent.length));
		connection.setDefaultUseCaches(false);
		connection.setDoOutput(true);		
		
		printLogs("Invoking the MiniSoft web service");
		
		wr = new DataOutputStream (connection.getOutputStream());
		wr.write(bContent);
		
		printLogs("Invoked the MiniSoft web service");
		
		wr.flush();
        wr.close();
        
        printLogs("Before reading ResponseMessage");
        
        String responseStatus = connection.getResponseMessage();
        printLogs("responseStatus: "+responseStatus);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(
        		connection.getInputStream()));
        printLogs("After reading InputStream");
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        printLogs("response: " + response.toString());
        
        String strResponse=response.toString();
        
        printLogs("strResponse: " + strResponse);
		
		Document responseDoc=null;		
		
		responseDoc=SCXmlUtil.createFromString(strResponse);	
		
		VSIUtils.invokeService(env, "VSISOMPrintXML_DB", docShipmentPackedXML);
		
		printLogs("MiniSoft Request is stored in DB");
		
		printLogs("MiniSoft Response will be stored in DB");
		
		VSIUtils.invokeService(env, "VSISOMPrintXML_DB", responseDoc);
		
		printLogs("MiniSoft Response is stored in DB");
		
		printLogs("Response from MiniSoft WebService: "+XMLUtil.getXMLString(responseDoc));
		
		printLogs("================Exiting invokeMiniSoftWebService Method================");
		
		return responseDoc;
	}

	private void printLogs(String mesg) {
		if(log.isDebugEnabled()){
			log.debug(TAG +" : "+mesg);
		}
	}
	
	private void putElementValue(Element childEle, String key, Object value) {
		Element ele = SCXmlUtil.createChild(childEle, key);
		if(value instanceof String ) {
			ele.setTextContent((String)value);
		}else if(value instanceof Element ) {
			ele.appendChild((Element)value);
		}
	}

}
