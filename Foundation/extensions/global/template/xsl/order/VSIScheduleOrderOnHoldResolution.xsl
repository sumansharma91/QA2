<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
     
    <ScheduleOrder CheckInventory="N" OrderHeaderKey="{HoldType/OrderLines/OrderLine/@OrderHeaderKey}"/>
	
     </xsl:template>
</xsl:stylesheet>