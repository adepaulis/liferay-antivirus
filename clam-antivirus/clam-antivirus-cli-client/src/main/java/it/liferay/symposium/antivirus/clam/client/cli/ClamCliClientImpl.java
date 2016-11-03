package it.liferay.symposium.antivirus.clam.client.cli;

import java.io.File;
import java.io.IOException;

import org.osgi.service.component.annotations.Component;

import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import it.liferay.symposium.antivirus.api.AntivirusScannerResult;
import it.liferay.symposium.antivirus.clam.client.api.ClamClient;

@Component(
		service = ClamClient.class
	)
public class ClamCliClientImpl implements ClamClient{
	
	public AntivirusScannerResult scan(File file) throws PortalException{
		
		try{
			if(_log.isInfoEnabled())
				_log.info("Executing scan command...");
				
			int exitValue = executeCommand(
				ClamCliCommands.SCAN, 
				ClamCliCommands.Options.STD_OUT,
				ClamCliCommands.Options.NO_SUMMARY, 
				file.getAbsolutePath());
			
			return parseScanResult(exitValue);
		}
		catch(IOException |InterruptedException e){
			throw new PortalException(e);
		}
	}
	
	public void updateDefinitions() throws PortalException{
		try{
			executeCommand(ClamCliCommands.UPDATE_DEFINITIONS);
		}
		catch(IOException |InterruptedException e){
			throw new PortalException(e);
		}
	}
	
	private int executeCommand(String... params) 
		throws IOException, InterruptedException{
		Process process = null;

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(params);

			processBuilder.redirectErrorStream(true);

			process = processBuilder.start();

			process.waitFor();

			int exitValue = process.exitValue();
			
			return exitValue;
		}
		finally {
			if (process != null) {
				process.destroy();
			}
		}
	}
	
	private AntivirusScannerResult parseScanResult(int exitValue) 
		throws PortalException{
		
		if (exitValue == 1) {
			return AntivirusScannerResult.VIRUS_DETECTED;
		}
		else if (exitValue >= 2) {
			throw new AntivirusScannerException(
				AntivirusScannerException.PROCESS_FAILURE);
		}
		else{
			return AntivirusScannerResult.FILE_CLEAN;
		}
	}
	
	private static Log _log =
			LogFactoryUtil.getLog(ClamCliClientImpl.class);
}
