<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.shared.inv.*" %> 

<%
boolean isForReturn = false;
if (equals(request.getParameter("IsForReturn"),"Y")) {
	isForReturn = true;
}

String displayInEnterpriseCurrency = request.getParameter("DisplayInEnterpriseCurrency");
String total = "";

if (equals(displayInEnterpriseCurrency,"Y")) {
	total = "TotalsInEnterpriseCurrency";
} else {
	total = "Totals";	
}

String chargeType=request.getParameter("chargeType");
if (isVoid(chargeType)) {
    chargeType="Remaining";
}

String ChargeViewID="";
String TaxViewID="";
String qtyType="";
if(equals(chargeType,"Overall")) {
    ChargeViewID="L01";
    TaxViewID="L04";
	qtyType="OrderedPricing";
}
else if(equals(chargeType,"Remaining")) {
    ChargeViewID="L02";
    TaxViewID="L05";
	qtyType=chargeType + "Pricing";
}
else if(equals(chargeType,"Invoiced")) {
    ChargeViewID="L03";
    TaxViewID="L06";
    qtyType=chargeType + "Pricing";
}
%>

<table class="table" cellpadding="0" cellspacing="0" width="100%">
<thead>
    <tr>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line_#</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LinePriceInfo/@PricingUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
        <td class="numerictablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@PricingQty")%>"><yfc:i18n>Quantity</yfc:i18n></td>
        <td class="numerictablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@UnitPrice")%>"><yfc:i18n>Unit_Price</yfc:i18n></td>
        <td class="numerictablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@ExtendedPrice")%>"><yfc:i18n>Extended_Price</yfc:i18n></td>
        <td class="numerictablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@Discount")%>"><yfc:i18n>Discount</yfc:i18n></td>
		<% if (isForReturn) { %>
			<td class="numerictablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@Charges")%>"><yfc:i18n>Refunded_Charges</yfc:i18n></td>
		<% } else { %>
			<td class="numerictablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@Charges")%>"><yfc:i18n>Charges</yfc:i18n></td>
		<%}%>
        <td class="numerictablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@Tax")%>"><yfc:i18n>Taxes</yfc:i18n></td>
        <td class="numerictablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@LineTotal")%>"><yfc:i18n>Line_Total</yfc:i18n></td>
    </tr>
</thead>
<tbody> 
    <yfc:loopXML name="Order" binding="xml:/Order/OrderLines/@OrderLine" id="orderline"> 

	<% if (equals(getValue("orderline","xml:/OrderLine/@ItemGroupCode"), INVConstants.ITEM_GROUP_CODE_SHIPPING) )
	{%>
		<yfc:makeXMLInput name="orderLineKey">
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:orderline:/OrderLine/@OrderLineKey" />
		</yfc:makeXMLInput>
		<tr>
			<td class="tablecolumn" sortValue="<%=getNumericValue("xml:orderline:/OrderLine/@PrimeLineNo")%>">
				<% if(showOrderLineNo("Order","Order")) {%>
					<a <%=getDetailHrefOptions("L07", getParameter("orderLineKey"), "")%>><yfc:getXMLValue name="orderline" binding="xml:/OrderLine/@PrimeLineNo"/></a>
				<%} else {%>
					<yfc:getXMLValue name="orderline" binding="xml:/OrderLine/@PrimeLineNo"/>
				<%}%>
			</td>
			<td class="tablecolumn"><yfc:getXMLValue name="orderline" binding="xml:/OrderLine/Item/@ItemID"/></td>
			<td class="tablecolumn"><yfc:getXMLValue name="orderline" binding="xml:/OrderLine/Item/@ProductClass"/></td>
            <td class="tablecolumn"><yfc:getXMLValue name="orderline" binding="xml:/OrderLine/LinePriceInfo/@PricingUOM"/></td>
            <td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:orderline:/OrderLine/Line",chargeType,total,"/@PricingQty"))%>">
                <yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@PricingQty")%>'/>
			</td>
			<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:orderline:/OrderLine/Line",chargeType,total,"/@UnitPrice"))%>">
				<yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@UnitPrice")%>'/>
			</td>
			<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:orderline:/OrderLine/Line",chargeType,total,"/@ExtendedPrice"))%>">
				<yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@ExtendedPrice")%>'/>
			</td>
			<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:orderline:/OrderLine/Line",chargeType,total,"/@Discount"))%>">
				<% if (equals(displayInEnterpriseCurrency,"Y")) { %>
					<yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@Discount")%>'/>
				<%} else {%>
					<a <%=getDetailHrefOptions(ChargeViewID, getParameter("orderLineKey"),"")%>><yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@Discount")%>'/></a>
				<%}%>
			</td>
			<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:orderline:/OrderLine/Line",chargeType,total,"/@Charges"))%>">
				<% if (equals(displayInEnterpriseCurrency,"Y")) { %>
					<yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@Charges")%>'/>
				<%} else {%>
					<a <%=getDetailHrefOptions(ChargeViewID, getParameter("orderLineKey"),"")%>><yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@Charges")%>'/></a>
				<%}%>
			</td>
			<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:orderline:/OrderLine/Line",chargeType,total,"/@Tax"))%>">
				<% if (equals(displayInEnterpriseCurrency,"Y")) { %>
					<yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@Tax")%>'/>
				<%} else {%>
					<a <%=getDetailHrefOptions(TaxViewID,getParameter("orderLineKey"),"")%>><yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@Tax")%>'/></a>
				<%}%>
			</td>
			<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:orderline:/OrderLine/Line",chargeType,total,"/@LineTotal"))%>">
				 <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;
				<yfc:getXMLValue name="orderline" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,total,"/@LineTotal")%>'/> 
				<%=curr0[1]%>&nbsp;
			</td>               
		</tr>                    
	<%}%>
    </yfc:loopXML>
</tbody>
</table>
                        
