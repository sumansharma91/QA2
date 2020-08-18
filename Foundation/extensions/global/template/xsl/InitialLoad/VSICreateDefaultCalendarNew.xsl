<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
     <Calendar>   
        <xsl:attribute  name = "CalendarId" >
	     <xsl:value-of select="Organization/@OrganizationCode"/>
		 </xsl:attribute>
		 <xsl:attribute  name = "CalendarKey" >
	     <xsl:value-of select="Organization/@OrganizationCode"/>
		 </xsl:attribute>
		 		 <xsl:attribute  name = "OrganizationCode">
				 <xsl:value-of select="Organization/@OrganizationCode"/>
				 </xsl:attribute>
				 <EffectivePeriods>
				 <EffectivePeriod>
				 <xsl:attribute  name = "EffectiveFromDate" >2000-01-01</xsl:attribute>
		 <xsl:attribute  name = "EffectiveToDate" >2099-12-31</xsl:attribute>
				 </EffectivePeriod>
				 </EffectivePeriods>
     </Calendar>
     </xsl:template>
</xsl:stylesheet>
