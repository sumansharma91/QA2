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
            <input type="hidden" name="xml:/Order/@OrderDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@ReqDeliveryDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@ReqShipDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N"/>
        </td>
    </tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true"/>

    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/Order/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/Order/@BuyerOrganizationCodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@BuyerOrganizationCodeQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode")%>/>
			<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
            <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
       <tr>
        <td class="searchlabel" >
            <yfc:i18n>Seller</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@SellerOrganizationCodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@SellerOrganizationCodeQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode")%>/>
            <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
<tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer_Account_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/PaymentMethod/@CustomerAccountNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/PaymentMethod/@CustomerAccountNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/PaymentMethod/@CustomerAccountNo")%>/>
        </td>
    </tr>
    <tr> 
        <td class="searchlabel" ><yfc:i18n>Currency</yfc:i18n></td>
    </tr>
    <tr> 
        <td nowrap="true" class="searchcriteriacell">
            <select class="combobox" name="xml:/Order/PriceInfo/@Currency">
                <yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription" value="Currency" selected="xml:/Order/PriceInfo/@Currency" isLocalized="Y"/>
            </select>
        </td>
    </tr>

    <tr> 
        <td class="searchlabel" ><yfc:i18n>Total_Amount</yfc:i18n></td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select class="combobox" name="xml:/Order/PriceInfo/@TotalAmountQryType">
                <yfc:loopOptions binding="xml:/QueryTypeList/NumericQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Order/PriceInfo/@TotalAmountQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/PriceInfo/@TotalAmount")%> />
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@FromOrderDate_YFCDATE","xml:/Order/@FromOrderDate_YFCDATE","")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
            <input class="dateinput" type="hidden" <%=getTextOptions("xml:/Order/@FromOrderDate_YFCTIME","xml:/Order/@FromOrderDate_YFCTIME","")%>/>
			<yfc:i18n>To</yfc:i18n>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@ToOrderDate_YFCDATE","xml:/Order/@ToOrderDate_YFCDATE","")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/Order/@ToOrderDate_YFCTIME","xml:/Order/@ToOrderDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
        </td>
    </tr>
    <tr>
	    <td class="searchlabel" >
	        <yfc:i18n>Requested_Ship_Date</yfc:i18n>
	    </td>
	</tr>
	<tr>
	    <td nowrap="true" class="searchcriteriacell">
	        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@FromReqShipDate_YFCDATE","xml:/Order/@FromReqShipDate_YFCDATE","")%> >
	        <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/Order/@FromReqShipDate_YFCTIME","xml:/Order/@FromReqShipDate_YFCTIME","")%>/>
	        <yfc:i18n>To</yfc:i18n>
	        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ToReqShipDate_YFCDATE","xml:/Order/@ToReqShipDate_YFCDATE","")%> >
	        <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/Order/@ToReqShipDate_YFCTIME","xml:/Order/@ToReqShipDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>   
	    </td>
	</tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" >
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@FromReqDeliveryDate_YFCDATE","xml:/Order/@FromReqDeliveryDate_YFCDATE","")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
            <input class="dateinput" type="hidden" <%=getTextOptions("xml:/Order/@FromReqDeliveryDate_YFCTIME","xml:/Order/@FromReqDeliveryDate_YFCTIME","")%>/>
			<yfc:i18n>To</yfc:i18n>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@ToReqDeliveryDate_YFCDATE","xml:/Order/@ToReqDeliveryDate_YFCDATE","")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/Order/@ToReqDeliveryDate_YFCTIME","xml:/Order/@ToReqDeliveryDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>   
        </td>
    </tr>
</table>
