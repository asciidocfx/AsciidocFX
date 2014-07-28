## Asciidoc Editor based on JavaFX 8

AsciidocFX is editor for [Asciidoc Markup Language](http://www.methods.co.nz/asciidoc/).

It is a basic editor that reflects any changes in editor to Preview Pane immediately.

#### Docbook and PDF support

You can convert your Asciidoc Books to Docbook and PDF with single click.

#### FullScreen Support

Follow the View > Full Screen

<img src="http://kodcu.com/asciidocfx/ascii-full-screen.png" width="700" height="371"/>

#### Closable Preview Pane

Double click to Tab section editor will be expanded, dblclick again will get previous state

#### External Browser Support

Click "External Browser" in Preview Pane, your favorite browser will be opened.

<img src="http://kodcu.com/asciidocfx/ascii-external.png" width="700" height="371"/>

#### Table Generator Feature

Follow the View > Generate Table

<img src="http://kodcu.com/asciidocfx/ascii-table-generator.png" width="700" height="371"/>

#### Closable Tabs

You can close the tab what you want and after right click to any tab, you can close all or other tabs quickly

<img src="http://kodcu.com/asciidocfx/ascii-closable-tabs.png" width="700" height="371"/>

#### Working Directory Section

In the left side of application,you can set your working directory. All listed directories and ASciidoc files will be served there. When you dbl click of any item, it will be opened on Tab section.

<img src="http://kodcu.com/asciidocfx/ascii-working-directory.png" width="700" height="371"/>

### How to Build executable JAR

1. Install JDK 8 and set JAVA_HOME properly
2. Enter AsciidocFX directory and run
    * $ mvn jfx:jar
3. Follow to target/jfx/app directory and run
    * $ java -jar AsciidocFX.jar

### How to Build Native installer (RPM,DKG,DEB,EXE,MSI)
1. If you want to build native installer on your platform
    * Run $ mvn jfx:native
2. Dependencies
    * For Windows
        * Wix 3.0 or Inno Setup 5
    * For Linux
        * RPMBuild or dpkg

#### If you want not to build AsciidocFX, download the Native installers:
<p><b>
<a href="https://github.com/rahmanusta/AsciidocFX/releases/download/v1.0.2/AsciidocFX-1.0.2.msi.zip">AsciidocFX (Windows)</a>
</b></p>
<p><b>
<a href="https://github.com/rahmanusta/AsciidocFX/releases/download/v1.0.2/AsciidocFX-1.0-2.x86_64.rpm">AsciidocFX (Linux)</a>
</b></p>
Note:
* Linux installer installes to /opt dir.
* I dont have Mac, you can build yourself :)


[![Analytics](https://ga-beacon.appspot.com/UA-52823012-1/AsciidocFX/readme)](https://github.com/rahmanusta/AsciidocFX)
