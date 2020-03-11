package com.curtisnewbie.pdfprocess;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.image.*;

import com.curtisnewbie.main.LoggerProducer;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
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
     * Extract all images in the PDF document
     * 
     * @return all images in a List
     */
    public List<BufferedImage> extractImages() {
        var pages = pdfDoc.getNumberOfPages();
        return extractImages(1, pages);
    }

    /**
     * Extract all images in the specified pages
     * 
     * @param from from which page (starting at 1)
     * @param to   to which page (inclusive)
     * @return all images in the specified pages
     */
    public List<BufferedImage> extractImages(int from, int to) {
        logger.info(String.format("Extracting images from pages %d-%d", from, to));
        var pages = pdfDoc.getNumberOfPages();
        if (from <= 0 || to > pages) {
            return null;
        }

        List<BufferedImage> images = new ArrayList<>();
        for (int i = from; i < to; i++) {
            var currPage = pdfDoc.getPage(i);
            images.addAll(extractImagesInPage(currPage));
        }
        return images;
    }

    private List<BufferedImage> extractImagesInPage(PDPage page) {
        List<BufferedImage> images = new ArrayList<>();
        PDResources resources = page.getResources();
        images.addAll(extractImagesInResources(resources));
        return images;
    }

    private List<BufferedImage> extractImagesInResources(PDResources resources) {
        List<BufferedImage> images = new ArrayList<>();
        // iterate the external objects (e.g., images) in this resources of this page
        for (COSName objName : resources.getXObjectNames()) {
            try {
                PDXObject obj = resources.getXObject(objName);
                if (obj instanceof PDFormXObject)
                    // nested xObject, do recursion
                    images.addAll(extractImagesInResources(((PDFormXObject) obj).getResources()));
                else if (obj instanceof PDImageXObject)
                    images.add(((PDImageXObject) obj).getImage());
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error when extracting image in page, skipping...");
            }
        }
        return images;
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