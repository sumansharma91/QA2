<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="java.math.BigDecimal" %>

<%!
public String checkNull(String sCheck)
{
	if (sCheck==null) return("");
	else return sCheck;
}

%>
<%
YFCElement oRep;
YFCElement root = (YFCElement)request.getAttribute("Receipt");
Map oMap=new HashMap();
String sKey=" ";
String documentType=" ";
String enterpriseCode=" ";

	if(root != null)
	{
		YFCElement oItem = root.getChildElement("ReceiptLines"); 
		if(oItem!= null)
			{
			for (Iterator i = oItem.getChildren(); i.hasNext();)
				{
					oRep = (YFCElement) i.next();
					sKey = checkNull(oRep.getAttribute("ItemID"))
										+checkNull(oRep.getAttribute("OrderNo"))
											+checkNull(oRep.getAttribute("PrimeLineNo"))
												+checkNull(oRep.getAttribute("ReleaseNo"))
													+checkNull(oRep.getAttribute("CaseId"))
													+checkNull(oRep.getAttribute("PalletId"))
														+checkNull(oRep.getAttribute("DispositionCode"))
															+checkNull(oRep.getAttribute("SerialNo"))
															+checkNull(oRep.getAttribute("TagNumber"));


					//gaurav
					if(!isVoid(oRep.getDateAttribute("ShipByDate"))){
						sKey += oRep.getDateAttribute("ShipByDate").getDateString(getLocale());
					}
					if(!oMap.containsKey(sKey)) {	
						oMap.put(sKey,oRep.getAttribute("Quantity"));
					} else {
						double iQty =Double.parseDouble((String)oMap.get(sKey))+Double.parseDouble(oRep.getAttribute("Quantity"));
						oRep.setAttribute("Quantity",iQty);
						oMap.remove(sKey);
						oMap.put(sKey,oRep.getAttribute("Quantity"));	
					}
				}
				YFCElement oShipment = root.getChildElement("ReceiptLines"); 
				if(oShipment != null){
					documentType = oShipment.getAttribute("DocumentType");
					enterpriseCode = oShipment.getAttribute("EnterpriseCode");
				}

			}
	}
%>
<table class="table">
<thead>
    <tr>  
		<td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/OrderLine/Item/@ItemShortDesc")%>">
            <yfc:i18n>Description</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ProductClass")%>">
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@UnitOfMeasure")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@DispositionCode")%>">
            <yfc:i18n>Disposition_Code</yfc:i18n>
        </td>
