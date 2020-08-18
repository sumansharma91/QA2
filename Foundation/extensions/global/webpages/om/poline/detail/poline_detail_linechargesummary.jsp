<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table class="table" cellpadding="0" cellspacing="0" width="100%" suppressFooter="true">
<thead>
    <tr>
        <td class="numerictablecolumnheader" sortable="no" width="40%">&nbsp;</td>
        <td class="numerictablecolumnheader" sortable="no" width="60%"><yfc:i18n>Ordered</yfc:i18n></td>
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
    </tr>
    <tr>
        <td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Discount</yfc:i18n></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L04",getParameter("chargeDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@Discount" /></a></td>
    </tr>
    <tr>
        <td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Charges</yfc:i18n></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L04",getParameter("chargeDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@Charges" /></a></td>
    </tr>
    <tr>
        <td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Tax</yfc:i18n></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L01",getParameter("taxDetailKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@Tax" /></a></td>
    </tr>
    <tr>
        <td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Totals</yfc:i18n></td>
		<td class="totaltext"><% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@LineTotal" />&nbsp;<%=curr0[1]%></td>
    </tr>
</tbody>
</table>
