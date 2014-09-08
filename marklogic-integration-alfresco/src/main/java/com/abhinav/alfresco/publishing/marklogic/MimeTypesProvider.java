/*
 * Created By: Abhinav Kumar Mishra
 * Copyright &copy; 2014-2015. Abhinav Kumar Mishra. 
 * All rights reserved.
 */
package com.abhinav.alfresco.publishing.marklogic;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.util.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zaizi.alfresco.publishing.marklogic.MarkLogicPublishingModel;

/**
 * The Class MimeTypesProvider.<br/>
 * Singleton class will provide the supported mime types mapped in alfresco-global.properties file.<br/>
 * 
 * @author Abhinav kumar mishra
 */
public final class MimeTypesProvider {
	
	/** The Constant LOG. */
	private final static Log LOG = LogFactory.getLog(MimeTypesProvider.class);

	/** The Constant supportedMimeTypes. */
	private final Set<String> supportedMimeTypes = new HashSet<String>();

	/** The Constant instance. */
	private static final MimeTypesProvider INSTANCE = new MimeTypesProvider();

	/**
	 * Instantiates a new MIME types provider.
	 */
	private MimeTypesProvider() {
		super();
		init();
	}


	/**
	 * Gets the single instance of MIMETypesProvider.
	 *
	 * @return single instance of MIMETypesProvider
	 */
	public static MimeTypesProvider getInstance() {	
		return INSTANCE;
	}
	
	/**
	 * Inits the SupportedMimeTypes
	 */
	private void init() {
		final Properties props = new Properties();
		try (InputStream inStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(MarkLogicPublishingModel.MIMETYPES_PROPERTIESFILE)) {
			props.load(inStream);
		} catch (IOException ioex) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Exception getting the mimetypes from alfreco-global.properties: ",ioex);
			}
		}
		// Get the supported mimetypes from alfreco-global.properties file
		if (props.getProperty(MarkLogicPublishingModel.SUPPORTD_MIME_KEY) != null) {
			final StringTokenizer tokens = new StringTokenizer(
					props.getProperty(MarkLogicPublishingModel.SUPPORTD_MIME_KEY),",");
			while (tokens.hasMoreTokens()) {
				supportedMimeTypes.add(tokens.nextToken().trim());
			}
			LOG.info("SupportedMimeTypes by MarkLogic publishing channel: "+ supportedMimeTypes);
		}
	}

	/**
	 * Gets the mime types<br/>
	 * Gets the supported mimetypes form the alfresco-global.properties file, 
	 * if not defined then return the default mimetypes.<br/>
	 * <b>To declare mimetypes use following comma seperated syntax: </b><br/>
	 * supportedMimeTypes=application/json, application/msword, application/octet-stream,... etc.
	 *
	 * @return the mime types to be supported
	 */
	public Set<String> getMimeTypes() {
		if (supportedMimeTypes.isEmpty()) {
			// If mimetypes are not defined in properties file then return the
			// default supported mimetypes
			return CollectionUtils.unmodifiableSet(MimetypeMap.MIMETYPE_XML,
					MimetypeMap.MIMETYPE_XHTML, MimetypeMap.MIMETYPE_JSON,
					MimetypeMap.MIMETYPE_PDF, MimetypeMap.MIMETYPE_WORD,
					MimetypeMap.MIMETYPE_EXCEL,MimetypeMap.MIMETYPE_TEXT_PLAIN,
					MimetypeMap.MIMETYPE_PPT,MimetypeMap.MIMETYPE_HTML,
					MimetypeMap.MIMETYPE_OPENXML_SPREADSHEET,
					MimetypeMap.MIMETYPE_OPENXML_PRESENTATION,
					MimetypeMap.MIMETYPE_OPENXML_WORDPROCESSING,
					MimetypeMap.MIMETYPE_BINARY,MimetypeMap.MIMETYPE_IMAGE_GIF,
					MimetypeMap.MIMETYPE_IMAGE_JPEG,MimetypeMap.MIMETYPE_IMAGE_PNG,
					MimetypeMap.MIMETYPE_OUTLOOK_MSG, MimetypeMap.MIMETYPE_ZIP);
		} else {
			// Get the supported mimetypes from alfreco-global.properties file
			return CollectionUtils.unmodifiableSet(supportedMimeTypes);
		}
	}
}
