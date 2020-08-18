<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
    yfcDoNotPromptForChanges(true);
</script>


<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" <%=getTextOptions("xml:/Receipt/Shipment/@OrderReleaseKey", "xml:/OrderReleaseDetail/@OrderReleaseKey")%>/>
            <input type="hidden" <%=getTextOptions("xml:/Receipt/Shipment/@OrderHeaderKey", "xml:/OrderRelease/@OrderHeaderKey")%>/>
        </td>
    </tr>
    <tr>
        <yfc:makeXMLInput name="orderKey">
            <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderRelease/@OrderHeaderKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="orderReleaseKey">
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderReleaseDetail/@OrderReleaseKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <td class="detaillabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="protectedtext">
			<% if(showOrderNo("OrderRelease","Order")) {%>
				<a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
					<yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderNo"></yfc:getXMLValue>
				</a>
			<%} else {%>
				<yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderNo"></yfc:getXMLValue>
			<%}%>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="protectedtext">
            <a <%=getDetailHrefOptions("L02",getParameter("orderReleaseKey"),"")%>>
            <yfc:getXMLValue binding="xml:/OrderRelease/@ReleaseNo"></yfc:getXMLValue></a>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Receipt_Date</yfc:i18n>
        </td>
			<td>
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptDate", getTodayDate())%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
			</td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>SCAC</yfc:i18n>
        </td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@Scac")%>>
				<yfc:loopOptions binding="xml:ScacList:/OrganizationList/@Organization" name="OrganizationCode"
                    value="OrganizationCode" selected="xml:/Receipt/Shipment/@Scac" />
            </select>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>BOL_#</yfc:i18n>
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@BolNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>PRO_#</yfc:i18n>
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ProNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Expected_Pallets</yfc:i18n>
        </td>
        <td>
            <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Receipt/@NumOfPallets")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Expected_Cartons</yfc:i18n>
        </td>
        <td>
            <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Receipt/@NumOfCartons")%>/>
        </td>
    </tr>
</table>
