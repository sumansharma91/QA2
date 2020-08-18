<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<ResolutionDetails>
			<Inbox>
				<xsl:attribute name="InboxKey"><xsl:value-of select="/Inbox/@InboxKey" /></xsl:attribute>
			</Inbox>
		</ResolutionDetails>
	</xsl:template>
</xsl:stylesheet>