# PDF Element Extractor

Program that extracts elements such as img, text from a pdf file. This program is powered by JavaFX and <a href="https://pdfbox.apache.org/">Apache PDFbox</a>. This version has a UI created using JavaFX, the CLI version is available in branch <a href="https://github.com/CurtisNewbie/PDF-Element-Extractor/tree/cli_version">"cli_version"</a>.

### Prerequisite

- Java 11
- Maven (OPTIONAL)

## How To Run It?

Download the UI version in RELEASE, then execute the following command:

    java -jar PDFElementExtractor-1.0.0-UI.jar

## Where Are The Extracted Data?

All extracted data are placed under your specified directory (if it is valid). Two directories (`images/` and `text/`) are created for classification as follows:

    -----
        |
        |
        |
        | yourSpecifiedDirectory/
                                |
                                |
                                | images/
                                |       |_ img1
                                |       |_ img2
                                |
                                |
                                | text/
                                      |
                                      |_ page1.txt

## Demo

<img src="https://user-images.githubusercontent.com/45169791/76635690-be673000-653f-11ea-8eb1-9fa4482c69a2.gif">
