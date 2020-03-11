package com.curtisnewbie.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.awt.image.*;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.curtisnewbie.main.LoggerProducer;

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

    static final Logger logger = LoggerProducer.getLogger(IOManager.class.getName());
    public static final String IMAGE_DIR = "images";
    public static final String TEXT_DIR = "text";

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
     * @param path     path
     * @param text     textual data
     * @param filename filename
     * @throws PdfNotFoundException
     * @throws IOException
     * 
     */
    public static void writeElementToFile(String path, String text, String filename)
            throws IOException, PdfNotFoundException {
        var file = validateAndCreateFile(path);
        var strPath = file.getAbsolutePath();
        var textDir = new File(strPath, TEXT_DIR);
        if (!textDir.exists()) {
            textDir.mkdir();
        }
        var fullPath = Paths.get(textDir.getAbsolutePath(), filename);
        Files.writeString(fullPath, text);
        System.out.println(String.format("Textual data written to \"%s\"", fullPath));
    }

    /**
     * Write image data to local file
     * 
     * @param path     path
     * @param image    image
     * @param filename filename
     * 
     * @throws PdfNotFoundException
     * @throws IOException
     * 
     */
    public static void writeElementToFile(String path, BufferedImage image, String filename)
            throws IOException, PdfNotFoundException {
        var file = validateAndCreateFile(path);
        var strPath = file.getAbsolutePath();
        var textDir = new File(strPath, IMAGE_DIR);
        if (!textDir.exists()) {
            textDir.mkdir();
        }

        // write image to local file
        var fullPath = Paths.get(textDir.getAbsolutePath(), filename);
        ImageIO.write(image, "png", new File(fullPath.toString()));
        System.out.println(String.format("Image data written to \"%s\"", fullPath));
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