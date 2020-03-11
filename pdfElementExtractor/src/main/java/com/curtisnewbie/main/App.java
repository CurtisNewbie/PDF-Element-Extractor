package com.curtisnewbie.main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.curtisnewbie.io.IOManager;
import com.curtisnewbie.pdfprocess.PdfProcessor;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * <p>
 * Main class
 * </p>
 */
public class App {

    static final Logger logger = LoggerProducer.getLogger("App");

    public static void main(String[] args) {
        PdfProcessor pdfProcessor = null;
        try {
            // console
            ConsoleInteraction.displayIntro();
            Parameters param = ConsoleInteraction.getParamFromConsole();

            // load PDF file into memory
            PDDocument doc = IOManager.readPdfFile(param.getFrom());
            pdfProcessor = new PdfProcessor(doc);

            // extract all textual data
            var allText = pdfProcessor.extractText();
            if (allText != null)
                IOManager.writeElementToFile(param.getTo(), allText, "extractedText.txt");
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pdfProcessor != null)
                pdfProcessor.close();
        }
    }
}
