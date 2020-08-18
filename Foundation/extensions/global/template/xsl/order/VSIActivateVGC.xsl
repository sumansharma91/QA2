<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
     <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.svsxml.svs.com" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
   <soapenv:Header>
       <wsse:Security soapenv:mustUnderstand="0">
          <wsse:UsernameToken>
             <wsse:Username><xsl:value-of select = "/VSIOnlineGiftCard/@Username"/></wsse:Username>
             <wsse:Password><xsl:value-of select = "/VSIOnlineGiftCard/@Password"/></wsse:Password>
           </wsse:UsernameToken>
        </wsse:Security>
   </soapenv:Header>
   <soapenv:Body>
      <ser:issueGiftCard>
         <request>
            <card>
 
               <cardNumber><xsl:value-of select = "/VSIOnlineGiftCard/@GiftCardNo"/></cardNumber>
               <pinNumber><xsl:value-of select = "/VSIOnlineGiftCard/@PinNo"/></pinNumber>
               
 				<xsl:choose>
		         <xsl:when test="/VSIOnlineGiftCard/@TrackData1">
		           <cardTrackOne><xsl:value-of select = "/VSIOnlineGiftCard/@TrackData1"/></cardTrackOne>
		         </xsl:when>
		         <xsl:otherwise>
		           <cardTrackOne></cardTrackOne>
		         </xsl:otherwise>
		       </xsl:choose>
		       
		       <xsl:choose>
		         <xsl:when test="/VSIOnlineGiftCard/@TrackData2">
		           <cardTrackTwo><xsl:value-of select = "/VSIOnlineGiftCard/@TrackData2"/></cardTrackTwo>
		         </xsl:when>
		         <xsl:otherwise>
		           <cardTrackTwo></cardTrackTwo>
		         </xsl:otherwise>
		       </xsl:choose>
 
            </card>
            <date>
<xsl:value-of select = "/VSIOnlineGiftCard/@dateToday"/>
</date>
            <invoiceNumber><xsl:value-of select = "/VSIOnlineGiftCard/@InvoiceNo"/></invoiceNumber>
                        <issueAmount>
                        <amount><xsl:value-of select = "/VSIOnlineGiftCard/@RefundAmount"/></amount>
                        <currency>USD</currency>
                        </issueAmount>
            <merchant>
               <merchantName>Vitaminshoppe</merchantName>
               <merchantNumber><xsl:value-of select = "/VSIOnlineGiftCard/@merchantNumber"/></merchantNumber>
               <storeNumber><xsl:value-of select = "/VSIOnlineGiftCard/@StoreNo"/></storeNumber>
               <division>0000</division>
            </merchant>
 
            <routingID><xsl:value-of select = "/VSIOnlineGiftCard/@routingID"/></routingID>
            <stan></stan>
            <transactionID><xsl:value-of select = "/VSIOnlineGiftCard/@TransactionID"/></transactionID>
            <checkForDuplicate>TRUE</checkForDuplicate>
         </request>
      </ser:issueGiftCard>
   </soapenv:Body>
</soapenv:Envelope>
     </xsl:template>
</xsl:stylesheet>