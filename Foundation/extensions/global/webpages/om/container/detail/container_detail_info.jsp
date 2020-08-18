<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/ydm_modificationutils.jspf" %>
<%
	String scacAndServiceKey=getValue("Container","xml:/Container/Shipment/@ScacAndServiceKey");
%>
<table width="100%" class="view">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Carrier_Service</yfc:i18n>
    </td>
    <td  nowrap="true" class="protectedtext">
		<% if(!isVoid(scacAndServiceKey)  ){ %>
        <yfc:getXMLValueI18NDB name="ScacAndServiceList" binding="xml:/ScacAndServiceList/ScacAndService/@ScacAndServiceDesc"/>
		<%}%>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Tracking_#</yfc:i18n> 
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue name="Container" binding="xml:/Container/@TrackingNo"/>
	       <input type="hidden" <%=getTextOptions( "xml:/Container/@TrackingNo", "xml:/Container/@TrackingNo")%>/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Manifest_#</yfc:i18n> 
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue name="Container" binding="xml:/Container/@ManifestNo"/>
	       <input type="hidden" <%=getTextOptions( "xml:/Container/@ManifestNo", "xml:/Container/@ManifestNo")%>/>
	</td>
</tr>
<tr>
	<td class="detaillabel" >
        <yfc:i18n>COD_Pay_Method</yfc:i18n>
    </td>
    <td class="protectedtext">
		  <yfc:getXMLValueI18NDB name="Container" binding="xml:/Container/Shipment/@CODPayMethod"/>
	</td>
    <td class="detaillabel" nowrap="true">
        <yfc:i18n>COD_Return_Tracking_#</yfc:i18n> 
    </td>
	<td class="protectedtext" >
		<yfc:getXMLValue name="Container" binding="xml:/Container/@CODReturnTrackingNo"/>
	       <input type="hidden" <%=getTextOptions( "xml:/Container/@CODReturnTrackingNo", "xml:/Container/@CODReturnTrackingNo")%>/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Gross_Weight</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerGrossWeight"/>
        &nbsp;
        <%=getComboText("xml:WeightUomList:/UomList/@Uom" ,"UomDescription" ,"Uom" ,"xml:/Container/@ContainerGrossWeightUOM",true)%>
    </td>
</tr>
<tr>
	<td class="detaillabel" >
        <yfc:i18n>COD_Amount</yfc:i18n>
    </td>
	<td class="protectedtext" nowrap="true">
		<% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%> 
        &nbsp;
		<input type="text" <%=yfsGetTextOptions("xml:/Container/@CODAmount", "xml:/Container/AllowedModifications")%> />
		&nbsp;
        <%=curr0[1]%>
	</td>
   <td class="detaillabel" >
        <yfc:i18n>Size</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/Corrugation/PrimaryInformation/@ShortDescription"/>
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Net_Weight</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerNetWeight"/>
        &nbsp;
        <%=getComboText("xml:WeightUomList:/UomList/@Uom" ,"UomDescription" ,"Uom" ,"xml:/Container/@ContainerNetWeightUOM",true)%>
    </td>
</tr>
<tr>
	<td class="detaillabel" >
        <yfc:i18n>Actual_Freight_Charge</yfc:i18n>
    </td>
    <td class="protectedtext">
		<% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%> 
        &nbsp;
        <yfc:getXMLValue name="Container" binding="xml:/Container/@ActualFreightCharge"/>
        &nbsp;
        <%=curr1[1]%>
	</td>
   <td class="detaillabel" >
        <yfc:i18n>Length</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerLength"/>
        &nbsp;
        <%=getComboText("xml:DimensionUomList:/UomList/@Uom" ,"UomDescription" ,"Uom" ,"xml:/Container/@ContainerLengthUOM",true)%>
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Actual_Weight</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/@ActualWeight"/>
        &nbsp;
        <%=getComboText("xml:WeightUomList:/UomList/@Uom" ,"UomDescription" ,"Uom" ,"xml:/Container/@ActualWeightUOM",true)%>
    </td>
</tr>
<tr>
	<td  nowrap="true" class="detaillabel" >
        <yfc:i18n>Special_Services_Surcharge</yfc:i18n>
    </td>
    <td class="protectedtext">
		<% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%> 
        &nbsp;
        <yfc:getXMLValue name="Container" binding="xml:/Container/@SpecialServicesSurcharge"/>
        &nbsp;
        <%=curr2[1]%>
	</td>
   <td class="detaillabel" >
        <yfc:i18n>Width</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerWidth"/>
        &nbsp;
        <%=getComboText("xml:DimensionUomList:/UomList/@Uom" ,"UomDescription" ,"Uom" ,"xml:/Container/@ContainerWidthUOM",true)%>
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Billed_Weight</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/@AppliedWeight"/>
        &nbsp;
        <%=getComboText("xml:WeightUomList:/UomList/@Uom" ,"UomDescription" ,"Uom" ,"xml:/Container/@ContainerNetWeightUOM",true)%>
    </td> 
</tr>
<tr>
	<td  nowrap="true" class="detaillabel" >
        <yfc:i18n>Declared_Insurance_Value</yfc:i18n> 
    </td>
   <td class="protectedtext">
        <% String[] curr3 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr3[0]%> 
        &nbsp;
        <yfc:getXMLValue name="Container" binding="xml:/Container/@DeclaredValue"/>
        &nbsp;
        <%=curr3[1]%>
    </td>
   <td class="detaillabel" >
        <yfc:i18n>Height</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerHeight"/>
        &nbsp;
        <%=getComboText("xml:DimensionUomList:/UomList/@Uom" ,"UomDescription" ,"Uom" ,"xml:/Container/@ContainerHeightUOM",true)%>
    </td>
		<td class="detaillabel" >
        <yfc:i18n>Has_Hazardous_Item(s)</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue name="Container" binding="xml:/Container/@IsHazmat"/>
    </td>
</tr>
</table>
