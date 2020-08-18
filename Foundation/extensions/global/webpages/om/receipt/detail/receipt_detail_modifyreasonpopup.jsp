<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreasonpopup.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<table width="100%" class="view">
<%
if(!isVoid(resolveValue("xml:/Receipt/@EnterpriseCode"))){

YFCDocument inDoc = YFCDocument.createDocument("Organization");
YFCElement inElem = inDoc.getDocumentElement();
inElem.setAttribute("OrganizationCode",resolveValue("xml:/Receipt/@EnterpriseCode"));
YFCElement tempElem = YFCDocument.parse("<OrganizationList><Organization InventoryOrganizationCode=\"\" /></OrganizationList>").getDocumentElement();
%>
<yfc:callAPI apiName="getOrganizationList" inputElement="<%=inElem%>" 
	templateElement="<%=tempElem%>" outputNamespace=""/>
<%}
%>
<tr>
        <td>
            <yfc:i18n>Reason_Code</yfc:i18n>
        </td>
      <td>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/ModificationReason/@ReasonCode")%> />
		<img class="lookupicon" name="search" onclick="callListLookup(this,'adjustreasoncode','&xml:/AdjustmentReason/@Node=<%=resolveValue("xml:/Receipt/@ReceivingNode")%>&xml:/AdjustmentReason/AdjustmentReasonDetails/AdjustmentReasonDetail/@EnterpriseCode=<%=resolveValue("xml:/OrganizationList/Organization/@InventoryOrganizationCode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Reason_Code") %> />
    </td>
</tr>
<tr>
        <td>
            <yfc:i18n>Reason_Text</yfc:i18n>
        </td>
        <td>
            <textarea class="unprotectedtextareainput" rows="3" cols="50" <%=getTextAreaOptions("xml:/ModificationReason/@ReasonText")%>></textarea>
        </td>
</tr>
<tr>
        <td></td>
        <td align="right">
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick="setOKClickedAttribute();return false;"/>
            <input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
        <td>
<tr>
</table>
