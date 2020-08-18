<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%
    String prePathId = getParameter("PrePathId");
%>
<table class="table" editable="false" width="100%" cellspacing="0" SuppressRowColoring="true">
    <thead> 
        <tr>
            <td class="tablecolumnheader"><yfc:i18n>Invoice_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Invoice_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Amount_Collected</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Total_Invoice_Amount</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding='<%=buildBinding("xml:/",prePathId,"/@InvoiceCollectionDetail")%>' id="InvoiceCollectionDetail">
            <tr>
                <yfc:makeXMLInput name="invoiceKey">
                    <yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@InvoiceKey" value="xml:/InvoiceCollectionDetail/@OrderInvoiceKey" />
                </yfc:makeXMLInput>                
                <td class="tablecolumn">
						<a <%=getDetailHrefOptions("L03",getParameter("invoiceKey"),"")%>>
							<yfc:getXMLValue binding="xml:/InvoiceCollectionDetail/@InvoiceNo"/>
						</a>
                </td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:/InvoiceCollectionDetail/@DateInvoiced")%>">
					<yfc:getXMLValue binding="xml:/InvoiceCollectionDetail/@DateInvoiced"/>
				</td>
                <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:InvoiceCollectionDetail:/InvoiceCollectionDetail/@AmountCollected")%>">
					<% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%> <%=getValue("InvoiceCollectionDetail","xml:/InvoiceCollectionDetail/@AmountCollected")%> <%=curr0[1]%>
				</td>
                <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:InvoiceCollectionDetail:/InvoiceCollectionDetail/@TotalAmount")%>">
					<% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%> <%=getValue("InvoiceCollectionDetail","xml:/InvoiceCollectionDetail/@TotalAmount")%> <%=curr1[1]%>
                </td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>
