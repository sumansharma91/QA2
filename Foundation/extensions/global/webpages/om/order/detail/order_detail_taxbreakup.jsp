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

<script language="javascript">
window.attachEvent("onload", IgnoreChangeNames);
document.body.attachEvent("onunload", processSaveRecordsForTaxes);
</script>

<%
String chargeType=request.getParameter("chargeType");
String chargeTypeAttributePrefix = "";
boolean editable = false;

if (isVoid(chargeType)) {
    chargeType="Overall";
}
else if (equals(chargeType,"Remaining")) {
    chargeTypeAttributePrefix = "Remaining";
    editable = true;
}
else if (equals(chargeType,"Invoiced")) {
    chargeTypeAttributePrefix = "Invoiced";
}

String chargeCategory="";
String chargeName="";
String chargeAmount="";

// Reset the editable flag if modifications are not allowed
editable = editable & isModificationAllowed("xml:/@Tax","xml:/Order/AllowedModifications");

Map cChargeCategory = getChargeCategoryMap((YFCElement)request.getAttribute("ChargeCategoryList"), (YFCElement)request.getAttribute("ChargeNameList"));

Set cChargeCat = cChargeCategory.keySet();
for(Iterator it2 = cChargeCat.iterator(); it2.hasNext(); )	{
	String sChargeCat = (String)it2.next();
	YFCElement e = (YFCElement)cChargeCategory.get(sChargeCat);
	request.setAttribute(sChargeCat, e);
}
%>

