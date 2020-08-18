<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
     <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.svsxml.svs.com" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
   <soapenv:Header>
       <wsse:Security soapenv:mustUnderstand="0">
          <wsse:UsernameToken>
             <wsse:Username><xsl:value-of select = "/GiftCard/@Username"/></wsse:Username>
             <wsse:Password><xsl:value-of select = "/GiftCard/@Password"/></wsse:Password>
           </wsse:UsernameToken>
        </wsse:Security>
   </soapenv:Header>
   <soapenv:Body>
      <ser:preAuth>
         <request>
            <card>
               <cardCurrency>840</cardCurrency>
               <cardNumber><xsl:value-of select = "/GiftCard/@cardNumber"/></cardNumber>
               <pinNumber><xsl:value-of select = "/GiftCard/@pinNumber"/></pinNumber>
               <cardExpiration></cardExpiration>
               <cardTrackOne></cardTrackOne>
               <cardTrackTwo></cardTrackTwo>
               <eovDate></eovDate>
            </card>
            <date><xsl:value-of select = "/GiftCard/@date"/></date>
            <invoiceNumber><xsl:value-of select = "/GiftCard/@invoiceNumber"/></invoiceNumber>
            <merchant>
               <merchantName>Vitaminshoppe</merchantName>
               <merchantNumber><xsl:value-of select = "/GiftCard/@merchantNumber"/></merchantNumber>
               <storeNumber><xsl:value-of select = "/GiftCard/@storeNumber"/></storeNumber>
               <division></division>
            </merchant>
            <requestedAmount>
               <amount><xsl:value-of select = "/GiftCard/@amount"/></amount>
               <currency>USD</currency>
            </requestedAmount>
            <routingID><xsl:value-of select = "/GiftCard/@routingID"/></routingID>
            <stan></stan>
            <transactionID><xsl:value-of select = "/GiftCard/@transactionID"/></transactionID>
            <checkForDuplicate>TRUE</checkForDuplicate>
         </request>
      </ser:preAuth>
   </soapenv:Body>
</soapenv:Envelope>
     </xsl:template>
</xsl:stylesheet>