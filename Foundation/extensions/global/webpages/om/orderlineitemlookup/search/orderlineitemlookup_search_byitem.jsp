<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script>
	function alertPrimeAndSublineNo(txtBox) {
		var primeLineNo = document.all("xml:/OrderLineStatus/@PrimeLineNo");
		var subLineNo = document.all("xml:/OrderLineStatus/@SubLineNo");
		if (txtBox.value != "") {
			if (txtBox == primeLineNo && subLineNo.value == "") {
				alert(YFCMSG162);
			} else if (txtBox == subLineNo && primeLineNo.value == "") {
				alert(YFCMSG163);
			}
		} else {
			if (txtBox == primeLineNo && subLineNo.value != "") {
				alert(YFCMSG163);
			} else if (txtBox == subLineNo && primeLineNo.value != "") {
				alert(YFCMSG162);
			}
		}
	}
</script>
<%	String defaultEnterpriseBinding = resolveValue("xml:/OrderLineStatus/@EnterpriseCode");	
	String defaultDocumentType = resolveValue("xml:/OrderLineStatus/@DocumentType");	
	String defaultOrderHeaderKey = resolveValue("xml:/OrderLineStatus/@OrderHeaderKey");	
	String defaultOrderNo = resolveValue("xml:/OrderLineStatus/@OrderNo");%>

<table class="view">
    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="ShowDocumentType" value="false"/>
		<jsp:param name="EnterpriseCodeBinding" value="xml:/OrderLineStatus/@EnterpriseCode"/>
        <jsp:param name="AcrossEnterprisesAllowed" value="false"/>
		<jsp:param name="DefaultEnterpriseBinding" value="<%=defaultEnterpriseBinding%>"/>
		<jsp:param name="DisableEnterprise" value="Y"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="protectedtext">
			<%=defaultOrderNo%>
            <input type="hidden" class="protectedtext" <%=getTextOptions("xml:/OrderLineStatus/@OrderNo","xml:/OrderLineStatus/@OrderNo",defaultOrderNo)%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Prime_Line_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <select name="xml:/OrderLineStatus/@PrimeLineNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/OrderLineStatus/@PrimeLineNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" onblur="alertPrimeAndSublineNo(this)" <%=getTextOptions("xml:/OrderLineStatus/@PrimeLineNo")%>/>
        </td>
    </tr>
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Release_#</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td class="searchcriteriacell">
            <select name="xml:/OrderLineStatus/@ReleaseNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/OrderLineStatus/@ReleaseNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLineStatus/@ReleaseNo")%>/>
		</td>
	</tr>
	<tr>
		<td  class="searchlabel" >
			<yfc:i18n>Sub_Line_#</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td class="searchcriteriacell" nowrap="true">
            <select name="xml:/OrderLineStatus/@SubLineNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/OrderLineStatus/@SubLineNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" onblur="alertPrimeAndSublineNo(this)" <%=getTextOptions("xml:/OrderLineStatus/@SubLineNo")%>/>
		</td>
	</tr>
	<input type="hidden" name="xml:/OrderLineStatus/@DocumentType" value="<%=defaultDocumentType%>" />
<!--	<input type="hidden" name="xml:/OrderLineStatus/@TransactionId" value="INCLUDE_SHIPMENT.<%=defaultDocumentType%>" /> -->
	<input type="hidden" name="xml:/OrderLineStatus/@TransactionId" value="INCLUDE_SHIPMENT" />
	<input type="hidden" name="xml:/OrderLineStatus/@OrderHeaderKey" value="<%=defaultOrderHeaderKey%>" />
</table>
