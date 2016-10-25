package com.songwie.log;

import com.songwie.util.aspect.Request;

public class ErrorLog {
	private static Logger LOGGER;
	
	static{
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ErrorLog.class);
		LOGGER = new Slf4jLogger(logger);
	}

	public void warn(String message) {
		LOGGER.warn(Request.getId() +"-"+ message);
	}

	public void warn(String format, Object... args) {
		LOGGER.warn(Request.getId() +"-"+ format, args);
	}

	public void error(String message) {
		LOGGER.error(Request.getId() +"-"+ message);
	}

	public void error(String format, Object... args) {
		LOGGER.error(Request.getId() +"-"+ format, args);
	}

	public void error(Exception ex) {
		LOGGER.error(Request.getId(), ex);
	}

	public void error(String message, Exception ex) {
		LOGGER.error(Request.getId() +"-"+ message, ex);
	}
}
