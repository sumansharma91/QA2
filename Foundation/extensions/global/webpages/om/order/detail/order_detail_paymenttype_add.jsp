<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@page  import="com.yantra.yfc.ui.backend.util.*" %>
<%@ include file="/console/jsp/paymenttokenizerutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="javascript">
	function showBillToAddressPanel(ckBox)	{
	    var editableAddressPanel = document.all("editablePaymentAddress");
	    var staticAddressPanel = document.all("staticPaymentAddress");
		if(ckBox.checked)	{
			editableAddressPanel.style.display = 'inline';
			staticAddressPanel.style.display = 'none';
			resetName("Y");
		}	else	{
			editableAddressPanel.style.display = 'none';
			staticAddressPanel.style.display = 'inline';
			resetName("N");
		}
	}

	function resetName(setAddressAtPaymentLevel)	{
	    var editableAddressPanel = document.all("editablePaymentAddress");

		var oCells = editableAddressPanel.getElementsByTagName("INPUT");
		for ( var i = 0; i < oCells.length; i++ ) {
			var oInput = oCells[i];
			if(setAddressAtPaymentLevel == "Y")	{
				if(oInput.name == "null"){
					oInput.name = oInput.origName;
				}
			}	else	{
				if(oInput.name != "null"){
					oInput.origName = oInput.name;
					oInput.name = "null";
				}
			}
		}
	}

	function disableAddressPanel()	{
		var staticAddressPanel = document.all("staticPaymentAddress");

		var controls = staticAddressPanel.getElementsByTagName("INPUT");
		disableCtrs(controls);

		var select = staticAddressPanel.getElementsByTagName("SELECT");
		disableCtrs(select);
	}

	function disableCtrs(ctrls)	{
		for ( var i = 0; i < ctrls.length ; i++ ) {
			ctrl = ctrls.item(i);
			ctrl.disabled="true";
		}
	}


</script>

<script language="javascript">
	window.attachEvent("onload", disableAddressPanel);
</script>

<%
boolean tokenizeCC = isCreditCardNoTokenized();
String ccerror = validateTokenizerURL("CREDIT_CARD", tokenizeCC,"CREDIT_CARD");
boolean tokenizeSVC = isStoreValueCardNoTokenized();
String svcerror = validateTokenizerURL("STORED_VALUE_CARD", tokenizeSVC, "STORED_VALUE_CARD");
%>

