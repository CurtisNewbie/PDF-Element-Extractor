package com.curtisnewbie.io;

import java.io.FileNotFoundException;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * 
 * <p>
 * PDF file is not found
 * </p>
 */
public class PdfNotFoundException extends FileNotFoundException {

    private static final long serialVersionUID = 7391760728622800543L;

    public PdfNotFoundException(String path) {
        super("Cannot find your PDF file"
                + (path != null && path.length() > 0 ? " at \"" + path + "\"" : ", you have specified an empty path")
                + ".");
    }

    public PdfNotFoundException() {
        super("We cannot find your PDF file.");
    }

}