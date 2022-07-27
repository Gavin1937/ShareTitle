package Gavin1937.ShareTitle.Util;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.ConsoleAppender;

import org.slf4j.Marker;


public class MyLogger
{
    
    public static void init(String path)
    {
        if (__logger != null)
            __logger.detachAndStopAllAppenders();
        
        __path = path;
        
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        
        ple.setPattern(__pattern);
        ple.setContext(lc);
        ple.start();
        
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setName("fileAppender");
        fileAppender.setFile(__path);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.start();
        
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setName("consoleAppender");
        consoleAppender.setEncoder(ple);
        consoleAppender.setContext(lc);
        consoleAppender.start();
        
        Logger logger = (Logger) LoggerFactory.getLogger("MyLogger");
        logger.addAppender(fileAppender);
        logger.addAppender(consoleAppender);
        logger.setLevel(__level);
        logger.setAdditive(false); /* set to true if root should log too */
        
        __logger = logger;
    }
    
    public static void init(String path, Level level)
    {
        __level = level;
        init(path);
    }
    
    public static Logger getLogger()
    {
        return __logger;
    }
    
    public static Level getLevel()
    {
        return __level;
    }
    
    public static String getPattern()
    {
        return __pattern;
    }

    public static String getPath()
    {
        return __path;
    }
    
    public static void setLevel(Level level)
    {
        __level = level;
        __logger.setLevel(__level);
    }
    
    public static void setPattern(String pattern)
    {
        __pattern = pattern;
        
        // reset all appenders with new __pattern
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        
        ple.setPattern(__pattern);
        ple.setContext(lc);
        ple.start();

        FileAppender<ILoggingEvent> fileAppender =
            (FileAppender<ILoggingEvent>)__logger.getAppender("fileAppender");
        ConsoleAppender<ILoggingEvent> consoleAppender =
            (ConsoleAppender<ILoggingEvent>)__logger.getAppender("consoleAppender");
        
        fileAppender.setContext(lc);
        fileAppender.setEncoder(ple);
        
        consoleAppender.setContext(lc);
        consoleAppender.setEncoder(ple);
        
        __logger.detachAndStopAllAppenders();
        
        __logger.addAppender(fileAppender);
        __logger.addAppender(consoleAppender);
    }
    
    
    // expose logging interfaces
    
    public static void trace(String msg) { __logger.trace(msg); }
    public static void trace(String format, Object arg) { __logger.trace(format, arg); }
    public static void trace(String format, Object arg1, Object arg2) { __logger.trace(format, arg1, arg2); }
    public static void trace(String format, Object... argArray) { __logger.trace(format, argArray); }
    public static void trace(String msg, Throwable t) { __logger.trace(msg, t); }
    public static void trace(Marker marker, String msg) { __logger.trace(marker, msg); }
    public static void trace(Marker marker, String format, Object arg) { __logger.trace(marker, format, arg); }
    public static void trace(Marker marker, String format, Object arg1, Object arg2) { __logger.trace(marker, format, arg1, arg2); }
    public static void trace(Marker marker, String format, Object... argArray) { __logger.trace(marker, format, argArray); }
    public static void trace(Marker marker, String msg, Throwable t) { __logger.trace(marker, msg, t); }
    
    public static void debug(String msg) { __logger.debug(msg); }
    public static void debug(String format, Object arg) { __logger.debug(format, arg); }
    public static void debug(String format, Object arg1, Object arg2) { __logger.debug(format, arg1, arg2); }
    public static void debug(String format, Object... argArray) { __logger.debug(format, argArray); }
    public static void debug(String msg, Throwable t) { __logger.debug(msg, t); }
    public static void debug(Marker marker, String msg) { __logger.debug(marker, msg); }
    public static void debug(Marker marker, String format, Object arg) { __logger.debug(marker, format, arg); }
    public static void debug(Marker marker, String format, Object arg1, Object arg2) { __logger.debug(marker, format, arg1, arg2); }
    public static void debug(Marker marker, String format, Object... argArray) { __logger.debug(marker, format, argArray); }
    public static void debug(Marker marker, String msg, Throwable t) { __logger.debug(marker, msg, t); }
    
    public static void info(String msg) { __logger.info(msg); }
    public static void info(String format, Object arg) { __logger.info(format, arg); }
    public static void info(String format, Object arg1, Object arg2) { __logger.info(format, arg1, arg2); }
    public static void info(String format, Object... argArray) { __logger.info(format, argArray); }
    public static void info(String msg, Throwable t) { __logger.info(msg, t); }
    public static void info(Marker marker, String msg) { __logger.info(marker, msg); }
    public static void info(Marker marker, String format, Object arg) { __logger.info(marker, format, arg); }
    public static void info(Marker marker, String format, Object arg1, Object arg2) { __logger.info(marker, format, arg1, arg2); }
    public static void info(Marker marker, String format, Object... argArray) { __logger.info(marker, format, argArray); }
    public static void info(Marker marker, String msg, Throwable t) { __logger.info(marker, msg, t); }
    
    public static void warn(String msg) { __logger.warn(msg); }
    public static void warn(String format, Object arg) { __logger.warn(format, arg); }
    public static void warn(String format, Object arg1, Object arg2) { __logger.warn(format, arg1, arg2); }
    public static void warn(String format, Object... argArray) { __logger.warn(format, argArray); }
    public static void warn(String msg, Throwable t) { __logger.warn(msg, t); }
    public static void warn(Marker marker, String msg) { __logger.warn(marker, msg); }
    public static void warn(Marker marker, String format, Object arg) { __logger.warn(marker, format, arg); }
    public static void warn(Marker marker, String format, Object arg1, Object arg2) { __logger.warn(marker, format, arg1, arg2); }
    public static void warn(Marker marker, String format, Object... argArray) { __logger.warn(marker, format, argArray); }
    public static void warn(Marker marker, String msg, Throwable t) { __logger.warn(marker, msg, t); }
    
    public static void error(String msg) { __logger.error(msg); }
    public static void error(String format, Object arg) { __logger.error(format, arg); }
    public static void error(String format, Object arg1, Object arg2) { __logger.error(format, arg1, arg2); }
    public static void error(String format, Object... argArray) { __logger.error(format, argArray); }
    public static void error(String msg, Throwable t) { __logger.error(msg, t); }
    public static void error(Marker marker, String msg) { __logger.error(marker, msg); }
    public static void error(Marker marker, String format, Object arg) { __logger.error(marker, format, arg); }
    public static void error(Marker marker, String format, Object arg1, Object arg2) { __logger.error(marker, format, arg1, arg2); }
    public static void error(Marker marker, String format, Object... argArray) { __logger.error(marker, format, argArray); }
    public static void error(Marker marker, String msg, Throwable t) { __logger.error(marker, msg, t); }
    
    
    // private members
    private static Logger __logger = null;
    private static Level __level = Level.INFO;
    private static String __pattern = "[%yellow(%date)][%boldBlue(%-5level)] - %msg%n";
    private static String __path = null;
}

