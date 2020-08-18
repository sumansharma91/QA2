<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.lang.Double" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>

<%
double headerTotal = 0;
double totalLineExtendedPrice = 0;
double totalLineCharges = 0;
double totalLineDiscount = 0;
double totalLineTax = 0;
double totalLineTotal = 0;
double balance = 0;


YFCElement oInvoiceDetail = (YFCElement)request.getAttribute("InvoiceDetail");
String useFeeNotDiscount = (String) request.getAttribute("UseFeeNotDiscount");

if(oInvoiceDetail != null)
{       
    YFCElement oInvoiceHeader = (YFCElement)oInvoiceDetail.getChildElement("InvoiceHeader");
    if(oInvoiceHeader != null) 
        {
        headerTotal = oInvoiceHeader.getDoubleAttribute("HeaderCharges") + oInvoiceHeader.getDoubleAttribute("HeaderTax") - oInvoiceHeader.getDoubleAttribute("HeaderDiscount");
        balance = oInvoiceHeader.getDoubleAttribute("TotalAmount") - oInvoiceHeader.getDoubleAttribute("AmountCollected") - oInvoiceHeader.getDoubleAttribute("CollectedThroughAR");
        }
}
%>
<yfc:loopXML name="InvoiceDetail" binding="xml:/InvoiceDetail/InvoiceHeader/LineDetails/@LineDetail" id="LineDetail"> 
<%
		YFCElement oLineDetail = (YFCElement)pageContext.getAttribute("LineDetail");

		//fix for CR ID 31808
		YFCElement oLineCharges =(YFCElement)oLineDetail.getChildElement("LineCharges");
		if(oLineCharges != null)
		{
			for (Iterator i = oLineCharges.getChildren(); i.hasNext();) {
				YFCElement oLineCharge = (YFCElement)i.next();
				String chargeCategory = oLineCharge.getAttribute("ChargeCategory");
				if ((equals(isChargeCategoryDiscount(chargeCategory, (YFCElement) request.getAttribute("ChargeCategoryList")),"Y")) && (equals(isChargeBillable(chargeCategory, (YFCElement) request.getAttribute("ChargeCategoryList")),"Y"))) {					
					totalLineDiscount = totalLineDiscount + oLineCharge.getDoubleAttribute("ChargeAmount");
				}					
			}

		}
		

        totalLineExtendedPrice += oLineDetail.getDoubleAttribute("ExtendedPrice");
        totalLineCharges += oLineDetail.getDoubleAttribute("Charges");
        totalLineTax += oLineDetail.getDoubleAttribute("Tax");
        totalLineTotal += oLineDetail.getDoubleAttribute("LineTotal");
%>
</yfc:loopXML>
<%
	totalLineCharges = totalLineCharges + totalLineDiscount; //fix for CR ID 31808
    YFCLocale locale = getLocale();
    String sHeaderTotal = getLocalizedStringFromComputedDouble(locale,headerTotal);
    String sBalance = getLocalizedStringFromComputedDouble(locale,balance);
    String sTotalLineExtendedPrice = getLocalizedStringFromComputedDouble(locale,totalLineExtendedPrice);
    String sTotalLineCharges = getLocalizedStringFromComputedDouble(locale,totalLineCharges);
    String sTotalLineTax = getLocalizedStringFromComputedDouble(locale,totalLineTax);
    String sTotalLineTotal = getLocalizedStringFromComputedDouble(locale,totalLineTotal);
    String sTotalLineDiscount = getLocalizedStringFromComputedDouble(locale,totalLineDiscount);
%>

<yfc:makeXMLInput name="InvoiceKey">
    <yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@InvoiceKey" value="xml:/InvoiceDetail/InvoiceHeader/@OrderInvoiceKey" />
    <yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@OrderHeaderKey" value="xml:/InvoiceDetail/InvoiceHeader/Order/@OrderHeaderKey" />
</yfc:makeXMLInput>

<table class="table" cellpadding="0" cellspacing="0" width="100%" suppressFooter="true">
<thead>
    <tr>
        <td class="numerictablecolumnheader" style="width:20px" sortable="no">&nbsp;</td>
        <td class="numerictablecolumnheader" sortable="no"><yfc:i18n>Price</yfc:i18n></td>
		<% if (!equals(useFeeNotDiscount,"Y")) { %>
			<td class="numerictablecolumnheader" sortable="no"><yfc:i18n>Discount</yfc:i18n></td>
		<% } else { %>
			<td class="numerictablecolumnheader" sortable="no"><yfc:i18n>Fees</yfc:i18n></td>
		<% } %>
        <td class="numerictablecolumnheader" sortable="no"><yfc:i18n>Charges</yfc:i18n></td>
        <td class="numerictablecolumnheader" sortable="no"><yfc:i18n>Taxes</yfc:i18n></td>
        <td class="numerictablecolumnheader" sortable="no"><yfc:i18n>Totals</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <tr class="oddrow">
        <td class="numerictablecolumnheader" style="width:20px" onmouseover="style.cursor='auto'"><yfc:i18n>Header</yfc:i18n></td>
        <td class="numerictablecolumn">-</td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L01",getParameter("InvoiceKey"),"")%>><yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@HeaderDiscount"/></a></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L01",getParameter("InvoiceKey"),"")%>><yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@HeaderCharges"/></a></td>
        <td class="numerictablecolumn"><a <%=getDetailHrefOptions("L02",getParameter("InvoiceKey"),"")%>><yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@HeaderTax"/></a></td>
        <td class="totaltext"><% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<%=sHeaderTotal%>&nbsp;<%=curr0[1]%></td>
    </tr>
    <tr>
        <td class="numerictablecolumnheader" style="width:20px" onmouseover="style.cursor='auto'"><yfc:i18n>Line</yfc:i18n></td>
        <td class="numerictablecolumn"><%=sTotalLineExtendedPrice%></td>
        <td class="numerictablecolumn"><%=sTotalLineDiscount%></td>
        <td class="numerictablecolumn"><%=sTotalLineCharges%></td>
        <td class="numerictablecolumn"><%=sTotalLineTax%></td>
        <td class="totaltext"><% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;<%=sTotalLineTotal%>&nbsp;<%=curr1[1]%></td>
    </tr>
    <tr>
        <td class="numerictablecolumnheader" style="width:20px" onmouseover="style.cursor='auto'"><yfc:i18n>Totals</yfc:i18n></td>
        <td class="totaltext"><% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%>&nbsp;<%=sTotalLineExtendedPrice%>&nbsp;<%=curr2[1]%></td>
        <td class="totaltext"><% String[] curr3 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr3[0]%>&nbsp;<yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@TotalDiscount"/>&nbsp;<%=curr3[1]%></td> 
        <td class="totaltext"><% String[] curr4 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr4[0]%>&nbsp;<yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@TotalCharges"/>&nbsp;<%=curr4[1]%></td>
        <td class="totaltext"><% String[] curr5 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr5[0]%>&nbsp;<yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@TotalTax"/>&nbsp;<%=curr5[1]%></td>
        <td class="totaltext"><% String[] curr6 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr6[0]%>&nbsp;<yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@TotalAmount"/>&nbsp;<%=curr6[1]%></td>
    </tr>
</tbody>
</table>
