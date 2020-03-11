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

    static final Logger logger = LoggerProducer.getLogger(App.class.getName());

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
            var allText = pdfProcessor.extractText(1);
            if (allText != null) {
                int count = 0;
                for (var t : allText)
                    IOManager.writeElementToFile(param.getTo(), t, "page" + (count++) + ".txt");
            }

            // extract all images
            var allImages = pdfProcessor.extractImages();
            if (allImages.size() == 0) {
                logger.info("No image Found");
            } else {
                int count = 0;
                for (var img : allImages) {
                    IOManager.writeElementToFile(param.getTo(), img, "img" + (count++));
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            if (pdfProcessor != null)
                pdfProcessor.close();
        }
    }
}
