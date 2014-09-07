/*********************************************************************************   
 *   Copyright 2012 Zaizi Ltd
 *    
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 ********************************************************************************/
package org.zaizi.alfresco.publishing.marklogic;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.abhinav.alfresco.publishing.marklogic.ConfigReader;
import com.abhinav.alfresco.publishing.marklogic.MimeTypesProvider;

/**
 * Channel definition for publishing/unpublishing XML content to MarkLogic Server.<br/>
 * <b>Note:</b> This class file is forked form https://github.com/zaizi/marklogic-alfresco-integration.git
 * Modified the method call for to handle the publishing and unpublishing to support MarkLogic REST apis.<br/>
 * Also added method to get the mimetypes from properties file.<br/>
 * <b>Modified by-</b> Abhinav Kumar Mishra<br/>
 * <i>Now, classes are compitable to JDK7 and HttpClient 4.3.x api.</i>
 * 
 * @author aayala
 */
public class MarkLogicPublishingHelper {
    
    /** The Constant log. */
    private final static Log LOG = LogFactory.getLog(MarkLogicPublishingHelper.class);   
    
	/**
	 * Gets the closable http client.
	 *
	 * @param channelProperties the channel properties
	 * @return the closable http client
	 */
	public CloseableHttpClient getClosableHttpClient(
			final Map<QName, Serializable> channelProperties) {
		
		final Properties alfrescoGlobalProps = ConfigReader.getInstance().getKeys();
		// Getting the httpClient object with authentication header
		CloseableHttpClient httpclient = null;
		if (Boolean.parseBoolean(alfrescoGlobalProps.getProperty(MarkLogicPublishingModel.ML_AUTH_ENABLED))) {
			LOG.debug("MarkLogic authentication enabled.");
			httpclient = HttpClients.custom().setDefaultCredentialsProvider(
					credentialProvider(channelProperties,alfrescoGlobalProps)).build();
		} else {
			LOG.debug("MarkLogic authentication disabled.");
			httpclient = HttpClients.custom().build();
		}
		return httpclient;
	}
    
	/**
	 * Credential provider.
	 *
	 * @param channelProperties the channel properties
	 * @param alfrescoGlobalProps the properties
	 * @return the credentials provider
	 */
	private CredentialsProvider credentialProvider(
			final Map<QName, Serializable> channelProperties,
			final Properties alfrescoGlobalProps) {
    
    	final String markLogicUsername = alfrescoGlobalProps.getProperty(MarkLogicPublishingModel.ML_USR);
        final String markLogicPassword = alfrescoGlobalProps.getProperty(MarkLogicPublishingModel.ML_PASS);
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
		final AuthScope authscope = new AuthScope(
				(String) channelProperties.get(MarkLogicPublishingModel.PROP_HOST),
				(int) channelProperties.get(MarkLogicPublishingModel.PROP_PORT));
		final UsernamePasswordCredentials credential = new UsernamePasswordCredentials(
				markLogicUsername, markLogicPassword);
		// Setting the credentials in AuthScope.
		credsProvider.setCredentials(authscope, credential);
		return credsProvider;
	}
    
    /**
     * Build URI for a nodeRef into MarkLogic Server using the channel properties.
     *
     * @param nodeToPublish the node to publish
     * @param channelProperties the channel properties
     * @return the put uri from node ref and channel properties
     * @throws URISyntaxException the uRI syntax exception
     */
	public String getPutURIFromNodeRefAndChannelProperties(final NodeRef nodeToPublish,
			final Map<QName, Serializable> channelProperties) throws URISyntaxException {
		return getUri(nodeToPublish, channelProperties,MarkLogicPublishingModel.PUBLISH_URI_KEY);
	}
    
    /**
     * Gets the delete uri from node ref and channel properties.
     *
     * @param nodeToPublish the node to publish
     * @param channelProperties the channel properties
     * @return the delete uri from node ref and channel properties
     * @throws URISyntaxException the uRI syntax exception
     */
	public String getDeleteURIFromNodeRefAndChannelProperties(
			final NodeRef nodeToPublish, final Map<QName, Serializable> channelProperties) throws URISyntaxException {
		return getUri(nodeToPublish, channelProperties,MarkLogicPublishingModel.UNPUBLISH_URI_KEY);
	}

	/**
	 * Gets the uri.
	 *
	 * @param nodeToPublish the node to publish
	 * @param channelProperties the channel properties
	 * @param taskToPerform the task to perform
	 * @return the uri
	 * @throws URISyntaxException the uRI syntax exception
	 */
	private String getUri(final NodeRef nodeToPublish,
			final Map<QName, Serializable> channelProperties,
			String taskToPerform) throws URISyntaxException {	
		final URIBuilder buildUri = new URIBuilder();
		buildUri.setScheme(MarkLogicPublishingModel.PROTOCOL);
		buildUri.setHost((String) channelProperties.get(MarkLogicPublishingModel.PROP_HOST));
		buildUri.setPort((Integer) channelProperties.get(MarkLogicPublishingModel.PROP_PORT));
		buildUri.setPath(taskToPerform);
		buildUri.setParameter(MarkLogicPublishingModel.URI , nodeToPublish.toString());
		LOG.info("URI For MarkLogic Publishing channel:>>>> "+buildUri.toString());
		return buildUri.toString();
	}
		
	/**
	 * Gets the mime types to be supported.<br/>
	 * Gets the supported mimetypes form the alfresco-global.properties file, if not defined then return the default mimetypes.<br/>
	 * <b>To declare mimetypes use following comma seperated syntax: </b><br/>
	 * supportedMimeTypes=application/json, application/msword, application/octet-stream,... etc.
	 *
	 * @return the mime types to be supported
	 */
	public static Set<String> getMimeTypesToBeSupported() {
		return MimeTypesProvider.getInstance().getMimeTypes();
	} 
}
