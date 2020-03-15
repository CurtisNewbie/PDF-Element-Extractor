package com.curtisnewbie.pdfprocess;

public class PageRange {

    private int from;
    private int to;

    PageRange(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFromPage() {
        return this.from;
    }

    public int getToPage() {
        return this.to;
    }
}