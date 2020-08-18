<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%	
	String useFeeNotDiscount = (String) request.getAttribute("UseFeeNotDiscount");
%>


<table class="table" cellpadding="0" cellspacing="0" width="100%">
<thead>
    <tr>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line#</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/LineDetailTranQuantity/@ShippedQty")%>"><yfc:i18n>Quantity/UOM</yfc:i18n></td>
        <td class="tablecolumnheader" ><yfc:i18n>Unit_Price</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/@ExtendedPrice")%>"><yfc:i18n>Extended_Price</yfc:i18n></td>
        
		<% if (!equals(useFeeNotDiscount,"Y")) { %>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/@Charges")%>"><yfc:i18n>Charges_Discount</yfc:i18n></td>
		<% } else { %>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/@Charges")%>"><yfc:i18n>Charges_Fees</yfc:i18n></td>
		<% } %>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/@Tax")%>"><yfc:i18n>Taxes</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/LineDetails/LineDetail/@LineTotal")%>"><yfc:i18n>Line_Total</yfc:i18n></td>
    </tr>
</thead>
<tbody> 
    <yfc:loopXML name="InvoiceDetail" binding="xml:/InvoiceDetail/InvoiceHeader/LineDetails/@LineDetail" id="LineDetail"> 
	<%	if(equals(resolveValue("xml:/LineDetail/OrderLine/@ItemGroupCode"), "PROD") )	{	%>

    <yfc:makeXMLInput name="InvoiceKey">
        <yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@InvoiceKey" value="xml:/InvoiceDetail/InvoiceHeader/@OrderInvoiceKey" />
        <yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@OrderLineKey" value="xml:/LineDetail/@OrderLineKey" />
    </yfc:makeXMLInput>
    <yfc:makeXMLInput name="orderLineKey">
        <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/LineDetail/@OrderLineKey"/>
        <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/InvoiceDetail/InvoiceHeader/Order/@OrderHeaderKey"/>
    </yfc:makeXMLInput>
    <tr>
        <td class="tablecolumn" sortValue="<%=getNumericValue("xml:LineDetail:/LineDetail/OrderLine/@PrimeLineNo")%>">
			<% if(showOrderLineNo("InvoiceDetail","Order")) {%>
	            <a <%=getDetailHrefOptions("L03",getParameter("orderLineKey"),"")%>>
		            <yfc:getXMLValue binding="xml:/LineDetail/OrderLine/@PrimeLineNo"/>
				</a>
			<%} else {%>
				<yfc:getXMLValue binding="xml:/LineDetail/OrderLine/@PrimeLineNo"/>
			<%}%>
        </td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineDetail/OrderLine/Item/@ItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineDetail/OrderLine/Item/@ProductClass"/></td>        
        <%  
            String invUOM = resolveValue("xml:/LineDetail/LineDetailTranQuantity/@TransactionalUOM");
            String pricingUOM = resolveValue("xml:/LineDetail/OrderLine/LinePriceInfo/@PricingUOM");            
        %>
        <td class="numerictablecolumn">
            <yfc:getXMLValue binding="xml:/LineDetail/LineDetailTranQuantity/@ShippedQty"/>
            <% if(!isVoid(invUOM)) { %>
                &nbsp;<%=invUOM%>&nbsp;
            <% } %>
            <% if((!isVoid(pricingUOM)) && (!equals(invUOM,pricingUOM))) { %>
                    <yfc:i18n>(</yfc:i18n>
                    <yfc:getXMLValue binding="xml:/LineDetail/@PricingQty"/>
                    &nbsp;<%=pricingUOM%>&nbsp;
                    <yfc:i18n>)</yfc:i18n>                        
            <% } %>           
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:LineDetail:/LineDetail/@UnitPrice")%>" >
			<%=resolveValue("xml:/LineDetail/@UnitPrice")%>
			
		<%	if(equals(resolveValue("xml:InvoiceDetail/InvoiceHeader/@InvoiceCreationReason"), "PRICE_CHANGE.Y" ) )	{	
				double dOrigPrice = getNumericValue("xml:/LineDetail/@OriginalUnitPrice");
				double dNewPrice = getNumericValue("xml:/LineDetail/@UnitPrice") + dOrigPrice;			%>
				<br>
				<%=getFormatedI18N("Original_Unit_Price_arg0", new String[]{getFormattedDouble(dOrigPrice)} )%><br>
				<%=getFormatedI18N("New_Unit_Price_arg0", new String[]{getFormattedDouble(dNewPrice)} )%>
		<%	}	%>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:LineDetail:/LineDetail/@ExtendedPrice")%>">
            <yfc:getXMLValue binding="xml:/LineDetail/@ExtendedPrice"/>
        </td>

        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:LineDetail:/LineDetail/@Charges")%>">
            <a <%=getDetailHrefOptions("L01",getParameter("InvoiceKey"),"")%>><yfc:getXMLValue binding="xml:/LineDetail/@Charges"/></a>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:LineDetail:/LineDetail/@Tax")%>"> 
            <a <%=getDetailHrefOptions("L02",getParameter("InvoiceKey"),"")%>><yfc:getXMLValue binding="xml:/LineDetail/@Tax"/></a>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:LineDetail:/LineDetail/@LineTotal")%>">
            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/LineDetail/@LineTotal"/>&nbsp;<%=curr0[1]%>
        </td>
    </tr>                       
	<%	}	%>
    </yfc:loopXML>
</tbody>
</table>
