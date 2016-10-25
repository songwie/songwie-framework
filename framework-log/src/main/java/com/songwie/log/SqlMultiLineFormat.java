package com.songwie.log;

import java.io.PrintStream;

import com.p6spy.engine.logging.appender.FormattedLogger;
import com.p6spy.engine.logging.appender.P6Logger;



public class SqlMultiLineFormat extends FormattedLogger implements P6Logger{
	  protected PrintStream qlog;

	  public SqlMultiLineFormat() {
		  qlog = System.out;
	  }
	    
	  public void logException(Exception e) {
		e.printStackTrace(qlog);
	  }

	  public void logText(String text) {
		  text = text.replaceAll("\r|\n", "");
		  qlog.println(text);
		  setLastEntry(text);
	  }
}
 
