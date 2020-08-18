<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="text" indent="yes"/>
	<xsl:template match="*">
Order No. <xsl:value-of select="//@OrderNo"/> has an invalid Ship To Address. Please review.

Invalid Zip Codes:
<xsl:for-each select="//InvalidZipCode">
			<xsl:value-of select="@Value"/>
			<xsl:text>&#10;</xsl:text>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
