package com.curtisnewbie.pdfprocess;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.curtisnewbie.main.LoggerProducer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * 
 * <p>
 * Class that is responsible for processing the PDF file, e.g., extracting text
 * and so on. It must always be closed when done processing.
 * </p>
 * 
 */
public class PdfProcessor {

    private PDDocument pdfDoc;

    private Logger logger = LoggerProducer.getLogger(this.getClass().getName());

    public PdfProcessor(PDDocument pdfDoc) throws NullPointerException {
        this.pdfDoc = pdfDoc;
    }

    /**
     * Extract all text in the specified pages
     * 
     * @param from from which page (starting at 1)
     * @param to   to which page (inclusive)
     * @return all text in the specified pages
     */
    public String extractText(int from, int to) {
        try {
            var pages = pdfDoc.getNumberOfPages();
            if (from <= 0 || to > pages) {
                return null;
            }

            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(from);
            textStripper.setEndPage(to);
            Writer writer = new CharArrayWriter();
            textStripper.writeText(pdfDoc, writer);
            return writer.toString();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    /**
     * Extract all text in the PDF document
     * 
     * @return all text in the PDF document
     */
    public String extractText() {
        var pages = pdfDoc.getNumberOfPages();
        return extractText(1, pages);
    }

    /**
     * Close the internal {@code PDDocument}
     */
    public void close() {
        try {
            this.pdfDoc.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

}