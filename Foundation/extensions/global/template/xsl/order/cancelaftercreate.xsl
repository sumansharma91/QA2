<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output version="1.0" indent="yes" encoding="UTF-8" method="xml" />
   <xsl:template match="/">
      <Order Action="CANCEL" OrderHeaderKey="{/Order/@OrderHeaderKey}" ModificationReasonCode="ATG" ModificationReasonText="ATG" />
   </xsl:template>
</xsl:stylesheet>

