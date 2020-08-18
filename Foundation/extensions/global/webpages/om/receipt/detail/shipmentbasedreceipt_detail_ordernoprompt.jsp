<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@page import="com.yantra.yfs.ui.backend.*"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="javascript">

function myInit()
{
//	getKeyFromParent("TempHeaderKey", "xml:/Order/@ReturnHeaderKey");
	getKeyFromParent("TempHeaderKey", "NewOrderHeaderKey");
	getKeyFromParent("TempBuyer", "NewBuyer");
	getKeyFromParent("TempSeller", "NewSeller");
	getKeyFromParent("TempCurrency", "NewCurrency");
	getKeyFromParent("xml:/Order/@EnterpriseCode", "xml:/Order/@EnterpriseCode");
}

function searchSave() {
	window.close();
}
function okClick(obj) {
	
	var passedObj = new Object();
	var sTmp = document.all("NewOrderHeaderKey");
	passedObj.orderKey = sTmp.value;
	sTmp = document.all("NewBuyer");
	passedObj.buyer = sTmp.value;
	sTmp = document.all("NewSeller");
	passedObj.seller = sTmp.value;
	sTmp = document.all("NewCurrency");
	passedObj.currency = sTmp.value;

	yfcShowDetailPopupWithDynamicKey(obj, "YOMD8004", 'shipmentbasedreceipt','Order',passedObj);
}
</script>

<script language="javascript">
    window.attachEvent("onload", myInit);
</script>


<table class="view" width="100%">
	<tr>
		<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
		<td class="protectedtext">
			<input type="text" class="protectedinput" contenteditable="false" <%=getTextOptions("xml:/Order/@EnterpriseCode","xml:/Receipts/Receipt/@EnterpriseCode")%>  />
			<input type="hidden" class="protectedinput" contenteditable="false" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode","xml:/Receipts/Receipt/@BuyerOrganizationCode")%>  />
			<input type="hidden" class="protectedinput" contenteditable="false" <%=getTextOptions("xml:/Order/@SellerOrganizationCode","xml:/Receipts/Receipt/@SellerOrganizationCode")%>  />
		</td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
		<td>
			<input type="hidden" name="NewOrderHeaderKey" value="" />
			<input type="hidden" name="xml:/Order/@ReturnHeaderKey" value="" />
			<input type="hidden" name="NewBuyer" value="" />
			<input type="hidden" name="NewSeller" value="" />
			<input type="hidden" name="NewCurrency" value="" />
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo","")%> />
			<img class="lookupicon" onclick="callOrderNoLookup('xml:/Order/@OrderNo', 'xml:/Order/@EnterpriseCode','order', 'yfcListViewGroupId=YOML999&yfcSearchViewGroupId=YOMS010')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order")%>/>
		</td>
	</tr>
	</br>
	<tr>
		<td colspan=2 align=right>
			<input class="button" type="button" value='<%=getI18N("Ok")%>' onclick="okClick(this); return false;"/>
			<input class="button" type="button" value='<%=getI18N("Cancel")%>' onclick="window.close();return false;"/>
		</td>
	</tr>
</table>
