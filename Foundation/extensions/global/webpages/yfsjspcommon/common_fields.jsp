<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
    String screenType = getParameter("ScreenType");
    if (!equals(screenType, "detail")) {
        // There are only 2 values that are supported for screenType: "search" & "detail"
        // If anything other than "detail" is passed, then assume ScreenType is "search"
        screenType = "search";
    }
	//System.out.println("##################################################");
    // Set the appropriate parameters based on the screen type. Defaults assume search screen.
    String labelTdClass = "searchlabel";
    String inputTdClass = "searchcriteriacell";
    String refreshJavaScript = "changeSearchView(getCurrentSearchViewId())";
    String columnLayout = "1";
	String acrossEnterprisesAllowed = "true";
	String acrossAllNodesAllowed = "true";

	if (equals(screenType, "detail")) {
        
        labelTdClass = "detaillabel";
        inputTdClass = "";
        refreshJavaScript = "updateCurrentView()";
        columnLayout = "3";
		acrossEnterprisesAllowed = "false";
    }

    String overrideColumnLayout = getParameter("ColumnLayout");
    if (!isVoid(overrideColumnLayout)) {
        columnLayout = overrideColumnLayout;
    }

    String showDocumentType = getParameter("ShowDocumentType");
    if (isVoid(showDocumentType)) {
        // The default value of ShowDocumentType is "true"
        showDocumentType = "true";
    }

    String showEnterpriseCode = getParameter("ShowEnterpriseCode");
    if (isVoid(showEnterpriseCode)) {
        // The default value of ShowEnterpriseCode is "true"
        showEnterpriseCode = "true";
    }

    String enterpriseCodeBinding = getParameter("EnterpriseCodeBinding");
    //System.out.println("enterpriseCodeBinding : "+enterpriseCodeBinding);
	if (isVoid(enterpriseCodeBinding)) {
        // The default value of EnterpriseCodeBinding is "xml:/Order/@EnterpriseCode"
        enterpriseCodeBinding = "xml:/Order/@EnterpriseCode";
    }

    String showNode = getParameter("ShowNode");
    if (isVoid(showNode)) {
        // The default value of ShowNode is "false"
        showNode = "false";
    }

    String doNotProtectNode = getParameter("DoNotProtectNode");
    if (isVoid(doNotProtectNode)) {
        // The default value of doNotProtectNode is "false"
        doNotProtectNode = "false";
    }
    
    String selectedNode = null;

    // Get the user ID from the special namespace "CurrentUser"
    String userID = resolveValue("xml:CurrentUser:/User/@Loginid");
	// System.out.println("current user is: " + userID);

    String disableEnterprise = getParameter("DisableEnterprise");
    if (isVoid(disableEnterprise)) {
		disableEnterprise = "N";
	}
%>

<script language="javascript">

function refreshIfValueChanged(controlObj) {

    if (yfcHasControlChanged(controlObj)) {
		<%=refreshJavaScript%>;
    }
}

function acrossAllEntChange(acrossAllSelected) {

    var entField = document.all("enterpriseFieldObj");
    if (entField != null) {

        if (acrossAllSelected == true) {
            // Blank out the enterprise code.
            entField.value = " ";
        }
        <%=refreshJavaScript%>;
    }
}

function acrossAllNodeChange(acrossAllSel) {

    var nodeField = document.all("nodeFieldObj");
    if (nodeField != null) {

        if (acrossAllSel == true) {
            // Blank out the Node.
            nodeField.value = " ";
        }
        <%=refreshJavaScript%>;
    }
}
</script>

