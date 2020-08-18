<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
      <Order>
      
      <xsl:copy-of select = "/Order/@*"/>
	    <xsl:attribute  name = "DocumentType">0001</xsl:attribute>
	  <xsl:attribute  name = "DraftOrderFlag">N</xsl:attribute>       
	   <xsl:attribute  name = "MaximumRecords">200</xsl:attribute>
	   <xsl:attribute  name = "ReadFromHistory">N</xsl:attribute>
      <OrderLine>
      <xsl:copy-of select = "/Order/OrderLine/@*"/>
      
      </OrderLine>
      </Order>
            
   </xsl:template>
</xsl:stylesheet>