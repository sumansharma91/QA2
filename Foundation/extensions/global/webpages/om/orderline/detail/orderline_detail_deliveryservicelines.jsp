<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="orderline.jspf"%>

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
       		<yfc:loopXML name="Order" binding="xml:/Order/OrderLines/@OrderLine" id="OrderLine">
			<%if(resolveValue("xml:OrderLine:/OrderLine/@ItemGroupCode").equals("DS")){%>
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
				<yfc:getXMLValue binding="xml:/OrderLine/@AppendedBundleComponentID"></yfc:getXMLValue>&nbsp;
                        
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
                    <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/Item/@UnitCost"></yfc:getXMLValue>&nbsp;<%=curr0[1]%>
                </td>
                <td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:KitLine:/KitLine/KitLineTranQuantity/@KitQty")%>">
                    <yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@KitQty"></yfc:getXMLValue>
                </td>
                <td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:KitLine:/KitLine/KitLineTranQuantity/@ComponentQty")%>">
                    <yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@ComponentQty"></yfc:getXMLValue>
                </td>
            </tr>
			<%}%>
        </yfc:loopXML>
</tbody>
</table>
