<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<%
    String sBindingNode=request.getParameter("BindingNode");
    String sInputNode=request.getParameter("InputNode");
%>

<table width="100%" border=0 class="view">
<tr>
    <td class="detaillabel">
        <yfc:i18n>Lot_Manufacture_Date</yfc:i18n>
    </td>
    <td nowrap="true">
        <input type="text" class="dateinput" <%=getTextOptions(buildBinding(sBindingNode, "/@ManufacturingDate", "") , buildBinding(sInputNode, "/@ManufacturingDate", ""))%> />
        <img class="lookupicon" name="Calendar" border="0" onclick="invokeCalendar(this);return false;" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
    </td>
</tr>
<tr>    
    <td class="detaillabel">
        <yfc:i18n>Lot_Attribute_1</yfc:i18n>
    </td>
    <td>
        <input type="text" class="unprotectedinput" <%=getTextOptions(buildBinding(sBindingNode, "/@LotAttribute1", "") , buildBinding(sInputNode, "/@LotAttribute1", ""))%> />
    </td>
</tr>
<tr>    
    <td class="detaillabel">
        <yfc:i18n>Lot_Attribute_2</yfc:i18n>
    </td>
    <td>
        <input type="text" class="unprotectedinput" <%=getTextOptions(buildBinding(sBindingNode, "/@LotAttribute2", "") , buildBinding(sInputNode, "/@LotAttribute2", ""))%> />
    </td>
</tr>
<tr>    
    <td class="detaillabel">
        <yfc:i18n>Lot_Attribute_3</yfc:i18n>
    </td>
    <td>
        <input type="text" class="unprotectedinput" <%=getTextOptions(buildBinding(sBindingNode, "/@LotAttribute3", "") , buildBinding(sInputNode, "/@LotAttribute3", ""))%> />
    </td>
</tr>
</table>
