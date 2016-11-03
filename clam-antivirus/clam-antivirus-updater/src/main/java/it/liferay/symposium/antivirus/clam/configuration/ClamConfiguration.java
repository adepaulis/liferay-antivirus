package it.liferay.symposium.antivirus.clam.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(category = "Antivirus Configuration")
@Meta.OCD(
	id = ClamConfiguration.id,
	localization = "content/Language",
	name = "Clam Configuration"
)
public interface ClamConfiguration{

	public static final String id = "it.liferay.symposium.antivirus.clam.configuration.ClamConfiguration";
	
	@Meta.AD(
			deflt = "0 0/2 * 1/1 * ? *", 
			description = "Setting Cron Expression for Clam Definitions Updates (in cron unix pattern)",
			required = false
		)
	public String clamDefinitionsUpadateCronExpression();
}