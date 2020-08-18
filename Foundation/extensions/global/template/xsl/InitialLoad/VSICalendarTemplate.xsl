<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<VSICal>
<xsl:attribute name="TotalNumberOfRecords" >
<xsl:value-of select="Calendars/@TotalNumberOfRecords"/>
</xsl:attribute>
<xsl:attribute name="CalendarKey" >
<xsl:value-of select="Calendars/Calendar/@CalendarKey"/>
</xsl:attribute>
</VSICal>

	</xsl:template>
</xsl:stylesheet>