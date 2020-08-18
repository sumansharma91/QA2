<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
 <xsl:output method="text" indent="yes" />
 <xsl:template match="*">
All or part of Wholesale Return No.  <xsl:value-of select="//@OrderNo"/> has been received in OMS and is awaiting invoice creation. Please reveiw the receipt details and close the "Wholesale Order - Return Received" alert.
</xsl:template>
</xsl:stylesheet>
