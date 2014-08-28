<?xml version="1.0" encoding="UTF-8"?>
<!--
  This file was extracted from the XSL DocBook Stylesheet distribution.
  It has been customized for the Asciidoctor project (http://asciidoctor.org).
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xslthl="http://xslthl.sf.net"
                exclude-result-prefixes="xslthl">

    <xsl:import href="../docbook/highlighting/common.xsl"/>
    <!--<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/highlighting/common.xsl"/>-->

    <xsl:param name="highlight.source" select="1"/>

    <xsl:template match="xslthl:keyword" mode="xslthl">
        <fo:inline font-weight="bold" color="black">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:string" mode="xslthl">
        <fo:inline color="#DD1144">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:comment" mode="xslthl">
        <fo:inline font-weight="bold" color="#999999">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:tag" mode="xslthl">
        <fo:inline color="navy">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:htmltag" mode="xslthl">
        <fo:inline color="navy">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:attribute" mode="xslthl">
        <fo:inline color="teal">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <!-- value is mapped to an attribute value in XML -->
    <xsl:template match="xslthl:value" mode="xslthl">
        <fo:inline color="#DD1144">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:number" mode="xslthl">
        <fo:inline color="#009999">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:annotation" mode="xslthl">
        <fo:inline color="#000077">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
        <!--
        <fo:inline color="#888888"><xsl:apply-templates mode="xslthl"/></fo:inline>
        -->
    </xsl:template>

    <!-- directive is mapped to a processing instruction in XML -->
    <xsl:template match="xslthl:directive" mode="xslthl">
        <fo:inline font-weight="bold" color="#999999">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:doctype" mode="xslthl">
        <fo:inline font-weight="bold" color="#999999">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:doccomment" mode="xslthl">
        <fo:inline font-style="italic" color="#999999">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:heading" mode="xslthl">
        <fo:inline color="#880000">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:title" mode="xslthl">
        <fo:inline font-weight="bold" color="#880000">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="xslthl:bullet" mode="xslthl">
        <fo:inline color="#008800">
            <xsl:apply-templates mode="xslthl"/>
        </fo:inline>
    </xsl:template>

</xsl:stylesheet>
