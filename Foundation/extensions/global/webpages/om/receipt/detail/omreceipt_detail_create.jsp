<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@include file="/console/jsp/modificationutils.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript">
yfcDoNotPromptForChanges(true); 
</script>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>
<table class="view" width="100%">
<input type="hidden" name="xml:/Receipt/@ShipmentKey" value='<%=resolveValue("xml:/Shipment/@ShipmentKey")%>'/>
<input type="hidden" name="xml:/Receipt/@DocumentType" value='<%=resolveValue("xml:/Shipment/@DocumentType")%>'/>
<input type="hidden" name="xml:/Receipt/Shipment/@SCAC" value='<%=resolveValue("xml:/Shipment/@SCAC")%>'/>
<input type="hidden" name="xml:/Receipt/Shipment/@BolNo" value='<%=resolveValue("xml:/Shipment/@BolNo")%>'/>
	<tr>
		<td class="detaillabel">
			<yfc:i18n>Receipt_#</yfc:i18n>
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptNo")%>/>
			<input type="hidden" name="xml:/Receipt/@Refresh" value="refresh" />
        </td>
    </tr>
<% if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) { %>
	<tr> 
        <td class="detaillabel">
            <yfc:i18n>Receiving_Dock</yfc:i18n>
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingDock")%>/>
			<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:/Shipment/@ReceivingNode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />

        </td>
    </tr>
<% }else{ %>
<input type="hidden" name="xml:/Receipt/@ReceivingDock" value=""/>
<%}%>
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
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
		     <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@ShipmentNo"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ShipmentNo","xml:/Shipment/@ShipmentNo")%> />
            </td>
        
    </tr>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
		<td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@EnterpriseCode"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@EnterpriseCode","xml:/Shipment/@EnterpriseCode")%> />
		</td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Expected_Delivery_Date</yfc:i18n>
        </td>
		    <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@ExpectedDeliveryDate"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ExpectedDeliveryDate","xml:/Shipment/@ExpectedDeliveryDate")%> />
            </td>
	</tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Receiving_Node</yfc:i18n>
        </td>
		    <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@ReceivingNode"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingNode","xml:/Shipment/@ReceivingNode")%> />
            </td>
        
    </tr> 
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
		    <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@BuyerOrganizationCode"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@BuyerOrganizationCode","xml:/Shipment/@BuyerOrganizationCode")%> />
            </td>
        
    </tr> 
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Seller</yfc:i18n>
        </td>
		    <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@SellerOrganizationCode"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@SellerOrganizationCode","xml:/Shipment/@SellerOrganizationCode")%> />
            </td>
    </tr> 
		<tr>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Pallets</yfc:i18n>
        </td>
		    <td>
               <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@NumOfPallets","xml:/Shipment/@NumOfPallets")%> />
            </td>
    </tr>
		<tr>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Cases</yfc:i18n>
        </td>
		    <td>
                <input type="test" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@NumOfCartons","xml:/Shipment/@NumOfCartons") %> />
            </td>
    </tr> 
	<tr>        
		<td/>
        <td align="right">
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick="setOKClickedAttributeForReceipt();"/>
			&nbsp&nbsp&nbsp&nbsp
			<input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close()"/>
        <td>
    <tr>
</table>
