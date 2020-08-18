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
<script>
	function disableAndClearNoOfLPNs(cbLPNType)
	{
		var cell = cbLPNType.parentElement;
        var cellToModify = cell.parentElement.cells(cell.cellIndex + 1);
        if (cellToModify != null) {
			var children = cellToModify.getElementsByTagName("INPUT");
			if (!children)
				return;
			if ("" == cbLPNType.value ) {
				children(0).errorDescription="";
				children(0).runtimeStyle.cssText="";
				children(0).value="";
				children(0).disabled=true;
			} else {
				children(0).disabled=false;
			}
		}
    }
</script>
<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
<td>
<table class="view" width="100%">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Shipment_Line_#</yfc:i18n>
    </td>
    <td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ShipmentLineNo">
		</yfc:getXMLValue>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>PO_#</yfc:i18n>
    </td>
    <td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@OrderNo"/>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>Release_#</yfc:i18n>
    </td>
    <td class="protectedtext">
	<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@OrderReleaseNo"/>
	</td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Item_ID</yfc:i18n>
    </td>
    <td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ItemID"/>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Unit_Of_Measure</yfc:i18n>
    </td>
    <td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@UOM"/>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>Product_Class</yfc:i18n>
    </td>
    <td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ProductClass"/>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Quality_Status</yfc:i18n>
    </td>
    <td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@SuggestedQualityStatus"/>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Total_Quantity</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@TotalQuantity"/>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>Received_Quantity</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ReceivedQuantity"/>
	</td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Available_To_Receive</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@AvailableToReceiveQuantity"/>
	</td>
</tr>
</table>
</td>
</tr>
<tr>
    <td>
        <table class="table" ID="ReceiveLines" width="100%" editable="true" yfcMandatoryMessage="<yfc:i18n>Receipt_information_must_be_entered</yfc:i18n>">
            <thead>
                <tr>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@LotNumber")%>"><yfc:i18n>Lot_#</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@RevisionNo")%>"><yfc:i18n>Revision_#</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@SerialNo")%>"><yfc:i18n>Serial_#</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ShipByDate")%>"><yfc:i18n>Ship_By_Date</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@PalletUOM")%>"><yfc:i18n>Pallet_Type</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@NoOfPallets")%>"><yfc:i18n>No_Of_Pallets</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@CaseUOM")%>"><yfc:i18n>Case_Type</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@NoOfCases")%>"><yfc:i18n>No_Of_Cases</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@NoOfUnits")%>"><yfc:i18n>No_Of_Units</yfc:i18n></td>
                </tr>
            </thead>
            <tbody>
            <yfc:loopXML binding="xml:/GetLinesToReceive/ReceivableLineList/@ReceivableLine" id="ReceivableLine">
            <tr>
                <td class="tablecolumn" ShouldCopy="true" >
					<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@ShipmentLineNo","xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ShipmentLineNo")%>/>
			        <input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@OrderNo","xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@OrderNo")%>/>
			        <input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@OrderReleaseNo","xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@OrderReleaseNo")%>/>
			        <input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@ItemID","xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ItemID")%>/>
			        <input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@UnitOfMeasure","xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@UOM")%>/>
			        <input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@ProductClass","xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ProductClass")%>/>
			        <input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@QualityStatus","xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@SuggestedQualityStatus")%>/>

					<input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@LotNumber", "")%>/>
                </td>
                <td class="tablecolumn" ShouldCopy="true" >
					<input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@RevisionNo", "")%>/>
                </td>
                <td class="tablecolumn" ShouldCopy="true" >
					<input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@SerialNo", "")%>/>
                    <img IconName="addSplitLine" class="lookupicon" src="../console/icons/add.gif" onclick="yfcSplitLine(this)" /> 
				</td>
                <td class="tablecolumn" nowrap="true" ShouldCopy="true" >
                    <input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@ShipByDate", "")%>/>
                    <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
                </td>
                <td class="numerictablecolumn" ShouldCopy="true" align="left" >
					<select name=<%=buildBinding("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@PalletUOM","","")%> class="combobox" onchange="disableAndClearNoOfLPNs(this);">
						<option value ="">&nbsp;</option>				
			            <yfc:loopXML binding="xml:/Item/ItemUOMList/@ItemUOM" id="ItemUOM">
							<% if (equals("Pallet",resolveValue("xml:ItemUOM:/ItemUOM/@UOM"))) {%>
							<option value ="<%=resolveValue("xml:ItemUOM:/ItemUOM/@Quantity")%>"><%=resolveValue("xml:ItemUOM:/ItemUOM/@Quantity")%></option>
							<% } %>
						</yfc:loopXML>
					</select>
                </td>
				<td class="numerictablecolumn" ShouldCopy="true" align="left" >
					<input type="text" align="left" class="numericunprotectedinput" NewName="true" NewClassName="numericunprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@NoOfPallets", "")%> disabled="true"/>
                </td>
                <td class="numerictablecolumn" ShouldCopy="true" align="left" >
					<select name=<%=buildBinding("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@CaseUOM","","")%> class="combobox" onchange="disableAndClearNoOfLPNs(this);">
						<option value ="">&nbsp;</option>				
			            <yfc:loopXML binding="xml:/Item/ItemUOMList/@ItemUOM" id="ItemUOM">
							<% if (equals("Case",resolveValue("xml:ItemUOM:/ItemUOM/@UOM"))) {%>
							<option value ="<%=resolveValue("xml:ItemUOM:/ItemUOM/@Quantity")%>"><%=resolveValue("xml:ItemUOM:/ItemUOM/@Quantity")%></option>
							<% } %>
						</yfc:loopXML>
					</select>
                </td>
                <td class="numerictablecolumn" ShouldCopy="true" align="left" >
					<input type="text" align="left" class="numericunprotectedinput" NewName="true" NewClassName="numericunprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@NoOfCases", "")%> disabled="true"/>
                </td>
                <td class="numerictablecolumn" ShouldCopy="true" align="left" >
                    <input type="text" align="left" class="numericunprotectedinput" NewName="true" NewClassName="numericunprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + ReceivableLineCounter + "/@NoOfUnits", "")%>/>
                </td>
                </td>
            </tr>
            </yfc:loopXML>
            </tbody>
        </table>
    </td>
</tr>
</table>
