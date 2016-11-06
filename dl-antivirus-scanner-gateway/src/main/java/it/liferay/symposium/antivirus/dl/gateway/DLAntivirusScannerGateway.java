package it.liferay.symposium.antivirus.dl.gateway;

import java.io.File;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.liferay.document.library.kernel.antivirus.AntivirusScanner;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerUtil;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerWrapper;
import com.liferay.document.library.kernel.antivirus.BaseFileAntivirusScanner;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import it.liferay.symposium.antivirus.api.AntivirusScannerResult;
import it.liferay.symposium.antivirus.api.AntivirusScannerService;
import it.liferay.symposium.antivirus.dl.gateway.config.DLAntivirusScannerGatewayConfiguration;

@Component(
		immediate = true,
		service = AntivirusScanner.class,
		configurationPid=DLAntivirusScannerGatewayConfiguration.id
)
public class DLAntivirusScannerGateway 
	extends BaseFileAntivirusScanner{

	@Override
	public void scan(File file) throws AntivirusScannerException {
		
		if(_log.isInfoEnabled())
			_log.info("Scanning " + file.getName());
		
		try{
			AntivirusScannerResult scanResult = 
					_antivirusScanner.scan(file);
			
			throwIfNullOrVirus(scanResult);
		}
		catch(AntivirusScannerException e){
			throw e;
		}
		catch(PortalException e){
			_log.error(e);
			throw new AntivirusScannerException(
					AntivirusScannerException.PROCESS_FAILURE);
		}
		
		if(_log.isInfoEnabled())
			_log.info("Scan completed no virus found");
	}

	private void throwIfNullOrVirus(AntivirusScannerResult scanResult)
			throws AntivirusScannerException {
		if(scanResult == null){
			throw new AntivirusScannerException(
					AntivirusScannerException.PROCESS_FAILURE);
		}
		else if(scanResult.equals(
				AntivirusScannerResult.VIRUS_DETECTED)){
			if(_log.isInfoEnabled())
				_log.info("Virus Detected!");
			
			throw new AntivirusScannerException(
					AntivirusScannerException.VIRUS_DETECTED);
		}
	}
	
	@Deactivate
	protected void deactivate() {
		if (_log.isInfoEnabled()) 
			_log.info("Trying to deactivate DLAntivirusScannerGateway");

		try {
			AntivirusScannerWrapper antivirusScannerWrapper =
					(AntivirusScannerWrapper)
					AntivirusScannerUtil.getAntivirusScanner();
			
			antivirusScannerWrapper.setAntivirusScanner(null);
			
			if (_log.isInfoEnabled())
				_log.info("DLAntivirusScannerGateway deactivated");
			
		} catch (Exception e) {
			_log.error("Error deactivating DL Antivirus Scanner ");
			_log.error(e, e);
		}
	}
	
	@Activate
	protected void activate(Map<String, Object> properties) {
		try {
			
			if (_log.isInfoEnabled()) 
				_log.info("Trying to deactivate DLAntivirusScannerGateway");
			
			this._config = ConfigurableUtil.createConfigurable(
					DLAntivirusScannerGatewayConfiguration.class, properties);
			
			AntivirusScannerWrapper antivirusScannerWrapper =
					(AntivirusScannerWrapper)
					AntivirusScannerUtil.getAntivirusScanner();
			
			antivirusScannerWrapper.setAntivirusScanner(this);
			
			if (_log.isInfoEnabled()) 
				_log.info("DLAntivirusScannerGateway registered as "
						+ "Document Library Antivirus");
			
		} catch (Exception e) {
			_log.error("Error activating DLAntivirusScannerGateway");
			_log.error(e, e);
		}
	}
	
	private DLAntivirusScannerGatewayConfiguration _config;
	
	@Override
	public boolean isActive() {
		return _config.isActive();
	}
	
	@Modified
	protected void modified(Map<String, Object> properties) {
		this._config = ConfigurableUtil.createConfigurable(
				DLAntivirusScannerGatewayConfiguration.class, properties);
	}
	
	@Reference()
	public void setAntivirusScanner(AntivirusScannerService antivirusScanner){
		
		if (_log.isInfoEnabled()) 
			_log.info("Binding " + antivirusScanner.getClass().getName());
		
		_antivirusScanner = antivirusScanner;
	}
	
	private AntivirusScannerService _antivirusScanner;
	
	private static Log _log =
			LogFactoryUtil.getLog(DLAntivirusScannerGateway.class);
}