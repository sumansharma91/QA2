<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <SaveNewCustomer xmlns="http://NsbGroup.com/webservices/">
            <parameters>
                <DatabaseGroupID>200</DatabaseGroupID>
                <SalesAssociateNumber>999999</SalesAssociateNumber>
                <StoreNumber>999</StoreNumber>
                <CustomerNumber>-1</CustomerNumber>
                <LanguageID>-1</LanguageID>
            </parameters>
            <checkDuplicates>true</checkDuplicates>
            <customer>
	                <RecordState>Added</RecordState>
	                <ID>-1</ID>
	                <Number><xsl:value-of select = "Customer/@CustomerID" /></Number>
	                <OriginalNumber>-1</OriginalNumber>
	                <TitleCode/>
	                <FirstName><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/@FirstName" /></FirstName>
	       		    <LastName><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/@LastName" /></LastName>
	                 <Gender>U</Gender>
	               <MaritalStatus>U</MaritalStatus>
	               <StoreNumber>999</StoreNumber>
	                <LanguageCode>ENG</LanguageCode>
	                <StatusCode>A</StatusCode>
	                <EmailAddress><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/@EmailID" /></EmailAddress> 
	                
	                <EmailOptInFlagCode>1</EmailOptInFlagCode>  
            </customer>
            <customerAddresses>
                <customerAddresses>
				      <Items>
				        <CustomerAddressWS>
					<RecordState>Added</RecordState>
				          <CountryCode><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress/PersonInfo/@Country" /></CountryCode>
				          <TelephoneNumber><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/@MobilePhone" /></TelephoneNumber>
				          <Address1><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress/PersonInfo/@AddressLine1" /></Address1>
				          <City><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress/PersonInfo/@City" /></City>
				          <State><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress/PersonInfo/@State" /></State>
					<PostalCode><xsl:value-of select = "Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress/PersonInfo/@ZipCode" /></PostalCode>
				          <AddressType xsi:nil="true" />
				          <CountryAddressFormat xsi:nil="true" />
				        </CustomerAddressWS>
				      </Items>
				    </customerAddresses>
                <CustomerNumber>-1</CustomerNumber>
            </customerAddresses>
        </SaveNewCustomer>
    </soap:Body>
</soap:Envelope>
	</xsl:template>
</xsl:stylesheet>