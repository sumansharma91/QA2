<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ page import="java.util.*" %>
<%@ include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	
	String inputStr = request.getParameter("YFCInput");
	YFCDocument inDoc = null;
	YFCElement inElem = null;
	YFCElement tempElem = null;

	if (!isVoid(inputStr)) {
		inDoc = YFCDocument.parse(inputStr);
		inElem = inDoc.getDocumentElement();
		YFCDocument oInstDoc = YFCDocument.createDocument("ItemInstruction");
		YFCElement oInstElem = oInstDoc.getDocumentElement();
		oInstElem.setAttribute("InstructionType","RECEIVE");
		inElem.importNode(oInstElem);
		tempElem = YFCDocument.parse("<Item><PrimaryInformation DefaultProductClass=\"\" CountryOfOrigin=\"\" Description=\"\" ShortDescription=\"\" SerializedFlag=\"\" NumSecondarySerials=\"\"/><InventoryParameters />  <ClassificationCodes /> <ItemAliasList /> <ItemExclusionList /><AdditionalAttributeList />   <LanguageDescriptionList /> <Components /><InventoryTagAttributes/>  <AlternateUOMList TotalNumberOfRecords=\"\" ><AlternateUOM  />  </AlternateUOMList> <ItemInstructionList TotalNumberOfRecords=\"\">  <ItemInstruction  />   </ItemInstructionList>  <ItemOptionList />   <ItemServiceAssocList /> <CategoryList />   </Item>").getDocumentElement();
	}

%>

	<yfc:callAPI apiName="getNodeItemDetails" inputElement="<%=inElem%>" 
	templateElement="<%=tempElem%>" outputNamespace=""/>

<%
	
	YFCElement outElem = (YFCElement)request.getAttribute("Item");

	if(outElem != null ) {
		YFCElement itemInstructionListElem = outElem.getChildElement("ItemInstructionList");
		if(itemInstructionListElem != null) {
			int iRecNo = itemInstructionListElem.getIntAttribute("TotalNumberOfRecords");
                        int iNoOfRecTobeDeleted = 0;
			if(iRecNo >= 1) {
				for(Iterator i = itemInstructionListElem.getChildren();i.hasNext();) {
					 YFCElement elemToRm = (YFCElement)i.next();
					 if(!YFCCommon.equals(elemToRm.getAttribute("InstructionType"),"RECEIVE")) {
						  itemInstructionListElem.removeChild(elemToRm);
						  iNoOfRecTobeDeleted++;
					 }
				}
			}
            itemInstructionListElem.setIntAttribute("TotalNumberOfRecords",iRecNo-iNoOfRecTobeDeleted);
		}
	}

	response.getWriter().write(outElem.getString());
	response.getWriter().flush();
	response.getWriter().close();

%>
