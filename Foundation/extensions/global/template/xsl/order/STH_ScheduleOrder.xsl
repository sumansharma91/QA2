<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

   <xsl:template match="/">
		<Order>
			<xsl:attribute name="CheckInventory">N</xsl:attribute>
			<xsl:attribute name="ScheduleAndRelease">Y</xsl:attribute>
			<xsl:attribute name="IgnoreReleaseDate">Y</xsl:attribute>
			<xsl:attribute name="OrderHeaderKey"><xsl:value-of select="Order/@OrderHeaderKey" /></xsl:attribute>
		</Order>	
   </xsl:template>
</xsl:stylesheet>                       