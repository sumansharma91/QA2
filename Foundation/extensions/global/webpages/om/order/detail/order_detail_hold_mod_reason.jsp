<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/yfcscripts/yfc.js"></script>
<script language="javascript">
	function setModificationReasonAndOverrideFlag(override, modReasonCode, modReasonText)	{

		var oThisReasonCode = document.all("xml:/OrderHold/@ModificationReasonCode");
		var oThisReasonText = document.all("xml:/OrderHold/@ModificationReasonText");

		// if checkbox are not changed... dont call save
		//var obj = document.documentElement
		//if(!yfcHasObjectChanged("INPUT", obj))	{
		//	oThisReasonCode.value='';
		//	oThisReasonText.value=''
		//	alert(YFCMSG008);
		//	return false;
		//}

		setOverrideFlag(override);

		var oReasonCode = document.all(modReasonCode);
		oReasonCode.value=oThisReasonCode.value;

		var oReasonText = document.all(modReasonText);
		oReasonText.value=oThisReasonText.value;

		return (true);
	}
</script>

<table class="view" width="100%">
<tr>
    <td class="detaillabel">
        <yfc:i18n>Reason_Code</yfc:i18n>
    </td>
	<td colspan="2">
		<select name="xml:/OrderHold/@ModificationReasonCode" class="combobox" >
			<yfc:loopOptions binding="xml:ReasonCodeList:/CommonCodeList/@CommonCode" 
                name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
		</select>
    </td>
	<td>
	</td>
</tr> 
<tr>    
    <td class="detaillabel">
        <yfc:i18n>Reason_Text</yfc:i18n>
    </td>
	<td colspan="2">
		<textarea class="unprotectedtextareainput" rows="3" cols="100" <%=yfsGetTextAreaOptions("xml:/OrderHold/@ModificationReasonText", " ", "xml:/Order/AllowedModifications")%>></textarea>
    </td>
	<td>
	</td>
</tr>
</table>
