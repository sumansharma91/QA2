<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="javascript">

function doPopup()
{
    var sVal = document.all("hidMyEntityKey");
    var tmp = '<%=HTMLEncode.htmlEscape(getParameter("hidPrepareEntityKey"))%>';
    var getViewID = '<%=HTMLEncode.htmlEscape(getParameter("viewToRedirectTo"))%>';
    var oError = document.all('YFCDetailError');

    // We will no longer redirect to the plan appointment screen
    //if(tmp == "Y" && oError.HasError == "N")
    //{
	//	yfcShowDetailPopup(getViewID,"", 900, 550,null,'',sVal.value);
    //}
}

function isBoxChecked(baseName) {
    var inputTag = document.all.tags("INPUT");
    for( i=0; i<inputTag.length; i++ ) {
        var tagId = inputTag[i].id;
        if( tagId ) {
            if( tagId.search(baseName) == 0 ) {
                if( ! inputTag[i].disabled && inputTag[i].checked ) {
                    return true;
                }
            }
        }
    }
    return false;
}

function saveButtonOnAddDeliveryClicked(modReasonView,planApptViewID)
{
    if( ! isBoxChecked("chkboxColumn_") ) {
        alert(YFCMSG002);
        return false;
    }
    var tmp = document.all("hidPrepareEntityKey");
	var setViewID= document.all("viewToRedirectTo");

	var itemCountCount = 1;
    var orderLineCount = 1;
	var shldProceed = false;

	var ckBox = document.all('chkboxColumn_' + itemCountCount + "_" + orderLineCount);
	while(ckBox) {
		orderLineCount = 1;
		while(ckBox) {
			if(ckBox.checked)
			{
				shldProceed = true;
				break;
			}
			ckBox = document.all('chkboxColumn_' + itemCountCount + "_" + ++orderLineCount);
		}
		orderLineCount = 1;
		ckBox = document.all('chkboxColumn_' + ++itemCountCount + "_" + orderLineCount);
	}

	if(shldProceed)
	{
		if(enterModificationReason(modReasonView,'xml:/Order/@ModificationReasonCode','xml:/Order/@ModificationReasonText','xml:/Order/@Override') )
		{
			tmp.value = "Y";
			setViewID.value=planApptViewID;
			return true;
		}
	}
	else
	{
		alert(YFCMSG002);
	}
	return false;
}

</script>

<% if(equals(getParameter("hidPrepareEntityKey"),"Y")) {  %>
		<script>
			window.attachEvent("onload",doPopup);
		</script>
<% } %>

<table  class="anchor" cellSpacing="0" cellpadding="7px">
    <yfc:makeXMLInput name="addedDeliveryLineKey">
	    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:AddLine:/OrderLine/@OrderLineKey" /> 
	    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:AddLine:/OrderLine/@OrderHeaderKey" />
	</yfc:makeXMLInput>
		<tr>
			<td>
		        <input type="hidden"  name="hidPrepareEntityKey" value="N"/>
		        <input type="hidden"  name="viewToRedirectTo" />
				<input type="hidden"  name="isError" value="N"/>
		        <input type="hidden" name="hidMyEntityKey" value='<%=getParameter("addedDeliveryLineKey")%>' />
			</td>
		</tr>
		<tr >
				<td>
						<jsp:include  page="/yfc/innerpanel.jsp" flush='true' >
							<jsp:param  name="CurrentInnerPanelID" value="I01" />
							<jsp:param  name="ModifyView" value="true" />
						</jsp:include>
				</td>
		</tr>
		<tr>
	        <td>
				<input type="hidden" name="xml:/OrderLine/@OrderHeaderKey" value='<%=getValue("Order", "xml:/Order/@OrderHeaderKey")%>'/>
	        </td>
		</tr>

<%
//		System.out.println("userHasOverridePermissions() = "+userHasOverridePermissions());
		int totalDeliveryServiceGroupElements = 0;
		String sDeliverySetToShow = getParameter("DeliverySetToShow");
//		System.out.println("sDeliverySetToShow = "+sDeliverySetToShow);
		if (isVoid(sDeliverySetToShow)) 
			sDeliverySetToShow = "1";
	
		ArrayList DeliveryServiceGroupList = getLoopingElementList("xml:OrderDelivery:/Order/@DeliveryServiceGroup"); //looping over DeliveryServiceGroup
		for (int DeliveryServiceGroupCounter = 0; DeliveryServiceGroupCounter < DeliveryServiceGroupList.size(); DeliveryServiceGroupCounter++) 
		{	if ((DeliveryServiceGroupCounter+1)  == Integer.parseInt(sDeliverySetToShow)) {
//				System.out.println("DeliveryServiceGroupCounter = "+DeliveryServiceGroupCounter);	
				YFCElement singleDeliveryServiceGroupElem = (YFCElement) DeliveryServiceGroupList.get(DeliveryServiceGroupCounter);
				request.removeAttribute(YFCUIBackendConsts.YFC_CURRENT_INNER_PANEL_ID);
				request.setAttribute("DeliveryServiceGroup", singleDeliveryServiceGroupElem);
				String panelHeader= new String();
				panelHeader = "Delivery_Request_#" ;
				String panelCounter = "" + (DeliveryServiceGroupCounter+1);
	%>
				<tr >
						<td>
								<jsp:include  page="/yfc/innerpanel.jsp" flush='true' >
									<jsp:param  name="CurrentInnerPanelID" value="I02" />
									<jsp:param name="Title" value='<%=panelHeader%>'/>
									<jsp:param name="TitleQty" value="<%=panelCounter%>" />
								</jsp:include>
								<input type="hidden" name="DeliverySetToShow" value="1" />
						</td>
				</tr>
	<%
			}	
			totalDeliveryServiceGroupElements = DeliveryServiceGroupCounter+1;
		}

		if (totalDeliveryServiceGroupElements > 1) 
		{	%>
				<tr >
					<td>
							<jsp:include  page="/yfc/innerpanel.jsp" flush='true' >
								<jsp:param  name="CurrentInnerPanelID" value="I03" />
								<jsp:param name="DeliverySetAlreadyShown" value='<%=sDeliverySetToShow%>'/>
							</jsp:include>
					</td>
				</tr>
<%	}	%>
</table>
