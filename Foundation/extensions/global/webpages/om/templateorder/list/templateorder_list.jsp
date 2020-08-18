<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
                <input type="hidden" name="xml:/Order/@Override" value="N"/>
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader">&nbsp;</td>
            <td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Buyer</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Seller</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Order_Name</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/OrderList/@Order" id="Order">
            <tr>
                <yfc:makeXMLInput name="orderKey">
                    <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
                </yfc:makeXMLInput>                
                <td class="checkboxcolumn">                     
                    <input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey"/>
                </td>
                <td valign="top">
                    <% if (equals(resolveValue("xml:/Order/@DefaultTemplate"), "Y")) {%>
                        <img <%=getImageOptions(YFSUIBackendConsts.DEFAULT_TEMPLATE, "Default_Template")%>/>
                    <% } %>
                </td>
                <td class="tablecolumn">
					<a href="javascript:showDetailFor('<%=getParameter("orderKey")%>');">
						<yfc:getXMLValue binding="xml:/Order/@OrderNo"/>
					</a>                    
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/@EnterpriseCode"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/@OrderName"/></td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>
