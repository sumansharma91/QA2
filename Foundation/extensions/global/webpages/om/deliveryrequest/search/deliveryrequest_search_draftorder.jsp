<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@page import="com.yantra.shared.inv.INVConstants"%>

<jsp:include page="/om/orderline/search/orderline_search_byparticipant.jsp" flush="true">
    <jsp:param name="DraftOrderFlag" value="Y"/>
    <jsp:param name="ItemGroupCode" value="<%=INVConstants.ITEM_GROUP_CODE_DELIVERY%>"/>
</jsp:include>
