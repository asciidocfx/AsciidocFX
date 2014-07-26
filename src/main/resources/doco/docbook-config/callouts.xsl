<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!--
      Callouts
    -->

    <xsl:param name="callouts.extension">1</xsl:param>
    <xsl:param name="callout.defaultcolumn">60</xsl:param>
    <xsl:param name="callout.icon.size">9pt</xsl:param>
    <xsl:param name="callout.graphics">1</xsl:param>
    <xsl:param name="callout.graphics.number.limit">10</xsl:param>
    <xsl:param name="callout.graphics.extension">.svg</xsl:param>
    <!--
    <xsl:param name="callout.graphics.extension">.png</xsl:param>
    -->
    <!--
    <xsl:param name="callout.list.table">1</xsl:param>
    -->

    <xsl:param name="callout.graphics.path">
        <xsl:if test="$img.src.path != ''">
            <xsl:value-of select="$img.src.path"/>
        </xsl:if>
        <xsl:text>images/icons/callouts/</xsl:text>
    </xsl:param>

</xsl:stylesheet>
