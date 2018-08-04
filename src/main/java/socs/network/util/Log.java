package socs.network.util;

import java.util.Date;

public class Log {

	private String aLog;

	public Log() {
		aLog = "";
	}

	public synchronized void write(String str) {
		aLog += "\n[" + new Date() + "] " + str;
	}

	@Override
	public String toString() {
		return aLog;
	}
}
