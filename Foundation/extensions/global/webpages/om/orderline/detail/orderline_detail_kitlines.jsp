<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="orderline.jspf"%>

<%	if(equals("Y",resolveValue("xml:/OrderLineDetail/@IsBundleParent"))) {
	
		YFCElement OrderInput = YFCDocument.parse("<Order/>").getDocumentElement();		OrderInput.setAttribute("OrderHeaderKey",resolveValue("xml:/OrderLineDetail/@OrderHeaderKey"));
		YFCElement orderTemplate = YFCDocument.parse("<Order OrderHeaderKey=\"\"><OrderLines><OrderLine  OrderLineKey=\"\" KitCode=\"\"><Item ItemID=\"\"></Item><BundleParentLine OrderLineKey=\"\"></BundleParentLine></OrderLine></OrderLines></Order>").getDocumentElement();
		//System.out.println("orderTemplate :" + orderTemplate);
		%>		
			 <yfc:callAPI apiName="getOrderDetails" inputElement="<%=OrderInput%>"  outputNamespace="Order" /> 
		<% 
		YFCElement OrderDoc = (YFCElement)request.getAttribute("Order");
		//System.out.println("OrderDoc :" + OrderDoc);
		rearrangeBundleComponents(OrderDoc);
		request.setAttribute("Order",OrderDoc);
		} 
YFCElement orderlineDoc = (YFCElement)request.getAttribute("OrderLine");
String lineid = resolveValue("xml:/OrderLineDetail/@OriginalLineItemClicked");

%>
<table class="view" width="100%">
<tr>
<td colspan="6">
	<table>
		<tr>
		    <td class="detaillabel" >
		        <yfc:i18n>Kit_Code</yfc:i18n>
		    </td>
		    <td class="protectedtext">
		    	<%=getComboText("xml:KitCodeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/OrderLine/@KitCode", true)%>
		    </td>
		</tr>
	</table>
</tr>
</table>

<table class="table" width="100%">
<thead>
<tr><% if(equals("Y",resolveValue("xml:/OrderLineDetail/@IsBundleParent"))) { %>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td> <%}%>
    <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
    <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
    <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/KitLineTranQuantity/@TransactionalUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
    <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
    <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/@UnitCost")%>"><yfc:i18n>Unit_Cost</yfc:i18n></td>
    <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/KitLineTranQuantity/@KitQty")%>"><yfc:i18n>Qty_Per_Kit</yfc:i18n></td>
    <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/KitLineTranQuantity/@ComponentQty")%>"><yfc:i18n>Component_Qty</yfc:i18n></td>
