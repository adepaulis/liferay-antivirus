package it.liferay.symposium.antivirus.dl.plugin;

import java.io.File;
import java.util.SortedMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.document.library.kernel.antivirus.AntivirusScanner;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerUtil;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerWrapper;
import com.liferay.document.library.kernel.antivirus.BaseFileAntivirusScanner;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import it.liferay.symposium.antivirus.api.AntivirusScannerResult;
import it.liferay.symposium.antivirus.api.AntivirusScannerService;

@Component(
		immediate = true,
		service = AntivirusScanner.class
	)
public class DLAntivirusScannerPipeline extends BaseFileAntivirusScanner{

	@Override
	public void scan(File file) throws AntivirusScannerException {
		if(_log.isInfoEnabled())
			_log.info("Scanning " + file.getName());
		
		try{
			SortedMap<ServiceReference<AntivirusScannerService>, AntivirusScannerService>
				serviceReferences =_serviceTracker.getTracked();
			
			for(AntivirusScannerService scanner : serviceReferences.values()){	
				AntivirusScannerResult scanResult = scanner.scan(file);	
				throwIfNullOrVirus(scanResult);
			}
			

		}
		catch(AntivirusScannerException avE){
			throw avE;
		}
		catch(Exception e){
			_log.error(e, e);
			throw new AntivirusScannerException(AntivirusScannerException.PROCESS_FAILURE);
		}
		
		if(_log.isInfoEnabled())
			_log.info("Scan completed no virus found");
	}
	
	@Deactivate
	protected void deactivate(BundleContext bundleContext) {

		try {
			AntivirusScannerWrapper antivirusScannerWrapper =
					(AntivirusScannerWrapper)
					AntivirusScannerUtil.getAntivirusScanner();
			
			antivirusScannerWrapper.setAntivirusScanner(null);
			
			_serviceTracker.close();
			
			if (_log.isInfoEnabled())
				_log.info("DL Antivirus Scanner deactivated");
			
		} catch (Exception e) {
			_log.error("Error deactivating DL Antivirus Scanner ");
			_log.error(e, e);
		}
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

	@Activate
	protected void activate(BundleContext bundleContext) {
		
		try {
			AntivirusScannerWrapper antivirusScannerWrapper =
					(AntivirusScannerWrapper)
					AntivirusScannerUtil.getAntivirusScanner();
			
			antivirusScannerWrapper.setAntivirusScanner(this);

			if (_log.isInfoEnabled()) 
				_log.info("DL Antivirus Scanner Pipeline registered as "
						+ "Document Library Antivirus");
			
			_serviceTracker = ServiceTrackerFactory.
					open(AntivirusScannerService.class);
			
			if (_log.isInfoEnabled()) 
				_log.info("DL Antivirus Scanner Pipeline activated");
		} catch (Exception e) {
			_log.error("Error activating DL Antivirus Scanner Pipeline");
			_log.error(e, e);
		}
	}
	
	private ServiceTracker<AntivirusScannerService, AntivirusScannerService> 
		_serviceTracker;
	
	private static Log _log =
			LogFactoryUtil.getLog(DLAntivirusScannerPipeline.class);
}