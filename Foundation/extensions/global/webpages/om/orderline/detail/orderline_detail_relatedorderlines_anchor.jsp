<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<% setHistoryFlags((YFCElement) request.getAttribute("OrderLine")); %>
<yfc:callAPI apiID='AP1'/> <% /* ChainedOrderLineList */ %>
<yfc:callAPI apiID='AP2'/> <% /* DerivedOrderLineList */ %>

<% prepareRelatedOrderLines((YFCElement) request.getAttribute("OrderLine"),(YFCElement) request.getAttribute("ChainedFromOrderLine"),(YFCElement) request.getAttribute("DerivedFromOrderLine"),(YFCElement) request.getAttribute("ChainedOrderLineList"),(YFCElement) request.getAttribute("DerivedOrderLineList")); %>

<table class="anchor" cellpadding="7px" cellSpacing="0">
	<tr>
	    <td>
	        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
	            <jsp:param name="CurrentInnerPanelID" value="I01"/>
	        </jsp:include>
	    </td>
	</tr>
	
<%  ArrayList docTypeList = getLoopingElementList("xml:/OrderLine/RelatedDocuments/FinalRelatedDocuments/@DocumentType");
    for (int DocumentTypeCounter = 0; DocumentTypeCounter < docTypeList.size(); DocumentTypeCounter++) {
       
       YFCElement singleDocType = (YFCElement) docTypeList.get(DocumentTypeCounter);
       pageContext.setAttribute("DocumentType", singleDocType); 
       
       request.setAttribute("DocumentType", pageContext.getAttribute("DocumentType"));

       setAdditionalAttr(singleDocType, (YFCElement) request.getAttribute("DocumentParamsList"));
       String sDocumentType = getDBString(singleDocType.getAttribute("DocumentTypeDescription")) + " " ; String sLines = "Lines";
       String sDocumentTypeLiteral = singleDocType.getAttribute("DocumentType") + "_Order" ;
       String sIsReturnService = singleDocType.getAttribute("isReturnDocument");
    %>
	<tr>
        <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true">
            <jsp:param name="CurrentInnerPanelID" value="I02"/> 
            <jsp:param name="DocumentTypeCounter" value='<%=String.valueOf(DocumentTypeCounter)%>'/>
            <jsp:param name="Title" value='<%=sDocumentTypeLiteral%>'/>
			<jsp:param name="TitleQty" value="<%=sLines%>" />
            <jsp:param name="IsReturnService" value='<%=sIsReturnService%>'/>
        </jsp:include>
        </td>
    </tr>
<% } %>
</table>
