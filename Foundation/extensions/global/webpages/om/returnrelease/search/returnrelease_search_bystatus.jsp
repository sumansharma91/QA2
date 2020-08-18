<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<%  
	YFCDate oEndDate = new YFCDate(); 
	oEndDate.setEndOfDay();
%>
<table class="view">
<tr> 
    <td>
        <input type="hidden" name="xml:/OrderRelease/@StatusQryType" value="BETWEEN"/>
		<input type="hidden" name="xml:/OrderRelease/Order/@OrderDateQryType" value="BETWEEN"/>		
    </td>
</tr>    

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="DocumentTypeBinding" value="xml:/OrderRelease/Order/@DocumentType"/>
        <jsp:param name="EnterpriseCodeBinding" value="xml:/OrderRelease/Order/@EnterpriseCode"/>
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code)
       // Statuses are refreshed. %>
    <yfc:callAPI apiID="AP1"/>

<tr> 
    <td class="searchlabel" ><yfc:i18n>Order_#</yfc:i18n></td>
</tr>    
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/Order/@OrderNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/Order/@OrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@OrderNo")%> />
    </td>
</tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/OrderRelease/Order/@FromOrderDate_YFCDATE","xml:/OrderRelease/Order/@FromOrderDate_YFCDATE","")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
            <input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderRelease/Order/@FromOrderDate_YFCTIME","xml:/OrderRelease/Order/@FromOrderDate_YFCTIME","")%>/>
			<yfc:i18n>To</yfc:i18n>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/OrderRelease/Order/@ToOrderDate_YFCDATE","xml:/OrderRelease/Order/@ToOrderDate_YFCDATE","")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderRelease/Order/@ToOrderDate_YFCTIME","xml:/OrderRelease/Order/@ToOrderDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>X_Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/OrderRelease/OrderLine/DerivedFrom/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/OrderRelease/OrderLine/DerivedFrom/@OrderNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/OrderLine/DerivedFrom/@OrderNo")%>/>
			<img class="lookupicon" onclick="callOrderNoLookup('xml:/OrderRelease/OrderLine/DerivedFrom/@OrderNo', null,'order', 'yfcListViewGroupId=YOML011')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order")%>/>
        </td>
    </tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Release_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/@ReleaseNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/NumericQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/@ReleaseNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ReleaseNo")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Buyer</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/Order/@BuyerOrganizationCodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/Order/@OrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@BuyerOrganizationCode")%> />
		<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
        <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/OrderRelease/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Seller</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/Order/@SellerOrganizationCodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/Order/@OrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@SellerOrganizationCode")%> />
        <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/OrderRelease/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
	<td class="searchcriteriacell" >
		<yfc:i18n>Pickup_Method</yfc:i18n>
	</td>
</tr> 
<tr> 
	<td>
        <select class="combobox" name="xml:/OrderRelease/@DeliveryMethod">
			<yfc:loopOptions binding="xml:DeliveryMethodList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderRelease/@DeliveryMethod" isLocalized="Y"/>
		</select>
	</td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Order_Release_Line_Status</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" >
        <select name="xml:/OrderRelease/@FromStatus" class="combobox">
            <yfc:loopOptions binding="xml:/StatusList/@Status" name="Description" value="Status" selected="xml:/OrderRelease/@FromStatus" isLocalized="Y"/>
        </select>
        <span class="searchlabel" ><yfc:i18n>To</yfc:i18n></span>
    </td>   
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderRelease/@ToStatus" class="combobox">
            <yfc:loopOptions binding="xml:/StatusList/@Status" name="Description" value="Status" selected="xml:/OrderRelease/@ToStatus" isLocalized="Y"/>
        </select>
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Ship_Node</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ShipNode","xml:/OrderRelease/@ShipNode")%> />
        <img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node") %> />
    </td>
</tr>
</table>
