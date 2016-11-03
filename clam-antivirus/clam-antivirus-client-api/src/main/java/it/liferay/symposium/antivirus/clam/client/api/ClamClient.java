package it.liferay.symposium.antivirus.clam.client.api;

import java.io.File;

import com.liferay.portal.kernel.exception.PortalException;

import it.liferay.symposium.antivirus.api.AntivirusScannerResult;

public interface ClamClient {
	
	AntivirusScannerResult scan(File file) throws PortalException;

	void updateDefinitions() throws PortalException;
}
