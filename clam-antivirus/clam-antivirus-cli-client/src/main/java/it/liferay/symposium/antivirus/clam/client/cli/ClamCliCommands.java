package it.liferay.symposium.antivirus.clam.client.cli;

public class ClamCliCommands {
	public static final String UPDATE_DEFINITIONS = "C:\\AV\\ClamAV-x64\\freshclam.exe";
	public static final String SCAN = "C:\\AV\\ClamAV-x64\\clamscan.exe";
	
	public static class Options {
		public static final String NO_SUMMARY = "--no-summary";
		public static final String STD_OUT = "--stdout";
	}
}
