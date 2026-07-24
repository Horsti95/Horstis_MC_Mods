package com.horsti.core.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HorstiLog {
	private static final Logger LOGGER = LoggerFactory.getLogger("horsti");

	private HorstiLog() {
	}

	public static void info(String msg) {
		LOGGER.info(msg);
	}

	public static void warn(String msg) {
		LOGGER.warn(msg);
	}
}
