<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="java.lang.Double" %>

<%
double balance = 0;

YFCElement oInvoiceDetail = (YFCElement)request.getAttribute("InvoiceDetail");
if(oInvoiceDetail != null)
{       
    YFCElement oInvoiceHeader = (YFCElement)oInvoiceDetail.getChildElement("InvoiceHeader");
    if(oInvoiceHeader != null) 
        {        
        balance = oInvoiceHeader.getDoubleAttribute("TotalAmount") - oInvoiceHeader.getDoubleAttribute("AmountCollected") - oInvoiceHeader.getDoubleAttribute("CollectedThroughAR");
        }
}
YFCLocale locale = getLocale();
String sBalance = getLocalizedStringFromComputedDouble(locale,balance);
%>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel" nowrap="true"><yfc:i18n>Invoiced</yfc:i18n></td>
        <td class="protectednumber"><span class="protectednumber" ><yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@TotalAmount"/>&nbsp;</span><span class="protectedtext"  style="width:8px;"><yfc:i18n>Plus</yfc:i18n></span></td>
    </tr>
    <tr>
        <td class="detaillabel" nowrap="true" ><yfc:i18n>Collected</yfc:i18n></td>
        <td class="protectednumber" nowrap="true">
            <span class="protectednumber" >
                <a <%=getDetailHrefOptions("L01", "", "")%>><yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@AmountCollected"/></a></span>
            <span class="protectedtext" style="width:8px"><yfc:i18n>Minus</yfc:i18n></span>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" nowrap="true" ><yfc:i18n>Collected_Through_AR</yfc:i18n></td>
        <td class="protectednumber"><span class="protectednumber" ><yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@CollectedThroughAR"/>&nbsp;</span><span class="protectedtext"  style="width:8px"><yfc:i18n>Minus</yfc:i18n></span></td>
    </tr>
    <tr>
        <td  class="detaillabel" nowrap="true" ><yfc:i18n>Balance</yfc:i18n></td>
        <td class="totaltext"><span class="protectednumber" ><% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<%=sBalance%>&nbsp;<%=curr0[1]%></span><span class="protectedtext"  style="width:8px"><yfc:i18n>&nbsp;</yfc:i18n></span></td>
    </tr>
</table>
