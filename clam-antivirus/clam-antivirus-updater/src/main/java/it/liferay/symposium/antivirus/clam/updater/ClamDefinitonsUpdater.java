package it.liferay.symposium.antivirus.clam.updater;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseSchedulerEntryMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.TriggerFactoryUtil;

import aQute.bnd.annotation.metatype.Configurable;
import it.liferay.symposium.antivirus.clam.client.api.ClamClient;
import it.liferay.symposium.antivirus.clam.configuration.ClamConfiguration;

@Component(
		configurationPid = ClamConfiguration.id, 
		immediate = true, 
		service = ClamDefinitonsUpdater.class
)
public class ClamDefinitonsUpdater 
	extends BaseSchedulerEntryMessageListener {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = 
				Configurable.createConfigurable(
						ClamConfiguration.class, 
						properties);

		if (_log.isInfoEnabled()) {
			_log.info("ClamScanUpdater Setting => "
					+ getClamDefinitionsUpadateCronExpression());
		}

		schedulerEntryImpl.setTrigger(
				TriggerFactoryUtil.createTrigger(getEventListenerClass(),
				getEventListenerGroupName(), 
				getClamDefinitionsUpadateCronExpression()));

		_schedulerEngineHelper.register(this, 
				schedulerEntryImpl, 
				DestinationNames.SCHEDULER_DISPATCH);
	}

	private String getEventListenerGroupName() {
		return _eventListenerGroupName;
	}

	@Deactivate
	protected void deactivate() {
		_schedulerEngineHelper.unregister(this);
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		
		if(_log.isInfoEnabled());
			_log.info("Updating Clam AV Definitons");
		
		_clamCliClient.updateDefinitions();
	}
	
	@Reference
	public void setClamCliClient(ClamClient clamCliClient){
		_clamCliClient = clamCliClient;
	}
	
	private ClamClient _clamCliClient; 

	@Reference(unbind = "-")
	protected void setSchedulerEngineHelper(
		SchedulerEngineHelper schedulerEngineHelper) {

		_schedulerEngineHelper = schedulerEngineHelper;
	}
	
	private String getClamDefinitionsUpadateCronExpression() {
		return _configuration.clamDefinitionsUpadateCronExpression();
	}

	
	private volatile ClamConfiguration _configuration;
	private static String _eventListenerGroupName = 
			"Clam AV Definitions Update";
	private SchedulerEngineHelper _schedulerEngineHelper;

	private static final Log _log = 
			LogFactoryUtil.getLog(ClamDefinitonsUpdater.class);
}