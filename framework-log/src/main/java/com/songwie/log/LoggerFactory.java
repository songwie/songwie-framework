package com.songwie.log;


public class LoggerFactory {
    /**
     * Return logger by name
     *
     * @param name
     * @return Logger
     */
    public static Logger getLogger(String name) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(name);
        return new Slf4jLogger(logger);
    }

    /**
     * Return logger by class
     *
     * @param name
     * @return Logger
     */
    public static Logger getLogger(Class<?> clazz) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(clazz);
        return new Slf4jLogger(logger);
    }
}
