<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<table class="anchor" cellpadding="7px" width="100%">
    <tr>
        <td>
            <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                <jsp:param name="CurrentInnerPanelID" value="I01"/>
            </jsp:include>
        </td>
    </tr>
    <%  ArrayList negItemList = getLoopingElementList("xml:/Negotiations/Negotiation/NegotiationItems/@NegotiationItem");
        for (int NegotiationItemCounter = 0; NegotiationItemCounter < negItemList.size(); NegotiationItemCounter++) {
           
           YFCElement singleNegItem = (YFCElement) negItemList.get(NegotiationItemCounter);
           pageContext.setAttribute("NegotiationItem", singleNegItem); %>
       
        <% request.setAttribute("NegotiationItem", pageContext.getAttribute("NegotiationItem")); %>
        <tr>
            <td>
                <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                    <jsp:param name="CurrentInnerPanelID" value="I02"/>
                    <jsp:param name="NegotiationItemIndex" value="<%=String.valueOf(NegotiationItemCounter)%>"/>
                </jsp:include>
            </td>
        </tr>
    <% } %>
    <tr>
        <td>
            <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
                <jsp:param name="CurrentInnerPanelID" value="I03"/>
            </jsp:include>
        </td>
    </tr>
</table>
