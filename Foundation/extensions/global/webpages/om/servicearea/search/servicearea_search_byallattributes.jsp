<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/order.jspf" %>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript">
function setReferenceFields(serviceTypeId) {
		var otableId = window.document.getElementById("SearchTable");
		var oCells = otableId.getElementsByTagName("INPUT");
		var itemValue = "";
		var uomValue = "";
		var itemGroupCodeValue = "";
		var count = 0;
		var item = document.all("xml:yfcSearchCriteria:/Promise/PromiseServiceLines/PromiseServiceLine/@ItemID");
		var uom = document.all("xml:yfcSearchCriteria:/Promise/PromiseServiceLines/PromiseServiceLine/@UnitOfMeasure");
		var itemGroupCode = document.all("xml:yfcSearchCriteria:/Promise/PromiseServiceLines/PromiseServiceLine/@ItemGroupCode");
		for ( var i = 0; i < oCells.length; i++ ) {
			if(serviceTypeId == oCells[i].value){
				itemValue = oCells[i].getAttribute("ItemID");
				uomValue = oCells[i].getAttribute("Uom");
				itemGroupCodeValue = oCells[i].getAttribute("ItemGroupCode");
				item.value = itemValue;
				uom.value = uomValue; 
				itemGroupCode.value = itemGroupCodeValue;
				count = 1;
			}
		}
		
		if(count < 1){
			item.value = " ";
			uom.value = " "; 
			itemGroupCode.value = " ";
		}
	}
</script>

<% 

	// Default the enterprise code if it is not passed
    String enterpriseCode = (String) request.getParameter("xml:/Promise/@EnterpriseCode");
    if (isVoid(enterpriseCode)) {
        enterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
        request.setAttribute("xml:/Promise/@EnterpriseCode", enterpriseCode);
    }

	// Default the seller to logged in organization if it plays a role of seller
    String sellerOrgCode = (String) request.getParameter("xml:yfcSearchCriteria:/Promise/@OrganizationCode");
    if (isVoid(sellerOrgCode)) {
        if(isRoleDefaultingRequired((YFCElement) request.getAttribute("CurrentOrgRoleList"))){
            sellerOrgCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
        }
    }

    String country = (String) request.getParameter("xml:yfcSearchCriteria:/Promise/PromiseServiceLines/PromiseServiceLine/ShipToAddress/@Country");
    if (isVoid(country)) {
		country = getLocale().getCountry();
	}	
%>

<table class="view">
<tr>
	<td>
		<input type="hidden" <%=getTextOptions("xml:/Promise/PromiseServiceLines/PromiseServiceLine/@ItemID")%>/>
		<input type="hidden" <%=getTextOptions("xml:/Promise/PromiseServiceLines/PromiseServiceLine/@UnitOfMeasure")%>/>
		<input type="hidden" <%=getTextOptions("xml:/Promise/PromiseServiceLines/PromiseServiceLine/@ItemGroupCode")%>/>
		<input type="hidden" <%=getTextOptions("xml:/Promise/PromiseServiceLines/PromiseServiceLine/@LineId","1")%>/>
    </td>
</tr>
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
	<jsp:param name="EnterpriseCodeBinding" value="xml:/Promise/@EnterpriseCode"/>
	<jsp:param name="ShowDocumentType" value="false"/>
	<jsp:param name="AcrossEnterprisesAllowed" value="false"/>
	<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Enterprise Code) %>
    <yfc:callAPI apiID="AP2"/>

<%	filterServiceTypeList((YFCElement) request.getAttribute("ServiceTypes")); %>

<tr>
	<td class="searchlabel" >
		<yfc:i18n>Seller</yfc:i18n>
	</td>
</tr>
<tr>
	<td nowrap="true" class="searchcriteriacell">
		<select class="combobox" <%=getComboOptions("xml:/Promise/@OrganizationCodeQryType")%>>
			<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
			value="QueryType" selected="xml:/Promise/@OrganizationCodeQryType"/>
		</select>
		<% if(!isVoid(sellerOrgCode)){ %>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Promise/@OrganizationCode", sellerOrgCode)%>/>
		<% } else { %>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Promise/@OrganizationCode")%>/>
		<% } %>
		<img class="lookupicon" name="search" onclick="callLookup(this,'organization')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization") %> />
	</td>
</tr>
<tr>
    <td class="searchlabel"><yfc:i18n>Country</yfc:i18n></td>
</tr>
<tr>
	<td class="searchcriteriacell">
		<select class="combobox" <%=getComboOptions("xml:/Promise/PromiseServiceLines/PromiseServiceLine/ShipToAddress/@Country")%>>
			<yfc:loopOptions binding="xml:CommonCountryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" isLocalized="Y" value="CodeValue"  selected="<%=country%>"/>
		</select>
</tr>
 <tr>
	<td class="searchlabel" >
		<yfc:i18n>Service_Type</yfc:i18n>
	</td>
</tr>
<tr>
	<td class="searchcriteriacell">
		<select class="combobox" onchange="setReferenceFields(this.value)" <%=getComboOptions("xml:/Promise/PromiseServiceLines/PromiseServiceLine/@ServiceTypeID")%>>
			<yfc:loopOptions binding="xml:ServiceTypes:/ServiceTypeList/@ServiceType" name="Description"
			value="ServiceTypeID" selected="xml:/Promise/PromiseServiceLines/PromiseServiceLine/@ServiceTypeID" isLocalized="Y"/>
		</select>
	</td>
</tr>
<tr>
    <td class="searchlabel"><yfc:i18n>Postal_Code</yfc:i18n></td>
</tr>
<tr>
    <td>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Promise/PromiseServiceLines/PromiseServiceLine/ShipToAddress/@ZipCode")%>/>
    </td>
</tr>
</table>
<table class="table" ID="SearchTable" width="100%">
	<yfc:loopXML name="ServiceTypes" binding="xml:ServiceTypes:/ServiceTypeList/@ServiceType" id="ServiceType">
		<tr style='display:none'>
			<td class="tablecolumn" ID="HIDE">
		        <input type="hidden" name="ServiceTypes" value='<%=getValue("ServiceType","xml:/ServiceType/@ServiceTypeID")%>' ItemID='<%=getValue("ServiceType","xml:/ServiceType/@ReferenceItemID")%>' Uom='<%=getValue("ServiceType","xml:/ServiceType/@ReferenceItemUOM")%>'
				ItemGroupCode='<%=getValue("ServiceType","xml:/ServiceType/@ItemGroupCode")%>'
				/>
			</td>
		</tr>
    </yfc:loopXML>
</table>
