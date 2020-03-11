package com.curtisnewbie.main;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * <p>
 * Object that stores the input and output file paths
 * </p>
 */
public class Parameters {

    private String from;
    private String to;

    Parameters(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }
}