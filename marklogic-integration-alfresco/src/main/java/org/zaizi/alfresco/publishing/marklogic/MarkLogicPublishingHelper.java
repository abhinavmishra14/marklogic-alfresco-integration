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

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.node.encryption.MetadataEncryptor;
import org.alfresco.repo.publishing.PublishingModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.collections.CollectionUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * Channel definition for publishing/unpublishing XML content to MarkLogic Server.<br/>
 * <b>Note:</b> This class file is forked form https://github.com/zaizi/marklogic-alfresco-integration.git
 * Modified the method call for to handle the publishing and unpublishing to support MarkLogic REST apis.<br/>
 * <b>Modified by-</b> Abhinav Kumar Mishra
 * 
 * @author aayala
 */
public class MarkLogicPublishingHelper {
    
    /** The encryptor. */
    private MetadataEncryptor encryptor;

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
		
		URI uri = URIUtils.createURI("http", (String) channelProperties.get(MarkLogicPublishingModel.PROP_HOST),
				     (Integer) channelProperties.get(MarkLogicPublishingModel.PROP_PORT),
				  MarkLogicPublishingModel.PUBLISH_URI_KEY, "uri=" + nodeToPublish.toString(), null);
		return uri;
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
		
		URI uri = URIUtils.createURI("http", (String) channelProperties.get(MarkLogicPublishingModel.PROP_HOST),
				    (Integer) channelProperties.get(MarkLogicPublishingModel.PROP_PORT),
				  MarkLogicPublishingModel.UNPUBLISH_URI_KEY, "uri="+ nodeToPublish.toString(), null);
		return uri;
	}
	
	/**
	 * Gets the mime types to be supported.
	 *
	 * @return the mime types to be supported
	 */
	public static Set<String> getMimeTypesToBeSupported() {
		return CollectionUtils.unmodifiableSet(MimetypeMap.MIMETYPE_XML,
				MimetypeMap.MIMETYPE_XHTML, MimetypeMap.MIMETYPE_JSON,
				MimetypeMap.MIMETYPE_PDF, MimetypeMap.MIMETYPE_WORD,
				MimetypeMap.MIMETYPE_EXCEL, MimetypeMap.MIMETYPE_TEXT_PLAIN,
				MimetypeMap.MIMETYPE_PPT,
				MimetypeMap.MIMETYPE_OPENXML_SPREADSHEET,
				MimetypeMap.MIMETYPE_OPENXML_PRESENTATION,
				MimetypeMap.MIMETYPE_OPENXML_WORDPROCESSING,
				MimetypeMap.MIMETYPE_BINARY, MimetypeMap.MIMETYPE_IMAGE_GIF);
	}
}
