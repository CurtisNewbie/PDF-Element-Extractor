package com.curtisnewbie.main;

import java.util.logging.Logger;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * 
 * <p>
 * Producer of Logger
 * </p>
 * 
 */
public class LoggerProducer {

    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }
}