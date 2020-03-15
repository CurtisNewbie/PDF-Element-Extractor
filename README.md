# PDF Element Extractor

Program that extracts elements such as img, text from a pdf file. This program is powered by JavaFX and <a href="https://pdfbox.apache.org/">Apache PDFbox</a>. This version has a UI created using JavaFX, the CLI version is available in branch <a href="https://github.com/CurtisNewbie/PDF-Element-Extractor/tree/cli_version">"cli_version"</a>.

### Prerequisite

- Java 11
- Maven (OPTIONAL)

## How To Run It?

Download the UI version in RELEASE, then execute the following command:

    java -jar PDFElementExtractor-1.0.1-UI.jar

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

## Page Range

You do not have to specify the page range. If you leave it empty, the program will extract data from all pages. If you only enter "from" textfield or only "to" textfield or an invalid entry, the program will automatically correct it for you. An invalid entry can be anything that is not a valid Integer or a number that exceeds the correct page range. 

    For example: 
    
    If you enter in invalid string in "from" textfield and a number "10" in "to" textfield as follows: 
    
        Page From "abc" To 10
    
    It will extract data from page 1 to page 10. 

## Demo

<img src="https://user-images.githubusercontent.com/45169791/76706673-391b8100-66e1-11ea-97fd-b014b419b771.gif" height="600">
