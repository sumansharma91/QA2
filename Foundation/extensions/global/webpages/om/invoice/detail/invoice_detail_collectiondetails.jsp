<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<table class="table" width="100%">

<thead>
    <tr>
        <td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/CollectionDetails/CollectionDetail/@AmountCollected")%>"><yfc:i18n>Amount_Collected</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/InvoiceDetail/InvoiceHeader/CollectionDetails/CollectionDetail/@Reference")%>"><yfc:i18n>References</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <%  ArrayList collectionList = getLoopingElementList("xml:/InvoiceDetail/InvoiceHeader/CollectionDetails/@CollectionDetail");
        for (int CollectionDetailCounter = 0; CollectionDetailCounter < collectionList.size(); CollectionDetailCounter++) {
           
           YFCElement singleCollection = (YFCElement) collectionList.get(CollectionDetailCounter);
           pageContext.setAttribute("CollectionDetail", singleCollection); %>
       
        <%request.setAttribute("CollectionDetail", (YFCElement)pageContext.getAttribute("CollectionDetail"));%>
        <tr>
            <td class="tablecolumn" sortValue="<%=getNumericValue("xml:CollectionDetail:/CollectionDetail/@AmountCollected")%>">
                <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/CollectionDetail/@AmountCollected"/>&nbsp;<%=curr0[1]%>
            </td>            
			<% String chargeTypeDesc = "ChargeType_" + resolveValue("xml:/CollectionDetail/@ChargeType"); %>
            <td class="tablecolumn">
            	<% if (!isVoid(getValue("CollectionDetail","xml:/CollectionDetail/PaymentMethod/@PaymentKey"))) {%>
                <table width="100%" class="table">
                    <tr>
                        <td height="100%">
                            <jsp:include page="/om/order/detail/order_detail_paymenttype_collections.jsp" flush="true">
                                <jsp:param name="PrePathId" value="CollectionDetail/PaymentMethod"/>
                                <jsp:param name="ShowAdditionalParams" value="N"/>
                            </jsp:include>
                        </td>
                    </tr>
                </table>


                <% } else if (equals("TRANSFER_IN",getValue("CollectionDetail","xml:/CollectionDetail/@ChargeType"))){
				%>
					<table width="100%" class="table">
						<tr>
							<td height="100%">
							  <yfc:makeXMLInput name="TransferFromOhKey">
									<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/CollectionDetail/@TransferFromOhKey" />
							   </yfc:makeXMLInput>
								<a <%=getDetailHrefOptions("L03", getValue("CollectionDetail", 		
									"xml:/CollectionDetail/TransferFromOrder/@DocumentType"), getParameter("TransferFromOhKey"), "")%>>
									<yfc:i18n><%=chargeTypeDesc%></yfc:i18n>
								</a>
							</td>
						</tr>
					</table>
						
				<% } else if( equals("TRANSFER_OUT",getValue("CollectionDetail","xml:/CollectionDetail/@ChargeType"))) {
				%>
					<table width="100%" class="table">
						<tr>
							<td height="100%">
							<yfc:makeXMLInput name="TransferToOhKey">
										<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/CollectionDetail/@TransferToOhKey" />
							</yfc:makeXMLInput>
                            <a <%=getDetailHrefOptions("L04", getValue("CollectionDetail", 		
								"xml:/CollectionDetail/TransferToOrder/@DocumentType"), getParameter("TransferToOhKey"), "")%>>
								<yfc:i18n><%=chargeTypeDesc%></yfc:i18n>
							</a>
							</td>
						</tr>
					</table>

				<% } else if( equals("DEFERRED_CREDIT",getValue("CollectionDetail","xml:/CollectionDetail/@ChargeType"))) {
				%>
					<table width="100%" class="table">
						<tr>
							<td height="100%">
								<yfc:i18n><%=chargeTypeDesc%></yfc:i18n>
							</td>
						</tr>
					</table>

                <% } else {%>                    
				<table width="100%" class="table">
					<tr>
						<yfc:callAPI apiID='AP1'/>
						<yfc:makeXMLInput name="invoiceKey">
		                	<yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@InvoiceKey" value="xml:/CollectionDetail/@OrderInvoiceKey"/>
		               	</yfc:makeXMLInput>
						<td><yfc:i18n>Invoice_#</yfc:i18n></td>						
		                <td>
							<a <%=getDetailHrefOptions("L02", getParameter("invoiceKey"), "")%>>
								<yfc:getXMLValue binding="xml:ExtraInvoices:/InvoiceDetail/InvoiceHeader/@InvoiceNo"/>
							</a>
						</td>
					</tr>
				</table>
                <%}%>
            </td>
        </tr>                       
    <% } %>
</tbody>
</table>
