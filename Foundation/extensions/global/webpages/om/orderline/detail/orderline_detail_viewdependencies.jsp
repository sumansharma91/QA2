<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<yfc:callAPI apiID="AP1"/> <!-- getOrderLineList -->

<script language="javascript">
    function window.onload() {
        if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
            return;
        }       
        <% if (isVoid(resolveValue("xml:/OrderLineList/OrderLine/@OrderLineKey"))) { %>
			window.close();
		<%}%>        
    }
</script>

<% boolean isHistory=equals(resolveValue("xml:/OrderLine/@isHistory"),"Y"); %>

<table class="view" width="100%">
<tr>
    <td>
        <table class="table" cellspacing="0" width="100%" >
            <thead>
                <tr>
                    <td class="checkboxheader" sortable="no">
                        <%if( !isHistory ) { /* Then checkboxes are useless, since the only action has been removed */ %>
                            <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
                        <%}else { %>
                            &nbsp;
                        <%}%>
                    </td>
                    <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
                    <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
                    <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
                    <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/OrderLineTranQuantity/@TransactionalUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
                    <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/Item/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
                    <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/@ReceivingNode")%>"><yfc:i18n>Recv_Node</yfc:i18n></td>
                    <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/@ShipNode")%>"><yfc:i18n>Ship_Node</yfc:i18n></td>
                    <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/@ReqShipDate")%>"><yfc:i18n>Ship_Date</yfc:i18n></td>
                    <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/OrderLineTranQuantity/@OrderedQty")%>"><yfc:i18n>Line_Qty</yfc:i18n></td>
                    <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/LineOverallTotals/@LineTotal")%>"><yfc:i18n>Amount</yfc:i18n></td>
                    <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLineList/OrderLine/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
                </tr>
            </thead>
            <tbody>
                <yfc:loopXML name="OrderLineList" binding="xml:/OrderLineList/@OrderLine" id="dependentLine">
                    <tr>
                        <yfc:makeXMLInput name="dependentLineKey">
                            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:dependentLine:/OrderLine/@OrderLineKey"/>
                        </yfc:makeXMLInput>       
                        <td class="checkboxcolumn" >
                            <%if( !isHistory ) { %>
	                            <input type="checkbox" value='<%=getParameter("dependentLineKey")%>' name="chkEntityKey" dependentLineCounter='<%=dependentLineCounter%>' orderLineKeyVal='<%=resolveValue("xml:dependentLine:/OrderLine/@OrderLineKey")%>' removeDependencyVal="Y"/>
                            <%}else {%>
                                &nbsp;
                            <%}%>
							<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + dependentLineCounter + "/@OrderLineKey","xml:dependentLine:/OrderLine/@OrderLineKey")%> />
							<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + dependentLineCounter + "/Dependency/@RemoveDependency","N")%> />
                        </td>
                        <td class="tablecolumn" sortValue="<%=getNumericValue("xml:dependentLine:/OrderLine/@PrimeLineNo")%>">            
            				<% if(showOrderLineNo("dependentLine","Order")) {%>
	                            <a <%=getDetailHrefOptions("L01", getParameter("dependentLineKey"), "")%>>
		                            <yfc:getXMLValue binding="xml:dependentLine:/OrderLine/@PrimeLineNo"/>
								</a>
							<%} else {%>
								<yfc:getXMLValue binding="xml:dependentLine:/OrderLine/@PrimeLineNo"/>
							<%}%>
                        </td>                
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:dependentLine:/OrderLine/Item/@ItemID"/></td>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:dependentLine:/OrderLine/Item/@ProductClass"/></td>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:dependentLine:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:dependentLine:/OrderLine/Item/@ItemDesc"/></td>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:dependentLine:/OrderLine/@ReceivingNode"/></td>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:dependentLine:/OrderLine/@ShipNode"/></td>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:dependentLine:/OrderLine/@ReqShipDate"/></td>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:dependentLine:/OrderLine/OrderLineTranQuantity/@OrderedQty"/></td>
                        <td class="numerictablecolumn" nowrap="true" sortValue="<%=getNumericValue("xml:dependentLine:/OrderLine/LineOverallTotals/@LineTotal")%>">
                            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;
                            <yfc:getXMLValue binding="xml:dependentLine:/OrderLine/LineOverallTotals/@LineTotal"/>&nbsp;
                            <%=curr0[1]%>
                        </td>
                        <td class="tablecolumn">
                            <a <%=getDetailHrefOptions("L02", getParameter("orderLineKey"),"ShowReleaseNo=Y")%>>
                            <%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%></a>
                        </td>
                    </tr>
                </yfc:loopXML>            
            </tbody>
        </table>
    </td>
</tr>
<tr>
    <td>
        <fieldset>
        <legend><yfc:i18n>Dependency_Properties</yfc:i18n></legend> 
            <table class="view" width="100%">
                <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@OrderLineKey","xml:/OrderLine/@OrderLineKey")%> />
                <tr>   
                    <td>
                        <input type="radio" class="radiobutton" <%=yfsGetRadioOptions("xml:/Order/OrderLines/OrderLine/@DependencyShippingRule","xml:/OrderLine/@DependencyShippingRule","01","xml:/OrderLine/AllowedModifications")%> onclick="setMergeNodeState(this.value)"/>
                        <yfc:i18n>Ship_Together</yfc:i18n>
                    </td>     
                    <td>
                        <input type="radio" class="radiobutton" <%=yfsGetRadioOptions("xml:/Order/OrderLines/OrderLine/@DependencyShippingRule","xml:/OrderLine/@DependencyShippingRule","02","xml:/OrderLine/AllowedModifications")%> onclick="setMergeNodeState(this.value)"/>
                        <yfc:i18n>Deliver_Together</yfc:i18n>
                    </td>   
                    <td class="detaillabel">
                        <yfc:i18n>Merge_Node</yfc:i18n>
                    </td>
                    <td>
                        <input id="MergeNode" <% if (equals(getValue("OrderLine","xml:/OrderLine/@DependencyShippingRule"),"01")) { %> contentEditable="false" <%}%> type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@MergeNode","xml:/OrderLine/@MergeNode","xml:/OrderLine/AllowedModifications")%>/>
                        <img <% if (equals(getValue("OrderLine","xml:/OrderLine/@DependencyShippingRule"),"01")) { %> disabled="true" <%}%> class="lookupicon" onclick="callLookup(this,'shipnode')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Merge_Node", "xml:/OrderLine/@MergeNode", "xml:/OrderLine/AllowedModifications")%> name="NodeLookUp"/>
                    </td> 
                </tr>               
            </table>
        </fieldset>
    </td>
</tr>
</table>
