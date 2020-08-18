<%
/* 
 * Licensed Materials - Property of IBM
 * IBM Sterling Selling and Fulfillment Suite
 * (C) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.ui.backend.util.HTMLEncode" %>

<%
    String xmlName = getParameter("DataXML");
    if (isVoid(xmlName)) {
        xmlName = "EntityDetail";
    }
    
    String outputPath = getParameter("OutputPath");
    if (isVoid(outputPath)) {
        outputPath = getParameter("Path");
    }
	outputPath = HTMLEncode.htmlEscape(outputPath);

    String allowedModValue = getParameter("AllowedModValue");
    if (isVoid(allowedModValue)) {
        allowedModValue = "false";
    }

    String addressTypePath = getParameter("AddressTypePath");
    if (!isVoid(addressTypePath)) {
        String outputAddressTypePath = getParameter("OutputAddressTypePath");
        if (isVoid(outputAddressTypePath)) {
            outputAddressTypePath = getParameter("AddressTypePath");
        }
        String addressType = getValueNoNull(xmlName, getParameter("AddressTypePath")+"@AddressType");
        %>
        <input type="hidden" yName="<%=outputAddressTypePath%>@AddressType" class="protectedtext" contentEditable=false value="<%=addressType%>">
        <%
    }
    String displayPhoneNo = getParameter("DisplayPhoneNo");     
    String company = getValueNoNull(xmlName, getParameter("Path")+"@Company");
    String firstName = getValueNoNull(xmlName, getParameter("Path")+"@FirstName");
    String middleName = getValueNoNull(xmlName, getParameter("Path")+"@MiddleName");
    String lastName = getValueNoNull(xmlName, getParameter("Path")+"@LastName");
    String addressLine1 = getValueNoNull(xmlName, getParameter("Path")+"@AddressLine1");
    String addressLine2 = getValueNoNull(xmlName, getParameter("Path")+"@AddressLine2");
    String addressLine3 = getValueNoNull(xmlName, getParameter("Path")+"@AddressLine3");
    String addressLine4 = getValueNoNull(xmlName, getParameter("Path")+"@AddressLine4");
    String addressLine5 = getValueNoNull(xmlName, getParameter("Path")+"@AddressLine5");
    String addressLine6 = getValueNoNull(xmlName, getParameter("Path")+"@AddressLine6");

    String city = getValueNoNull(xmlName, getParameter("Path")+"@City");
    String state = getValueNoNull(xmlName, getParameter("Path")+"@State");
    String postalCode = getValueNoNull(xmlName, getParameter("Path")+"@ZipCode");
    String country = getValueNoNull(xmlName, getParameter("Path")+"@Country");
    String dayphone = getValueNoNull(xmlName, getParameter("Path")+"@DayPhone");
    String eveningphone = getValueNoNull(xmlName, getParameter("Path")+"@EveningPhone");
    String mobilephone = getValueNoNull(xmlName, getParameter("Path")+"@MobilePhone");
    String fax = getValueNoNull(xmlName, getParameter("Path")+"@DayFaxNo");
    String email = getValueNoNull(xmlName, getParameter("Path")+"@EMailID");
    
    String alternateEmailID = getValueNoNull(xmlName, getParameter("Path")+"@AlternateEmailID");
    String beeper = getValueNoNull(xmlName, getParameter("Path")+"@Beeper");
    String department = getValueNoNull(xmlName, getParameter("Path")+"@Department");
    String eveningFaxNo = getValueNoNull(xmlName, getParameter("Path")+"@EveningFaxNo");
    String jobTitle = getValueNoNull(xmlName, getParameter("Path")+"@JobTitle");
    String otherPhone = getValueNoNull(xmlName, getParameter("Path")+"@OtherPhone");
    String suffix = getValueNoNull(xmlName, getParameter("Path")+"@Suffix");
    String title = getValueNoNull(xmlName, getParameter("Path")+"@Title");

    String errortxt = getValueNoNull(xmlName, request.getParameter("Path")+"@ErrorTxt");
    String httpurl = getValueNoNull(xmlName, request.getParameter("Path")+"@HttpUrl");
    String personid = getValueNoNull(xmlName, request.getParameter("Path")+"@PersonID");
    String prefershipaddr = getValueNoNull(xmlName, request.getParameter("Path")+"@PreferredShipAddress");
    String usecount = getValueNoNull(xmlName, request.getParameter("Path")+"@UseCount");
    String verificationstatus = getValueNoNull(xmlName, request.getParameter("Path")+"@VerificationStatus");
	String style = getParameter("style");
	if(!isVoid(style) )	{
		style = "style='"+style+"'";
	}

%>
    <input type="hidden" yName="xml:/AddressTemp/@AllowedModValue" value="<%=HTMLEncode.htmlEscape(allowedModValue)%>"/>
    <input type="hidden" yName="<%=outputPath%>@Company" class="protectedtext" contentEditable=false value="<%=company%>">
    <input type="hidden" yName="<%=outputPath%>@FirstName" class="protectedtext" contentEditable=false value="<%=firstName%>">
    <input type="hidden" yName="<%=outputPath%>@MiddleName" class="protectedtext" contentEditable=false value="<%=middleName%>">
    <input type="hidden" yName="<%=outputPath%>@LastName" class="protectedtext" contentEditable=false value="<%=lastName%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine1" class="protectedtext" contentEditable=false value="<%=addressLine1%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine2" class="protectedtext" contentEditable=false value="<%=addressLine2%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine3" class="protectedtext" contentEditable=false value="<%=addressLine3%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine4" class="protectedtext" contentEditable=false value="<%=addressLine4%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine5" class="protectedtext" contentEditable=false value="<%=addressLine5%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine6" class="protectedtext" contentEditable=false value="<%=addressLine6%>">
    <input type="hidden" yName="<%=outputPath%>@City" class="protectedtext" contentEditable=false value="<%=city%>">
    <input type="hidden" yName="<%=outputPath%>@State" class="protectedtext" contentEditable=false value="<%=state%>">
    <input type="hidden" yName="<%=outputPath%>@ZipCode" class="protectedtext" contentEditable=false value="<%=postalCode%>">
    <input type="hidden" yName="<%=outputPath%>@Country" class="protectedtext" contentEditable=false value="<%=country%>">
    <input type="hidden" yName="<%=outputPath%>@DayPhone" class="protectedtext" contentEditable=false value="<%=dayphone%>">
    <input type="hidden" yName="<%=outputPath%>@EveningPhone" class="protectedtext" contentEditable=false value="<%=eveningphone%>">
    <input type="hidden" yName="<%=outputPath%>@MobilePhone" class="protectedtext" contentEditable=false value="<%=mobilephone%>">
    <input type="hidden" yName="<%=outputPath%>@DayFaxNo" class="protectedtext" contentEditable=false value="<%=fax%>">
    <input type="hidden" yName="<%=outputPath%>@EMailID" class="protectedtext" contentEditable=false value="<%=email%>">
    <input type="hidden" yName="<%=outputPath%>@AlternateEmailID" class="protectedtext" contentEditable=false value="<%=alternateEmailID%>">
    <input type="hidden" yName="<%=outputPath%>@Beeper" class="protectedtext" contentEditable=false value="<%=beeper%>">
    <input type="hidden" yName="<%=outputPath%>@Department" class="protectedtext" contentEditable=false value="<%=department%>">
    <input type="hidden" yName="<%=outputPath%>@EveningFaxNo" class="protectedtext" contentEditable=false value="<%=eveningFaxNo%>">
    <input type="hidden" yName="<%=outputPath%>@JobTitle" class="protectedtext" contentEditable=false value="<%=jobTitle%>">
    <input type="hidden" yName="<%=outputPath%>@OtherPhone" class="protectedtext" contentEditable=false value="<%=otherPhone%>">
    <input type="hidden" yName="<%=outputPath%>@Suffix" class="protectedtext" contentEditable=false value="<%=suffix%>">
    <input type="hidden" yName="<%=outputPath%>@Title" class="protectedtext" contentEditable=false value="<%=title%>">
    <input type="hidden" yName="<%=outputPath%>@ErrorTxt" class="protectedtext" contentEditable=false value="<%=errortxt%>">
    <input type="hidden" yName="<%=outputPath%>@HttpUrl" class="protectedtext" contentEditable=false value="<%=httpurl%>">
    <input type="hidden" yName="<%=outputPath%>@PersonID" class="protectedtext" contentEditable=false value="<%=personid%>">
    <input type="hidden" yName="<%=outputPath%>@PreferredShipAddress" class="protectedtext" contentEditable=false value="<%=prefershipaddr%>">
    <input type="hidden" yName="<%=outputPath%>@UseCount" class="protectedtext" contentEditable=false value="<%=usecount%>">
    <input type="hidden" yName="<%=outputPath%>@VerificationStatus" class="protectedtext" contentEditable=false value="<%=verificationstatus%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine1" class="protectedtext" contentEditable=false value="<%=addressLine1%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine2" class="protectedtext" contentEditable=false value="<%=addressLine2%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine3" class="protectedtext" contentEditable=false value="<%=addressLine3%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine4" class="protectedtext" contentEditable=false value="<%=addressLine4%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine5" class="protectedtext" contentEditable=false value="<%=addressLine5%>">
    <input type="hidden" yName="<%=outputPath%>@AddressLine6" class="protectedtext" contentEditable=false value="<%=addressLine6%>">
    <input type="hidden" yName="<%=outputPath%>@City" class="protectedtext" contentEditable=false value="<%=city%>">
    <input type="hidden" yName="<%=outputPath%>@State" class="protectedtext" contentEditable=false value="<%=state%>">
    <input type="hidden" yName="<%=outputPath%>@ZipCode" class="protectedtext" contentEditable=false value="<%=postalCode%>">
    <input type="hidden" yName="<%=outputPath%>@Country" class="protectedtext" contentEditable=false value="<%=country%>">
    <input type="hidden" yName="<%=outputPath%>@DayPhone" class="protectedtext" contentEditable=false value="<%=dayphone%>">
    <input type="hidden" yName="<%=outputPath%>@EveningPhone" class="protectedtext" contentEditable=false value="<%=eveningphone%>">
    <input type="hidden" yName="<%=outputPath%>@MobilePhone" class="protectedtext" contentEditable=false value="<%=mobilephone%>">
    <input type="hidden" yName="<%=outputPath%>@DayFaxNo" class="protectedtext" contentEditable=false value="<%=fax%>">
    <input type="hidden" yName="<%=outputPath%>@EMailID" class="protectedtext" contentEditable=false value="<%=email%>">
    <input type="hidden" yName="<%=outputPath%>@AlternateEmailID" class="protectedtext" contentEditable=false value="<%=alternateEmailID%>">
    <input type="hidden" yName="<%=outputPath%>@Beeper" class="protectedtext" contentEditable=false value="<%=beeper%>">
    <input type="hidden" yName="<%=outputPath%>@Department" class="protectedtext" contentEditable=false value="<%=department%>">
    <input type="hidden" yName="<%=outputPath%>@EveningFaxNo" class="protectedtext" contentEditable=false value="<%=eveningFaxNo%>">
    <input type="hidden" yName="<%=outputPath%>@JobTitle" class="protectedtext" contentEditable=false value="<%=jobTitle%>">
    <input type="hidden" yName="<%=outputPath%>@OtherPhone" class="protectedtext" contentEditable=false value="<%=otherPhone%>">
    <input type="hidden" yName="<%=outputPath%>@Suffix" class="protectedtext" contentEditable=false value="<%=suffix%>">
    <input type="hidden" yName="<%=outputPath%>@Title" class="protectedtext" contentEditable=false value="<%=title%>">
    <input type="hidden" yName="<%=outputPath%>@ErrorTxt" class="protectedtext" contentEditable=false value="<%=errortxt%>">
    <input type="hidden" yName="<%=outputPath%>@HttpUrl" class="protectedtext" contentEditable=false value="<%=httpurl%>">
    <input type="hidden" yName="<%=outputPath%>@PersonID" class="protectedtext" contentEditable=false value="<%=personid%>">
    <input type="hidden" yName="<%=outputPath%>@PreferredShipAddress" class="protectedtext" contentEditable=false value="<%=prefershipaddr%>">
    <input type="hidden" yName="<%=outputPath%>@UseCount" class="protectedtext" contentEditable=false value="<%=usecount%>">
    <input type="hidden" yName="<%=outputPath%>@VerificationStatus" class="protectedtext" contentEditable=false value="<%=verificationstatus%>">

<table height="100%" class="view" cellSpacing=0 cellPadding=0 <%=style%> >
    <tr>
        <td valign="top" class="protectedtext">
            <% if(!isVoid(company)) { %> 
                <%=company%>
                <br/>
                <% } %>
            <% if(!isVoid(firstName)) { %> 
                <%=firstName%>
            <% } %>
            <% if(!isVoid(middleName)) { %> 
                <%=middleName%>
            <% } %>
            <% if(!isVoid(lastName)) { %> 
                <%=lastName%>
            <% } if ((!isVoid(firstName)) ||
                    (!isVoid(middleName)) ||
                    (!isVoid(lastName))) { %>
                <br/>
            <% } if(!isVoid(addressLine1)) { %> 
                <%=addressLine1%>
				<br/>
            <% } if(!isVoid(addressLine2)) { %> 
                <%=addressLine2%>
                <br/>
            <% } if(!isVoid(addressLine3)) { %> 
                <%=addressLine3%>
                <br/>
            <% } if(!isVoid(addressLine4)) { %> 
                <%=addressLine4%>
                <br>
            <% } if(!isVoid(addressLine5)) { %> 
                <%=addressLine5%>
                <br/>
            <% } if(!isVoid(addressLine6)) { %> 
                <%=addressLine6%>
                <br>
            <% } if(!isVoid(city)) { %> 
                <%=city%>
            <% } %>
            <% if(!isVoid(state)) { %> 
                <%=state%>
            <% } %>
            <% if(!isVoid(postalCode)) { %> 
                <%=postalCode%>&nbsp;
            <% } if ((!isVoid(city)) && 
                    (!isVoid(state)) && 
                    (!isVoid(postalCode))) { %>
                    <br/>
            <% } if(!isVoid(country)) { %> 
                <%=country%>
                <br/>   
            <%} %>
            <%if(equals("Y",displayPhoneNo)){%>
                <%if(!isVoid(dayphone)){%>
                    <b><yfc:i18n>Day_Phone</yfc:i18n>&nbsp;:&nbsp;</b><%=dayphone%>&nbsp;<br/>
                <%}%>
                <%if(!isVoid(mobilephone)){%>
                    <b><yfc:i18n>Mobile_Phone</yfc:i18n>&nbsp;:&nbsp;</b><%=mobilephone%>&nbsp;<br/>
                <%}%>
                <%if(!isVoid(eveningphone)){%>
                    <b><yfc:i18n>Evening_Phone</yfc:i18n>&nbsp;:&nbsp;</b><%=eveningphone%>&nbsp;
                <%}%>
            <%}%>
			</td>
			<% if(!isVoid(email)) { %> 
				<tr>
					<td valign="top" class="protectedtext">
						<b>E-Mail ID:</b>
					</td>
					<td valign="top" class="protectedtext">
						<%=email%>
						<br/>
					
					</td>
				</tr>
			<% } %>
    </tr>
</table>