<table class="view" cellspacing="0" cellpadding="0" border="0" width="100%" cols="6">
<tr>        
    <td valign="top">
		<input type="hidden" id="callcreatetokencc" name="xml:/Order/PaymentMethods/PaymentMethod/@callcreatetokencc"/>
		<input type="hidden" id="callcreatetokensvc" name="xml:/Order/PaymentMethods/PaymentMethod/@callcreatetokensvc"/>
        <input type="hidden" name="xml:/Order/PaymentMethods/PaymentMethod/@PaymentTypeGroup"/>
        <input type="hidden" name="xml:/Order/@ModificationReasonCode"/>
        <input type="hidden" name="xml:/Order/@ModificationReasonText"/>
		<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
		<input type="hidden" name="xml:/Order/@Override" value="N"/>
		<input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("Order", "xml:/Order/@DraftOrderFlag")%>'/>
        <table  class="view" width="100%">
	    <tr>
	        <td class="detaillabel" ><yfc:i18n>Payment_Type</yfc:i18n></td>
	        <td>
	            <select <%=yfsGetComboOptions("xml:/Order/PaymentMethods/PaymentMethod/@PaymentType","","xml:/Order/AllowedModifications")%> onchange="selectCard(this.value,this);setUnlimitedCharges(this.value);resetName('N');setPaymentTypeGroup(this.value,<%=ccerror%>,<%=svcerror%>)">
		                <yfc:loopOptions binding="xml:AddPaymentTypeList:/PaymentTypeList/@PaymentType" name="PaymentTypeDescription"
		                value="PaymentType" isLocalized="Y"/>
	            </select>
	        </td>
	     </tr>
         
		 <tbody  <%=getCurrentStyle("CREDIT_CARD", "Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentType", getValue("Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentTypeGroup"))%> >
	        <jsp:include page="/om/order/detail/order_detail_paymenttype_creditcard.jsp" flush="true">
                    <jsp:param name="Incomplete" value='Y'/>
                    <jsp:param name="CreditCardEncrypted" value='N'/>
                    <jsp:param name="CreditCardAddnlEncrypted" value='N'/>
					<jsp:param name="PaymentTypeBinding" value="xml:/Order/PaymentMethods/PaymentMethod/@PaymentTypeGroup"/>
                    <jsp:param name="ModifyFlag" value='Y'/>
					 <jsp:param name="paymentservererrorforadd" value='<%=ccerror%>'/>
			</jsp:include>
			<tr>
				<td colspan="4">
					<input class="checkbox" type="checkbox" onclick="showBillToAddressPanel(this);" <%=getCheckBoxOptions("xml:/Order/PaymentMethods/PaymentMethod/@OverrideBillToKey","","Y")%>/>
					<yfc:i18n>Override_BillTo_For_This_Payment</yfc:i18n>
				</td>
			</tr>  
	     </tbody>

         <tbody <%=getCurrentStyle("CUSTOMER_ACCOUNT", "Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentType",getValue("Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentTypeGroup"))%> >
	        <jsp:include page="/om/order/detail/order_detail_paymenttype_account.jsp" flush="true">
                    <jsp:param name="Incomplete" value='Y'/>
                    <jsp:param name="CustomerAccountEncrypted" value='N'/>
                </jsp:include>
	     </tbody>
         <tbody <%=getCurrentStyle("CHECK", "Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentType",getValue("Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentTypeGroup"))%> >
	        <jsp:include page="/om/order/detail/order_detail_paymenttype_check.jsp" flush="true">
                    <jsp:param name="Incomplete" value='Y'/>
                    <jsp:param name="OtherPaymentTypeEncrypted" value='N'/>
                </jsp:include>
	     </tbody>
		 <tbody <%=getCurrentStyle("STORED_VALUE_CARD", "Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentType",getValue("Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentTypeGroup"))%> >
	        <jsp:include page="/om/order/detail/order_detail_paymenttype_svc.jsp" flush="true">
                    <jsp:param name="Incomplete" value='Y'/>
                    <jsp:param name="StoredValueCardEncrypted" value='N'/>
					<jsp:param name="PaymentTypeBinding" value="xml:/Order/PaymentMethods/PaymentMethod/@PaymentTypeGroup"/>
                    <jsp:param name="ModifyFlag" value='Y'/>
					 <jsp:param name="paymentservererrorforadd" value='<%=svcerror%>'/>
            </jsp:include>
	     </tbody>
         <tbody <%=getCurrentStyle("OTHER", "Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentType",getValue("Order", "xml:/Order/PaymentMethods/PaymentMethod/@PaymentType"))%> isDefault="Y">
	        <jsp:include page="/om/order/detail/order_detail_paymenttype_check.jsp" flush="true">
                    <jsp:param name="Incomplete" value='Y'/>
                    <jsp:param name="OtherPaymentTypeEncrypted" value='N'/>
                </jsp:include>
	     </tbody>
         </table>
    </td>
    <td valign="top">
        <table class="view" width="100%">
            <tr>
                <td class="detaillabel" ><yfc:i18n>Charge_Sequence</yfc:i18n></td>
                <td>
                    <input type="text" <%=yfsGetTextOptions("xml:/Order/PaymentMethods/PaymentMethod/@ChargeSequence","xml:/Order/AllowedModifications")%>/>
                </td>
            </tr>
            <tr>
                <td class="detaillabel" ><yfc:i18n>Unlimited_Charges</yfc:i18n></td>
                <td>
                    <input class="checkbox" type="checkbox" <%=yfsGetCheckBoxOptions("xml:/Order/PaymentMethods/PaymentMethod/@UnlimitedCharges","","Y","xml:/Order/AllowedModifications")%>/>
                </td>
            </tr>  
            <tr>
                <td class="detaillabel" ><yfc:i18n>Max_Charge_Limit</yfc:i18n></td>
                <td>
                    <input type="text" <%=yfsGetTextOptions("xml:/Order/PaymentMethods/PaymentMethod/@MaxChargeLimit","xml:/Order/AllowedModifications")%>/>
                </td>
            </tr>
        </table>
    </td>
</tr>

<tr>
	<%
		request.removeAttribute(YFCUIBackendConsts.YFC_CURRENT_INNER_PANEL_ID);
	%>

	<table class="view" width="100%" cellSpacing="4">
		<tr id="editablePaymentAddress" style='display:none'>
		    <td height="100%" width="100%" addressip="true">
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
					<jsp:param name="CurrentInnerPanelID" value="I02"/>
					<jsp:param name="Path" value="xml:/Order/PaymentMethods/PaymentMethod/PersonInfoBillTo"/>
					<jsp:param name="DataXML" value="Order"/>
					<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("BillToAddress", "xml:/Order/AllowedModifications")%>'/>
				</jsp:include>
			</td>
		</tr>
		<tr id="staticPaymentAddress" style='display:none'>
            <input type="hidden" value='N' yName="AllowedModValue"/>
		    <td height="100%" width="100%" addressip="true">
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
					<jsp:param name="CurrentInnerPanelID" value="I02"/>
					<jsp:param name="Path" value="xml:/Order/PersonInfoBillTo"/>
					<jsp:param name="DataXML" value="Order"/>
					<jsp:param name="AllowedModValue" value='N'/>
				</jsp:include>
			</td>
		</tr>
	</table>
</tr>

</table>

<table class="table" ID="SearchTable" width="100%">
	<yfc:loopXML name="PaymentTypeList" binding="xml:/PaymentTypeList/@PaymentType" id="PaymentType">
		<tr style='display:none'>
			<td class="tablecolumn" ID="HIDE">
		        <input type="hidden" name="PaymentTypeList" value='<%=resolveValue("xml:/PaymentType/@PaymentType")%>' groupvalue='<%=resolveValue("xml:/PaymentType/@PaymentTypeGroup")%>' chargeuptovalue='<%=resolveValue("xml:/PaymentType/@ChargeUpToAvailable")%>'/>
			</td>
		</tr>
    </yfc:loopXML>
</table>
