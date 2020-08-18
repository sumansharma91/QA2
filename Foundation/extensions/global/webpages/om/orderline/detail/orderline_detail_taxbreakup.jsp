<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %> 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
window.attachEvent("onload", IgnoreChangeNames);
document.body.attachEvent("onunload", processSaveRecordsForTaxes);
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
    quantity = "Remaining_Quantity";
}
else if (equals(chargeType,"Invoiced")) {
    chargeTypeAttributePrefix = "Invoiced";
    chargeQty="InvoicedPricing";
    quantity = "Invoiced_Quantity";
}

String chargeCategory="";
String chargeName="";
String chargeAmount="";

// Reset the editable flag if modifications are not allowed
editable = editable & isModificationAllowed("xml:/@Tax","xml:/OrderLine/AllowedModifications");
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
    <td class="detaillabel" ><yfc:i18n><%=quantity%></yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@PricingQty")%>'/></td>
    <td class="detaillabel" ><yfc:i18n>Pricing_UOM</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/LinePriceInfo/@PricingUOM"/></td> 
    <td class="detaillabel" ><yfc:i18n>Unit_Price</yfc:i18n></td>
    <td class="protectedtext"><% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@UnitPrice")%>'/>&nbsp;<%=curr0[1]%></td>
    <td class="detaillabel" ><yfc:i18n>Extended_Price</yfc:i18n></td>
    <td class="protectedtext"><% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;<yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@ExtendedPrice")%>'/>&nbsp;<%=curr1[1]%></td>
