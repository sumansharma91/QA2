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
        <input type="hidden" name="xml:/OrderRelease/@ReqShipDateQryType" value="BETWEEN"/>
        <input type="hidden" name="xml:/OrderRelease/@ReqDeliveryDateQryType" value="BETWEEN"/>
        <input type="hidden" name="xml:/OrderRelease/Order/@OrderDateQryType" value="BETWEEN"/>
    </td>
</tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="DocumentTypeBinding" value="xml:/OrderRelease/Order/@DocumentType"/>
        <jsp:param name="EnterpriseCodeBinding" value="xml:/OrderRelease/Order/@EnterpriseCode"/>
    </jsp:include>

<tr> 
    <td class="searchlabel" ><yfc:i18n>Order_#</yfc:i18n></td>
</tr>    
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/@SalesOrderNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/@SalesOrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@SalesOrderNo")%> />
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
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/Order/@BuyerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@BuyerOrganizationCode")%> />
		<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
        <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/OrderRelease/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Seller</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderLine/Order/@SellerOrganizationCodeQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
            value="QueryType" selected="xml:/OrderRelease/Order/@SellerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@SellerOrganizationCode")%>/>
        <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/OrderRelease/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Ship_Node</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/@ShipNodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/@ShipNodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ShipNode")%> />
        <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node") %> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/@ReceivingNodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/@ReceivingNodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ReceivingNode")%> />
        <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node") %> />
    </td>
</tr>

<tr> 
	<td class="searchcriteriacell" >
		<yfc:i18n>Delivery_Method</yfc:i18n>
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
        <yfc:i18n>Order_Date</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@FromOrderDate_YFCDATE","xml:/OrderRelease/Order/@FromOrderDate_YFCDATE","")%> >
        <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderRelease/Order/@FromOrderDate_YFCTIME","xml:/OrderRelease/Order/@FromOrderDate_YFCTIME","")%>/>
		<yfc:i18n>To</yfc:i18n>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@ToOrderDate_YFCDATE","xml:/OrderRelease/Order/@ToOrderDate_YFCDATE","")%> >
        <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderRelease/Order/@ToOrderDate_YFCTIME","xml:/OrderRelease/Order/@ToOrderDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Requested_Ship_Date</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@FromReqShipDate_YFCDATE","xml:/OrderRelease/@FromReqShipDate_YFCDATE","")%> >
        <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
        <input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderRelease/@FromReqShipDate_YFCTIME","xml:/OrderRelease/@FromReqShipDate_YFCTIME","")%>/>
		<yfc:i18n>To</yfc:i18n>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ToReqShipDate_YFCDATE","xml:/OrderRelease/@ToReqShipDate_YFCDATE","")%> >
        <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderRelease/@ToReqShipDate_YFCTIME","xml:/OrderRelease/@ToReqShipDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@FromReqDeliveryDate_YFCDATE","xml:/OrderRelease/@FromReqDeliveryDate_YFCDATE","")%> >
        <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
        <input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderRelease/@FromReqDeliveryDate_YFCTIME","xml:/OrderRelease/@FromReqDeliveryDate_YFCTIME","")%>/>
		<yfc:i18n>To</yfc:i18n>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ToReqDeliveryDate_YFCDATE","xml:/OrderRelease/@ToReqDeliveryDate_YFCDATE","")%> >
        <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderRelease/@ToReqDeliveryDate_YFCTIME","xml:/OrderRelease/@ToReqDeliveryDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
    </td>
</tr>
</table>
