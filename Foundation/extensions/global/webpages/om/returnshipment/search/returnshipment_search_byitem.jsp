<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
  <jsp:param name="ShowDocumentType" value="true"/>
  <jsp:param name="DocumentTypeBinding" value="xml:/Shipment/@DocumentType"/>
  <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
  <jsp:param name="EnterpriseCodeBinding" value="xml:/Shipment/@EnterpriseCode"/>
 </jsp:include>
   <tr>
        <td class="searchlabel" >
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/@ShipmentNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Shipment/@ShipmentNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ShipmentNo")%>/>
        </td>
    </tr>
    
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/ShipmentLines/ShipmentLine/@ItemIDQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                    value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/@ItemIDQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemID")%>/>
 		    <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
            <img class="lookupicon" onclick="callItemLookup('xml:/Shipment/ShipmentLines/ShipmentLine/@ItemID','xml:/Shipment/ShipmentLines/ShipmentLine/@ProductClass','xml:/Shipment/ShipmentLines/ShipmentLine/@UnitOfMeasure','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
        </td>
    </tr>
	<input type="hidden" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/@ProductClass")%>/>
	<input type="hidden" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/@UnitOfMeasure")%>/>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@BuyerOrganizationCode")%>/>
            <img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=BUYER')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Seller</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@SellerOrganizationCode")%>/>
            <img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=SELLER')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
	<%if(isShipNodeUser()){%>
		<input type="hidden" <%=getTextOptions("xml:/Shipment/@ReceivingNode", "xml:CurrentUser:/User/@Node")%>/>
	<%}%>
</table>
