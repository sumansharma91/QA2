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

function CallBeforeInspect(){
	if (document.all("DcmIntegrationRealTimeFlag").value == "true")
	{
		if (!document.all("xml:/InspectReceipt/@LocationId").value && !document.all("xml:/Receipt/@LocationId").value){
			alert(YFCMSG106);//Location and Inventory Status are mandatory
			return false;
		} else if (!document.all("xml:/InspectReceipt/@LocationId").value && document.all("xml:/Receipt/@LocationId").value) {
			document.all("xml:/InspectReceipt/@LocationId").value=document.all("xml:/Receipt/@LocationId").value;
			document.all("xml:/Receipt/@LocationId").value="";
		}
		if (!document.all("xml:/Receipt/@InventoryStatusOnThePopup").value){
			alert(YFCMSG106);//Location and Inventory Status are mandatory
			return false;
		} else {
			var changedStatusValues="false";
			var NoOfReceiptLines=document.getElementsByName("ReceiptLineToBeInspectedRow");
			for (var i=0;i < NoOfReceiptLines.length;i++ )
			{
				var nameString="xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+i+"/@InventoryStatus";
				var LineInventoryStatus=document.all(nameString);
				changedStatusValues="true";
				LineInventoryStatus.value=document.all("xml:/Receipt/@InventoryStatusOnThePopup").value;
			}
		}
		return setOverrideFlag('xml:/InspectReceipt/@OverrideModificationRules');
	} else {
		return setOverrideFlag('xml:/InspectReceipt/@OverrideModificationRules');
	}	
}
</script>
<input type="hidden" value='<%=userHasOverridePermissions()%>' name="userHasOverridePermissions" />
<table class="view" width="100%">
	<yfc:makeXMLInput name="receiptKey">
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:ReceiptDetails:/Receipt/@ReceiptHeaderKey"> </yfc:makeXMLKey>
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:ReceiptDetails:/Receipt/@ReceiptNo"> </yfc:makeXMLKey>
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:ReceiptDetails:/Receipt/@ReceivingDock"> </yfc:makeXMLKey>
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:ReceiptDetails:/Receipt/@ReceivingNode"> </yfc:makeXMLKey>
			<yfc:makeXMLKey binding="xml:/Receipt/@DocumentType" value="xml:ReceiptDetails:/Receipt/Shipment/@DocumentType"> </yfc:makeXMLKey>
	</yfc:makeXMLInput>
	<yfc:makeXMLInput name="shipmentKey">
			<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:ReceiptDetails:/Receipt/Shipment/@ShipmentKey"> </yfc:makeXMLKey>
	</yfc:makeXMLInput>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
		<td class="protectedtext"><a <%=getDetailHrefOptions("L04", getParameter("receiptKey"),"")%> >
			<yfc:getXMLValue binding="xml:ReceiptDetails:/Receipt/@ReceiptNo"/></a>
		</td>
		<td class="detaillabel" ><yfc:i18n>Shipment_#</yfc:i18n></td>
		<td class="protectedtext">	<a <%=getDetailHrefOptions("L01", getParameter("shipmentKey"),"")%> >
			<yfc:getXMLValue binding="xml:ReceiptDetails:/Receipt/Shipment/@ShipmentNo"/></a>
		</td>
		<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td> 
		<td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptDetails:/Receipt/Shipment/@OrderNo"/></td>
		<td>
			<input type="hidden" name="xml:/InspectReceipt/@OverrideModificationRules" value="N"/>
	    </td>
	</tr>
		<tr>
		<td class="detaillabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipt/@ItemID"/>
		</td>
		<td class="detaillabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
		<td class="protectedtext">	
			<yfc:getXMLValue binding="xml:/Receipt/@ProductClass"/>
		</td>
		<td class="detaillabel" ><yfc:i18n>UOM</yfc:i18n></td> 
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@UnitOfMeasure"/></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Receipt_Status</yfc:i18n></td>
		<td class="protectedtext"><a <%=getDetailHrefOptions("L02", getParameter("receiptKey"),"")%> >
			<yfc:getXMLValueI18NDB binding="xml:ReceiptDetails:/Receipt/Status/@StatusName"/></a></td>
		<td class="detaillabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
			<yfc:makeXMLInput name="ReceivingNodeKey" >
				<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:ReceiptDetails:/Receipt/@ReceivingNode" />
			</yfc:makeXMLInput>
			<input type="hidden" name="xml:/InspectReceipt/@ReceivingNode" value='<%=resolveValue("xml:ReceiptDetails:/Receipt/@ReceivingNode")%>'/>
		<td class="protectedtext">	<a <%=getDetailHrefOptions("L03",getParameter("ReceivingNodeKey"),"")%> >
			<yfc:getXMLValue binding="xml:ReceiptDetails:/Receipt/@ReceivingNode"/>
		</a>
		</td>
