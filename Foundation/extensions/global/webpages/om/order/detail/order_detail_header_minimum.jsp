<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
            <input type="hidden" name="xml:/Order/@ModificationReasonCode" />
            <input type="hidden" name="xml:/Order/@ModificationReasonText"/>
            <input type="hidden" name="xml:/Order/@Override" value="N"/>
            <input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("Order", "xml:/Order/@DraftOrderFlag")%>'/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@EnterpriseCode"/></td>
        <td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
        <td class="protectedtext">        
        <yfc:makeXMLInput name="OrganizationKey" >
				<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Order/@BuyerOrganizationCode" />
			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L02",getParameter("OrganizationKey"),"")%> >
				<yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/>
			</a>
        </td>
        <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
        <td class="protectedtext">
        	<yfc:makeXMLInput name="OrganizationKey" >
				<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Order/@SellerOrganizationCode" />
			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L03",getParameter("OrganizationKey"),"")%> >
				<yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/>
			</a>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></td>
        <td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
        <td class="protectedtext">
            <% if (isVoid(getValue("Order", "xml:/Order/@Status"))) {%>
                [<yfc:i18n>Draft</yfc:i18n>]
            <% } else { %>
                <a <%=getDetailHrefOptions("L01", getParameter("orderKey"), "ShowReleaseNo=Y")%>><%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"),true)%></a>
            <% } %>
            <% if (equals("Y", getValue("Order", "xml:/Order/@HoldFlag"))) { %>
                <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%>/>
            <% } %>
            <% if (equals("Y", getValue("Order","xml:/Order/@isHistory") )){ %>
                <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order")%>/>
            <% } %>
        </td>
        <td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderDate"/></td>
    </tr>
</table>
