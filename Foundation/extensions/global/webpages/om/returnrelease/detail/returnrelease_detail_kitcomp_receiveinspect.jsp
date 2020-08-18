<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
    //window.attachEvent("onload", IgnoreChangeNames);
    document.body.attachEvent("onunload", processSaveRecordsForReturnReceipt);
</script>
<script language="javascript">
    yfcDoNotPromptForChanges(true);
</script>
<%
double quantity = 0;
//This screen doubles for receiving and inspection.  Based on a parameter, display can change.
String isInsp = getParameter("IsInspection");
boolean isInspection = false;
String root = "Receipt/ReceiptLines/ReceiptLine";
if (equals(isInsp, "Y")) {
    isInspection = true;
    root = "InspectOrder/ReceiptLines/FromReceiptLine/ToReceiptLines/ToReceiptLine";
}
%>

<yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
	<%quantity += getNumericValue("xml:/OrderStatus/@StatusQty");%>
</yfc:loopXML>

<input type="hidden" NewName="true" NewResetValue="false" <%=getTextOptions(root+ "/@InspectedBy", "xml:CurrentUser:/User/@Loginid")%> />
<input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceiptHeaderKey", "xml:/Receipts/Receipt/@ReceiptHeaderKey")%> />
<input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceivingNode", "xml:/Receipts/Receipt/@ReceivingNode")%> />
<table class="table" width="100%" editable="true" ID="KitComponents"  yfcMandatoryMessage="<yfc:i18n>Component_details_must_be_entered</yfc:i18n>" >
<thead>
    <tr>
    	<td class="tablecolumnheader" style="width:30px" sortable="no"><yfc:i18n>Kit_Details</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/OrderLine/InventoryItem/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" ><yfc:i18n>UOM</yfc:i18n></td>
        <td class="tablecolumnheader" ><yfc:i18n>Description</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/KitLineTranQuantity/@KitQty")%>"><yfc:i18n>Kit_Qty</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@Quantity")%>">
            <%if (!isInspection) {%>
            <yfc:i18n>Open_For_Receipt</yfc:i18n>
            <%} else {%>
            <yfc:i18n>Open_For_Inspection</yfc:i18n>
            <%}%>
        </td>
</tr>
</tr>
</thead>
<tbody>
   	<%String className="oddrow";%>
	<yfc:loopXML binding="xml:/OrderLine/KitLines/@KitLine" id="KitLine">
        	<%
			int openforreceiptvalueis = (int)(quantity * getNumericValue("xml:/KitLine/KitLineTranQuantity/@KitQty"));
			if (openforreceiptvalueis != 0 ) {
				if (className.equals("oddrow"))
						className="evenrow";
					else
						className="oddrow";													
			
			YFCElement container = (YFCElement) request.getAttribute("OrderLine");
			container.setAttribute("ItemID",resolveValue("xml:/KitLine/@ItemID"));
			container.setAttribute("ProductClass",resolveValue("xml:/KitLine/@ProductClass"));
			container.setAttribute("UnitOfMeasure",resolveValue("xml:/KitLine/@UnitOfMeasure"));
			%>
			<yfc:callAPI apiID='AP1'/>
			<tr class='<%=className%>'>
			<td>
				<img onclick="expandCollapseDetails('optionSet_<%=KitLineCounter%>','<%=replaceI18N("Click_To_Add_Kit_Info")%>','<%=replaceI18N("Click_To_Hide_Kit_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_Add_Kit_Info")%> />

			</td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/KitLine/@ItemID"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/KitLine/@UnitOfMeasure"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/KitLine/@ItemDesc"/></td>
            <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@KitQty"/></td>
                             
        	<td class="numerictablecolumn">
                <% 
					String displayValue = getLocalizedStringFromInt(getLocale(), openforreceiptvalueis);
				%>
                <%=displayValue%>
            </td>
           

            		<tr id='<%="optionSet_"+KitLineCounter%>' class='<%=className%>' style="display:none">

		
					<td colspan="7" >
						<jsp:include page="/om/returnrelease/detail/returnrelease_detail_includetag.jsp" flush="true">
							<jsp:param name="optionSetBelongingToLine" value='<%=String.valueOf(KitLineCounter)%>'/>
							<jsp:param name="Modifiable" value='true'/>
							<jsp:param name="LabelTDClass" value='detaillabel'/>
			                <jsp:param name="TagContainer" value='OrderLine'/>
					        <jsp:param name="TagElement" value='KitLine'/>
							<jsp:param name="RepeatingElement" value='KitLine'/>
							<jsp:param name="TotalBinding" value='<%=root%>'/>
							<jsp:param name="ItemID" value='<%=resolveValue("xml:/KitLine/@ItemID")%>'/>
							<jsp:param name="PC" value='<%=resolveValue("xml:/KitLine/@ProductClass")%>'/>
							<jsp:param name="UOM" value='<%=resolveValue("xml:/KitLine/@UnitOfMeasure")%>'/>
							<jsp:param name="OrderLineKey" value='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>'/>
						</jsp:include>
					</td>
				</tr>
			</tr>
			<% } %>
    </yfc:loopXML>
</tbody>
</table>
