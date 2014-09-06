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
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;

import com.abhinav.alfresco.publishing.marklogic.MIMETypesProvider;

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
		return MIMETypesProvider.getInstance().getMimeTypes();
	} 
}
