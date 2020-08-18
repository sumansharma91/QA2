<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript">
		yfcDoNotPromptForChanges(true);
</script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript">

function processCachedGroupDetails(){
        yfcSpecialChangeNames("ReceiptLine", false);
}
    document.body.attachEvent("onunload",processCachedGroupDetails); 
</script>
<table class="table" editable="false" width="100%" cellspacing="0" id="ReceiptLine">
<thead> 
    <tr> 
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ItemId")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ProductClass")%>">
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@UnitOfMeasure")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@PalletId")%>">
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@CaseId")%>">
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@SerialNo")%>">
            <yfc:i18n>Serial_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Inventory_Status</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@Quantity")%>">
            <yfc:i18n>UnReceive_Quantity</yfc:i18n>
        </td>
	</tr>
</thead> 
<tbody>
    <yfc:loopXML binding="xml:ReceiptLineList:/ReceiptLineList/@ReceiptLine" id="ReceiptLine"> 

	<%double sQty=getNumericValue("xml:/ReceiptLine/@Quantity");
	if(sQty>0){
		%>
	<tr> 
		
		<td class="tablecolumn">
			<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ItemID"/>
				<input type="hidden"
				 <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+ ReceiptLineCounter+"/@ReceiptLineNo", "xml:/ReceiptLine/@ReceiptLineNo")%>/>
				<input type="hidden"
				 <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+ ReceiptLineCounter+"/@ReceiptLineKey", "xml:/ReceiptLine/@ReceiptLineKey")%>/>
				<input type="hidden"
				 <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+ ReceiptLineCounter+"/@Quantity", "xml:/ReceiptLine/@Quantity")%>/>
	    </td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ProductClass"/>
	    </td> 
		<td class="tablecolumn">
	        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@UnitOfMeasure"/>
	    </td> 
		<td class="tablecolumn">
			<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@PalletId"/>
	    </td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@CaseId"/>
	    </td> 
		<td class="tablecolumn">
	        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@SerialNo"/>
	    </td> 
		<td nowrap="true" class="tablecolumn">
	 		<select OldValue="" <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+ReceiptLineCounter+"/@InventoryStatus")%> class="combobox">
				<yfc:loopOptions binding="xml:InventoryStatusList:/InventoryStatusList/@InventoryStatus" 
					name="InventoryStatus" value="InventoryStatus"/>
			</select>
        </td>
		<td class="tablecolumn">
	        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@Quantity"/>
	    </td> 
       <td class="numerictablecolumn">
            <input type="text" 		    <%=yfsGetTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+ReceiptLineCounter+"/@UnreceiveQuantity","", "xml:/ReceiptLine/Receipt/AllowedModifications")%> style='width:40px' /> 
        </td>
	</tr>
	<%}
	%>
	 </yfc:loopXML> 
<input type="hidden" name="xml:/Receipt/@ReceiptHeaderKey" value='<%=getValue("ReceiptLineList","xml:/ReceiptLineList/ReceiptLine/@ReceiptHeaderKey")%>'/>
	<input type="hidden" name="xml:/Receipt/Audit/@ReasonCode"/>
	<input type="hidden" name="xml:/Receipt/Audit/@ReasonText"/>
</tbody>
</table>
