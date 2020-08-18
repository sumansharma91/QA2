<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
 <xsl:output method="text" indent="yes" />
 <xsl:template match="*">
Wholesale order no. <xsl:value-of select="//@OrderNo"/> has been created in OMS. Please verify.

</xsl:template>
</xsl:stylesheet>
