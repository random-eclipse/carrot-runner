package com.carrotgarden.eclipse.space.logger;

import com.carrotgarden.eclipse.space.Plugin;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

/**
 * logback appender which prints to the eclipse console
 */
public class ScriptAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	volatile Encoder<ILoggingEvent> logEncoder;

	void setEncoder(final Encoder<ILoggingEvent> logEncoder) {
		this.logEncoder = logEncoder;
	}

	@Override
	protected void append(final ILoggingEvent logEvent) {
		try {
			logEncoder.doEncode(logEvent);
		} catch (final Exception e) {
			Plugin.log.error("Logback failure.", e);
		}
	}

}
