<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/order.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
function setOrderStateFlags(value1, value2){
	var oOrderComplete = document.all("xml:/Order/@OrderComplete");
	if (oOrderComplete != null)
		oOrderComplete.value = value1;
	var oReadFromHistory = document.all("xml:/Order/@ReadFromHistory");
	if (oReadFromHistory != null)
		oReadFromHistory.value = value2;
}
function refreshSearchScreen(controlObj) {
    if (yfcHasControlChanged(controlObj)) {
        changeSearchView(getCurrentSearchViewId());
	}
}
function setByAddressSearchCriteria(){
    
    var radAddress = document.all("RadAddress");
    var billToKey = document.all("xml:/Order/SearchByAddress/@BillToKey");
    var shipToKey = document.all("xml:/Order/SearchByAddress/@ShipToKey");
    var personInfoKey = document.all("xml:/PersonInfo/@PersonInfoKey");
	var addressValue = "B";
	if(radAddress[1].checked)
		addressValue = "S";
	if(radAddress[2].checked)
		addressValue = "E";

	billToKey.value = "";
	shipToKey.value = "";
	if(personInfoKey.value != null){
		if (addressValue == "B" || addressValue == "E") {
			billToKey.value = personInfoKey.value;
		}
		if (addressValue == "S" || addressValue == "E") {
			shipToKey.value = personInfoKey.value;
		}
	}
}
</script>

<%
	// Values for radiobinding
	// open : OrderComplete="N" ReadFromHistory="N" radiobinding="open"
	// recent : OrderComplete=" " ReadFromHistory="N" radiobinding="recent"
	// history : OrderComplete=" " ReadFromHistory="Y" radiobinding="history"
	// all : OrderComplete=" " ReadFromHistory="B" radiobinding="all"

	// default is open : OrderComplete="N" ReadFromHistory="N" radiobinding="open"

	String bReadFromHistory = resolveValue("xml:/Order/@ReadFromHistory");
	String sOrderComplete = resolveValue("xml:/Order/@OrderComplete");

	String radiobinding = "open";
	if(isVoid(sOrderComplete) && equals(bReadFromHistory, "N")){
		radiobinding = "recent";
	}
	else if(isVoid(sOrderComplete) && equals(bReadFromHistory, "Y")){
		radiobinding = "history";
	}
	else if(isVoid(sOrderComplete) && equals(bReadFromHistory, "B")){
		radiobinding = "all";
    }
	else{
		radiobinding = "open";
		YFCElement orderElem = (YFCElement)request.getAttribute("Order");
		if(orderElem == null){
			orderElem = YFCDocument.createDocument("Order").getDocumentElement();
		}
		orderElem.setAttribute("OrderComplete", "N");
		orderElem.setAttribute("ReadFromHistory", "N");
		bReadFromHistory = "N";
		sOrderComplete = "N";
	}
	
	//processing for the address lookup and address keys
	
	String billToKey = resolveValue("xml:/Order/SearchByAddress/@BillToKey");
	String shipToKey = resolveValue("xml:/Order/SearchByAddress/@ShipToKey");
	String radAddress = "";

	String personInfoKey ="";
	if(!isVoid(billToKey) && !isVoid(shipToKey)){
		radAddress = "E";
		personInfoKey = billToKey; 	
	}
	else if(!isVoid(billToKey)){
		radAddress = "B";
		personInfoKey = billToKey; 	
	}
	else if(!isVoid(shipToKey)){
		radAddress = "S";
		personInfoKey = shipToKey; 	
	}

	//set the address radiobuttons
	if(isVoid(radAddress)){
		radAddress = "E";
	}
	
	YFCElement personInfoElement = (YFCElement)request.getAttribute("PersonInfo");
	if(personInfoElement == null){
		personInfoElement = YFCDocument.createDocument("PersonInfo").getDocumentElement();
		request.setAttribute("PersonInfo",personInfoElement);
	}
	personInfoElement.setAttribute("PersonInfoKey", personInfoKey);
