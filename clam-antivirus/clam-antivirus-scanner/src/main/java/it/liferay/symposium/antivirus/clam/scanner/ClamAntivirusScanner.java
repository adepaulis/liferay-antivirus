package it.liferay.symposium.antivirus.clam.scanner;

import java.io.File;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import it.liferay.symposium.antivirus.api.AntivirusScannerResult;
import it.liferay.symposium.antivirus.api.AntivirusScannerService;
import it.liferay.symposium.antivirus.clam.client.api.ClamClient;

@Component(
	immediate = true,
	service = AntivirusScannerService.class
)
public class ClamAntivirusScanner implements AntivirusScannerService {

	@Override
	public AntivirusScannerResult scan(File file) throws PortalException {
		
		if(_log.isInfoEnabled())
			_log.info("Scanning file...");
		
		AntivirusScannerResult scannerResult = _clamCliClient.scan(file);
		
		if(_log.isInfoEnabled())
			_log.info("Scan complete ... " + scannerResult);
		
		return scannerResult;
		
	}
	
	@Reference
	public void setClamCliClient(ClamClient clamCliClient){
		_clamCliClient = clamCliClient;
	}
	
	private ClamClient _clamCliClient; 
	
	private static Log _log =
			LogFactoryUtil.getLog(ClamAntivirusScanner.class);
}