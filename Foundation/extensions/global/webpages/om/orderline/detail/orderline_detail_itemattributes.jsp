<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
<td>
	<table class="view" width="100%">
	<tr>
		<td>
			<fieldset>
			<legend><yfc:i18n>Classifications</yfc:i18n></legend> 
				<table class="view" width="100%">                
					<tr> 
						<td class="detaillabel" ><yfc:i18n>NMFC_Class</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@NMFCClass", "xml:/OrderLine/Item/@NMFCClass", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						<td class="detaillabel" ><yfc:i18n>NMFC_Code</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@NMFCCode", "xml:/OrderLine/Item/@NMFCCode", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						<td class="detaillabel" ><yfc:i18n>NMFC_Description</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@NMFCDescription", "xml:/OrderLine/Item/@NMFCDescription", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
					</tr>
					<tr>                     
						<td class="detaillabel" ><yfc:i18n>ISBN</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ISBN", "xml:/OrderLine/Item/@ISBN", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						<td class="detaillabel" ><yfc:i18n>Harmonized_Code</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@HarmonizedCode", "xml:/OrderLine/Item/@HarmonizedCode", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						<td class="detaillabel" ><yfc:i18n>Tax_Product_Code</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@TaxProductCode", "xml:/OrderLine/Item/@TaxProductCode", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
					</tr>
					<tr> 
						<td class="detaillabel" ><yfc:i18n>ECCN_No</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ECCNNo", "xml:/OrderLine/Item/@ECCNNo", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						<td class="detaillabel" ><yfc:i18n>Schedule_B_Code</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ScheduleBCode", "xml:/OrderLine/Item/@ScheduleBCode", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						 <td class="detaillabel" ><yfc:i18n>UPC_Code</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@UPCCode", "xml:/OrderLine/Item/@UPCCode", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
					</tr>
				</table>
			</fieldset>
		</td>
	</tr>
	<tr>
		<td>
			<fieldset>
			<legend><yfc:i18n>Other_Attributes</yfc:i18n></legend> 
				<table class="view" width="100%">
					<tr>
						<td class="detaillabel" ><yfc:i18n>Country_Of_Origin</yfc:i18n></td>
						<td>
							<select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/Item/@CountryOfOrigin", "xml:/OrderLine/Item/@CountryOfOrigin", "xml:/OrderLine/AllowedModifications")%> >
								<yfc:loopOptions binding="xml:CountryOfOriginList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
								value="CodeValue" selected="xml:/OrderLine/Item/@CountryOfOrigin" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/Item/@CountryOfOrigin"/>
							</select>
						</td>
						<td class="detaillabel" ><yfc:i18n>Import_Licence_No</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ImportLicenseNo", "xml:/OrderLine/@ImportLicenseNo", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						<td class="detaillabel" ><yfc:i18n>Import_Licence_Validity</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ImportLicenseExpDate","xml:/OrderLine/@ImportLicenseExpDate", "xml:/OrderLine/AllowedModifications")%> >
							<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/OrderLine/@ImportLicenseExpDate", "xml:/OrderLine/AllowedModifications") %> />
						</td>
					</tr>
					<tr>                             
						<td class="detaillabel" ><yfc:i18n>Product_Line</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ProductLine", "xml:/OrderLine/Item/@ProductLine", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						<td class="detaillabel" ><yfc:i18n>Manufacturer</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ManufacturerName", "xml:/OrderLine/Item/@ManufacturerName", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>
						<td class="detaillabel" ><yfc:i18n>Unit_Cost</yfc:i18n></td>
						<td nowrap="true">
                            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@UnitCost", "xml:/OrderLine/Item/@UnitCost", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
                            <%=curr0[1]%>
						</td>						   
					</tr>
					<tr>
						<td class="detaillabel" ><yfc:i18n>Item_Weight</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ItemWeight", "xml:/OrderLine/Item/@ItemWeight", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>  
						<td class="detaillabel" ><yfc:i18n>Item_Weight_UOM</yfc:i18n></td>
						<td nowrap="true">
							<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ItemWeightUOM", "xml:/OrderLine/Item/@ItemWeightUOM", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
						</td>						
					</tr>                   
				</table>            
			</fieldset>
		</td>
	</tr>
	</table>
</td>
</tr>
<tr>
	<td>
	<table class="table" cellpadding="0" cellspacing="0" width="100%" suppressFooter="true">
	<thead>
		<tr>
			<td class="tablecolumnheader" style="width:10%" sortable="no">&nbsp;</td>
			<td class="tablecolumnheader" style="width:20%" sortable="no"><yfc:i18n>Item</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Description</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Customer</yfc:i18n></td>
			<td nowrap="true">
				<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@CustomerItem", "xml:/OrderLine/Item/@CustomerItem", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
			</td>     
			<td nowrap="true">
				<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@CustomerItemDesc", "xml:/OrderLine/Item/@CustomerItemDesc", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
			</td>
		</tr>
		<tr>
			<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Manufacturer</yfc:i18n></td>
			<td nowrap="true">
				<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ManufacturerItem", "xml:/OrderLine/Item/@ManufacturerItem", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
			</td>   
			<td nowrap="true">
				<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ManufacturerItemDesc", "xml:/OrderLine/Item/@ManufacturerItemDesc", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
			</td> 
		</tr>
		<tr>
			<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Supplier</yfc:i18n></td>
			<td nowrap="true">
				<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@SupplierItem", "xml:/OrderLine/Item/@SupplierItem", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
			</td>      
			<td nowrap="true">
				<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@SupplierItemDesc", "xml:/OrderLine/Item/@SupplierItemDesc", "xml:/OrderLine/AllowedModifications")%> />&nbsp;
			</td>
		</tr>
	</tbody>
	</table>				
	</td>
</tr>
</table>

