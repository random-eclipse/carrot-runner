package com.carrotgarden.eclipse.space.logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.carrotgarden.eclipse.space.Plugin;
import com.carrotgarden.eclipse.space.pref.MainPage;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * expose slf4j logger for script plugin which prints to runner plugin console
 */
public class ScriptLogging {

	static final String NAME = Plugin.PLUGIN_NAME;

	static volatile LoggerContext context;

	static volatile Logger logger;

	public static void initialize() {
		try {

			context = new LoggerContext();

			final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
			encoder.setContext(context);
			encoder.init(Plugin.console().out);
			encoder.setImmediateFlush(true);
			encoder.setPattern(MainPage.logbackPattern());
			encoder.start();

			final ScriptAppender appender = new ScriptAppender();
			appender.setContext(context);
			appender.setName(ScriptLogging.NAME);
			appender.setEncoder(encoder);
			appender.start();

			logger = context.getLogger(ScriptLogging.NAME);
			logger.addAppender(appender);
			logger.setLevel(ScriptLogging.level(MainPage.logbackLevel()));

			serviceRegister();

		} catch (final Throwable e) {
			Plugin.log.error("Logback failure", e);
		}
	}

	public static void terminate() {
		try {
			servivceUnregister();
			logger = null;
			context = null;
		} catch (final Throwable e) {
			Plugin.log.error("Logback failure", e);
		}
	}

	static Level level(final String name) {
		switch (name.toLowerCase()) {
		case "debug":
			return Level.DEBUG;
		case "info":
			return Level.INFO;
		case "warn":
			return Level.WARN;
		case "error":
			return Level.ERROR;
		default:
			return Level.ALL;
		}
	}

	static volatile ServiceRegistration<?> loggerRegistration;

	static void serviceRegister() {
		BundleContext context = Plugin.instance().context();
		String face = org.slf4j.Logger.class.getName();
		org.slf4j.Logger service = logger;
		loggerRegistration = context.registerService(face, service, null);
	}

	static void servivceUnregister() {
		loggerRegistration.unregister();
	}

}
