<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output version="1.0" indent="yes" encoding="UTF-8" method="xml" />
   <xsl:template match="/">
   <Order>
   
   
   <xsl:copy-of select="/Order/@*"/>
     <xsl:copy-of select="/Order/*[not(self::OrderAudits or self::OrderLines or self::OrderStatuses or self::AdditionalAddresses)]"/>
   <OrderLines>
   <xsl:copy-of select="/Order/OrderLines/@*"/>
   
   <xsl:for-each select = "/Order/OrderLines/OrderLine[@CustomerPONo = //Order/@CustomerPONo]">
   
   <xsl:attribute  name = "Publish" >
   <xsl:value-of select="'Y'" />
   </xsl:attribute>
   <OrderLine>
   <xsl:copy-of select="./@*"/>
      <xsl:copy-of select="./*[not(self::References or self::KitLines or self::AdditionalAddresses or self::Schedules)]"/>
   </OrderLine>
    </xsl:for-each>
   </OrderLines>
   </Order>
      
   </xsl:template>
</xsl:stylesheet>

