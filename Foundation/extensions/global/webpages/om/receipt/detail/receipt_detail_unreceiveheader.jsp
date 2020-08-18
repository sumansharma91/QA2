<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="javascript">

function CallBeforeUnreceive(){

	if(enterActionModificationReason('YOMD8057','xml:/Receipt/Audit/@ReasonCode','xml:/Receipt/Audit/@ReasonText')){
		return setOverrideFlag('xml:/Receipt/@OverrideModificationRules');
	}else{
		return false;	
	}
}
</script>
<input type="hidden" value='<%=userHasOverridePermissions()%>' name="userHasOverridePermissions" />
<table width="100%" class="view">
<tr>
	<td>
	 <input type="hidden" name="xml:/Receipt/@OverrideModificationRules" value="N"/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Enterprise_Code</yfc:i18n>
    </td>
	<td  class="protectedtext"  nowrap="true">
	     <yfc:getXMLValue binding="xml:/Receipt/@EnterpriseCode"/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Node</yfc:i18n>
    </td>
    <td  class="protectedtext"  nowrap="true">
	     <yfc:getXMLValue binding="xml:/Receipt/@ReceivingNode"/> 
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Receipt_#</yfc:i18n>
    </td>
	<td  class="protectedtext"  nowrap="true">
	     <%if(isVoid(resolveValue("xml:/Receipt/@ReceiptNo"))){%>
			<yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/Receipt/@ReceiptNo"/>
		 <%}else{%>
			<yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"/>
		 <%}%>
		<input type="hidden" <%=getTextOptions("xml:/Receipt/@OverrideModificationRules","","")%> />
	</td>
</tr>
<tr>
	<td class="detaillabel" >
		<yfc:i18n>Location</yfc:i18n>
	</td>
	<td nowrap="true" >
	     <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Receipt/@LocationId","xml:/ReceiptLineList/ReceiptLine/Receipt/@ReceivingDock")%>/> 
		  <img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=' + '<%=resolveValue("xml:/Receipt/@ReceivingNode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Pallet_ID</yfc:i18n>
    </td>
    <td class="protectedtext"  nowrap="true">
	    <yfc:getXMLValue binding="xml:/Receipt/@PalletId"/>
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Case_ID</yfc:i18n>
    </td>
	<td class="protectedtext"  nowrap="true">
	    <yfc:getXMLValue binding="xml:/Receipt/@CaseId"/> 
	</td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Item_ID</yfc:i18n>
    </td>
    <td class="protectedtext"  nowrap="true">
	    <yfc:getXMLValue binding="xml:/Receipt/@ItemID"/>
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Product_Class</yfc:i18n>
    </td>
    <td class="protectedtext"  nowrap="true">
	    <yfc:getXMLValue binding="xml:/Receipt/@ProductClass"/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Unit_Of_Measure</yfc:i18n>
    </td>
    <td class="protectedtext"  nowrap="true">
		<yfc:getXMLValue binding="xml:/Receipt/@UnitOfMeasure"/>
    </td>
</tr>
</table>
