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
 * and so on. It must always be closed when done processing. When using methods
 * to extract data, the page range should be validated using
 * {@link PdfProcessor#validateAndReturnPageRange(int, int)}. If the page range
 * is invalid, there may be unexpected behaviours.
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
        logger.info(String.format("Extracting text from pages %d-%d", from, to));
        try {
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
     * Extract all text in the specified pages and put every N pages (e.g., 1)
     * together into a string.
     * 
     * @param from  from which page (starting at 1)
     * @param to    to which page (inclusive)
     * @param every the string that every N pages should be put together
     * @return all text in the specified pages
     */
    public List<String> extractText(int from, int to, int every) {
        List<String> list = new ArrayList<>();
        if (from != to && to - from + 1 < every) {
            every = to - from + 1;
        }
        if (to == from) {
            list.add(extractText(from, to));
            return list;
        } else {
            for (int i = from; i < to; i += every) {
                // remaining pages
                if (i + every >= to) {
                    list.add(extractText(i, to));
                }
                list.add(extractText(i, i + every));
            }
            return list;
        }
    }

    /**
     * Extract all text in this PDF document, and put every N pages together into a
     * string
     * 
     * @param every the string that every N pages should be put together
     * @return all text in the specified pages
     */
    public List<String> extractText(int every) {
        var pages = pdfDoc.getNumberOfPages();
        return extractText(1, pages, every);
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

    /**
     * <p>
     * Validate if the given page range is valid, if not it returns a valid page
     * range by correcting the invalid values.
     * </p>
     * <p>
     * For example, if the given from page is less than 1, then it returns 1 as a
     * valid number.
     * </p>
     * 
     * @param fromPage
     * @param toPage
     * @return validated page range
     */
    public PageRange validateAndReturnPageRange(int fromPage, int toPage) {
        var pages = pdfDoc.getNumberOfPages();
        if (fromPage <= 0)
            fromPage = 1;
        else if (fromPage > pages)
            fromPage = pages;

        if (toPage > pages || toPage < 0)
            toPage = pages;
        if (toPage < fromPage)
            toPage = fromPage;
        return new PageRange(fromPage, toPage);
    }
}