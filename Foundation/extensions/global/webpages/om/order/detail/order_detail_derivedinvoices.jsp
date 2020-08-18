<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/order.jspf" %>

<table class="table" editable="false" width="100%" cellpadding="0" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Invoice_#</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Order_#</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Document_Type</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Invoice_Type</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Total_Amount</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Amount_Collected</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:DerivedOrderInvoiceList:/OrderInvoiceList/@OrderInvoice" id="DerivedOrderInvoice">
            <tr>
                <yfc:makeXMLInput name="invoiceKey">
                    <yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@InvoiceKey" value="xml:DerivedOrderInvoice:/OrderInvoice/@OrderInvoiceKey" />
                </yfc:makeXMLInput>
                <yfc:makeXMLInput name="orderKey">
                    <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:DerivedOrderInvoice:/OrderInvoice/Order/@OrderHeaderKey" />
                </yfc:makeXMLInput>
                <td>
                    <input type="checkbox" value='<%=getParameter("invoiceKey")%>' name="chkEntityKey" <% if(!showOrderNo("DerivedOrderInvoice","Order")) {%> disabled="true" <%}%>/>
                </td>
                <td class="tablecolumn"><a <%=getDetailHrefOptions("L01",getParameter("invoiceKey"),"")%>>
                    <yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/@InvoiceNo"/></a>
                </td>
                <td class="tablecolumn">
					<% if(showOrderNo("DerivedOrderInvoice","Order")) {%>
						<a <%=getDetailHrefOptions("L02", getValue("DerivedOrderInvoice", "xml:/OrderInvoice/Order/@DocumentType"), getParameter("orderKey"), "")%>>
	                    <yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/Order/@OrderNo"/>
						</a>
					<%} else {%>
						<yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/Order/@OrderNo"/>
					<%}%>
                </td>
                <td class="tablecolumn">
                    <%=displayDocumentDescription(getValue("DerivedOrderInvoice", "xml:/OrderInvoice/Order/@DocumentType"), (YFCElement) request.getAttribute("DocumentParamsList"))%>
                </td>
                <td class="tablecolumn"><%=getI18N(getValue("DerivedOrderInvoice", "xml:/OrderInvoice/@InvoiceType"))%></td>
                <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:DerivedOrderInvoice:/OrderInvoice/@TotalAmount")%>">
                    <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/@TotalAmount"/>&nbsp;<%=curr0[1]%>
                </td>
                <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:DerivedOrderInvoice:/OrderInvoice/@AmountCollected")%>">
                    <% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;<yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/@AmountCollected"/>&nbsp;<%=curr1[1]%>
                </td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>
