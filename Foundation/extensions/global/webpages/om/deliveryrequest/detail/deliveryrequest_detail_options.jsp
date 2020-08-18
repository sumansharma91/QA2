<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf"%>
<%@include file="/console/jsp/modificationutils.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="javascript">
	document.body.attachEvent("onunload", processSaveRecords);
</script>

	
	<table  editable="false" width="100%" class="table" cellspacing="0" cellpadding="0" ID="specialChange" >
		<thead>
			<tr>
				<td class="tablecolumnheader" sortable="no">&nbsp;</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Option_ID</yfc:i18n>
				</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Description</yfc:i18n>
				</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Price</yfc:i18n>
				</td>
			</tr>
		</thead>

		<% Integer ctr = new Integer(0); %>
		<tbody>
			<yfc:loopXML binding="xml:/OrderLine/OrderLineOptions/@OrderLineOption" id="OrderLineOption">
				<tr>
					<td class="checkboxcolumn">
						<% YFCElement orderLineOptionElem = (YFCElement) pageContext.getAttribute("OrderLineOption");
						   orderLineOptionElem.setAttribute("Action", "MODIFY");
						%>

						<input class="checkbox" type="checkbox" yfcCheckedValue="MODIFY" yfcUnCheckedValue="REMOVE" <%=yfsGetCheckBoxOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+OrderLineOptionCounter+"/@Action", "xml:/OrderLineOption/@Action", "MODIFY", "xml:/OrderLine/AllowedModifications")%>  />

						<input type="hidden" <%= getTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+OrderLineOptionCounter+"/@OptionItemID",
						"xml:/OrderLineOption/@OptionItemID") %> />

						<input type="hidden" <%= getTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+OrderLineOptionCounter+"/@OptionUOM",
						"xml:/OrderLineOption/@OptionUOM") %> />

						<input type="hidden" <%= getTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+OrderLineOptionCounter+"/@OptionDescription",
						"xml:/OrderLineOption/@OptionDescription") %> />

					</td>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:/OrderLineOption/@OptionItemID" />
					</td>
					<td class="tablecolumn">
							<yfc:getXMLValue  binding="xml:/OrderLineOption/@OptionDescription" />
					</td>
					<td class="tablecolumn">
				            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;
							<yfc:getXMLValue  binding="xml:/OrderLineOption/@OptionPrice" />&nbsp;
				            <%=curr0[1]%>
					</td>
				</tr>
				<% ctr = OrderLineOptionCounter;  %>
			</yfc:loopXML>

			<%	int iCtr = ctr.intValue(); %>
			<yfc:loopXML name="additionalOptions" binding="xml:additionalOptions:/OrderLine/AdditionalOptions/@ItemOption" id="ItemOption">
				<% ctr = new Integer (ItemOptionCounter.intValue() + iCtr );   %>
				<tr>
					<yfc:callAPI  apiID="AP1" />
					<td class="checkboxcolumn">
						<input class="checkbox" type="checkbox" yfcCheckedValue="CREATE" yfcUnCheckedValue=" " <%=yfsGetCheckBoxOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+ctr+"/@Action", "xml:/OrderLineOption/@Action", " ", "xml:/OrderLine/AllowedModifications")%>  />

						<input type="hidden" <%= getTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+ctr+"/@OptionItemID",
						"xml:/ItemOption/@ItemID") %> />

						<input type="hidden" <%= getTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+ctr+"/@OptionUOM",
						"xml:/ItemOption/@UnitOfMeasure") %> />

						<input type="hidden" <%= getTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+ctr+"/@OptionDescription",
						"xml:/ItemOption/PrimaryInformation/@Description") %> />

						<!-- <input type="hidden" <%= getTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineOptions/OrderLineOption_"+ctr+"/@UnitPrice",
						"xml:/ItemOption/@OptionPrice") %> /> -->

					</td>

					<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/ItemOption/@ItemID" />
					</td>
					<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/ItemOption/PrimaryInformation/@Description" />
					</td>
					<td class="tablecolumn">
						<% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;
						<yfc:getXMLValue  binding="xml:/ItemOption/@OptionPrice" />&nbsp;
						<%=curr1[1]%>
					</td>
				</tr>
			</yfc:loopXML>
		</tbody>
	</table>
