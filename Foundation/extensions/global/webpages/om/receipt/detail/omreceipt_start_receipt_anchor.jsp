<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<% 
	String sno=resolveValue("xml:/Shipment/@ShipmentNo");
%>
<% if(!isVoid(sno)){
	
	
	int NumRecs=(int)getNumericValue(getValue("OpenReceipts","xml:/Receipts/@TotalNumberOfRecords"));

	if(NumRecs<=0){
	
	
	%>
	<table class="anchor" cellpadding="7px"  cellSpacing="0" >
	<tr>
		<td colspan="3" valign="top">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
			
		</td>
	</tr>
	</table>
	<%}else{
		

		//check if the page is not getting reloaded  after receipt creation 

		if(!(equals("refresh",resolveValue("xml:/Receipt/@Refresh")))){

		//open receipt already exists for the shipment. cannot start another receipt. show alert.
		%>
		<script language="javascript">
			
			alert(YFCMSG107);//Open Receipt Exists For The Shipment. Cannot Start Another Receipt
			window.close();
		
		</script>


	<%}
  }

}

else{
	String orderno=resolveValue("xml:/Order/@OrderHeaderKey");	
	if (!isVoid(orderno)) {%>
		<table class="anchor" cellpadding="7px"  cellSpacing="0" >
		<tr>
			<td colspan="3" valign="top">
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
					<jsp:param name="CurrentInnerPanelID" value="I03"/>
				</jsp:include>
			</td>
		</tr>
		</table>
<%	} else { %>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="3" valign="top">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
</table>
<%
	}
}%>
