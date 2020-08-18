<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <CalculateReward xmlns="http://NsbGroup.com/webservices/">
            <parameters>
                <DatabaseGroupID>200</DatabaseGroupID>
                <SalesAssociateNumber>999999</SalesAssociateNumber>
                <StoreNumber>999</StoreNumber>
                <CustomerNumber><xsl:value-of select = "Order/@BillToID" /></CustomerNumber>
                <LanguageID>-1</LanguageID>
            </parameters>
			 <transactionInfo>
            <RecordState>Unchanged</RecordState>
            <CustomerNumber><xsl:value-of select = "Order/@BillToID" /></CustomerNumber>
			
			<xsl:variable name="Date">
			<xsl:value-of select = "Order/@OrderDate" />
			</xsl:variable>
			
            <POSTransactionNumber>1</POSTransactionNumber>
            <TransactionDate><xsl:value-of select="substring($Date,1,19)"/></TransactionDate>
		    <RegisterID>1</RegisterID>
			 <StoreNumber><xsl:value-of select = "Order/@EnteredBy" /></StoreNumber>
            <CurrencyCode>USD</CurrencyCode>
            <TransactionTypeCode>s</TransactionTypeCode>
            <TransactionTime><xsl:value-of select="substring($Date,1,19)"/></TransactionTime>
            <TransactionInfoDetails>
			 <xsl:for-each select="Order/OrderLines/OrderLine">
			  <TransactionInfoDetailWS>
			  
			<xsl:variable name="LineTotal">
			<xsl:value-of select = "./LinePriceInfo/@LineTotal" />
			</xsl:variable>
			
			<xsl:variable name="Qty">
			<xsl:value-of select = "@OrderedQty" />
			</xsl:variable>
						 
			  <RecordState>Unchanged</RecordState>
			   <LineNumber><xsl:value-of select = "@PrimeLineNo" /></LineNumber>
                  <ProductCode><xsl:value-of select = "./Item/@ItemID" /></ProductCode>
                  <Quantity><xsl:value-of select = "@OrderedQty" /></Quantity>
                  <NetRetail><xsl:value-of select = "./LinePriceInfo/@RewardTotal" /></NetRetail>
                <DiscountPercent>0</DiscountPercent>
                <CouponCode></CouponCode>
			   </TransactionInfoDetailWS>
			 </xsl:for-each>
			</TransactionInfoDetails>
			<TransactionInfoTenders></TransactionInfoTenders>
			</transactionInfo>
			</CalculateReward>
    </soap:Body>
</soap:Envelope>
	</xsl:template>
</xsl:stylesheet>