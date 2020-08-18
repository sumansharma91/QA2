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
            <input type="hidden" name="xml:/OrderLine/Order/@OrderDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/OrderLine/@ReqDeliveryDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/OrderLine/@ReqShipDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/OrderLine/Order/@DraftOrderFlag" value="N"/>
			<input type="hidden" name='xml:/OrderLine/@ItemGroupCode' value='PROD'  />
        </td>
    </tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="DocumentTypeBinding" value="xml:/OrderLine/Order/@DocumentType"/>
        <jsp:param name="EnterpriseCodeBinding" value="xml:/OrderLine/Order/@EnterpriseCode"/>
    </jsp:include>

    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/OrderLine/Order/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/OrderLine/Order/@OrderNoQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@OrderNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@BuyerOrganizationCode")%>/>
			<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
            <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
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
                value="QueryType" selected="xml:/OrderLine/Order/@SellerOrganizationCodeQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@SellerOrganizationCode")%>/>
            <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer_Account_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNo")%>/>
        </td>
    </tr>
    <tr>
        <td  class="searchlabel" >
            <yfc:i18n>Order_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" >
            <input class="dateinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@FromOrderDate_YFCDATE","xml:/OrderLine/Order/@FromOrderDate_YFCDATE","")%>/>
            <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
            <input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderLine/Order/@FromOrderDate_YFCTIME","xml:/OrderLine/Order/@FromOrderDate_YFCTIME","")%>/>
			<yfc:i18n>To</yfc:i18n>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@ToOrderDate_YFCDATE","xml:/OrderLine/Order/@ToOrderDate_YFCDATE","")%>/>
            <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderLine/Order/@ToOrderDate_YFCTIME","xml:/OrderLine/Order/@ToOrderDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Requested_Ship_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/@FromReqShipDate_YFCDATE","xml:/OrderLine/@FromReqShipDate_YFCDATE","")%>/>
            <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
            <input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderLine/@FromReqShipDate_YFCTIME","xml:/OrderLine/@FromReqShipDate_YFCTIME","")%>/>
			<yfc:i18n>To</yfc:i18n>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/@ToReqShipDate_YFCDATE","xml:/OrderLine/@ToReqShipDate_YFCDATE","")%>/>
            <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderLine/@ToReqShipDate_YFCTIME","xml:/OrderLine/@ToReqShipDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/OrderLine/@FromReqDeliveryDate_YFCDATE","xml:/OrderLine/@FromReqDeliveryDate_YFCDATE","")%>/>
            <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
            <input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderLine/@FromReqDeliveryDate_YFCTIME","xml:/OrderLine/@FromReqDeliveryDate_YFCTIME","")%>/>
			<yfc:i18n>To</yfc:i18n>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/OrderLine/@ToReqDeliveryDate_YFCDATE","xml:/OrderLine/@ToReqDeliveryDate_YFCDATE","")%>/>
            <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/OrderLine/@ToReqDeliveryDate_YFCTIME","xml:/OrderLine/@ToReqDeliveryDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
        </td>
    </tr>
</table>
