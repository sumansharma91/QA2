<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.math.BigDecimal" %>

<%!
public String checkNull(String sCheck)
{
	if (sCheck==null) return("");
	else return sCheck;
}

public void setMap(Map oMap, String key, String attr, YFCElement element)
{
	if(!oMap.containsKey(key)) {	
		oMap.put(key,element.getAttribute(attr));
	} else {
		double iQty =Double.parseDouble((String)oMap.get(key))+Double.parseDouble(element.getAttribute(attr));
		element.setAttribute(attr,iQty);
		oMap.remove(key);
		oMap.put(key,element.getAttribute(attr));	
	}
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
								+checkNull(oRep.getAttribute("ProductClass"))
									+checkNull(oRep.getAttribute("UnitOfMeasure"))
									+checkNull(oRep.getAttribute("DispositionCode"))
										+checkNull(oRep.getAttribute("OrderNo"))
											+checkNull(oRep.getAttribute("ReleaseNo"))
												+checkNull(oRep.getAttribute("TagNumber"))
													+checkNull(oRep.getAttribute("PrimeLineNo"));

					setMap(oMap, sKey, "Quantity", oRep);
					setMap(oMap, sKey+"NetWeight", "NetWeight", oRep);
				}
			}
		YFCElement oShipment = root.getChildElement("ReceiptLines"); 
		if(oShipment != null){
			documentType = oShipment.getAttribute("DocumentType");
			enterpriseCode = oShipment.getAttribute("EnterpriseCode");
		}
	}
