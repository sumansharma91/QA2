<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/">
<xsl:for-each select="Cities/City">
<VSICountyData>
<xsl:attribute name="ExtnCity">
<xsl:value-of select="@City"/>
</xsl:attribute>
<xsl:attribute name="ExtnState">
<xsl:value-of select="@State"/>
</xsl:attribute>
<xsl:attribute name="ExtnZipCode">
<xsl:value-of select="@ZipCode"/>
</xsl:attribute>
<xsl:attribute name="ExtnCounty">
<xsl:value-of select="@County"/>
</xsl:attribute>
<xsl:attribute name="ExtnCountry">
<xsl:choose>
<xsl:when test="@Country='U'">US</xsl:when>
<xsl:when test="@Country='C'">CA</xsl:when>
<xsl:when test="@Country != 'C' and @Country != 'U'"><xsl:value-of select="@Country"/></xsl:when>
</xsl:choose>
</xsl:attribute>
</VSICountyData>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>