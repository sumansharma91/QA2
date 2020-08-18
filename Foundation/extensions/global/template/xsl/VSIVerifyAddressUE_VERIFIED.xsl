<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template match="*">
		<xsl:element name="PersonInfoList">
			<xsl:attribute name="ProceedWithSingleAVSResult"><xsl:value-of select="'Y'"/></xsl:attribute>
			<xsl:attribute name="TotalNumberOfRecords"><xsl:value-of select="count(//PersonInfo)"/></xsl:attribute>
			<xsl:element name="PersonInfo">
				<xsl:copy-of select="//PersonInfo/@*"/>
				<xsl:attribute name="IsAddressVerified"><xsl:value-of select="'Y'"/></xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
