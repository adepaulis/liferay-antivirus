package it.liferay.symposium.antivirus.api;

import java.io.File;

import com.liferay.portal.kernel.exception.PortalException;

public interface AntivirusScannerService {
	
	AntivirusScannerResult scan(File file) throws PortalException;
}


