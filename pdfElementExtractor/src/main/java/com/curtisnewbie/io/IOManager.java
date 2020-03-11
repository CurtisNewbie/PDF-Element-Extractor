package com.curtisnewbie.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * 
 * <p>
 * Class for handling IO
 * </p>
 */
public class IOManager {

    /**
     * Read local PDF file
     * 
     * @param path
     * @return an in-memory PDDocument representing the PDF file
     * @throws PdfNotFoundException
     * @throws IOException
     */
    public static PDDocument readPdfFile(String path) throws IOException, PdfNotFoundException {
        var file = validateAndCreateFile(path);
        return PDDocument.load(file);
    }

    /**
     * Write textual data to local file
     * 
     * @param path path
     * @param text textual data
     * @throws PdfNotFoundException
     * @throws IOException
     * 
     */
    public static void writeElementToFile(String path, String text) throws IOException, PdfNotFoundException {
        var file = validateAndCreateFile(path);
        Files.writeString(file.toPath(), text);
    }

    static File validateAndCreateFile(String path) throws PdfNotFoundException {
        if (path == null || path.length() == 0)
            throw new PdfNotFoundException();

        var file = new File(path);
        if (!file.exists())
            throw new PdfNotFoundException();
        return file;
    }

}