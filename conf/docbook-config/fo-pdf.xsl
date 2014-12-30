<?xml version="1.0" encoding="UTF-8"?>
<!--
  Generates a FO document from a DocBook XML document using the DocBook XSL stylesheets.
  See http://docbook.sourceforge.net/release/xsl/1.78.1/doc/fo for all parameters.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:db="http://docbook.org/ns/docbook"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                exclude-result-prefixes="db">
    <!--
      The absolute URL imports point to system-wide locations by way of this /etc/xml/catalog entry:

        <rewriteURI
          uriStartString="http://docbook.sourceforge.net/release/xsl/current"
          rewritePrefix="file:///usr/share/sgml/docbook/xsl-stylesheets-%docbook-style-xsl-version%"/>

      %docbook-style-xsl-version% represents the version installed on the system.
    -->
    <!--<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/fo/docbook.xsl"/>-->
    <xsl:import href="../docbook/fo/docbook.xsl"/>
    <xsl:import href="common.xsl"/>
    <xsl:import href="highlight.xsl"/>
    <xsl:import href="callouts.xsl"/>

    <!-- Enable extensions for FOP version 0.90 and later -->
    <xsl:param name="fop1.extensions">1</xsl:param>

    <!--
      AsciiDoc compat
    -->

    <xsl:template match="processing-instruction('asciidoc-br')">
        <fo:block/>
    </xsl:template>

    <xsl:template match="processing-instruction('asciidoc-hr')">
        <fo:block space-after="1em">
            <fo:leader leader-pattern="rule" rule-thickness="0.5pt" rule-style="solid" leader-length.minimum="100%"/>
        </fo:block>
    </xsl:template>

    <xsl:template match="processing-instruction('asciidoc-pagebreak')">
        <fo:block break-after='page'/>
    </xsl:template>

    <!--
      Font selectors
    -->

    <xsl:template name="pickfont-sans">
        <xsl:text>Arial,sans-serif</xsl:text>
    </xsl:template>

    <xsl:template name="pickfont-serif">
        <xsl:text>Georgia,serif</xsl:text>
    </xsl:template>

    <xsl:template name="pickfont-mono">
        <xsl:text>Liberation Mono,Courier New,Courier,monospace</xsl:text>
    </xsl:template>

    <xsl:template name="pickfont-dingbat">
        <xsl:call-template name="pickfont-sans"/>
    </xsl:template>

    <xsl:template name="pickfont-symbol">
        <xsl:text>Symbol,ZapfDingbats</xsl:text>
    </xsl:template>

    <!--
      Fonts
    -->

    <xsl:param name="body.font.family">
        <xsl:call-template name="pickfont-sans"/>
    </xsl:param>

    <xsl:param name="sans.font.family">
        <xsl:call-template name="pickfont-sans"/>
    </xsl:param>

    <xsl:param name="monospace.font.family">
        <xsl:call-template name="pickfont-mono"/>
    </xsl:param>

    <!--
    <xsl:param name="dingbat.font.family">
      <xsl:call-template name="pickfont-dingbat"/>
    </xsl:param>

    <xsl:param name="symbol.font.family">
      <xsl:call-template name="pickfont-symbol"/>
    </xsl:param>
    -->

    <xsl:param name="title.font.family">
        <xsl:call-template name="pickfont-serif"/>
    </xsl:param>

    <!--
      Text properties
    -->

    <xsl:param name="hyphenate">false</xsl:param>
    <!--
    <xsl:param name="alignment">left</xsl:param>
    -->
    <xsl:param name="alignment">justify</xsl:param>
    <xsl:param name="line-height">1.5</xsl:param>
    <xsl:param name="body.font.master">10</xsl:param>
    <xsl:param name="body.font.size">
        <xsl:value-of select="$body.font.master"/><xsl:text>pt</xsl:text>
    </xsl:param>

    <xsl:attribute-set name="root.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$text.color"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <!-- normal.para.spacing is the only attribute set applied to all paragraphs -->
    <xsl:attribute-set name="normal.para.spacing">
        <xsl:attribute name="space-before.optimum">0</xsl:attribute>
        <xsl:attribute name="space-before.minimum">0</xsl:attribute>
        <xsl:attribute name="space-before.maximum">0</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute>
        <!--
        <xsl:attribute name="color"><xsl:value-of select="$text.color"/></xsl:attribute>
        -->
    </xsl:attribute-set>

    <xsl:attribute-set name="monospace.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$code.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">
            <xsl:value-of select="$code.font-weight"/>
        </xsl:attribute>
        <!--
        <xsl:attribute name="font-size">
          <xsl:value-of select="$body.font.master * 0.9"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
        -->
        <xsl:attribute name="background-color">
            <xsl:value-of select="$code.background-color"/>
        </xsl:attribute>
        <xsl:attribute name="padding">
            <xsl:choose>
                <xsl:when test="$code.background-color != 'transparent'">.3em .25em .1em .25em</xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="verbatim.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$text.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="border-width">1pt</xsl:attribute>
        <xsl:attribute name="border-color">#BFBFBF</xsl:attribute>
        <xsl:attribute name="background-color">#BFBFBF</xsl:attribute>
        <xsl:attribute name="space-before.minimum">0</xsl:attribute>
        <xsl:attribute name="space-before.optimum">.2em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">.4em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">1em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1.2em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.4em</xsl:attribute>
        <xsl:attribute name="hyphenate">false</xsl:attribute>
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
        <xsl:attribute name="white-space-collapse">false</xsl:attribute>
        <xsl:attribute name="white-space-treatment">preserve</xsl:attribute>
        <xsl:attribute name="linefeed-treatment">preserve</xsl:attribute>
        <xsl:attribute name="text-align">start</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="monospace.verbatim.properties"
                       use-attribute-sets="monospace.properties verbatim.properties">

        <!--  <xsl:attribute name="keep-together.within-column">always</xsl:attribute> -->

        <xsl:attribute name="font-size">8pt</xsl:attribute>
        <xsl:attribute name="text-align">start</xsl:attribute>
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
        <!--
        <xsl:attribute name="hyphenation-character">&#x25BA;</xsl:attribute>
        -->
    </xsl:attribute-set>

    <!-- shade.verbatim.style is added to listings when shade.verbatim is enabled -->
    <xsl:param name="shade.verbatim">1</xsl:param>

    <xsl:attribute-set name="shade.verbatim.style">
        <xsl:attribute name="background-color">#FBFBFB</xsl:attribute>
        <!--
        <xsl:attribute name="background-color">
          <xsl:choose>
            <xsl:when test="ancestor::db:note">
              <xsl:text>#D6DEE0</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:caution">
              <xsl:text>#FAF8ED</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:important">
              <xsl:text>#E1EEF4</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:warning">
              <xsl:text>#FAF8ED</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:tip">
              <xsl:text>#D5E1D5</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>#FFF</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        -->
        <xsl:attribute name="color">
            <xsl:value-of select="$text.color"/>
        </xsl:attribute>
        <!--
        <xsl:attribute name="color">
          <xsl:choose>
            <xsl:when test="ancestor::db:note">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:caution">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:important">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:warning">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:tip">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>#222</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        -->
        <xsl:attribute name="padding">1em .5em .75em .5em</xsl:attribute>
        <!-- make sure block it aligns with block title -->
        <xsl:attribute name="margin-left">
            <xsl:value-of select="$title.margin.left"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <!-- Underline and Strikethrough -->

    <xsl:template match="del">
        <fo:inline text-decoration="underline">
          <xsl:call-template name="inline.charseq"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="u">
        <fo:inline text-decoration="line-through">
          <xsl:call-template name="inline.charseq"/>
        </fo:inline>
    </xsl:template>
    
    <!--
      Page layout
    -->

    <xsl:param name="page.height.portrait">10in</xsl:param>
    <xsl:param name="page.width.portrait">7in</xsl:param>

    <xsl:param name="page.margin.inner">1.9cm</xsl:param>
    <xsl:param name="page.margin.outer">1.27cm</xsl:param>

    <xsl:param name="body.margin.inner">1mm</xsl:param>
    <xsl:param name="body.margin.outer">1mm</xsl:param>

    <xsl:param name="page.margin.top">10mm</xsl:param>
    <xsl:param name="body.margin.top">15mm</xsl:param>

    <xsl:param name="region.before.extent">10mm</xsl:param>
    <xsl:param name="region.after.extent">10mm</xsl:param>

    <xsl:param name="page.margin.bottom">10mm</xsl:param>
    <xsl:param name="body.margin.bottom">15mm</xsl:param>

    <xsl:param name="body.start.indent">0</xsl:param>
    <!-- text indentation -->
    <xsl:param name="body.end.indent">0</xsl:param>
    <xsl:param name="double.sided">1</xsl:param>

    <xsl:param name="generate.toc">
        appendix toc,title
        article/appendix nop
        article title
        book toc,title,figure,table,example,equation
        chapter nop
        part toc,title
        preface toc,title
        qandadiv toc
        qandaset toc
        reference toc,title
        sect1 toc
        sect2 toc
        sect3 toc
        sect4 toc
        sect5 toc
        section toc
        set toc,title
    </xsl:param>


    <!--
      Table of Contents
    -->

    <xsl:param name="bridgehead.in.toc">0</xsl:param>
    <xsl:param name="toc.section.depth">2</xsl:param>

    <xsl:template name="toc.line">
        <xsl:variable name="id">
            <xsl:call-template name="object.id"/>
        </xsl:variable>

        <xsl:variable name="label">
            <xsl:apply-templates select="." mode="label.markup"/>
        </xsl:variable>

        <fo:block text-align-last="justify" end-indent="{$toc.indent.width}pt"
                  last-line-end-indent="-{$toc.indent.width}pt">
            <fo:inline keep-with-next.within-line="always">
                <fo:basic-link internal-destination="{$id}" color="#005498">
                    <!-- Chapter titles should be bold. -->
                    <!--
                    <xsl:choose>
                      <xsl:when test="local-name(.) = 'chapter'">
                        <xsl:attribute name="font-weight">bold</xsl:attribute>
                      </xsl:when>
                    </xsl:choose>
                    -->
                    <xsl:if test="$label != ''">
                        <!--
                        <xsl:value-of select="'Chapter '"/>
                        -->
                        <xsl:copy-of select="$label"/>
                        <xsl:value-of select="$autotoc.label.separator"/>
                    </xsl:if>
                    <xsl:apply-templates select="." mode="titleabbrev.markup"/>
                </fo:basic-link>
            </fo:inline>
            <fo:inline keep-together.within-line="always">
                <xsl:text> </xsl:text>
                <fo:leader leader-pattern="dots" leader-pattern-width="3pt"
                           leader-alignment="reference-area"
                           keep-with-next.within-line="always"/>
                <xsl:text> </xsl:text>
                <fo:basic-link internal-destination="{$id}" color="#005498">
                    <fo:page-number-citation ref-id="{$id}"/>
                </fo:basic-link>
            </fo:inline>
        </fo:block>
    </xsl:template>

    <!--
      Blocks
     -->

    <xsl:attribute-set name="formal.object.properties">
        <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute>
        <!-- Make examples, tables etc. break across pages -->
        <xsl:attribute name="keep-together.within-column">auto</xsl:attribute>
    </xsl:attribute-set>
    <!--
        <xsl:param name="formal.title.placement">
            figure after
            example before
            table before
        </xsl:param>
        -->

    <xsl:param name="formal.title.placement">
        figure after
        example before
        equation before
        table before
        procedure before
    </xsl:param>

    <xsl:attribute-set name="formal.title.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$caption.color"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:template match="*" mode="admon.graphic.width">
        <xsl:text>36pt</xsl:text>
    </xsl:template>

    <xsl:attribute-set name="admonition.properties">
        <xsl:attribute name="color">#6F6F6F</xsl:attribute>
        <xsl:attribute name="padding-left">18pt</xsl:attribute>
        <xsl:attribute name="border-left-width">.75pt</xsl:attribute>
        <xsl:attribute name="border-left-style">solid</xsl:attribute>
        <xsl:attribute name="border-left-color">
            <xsl:value-of select="$border.color"/>
        </xsl:attribute>
        <xsl:attribute name="margin-left">0</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="graphical.admonition.properties">
        <xsl:attribute name="margin-left">12pt</xsl:attribute>
        <xsl:attribute name="margin-right">12pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="example.properties" use-attribute-sets="formal.object.properties">
        <xsl:attribute name="border-width">1pt</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">#E6E6E6</xsl:attribute>
        <xsl:attribute name="padding-top">12pt</xsl:attribute>
        <xsl:attribute name="padding-right">12pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">0</xsl:attribute>
        <xsl:attribute name="padding-left">12pt</xsl:attribute>
        <xsl:attribute name="margin-left">0</xsl:attribute>
        <xsl:attribute name="margin-right">0</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="sidebar.properties" use-attribute-sets="formal.object.properties">
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-width">1pt</xsl:attribute>
        <xsl:attribute name="border-color">#D9D9D9</xsl:attribute>
        <xsl:attribute name="background-color">#F2F2F2</xsl:attribute>
        <xsl:attribute name="padding-start">16pt</xsl:attribute>
        <xsl:attribute name="padding-end">16pt</xsl:attribute>
        <xsl:attribute name="padding-top">18pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">0</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="sidebar.title.properties">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.fontset"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">
            <xsl:value-of select="$header.font-weight"/>
        </xsl:attribute>
        <xsl:attribute name="color">
            <xsl:value-of select="$caption.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 1.6"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="margin-bottom">
            <xsl:value-of select="$body.font.master"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
    </xsl:attribute-set>

    <!--
      Tables
    -->

    <xsl:attribute-set name="table.cell.padding">
        <xsl:attribute name="padding-left">4pt</xsl:attribute>
        <xsl:attribute name="padding-right">4pt</xsl:attribute>
        <xsl:attribute name="padding-top">2pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">2pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:param name="table.frame.border.thickness">0.3pt</xsl:param>
    <xsl:param name="table.cell.border.thickness">0.15pt</xsl:param>
    <xsl:param name="table.cell.border.color">#5c5c4f</xsl:param>
    <xsl:param name="table.frame.border.color">#5c5c4f</xsl:param>
    <xsl:param name="table.cell.border.right.color">white</xsl:param>
    <xsl:param name="table.cell.border.left.color">white</xsl:param>
    <xsl:param name="table.frame.border.right.color">white</xsl:param>
    <xsl:param name="table.frame.border.left.color">white</xsl:param>

    <xsl:attribute-set name="table.cell.padding">
        <xsl:attribute name="padding-left">4pt</xsl:attribute>
        <xsl:attribute name="padding-right">4pt</xsl:attribute>
        <xsl:attribute name="padding-top">2pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">2pt</xsl:attribute>
    </xsl:attribute-set>

    <!--
      Graphics
    -->

    <!-- graphicsize.extension only relevant for html output -->
    <!--
    <xsl:param name="graphicsize.extension">1</xsl:param>
    -->
    <xsl:param name="default.image.width">4in</xsl:param>
    <xsl:param name="default.inline.image.height">1em</xsl:param>

    <!--
      Titles
    -->

    <xsl:attribute-set name="section.title.properties">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.fontset"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">
            <xsl:value-of select="$header.font-weight"/>
        </xsl:attribute>
        <xsl:attribute name="color">
            <xsl:value-of select="$title.color"/>
        </xsl:attribute>
        <!-- font size is calculated dynamically by section.heading template -->
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
        <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-before.optimum">1.0em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1.0em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <!-- make sure block it aligns with block title -->
        <xsl:attribute name="start-indent">
            <xsl:value-of select="$title.margin.left"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level1.properties">
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 1.6"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level2.properties">
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 1.4"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level3.properties">
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 1.3"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level4.properties">
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 1.2"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level5.properties">
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 1.1"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level6.properties">
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="component.title.properties">
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
        <xsl:attribute name="space-before.optimum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-before.minimum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-before.maximum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-after.optimum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-after.minimum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-after.maximum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="hyphenate">false</xsl:attribute>
        <xsl:attribute name="font-weight">
            <xsl:value-of select="$header.font-weight"/>
        </xsl:attribute>
        <!-- color support on fo:block, to which this gets applied, added in DocBook XSL 1.78.1 -->
        <xsl:attribute name="color">
            <xsl:value-of select="$title.color"/>
        </xsl:attribute>
        <!--
        <xsl:attribute name="color">
          <xsl:choose>
            <xsl:when test="not(parent::db:chapter | parent::db:article | parent::db:appendix)">
              <xsl:value-of select="$title.color"/>
            </xsl:when>
            <xsl:otherwise>inherit</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        -->
        <xsl:attribute name="text-align">
            <xsl:choose>
                <xsl:when
                        test="((parent::db:article | parent::db:articleinfo) and not(ancestor::db:book) and not(self::db:bibliography)) or (parent::db:slides | parent::db:slidesinfo)">
                    center
                </xsl:when>
                <xsl:otherwise>left</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
        <xsl:attribute name="start-indent">
            <xsl:value-of select="$title.margin.left"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <!-- override to set color -->
    <xsl:template match="db:formalpara/db:title | formalpara/title">
        <xsl:variable name="titleStr">
            <xsl:apply-templates/>
        </xsl:variable>
        <xsl:variable name="lastChar">
            <xsl:if test="$titleStr != ''">
                <xsl:value-of select="substring($titleStr,string-length($titleStr),1)"/>
            </xsl:if>
        </xsl:variable>

        <fo:inline font-weight="bold"
                   color="{$caption.color}"
                   keep-with-next.within-line="always">
            <xsl:copy-of select="$titleStr"/>
            <xsl:if test="$lastChar != ''
                    and not(contains($runinhead.title.end.punct, $lastChar))">
                <xsl:value-of select="$runinhead.default.title.end.punct"/>
            </xsl:if>
        </fo:inline>
    </xsl:template>

    <!--
      Anchors & Links
    -->

    <xsl:attribute-set name="xref.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$link.color"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="simple.xlink.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$link.color"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <!--
      Lists
    -->

    <xsl:param name="qandadiv.autolabel">0</xsl:param>
    <xsl:param name="variablelist.as.blocks">1</xsl:param>

    <xsl:attribute-set name="list.block.properties">
        <xsl:attribute name="margin-left">0.4em</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="list.block.spacing">
        <xsl:attribute name="space-before.optimum">1.2em</xsl:attribute>
        <xsl:attribute name="space-before.minimum">1em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">1.4em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1.2em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">1em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.4em</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="list.item.spacing">
        <xsl:attribute name="space-before.optimum">0.5em</xsl:attribute>
        <xsl:attribute name="space-before.minimum">0.2em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">0.8em</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="variablelist.term.properties">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:template name="itemizedlist.label.markup">
        <xsl:param name="itemsymbol" select="'disc'"/>

        <xsl:choose>
            <xsl:when test="$itemsymbol='none'"></xsl:when>
            <xsl:when test="$itemsymbol='circle'">&#x25E6;</xsl:when>
            <xsl:when test="$itemsymbol='disc'">&#x2022;</xsl:when>
            <xsl:when test="$itemsymbol='square'">&#x25AA;</xsl:when>
            <xsl:when test="$itemsymbol='checked'">&#x25A0;</xsl:when>
            <xsl:when test="$itemsymbol='unchecked'">&#x25A1;</xsl:when>
            <xsl:otherwise>&#x2022;</xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--
      Title pages
    -->

    <xsl:param name="titlepage.color">#6F6F6F</xsl:param>
    <!--
    <xsl:param name="titlepage.color" select="$title.color"/>
    -->

    <xsl:attribute-set name="book.titlepage.recto.style">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.fontset"/>
        </xsl:attribute>
        <xsl:attribute name="color">
            <xsl:value-of select="$titlepage.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="chapter.titlepage.recto.style">
        <xsl:attribute name="color">
            <xsl:value-of select="$chapter.title.color"/>
        </xsl:attribute>
        <xsl:attribute name="background-color">white</xsl:attribute>
        <xsl:attribute name="font-size">24pt</xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <!--xsl:attribute name="wrap-option">no-wrap</xsl:attribute-->
        <!--
        <xsl:attribute name="padding-left">1em</xsl:attribute>
        <xsl:attribute name="padding-right">1em</xsl:attribute>
        -->
    </xsl:attribute-set>

    <xsl:attribute-set name="preface.titlepage.recto.style" use-attribute-sets="chapter.titlepage.recto.style">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.fontset"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="part.titlepage.recto.style">
        <xsl:attribute name="color">
            <xsl:value-of select="$title.color"/>
        </xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
    </xsl:attribute-set>

    <!-- override to set different color for book title -->
    <xsl:template match="db:title | title" mode="book.titlepage.recto.auto.mode">
        <fo:block xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="24.8832pt"
                  space-before="18.6624pt">
            <!-- FIXME don't use hardcoded value here -->
            <xsl:attribute name="color">black</xsl:attribute>
            <xsl:attribute name="font-weight">
                <xsl:value-of select="$header.font-weight"/>
            </xsl:attribute>
            <xsl:call-template name="division.title">
                <xsl:with-param name="node" select="ancestor-or-self::db:book[1] | ancestor-or-self::book[1]"/>
            </xsl:call-template>
        </fo:block>
    </xsl:template>

    <!-- add revision info on title page -->
    <xsl:template match="db:revision | revision" mode="book.titlepage.recto.auto.mode">
        <fo:block xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="14.4pt"
                  space-before="1in" font-family="{$title.fontset}">
            <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'Revision'"/>
            </xsl:call-template>
            <xsl:call-template name="gentext.space"/>
            <xsl:apply-templates select="db:revnumber | revnumber" mode="titlepage.mode"/>
        </fo:block>
        <fo:block xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="14.4pt"
                  font-family="{$title.fontset}">
            <xsl:apply-templates select="db:date | date" mode="titlepage.mode"/>
        </fo:block>
    </xsl:template>

    <!-- override to force use of title, author and one revision on titlepage -->
    <xsl:template name="book.titlepage.recto">
        <xsl:choose>
            <xsl:when test="db:bookinfo/db:title | bookinfo/title">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:bookinfo/db:title | bookinfo/title"/>
            </xsl:when>
            <xsl:when test="db:info/db:title | info/title">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="db:info/db:title | info/title"/>
            </xsl:when>
            <xsl:when test="db:title | title">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="db:title | title"/>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="db:bookinfo/db:subtitle | bookinfo/subtitle">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:bookinfo/db:subtitle | bookinfo/subtitle"/>
            </xsl:when>
            <xsl:when test="db:info/db:subtitle | info/subtitle">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:info/db:subtitle | info/subtitle"/>
            </xsl:when>
            <xsl:when test="db:subtitle | subtitle">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="db:subtitle | subtitle"/>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="db:bookinfo//db:author | bookinfo//author">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:bookinfo//db:author | bookinfo//author"/>
            </xsl:when>
            <xsl:when test="db:info//db:author | info//author">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="db:info//db:author | info//author"/>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="db:bookinfo/db:revhistory/db:revision[1] | bookinfo/revhistory/revision[1]">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:bookinfo/db:revhistory/db:revision[1] | bookinfo/revhistory/revision[1]"/>
            </xsl:when>
            <xsl:when test="db:info/db:revhistory/db:revision[1] | info/revhistory/revision[1]">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:info/db:revhistory/db:revision[1] | info/revhistory/revision[1]"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <!-- cut out these pages -->
    <xsl:template name="book.titlepage.before.verso"/>
    <xsl:template name="book.titlepage.verso"/>

    <!--
      Footnotes
    -->

    <xsl:param name="footnote.number.format">1</xsl:param>
    <xsl:param name="footnote.number.symbols"/>

    <xsl:param name="footnote.font.size">
        <xsl:value-of select="$body.font.master * 0.8"/><xsl:text>pt</xsl:text>
    </xsl:param>

    <xsl:attribute-set name="footnote.mark.properties">
        <!-- override font-family for mark since we don't need full font set -->
        <xsl:attribute name="font-family">
            <xsl:value-of select="$body.font.family"/>
        </xsl:attribute>
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 0.8"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="color">
            <xsl:value-of select="$link.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="padding">0 1pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="footnote.properties">
        <!-- force color since it will otherwise inherit from location of footnote text -->
        <xsl:attribute name="color">
            <xsl:value-of select="$text.color"/>
        </xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="footnote.sep.leader.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$border.color"/>
        </xsl:attribute>
        <xsl:attribute name="leader-pattern">rule</xsl:attribute>
        <xsl:attribute name="leader-length">2in</xsl:attribute>
        <xsl:attribute name="rule-thickness">0.5pt</xsl:attribute>
    </xsl:attribute-set>

    <!-- Index does not use normal.para.spacing, so set text.color explicitly -->
    <!--
    <xsl:attribute-set name="index.div.title.properties">
      <xsl:attribute name="color"><xsl:value-of select="$text.color"/></xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="index.entry.properties">
      <xsl:attribute name="color"><xsl:value-of select="$text.color"/></xsl:attribute>
    </xsl:attribute-set>
    -->

    <!--Centering figure title-->
    <xsl:attribute-set name="formal.title.properties">
        <xsl:attribute name="text-align">
            <xsl:choose>
                <xsl:when test="self::figure">center</xsl:when>
                <xsl:otherwise>left</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:attribute-set>
</xsl:stylesheet>
