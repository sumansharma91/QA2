<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script>
function processSaveRecordsForReceiveLines() {
	yfcSpecialChangeNames("ReceiveLines", true);
}
</script>
<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForReceiveLines);
</script>
<yfc:callAPI apiID="AP2"/>
<%	int tagtrack=0;
	Map identifierAttrMap=null;	
	Map extnIdentifierAttrMap =null; 
%>
 <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
	<%
		String tagElement = "InventoryTagAttributes";
		prepareTagDetails ((YFCElement) request.getAttribute("OrderLine"),tagElement,(YFCElement) request.getAttribute("ItemDetails"));
		identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"IdentifierAttributes");
		extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnIdentifierAttributes");
		tagtrack=0;
	%>
 </yfc:hasXMLNode>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
    yfcDoNotPromptForChanges(true);
</script>

<script language="javascript">
	function processReceiveLines(){
        yfcSpecialChangeNames("ReceiveLines", false);
	}
    document.body.attachEvent("onunload",processReceiveLines); 
</script>



<%double quantity = 0;%>
<yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
	<%quantity += getNumericValue("xml:/OrderStatus/@StatusQty");%>
</yfc:loopXML>

<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
<td>
<table class="view" width="100%">
<tr>
    <input type="hidden" <%=getTextOptions("xml:/Receipt/Shipment/@OrderReleaseKey", "xml:/OrderLineDetail/@OrderReleaseKey")%> />
    <input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceiptHeaderKey", "xml:/Receipts/Receipt/@ReceiptHeaderKey")%> />
    <input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceivingNode", "xml:/Receipts/Receipt/@ReceivingNode")%> />
    
	<td class="detaillabel" >
        <yfc:i18n>Line_#</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"></yfc:getXMLValue></td>
    <td class="detaillabel" >
        <yfc:i18n>Line_Quantity</yfc:i18n>
    </td>
    <td class="protectednumber"><yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"/></td>
    <td class="detaillabel" >
        <yfc:i18n>Receivable_Quantity</yfc:i18n>
    </td>
    <td class="protectednumber">
        <%=getLocalizedStringFromDouble(getLocale(), quantity)%>
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
        <yfc:i18n>UOM</yfc:i18n>
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
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"></yfc:getXMLValue>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Return_Reason</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@ReturnReasonShortDesc"></yfc:getXMLValue>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Line_Type</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@LineType"></yfc:getXMLValue>
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
                  	<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@SerialNo")%>"><yfc:i18n>Serial_#</yfc:i18n></td>
                   
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
					<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ShipByDate")%>"><yfc:i18n>Ship_By_Date</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@Quantity")%>"><yfc:i18n>Qty</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@DispositionCode")%>"><yfc:i18n>Disposition</yfc:i18n></td>
                    <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@InspectionComments")%>"><yfc:i18n>Comments</yfc:i18n></td>
                </tr>
            </thead>
            <tbody>
            <yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
            <tr>
	        <yfc:makeXMLInput name="orderLineKey">
        	        <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey"/>
               		<yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
               		<yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@StatusQty" value="xml:/OrderStatus/@StatusQty"/>
            	</yfc:makeXMLInput>
				
                <td class="tablecolumn" ShouldCopy="true" >
                    <img IconName="addSplitLine" class="lookupicon" src="../console/icons/add.gif" onclick="yfcSplitLine(this)" />                            
				    <input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@SerialNo", "")%>/>

					<input type="hidden" NewName="true" NewResetValue="false" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@ItemID", "xml:/OrderLine/Item/@ItemID")%> />
					
					<% if(isVoid(resolveValue("xml:/OrderLineDetail/@OrigOrderLineKey") ) )	{	%>
						<input type="hidden" NewName="true" NewResetValue="false" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@OrderLineKey", "xml:/OrderLine/@OrderLineKey")%> />
					<% }	else	{ %>
						<input type="hidden" NewName="true" NewResetValue="false" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@OrderLineKey", "xml:/OrderLineDetail/@OrigOrderLineKey")%> />
					<% } %>
						
					<input type="hidden" NewName="true" NewResetValue="false" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@InspectedBy", "xml:CurrentUser:/User/@Loginid")%> />

                    <input type="hidden" NewName="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@ExpectedLotNumber", "xml:/OrderStatus/OrderLine/Schedule/@LotNumber")%> />
                </td>

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
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
											<td class="tablecolumn" ShouldCopy="true" >
                    <input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@"+currentAttr, "")%>/>
                </td>
					
								    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}

%>

                <td class="tablecolumn" nowrap="true" ShouldCopy="true" >
                    <input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@ShipByDate", "")%>/>
                    <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
                </td>
                <td class="numerictablecolumn" ShouldCopy="true" >
                    <input type="text" class="numericunprotectedinput" NewName="true" NewClassName="numericunprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@Quantity", "")%>/>
                </td>
                <td class="tablecolumn"  ShouldCopy="true" >
                    <select class="combobox" NewName="true" NewResetValue="true" <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@DispositionCode")%>>
                        <yfc:loopOptions binding="xml:/ReturnDispositionList/@ReturnDisposition" name="Description" value="DispositionCode" selected="xml:/Receipt/@DispositionCode" isLocalized="Y"/>
                    </select>
                </td>
                <td class="tablecolumn" nowrap="true" ShouldCopy="true" >
                    <input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_" + OrderStatusCounter + "/@InspectionComments", "")%>/>
                </td>
            </tr>
            </yfc:loopXML>
            </tbody>
        </table>
    </td>
</tr>

</table>

