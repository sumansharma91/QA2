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
<script language="javascript">
yfcDoNotPromptForChanges(true); 
</script>
<yfc:callAPI apiID="AP1"/>
<% if (equals("0003",resolveValue("xml:/OrderRelease/Order/@DocumentType"))) { %>
<yfc:callAPI apiID="AP5"/>
<% } else { %>
<yfc:callAPI apiID="AP2"/>
<% } %>
<yfc:callAPI apiID="AP4"/>
<table class="view" width="100%">
	<input type="hidden" name="xml:/Receipt/Shipment/@OrderHeaderKey" value='<%=resolveValue("xml:/OrderRelease/@OrderHeaderKey")%>'/>
	<input type="hidden" name="xml:/Receipt/Shipment/@OrderReleaseKey" value='<%=resolveValue("xml:/OrderRelease/@OrderReleaseKey")%>'/>
	<input type="hidden" name="xml:/Receipt/@DocumentType" value='<%=resolveValue("xml:/OrderRelease/Order/@DocumentType")%>'/>
	<input type="hidden" name="xml:/Receipt/Shipment/@SCAC" value='<%=resolveValue("xml:/OrderRelease/@SCAC")%>'/>
	<input type="hidden" name="xml:/Receipt/Shipment/@CarrierServiceCode" value='<%=resolveValue("xml:/OrderRelease/@CarrierServiceCode")%>'/>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Receipt_#</yfc:i18n>
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptNo")%>/>
        </td>
    </tr>
	<tr> 
    <td class="detaillabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
    <td class="protectedtext">
<%	if (equals("0003",resolveValue("xml:/OrderRelease/Order/@DocumentType"))){ %>
		<yfc:getXMLValue binding="xml:/OrderRelease/@ShipNode"/>
		<input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingNode","xml:/OrderRelease/@ShipNode")%> />
		<input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ShipNode","xml:/OrderRelease/@ReceivingNode")%> />
<%}else{%>
		<yfc:getXMLValue binding="xml:/OrderRelease/@ReceivingNode"/>
		<input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingNode","xml:/OrderRelease/@ReceivingNode")%> />
		<input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ShipNode","xml:/OrderRelease/@ShipNode")%> />
<%}%>
    </td>
	</tr>
<% if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) { %>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Receiving_Dock</yfc:i18n>
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingDock")%>/>
<%		if (equals("0005",resolveValue("xml:/OrderRelease/Order/@DocumentType"))) { %>
			<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:/OrderRelease/@ReceivingNode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
<%		} else if (equals("0003",resolveValue("xml:/OrderRelease/Order/@DocumentType"))){ %>
			<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:/OrderRelease/@ShipNode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
<%		}	%>
        </td>
    </tr>
<% } %>
	<tr>
	<td class="detaillabel" >
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
        <td class="protectedtext">
				<yfc:getXMLValue binding="xml:/OrderRelease/@EnterpriseCode"></yfc:getXMLValue>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@EnterpriseCode","xml:/OrderRelease/@EnterpriseCode")%> />
		</td>
    </tr>
	<tr>
	<td class="detaillabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="protectedtext">
				<yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderNo"></yfc:getXMLValue>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@OrderNo","xml:/OrderRelease/Order/@OrderNo")%> />
		</td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/OrderRelease/@ReleaseNo"></yfc:getXMLValue></a>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ReleaseNo","xml:/OrderRelease/@ReleaseNo")%> />
        </td>
    </tr>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Pro_#</yfc:i18n>
        </td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ProNo","xml:/Receipt/Shipment/@ProNo")%> />
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
        <td class="detaillabel"  nowrap="true">
			<yfc:i18n>Buyer</yfc:i18n> 
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/OrderRelease/Order/@BuyerOrganizationCode"/>
			<input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@BuyerOrganizationCode","xml:/OrderRelease/Order/@BuyerOrganizationCode")%> />
      	</td>
	<tr>
	<tr>
        <td class="detaillabel"  nowrap="true">
			<yfc:i18n>Seller</yfc:i18n> 
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/OrderRelease/Order/@SellerOrganizationCode"/>
			<input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@SellerOrganizationCode","xml:/OrderRelease/Order/@SellerOrganizationCode")%> />
		</td>
    </tr>
	 </tr> 
		<tr>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Pallets</yfc:i18n>
        </td>
		    <td>
               <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Receipt/@NumOfPallets","xml:/Shipment/@NumOfPallets")%> />
            </td>

    </tr> 
		<tr>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Cases</yfc:i18n>
        </td>
		    <td>
                <input type="test" class="numericunprotectedinput" <%=getTextOptions("xml:/Receipt/@NumOfCartons","xml:/Shipment/@NumOfCartons")%> />
            </td>

    </tr> 

    </table>
