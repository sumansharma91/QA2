<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output version="1.0" indent="yes" encoding="UTF-8" method="xml" />
   <xsl:template match="/">
   <OrderStatusChange>
 
   <xsl:copy-of select="/OrderStatusChange/@*"/>
   
   <xsl:attribute  name = "OrderNo" >
   <xsl:value-of select="''" />
   </xsl:attribute>
    <xsl:attribute  name = "OrderHeaderKey" >
   <xsl:value-of select="/OrderStatusChange/OrderLineList/OrderLine/@OrderHeaderKey" />
   </xsl:attribute>
     <xsl:copy-of select="/OrderStatusChange/*[not(self::OrderLineList)]"/>
     
   <OrderLines>
   <xsl:copy-of select="/OrderStatusChange/OrderLineList/@*"/>
    <xsl:for-each select = "/OrderStatusChange/OrderLineList/OrderLine[number(@OrderedQty) > 0 ]">
  
   <OrderLine>
   <xsl:copy-of select="./@*"/>
      <xsl:attribute  name = "Quantity" >
   <xsl:value-of select="./@OrderedQty" />
   </xsl:attribute>
   </OrderLine>
    </xsl:for-each>
   </OrderLines>
   </OrderStatusChange>
      
   </xsl:template>
</xsl:stylesheet>

