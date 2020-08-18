<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="../console/scripts/om.js"></script>
<%
	String className = getParameter("className");
	String loopXMLBinding = "xml:ReturnOrder:/Order/OrderLines/@OrderLine";
	String loopXMLNamespace = getParameter("LoopXMLNamespace");
	if(!isVoid(loopXMLNamespace)){
		loopXMLBinding = "xml:/" + loopXMLNamespace + "/OrderLines/@OrderLine";
	}	

	String sortString = " sortable='no'";
	String allowSorting = getParameter("AllowSorting");
	if(equals(allowSorting, "Y")){
		sortString = " ";
	}
%>
<yfc:callAPI apiID="AP3"/>	<!-- commonCodeList -->
<yfc:callAPI apiID="AP4"/>	<!-- getOrderDetails -->

<table class="table" >
	<tbody>
		<tr class='<%=className%>' >
			<td width="10%" >				&nbsp;			</td>
			<td width="80%" style="border:1px solid black">
				<table class="table" width="100%" cellspacing="0"  >
					<thead>
						<tr>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>Line_#</yfc:i18n></td>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>Date</yfc:i18n></td>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>Item_ID</yfc:i18n><br/><yfc:i18n>Description</yfc:i18n></td>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>Product_Class</yfc:i18n></td>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>UOM</yfc:i18n></td>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>Reason_Code</yfc:i18n></td>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>Quantity</yfc:i18n></td>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>Amount</yfc:i18n></td>
							<td class="tablecolumnheader" <%=sortString%>><yfc:i18n>Status</yfc:i18n></td>
						</tr>
					</thead>
					<tbody>
						<yfc:loopXML binding="<%=loopXMLBinding%>" id="OrderLine">
							<tr>
								<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/></td>
								<td class="tablecolumn" <% if(!isVoid(sortString)){ %> sortValue='<%=getDateValue("xml:/OrderLine/@Createts")%>' 	<%}%>><yfc:getXMLValue binding="xml:/OrderLine/@Createts"/></td>
								<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/><br/>
									<%=getLocalizedOrderLineDescription("OrderLine")%>
								</td>
								<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
								<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
								<td class="tablecolumn">
									<%=getComboText("xml:ReasonCodeList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/OrderLine/@ReturnReason",true)%>
								</td>
								<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"/></td>
								<td class="numerictablecolumn" nowrap="true" >
									<% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@LineTotal"/>&nbsp;<%=curr0[1]%>
								</td>
								<td class="tablecolumn"><%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%></td>
							</tr>
						</yfc:loopXML> 
					</tbody>
				</table>
			</td>
			<td width="10%" >				&nbsp;			</td>
		</tr>
	</tbody>
</table>	
