<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="../console/scripts/om.js"></script>
<script language="javascript">
window.attachEvent("onload", IgnoreChangeNames);
document.body.attachEvent("onunload", processSaveOrderLineRecordsForCharges); 
</script>

<%
String chargeType=request.getParameter("chargeType");
String chargeTypeAttributePrefix = "";
String chargeQty = "";
String quantity = "";
boolean editable = false;

if (isVoid(chargeType)) {
    chargeType="Overall";
    chargeQty="OrderedPricing";
    quantity = "OrderedQty";
}
else if (equals(chargeType,"Remaining")) {
    chargeTypeAttributePrefix = "Remaining";
    chargeQty="RemainingPricing";
    editable = true;
    quantity = "Open_Quantity";
}
else if (equals(chargeType,"Invoiced")) {
    chargeTypeAttributePrefix = "Invoiced";
    chargeQty="InvoicedPricing";
    quantity = "Invoiced_Quantity";
}

// Reset the editable flag if modifications are not allowed
editable = editable & isModificationAllowed("xml:/@ChargePerLine","xml:/OrderLine/AllowedModifications");

Map cChargeCategory = getChargeCategoryMap((YFCElement)request.getAttribute("ChargeCategoryList"), (YFCElement)request.getAttribute("ChargeNameList"));

Set cChargeCat = cChargeCategory.keySet();
for(Iterator it2 = cChargeCat.iterator(); it2.hasNext(); )	{
	String sChargeCat = (String)it2.next();
	YFCElement e = (YFCElement)cChargeCategory.get(sChargeCat);
	request.setAttribute(sChargeCat, e);
}

%>

<table class="view" width="100%">
<tr>
    <td><yfc:i18n><%=quantity%></yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@PricingQty")%>'/></td>    
    <td class="detaillabel" ><yfc:i18n>Pricing_UOM</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/LinePriceInfo/@PricingUOM"/></td>    
    <td><yfc:i18n>Unit_Price</yfc:i18n></td>
    <%if (equals(chargeType,"Remaining")) {%>
        <td>
            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LinePriceInfo/@UnitPrice","xml:/OrderLine/Line" + chargeType + "Totals/@UnitPrice","xml:/OrderLine/AllowedModifications")%>/>
            <%=curr0[1]%>
        </td>
    <%} else {%>
        <td class="protectedtext"><% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;<yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@UnitPrice")%>'/>&nbsp;<%=curr1[1]%></td>
    <%}%>
    <td><yfc:i18n>Extended_Price</yfc:i18n></td>
    <td class="protectedtext"><% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%>&nbsp;<yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@ExtendedPrice")%>'/>&nbsp;<%=curr2[1]%></td>