</tr>
</thead>
<tbody>
        <yfc:loopXML binding="xml:/OrderLine/KitLines/@KitLine" id="KitLine">
            <yfc:makeXMLInput name="invItemKey">
                <yfc:makeXMLKey binding="xml:/InventoryItem/@ItemID" value="xml:KitLine:/KitLine/@ItemID" ></yfc:makeXMLKey>
                <yfc:makeXMLKey binding="xml:/InventoryItem/@ProductClass" value="xml:KitLine:/KitLine/@ProductClass" ></yfc:makeXMLKey>
                <yfc:makeXMLKey binding="xml:/InventoryItem/@UnitOfMeasure" value="xml:KitLine:/KitLine/KitLineTranQuantity/@TransactionalUOM" ></yfc:makeXMLKey>
                <yfc:makeXMLKey binding="xml:/InventoryItem/@OrganizationCode" value="xml:/OrderLine/Order/@SellerOrganizationCode" ></yfc:makeXMLKey>
				<% if(isShipNodeUser()) { %>
					<yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:CurrentUser:/User/@Node"/>
				<%} else {%>
	                <yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:/OrderLine/@ShipNode" ></yfc:makeXMLKey>
				<%}%>
            </yfc:makeXMLInput>
            <tr>
                <td nowrap="true" class="tablecolumn">
                    <% 
                        String sNode = resolveValue("xml:/OrderLine/@ShipNode");
                        if(isVoid(sNode)) {%>
                            <a <%=getDetailHrefOptions("L01",getParameter("invItemKey"),"")%>><yfc:getXMLValue binding="xml:/KitLine/@ItemID"></yfc:getXMLValue></a>&nbsp;
                        <%} else {%>
                            <a <%=getDetailHrefOptions("L02",getParameter("invItemKey"),"")%>><yfc:getXMLValue binding="xml:/KitLine/@ItemID"></yfc:getXMLValue></a>&nbsp;
                        <% } %>
                </td>
                <td nowrap="true" class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/KitLine/@ProductClass"></yfc:getXMLValue>
                </td>
                <td nowrap="true" class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@TransactionalUOM"></yfc:getXMLValue>
                </td>
                <td nowrap="true"class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/KitLine/@ItemShortDesc"></yfc:getXMLValue>
                </td>
                <td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:KitLine:/KitLine/@UnitCost")%>">
                    <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/KitLine/@UnitCost"></yfc:getXMLValue>&nbsp;<%=curr0[1]%>
                </td>
                <td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:KitLine:/KitLine/KitLineTranQuantity/@KitQty")%>">
                    <yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@KitQty"></yfc:getXMLValue>
                </td>
                <td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:KitLine:/KitLine/KitLineTranQuantity/@ComponentQty")%>">
                    <yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@ComponentQty"></yfc:getXMLValue>
                </td>
            </tr>
        </yfc:loopXML>

		<yfc:loopXML name="Order" binding="xml:/Order/OrderLines/@OrderLine" id="OrderLine">
		<%if(resolveValue("xml:OrderLine:/OrderLine/@ItemGroupCode").equals("PROD")){%>

             <yfc:makeXMLInput name="invItemKey">
                <yfc:makeXMLKey binding="xml:/InventoryItem/@ItemID" value="xml:/OrderLine/Item/@ItemID" ></yfc:makeXMLKey>
                <yfc:makeXMLKey binding="xml:/InventoryItem/@ProductClass" value="xml:/OrderLine/Item/@ProductClass" ></yfc:makeXMLKey>
                <yfc:makeXMLKey binding="xml:/InventoryItem/@UnitOfMeasure" value="xml:/OrderLine/Item/@UnitOfMeasure" ></yfc:makeXMLKey>
                <yfc:makeXMLKey binding="xml:/InventoryItem/@OrganizationCode" value="xml:/Order/@SellerOrganizationCode" ></yfc:makeXMLKey>
				<% if(isShipNodeUser()) { %>
					<yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:CurrentUser:/User/@Node"/>
				<%} else {%>
	                <yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:/OrderLine/@ShipNode" ></yfc:makeXMLKey>
				<%}%>
            </yfc:makeXMLInput>

			<yfc:makeXMLInput name="orderLineKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey"/>
            </yfc:makeXMLInput>
            <tr>
			<td class="tablecolumn" sortValue="<%=getNumericValue("xml:/OrderLine/@PrimeLineNo")%>">
				<a <%=getDetailHrefOptions("L03", getParameter("orderLineKey"), "")%>>
				<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/></a>			
			</td>
            <td nowrap="true" class="tablecolumn"> 
				<%if(lineid.equals(resolveValue("xml:/OrderLine/@PrimeLineNo"))){%><b><%}%>
				  <% 
                        String sNode = resolveValue("xml:/OrderLine/@ShipNode");
						String appendedSpace = resolveValue("xml:/OrderLine/@AppendedSpace");
                        if(isVoid(sNode)) {%>
							<% if(!"BUNDLE".equals(resolveValue("xml:/OrderLine/@KitCode"))){%>
								<%=HTMLEncode.htmlUnescape(appendedSpace)%>
							   <a <%=getDetailHrefOptions("L01",getParameter("invItemKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue></a>&nbsp;
							<%} else {%>
								<%=HTMLEncode.htmlUnescape(appendedSpace)%>

								<yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue>
							<% } %>
                        <%} else {%>
                           <% if(!"BUNDLE".equals(resolveValue("xml:/OrderLine/@KitCode"))){%>
							<%=HTMLEncode.htmlUnescape(appendedSpace)%>

                              <a <%=getDetailHrefOptions("L02",getParameter("invItemKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue></a>&nbsp;
							<%} else {%>
							<%=HTMLEncode.htmlUnescape(appendedSpace)%>

							  <yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue>
					  <% }
							}
						%>
                </td>
                <td nowrap="true" class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"></yfc:getXMLValue>
                </td>
                <td nowrap="true" class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/OrderLine/Item/@UnitOfMeasure"></yfc:getXMLValue>
                </td>
                <td nowrap="true"class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"></yfc:getXMLValue>
                </td>
                <td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine/Item/@UnitCost")%>">
                    <% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/Item/@UnitCost"></yfc:getXMLValue>&nbsp;<%=curr1[1]%>
                </td>
                <td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:/OrderLine/@KitQty")%>">
                    <yfc:getXMLValue binding="xml:/OrderLine/@KitQty"></yfc:getXMLValue>
                </td>
                <td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:/OrderLine/@OrderedQty")%>">
                    <yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"></yfc:getXMLValue>
                </td>
            </tr>
			<%}%>
        </yfc:loopXML>
</tbody>
</table>
