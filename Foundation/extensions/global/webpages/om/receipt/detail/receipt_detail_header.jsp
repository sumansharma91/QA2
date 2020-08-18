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

<%  
	// seperate ip jsp for create view now, modify is always false here
    String modifyView = resolveValue("xml:/Receipt/@OpenReceiptFlag");
    boolean modify = false;
    
    if (equals(modifyView, "Y")) {
        modify = true;
    }
    
    //If this is a PO Receipt then the receipt should not be modifiable.
    String isPOReceipt = request.getParameter("isPOReceipt");
    if (equals(isPOReceipt,"true"))
    	modify = false;
%>

<table class="view" width="100%">
    <tr>
        <yfc:makeXMLInput name="orderKey">
            <yfc:makeXMLKey binding="xml:/OrderRelease/@OrderHeaderKey" value="xml:/Receipt/Shipment/@OrderHeaderKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="orderReleaseKey">
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/Receipt/Shipment/@OrderReleaseKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <td class="detaillabel">
            <yfc:i18n>Receipt_#</yfc:i18n>
        </td>
        <td>
            <yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"></yfc:getXMLValue></a>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="protectedtext">
			<% if(showOrderNo("Receipt","Order")) {%>
	            <a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
		            <yfc:getXMLValue binding="xml:/Receipt/Shipment//@OrderNo"></yfc:getXMLValue>
				</a>
			<%} else {%>
				<yfc:getXMLValue binding="xml:/Receipt/Shipment//@OrderNo"></yfc:getXMLValue>
			<%}%>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="protectedtext">
            <a <%=getDetailHrefOptions("L02",getParameter("orderReleaseKey"),"")%>>
            <yfc:getXMLValue binding="xml:/Receipt/Shipment//@ReleaseNo"></yfc:getXMLValue></a>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Receipt_Date</yfc:i18n>
        </td>
			<td>
		<%if (modify) {%>
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptDate")%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
		<%} else {%>
				<input type="text" class="protectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptDate")%>/>
		<%}%>
			</td>
        <td class="detaillabel">
            <yfc:i18n>SCAC</yfc:i18n>
        </td>
        <%if (modify) {%>
		<td>
            <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@Scac")%>>
				<yfc:loopOptions binding="xml:ScacList:/OrganizationList/@Organization" name="OrganizationCode"
                    value="OrganizationCode" selected="xml:/Receipt/Shipment/@Scac" />
            </select>
		</td>	
        <%} else { %>
		<td class="protectedtext" >
                <% String scac = resolveValue("xml:/Receipt/Shipment/@Scac");%>
                <yfc:loopXML binding="xml:ScacList:/OrganizationList/@Organization" id="Organization" >
                    <%if (equals(resolveValue("xml:/Organization/@OrganizationCode"), scac)) {%>
                    <yfc:getXMLValue binding="xml:/Organization/@OrganizationCode"></yfc:getXMLValue>
                    <%}%>
                </yfc:loopXML>
		</td>
		<%} %>
		<td class="detaillabel">
            <yfc:i18n>BOL_#</yfc:i18n>
        </td>
        <td>
            <input type="text" class="<%=(modify?"unprotectedinput":"protectedinput")%>" <%=getTextOptions("xml:/Receipt/Shipment/@BolNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>PRO_#</yfc:i18n>
        </td>
        <td>
            <input type="text" class="<%=(modify?"unprotectedinput":"protectedinput")%>" <%=getTextOptions("xml:/Receipt/Shipment/@ProNo")%>/>
        </td>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Expected_Pallets</yfc:i18n>
        </td>
        <td>
            <input type="text" class="<%=(modify?"numericunprotectedinput":"numericprotectedinput")%>" <%=getTextOptions("xml:/Receipt/@NumOfPallets")%>/>
        </td>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Expected_Cartons</yfc:i18n>
        </td>
        <td>
            <input type="text" class="<%=(modify?"numericunprotectedinput":"numericprotectedinput")%>" <%=getTextOptions("xml:/Receipt/@NumOfCartons")%>/>
        </td>
    </tr>
	<% if (!modify) {%>
	<tr>
        <td class="detaillabel"><yfc:i18n>Open</yfc:i18n></td>
        <td class="protectedtext" ><yfc:getXMLValue binding="xml:/Receipt/@OpenReceiptFlag" /> </td>
	</tr>
	<%}%>
</table>
