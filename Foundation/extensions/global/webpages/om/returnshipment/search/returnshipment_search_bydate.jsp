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
  <jsp:param name="EnterpriseCodeBinding" value="xml:/Shipment/@EnterpriseCode"/>
 </jsp:include>
    <tr> 
        <td>
            <input type="hidden" name="xml:/Shipment/@ExpectedShipmentDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Shipment/@ExpectedDeliveryDateQryType" value="BETWEEN"/>
			<input type="hidden" name="xml:/Shipment/@ActualShipmentDateQryType" value="BETWEEN"/>
			<input type="hidden" name="xml:/Shipment/@ActualDeliveryDateQryType" value="BETWEEN"/>
        </td>
    </tr>    
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
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/ShipmentLines/ShipmentLine/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/@OrderNoQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/@OrderNo")%>/>
        </td>
    </tr>
    
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Expected_Ship_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@FromExpectedShipmentDate_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@FromExpectedShipmentDate_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
            <yfc:i18n>To</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@ToExpectedShipmentDate_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@ToExpectedShipmentDate_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Expected_Delivery_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@FromExpectedDeliveryDate_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@FromExpectedDeliveryDate_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
            <yfc:i18n>To</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@ToExpectedDeliveryDate_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@ToExpectedDeliveryDate_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Actual_Ship_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@FromActualShipmentDate_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@FromActualShipmentDate_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
            <yfc:i18n>To</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@ToActualShipmentDate_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@ToActualShipmentDate_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Actual_Delivery_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@FromActualDeliveryDate_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@FromActualDeliveryDate_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
            <yfc:i18n>To</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@ToActualDeliveryDate_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/@ToActualDeliveryDate_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
        </td>
    </tr>
	<%if(isShipNodeUser()){%>
		<input type="hidden" <%=getTextOptions("xml:/Shipment/@ReceivingNode", "xml:CurrentUser:/User/@Node")%>/>
	<%}%>
</table>
