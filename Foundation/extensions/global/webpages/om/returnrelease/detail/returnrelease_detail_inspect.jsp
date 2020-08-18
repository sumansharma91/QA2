<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<yfc:callAPI apiID="AP3"/>
<%	int tagtrack=0;
	Map identifierAttrMap=null;	
	Map extnIdentifierAttrMap =null; 
%>
 <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
	<%
		String tagElement = "ReceiptLine";
		prepareTagDetails ((YFCElement) request.getAttribute("OrderLine"),tagElement,(YFCElement) request.getAttribute("ItemDetails"));
		identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"IdentifierAttributes");
		extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnIdentifierAttributes");

	%>
 </yfc:hasXMLNode>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%double inspectableQuantity = 0;%>
<yfc:loopXML binding="xml:/ReceiptLines/@ReceiptLine" id="ReceiptLine">
	<%inspectableQuantity  += getNumericValue("xml:/ReceiptLine/@AvailableForTranQuantity");%>
</yfc:loopXML>

<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
	<td>
    	<table class="view" width="100%">
    	<tr>
            <input type="hidden" <%=getTextOptions("xml:/InspectOrder/@OrderHeaderKey", "xml:/OrderRelease/@OrderHeaderKey")%>/>
            <td class="detaillabel" >
                <yfc:i18n>Line_#</yfc:i18n>
            </td>
            <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"></yfc:getXMLValue></td>
            <td class="detaillabel" >
                <yfc:i18n>Line_Quantity</yfc:i18n>
            </td>
            <td class="protectednumber"><yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"/></td>
            <td class="detaillabel" >
                <yfc:i18n>Inspectable_Quantity</yfc:i18n>
            </td>
            <td class="protectednumber">
                <%=inspectableQuantity%>
            </td>
    	</tr>
    	<tr>
    	    <td class="detaillabel" >
    	        <yfc:i18n>Item_ID</yfc:i18n>
    	    </td>
    	    <td class="protectedtext">
    	        <yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue></a>
    	    </td>
    	    <td class="detaillabel" >
    	        <yfc:i18n>Unit_Of_Measure</yfc:i18n>
    	    </td>
    	    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/Item/@UnitOfMeasure"></yfc:getXMLValue></td>
    	    <td class="detaillabel" >
    	        <yfc:i18n>Product_Class</yfc:i18n>
    	    </td>
    	    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"></yfc:getXMLValue>
    	    </td>
    	</tr>
    	<tr>
    	    <td class="detaillabel" >
    	        <yfc:i18n>Description</yfc:i18n>
    	    </td>
    	    <td class="protectedtext" colspan="5"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"></yfc:getXMLValue>
    	    </td>
    	</tr>
    	</table>
	</td>
</tr>
<tr>
	<td>
        <table class="table" width="100%" yfcMandatoryMessage="<yfc:i18n>Inspection_details_must_be_entered</yfc:i18n>" >
        <thead>
        <tr>
        	<td class="checkboxheader" sortable="no">
        		<input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
        	</td>
            <td class="tablecolumnheader">&nbsp;</td>
            <td class="tablecolumnheader"><yfc:i18n>Serial_#</yfc:i18n></td>
            
