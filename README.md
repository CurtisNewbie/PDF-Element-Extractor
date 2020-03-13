# PDF Element Extractor

Program that extracts elements such as img, text from a pdf file. This program is powered by JavaFX and <a href="https://pdfbox.apache.org/">Apache PDFbox</a>. This is a CLI version, the version with UI is available in branch <a href="https://github.com/CurtisNewbie/PDF-Element-Extractor">"master"</a>.

### Prerequisite

- Java 11
- Maven (OPTIONAL)

## How To Run It?

Download the CLI version in RELEASE, then execute the following command:

    java -jar PDFElementExtractor-1.0.0-CLI.jar

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