<%	boolean DcmIntegrationRealTimeFlag=false;
		if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) 
		{	DcmIntegrationRealTimeFlag=true;	%>
				<td class="detaillabel" ><yfc:i18n>Receiving_Dock</yfc:i18n></td>
				<td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptDetails:/Receipt/@ReceivingDock"/></td>
			</tr>
			<tr>		
				<td class="detaillabel" ><yfc:i18n>Location</yfc:i18n></td>
				<input type="hidden" name="xml:/InspectReceipt/@LocationId" value='<%=resolveValue("xml:/Receipt/@LocationId")%>'/>	
		<%	if (!equals(resolveValue("xml:/Receipt/@LocationId"),"")) {	%>
						<td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@LocationId"/></td>
						<input type="hidden" name="xml:/Receipt/@LocationId" value='<%=resolveValue("xml:/Receipt/@LocationId")%>'/>	
		<%	}	else{	 %>
						<td nowrap="true">
							<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@LocationId","xml:/Receipt/@LocationId","xml:/InspectReceipt/@LocationId")%> />
							<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:ReceiptDetails:/Receipt/@ReceivingNode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
						</td>	
		<%	}	%>
				<td class="detaillabel" ><yfc:i18n>Inventory_Status</yfc:i18n></td>

		<%	if (!equals(resolveValue("xml:/Receipt/@InventoryStatus"),"")) {	%>
					<td class="protectedtext"><%=resolveValue("xml:/Receipt/@InventoryStatus")%></td>
					<input type="hidden" name="xml:/Receipt/@InventoryStatusOnThePopup" value='<%=resolveValue("xml:/Receipt/@InventoryStatus")%>'/>	
					<input type="hidden" name="xml:/Receipt/@InventoryStatus" value='<%=resolveValue("xml:/Receipt/@InventoryStatus")%>'/>	
		<%	}	else{	 %>
					<yfc:callAPI apiID="AP3"/>
					<td nowrap="true">
						<select name="xml:/Receipt/@InventoryStatusOnThePopup" class="combobox" >
							<yfc:loopOptions binding="xml:InventoryStatusList:/InventoryStatusList/@InventoryStatus" 
							name="InventoryStatus" value="InventoryStatus" selected="xml:/Receipt/@InventoryStatusOnThePopup"/>
						</select>
					</td>
		<%	} 
			if (!equals(resolveValue("xml:/Receipt/@CaseId"),"")) { %>
				<td class="detaillabel" ><yfc:i18n>Case_ID</yfc:i18n></td>
				<td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@CaseId"/></td>
		<%  } else if (!equals(resolveValue("xml:/Receipt/@PalletId"),"")) { %>
				<td class="detaillabel" ><yfc:i18n>Pallet_ID</yfc:i18n></td>
				<td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@PalletId"/></td>
		<%  } %>
				</tr>
				<tr>
					<td class="detaillabel" ><yfc:i18n>Location_Quantity</yfc:i18n></td>
					<td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@Quantity"/></td>
<%			if (!equals(resolveValue("xml:/Receipt/@SerialNo"),"")) { %>
					<td class="detaillabel" ><yfc:i18n>Serial_#</yfc:i18n></td>
					<td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@SerialNo"/></td>
<%			}%>
		<%	} %>
		<input type="hidden" name="DcmIntegrationRealTimeFlag" value='<%=DcmIntegrationRealTimeFlag%>' />
		<input type="hidden" <%=getTextOptions("xml:/InspectReceipt/@OverrideModificationRules","","")%> />
	</tr>
</table>

