<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%
	boolean bPS = false;
	boolean bPROD = false;
	boolean bDS = false;
%>
<yfc:loopXML name="InvoiceDetail" binding="xml:/InvoiceDetail/InvoiceHeader/LineDetails/@LineDetail" id="LineDetail">
	<%
		YFCElement eLineDetail = getElement("LineDetail");
		if(eLineDetail != null)	{
			YFCElement eOrderLine = eLineDetail.getChildElement("OrderLine");
			if(eOrderLine != null)	{
				String sItemGrpCode = eOrderLine.getAttribute("ItemGroupCode");
				bPS = bPS || equals("PS", sItemGrpCode);
				bDS = bDS || equals("DS", sItemGrpCode);
				bPROD = bPROD || equals("PROD", sItemGrpCode);
			}
		}
	%>
</yfc:loopXML>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td height="100%" width="25%" addressip="true">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Path" value="xml:/InvoiceDetail/InvoiceHeader/Order/PersonInfoBillTo"/>
            <jsp:param name="DataXML" value="InvoiceDetail"/>
            <jsp:param name="AllowedModValue" value='N'/>
        </jsp:include>
    </td>
    <td height="100%" width="50%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
    <td height="100%" width="25%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I05"/>
        </jsp:include>
    </td>
</tr>

<%	if(bPROD)	{	%>
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
        </jsp:include>
    </td>
</tr>
<%	}
if(bPS)	{	%>
	<tr>
		<td colspan="3" >
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I06"/>
				<jsp:param name="ItemGroupCode" value="PS"/>
			</jsp:include>
		</td>
	</tr>
<%	}		
if(bDS)	{	%>
	<tr>
		<td colspan="3" >
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I07"/>
				<jsp:param name="ItemGroupCode" value="DS"/>
			</jsp:include>
		</td>
	</tr>
<%	}	%>

</table>