<%		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		normalMap = identifierAttrMap;
		extnMap = extnIdentifierAttrMap;

		int j=0;


		if ((normalMap != null) || (extnMap != null)) {
			tagtrack=1;%>
					
							<%while (j < 2) {
							    boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
											<td class="tablecolumnheader"><yfc:i18n><%=currentAttr%></yfc:i18n></td>
								    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}

%>
	
			
			<td class="tablecolumnheader"><yfc:i18n>Ship_By_Date</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/InspectOrder/ReceiptLines/ReceiptLine/@AvailableForTranQuantity")%>"><yfc:i18n>Remaining_Qty</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/InspectOrder/ReceiptLines/ReceiptLine/@DispositionCode")%>"><yfc:i18n>Disposition</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>Comments</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no"><yfc:i18n>New_Disposition</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/InspectOrder/ReceiptLines/ReceiptLine/@Quantity")%>"><yfc:i18n>Disposition_Qty</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/InspectOrder/ReceiptLines/ReceiptLine/@InspectionComments")%>"><yfc:i18n>Disposition_Comments</yfc:i18n></td>
        </tr>
        </thead>
        <tbody>
            <yfc:loopXML binding="xml:/ReceiptLines/@ReceiptLine" id="ReceiptLine">
    		<tr>
    			<yfc:makeXMLInput name="receiptLineKey">
		             <yfc:makeXMLKey binding="xml:/ReceiptLine/@ReceiptHeaderKey" value="xml:/ReceiptLine/@ReceiptHeaderKey" />
    				<yfc:makeXMLKey binding="xml:/ReceiptLine/@ReceiptLineKey" value="xml:/ReceiptLine/@ReceiptLineKey"/>
		             <yfc:makeXMLKey binding="xml:/ReceiptLine/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
		             <yfc:makeXMLKey binding="xml:/ReceiptLine/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey" />
		             <yfc:makeXMLKey binding="xml:/ReceiptLine/@OrderHeaderKey" value="xml:/OrderRelease/@OrderHeaderKey" />
		             <yfc:makeXMLKey binding="xml:/ReceiptLine/@ReceiptLineNo" value="xml:/ReceiptLine/@ReceiptLineNo" />
    			</yfc:makeXMLInput>
    
    			<td class="checkboxcolumn" ShouldCopy="false" nowrap="true">
    				<input type="checkbox" value='<%=getParameter("receiptLineKey")%>' name="chkEntityKey"/>
					<% //If OrigReceiptLineKey is not void, then this record has history.
	   				String lineKey = getValue("ReceiptLine", "xml:/ReceiptLine/@OrigReceiptLineKey");%>
                </td>
    			<td class="checkboxcolumn" nowrap="true" ShouldCopy="false" >
					<%if (!isVoid(lineKey)) {%>
					<a <%=getDetailHrefOptions("L01", getParameter("receiptLineKey"), "")%>>
					<img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.RECEIPT_LINE_HISTORY, "Disposition_History")%>></a>
					<%}%>
    			</td>
    			<td class="tablecolumn" nowrap="true" ShouldCopy="false" ><yfc:getXMLValue binding="xml:/ReceiptLine/@SerialNo"/></td>
    			
				
				
	<%	normalMap = null;
		extnMap = null;
		currentMap = null;
		normalMap = identifierAttrMap;
		extnMap = extnIdentifierAttrMap;

		j=0;


		if ((normalMap != null) || (extnMap != null)) {
			tagtrack=1;%>
					
							<%while (j < 2) {
							    boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);
											currentAttr="xml:/ReceiptLine/@"+currentAttr;
											%>
					<td class="tablecolumn" nowrap="true" ShouldCopy="false" ><%=resolveValue(currentAttr)%></td>
								    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}

	%>
				<td class="tablecolumn" nowrap="true" ShouldCopy="false" ><yfc:getXMLValue binding="xml:/ReceiptLine/@ShipByDate"/></td>
    			<td class="numerictablecolumn" nowrap="true" ShouldCopy="false" ><yfc:getXMLValue binding="xml:/ReceiptLine/@AvailableForTranQuantity"/></td>
    			<td class="tablecolumn" nowrap="true" ShouldCopy="false" >
					<!--<yfc:getXMLValue binding="xml:/ReceiptLine/@DispositionCode"/>-->
					<%=getComboText("xml:/ReturnDispositionList/@ReturnDisposition" ,"Description" ,"DispositionCode" ,"xml:/ReceiptLine/@DispositionCode",true)%>
				</td>
				</td>
    			<td class="tablecolumn" ShouldCopy="false" >
    				<yfc:getXMLValue binding="xml:/ReceiptLine/@InspectionComments"/>
    			</td>
    			<td class="tablecolumn" nowrap="true" ShouldCopy="true" >
    			
    	            <img IconName="addSplitLine" src="../console/icons/add.gif" 
    	                   <% if (getNumericValue("xml:/ReceiptLine/@AvailableForTranQuantity") > 1) { %>
    	                   class="lookupicon" onclick="yfcSplitLine(this)" 
    	                   <%} else {%>
    	                   style="filter:progid:DXImageTransform.Microsoft.BasicImage(grayScale = 1)"
    	                   <%}%>
    	                   />                   
    				<input type="hidden" <%=getTextOptions("xml:/InspectOrder/ReceiptLines/FromReceiptLine_" + ReceiptLineCounter + "/@ReceiptHeaderKey", "xml:/ReceiptLine/@ReceiptHeaderKey")%>/>
					<%String sName="";
						sName="xml:/InspectOrder/ReceiptLines/FromReceiptLine_" + ReceiptLineCounter + "/@ReceiptLineNo";%>
    				<input type="hidden" name='<%=sName%>' value='<%=resolveValue("xml:/ReceiptLine/@ReceiptLineNo")%>'/>

                    <% String retDispBinding = "xml:/InspectOrder/ReceiptLines/FromReceiptLine_" + ReceiptLineCounter + "/ToReceiptLines/ToReceiptLine_1/@DispositionCode"; %>
    				<select NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" 
    					class="combobox" <%=getComboOptions(retDispBinding, "xml:/ReceiptLine/@DispositionCode")%>>
    				    <yfc:loopOptions binding="xml:/ReturnDispositionList/@ReturnDisposition" name="Description" value="DispositionCode" selected="xml:/ReceiptLine/@DispositionCode" isLocalized="Y" targetBinding="<%=retDispBinding%>"/>
    				</select>
    			</td>
    			<td class="tablecolumn" nowrap="true" ShouldCopy="true" >
    				<input type="text" NewName="true" NewClassName="numericunprotectedinput" NewContentEditable="true" NewResetValue="true" 
    					class="numericunprotectedinput" <%=getTextOptions("xml:/InspectOrder/ReceiptLines/FromReceiptLine_" + ReceiptLineCounter + "/ToReceiptLines/ToReceiptLine_1/@Quantity", "")%>/>
    			</td>
    			<td class="tablecolumn" nowrap="true" ShouldCopy="true" >
    				<input type="text" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" 
    					class="unprotectedinput" <%=getTextOptions("xml:/InspectOrder/ReceiptLines/FromReceiptLine_" + ReceiptLineCounter + "/ToReceiptLines/ToReceiptLine_1/@InspectionComments", "")%>/>
    			</td>
    		</tr>
            </yfc:loopXML>
        </tbody>
        </table>
	</td>
</tr>
</table>
