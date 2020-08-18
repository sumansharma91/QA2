<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>

<table class="table" cellspacing="0" width="100%">
<thead>
    <tr>
		<td class="checkboxheader" sortable="no">
            <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemShortDesc")%>"><yfc:i18n>Item_Description</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@PromisedApptStartDate")%>"><yfc:i18n>Appointment</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/ServiceAssociations/ServiceAssociation/@ServiceTimeOffset")%>"><yfc:i18n>Service_Offset</yfc:i18n>&nbsp<yfc:i18n>(Hrs)</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LinePriceInfo/@LineTotal")%>"><yfc:i18n>Amount</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderLine/ServiceAssociations/@ServiceAssociation" id="ServiceAssociation">
		<% if(!isVoid(resolveValue("xml:/ServiceAssociation/ServiceLine/@OrderLineKey")))	 
			{  
				request.setAttribute("ServiceAssociation", pageContext.getAttribute("ServiceAssociation")); 
				
				%>		
				<yfc:callAPI apiID='AP1'/>
			<%	if (equals(getValue("ServiceLine","xml:/OrderLine/@ItemGroupCode"),"PS")) 
					{		%>
						<tr>
							<yfc:makeXMLInput name="associationKey">
								<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:OrderLine:/OrderLine/Order/@OrderHeaderKey" />
								<yfc:makeXMLKey binding="xml:/Order/ProductServiceAssocs/ProductServiceAssoc/ProductLine/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
								<yfc:makeXMLKey binding="xml:/Order/ProductServiceAssocs/ProductServiceAssoc/ServiceLine/@OrderLineKey" value="xml:ServiceLine:/OrderLine/@OrderLineKey" />
							</yfc:makeXMLInput>
							<yfc:makeXMLInput name="serviceLineKey">
								<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:OrderLine:/OrderLine/Order/@OrderHeaderKey" />
								<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:ServiceLine:/OrderLine/@OrderLineKey" />
							</yfc:makeXMLInput>
							<td class="checkboxcolumn"><input type="checkbox" value='<%=getParameter("associationKey")%>' name="chkEntityKeyPS"/></td>
							<td class="tablecolumn" sortValue="<%=getNumericValue("xml:ServiceLine:/OrderLine/@PrimeLineNo")%>">
								<a <%=getDetailHrefOptions("L01", getParameter("serviceLineKey"), "")%>>
										<yfc:getXMLValue binding="xml:ServiceLine:/OrderLine/@PrimeLineNo"/></a>
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue name="ServiceLine" binding="xml:ServiceLine:/OrderLine/Item/@ItemID" />
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue binding="xml:ServiceLine:/OrderLine/Item/@ItemShortDesc" />
							</td>

							<td class="tablecolumn">
							<%-- cr 34417 --%>
							<%
								String sTimeWindow = displayTimeWindow(resolveValue("xml:ServiceLine:/OrderLine/@PromisedApptStartDate"), resolveValue("xml:ServiceLine:/OrderLine/@PromisedApptEndDate"), resolveValue("xml:ServiceLine:/OrderLine/@Timezone") );
								
								if(!isVoid(sTimeWindow) )
								{
							%>
									<%=sTimeWindow%>
									<%=showTimeZoneIcon(resolveValue("xml:ServiceLine:/OrderLine/@Timezone"), getLocale().getTimezone())%>
							<%	} %>
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue binding="xml:/ServiceAssociation/@ServiceTimeOffset" />
							</td>
							<td class="numerictablecolumn" nowrap="true" sortValue="<%=getNumericValue("xml:ServiceLine:/OrderLine/LinePriceInfo/@LineTotal")%>">
									<% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:ServiceLine:/OrderLine/LinePriceInfo/@LineTotal"/>&nbsp;<%=curr0[1]%>
							</td>
							<td class="tablecolumn">
								<%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%>
							</td>
						</tr>
			<%	}	%>
	<%	}	%>
	</yfc:loopXML> 
</tbody>
</table>
