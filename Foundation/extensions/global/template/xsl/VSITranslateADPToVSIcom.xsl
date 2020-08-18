<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes"/>
	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="@EnterpriseCode">
		<xsl:choose>
			<xsl:when test=". = 'ADP'">
				<xsl:attribute name="EnterpriseCode">
					<xsl:value-of select="'VSI.com'"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="EnterpriseCode">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="@SellerOrganizationCode">
		<xsl:choose>
			<xsl:when test=". = 'ADP'">
				<xsl:attribute name="SellerOrganizationCode">
					<xsl:value-of select="'VSI.com'"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="SellerOrganizationCode">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
