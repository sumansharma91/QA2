<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/TimeInTransitList/TimeInTransit">
	<ExtnTimeInTransit >
		<xsl:attribute name="ExtnCarrier">
			<xsl:value-of select="@Carrier" />
		</xsl:attribute>
		<xsl:attribute name="ExtnCarrierServiceCode">
			<xsl:value-of select="@CarrierServiceCode" />
		</xsl:attribute>
		<xsl:attribute name="ExtnShipFromZipCode">
			<xsl:value-of select="@ShipFromZipCode" />
		</xsl:attribute>
		<xsl:attribute name="ExtnShipToZipCode">
			<xsl:value-of select="@ShipToZipCode" />
		</xsl:attribute>
		<xsl:attribute name="ExtnTimeInTransit">
			<xsl:value-of select="@TimeInTransit" />
		</xsl:attribute>
	</ExtnTimeInTransit>
	</xsl:template>
</xsl:stylesheet>