</tr>
</table>
<table class="table" ID="TaxBreakup" cellspacing="0" width="100%"  <%if (editable) {%> initialRows="3" <%} %>>
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">&nbsp;</td>
        <td class="tablecolumnheader" sortable="no" style="width:70px"><yfc:i18n>Apply_To_Price</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Charge_Amount</yfc:i18n></td>	
        <td class="tablecolumnheader"><yfc:i18n>Tax_Name</yfc:i18n></td>	
        <td class="numerictablecolumnheader" <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%> ><yfc:i18n>Tax_Percentage</yfc:i18n></td>   
        <td class="numerictablecolumnheader" <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%>><yfc:i18n>Tax_Amount</yfc:i18n></td>   
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="OrderLine" binding="xml:/OrderLine/LineTaxes/@LineTax" id="LineTax"> 
    <tr>
        <td class="checkboxcolumn">&nbsp;</td>
        <td class="checkboxcolumn" >
            <input class="checkbox" type="checkbox" disabled="true" <%if(equals("Price", resolveValue("xml:/LineTax/@ChargeCategory") ) ) { %> checked <% } %> />
        </td>

        <td class="tablecolumn">
            <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_" + LineTaxCounter + "/@TaxName", "xml:/LineTax/@TaxName")%>/>
            <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_" + LineTaxCounter + "/@ChargeName", "xml:/LineTax/@ChargeName")%>/>
            <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_" + LineTaxCounter + "/@ChargeCategory", "xml:/LineTax/@ChargeCategory")%>/>

			<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/LineTax/@ChargeCategory", true)%>

	        <% if (equals(isChargeBillable(getValue("LineTax","xml:/LineTax/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"N")) { %>
                <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INFO_CHARGE, "Informational_Charge")%>>
            <%}%>
		</td>

		<td>
			<%=displayChargeNameDesc(resolveValue("xml:/LineTax/@ChargeCategory"), resolveValue("xml:/LineTax/@ChargeName"), (YFCElement)request.getAttribute("ChargeNameList") )%>
		</td>

		<%chargeCategory=getValue("LineTax","xml:/LineTax/@ChargeCategory");%>
        <%chargeName=getValue("LineTax","xml:/LineTax/@ChargeName");%>
        <yfc:loopXML name="OrderLine" binding="xml:/OrderLine/LineCharges/@LineCharge" id="LineCharge">
            <%if(equals(chargeCategory,getValue("LineCharge","xml:/LineCharge/@ChargeCategory")) && equals(chargeName,getValue("LineCharge","xml:/LineCharge/@ChargeName"))) {          
                chargeAmount=getValue("LineCharge",buildBinding("xml:/LineCharge/@",chargeTypeAttributePrefix,"ChargeAmount"));
                break;
            } else {
                chargeAmount=getLocalizedStringFromComputedDouble(getLocale(),0.00);
            }%>                         
        </yfc:loopXML>
        
        <td class="numerictablecolumn" sortValue="<%=getNumericValue(chargeAmount)%>">
	<%if (equals(getValue("LineTax","xml:/LineTax/@ChargeCategory"),"Price")) {%>
		&nbsp;<yfc:i18n>N/A</yfc:i18n>&nbsp;
	<%} else {%>
            <% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%>&nbsp;<%=chargeAmount%>&nbsp;<%=curr2[1]%>
	<%}%>
        </td>        
        <td class="tablecolumn"><%=getComboText("xml:CommonCodeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/LineTax/@TaxName", true)%></td>      
        <td class="numerictablecolumn" <%if (!equals(chargeType,"Remaining")) {%> sortValue="<%=getNumericValue("xml:LineTax:/LineTax/@TaxPercentage")%>" <%}%>>  
            <%if (equals(chargeType,"Remaining")) {%>
                <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_" + LineTaxCounter + "/@TaxPercentage","xml:/LineTax/@TaxPercentage","xml:/OrderLine/AllowedModifications")%>/>
            <%} else {%>
                <yfc:getXMLValue name="LineTax" binding="xml:/LineTax/@TaxPercentage"/>
            <%} %>
        </td>
        <td class="numerictablecolumn" <%if (!equals(chargeType,"Remaining")) {%> sortValue="<%=getNumericValue(buildBinding("xml:LineTax:/LineTax/@",chargeTypeAttributePrefix,"Tax"))%>" <%}%>>
            <% String[] curr3 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr3[0]%>&nbsp;
			  <% if (equals(isChargeCategoryDiscount(getValue("LineTax","xml:/LineTax/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"Y")) { %> <yfc:i18n>(-)</yfc:i18n> <%}%>
            <%if (equals(chargeType,"Remaining")) {%>
                <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_" + LineTaxCounter + "/@Tax",buildBinding("xml:/LineTax/@",chargeTypeAttributePrefix,"Tax"),"xml:/OrderLine/AllowedModifications")%>/>
            <%} else {%>
                <yfc:getXMLValue name="LineTax" binding='<%=buildBinding("xml:/LineTax/@",chargeTypeAttributePrefix,"Tax")%>'/>
            <%} %>
            &nbsp;<%=curr3[1]%>
        </td>
    </tr>                                                        
    </yfc:loopXML>
</tbody>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn" ID="DONTHIDE">&nbsp;</td>

        <td ID="DONTHIDE" type="checkboxTd">
            <input class="checkbox" type="checkbox" yfcCheckedValue="Price" yfcUnCheckedValue="" <%=getCheckBoxOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_/@ChargeCategory","ChargeCategory", "Price")%> onclick="setAsPriceChargeCategory(this)"/>
		</td>

		<td class="tablecolumn" ID="DONTHIDE" >
            <select  onChange="displayChargeNameDropDown(this, 'Y')" ID="ChargeCategory"  <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_/@ChargeCategory", "xml:/OrderLine/AllowedModifications", "TAX", "combo")%>>
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

					<select  <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_/@ChargeName", "xml:/OrderLine/AllowedModifications", "TAX", "combo")%>>
						<yfc:loopOptions binding="<%=loopBinding%>" name="Description" value="ChargeName" isLocalized="Y"/>
					</select>
				</td>
			<%	}	else	{	%>
					<td ID="<%=sChargeCat%>" style='display:none'>
						<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_/@ChargeName")%>	/> 
					</td>
		<%		}
			}	%>

        <td class="numerictablecolumn" ID="DONTHIDE"></td>

		<td class="tablecolumn" ID="DONTHIDE">
            <select <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_/@TaxName", "xml:/OrderLine/AllowedModifications", "TAX", "combo")%>>
                <yfc:loopOptions binding="xml:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
        <td class="numerictablecolumn" ID="DONTHIDE">
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_/@TaxPercentage", "xml:/OrderLine/AllowedModifications", "TAX", "text")%>/>
        </td>
        <td class="numerictablecolumn" ID="DONTHIDE">
            <% String[] curr4 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr4[0]%>&nbsp;
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineTaxes/LineTax_/@Tax", "xml:/OrderLine/AllowedModifications", "TAX", "text")%>/>
            &nbsp;<%=curr4[1]%>
        </td>
    </tr>
	<%if (editable) {%>
    <tr>
    	<td nowrap="true" colspan="8">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
    <%}%>
</tfoot>
</table>
