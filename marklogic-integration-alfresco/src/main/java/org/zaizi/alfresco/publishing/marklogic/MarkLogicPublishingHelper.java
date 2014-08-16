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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.node.encryption.MetadataEncryptor;
import org.alfresco.repo.publishing.PublishingModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.abhinav.alfresco.publishing.marklogic.MIMETypesProvider;

/**
 * Channel definition for publishing/unpublishing XML content to MarkLogic Server.<br/>
 * <b>Note:</b> This class file is forked form https://github.com/zaizi/marklogic-alfresco-integration.git
 * Modified the method call for to handle the publishing and unpublishing to support MarkLogic REST apis.<br/>
 * Also added method to get the mimetypes from properties file.<br/>
 * <b>Modified by-</b> Abhinav Kumar Mishra
 * 
 * @author aayala
 */
public class MarkLogicPublishingHelper {
    
    /** The encryptor. */
    private MetadataEncryptor encryptor;
    
    /** The Constant log. */
    private final static Log LOG = LogFactory.getLog(MarkLogicPublishingHelper.class);
	
    /**
     * Sets the encryptor.
     *
     * @param encryptor the new encryptor
     */
	public void setEncryptor(MetadataEncryptor encryptor) {
		this.encryptor = encryptor;
	}

    /**
     * Build a httpContext from channel properties.
     *
     * @param channelProperties the channel properties
     * @return the http context from channel properties
     */
	public HttpContext getHttpContextFromChannelProperties(
			final Map<QName, Serializable> channelProperties) {
		
		String markLogicUsername = (String) encryptor.decrypt(
				PublishingModel.PROP_CHANNEL_USERNAME,
				channelProperties.get(PublishingModel.PROP_CHANNEL_USERNAME));
		String markLogicPassword = (String) encryptor.decrypt(
				PublishingModel.PROP_CHANNEL_PASSWORD,
				channelProperties.get(PublishingModel.PROP_CHANNEL_PASSWORD));

		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				markLogicUsername, markLogicPassword);
		HttpContext context = new BasicHttpContext();
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, creds);
		context.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);

		return context;
	}

    /**
     * Build URI for a nodeRef into MarkLogic Server using the channel properties.
     *
     * @param nodeToPublish the node to publish
     * @param channelProperties the channel properties
     * @return the put uri from node ref and channel properties
     * @throws URISyntaxException the uRI syntax exception
     */
	public URI getPutURIFromNodeRefAndChannelProperties(final NodeRef nodeToPublish,
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
	public URI getDeleteURIFromNodeRefAndChannelProperties(
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
	private URI getUri(final NodeRef nodeToPublish,
			final Map<QName, Serializable> channelProperties,
			String taskToPerform) throws URISyntaxException {
		URI uri = URIUtils.createURI(MarkLogicPublishingModel.PROTOCOL,
				(String) channelProperties.get(MarkLogicPublishingModel.PROP_HOST),
				(Integer) channelProperties.get(MarkLogicPublishingModel.PROP_PORT),
				taskToPerform,MarkLogicPublishingModel.URI + nodeToPublish.toString(), null);
		LOG.info("URI For MarkLogic Publishing channel:>>>> "+uri.toString());
		return uri;
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
		return MIMETypesProvider.getInstance().getMimeTypes();
	} 
}
