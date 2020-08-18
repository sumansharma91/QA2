<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/">
     <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.svsxml.svs.com" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
   <soapenv:Header>
       <wsse:Security soapenv:mustUnderstand="0">
          <wsse:UsernameToken>
             <wsse:Username>vitshop-uat</wsse:Username>
             <wsse:Password>C3n^rum!U@t</wsse:Password>
           </wsse:UsernameToken>
        </wsse:Security>
   </soapenv:Header>
   <soapenv:Body>
      <ser:balanceInquiry> 
         <request>
         <amount>
               <amount><xsl:value-of select = "/GiftCard/@amount"/></amount>
               <currency>USD</currency>
            </amount>
            <card>
               <cardCurrency>840</cardCurrency>
               <cardNumber><xsl:value-of select = "/GiftCard/@cardNumber"/></cardNumber>
               <pinNumber><xsl:value-of select = "/GiftCard/@pinNumber"/></pinNumber>
               <cardExpiration></cardExpiration>
               <cardTrackOne></cardTrackOne>
               <cardTrackTwo></cardTrackTwo>
               <eovDate></eovDate>
            </card>
            <merchant>
               <merchantName>Vitaminshoppe</merchantName>
               <merchantNumber>066893</merchantNumber>
               <storeNumber><xsl:value-of select = "/GiftCard/@storeNumber"/></storeNumber>
               <division></division>
            </merchant>
            <routingID>6006492605250000000</routingID>
            <stan></stan>
            <transactionID><xsl:value-of select = "/GiftCard/@transactionID"/></transactionID>
            <checkForDuplicate>TRUE</checkForDuplicate>
            <date><xsl:value-of select = "/GiftCard/@date"/></date>
            <invoiceNumber><xsl:value-of select = "/GiftCard/@invoiceNumber"/></invoiceNumber>
         </request>
      </ser:balanceInquiry> 
   </soapenv:Body>
</soapenv:Envelope>
      </xsl:template>
</xsl:stylesheet>