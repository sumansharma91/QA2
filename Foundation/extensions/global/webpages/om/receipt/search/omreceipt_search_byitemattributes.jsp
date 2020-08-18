<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table class="view">
	<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
	  <jsp:param name="ShowDocumentType" value="true"/>
	  <jsp:param name="DocumentTypeBinding" value="xml:/Receipt/Shipment/@DocumentType"/>
	  <jsp:param name="EnterpriseCodeBinding" value="xml:/Receipt/Shipment/@EnterpriseCode"/>
	  <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
	  <jsp:param name="RefreshOnDocumentType" value="true"/>
	</jsp:include>
	<yfc:callAPI apiID="AP2"/>
	<yfc:callAPI apiID="AP3"/>
	<yfc:callAPI apiID="AP4"/>
	<tr> 
		<td>
			 <input type="hidden" <%=getTextOptions("xml:/Receipt/Shipment/@DocumentType", "xml:/CurrentEntity/@DocumentType")%> />
		</td>
	</tr>
<tr> 
	<td class="searchlabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
</tr>
<tr>
	<td class="protectedtext" nowrap="true">
		<% if(isShipNodeUser()) {%>
			<%=getValue("CurrentUser","xml:/User/@Node")%>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceivingNode", "xml:CurrentUser:/User/@Node")%>/>
		<%} else { %>
			<select name="xml:yfcSearchCriteria:/Receipt/@ReceivingNodeQryType" class="combobox" >
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
					name="QueryTypeDesc" value="QueryType" selected="xml:yfcSearchCriteria:/Receipt/@ReceivingNodeQryType"/>
	        </select>
		    <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingNode")%>/>
			<% String extraParams = getExtraParamsForTargetBinding("xml:/ShipNode/Organization/EnterpriseOrgList/OrgEnterprise/@EnterpriseOrganizationKey", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
			<img class="lookupicon" onclick="callLookup(this,'nodelookup','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node") %>/>
		<%}%>
    </td>
</tr>
	<tr>
		<td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
	</tr>
	<tr >
		<td class="searchcriteriacell" nowrap="true">
			   <select class="combobox" <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@ItemIDQryType")%>>
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
					name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/ReceiptLines/ReceiptLine/@ItemIDQryType"/>
			</select>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@ItemID") %> />
			 <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
			<img class="lookupicon" onclick="callItemLookup('xml:/Receipt/ReceiptLines/ReceiptLine/@ItemID','xml:/Receipt/ReceiptLines/ReceiptLine/@ProductClass','xml:/Receipt/ReceiptLines/ReceiptLine/@UnitOfMeasure','item','<%=extraParams%>')"  <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
		</td>
	</tr>
	<tr>
		<td class="searchlabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
	</tr>
	<tr>
		<td class="searchcriteriacell" nowrap="true">
			<select class="combobox" <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@ProductClass")%> >
				<yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
					name="CodeValue" value="CodeValue" selected="xml:/Receipt/ReceiptLines/ReceiptLine/@ProductClass"/>
			</select>
		</td>
	</tr>
	<tr>
		<td  class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
	</tr>
	<tr>
		<td class="searchcriteriacell" nowrap="true">
			<select class="combobox" <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@UnitOfMeasure")%>>
				<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" 
					name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/Receipt/ReceiptLines/ReceiptLine/@UnitOfMeasure"/>
			</select>
		</td>
	</tr>
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Serial_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select class="combobox" <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@SerialNoQryType")%>>
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Receipt/ReceiptLines/ReceiptLine/@SerialNoQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@SerialNo")%>/>
        </td>
    </tr>
		<tr>
        <td class="searchlabel" >
            <yfc:i18n>Disposition_Code</yfc:i18n>
        </td>
    </tr>
	<tr>
		<td class="searchcriteriacell" nowrap="true">
			<select class="combobox" <%=getComboOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@DispositionCode")%>>
				<yfc:loopOptions binding="xml:ReturnDispositionList:/ReturnDispositionList/@ReturnDisposition" 
					name="Description" value="DispositionCode" selected="xml:/Receipt/ReceiptLines/ReceiptLine/@DispositionCode"  isLocalized="Y"/>
			</select>
		</td>
	</tr>
	<tr>
		<td class="searchcriteriacell">
			<input type="checkbox" <%=getCheckBoxOptions("xml:/Receipt/@InspectionPendingFlag","xml:/Receipt/@InspectionPendingFlag", "N")%> yfcCheckedValue='Y' yfcUnCheckedValue='' ><yfc:i18n>Inspection_Pending</yfc:i18n></input>
		</td>
	</tr>
</table>

