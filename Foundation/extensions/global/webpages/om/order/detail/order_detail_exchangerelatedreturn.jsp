<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/order.jspf" %>
<%@include file="/console/jsp/paymentutils.jspf" %>
<%@ page import="com.yantra.util.*" %>

<table class="view" width="100%">
    <tr>
		<yfc:makeXMLInput name="orderKey">
			<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/ReturnOrderForExchange/@OrderHeaderKey" ></yfc:makeXMLKey>
		</yfc:makeXMLInput>
		<td class="detaillabel" ><yfc:i18n>0003_Order_#</yfc:i18n></td>
		<td class="protectedtext">
			<a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
				<yfc:getXMLValue binding="xml:/ReturnOrderForExchange/@OrderNo"></yfc:getXMLValue>
			</a>&nbsp;
		</td>
        <td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
        <td class="protectedtext">
            <% if (isVoid(getValue("ReturnOrderForExchange", "xml:/ReturnOrderForExchange/@Status"))) {%>
                [<yfc:i18n>Draft</yfc:i18n>]
            <% } else { %>
                <a <%=getDetailHrefOptions("L02", getParameter("orderKey"), "ShowReleaseNo=Y")%>><yfc:getXMLValue binding="xml:/ReturnOrderForExchange/@Status"/></a>
            <% } %>
        </td>
        <td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReturnOrderForExchange/@EnterpriseCode"/></td>
	</tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
        <td class="protectedtext">        
				<yfc:getXMLValue binding="xml:/ReturnOrderForExchange/@BuyerOrganizationCode"/>
        </td>
        <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
        <td class="protectedtext">
				<yfc:getXMLValue binding="xml:/ReturnOrderForExchange/@SellerOrganizationCode"/>
        </td>
        <td class="detaillabel" ><yfc:i18n>0003_Order_Date</yfc:i18n></td>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReturnOrderForExchange/@OrderDate"/>
		</td>
    </tr>
		<td colspan="6">
			<jsp:include page="/om/order/detail/order_detail_return_order_lines.jsp" flush="true" >
				<jsp:param name="LoopXMLNamespace" value="ReturnOrderForExchange"/>
				<jsp:param name="AllowSorting" value="Y"/>
			</jsp:include>
		</td>

	<tr>
	</tr>
</table>