%>
<div style="height:175px;overflow:auto">
<table class="table">
<thead>
    <tr>  
		<td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td> 
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ItemDesc")%>">
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
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@OrderNo")%>">
            <yfc:i18n>Order_#</yfc:i18n>
        </td> 
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@PrimeLineNo")%>">
            <yfc:i18n>Line_#</yfc:i18n>
        </td> 
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ReleaseNo")%>">
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@TagNumber")%>">
            <yfc:i18n>Tag_#</yfc:i18n>
        </td>
		<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@NetWeight")%>">
			<yfc:i18n>Net_Weight</yfc:i18n>
		</td>
		<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@NetWeightUom")%>">
			<yfc:i18n>Net_Weight_UOM</yfc:i18n>
		</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/Receipt/ReceiptLines/@ReceiptLine" id="ReceiptLine">

	<% 
		String sItemId = getValue("ReceiptLine","xml:/ReceiptLine/@ItemID",true);
		if (!isVoid(sItemId)) {
			sKey = sItemId + resolveValue("xml:/ReceiptLine/@ProductClass")
				+resolveValue("xml:/ReceiptLine/@UnitOfMeasure")
					+resolveValue("xml:/ReceiptLine/@DispositionCode")
						+resolveValue("xml:/ReceiptLine/@OrderNo")
							+resolveValue("xml:/ReceiptLine/@ReleaseNo")
								+resolveValue("xml:/ReceiptLine/@TagNumber")
								+YFCI18NUtils.getDeFormattedInt(getLocale(),getValue("ReceiptLine","xml:/ReceiptLine/@PrimeLineNo"));

        if(oMap.containsKey(sKey)) {
            String sQty = (String)oMap.get(sKey);
			double dQuantity = (new Double(sQty)).doubleValue();
            BigDecimal B1 = new BigDecimal(Double.parseDouble(sQty) );
            B1 = B1.setScale(2, BigDecimal.ROUND_HALF_UP);
            oMap.remove(sKey);

            String sNetWt = (String)oMap.get(sKey+"NetWeight");
            if(isVoid(sNetWt) )
                sNetWt = "0";
            BigDecimal bNetWt = new BigDecimal(Double.parseDouble(sNetWt) );
            bNetWt = bNetWt.setScale(2, BigDecimal.ROUND_HALF_UP);
            oMap.remove("NetWeight");

            // Reconcile note: This comparison is wrong for many reasons!
            // It will definately fail internationalization test! Need to clean this up.
            if (dQuantity > 0) { %>

                <tr> 
                 <yfc:makeXMLInput name="tagKey">
                    <yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Shipment/@ShipmentNo" value="xml:/Receipt/Shipment/@ShipmentNo"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Shipment/@EnterpriseCode" value="xml:/Receipt/Shipment/@EnterpriseCode"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:/Receipt/@ReceivingDock"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@Status" value="xml:/Receipt/@Status"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@StatusName" value="xml:/Receipt/Status/@StatusName"/>	<%//-- cr 36553 --%>
                    <yfc:makeXMLKey binding="xml:/Receipt/@ShortDescription" value="xml:/ReceiptLine/OrderLine/Item/@ItemShortDesc"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@OrderNo" value="xml:ReceiptLine:/ReceiptLine/@OrderNo"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@PrimeLineNo" value="xml:ReceiptLine:/ReceiptLine/@PrimeLineNo"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@ReleaseNo" value="xml:ReceiptLine:/ReceiptLine/@ReleaseNo"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:ReceiptLine:/ReceiptLine/@ItemID"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:ReceiptLine:/ReceiptLine/@ProductClass"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:ReceiptLine:/ReceiptLine/@UnitOfMeasure"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/@EnterpriseCode" value="xml:ReceiptLine:/ReceiptLine/@EnterpriseCode"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute1" value="xml:ReceiptLine:/ReceiptLine/@LotAttribute1"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute2" value="xml:ReceiptLine:/ReceiptLine/@LotAttribute2"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute3" value="xml:ReceiptLine:/ReceiptLine/@LotAttribute3"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotExpirationDate" value="xml:ReceiptLine:/ReceiptLine/@LotExpirationDate"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotKeyReference" value="xml:ReceiptLine:/ReceiptLine/@LotKeyReference"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotNumber" value="xml:ReceiptLine:/ReceiptLine/@LotNumber"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@ManufacturingDate" value="xml:ReceiptLine:/ReceiptLine/@ManufacturingDate"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@RevisionNo" value="xml:ReceiptLine:/ReceiptLine/@RevisionNo"/>
                    <yfc:makeXMLKey binding="xml:/Receipt/Tag/@BatchNo" value="xml:ReceiptLine:/ReceiptLine/@BatchNo"/>
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptLineKey" value="xml:ReceiptLine:/ReceiptLine/@ReceiptLineKey"/>

                 </yfc:makeXMLInput>
                    <yfc:makeXMLInput name="receiptKey">
                        <yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey" />
	                    <yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo" />
	                    <yfc:makeXMLKey binding="xml:/Receipt/@OrderNo" value="xml:ReceiptLine:/ReceiptLine/@OrderNo" />
	                    <yfc:makeXMLKey binding="xml:/Receipt/@PrimeLineNo" value="xml:ReceiptLine:/ReceiptLine/@PrimeLineNo" />
                        <yfc:makeXMLKey binding="xml:/Receipt/@EnterpriseCode" value="xml:/Receipt/Shipment/@EnterpriseCode" />
                        <yfc:makeXMLKey binding="xml:/Receipt/@ShipmentNo" value="xml:/Receipt/Shipment/@ShipmentNo" />
                        <yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode"/>
                        <yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:/Receipt/@ReceivingDock"/>
                        <yfc:makeXMLKey binding="xml:/Receipt/@StatusName" value="xml:/Receipt/Status/@StatusName"/>
                        <yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:/ReceiptLine/@ItemID"/>
                        <yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:ReceiptLine:/ReceiptLine/@ProductClass"/>
                        <yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:ReceiptLine:/ReceiptLine/@UnitOfMeasure"/>
						<yfc:makeXMLKey binding="xml:/Receipt/@TagNumber" value="xml:ReceiptLine:/ReceiptLine/@TagNumber"/>
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
                    <td class="tablecolumn">
                        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@OrderNo"/>
                    </td>
                    <td class="tablecolumn">
                        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@PrimeLineNo"/>
                    </td>
                    <td class="numerictablecolumn">
                        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ReleaseNo"/>
                    </td>
                    <td class="tablecolumn">
                        <a <%=getDetailHrefOptions("L01", getParameter("tagKey"),"")%> >
                            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@TagNumber"/>
                        </a>
                    </td>
                    <td class="numerictablecolumn">
                        <%=getFormattedDouble(bNetWt.doubleValue())%>
                    </td>
                    <td class="numerictablecolumn">
                        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@NetWeightUom"/>
                    </td>
                    <td class="numerictablecolumn">
                        <%=getFormattedDouble(B1.doubleValue())%>
                    </td>
                 </tr>
     <%
            }
        }
    } %>

    </yfc:loopXML>
</tbody>
</table>
</div>
