<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:template match="/">
  <Order>
    <xsl:attribute name="OrderHeaderKey"><xsl:value-of select="/OrderStatusChange/OrderAudit/@OrderHeaderKey" /></xsl:attribute>
</Order>
 </xsl:template>
</xsl:stylesheet>