<% if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) { %>
		 <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@CaseId")%>">
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@PalletId")%>">
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>
<% } %>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@OrderNo")%>">
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@PrimeLineNo")%>">
            <yfc:i18n>Line_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ReleaseNo")%>">
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ShipByDate")%>">
            <yfc:i18n>Ship_By_Date</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@CountryOfOrigin")%>">
            <yfc:i18n>COO</yfc:i18n> 
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@SerialNumber")%>">
            <yfc:i18n>Serial_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@TagNumber")%>">
            <yfc:i18n>Tag_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody>
    <yfc:loopXML binding="xml:/Receipt/ReceiptLines/@ReceiptLine" id="ReceiptLine"> 
	<%	sKey = resolveValue("xml:/ReceiptLine/@ItemID")
							+resolveValue("xml:/ReceiptLine/@OrderNo")
								+(int)getNumericValue("xml:/ReceiptLine/@PrimeLineNo")
									+resolveValue("xml:/ReceiptLine/@ReleaseNo")
										+resolveValue("xml:/ReceiptLine/@CaseId")
											+resolveValue("xml:/ReceiptLine/@PalletId")
											+resolveValue("xml:/ReceiptLine/@DispositionCode")
											+resolveValue("xml:/ReceiptLine/@SerialNo")
											+resolveValue("xml:/ReceiptLine/@TagNumber");
        //gaurav
		if(!isVoid(resolveValue("xml:/ReceiptLine/@ShipByDate"))){
			sKey += resolveValue("xml:/ReceiptLine/@ShipByDate");
		}
		if(oMap.containsKey(sKey) && !equals("",sKey) ) {


				String sQty = (String)oMap.get(sKey);
				double dQuantity = (new Double(sQty)).doubleValue();
                BigDecimal B1 = new BigDecimal(Double.parseDouble(sQty) );
                // Reconcile Note: Should not be setting scale inside the JSP... need to clean up.
        		B1 = B1.setScale(2, BigDecimal.ROUND_HALF_UP);
				oMap.remove(sKey);
				%>
					<tr> 
					 <yfc:makeXMLInput name="tagKey">
						<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Shipment/@ShipmentNo" value="xml:/Receipt/Shipment/@ShipmentNo"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Shipment/@EnterpriseCode" value="xml:/Receipt/Shipment/@EnterpriseCode"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:/Receipt/@ReceivingDock"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Status/@StatusName" value="xml:/Receipt/Status/@StatusName"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ShortDescription" value="xml:/ReceiptLine/OrderLine/Item/@ItemShortDesc"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@OrderNo" value="xml:/ReceiptLine/@OrderNo"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@PrimeLineNo" value="xml:/ReceiptLine/@PrimeLineNo"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ReleaseNo" value="xml:/ReceiptLine/@ReleaseNo"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:/ReceiptLine/@ItemID"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:/ReceiptLine/@ProductClass"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:/ReceiptLine/@UnitOfMeasure"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@EnterpriseCode" value="xml:/ReceiptLine/@EnterpriseCode"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute1" value="xml:/ReceiptLine/@LotAttribute1"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute2" value="xml:/ReceiptLine/@LotAttribute2"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute3" value="xml:/ReceiptLine/@LotAttribute3"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotExpirationDate" value="xml:/ReceiptLine/@LotExpirationDate"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotKeyReference" value="xml:/ReceiptLine/@LotKeyReference"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotNumber" value="xml:/ReceiptLine/@LotNumber"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@ManufacturingDate" value="xml:/ReceiptLine/@ManufacturingDate"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@RevisionNo" value="xml:/ReceiptLine/@RevisionNo"/>
						<yfc:makeXMLKey binding="xml:/Receipt/Tag/@BatchNo" value="xml:/ReceiptLine/@BatchNo"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptLineKey" value="xml:/ReceiptLine/@ReceiptLineKey"/>
					 </yfc:makeXMLInput>
						<yfc:makeXMLInput name="receiptKey">
							<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey" />
							<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo" />
							<yfc:makeXMLKey binding="xml:/Receipt/@ShipmentNo" value="xml:/Receipt/Shipment/@ShipmentNo" />
							<yfc:makeXMLKey binding="xml:/Receipt/@EnterpriseCode" value="xml:/Receipt/Shipment/@EnterpriseCode" />
							<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:/Receipt/@ReceivingDock"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@StatusName" value="xml:/Receipt/Status/@StatusName"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:/ReceiptLine/@ItemID"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:/ReceiptLine/@ProductClass"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:/ReceiptLine/@UnitOfMeasure"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@SerialNo" value="xml:/ReceiptLine/@SerialNo"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@CaseId" value="xml:/ReceiptLine/@CaseId"/>
							<yfc:makeXMLKey binding="xml:/Receipt/@PalletId" value="xml:/ReceiptLine/@PalletId"/>
						</yfc:makeXMLInput>
						<td class="checkboxcolumn">
							<input type="checkbox" value='<%=getParameter("receiptKey")%>' name="itemEntityKey"/>
						</td>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ItemID"/>
						</td>
						<td class="tablecolumn">
<%						if (isVoid(getValue("ReceiptLine","xml:/ReceiptLine/OrderLine/Item/@ItemShortDesc"))) {	
                            if( isVoid(getValue("ReceiptLine","xml:/ReceiptLine/@ShipmentLineKey"))) { %>
                                &nbsp;
<%                          } else {
                                YFCDocument oDoc = YFCDocument.parse("<ShipmentLines> <ShipmentLine ItemDesc=\"\"/></ShipmentLines>");
                                YFCElement oInpElement = oDoc.getDocumentElement().createChild("ShipmentLine");
                                oDoc.getDocumentElement().removeChild(oInpElement);
                                oInpElement.setAttribute("ShipmentLineKey",getValue("ReceiptLine","xml:/ReceiptLine/@ShipmentLineKey"));	
                                YFCElement tempElem = oDoc.getDocumentElement();%>
                                <yfc:callAPI apiName="getShipmentLineList" inputElement="<%=oInpElement%>" 
                                templateElement="<%=tempElem%>" outputNamespace="ShipmentLine"/>
                                <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLines/ShipmentLine/@ItemDesc"/>
<%                          }
						} else {	%>
                        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/OrderLine/Item/@ItemShortDesc"/>
<%						}	%>                        
						</td>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ProductClass"/>
						</td>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@UnitOfMeasure"/>
						</td>
						<% 
						   String dispositionCode = resolveValue("xml:/ReceiptLine/@DispositionCode");
						   if(!isVoid(dispositionCode)){	
							   YFCElement dispositionCodeElem = YFCDocument.createDocument("ReturnDisposition").getDocumentElement();
							   dispositionCodeElem.setAttribute("DispositionCode", dispositionCode);
							   dispositionCodeElem.setAttribute("DocumentType", documentType);
							   dispositionCodeElem.setAttribute("OrganizationCode", enterpriseCode);
							   YFCElement templateElem = YFCDocument.parse("<ReturnDisposition DispositionCode=\"\" Description=\"\" DispositionKey=\"\" />").getDocumentElement();
						%>
							   <yfc:callAPI apiName="getReturnDispositionList" inputElement="<%=dispositionCodeElem%>" templateElement="<%=templateElem%>" outputNamespace=""/>
							   <td class="tablecolumn"><yfc:getXMLValueI18NDB binding="xml:/ReturnDispositionList/ReturnDisposition/@Description" /></td>
						<% } else { %>
						<td class="tablecolumn">&nbsp;</td>
						<% } %>
					<% if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) { %>
						   <td class="tablecolumn">
								<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@CaseId"/>
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@PalletId"/>
							</td>
					<% } %>
						 <td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@OrderNo"/>
						</td>
						 <td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@PrimeLineNo"/>
						</td>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ReleaseNo"/>
						</td>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ShipByDate"/>
						</td>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@CountryOfOrigin"/>
						</td>
						<td class="tablecolumn">
							<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@SerialNo"/>
						</td>
						<td class="tablecolumn">
							<a <%=getDetailHrefOptions("L01", getParameter("tagKey"),"")%> >
								<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@TagNumber"/>
							</a>
						</td>
						<td class="numerictablecolumn">
							<%=getFormattedDouble(B1.doubleValue())%>
						</td>
					 </tr>
		<%		
			
		}%>
	   </yfc:loopXML> 
</tbody>
</table>
