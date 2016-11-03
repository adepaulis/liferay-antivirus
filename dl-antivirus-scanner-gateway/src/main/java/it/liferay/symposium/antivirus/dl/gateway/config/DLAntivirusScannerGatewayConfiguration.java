package it.liferay.symposium.antivirus.dl.gateway.config;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(category = "Antivirus Configuration")
@Meta.OCD(
	id = DLAntivirusScannerGatewayConfiguration.id,
	name = "Gateway Configuration"
)
public interface DLAntivirusScannerGatewayConfiguration{

	public static final String id = "it.liferay.symposium.antivirus.dl."
			+ "gateway.config.DLAntivirusScannerGatewayConfiguration";
	
	@Meta.AD(deflt = "true", 
			description="Scanner Active",
			required = false)
	public boolean isActive();
}



