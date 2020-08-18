<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	String documentType = resolveValue("xml:/InvoiceDetail/InvoiceHeader/Order/@DocumentType");
	String useFeeNotDiscount = "N";
	if (!isVoid(documentType)) {
		//Create input
		YFCElement getDocumentTypeListInputElem = YFCDocument.createDocument("DocumentParams").getDocumentElement();
		getDocumentTypeListInputElem.setAttribute("DocumentType",documentType);
		//Create Template
		String getDocumentTypeListTemplateStr = "<DocumentParamsList>" + "<DocumentParams DocumentParams=\"\" />" + "</DocumentParamsList>" ;
		YFCElement getDocumentTypeListTemplateElem = YFCDocument.parse(getDocumentTypeListTemplateStr).getDocumentElement();
	%>
		<yfc:callAPI apiName="getDocumentTypeList" inputElement="<%=getDocumentTypeListInputElem%>" templateElement="<%=getDocumentTypeListTemplateElem%>" outputNamespace="DocumentParamsList"/>
	<%
		YFCElement documentParamsListElem = (YFCElement) request.getAttribute("DocumentParamsList");
		if (documentParamsListElem != null) {
			YFCElement documentParamsElem = documentParamsListElem.getChildElement("DocumentParams");
			if (documentParamsElem != null) {
				String documentParamsStr = documentParamsElem.getAttribute("DocumentParams");
				if (!isVoid(documentParamsStr)) {
					YFCElement documentParamsAttributeElem = YFCDocument.parse(documentParamsStr).getDocumentElement();
					if (documentParamsAttributeElem != null) {
						YFCElement orderFulfillmentElem = documentParamsAttributeElem.getChildElement("ORDER_FULFILLMENT");
						if (orderFulfillmentElem != null) {
							String isReturnDocument = orderFulfillmentElem.getAttribute("isReturnDocument");
							if (equals(isReturnDocument,"Y")) {
								useFeeNotDiscount = "Y";								
							}
						}
					}					
				}				
			}
		}
	}
	request.setAttribute("UseFeeNotDiscount",useFeeNotDiscount);	
%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<table class="view" width="100%">
<tr>
    <yfc:makeXMLInput name="orderKey">
        <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/InvoiceDetail/InvoiceHeader/Order/@OrderHeaderKey" />
    </yfc:makeXMLInput>
	<yfc:makeXMLInput name="derivedOrderKey">
        <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/InvoiceDetail/InvoiceHeader/@DerivedFromOrderKey" />
    </yfc:makeXMLInput>
	<% if (!equals(useFeeNotDiscount,"Y")) { %>
		<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
	<% } else { %>
		<td class="detaillabel" ><yfc:i18n>Return_#</yfc:i18n></td>
	<% } %>
    <td class="protectedtext">
		<% if(showOrderNo("InvoiceDetail","Order")) {%>
	        <a <%=getDetailHrefOptions("L01", resolveValue("xml:/InvoiceDetail/InvoiceHeader/Order/@DocumentType"), getParameter("orderKey"),"")%>>
				<yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/Order/@OrderNo"/>
			</a>&nbsp;
		<%} else {%>
			<yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/Order/@OrderNo"/>
		<%}%>
    </td>
    <td class="detaillabel" nowrap="true"><yfc:i18n>Collected_Through_AR?</yfc:i18n></td>
	<%	String collectedEx = resolveValue("xml:/InvoiceDetail/InvoiceHeader/@CollectedExternally");
		if(!equals(collectedEx,"Y")) {
			collectedEx = "N";
		}
	%>
    <td class="protectedtext"><%=collectedEx%></td>
    <td class="detaillabel" ><yfc:i18n>Currency</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:CurrencyList:/CurrencyList/Currency/@CurrencyDescription"/></td>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Invoice_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@InvoiceNo"/></td>
    <td class="detaillabel" ><yfc:i18n>Invoice_Type</yfc:i18n></td>
    <td class="protectedtext"><%=getI18N(getValue("InvoiceDetail", "xml:/InvoiceDetail/InvoiceHeader/@InvoiceType"))%></td>
	<% if(!isVoid(resolveValue("xml:/InvoiceDetail/InvoiceHeader/@DerivedFromOrderKey"))) {%>
	    <td class="detaillabel" ><yfc:i18n>Derived_From_Order_#</yfc:i18n></td>
		<td class="protectedtext">
			<% if(showOrderNo("DerivedOrder","Order")) {%>
				<a <%=getDetailHrefOptions("L01", resolveValue("xml:DerivedOrder:/Order/@DocumentType"), getParameter("derivedOrderKey"),"")%>>
					<yfc:getXMLValue binding="xml:DerivedOrder:/Order/@OrderNo"/>
				</a>
			<%} else {%>
				<yfc:getXMLValue binding="xml:DerivedOrder:/Order/@OrderNo"/>
			<%}%>
		</td>
	<%}%>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Reference</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/InvoiceDetail/InvoiceHeader/@Reference1"/></td>
</tr>
</table>
