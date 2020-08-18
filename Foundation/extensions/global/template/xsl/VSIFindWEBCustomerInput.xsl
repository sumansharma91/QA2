<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <FindCustomers xmlns="http://NsbGroup.com/webservices/">
            <parameters>
                <DatabaseGroupID>200</DatabaseGroupID>
                <SalesAssociateNumber>999999</SalesAssociateNumber>
                <StoreNumber>999</StoreNumber>
                <CustomerNumber>-1</CustomerNumber>
                <LanguageID>-1</LanguageID>
            </parameters>
            <customerNumber>-1</customerNumber>
            <alternateKey1>-1</alternateKey1>
            <alternateKey2><xsl:value-of select = "Order/Extn/@CustomerAltKey" /></alternateKey2>
            <telephoneNumber/>
            <postalCode/>
            <address1/>
            <firstName/>
            <lastName/>
            <emailAddress/>
            <customerListCode>-1</customerListCode>
        </FindCustomers>
    </soap:Body>
</soap:Envelope>
	</xsl:template>
</xsl:stylesheet>