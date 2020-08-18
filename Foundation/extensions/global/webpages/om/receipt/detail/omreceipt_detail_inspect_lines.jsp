<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
    document.body.attachEvent("onunload",processReceiptLineDetails); 
</script>

<table class="table" id="ReceiptLineDetails">
<thead>
    <tr>  
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@ReceiptLineNo")%>">
            <yfc:i18n>Line</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@SerialNo")%>"> 				
			<yfc:i18n>Serial_#</yfc:i18n>
		</td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@PalletId")%>"> 			
			<yfc:i18n>Pallet_ID</yfc:i18n>
		</td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@CaseId")%>"> 				<yfc:i18n>Case_ID</yfc:i18n>
		</td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@ShipByDate")%>"> 				
			<yfc:i18n>Ship_By_Date</yfc:i18n>
		</td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/ReceiptLineList/ReceiptLine/@DispositionCode")%>">
            <yfc:i18n>Disposition</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Remaining_Qty</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@InspectionComments")%>">
            <yfc:i18n>Comments</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/ReceiptLineList/ReceiptLine/@DispositionCode")%>">
            <yfc:i18n>New_Disposition</yfc:i18n>
        </td>        
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Disposition_Qty</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@InspectionComments")%>">
            <yfc:i18n>Disposition_Comments</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody>
<%	String className="oddrow";
		ArrayList ReceiptLineList = getLoopingElementList("xml:getReceiptLineListToBeInspected:/ReceiptLineList/@ReceiptLine");	
		for (int ReceiptLineCounter = 0; ReceiptLineCounter < ReceiptLineList.size(); ReceiptLineCounter++) {
			
			YFCElement singleReceiptLine = (YFCElement) ReceiptLineList.get(ReceiptLineCounter);
			String dispCode=singleReceiptLine.getAttribute("DispositionCode");
			YFCElement receiptElem = (YFCElement) request.getAttribute("Receipt");
//			System.out.println(receiptElem.getString());
			receiptElem.setAttribute("DispositionCode",dispCode);
//			System.out.println(receiptElem.getString());

//			System.out.println("receiptElem.getAttribute() ==========="+receiptElem.getAttribute("DispositionCode"));
//			System.out.println("singleReceiptLine.getAttribute() ==========="+singleReceiptLine.getAttribute("DispositionCode"));

		     pageContext.setAttribute("ReceiptLine", singleReceiptLine);		
			 if (equals("oddrow",className))
						className="evenrow";
					else
						className="oddrow";		
%>
			<tr ID="ReceiptLineToBeInspectedRow" NewTRClassName='<%=className%>' >
				<td class="tablecolumn" ShouldCopy="false" >
					<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ReceiptLineNo"/>
					<input type="hidden" <%=getTextOptions("xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/@ReceiptHeaderKey","xml:/Receipt/@ReceiptHeaderKey")%> />	
					<input type="hidden" <%=getTextOptions("xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/@ReceiptLineNo","xml:/ReceiptLine/@ReceiptLineNo")%> />	
					<input type="hidden" <%=getTextOptions("xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/@InventoryStatus","xml:/Receipt/@InventoryStatus")%> />	
				</td>
				<td class="tablecolumn" ShouldCopy="false" >
					<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@SerialNo"/>
				</td>
				<td class="tablecolumn" ShouldCopy="false" >
					<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@PalletId"/>
				</td>
				<td class="tablecolumn" ShouldCopy="false" >
					<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@CaseId"/>
				</td>
				<td class="tablecolumn" ShouldCopy="false" >
					<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ShipByDate"/>
				</td>
				<td class="tablecolumn" ShouldCopy="false" >
					<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@DispositionCode"/>
				</td>
				<td class="numerictablecolumn" ShouldCopy="false" >
					<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@Quantity"/>
				</td>
				<td class="tablecolumn" ShouldCopy="false" >
					<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@InspectionComments"/>
				</td>
				<yfc:callAPI apiID='AP2'/>
	<%		YFCElement TransitionsListElem = (YFCElement) request.getAttribute("ReturnDisposition");
			if (TransitionsListElem == null) { %>
				<yfc:callAPI apiID='AP3'/>
<%			}
			YFCElement DispositionListElem = (YFCElement) request.getAttribute("ReturnDispositionList");
//				System.out.println("TransitionsListElem = "+TransitionsListElem.getString());
	%>
				<td class="tablecolumn" ShouldCopy="true" >
					<img IconName="addSplitLine" src="../console/icons/add.gif" 
    	                   <% if (getNumericValue("xml:/ReceiptLine/@Quantity") > 1) { %>
    	                   class="lookupicon" onclick="yfcSplitLine(this)" 
    	                   <%} else {%>
    	                   style="filter:progid:DXImageTransform.Microsoft.BasicImage(grayScale = 1)"
    	                   <%}%>
    	                   />
						   <%
								String sVal = "xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/ToReceiptLines/ToReceiptLine_1/@DispositionCode";
							if (TransitionsListElem != null) { %>
					<select OldValue="" <%=getComboOptions("xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/ToReceiptLines/ToReceiptLine_1/@DispositionCode")%> class="combobox" NewName="true" NewClassName="combobox" NewContentEditable="true" NewResetValue="true" >
								<yfc:loopOptions binding="xml:/ReturnDisposition/NextReturnDispositionList/@NextReturnDisposition" name="Description" 
								value="DispositionCode" selected="<%=sVal%>"/>
					 </select>
<%					} else {	%>
					<select OldValue="" <%=getComboOptions("xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/ToReceiptLines/ToReceiptLine_1/@DispositionCode")%> class="combobox" NewName="true" NewClassName="combobox" NewContentEditable="true" NewResetValue="true" >
								<yfc:loopOptions binding="xml:/ReturnDispositionList/@ReturnDisposition" name="Description" value="DispositionCode" selected="<%=sVal%>" isLocalized="Y"/>
					 </select>
<%					}	%>
				</td>
				<td class="numerictablecolumn" ShouldCopy="true" >
					<input type="text" NewName="true" NewResetValue="true" NewContentEditable="true" OldValue=""
					<%=yfsGetTextOptions("xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/ToReceiptLines/ToReceiptLine_1/@Quantity", "xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/ToReceiptLines/ToReceiptLine_1/@Quantity", "xml:/ReceiptLine/Receipt/AllowedModifications")%> style='width:40px' />
					<input type="hidden" NewName="true" NewResetValue="false"  <%=getTextOptions("xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/ToReceiptLines/ToReceiptLine_1/@InspectedBy",resolveValue("xml:CurrentUser:/User/@Loginid"))%> />
				 </td>
				<td class="tablecolumn" ShouldCopy="true"  >
					<input type="text" class="unprotectedinput" NewName="true" NewResetValue="true" NewContentEditable="true" NewClassName="unprotectedinput"  <%=getTextOptions("xml:/InspectReceipt/ReceiptLines/FromReceiptLine_"+ReceiptLineCounter+"/ToReceiptLines/ToReceiptLine_1/@InspectionComments","")%> />	
				</td>
			</tr>
<%	}	%>
</tbody>
</table>