<table class="table" ID="TaxBreakup" cellspacing="0" width="100%" <%if (editable) {%> initialRows="3" <%} %>>
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">&nbsp;</td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Charge_Amount</yfc:i18n></td>	
        <td class="tablecolumnheader"><yfc:i18n>Tax_Name</yfc:i18n></td>	
        <td class="numerictablecolumnheader" <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%>><yfc:i18n>Tax_Percentage</yfc:i18n></td>  
        <td class="numerictablecolumnheader" <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%>><yfc:i18n>Tax_Amount</yfc:i18n></td>   
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="Order" binding="xml:/Order/HeaderTaxes/@HeaderTax" id="HeaderTax"> 
    <tr>
        <td class="checkboxcolumn">&nbsp;</td>
        <td class="tablecolumn">
            <input type="hidden" <%=getTextOptions("xml:/Order/HeaderTaxes/HeaderTax_" + HeaderTaxCounter + "/@ChargeName", "xml:/HeaderTax/@ChargeName")%>/>
            <input type="hidden" <%=getTextOptions("xml:/Order/HeaderTaxes/HeaderTax_" + HeaderTaxCounter + "/@ChargeCategory", "xml:/HeaderTax/@ChargeCategory")%>/>
            <input type="hidden" <%=getTextOptions("xml:/Order/HeaderTaxes/HeaderTax_" + HeaderTaxCounter + "/@TaxName", "xml:/HeaderTax/@TaxName")%>/>   
			
			<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/HeaderTax/@ChargeCategory", true)%>

            <% if (equals(isChargeBillable(getValue("HeaderTax","xml:/HeaderTax/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"N")) { %>
                <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INFO_CHARGE, "Informational_Charge")%>>
            <%}%>
        </td>

		<td class="tablecolumn">
			<%=displayChargeNameDesc(resolveValue("xml:/HeaderTax/@ChargeCategory"), resolveValue("xml:/HeaderTax/@ChargeName"), 
				(YFCElement)request.getAttribute("ChargeNameList") )%>
		</td>

        <%chargeCategory=getValue("HeaderTax","xml:/HeaderTax/@ChargeCategory");%>
        <%chargeName=getValue("HeaderTax","xml:/HeaderTax/@ChargeName");%>
        <yfc:loopXML name="Order" binding="xml:/Order/HeaderCharges/@HeaderCharge" id="HeaderCharge">
            <%if(equals(chargeCategory, resolveValue("xml:/HeaderCharge/@ChargeCategory")) && equals(chargeName, resolveValue("xml:/HeaderCharge/@ChargeName"))) {          
                chargeAmount=resolveValue(buildBinding("xml:/HeaderCharge/@",chargeTypeAttributePrefix,"ChargeAmount"));
                break;
            } else {
                chargeAmount=getLocalizedStringFromComputedDouble(getLocale(),0.00);
            }%>                         
        </yfc:loopXML>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue(chargeAmount)%>">
            <% if (equals(isChargeCategoryDiscount(getValue("HeaderTax","xml:/HeaderTax/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"Y")) { %> <yfc:i18n>(-)</yfc:i18n> <%}%>
            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<%=chargeAmount%>&nbsp;<%=curr0[1]%>
        </td>        
        <td class="tablecolumn"><%=getComboText("xml:CommonCodeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/HeaderTax/@TaxName", true)%></td>    
        <td class="numerictablecolumn" <%if (!equals(chargeType,"Remaining")) {%> sortValue="<%=getNumericValue("xml:HeaderTax:/HeaderTax/@TaxPercentage")%>" <%}%>>
            <%if (equals(chargeType,"Remaining")) {%>
                <input type="text" <%=yfsGetTextOptions("xml:/Order/HeaderTaxes/HeaderTax_" + HeaderTaxCounter + "/@TaxPercentage","xml:/HeaderTax/@TaxPercentage","xml:/Order/AllowedModifications")%>/>
            <%} else {%>
                <yfc:getXMLValue name="HeaderTax" binding="xml:/HeaderTax/@TaxPercentage"/>
            <%} %>
        </td>
        <td class="numerictablecolumn" <%if (!equals(chargeType,"Remaining")) {%> sortValue="<%=getNumericValue(buildBinding("xml:HeaderTax:/HeaderTax/@",chargeTypeAttributePrefix,"Tax"))%>" <%}%>>
            <% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;
            <%if (equals(chargeType,"Remaining")) {%>
                <input type="text" <%=yfsGetTextOptions("xml:/Order/HeaderTaxes/HeaderTax_" + HeaderTaxCounter + "/@Tax","xml:/HeaderTax/@RemainingTax","xml:/Order/AllowedModifications")%>/>
            <%} else {%>
                <yfc:getXMLValue name="HeaderTax" binding='<%=buildBinding("xml:/HeaderTax/@",chargeTypeAttributePrefix,"Tax")%>'/>
            <%} %>
            &nbsp;<%=curr1[1]%>
        </td>
    </tr>                                                        
    </yfc:loopXML>
</tbody>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn" ID="DONTHIDE">&nbsp;</td>

        <td class="tablecolumn" ID="DONTHIDE" >
            <select  onChange="displayChargeNameDropDown(this)" <%=yfsGetTemplateRowOptions("xml:/Order/HeaderTaxes/HeaderTax_/@ChargeCategory", "xml:/Order/AllowedModifications", "TAX", "combo")%>>
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
					<select  <%=yfsGetTemplateRowOptions("xml:/Order/HeaderTaxes/HeaderTax_/@ChargeName", "xml:/Order/AllowedModifications", "TAX", "combo")%>>
						<yfc:loopOptions binding="<%=loopBinding%>" name="Description" value="ChargeName" isLocalized="Y"/>
					</select>
				</td>
			<%	}	else	{	%>
					<td ID="<%=sChargeCat%>" style='display:none'>
						<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/HeaderTaxes/HeaderTax_/@ChargeName")%>	/> 
					</td>
		<%		}
			}	%>
        <td class="numerictablecolumn" ID="DONTHIDE"></td>
        <td class="tablecolumn" ID="DONTHIDE">
            <select <%=yfsGetTemplateRowOptions("xml:/Order/HeaderTaxes/HeaderTax_/@TaxName", "xml:/Order/AllowedModifications", "TAX", "combo")%>>
                <yfc:loopOptions binding="xml:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue"  isLocalized="Y" />
            </select>
        </td>
        <td class="numerictablecolumn" ID="DONTHIDE">
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/HeaderTaxes/HeaderTax_/@TaxPercentage", "xml:/Order/AllowedModifications", "TAX", "text")%>/>
        </td>
        <td class="numerictablecolumn" ID="DONTHIDE">
            <% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%>&nbsp;
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/HeaderTaxes/HeaderTax_/@Tax", "xml:/Order/AllowedModifications", "TAX", "text")%>/>
            &nbsp;<%=curr2[1]%>
        </td>
    </tr>
	<%if (editable) {%>
    <tr>
    	<td nowrap="true" colspan="7">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
    <%}%>
</tfoot>
</table>