</tr>
</table>
<table class="table" ID="ChargeBreakup" cellspacing="0" width="100%" <%if (editable) {%> initialRows="3" <%} %>>
<thead>
    <tr>
        <td class="checkboxheader">&nbsp;</td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
        <td class="numerictablecolumnheader"  <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%>><yfc:i18n>Per_Unit</yfc:i18n></td>
        <td class="numerictablecolumnheader"  <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%>><yfc:i18n>Per_Line</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Charge_Amount</yfc:i18n></td>	
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="OrderLine" binding="xml:/OrderLine/LineCharges/@LineCharge" id="LineCharge"> 
    <tr>
        <td class="checkboxcolumn">&nbsp;</td>

        <td class="tablecolumn">
            <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_" + LineChargeCounter + "/@ChargeName", "xml:/LineCharge/@ChargeName")%>/>
            <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_" + LineChargeCounter + "/@ChargeCategory", "xml:/LineCharge/@ChargeCategory")%>/>           
			
			<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/LineCharge/@ChargeCategory", true)%>

			<% if (equals(isChargeBillable(getValue("LineCharge","xml:/LineCharge/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"N")) { %> 
                <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INFO_CHARGE, "Informational_Charge")%>>
			<%}%>
        </td>                        
        
		<td class="tablecolumn">
			<%=displayChargeNameDesc(resolveValue("xml:/LineCharge/@ChargeCategory"), resolveValue("xml:/LineCharge/@ChargeName"), (YFCElement)request.getAttribute("ChargeNameList") )%>
        </td>                        

		<%if (equals(chargeType,"Remaining")) {%>
        <td class="numerictablecolumn">
            <% String[] curr3 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr3[0]%>&nbsp;
                <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_" + LineChargeCounter + "/@ChargePerUnit","xml:/LineCharge/@RemainingChargePerUnit","xml:/OrderLine/AllowedModifications")%>/>
            &nbsp;<%=curr3[1]%>
        </td>
        <td class="numerictablecolumn">
            <% String[] curr4 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr4[0]%>&nbsp;
                <input type="text" ChargePerLine='<%=resolveValue("xml:/LineCharge/@ChargePerLine")%>'  <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_" + LineChargeCounter + "/@ChargePerLine","xml:/LineCharge/@RemainingChargePerLine","xml:/OrderLine/AllowedModifications")%>/>
            &nbsp;<%=curr4[1]%>
        </td> 
        <%} else {%>        
        <td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:LineCharge:/LineCharge/@",chargeTypeAttributePrefix,"ChargePerUnit"))%>">
            <% String[] curr5 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr5[0]%>&nbsp;
                <yfc:getXMLValue name="LineCharge" binding='<%=buildBinding("xml:/LineCharge/@",chargeTypeAttributePrefix,"ChargePerUnit")%>'/>
            &nbsp;<%=curr5[1]%>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:LineCharge:/LineCharge/@",chargeTypeAttributePrefix,"ChargePerLine"))%>">
            <% String[] curr6 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr6[0]%>&nbsp;
                <yfc:getXMLValue name="LineCharge" binding='<%=buildBinding("xml:/LineCharge/@",chargeTypeAttributePrefix,"ChargePerLine")%>'/>
            &nbsp;<%=curr6[1]%>
        </td>
        <%} %>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:LineCharge:/LineCharge/@",chargeTypeAttributePrefix,"ChargeAmount"))%>">
             <% String[] curr9 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr9[0]%>&nbsp;
                <% if (equals(isChargeCategoryDiscount(getValue("LineCharge","xml:/LineCharge/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"Y")) { %> <yfc:i18n>(-)</yfc:i18n> <%}%>
                <yfc:getXMLValue name="LineCharge" binding='<%=buildBinding("xml:/LineCharge/@",chargeTypeAttributePrefix,"ChargeAmount")%>'/>
            &nbsp;<%=curr9[1]%>
        </td>
    </tr>                                                        
    </yfc:loopXML>
</tbody>
<%	//	show only valid charge names.  if charge name is already used delete it from list.
	YFCElement eChargeNameList = (YFCElement)request.getAttribute("ChargeNameList");
	YFCElement eOrderLine = ((YFCElement)request.getAttribute("OrderLine"));

	modifyChargeNameList(eChargeNameList, eOrderLine.getChildElement("LineCharges") );
%>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn" ID="DONTHIDE">&nbsp;</td>

        <td class="tablecolumn" ID="DONTHIDE" >
            <select  onChange="displayChargeNameDropDown(this)" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargeCategory", "xml:/OrderLine/AllowedModifications", "PRICE", "combo")%>>
                <yfc:loopOptions binding="xml:/ChargeCategoryList/@ChargeCategory" name="Description" isLocalized="Y" value="ChargeCategory"/>
            </select>
        </td>
		<%	
			Set cCC = cChargeCategory.keySet();
			boolean bShowEmptyTD = true;
			for(Iterator it2 = cCC.iterator(); it2.hasNext(); )	{
				String sChargeCat = (String) it2.next();
				String loopBinding = "xml:" + sChargeCat + ":/ChargeNameList/@ChargeName";
				if(bShowEmptyTD)	{
		%>			<td></td>
		<%			bShowEmptyTD = false;
				}

				if(isTrue("xml:/Rules/@RuleSetValue") )	{		%>
				<td class="tablecolumn" style='display:none' ID="<%=sChargeCat%>" >

					<select  <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargeName", "xml:/OrderLine/AllowedModifications", "PRICE", "combo")%>>
						<yfc:loopOptions binding="<%=loopBinding%>" name="Description" value="ChargeName" isLocalized="Y"/>
					</select>
				</td>
			<%	}	else	{	%>
					<td ID="<%=sChargeCat%>" style='display:none'>
						<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargeName")%>	/> 
					</td>
		<%		}
			}	%>

        <td class="numerictablecolumn" ID="DONTHIDE">
            <% String[] curr7 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr7[0]%>&nbsp;
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargePerUnit", "xml:/OrderLine/AllowedModifications", "PRICE", "text")%>/>
            &nbsp;<%=curr7[1]%>
        </td>
        <td class="numerictablecolumn" ID="DONTHIDE">
            <% String[] curr8 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr8[0]%>&nbsp;
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargePerLine", "xml:/OrderLine/AllowedModifications", "PRICE", "text")%>/>
            &nbsp;<%=curr8[1]%>
        </td>
        <td class="numerictablecolumn" ID="DONTHIDE"></td>
    </tr>
	<%if (editable) {%>
    <tr>
    	<td nowrap="true" colspan="6">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
    <%}%>
</tfoot>
</table>
