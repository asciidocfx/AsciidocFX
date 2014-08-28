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

### How to Run AsciidocFX (For Users)

1. Firstly, install  [JRE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
2. Download the [AsciidocFX.jar](https://github.com/rahmanusta/AsciidocFX/releases/download/v1.0.4/AsciidocFX.jar) file
3. Run
    * java -jar AsciidocFX.jar

### How to Build executable JAR (For Developers)

1. Install [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and set JAVA_HOME properly
2. Install [Apache Maven](http://maven.apache.org/download.cgi) and set /bin directory to environment variables
3. Enter AsciidocFX directory and run
    * $ mvn clean install
4. Follow to target/ directory and run
    * $ java -jar AsciidocFX.jar

[![Analytics](https://ga-beacon.appspot.com/UA-52823012-1/AsciidocFX/readme)](https://github.com/rahmanusta/AsciidocFX)
