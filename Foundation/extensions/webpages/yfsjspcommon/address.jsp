<%
/* 
 * Licensed Materials - Property of IBM
 * IBM Sterling Selling and Fulfillment Suite
 * (C) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%
String sFilename="/yfsjspcommon/address_" + getLocale().getCountry()+".jsp";
java.net.URL oURL=pageContext.getServletContext().getResource(sFilename);
%>

<% if (equals("Y", (String) request.getParameter("ShowAnswerSetOptions"))) {
    String answerOptionsBinding = (String) request.getParameter("AnswerSetOptionsBinding");
%>
    <input type="hidden" name="AnswerSetOptionsBinding" value='<%=HTMLEncode.htmlEscape(answerOptionsBinding)%>'/>
<% } %>

<% if (oURL != null) { %>
    <jsp:include page="<%=sFilename%>" flush="true" />
<% } else { %>
    <jsp:include page="/yfsjspcommon/address_default.jsp" flush="true" />
<% } %>