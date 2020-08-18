<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Release_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Buyer</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Order_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Ship_Node</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
	    <yfc:loopXML binding="xml:/OrderReleaseList/@OrderRelease" id="OrderRelease" >
            <tr>
				<yfc:makeXMLInput name="orderReleaseKey">
					<yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey" />
				</yfc:makeXMLInput>
		        <td class="checkboxcolumn"> 
					<input type="checkbox" value='<%=getParameter("orderReleaseKey")%>' name="EntityKey" yfcMultiSelectCounter='<%=OrderReleaseCounter%>' yfcMultiSelectValue1='<%=getValue("OrderRelease", "xml:/OrderRelease/@OrderReleaseKey")%>' yfcMultiSelectValue2='Add'/>
				</td>
	            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderNo"/></td>
				<td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderRelease:/OrderRelease/@ReleaseNo")%>">
					<a href="javascript:showDetailFor('<%=getParameter("orderReleaseKey")%>');">
					<yfc:getXMLValue binding="xml:/OrderRelease/@ReleaseNo"/></a>
				</td>
                <td class="tablecolumn">
                        <%=displayOrderStatus(getValue("OrderRelease","xml:/OrderRelease/@MultipleStatusesExist"),getValue("OrderRelease","xml:/OrderRelease/@MaxOrderReleaseStatusDesc"),true)%>
                    <% if (equals("Y", getValue("Order", "xml:/OrderRelease/@HoldFlag"))) { %>
                        <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%>/>
                    <% } %>
                    <% if (equals("Y", getValue("OrderRelease","xml:/OrderRelease/@isHistory"))){ %>
                        <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order")%>/>
                    <% } %>
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderRelease/@EnterpriseCode"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderRelease/Order/@BuyerOrganizationCode"/></td>
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:/OrderRelease/Order/@OrderDate")%>"><yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderDate"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderRelease/@ShipNode"/></td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>
