<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	<xsl:template match="/">
		<Calendar>
			<xsl:copy-of select = "Calendar/@*"/>
			<xsl:attribute name="OrganizationCode">
				<xsl:value-of select="Calendar/@CalendarId"/>
			</xsl:attribute>
			<xsl:copy-of select = "Calendar/*"/>
		</Calendar>	
	</xsl:template>	
</xsl:stylesheet>