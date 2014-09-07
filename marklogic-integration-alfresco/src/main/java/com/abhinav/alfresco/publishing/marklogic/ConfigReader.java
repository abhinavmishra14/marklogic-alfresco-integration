/*
 * Created By: Abhinav Kumar Mishra
 * Copyright &copy; 2013-2014. Abhinav Kumar Mishra. 
 * All rights reserved.
 */
package com.abhinav.alfresco.publishing.marklogic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizi.alfresco.publishing.marklogic.MarkLogicPublishingModel;

/**
 * The Class ConfigReader.<br/>
 * Singleton class will provide the keys mapped in alfresco-global.properties
 * file.<br/>
 * 
 * @author Abhinav kumar mishra
 */
public final class ConfigReader {

	/** The Constant LOG. */
	private final static Log LOG = LogFactory.getLog(ConfigReader.class);

	/** The Constant s3ContentStoreKeys. */
	private final static Properties KEYS = new Properties();

	/** The Constant instance. */
	private static final ConfigReader INSTANCE = new ConfigReader();

	/**
	 * Instantiates a new keys provider.
	 */
	private ConfigReader() {
		super();
		init();
	}

	/**
	 * Gets the single instance of KeysProvider.
	 *
	 * @return single instance of KeysProvider
	 */
	public static ConfigReader getInstance() {
		return INSTANCE;
	}

	/**
	 * Loads the properties from the alfresco-global.properties.
	 */
	private void init() {
		try (InputStream inStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(MarkLogicPublishingModel.GLOBAL_PROPERTIESFILE)) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Loading access keys from 'alfresco-global.properties'");
			}
			KEYS.load(inStream);
		} catch (IOException ioex) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Exception getting the keys from alfreco-global.properties: ",ioex);
			}
		}
	}

	/**
	 * Gets the keys.
	 *
	 * @return the keys
	 */
	public Properties getKeys() {
		return KEYS;
	}
}