<tr>
    <% if (equals("true", showDocumentType)) {

        String documentTypeBinding = getParameter("DocumentTypeBinding");
        if (isVoid(documentTypeBinding)) {
            // The default value of DocumentTypeBinding is "xml:/Order/@DocumentType"
            documentTypeBinding = "xml:/Order/@DocumentType";
        }

        String refreshOnDocumentType = getParameter("RefreshOnDocumentType");
        if (isVoid(refreshOnDocumentType)) {
            // The default value of RefreshOnDocumentType is "false"
            refreshOnDocumentType = "false";
        }

        String hardCodeDocumentType = getParameter("HardCodeDocumentType");

        String applicationCode = getParameter("ApplicationCode");
		if (isVoid(applicationCode)) {
			// The default value of ApplicationCode comes from the entity resource definition
	        applicationCode = resolveValue("xml:/CurrentEntity/@ApplicationCode", false);
		}
        // System.out.println("Application code is: " + resolveValue("xml:/CurrentEntity/@ApplicationCode"));

        String showOnlyTemplateDocTypes = getParameter("ShowOnlyTemplateDocTypes");
		if (isVoid(showOnlyTemplateDocTypes)) {
			// The default value of ShowOnlyTemplateDocTypes is "false"
	        showOnlyTemplateDocTypes = "false";
		}

        String templateInputString = " IsTemplateDocument=\"N\"";
        if ("true".equals(showOnlyTemplateDocTypes)) {
            templateInputString = " IsTemplateDocument=\"Y\"";
        }
		
		//get the doc type output namespace and check for validity
		boolean validDocTypeOutputNamespace = false;
        String docTypeOutputNamespace = getParameter("DocTypeOutputNamespace");
        YFCElement docTypeListElement = (YFCElement) request.getAttribute(docTypeOutputNamespace);
		if(docTypeListElement != null){
			validDocTypeOutputNamespace = true;
		}

		String docTypeListOutputNamespace = "CommonDocumentTypeList";

		if(!validDocTypeOutputNamespace){
			// Call API to get the data for the Document Type field.
			String documentTypeInputStr = "<DocumentParams DocumentType=\"";
			if (!isVoid(hardCodeDocumentType)) {
				documentTypeInputStr = documentTypeInputStr + hardCodeDocumentType;
			}
			documentTypeInputStr = documentTypeInputStr + "\"" + templateInputString + "><DataAccessFilter UserId=\"" + userID + "\" ApplicationCode=\"" + applicationCode + "\" ModuleCode=\" \" AddToConsole=\"Y\"/></DocumentParams>";
			
			YFCElement documentTypeInput = YFCDocument.parse(documentTypeInputStr).getDocumentElement();
			YFCElement documentTypeTemplate = YFCDocument.parse("<DocumentParamsList TotalNumberOfRecords=\"\" DefaultDocTypeForApplication=\"\"><DocumentParams DocumentType=\"\" Description=\"\"/></DocumentParamsList>").getDocumentElement();
        %>

	        <yfc:callAPI apiName="getDocumentTypeList" inputElement="<%=documentTypeInput%>" templateElement="<%=documentTypeTemplate%>" outputNamespace="<%=docTypeListOutputNamespace%>"/>

        <%
			}//end of if validDocTypeOutputNamespace
		else{
			docTypeListOutputNamespace = docTypeOutputNamespace;
		}

		YFCElement allDocTypeListElement = (YFCElement) request.getAttribute(docTypeListOutputNamespace);

        double numberOfDocTypes = getNumericValue("xml:" + docTypeListOutputNamespace + ":/DocumentParamsList/@TotalNumberOfRecords");
        // System.out.println("Total number of document types is " + String.valueOf(numberOfDocTypes));
        
        String selectedDocumentType = resolveValue(documentTypeBinding);
        if (isVoid(selectedDocumentType) || equals("Y", getParameter("ResetDocumentType") ) ) {	//	cr 35413
            // Default the document type to the default one as returned by the API
            if (numberOfDocTypes > 0) {
                
                selectedDocumentType = getValue(docTypeListOutputNamespace, "xml:/DocumentParamsList/@DefaultDocTypeForApplication");

                // Check that the user actually has data security rights to the default document
                // type. If the user does not have rights, then default the first document type returned.
                boolean hasAccessToDefaultDocType = false;

                if (null != allDocTypeListElement) {
                    for (Iterator i = allDocTypeListElement.getChildren(); i.hasNext();) {

                        YFCElement singleDocType = (YFCElement)i.next();
                        if (equals(selectedDocumentType, singleDocType.getAttribute("DocumentType"))) {
                            hasAccessToDefaultDocType = true;
                        }
                    }
                }

                if (!hasAccessToDefaultDocType) {
                    // Default the document type to the first one in the API output.
                    selectedDocumentType = getValue(docTypeListOutputNamespace, "xml:/DocumentParamsList/DocumentParams/@DocumentType");
                }
            }
        }
        // Set the appropriate document type in the CommonFields namespace
        setCommonFieldAttribute("DocumentType", selectedDocumentType);
        
        // Set the document type description if there is only one document type
        String docTypeDesc = "";
        if (numberOfDocTypes == 1) {
            
            docTypeDesc = getValue(docTypeListOutputNamespace, "xml:/DocumentParamsList/DocumentParams/@Description");
        }
        %>

        <td class="<%=labelTdClass%>">
            <yfc:i18n>Document_Type</yfc:i18n>
        </td>
    
    <% if (equals(screenType, "search")) { %>
        </tr>
        <tr>
    <% } %>

        <% if (numberOfDocTypes > 1) { %>
            <td class="<%=inputTdClass%>" nowrap="true">
                <select class="combobox"
                <% if (equals(refreshOnDocumentType, "true")) { %> onChange="<%=refreshJavaScript%>" <% } %> 
                <%=getComboOptions(documentTypeBinding)%>>
                    <% if (null != allDocTypeListElement) {
                        for (Iterator i = allDocTypeListElement.getChildren(); i.hasNext();) {
                            YFCElement singleDocType = (YFCElement)i.next(); %>

                            <option value="<%=singleDocType.getAttribute("DocumentType")%>"
                                <% if (equals(selectedDocumentType, singleDocType.getAttribute("DocumentType"))) { %>
                                    selected="Y"
                                <% } %>
                            ><%=getDBString(singleDocType.getAttribute("Description"))%></option>
                        <% }
                    } %>
                </select>
            </td>
        <% } else { %>
            <td class="protectedtext" nowrap="true">
                <%=getDBString(docTypeDesc)%>
                <input type="hidden" <%=getTextOptions(documentTypeBinding, selectedDocumentType)%>/>
            </td>
        <% } %>

        <% if (equals(columnLayout, "1")) { %>
            </tr>
            <tr>
        <% } %>
    <% } %>

    <% if (equals("true", showNode)) {        

		String nodeBinding = getParameter("NodeBinding");
       if (isVoid(nodeBinding)) {
            // The default value of NodeBinding is "xml:/Order/@ShipNode"
            nodeBinding = "xml:/Order/@ShipNode";
        }
		String entityName = (String)request.getAttribute("Entity");
		String applicationId = YFCContextParams.getInstance(request).getConsoleApplicationId();
		YFCElement spAttr = UIApplication.getInstance(applicationId).getEntity(entityName).getSpecialAttributes();
		String applicationCode = "";
		if (!isVoid(spAttr)) {
			applicationCode = spAttr.getAttribute("ApplicationCode");
		}
		//System.out.println("applicationCode : "+applicationCode+" entityName : "+entityName+" nodeBinding : "+nodeBinding);
        String refreshOnNode = getParameter("RefreshOnNode");
        if (isVoid(refreshOnNode)) {
            // The default value of RefreshOnNode is "false"
            refreshOnNode = "false";
        }

		String nodeLabel = getParameter("NodeLabel");
        if (isVoid(nodeLabel)) {
            // The default value of NodeLabel is "Node"
            nodeLabel = "Node";
        }

        selectedNode = resolveValue(nodeBinding);

		double numberOfNodes = 0;
    %>

        <td class="<%=labelTdClass%>"><yfc:i18n><%=nodeLabel%></yfc:i18n></td>

    <% if (equals(screenType, "search")) { %>
        </tr>
        <tr>
    <% }

        String defaultNodeBinding ="";
		if (doesUserHasNode()) {
            numberOfNodes = 1;
            selectedNode = resolveValue("xml:CurrentUser:/User/@Node");
            defaultNodeBinding = "xml:CurrentUser:/User/@Node";

		} else {
			String acrossAllNodesValue = getParameter("acrossAllNodesAllowed");
			if (!isVoid(acrossAllNodesValue)) {
				acrossAllNodesAllowed = acrossAllNodesValue;
			}
			if ((equals("true", acrossAllNodesAllowed)) && (!equals("false", request.getParameter("AcrossNodes")))) {
				defaultNodeBinding = " ";
			}
			// Call API to get the data for the Node field.
			YFCElement nodeInput = YFCDocument.parse("<ShipNode/>").getDocumentElement();
			String selectedEnterprise = resolveValue(enterpriseCodeBinding);
			//if (!isVoid(selectedEnterprise)) {
				// Get all nodes owned by the logged in organization for any other user.
				//nodeInput = YFCDocument.parse("<ShipNode><Organization><EnterpriseOrgList><OrgEnterprise EnterpriseOrganizationKey=\"" + selectedEnterprise + "\"/></EnterpriseOrgList></Organization></ShipNode>").getDocumentElement();
			//}
			YFCElement nodeTemplate = YFCDocument.parse("<ShipNodeList TotalNumberOfRecords=\"\"><ShipNode ShipNode=\"\" OwnerKey=\"\" Description=\"\"/></ShipNodeList>").getDocumentElement();
			//System.out.println("getShipNodeList i/p : "+nodeInput);
			%>
				<yfc:callAPI apiName="getShipNodeList" inputElement="<%=nodeInput%>" templateElement="<%=nodeTemplate%>" outputNamespace="CommonNodeList"/>
			<% 
			numberOfNodes = getNumericValue("xml:CommonNodeList:/ShipNodeList/@TotalNumberOfRecords");
			if (numberOfNodes == 0 ){
				selectedNode = "";
				doNotProtectNode = "true";
			}
			//System.out.println("Total number of permissible nodes : " + String.valueOf(numberOfNodes));
			//System.out.println("getShipNodeList o/p : "+(YFCElement)request.getAttribute("CommonNodeList"));
			// If the number of nodes is 1, then make sure this one is selected and protected.
			if ((equals("false",doNotProtectNode)) && (!equals("true", request.getParameter("AcrossNodes")))) {
				if(numberOfNodes == 1){
					selectedNode = resolveValue("xml:CommonNodeList:/ShipNodeList/ShipNode/@ShipNode");
					YFCElement sessionUser = (YFCElement) request.getSession(true).getAttribute("CurrentUser");
					if(sessionUser != null){
						sessionUser.setAttribute("Node", selectedNode);
						%>
						<script>							
							if('<%=selectedNode%>' != null){								
								window.location.reload();
							}
						</script>
						<%
					}
				}
			}
		}

		if(isVoid(selectedNode)){
			if (!equals("true", request.getParameter("AcrossNodes"))) {				   
			   selectedNode = getSearchCriteriaValueWithDefaulting(nodeBinding, defaultNodeBinding);
			} 
		}
        boolean acrossAllNodes = false;
		// If Node is void at this point, then we need to make
		// the Across All Nodes Option selected (if it will be shown).
		if (isVoid(selectedNode)) {
			acrossAllNodes = true;
			// Set the default Node the CommonFields namespace even
			// though it will not appear selected in the Node field
			setCommonFieldAttribute("Node", resolveValue(nodeBinding, false));
			if(equals("detail", screenType)){ 
				if(numberOfNodes ==1){
					String sNode = resolveValue("xml:CommonNodeList:/ShipNodeList/ShipNode/@ShipNode");
					YFCElement sessionUser = (YFCElement) request.getSession(true).getAttribute("CurrentUser");
					if(sessionUser != null){			
						sessionUser.setAttribute("Node", sNode);
						%>
						<script>							
							if('<%=selectedNode%>' != null){								
								window.location.reload();
							} 
						</script>
						<%						
					}
				} else if (isVoid(resolveValue("xml:CurrentUser:/User/@Node")) && "wms".equals(applicationCode)){
				%>
					<script>					   
						function showDefaultNodeSelectionPopup(){
							//alert ("Loading node selection");
							var myObject = new Object();
							myObject.parentWindow = window;
							myObject.NodeBinding = "<%=nodeBinding%>";
							myObject.Time = getBrowserCurrentTime();
							yfcShowDetailPopup("YWMD830", "", "450", "250", myObject);
							if(myObject.returnedValue == "Refresh"){
								//reload only if OK is pressed in the pop-up else open home.detail
								window.location.reload();
							} else {
								var url= contextPath+"/console/home.detail";
								window.location.href = url;							
							}
							
						}
						function addLoadEvent(func) {
							//alert("addLoadEvent..."+func);
							var oldonload = window.onload;
							if (typeof window.onload != 'function') {
								//alert("addLoadEvent...if");
								window.onload = func;   
							}else{
								//alert("addLoadEvent...else");
								window.onload = function() { 
													oldonload();
													func();
												}   
							}
						}
						addLoadEvent(showDefaultNodeSelectionPopup);
						//window.attachEvent("onload", showDefaultNodeSelectionPopup)
					</script>
				<%
				}
			}else{				   
				if(!isVoid(resolveValue("xml:CurrentUser:/User/@Node"))){					  
				   selectedNode = resolveValue("xml:CurrentUser:/User/@Node");
				}
			}
		} else {
			// Set the appropriate node in the CommonFields namespace
			setCommonFieldAttribute("Node", selectedNode);           
		}

		if (acrossAllNodes && (equals("true",doNotProtectNode))) { %>
            <td class="<%=inputTdClass%>" nowrap="true">
                <input type="radio" name="AcrossNodes" value="false" onclick="acrossAllNodeChange(false)" checked="false"/>
                <input type="hidden" id="nodeFieldObj" <%=getTextOptions(nodeBinding, selectedNode)%>/>
                <select class="protectedinput" disabled="true" name="justForDisplay">
                    <yfc:loopOptions binding="xml:CommonNodeList:/ShipNodeList/@ShipNode" name="ShipNode"
                        value="ShipNode"/>
                </select>
            </td>
        <% } else { %>
        <% if (numberOfNodes > 20) { %>
            <td class="<%=inputTdClass%>" nowrap="true">
                <% if (equals("true", doNotProtectNode)&&(!isShipNodeUser())) { %>
                      <input type="radio" name="AcrossNodes" value="false" onclick="acrossAllNodeChange(false)" checked="true"/>
                <% } %>
                <input type="text" class="protectedinput" contenteditable="false" <%=getTextOptions(nodeBinding, selectedNode)%>  id="nodeFieldObj" />
                <img class="lookupicon" name="search"
                    onclick="callLookup(this,'shipnode','xml:/ShipNode/Organization/EnterpriseOrgList/OrgEnterprise/@EnterpriseOrganizationKey=<%=resolveValue("xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey")%>')
                    <% if (equals(refreshOnNode, "true")) { %>;refreshIfValueChanged(document.all('<%=nodeBinding%>'))<% } %>"
                    <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Node")%>/>
            </td>
        <% } else if ((numberOfNodes <= 20) && (numberOfNodes > 1)) { %>
            <td class="<%=inputTdClass%>" nowrap="true">
                   <% if (equals("true", doNotProtectNode)&&(!isShipNodeUser())) { %>
                       <input type="radio" name="AcrossNodes" value="false" onclick="acrossAllNodeChange(false)" checked="true"/>
                   <% } %>
                <select class="combobox"  id="nodeFieldObj" 
                <% if (equals(refreshOnNode, "true")) { %> onChange="<%=refreshJavaScript%>" <% } %>
                <%=getComboOptions(nodeBinding)%>>
                    <yfc:loopOptions binding="xml:CommonNodeList:/ShipNodeList/@ShipNode" name="ShipNode"
                    value="ShipNode" selected='<%=selectedNode%>'/>
                </select>
            </td>
        <% } else { %>
            <td class="protectedtext" nowrap="true">
            
                    <% if (equals("true", doNotProtectNode)&&(!isShipNodeUser())) { %>
                        <input type="radio" name="AcrossNodes" value="false" onclick="acrossAllNodeChange(false)" checked="true"/>
                    <% } %>
                <%=selectedNode%>
                <input type="hidden" id="nodeFieldObj" <%=getTextOptions(nodeBinding, selectedNode)%>/>
            </td>
       <% } %>
       <%}%>
        <% if (equals("true", doNotProtectNode)&&(!isShipNodeUser())) { %>
            </tr><tr>
            <td class="<%=inputTdClass%>" nowrap="true">
                <input type="radio" name="AcrossNodes" value="true" onclick="acrossAllNodeChange(true)" <% if (acrossAllNodes) {%> checked="true" <%}%>/>
                <yfc:i18n>Across_Nodes</yfc:i18n>
            </td>
        <% } %>

        <% if (equals(columnLayout, "1")) { %>
            </tr>
            <tr>
        <% } %>
    <% } %>

    <% if (equals("true", showEnterpriseCode)) {

        String enterpriseCodeLabel = getParameter("EnterpriseCodeLabel");
        if (isVoid(enterpriseCodeLabel)) {
            // The default value of EnterpriseCodeLabel is "Enterprise"
            enterpriseCodeLabel = "Enterprise";
        }

		String onlyInventoryOrganizations = getParameter("OnlyInventoryOrganizations");
		if(isVoid(onlyInventoryOrganizations)){
			// If onlyInventoryOrganizations is null making is blank
			onlyInventoryOrganizations = "";
		}

        String refreshOnEnterpriseCode = getParameter("RefreshOnEnterpriseCode");
        if (isVoid(refreshOnEnterpriseCode)) {
            // The default value of RefreshOnEnterpriseCode is "false"
            refreshOnEnterpriseCode = "false";
        }

        // The EnterpriseListForNodeField parameter determines if the list of enterprises
        // that are displayed in the screen should be based on the node selection. If passed as
        // true, then the list of enterprises will be all the enterprises for which this node
        // organization participates.
        String enterpriseListForNodeField = getParameter("EnterpriseListForNodeField");
        if (isVoid(enterpriseListForNodeField)) {
            // The default value of EnterpriseListForNodeField is "false"
            enterpriseListForNodeField = "false";
        }

        String organizationListForInventory = getParameter("OrganizationListForInventory");
        if (isVoid(organizationListForInventory)) {
            // The default value of OrganizationListForInventory is "false"
            organizationListForInventory = "false";
        }

        if (equals("true", organizationListForInventory)) {
            acrossEnterprisesAllowed = "false";
        }

		String organizationListForCapacity = getParameter("OrganizationListForCapacity");
        if (isVoid(organizationListForCapacity)) {
            // The default value of OrganizationListForCapacity is "false"
            organizationListForCapacity = "false";
        }

        if (equals("true", organizationListForCapacity)) {
            acrossEnterprisesAllowed = "false";
        }

        // The DefaultEnterpriseBinding parameter is used to properly default the correct enterprise
        // (or organization code) in the Enteprise field when the end user initially navigates to
        // the screen.
        String defaultEnterpriseBinding = getParameter("DefaultEnterpriseBinding");
		String usersEnterprise = resolveValue("xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
		if (isVoid(defaultEnterpriseBinding)) {
            // The default value of DefaultEnterpriseBinding is "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey"
            defaultEnterpriseBinding = "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey";
			// If an Org user(user's Org is not a Ship Node or an Enterprise) is logged in and organizationListForInventory,
			//	we want to default the Organization field to the logged in user's Org.
			if (equals("true", organizationListForInventory)) {
				if((!isShipNodeUser()) && (!isEnterprise())) {
					defaultEnterpriseBinding = "xml:CurrentOrganization:/Organization/@OrganizationCode";
				}
			}
        }

        String finalDefaultEnterpriseBinding = defaultEnterpriseBinding;
        // The AcrossEnterprisesAllowed parameter is used to show an additional radio button
        // next to the enterprise field where the user can choose to search across all
        // enterprises (basically, this is the equivalent to searching with enterprise code
        // as blanks). The default value for this field depends on other parameters.
        // If "ScreenType" is detail, then the default value is "false"
        // If "OrganizationListForInventory" is true, then the default value is "false"
        // Otherwise, the default value is "true".
        String acrossEnterprisesAllowedValue = getParameter("AcrossEnterprisesAllowed");
        if (!isVoid(acrossEnterprisesAllowedValue)) {
            acrossEnterprisesAllowed = acrossEnterprisesAllowedValue;
        }

        if ((equals("true", acrossEnterprisesAllowed)) && (!equals("false", request.getParameter("AcrossEnterprises")))) {
            finalDefaultEnterpriseBinding = " ";
        }

        // Call API(s) to get the data for the Enterprise field.
        YFCElement enterpriseInput = null;
        YFCElement enterpriseTemplate = YFCDocument.parse("<OrganizationList TotalNumberOfRecords=\"\"><Organization OrganizationCode=\"\" OrganizationKey=\"\" OrganizationName=\"\"/></OrganizationList>").getDocumentElement();

        if ((equals("true", enterpriseListForNodeField)) && (!isVoid(selectedNode))) {
			enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\" OnlyInventoryOrganizations=\""+ onlyInventoryOrganizations +"\"><RelatedOrgList><OrgEnterprise OrganizationKey=\"" + selectedNode + "\"/></RelatedOrgList><DataAccessFilter UserId=\"" + userID + "\"/></Organization>").getDocumentElement();
			//System.out.println("getOrganizationList i/p 1 : "+enterpriseInput);
			%>
            <yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseList"/>
        <% }
        else if (equals("true", organizationListForInventory)) {
			//if logged in user's org is Ship Node or Enterprise, we want a list of enterprises in which the user's org participates. 
			if((isShipNodeUser()) || (isEnterprise())) {
				enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\" OnlyInventoryOrganizations=\""+ onlyInventoryOrganizations +"\"><OrgRoleList><OrgRole RoleKey=\"ENTERPRISE\"/></OrgRoleList><DataAccessFilter UserId=\"" + userID + "\"/></Organization>").getDocumentElement();
			} else {
				//if logged in user's org is neither a Ship Node or Enterprise, we only want that org.
				enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\" OnlyInventoryOrganizations=\""+ onlyInventoryOrganizations +"\"><DataAccessFilter UserId=\"" + userID + "\"/></Organization>").getDocumentElement();
			} 
			//System.out.println("getOrganizationList i/p 2 : "+enterpriseInput);
			%>
			<yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseList"/>
        <% }
		else if (equals("true", organizationListForCapacity)) {
            enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\" OnlyCapacityOrganizations=\"Y\"/>").getDocumentElement(); 
			//System.out.println("getOrganizationList i/p 3 : "+enterpriseInput);
			%>
            <yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseList"/>
        <% }
        else {
            // Get a list of enterprises from the data security configuration
            enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\" OnlyInventoryOrganizations=\""+ onlyInventoryOrganizations +"\"><OrgRoleList><OrgRole RoleKey=\"ENTERPRISE\"/></OrgRoleList><DataAccessFilter UserId=\"" + userID + "\"/></Organization>").getDocumentElement(); 
			//System.out.println("getOrganizationList i/p 4 : "+enterpriseInput);
			%>
            <yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseListDataSecurity"/>

            <%
 			//System.out.println("getOrganizationList o/p 4 : "+(YFCElement)request.getAttribute("CommonEnterpriseListDataSecurity"));
              if (!isShipNodeUser()) {
                    // Get a list of enterprises this organization participates in (don't do this
                    // for a ship node user though).
                    enterpriseInput = YFCDocument.parse("<Organization MaximumRecords=\"21\" OnlyInventoryOrganizations=\""+ onlyInventoryOrganizations +"\"><RelatedOrgList><OrgEnterprise OrganizationKey=\"" + resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode") + "\"/></RelatedOrgList></Organization>").getDocumentElement(); 
					//System.out.println("getOrganizationList i/p 5 : "+enterpriseInput);
					%>
                    <yfc:callAPI apiName="getOrganizationList" inputElement="<%=enterpriseInput%>" templateElement="<%=enterpriseTemplate%>" outputNamespace="CommonEnterpriseListParticipation"/>
                <% 
 					//System.out.println("getOrganizationList o/p 5 : "+(YFCElement)request.getAttribute("CommonEnterpriseListParticipation"));
			}
            
            // Combine the two lists
            double numberOfEnterprisesInDSG = getNumericValue("xml:CommonEnterpriseListDataSecurity:/OrganizationList/@TotalNumberOfRecords");
			if(numberOfEnterprisesInDSG>0) {
				YFCElement completeOrgList = (YFCElement) request.getAttribute("CommonEnterpriseListDataSecurity");
				request.setAttribute("CommonEnterpriseList", completeOrgList);
			}
			else {
	            YFCElement completeOrgList = combineOrganizationLists((YFCElement) request.getAttribute("CommonEnterpriseListDataSecurity"), (YFCElement) request.getAttribute("CommonEnterpriseListParticipation"));
		        request.setAttribute("CommonEnterpriseList", completeOrgList);
			}
        }
        %>
        
        <%
			//System.out.println("CommonEnterpriseList : "+request.getAttribute("CommonEnterpriseList"));
           String selectedEnterprise = " ";
           if (!equals("true", request.getParameter("AcrossEnterprises"))) {
               selectedEnterprise = getSearchCriteriaValueWithDefaulting(enterpriseCodeBinding, finalDefaultEnterpriseBinding);
           }
           boolean acrossAllEnt = false;
           // If enterprise code is void at this point, then we need to make
           // the Across All Enterprises Option selected (if it will be shown).
           if (isVoid(selectedEnterprise)) {
               acrossAllEnt = true;
               // Set the default enterprise code in the CommonFields namespace even
               // though it will not appear selected in the enteprise field
               setCommonFieldAttribute("EnterpriseCode", resolveValue(defaultEnterpriseBinding, false));
           }
           else {
               // Set the appropriate enterprise code in the CommonFields namespace
               setCommonFieldAttribute("EnterpriseCode", selectedEnterprise);
               setCommonFieldAttribute("OrganizationCode", selectedEnterprise);
           }
           double numberOfEnterprises = getNumericValue("xml:CommonEnterpriseList:/OrganizationList/@TotalNumberOfRecords");
           //System.out.println("Total number of enterprises is " + String.valueOf(numberOfEnterprises));
			if (!isVoid(selectedEnterprise)) {
				//Check whether the selected enterprise is among the allowed enterprises.
				YFCElement allowedEnterpriseList = (YFCElement) request.getAttribute("CommonEnterpriseList");
				if (numberOfEnterprises <= 21) {
					boolean isEnterpriseAllowed = checkItemAllowed(allowedEnterpriseList, "OrganizationCode", selectedEnterprise);
					//System.out.println("isEnterpriseAllowed : "+isEnterpriseAllowed);				
					if (!isEnterpriseAllowed) {
						selectedEnterprise = allowedEnterpriseList.getFirstChildElement().getAttribute("OrganizationCode");
					}
				} else if (selectedEnterprise.equals(usersEnterprise)) {
					if(!checkItemAllowed(allowedEnterpriseList, "OrganizationCode", usersEnterprise)) {
						selectedEnterprise = allowedEnterpriseList.getFirstChildElement().getAttribute("OrganizationCode");
					}
				}
			}
        %>

        <td class="<%=labelTdClass%>">
            <yfc:i18n><%=enterpriseCodeLabel%></yfc:i18n>
        </td>
        
    <% if (equals(screenType, "search")) { %>
        </tr>
        <tr>
    <% } %>

        <% if (acrossAllEnt) { %>
            <td class="<%=inputTdClass%>" nowrap="true">
                <input type="radio" name="AcrossEnterprises" value="false" onclick="acrossAllEntChange(false)" checked="false"/>
                <input type="hidden" id="enterpriseFieldObj" <%=getTextOptions(enterpriseCodeBinding, selectedEnterprise)%>/>
                <select class="protectedinput" disabled="true" name="justForDisplay">
                    <yfc:loopOptions binding="xml:CommonEnterpriseList:/OrganizationList/@Organization" name="OrganizationCode"
                        value="OrganizationCode"/>
                </select>
            <% if (!equals(screenType, "search")) { %>
	            </td>
            <% } %>
        <% } else { %>
            <% if (numberOfEnterprises > 20) {

                // Show a protected text field with a lookup button next to it. The extra parameters sent
                // to the lookup are different for the inventory organization code field.
                String extraParams = "lookupForSingleRole=ENTERPRISE&applyDataSecurity=Y";
                if (equals("true", organizationListForInventory)) {
                    extraParams = "applyDataSecurity=Y";
                } %>

                <td class="<%=inputTdClass%>" nowrap="true">
                    <% if (equals("true", acrossEnterprisesAllowed)) { %>
                        <input type="radio" name="AcrossEnterprises" value="false" onclick="acrossAllEntChange(false)" checked="true"/>
                    <% } %>
                    
                    <input type="text" class="protectedinput" contenteditable="false" id="enterpriseFieldObj" <%=getTextOptions(enterpriseCodeBinding, selectedEnterprise)%>/>
                    <% if ((!equals("Y", disableEnterprise))) { %>
                        <img class="lookupicon" name="search" 
                        onclick="callLookup(this,'organization','<%=extraParams%>')
                        <% if (equals(refreshOnEnterpriseCode, "true")) { %>;refreshIfValueChanged(document.all('<%=enterpriseCodeBinding%>'))<% } %>"
                        <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
                    <% } %> 
				<% if (!equals(screenType, "search")) { %>
					</td>
				<% } %>
            <% } else if ((numberOfEnterprises <= 20) && (numberOfEnterprises > 1) && (!equals(disableEnterprise,"Y"))) { %>
                <td class="<%=inputTdClass%>" nowrap="true">

                    <% if (equals("true", acrossEnterprisesAllowed)) { %>
                        <input type="radio" name="AcrossEnterprises" value="false" onclick="acrossAllEntChange(false)" checked="true"/>
                    <% } %>

                    <select class="combobox" id="enterpriseFieldObj" 
                    <% if (equals(refreshOnEnterpriseCode, "true")) { %> onChange="<%=refreshJavaScript%>" <% } %>
                    <%=getComboOptions(enterpriseCodeBinding)%>>
					<%if(!equals("true",getParameter("DoNotDefaultEnterprise"))){%>
						<yfc:loopOptions binding="xml:CommonEnterpriseList:/OrganizationList/@Organization" name="OrganizationCode" value="OrganizationCode" selected='<%=selectedEnterprise%>'/>
					<%}else{
						selectedEnterprise = getParameter("EnterpriseCodeBinding");%>
						<yfc:loopOptions binding="xml:CommonEnterpriseList:/OrganizationList/@Organization" name="OrganizationCode" value="OrganizationCode" selected='<%=selectedEnterprise%>'/>
					<%}%>
                    </select>
				<% if (!equals(screenType, "search")) { %>
					</td>
				<% } %>
            <% } else { %>
                <td class="protectedtext" nowrap="true">
                    <% if (equals("true", acrossEnterprisesAllowed)) { %>
                        <input type="radio" name="AcrossEnterprises" value="false" onclick="acrossAllEntChange(false)" checked="true"/>
                    <% } 
					if (!isVoid(selectedEnterprise)){%>
						<%=selectedEnterprise%>
						<input type="hidden" id="enterpriseFieldObj" <%=getTextOptions(enterpriseCodeBinding, selectedEnterprise)%>/>
					<%} else if (numberOfEnterprises == 1) {%>
						<select class="combobox" id="enterpriseFieldObj" <% if (equals(refreshOnEnterpriseCode, "true")) { %> onChange="<%=refreshJavaScript%>" <% } %> <%=getComboOptions(enterpriseCodeBinding)%>>
							<yfc:loopOptions binding="xml:CommonEnterpriseList:/OrganizationList/@Organization" name="OrganizationCode" value="OrganizationCode" selected='<%=selectedEnterprise%>'/>							
						</select>
					<%}%>					
				<% if (!equals(screenType, "search")) { %>
					</td>
				<% } %>
            <% } %>
        <% } %>
        <% if (equals("true", acrossEnterprisesAllowed)) { %>
            <% if (!equals(screenType, "search")) { %>
	            <td class="<%=inputTdClass%>" nowrap="true">
            <% } %>
                <input type="radio" name="AcrossEnterprises" value="true" onclick="acrossAllEntChange(true)" <% if (acrossAllEnt) {%> checked="true" <%}%>/>
                <yfc:i18n>Across_Enterprises</yfc:i18n>
            </td>
	    <% } else if(equals("3", columnLayout) )	{%>
            <% if (equals(screenType, "search")) { %>
	            </td>
            <% } %>
			<td></td>
			<td></td>
	    <% } else {%>
            <% if (equals(screenType, "search")) { %>
	            </td>
            <% } %>
        <% } %>
    <% }else{ //Fix for CR 74471
		    if(equals("true",getParameter("DoNotDefaultEnterprise"))){%>
                <td class="detaillabel" >
                  <yfc:i18n>Enterprise</yfc:i18n>
                </td>
			<%YFCElement inputElem = YFCDocument.createDocument("Organization").getDocumentElement();
			YFCElement RelatedOrgList = inputElem.createChild("RelatedOrgList");
			YFCElement OrgEnterprise = RelatedOrgList.createChild("OrgEnterprise");
			YFCElement DataAccessFilter = inputElem.createChild("DataAccessFilter");
			inputElem.setAttribute("IgnoreOrdering","Y");
			inputElem.setAttribute("MaximumRecords","21");
			OrgEnterprise.setAttribute("OrganizationKey",resolveValue("xml:CurrentUser:/User/@Node"));
			DataAccessFilter.setAttribute("UserId",resolveValue("xml:CurrentUser:/User/@Loginid"));
			YFCElement templateElem = YFCDocument.parse("<OrganizationList> <Organization InventoryOrganizationCode=\"\" OrganizationCode=\"\" OrganizationKey=\"\"/></OrganizationList>").getDocumentElement();%>
			<yfc:callAPI apiName="getOrganizationList" inputElement="<%=inputElem%>" templateElement="<%=templateElem%>" outputNamespace="OrganizationList"/>
			<td nowrap="true">
				<select name='<%=enterpriseCodeBinding%>' class="combobox" >
					<yfc:loopOptions binding="xml:OrganizationList:/OrganizationList/@Organization" 
						name="OrganizationCode" value="OrganizationKey" selected='<%=enterpriseCodeBinding%>'/>
				</select>
			</td>
			<%} else if(equals("3", columnLayout)){%>
					<td></td>
				<% if (!equals("true", showNode)){%>
					<td></td>
				<%} else if (!equals("true", showDocumentType)){%>
					<td></td>
				<%}
			}			
		}%>
</tr>

<%!
	boolean doesUserHasNode() {
        String sNode = getValue("CurrentUser","xml:/User/@Node");
        boolean bUserHasNode = isVoid(sNode)?false:true;
		//System.out.println("UserHasNode : "+bUserHasNode);
		return bUserHasNode;
    }

	boolean checkItemAllowed(YFCElement listElem, String xmlAttribute, String item) {
		for (Iterator itr = listElem.getChildren();itr.hasNext();) { 		
			YFCElement nodeElem = (YFCElement) itr.next();
			String itemFromList = nodeElem.getAttribute(xmlAttribute);
			if (item.equals(itemFromList)) {
				return true;
			}
		}
		return false;
	}	
%>
<script language="javascript">
	function getBrowserCurrentTime(){
		var tDate = new Date();
		var tDateStr = ""+ tDate.getDate()+" "+tDate.getMinutes() + ":" + tDate.getSeconds();
		return tDateStr;
	}
</script>