%>
<table class="view">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@StatusQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N"/>
            <input type="hidden" <%=getTextOptions("xml:/PersonInfo/@PersonInfoKey", personInfoKey)%>/>
            <input type="hidden" name="xml:/Order/SearchByAddress/@BillToKey" value="<%=billToKey%>"/>
            <input type="hidden" name="xml:/Order/SearchByAddress/@ShipToKey" value="<%=shipToKey%>"/>
        </td>
    </tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
    </jsp:include>
    <% 
		if(!isVoid(personInfoKey)){
	%>
			<yfc:callAPI apiID="AP2"/>
    <%
		}
    %>
	<yfc:callAPI apiID="AP3"/>

    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@BuyerOrganizationCodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@BuyerOrganizationCodeQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode")%>/>
			<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
            <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Seller</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@SellerOrganizationCodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@SellerOrganizationCodeQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode")%>/>
            <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization") %> />
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true" >
            <select name="xml:/Order/OrderLine/Item/@ItemIDQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/OrderLine/Item/@ItemIDQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLine/Item/@ItemID")%>/>
            <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
            <img class="lookupicon" name="search" 
			onclick="callItemLookup('xml:/Order/OrderLine/Item/@ItemID','xml:/Order/OrderLine/Item/@ProductClass','xml:/Order/OrderLine/@OrderingUOM',
			'item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
        </td>
    </tr>
	<tr>
		<td class="searchlabel">
			<fieldset>
				<legend class="searchlabel"><yfc:i18n>Address</yfc:i18n>
					<img class="lookupicon" onclick="callAddressLookup('xml:/PersonInfo/@PersonInfoKey','addresslookup','');setByAddressSearchCriteria();refreshSearchScreen(document.all('xml:/PersonInfo/@PersonInfoKey'))" <%=getImageOptions(YFSUIBackendConsts.ADDRESS_DETAILS, "Search_for_Address") %> />
				</legend>
			<span class="protectedtext">
			<%			
			if(!isVoid(personInfoKey)){
			%>
			<% //display the address details %>
				<jsp:include page="/yfsjspcommon/address.jsp" flush="true">
					<jsp:param name="Path" value="xml:/PersonInfoList/PersonInfo"/>
					<jsp:param name="DataXML" value="PersonInfoList"/>
		        </jsp:include>
			</span>
			<% }else{ %>
			<br/>
			<%}%>
			<br/>
            <input type="radio" onclick="setByAddressSearchCriteria()" <%=getRadioOptions("RadAddress", radAddress, "B")%>>
                <yfc:i18n>Bill_To</yfc:i18n>
            </input>
            <input type="radio" onclick="setByAddressSearchCriteria()" <%=getRadioOptions("RadAddress", radAddress, "S")%>>
                <yfc:i18n>Ship_To</yfc:i18n>
            </input>
            <input type="radio" onclick="setByAddressSearchCriteria()" <%=getRadioOptions("RadAddress", radAddress, "E")%>>
                <yfc:i18n>Either</yfc:i18n>
            </input>
			</fieldset>
		</td>
	</tr>
    <tr>
        <td  class="searchlabel">
			<yfc:i18n>CarrierService</yfc:i18n>
		</td>
	</tr>
    <tr>
        <td class="searchcriteriacell">
            <select class="combobox" name="xml:/Order/OrderLine/@CarrierServiceCode">
                <yfc:loopOptions binding="xml:/CarrierServiceList/@CarrierService" 
                    name="CarrierServiceDesc" value="CarrierServiceCode" selected="xml:/Order/OrderLine/@CarrierServiceCode" isLocalized="Y"/>
            </select>
        </td>
    </tr>
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_State</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
			<input type="radio" onclick="setOrderStateFlags('N','N')" <%=getRadioOptions("xml:/Order/@Radiobinding", radiobinding, "open")%>><yfc:i18n>Open</yfc:i18n> 
			<input type="radio" onclick="setOrderStateFlags(' ','N')"  <%=getRadioOptions("xml:/Order/@Radiobinding", radiobinding, "recent")%>><yfc:i18n>Recent</yfc:i18n><!-- The use of 'NO' is done intentionally, getOrderList API returns history orders only if ReadFromHistory =='Y'  -->
			<input type="radio" onclick="setOrderStateFlags(' ','Y')" <%=getRadioOptions("xml:/Order/@Radiobinding", radiobinding, "history")%>><yfc:i18n>History</yfc:i18n>
            <input type="radio" onclick="setOrderStateFlags(' ','B')" <%=getRadioOptions("xml:/Order/@Radiobinding", radiobinding, "all")%>><yfc:i18n>All</yfc:i18n>
			<input type="hidden" name="xml:/Order/@OrderComplete" value="<%=sOrderComplete%>"/>
            <input type="hidden" name="xml:/Order/@ReadFromHistory" value="<%=bReadFromHistory%>"/>
        </td>
    </tr>
	<tr>
		<td class="searchlabel">
			<yfc:i18n>Selecting_All_may_be_slow</yfc:i18n>
		</td>
	</tr>
</table>
