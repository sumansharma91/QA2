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
document.body.attachEvent("onunload", processSaveOrderRecordsForCharges);
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

// Reset the editable flag if modifications are not allowed
editable = editable & isModificationAllowed("xml:/@ChargeAmount","xml:/Order/AllowedModifications");

Map cChargeCategory = getChargeCategoryMap((YFCElement)request.getAttribute("ChargeCategoryList"), (YFCElement)request.getAttribute("ChargeNameList"));

Set cChargeCat = cChargeCategory.keySet();
for(Iterator it2 = cChargeCat.iterator(); it2.hasNext(); )	{
	String sChargeCat = (String)it2.next();
	YFCElement e = (YFCElement)cChargeCategory.get(sChargeCat);
	request.setAttribute(sChargeCat, e);
}

%>

<table class="table" ID="ChargeBreakup" cellspacing="0" width="100%" <%if (editable) {%> initialRows="3" <%} %> >
<thead>
    <tr>
        <td class="checkboxheader">&nbsp;</td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
        <td class="numerictablecolumnheader" <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%>><yfc:i18n>Charge_Amount</yfc:i18n></td>   
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="Order" binding="xml:/Order/HeaderCharges/@HeaderCharge" id="HeaderCharge"> 
    <tr>
        <td class="checkboxcolumn">&nbsp;</td>
        
		<td class="tablecolumn">                        
            <input type="hidden" <%=getTextOptions("xml:/Order/HeaderCharges/HeaderCharge_" + HeaderChargeCounter + "/@ChargeName", "xml:/HeaderCharge/@ChargeName")%>/>
            <input type="hidden" <%=getTextOptions("xml:/Order/HeaderCharges/HeaderCharge_" + HeaderChargeCounter + "/@ChargeCategory", "xml:/HeaderCharge/@ChargeCategory")%>/>

			<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/HeaderCharge/@ChargeCategory", true)%>

        </td>

		<td class="tablecolumn">
		<%	String sChargeNameBinding = "xml:" + resolveValue("xml:/HeaderCharge/@ChargeCategory") + ":/ChargeNameList/@ChargeName";
		%>
			<%=displayChargeNameDesc(resolveValue("xml:/HeaderCharge/@ChargeCategory"), resolveValue("xml:/HeaderCharge/@ChargeName"), 
				(YFCElement)request.getAttribute(resolveValue("xml:/HeaderCharge/@ChargeCategory")) )%>

            <% if (equals(isChargeBillable(getValue("HeaderCharge","xml:/HeaderCharge/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"N")) { %>
                <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INFO_CHARGE, "Informational_Charge")%>>
            <%}%>
        </td>

        <td class="numerictablecolumn" <%if (!equals(chargeType,"Remaining")) {%> sortValue="<%=getNumericValue(buildBinding("xml:HeaderCharge:/HeaderCharge/@",chargeTypeAttributePrefix,"ChargeAmount"))%>" <%}%>>
            <% if (equals(isChargeCategoryDiscount(getValue("HeaderCharge","xml:/HeaderCharge/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"Y")) { %> <yfc:i18n>(-)</yfc:i18n> <%}%>
            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;
            <%if (equals(chargeType,"Remaining")) {%> 
                <input type="text" ChargeAmount='<%=resolveValue("xml:/HeaderCharge/@ChargeAmount")%>'  <%=yfsGetTextOptions("xml:/Order/HeaderCharges/HeaderCharge_" + HeaderChargeCounter + "/@ChargeAmount","xml:/HeaderCharge/@RemainingChargeAmount","xml:/Order/AllowedModifications")%>/>
            <%} else {%>
                <yfc:getXMLValue name="HeaderCharge" binding='<%=buildBinding("xml:/HeaderCharge/@",chargeTypeAttributePrefix,"ChargeAmount")%>'/>
            <%}%>
                &nbsp;<%=curr0[1]%>
        </td>
    </tr>                                         
    </yfc:loopXML>
</tbody>
<%	//	show only valid charge names.  if charge name is already used delete it from list.
	YFCElement eChargeNameList = (YFCElement)request.getAttribute("ChargeNameList");
	YFCElement eOrder = ((YFCElement)request.getAttribute("Order"));

	modifyChargeNameList(eChargeNameList, eOrder.getChildElement("HeaderCharges") );

%>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn" ID="DONTHIDE">&nbsp;</td>

        <td class="tablecolumn" ID="DONTHIDE" >
            <select  onChange="displayChargeNameDropDown(this)" <%=yfsGetTemplateRowOptions("xml:/Order/HeaderCharges/HeaderCharge_/@ChargeCategory", "xml:/Order/AllowedModifications", "PRICE", "combo")%>>
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

					<select  <%=yfsGetTemplateRowOptions("xml:/Order/HeaderCharges/HeaderCharge_/@ChargeName", "xml:/Order/AllowedModifications", "PRICE", "combo")%>>
						<yfc:loopOptions binding="<%=loopBinding%>" name="Description" value="ChargeName" isLocalized="Y"/>
					</select>
				</td>
			<%	}	else	{	%>
					<td ID="<%=sChargeCat%>" style='display:none'>
						<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/HeaderCharges/HeaderCharge_/@ChargeName")%>	/> 
					</td>
		<%		}
			}	%>

		<td class="numerictablecolumn" ID="DONTHIDE" >
			<% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;
			<input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/HeaderCharges/HeaderCharge_/@ChargeAmount", "xml:/Order/AllowedModifications", "PRICE", "text")%>/>
			&nbsp;<%=curr1[1]%>
		</td>
    </tr>

	<%if (editable) {%>
		<tr>
			<td nowrap="true" colspan="4">
				<jsp:include page="/common/editabletbl.jsp" flush="true"/>
			</td>
		</tr>
    <%}%>
</tfoot>
</table>
