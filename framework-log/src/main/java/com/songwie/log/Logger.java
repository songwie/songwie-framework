package com.songwie.log;

public interface Logger {
    public String getName();

    public void trace(String message);

    public void trace(String format, Object... args);

    public boolean isTraceEnabled();

    public void debug(String message);

    public void debug(String format, Object... args);
    
    public boolean isDebugEnabled();

    public void info(String message);

    public void info(String format, Object... args);

    public boolean isInfoEnabled();

    public void warn(String message);

    public void warn(String format, Object... args);

    public boolean isWarnEnabled();

    public void error(String message);

    public void error(String format, Object... args);
    
    public void error(Exception ex);
    
    public void error(String message,Exception ex);

    public boolean isErrorEnabled();
}
