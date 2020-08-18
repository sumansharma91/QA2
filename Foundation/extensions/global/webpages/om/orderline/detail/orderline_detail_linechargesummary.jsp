<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	boolean useDiscountNotFee = false;
	if (equals(request.getParameter("UseDiscountNotFee"),"Y")) {
		useDiscountNotFee = true;
	}
%>

<table class="table" cellpadding="0" cellspacing="0" width="100%" suppressFooter="true">
<thead>
    <tr>
        <td class="numerictablecolumnheader" sortable="no" width="19%">&nbsp;</td>
        <td class="numerictablecolumnheader" sortable="no" width="27%"><yfc:i18n>Overall</yfc:i18n></td>
        <td class="numerictablecolumnheader" sortable="no" width="27%"><yfc:i18n>Open</yfc:i18n></td>
        <td class="numerictablecolumnheader" sortable="no" width="27%"><yfc:i18n>Invoiced</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <tr>
        <td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Extended_Price</yfc:i18n>
            <yfc:makeXMLInput name="chargeDetailKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="taxDetailKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
            </yfc:makeXMLInput>
        </td>
        <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@ExtendedPrice" /></td>
        <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/LineRemainingTotals/@ExtendedPrice" /></td>
        <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/LineInvoicedTotals/@ExtendedPrice" /></td>
    </tr>
	<%	if(!equals(resolveValue("xml:/OrderLine/@ItemGroupCode"), "PROD") )	{	%>
	    <tr>
				<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Option_Price</yfc:i18n>
				<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@OptionPrice" /></td>
				<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/LineRemainingTotals/@OptionPrice" /></td>
				<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/LineInvoicedTotals/@OptionPrice" /></td>
		</tr>
	<%	}%>
    <tr>
		<% if (useDiscountNotFee) { %>
			<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Discount_Always</yfc:i18n></td>
		<% } else {%>
			<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Discount</yfc:i18n></td>
		<% } %>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L04",getParameter("chargeDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@Discount" /></a></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L05",getParameter("chargeDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineRemainingTotals/@Discount" /></a></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L06",getParameter("chargeDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineInvoicedTotals/@Discount" /></a></td>
    </tr>
    <tr>
        <td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Charges</yfc:i18n></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L04",getParameter("chargeDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@Charges" /></a></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L05",getParameter("chargeDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineRemainingTotals/@Charges" /></a></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L06",getParameter("chargeDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineInvoicedTotals/@Charges" /></a></td>
    </tr>
    <tr>
        <td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Tax</yfc:i18n></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L01",getParameter("taxDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@Tax" /></a></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L02",getParameter("taxDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineRemainingTotals/@Tax" /></a></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L03",getParameter("taxDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineInvoicedTotals/@Tax" /></a></td>
    </tr>
    <tr>
        <td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Totals</yfc:i18n></td>
        <td class="totaltext"><% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@LineTotal" />&nbsp;<%=curr0[1]%></td>
        <td class="totaltext"><% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineRemainingTotals/@LineTotal" />&nbsp;<%=curr1[1]%></td>
        <td class="totaltext"><% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineInvoicedTotals/@LineTotal" />&nbsp;<%=curr2[1]%></td>
    </tr>
</tbody>
</table>
