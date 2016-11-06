package it.liferay.symposium.antivirus.clam.client.cli;

public class ClamCliCommands {
	public static final String UPDATE_DEFINITIONS = "freshclam";
	public static final String SCAN = "clamscan";
	
	public static class Options {
		public static final String NO_SUMMARY = "--no-summary";
		public static final String STD_OUT = "--stdout";
	}
}
