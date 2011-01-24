package mr.common.context;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Implementación de {@link mr.common.context.EnviromentConfiguration
 * EnviromentConfiguration}.
 *
 * @author Mariano Ruiz
 */
public class EnviromentConfigurationImpl implements EnviromentConfiguration {

	private static final Log logger = LogFactory.getLog(EnviromentConfigurationImpl.class);

	private String enviroment;


	/**
	 * Constructor por default, el entorno se configura como
	 * {@link mr.common.context.EnviromentConfiguration#ENVIROMENT_PRODUCTION
	 * ENVIROMENT_PRODUCTION}
	 */
	public EnviromentConfigurationImpl() {
		enviroment = ENVIROMENT_PRODUCTION;
	}

	/**
	 * Se crea la configuración con el entorno pasado
	 * como parámetro.
	 * @param enviroment String - entorno de ejecución
	 */
	public EnviromentConfigurationImpl(String enviroment) {
		this.enviroment = enviroment;
	}

	@PostConstruct
	protected void logEnviroment() {
		String logMsg = "\n\n\n*****************************************************************************";
		      logMsg += "\n      STARTING APPLICATION IN '" + getEnviroment() + "' ENVIROMENT.";
		if(isProductionEnviroment()) {
			logMsg += "\n*****************************************************************************\n\n";
			logger.info(logMsg);
		} else {
			logMsg += "\n      Do NOT deploy to your live server(s) without changing this."
				    + "\n      See EnviromentConfiguration.getEnviroment() for more information.";
			logMsg += "\n*****************************************************************************\n\n";
			logger.warn(logMsg);
		}
	}

	public String getEnviroment() {
		return enviroment;
	}

	public boolean isDevelopmentEnviroment() {
		return enviroment.equals(ENVIROMENT_DEVELOPEMENT);
	}

	public boolean isTestEnviroment() {
		return enviroment.equals(ENVIROMENT_TEST);
	}

	public boolean isPreProductionEnviroment() {
		return enviroment.equals(ENVIROMENT_PRE_PRODUCTION);
	}

	public boolean isProductionEnviroment() {
		return enviroment.equals(ENVIROMENT_PRODUCTION);
	}
}
