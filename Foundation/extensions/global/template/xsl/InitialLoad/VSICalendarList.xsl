<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
     <Calendar>   
        <xsl:attribute  name = "CalendarId" >
	     <xsl:value-of select="Organization/@OrganizationCode"/>
		 </xsl:attribute>
     </Calendar>
     </xsl:template>
</xsl:stylesheet>