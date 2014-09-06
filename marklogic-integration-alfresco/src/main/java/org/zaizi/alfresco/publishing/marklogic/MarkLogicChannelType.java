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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.repo.publishing.AbstractChannelType;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Channel definition for publishing/unpublishing XML content to MarkLogic Server.<br/>
 * <b>Note:</b> This class file is forked form https://github.com/zaizi/marklogic-alfresco-integration.git
 * Modified the method call for to handle the publishing and unpublishing to support MarkLogic REST apis.<br/>
 * Also Added the supprot for XML,HTML,XHTML,GIF,DOC,PDF,DOCX,XLS,XLSX,DOCX,PPT,PPTX,TEXT,BINARY and JSON etc.<br/>
 * <b>Modified by-</b> Abhinav Kumar Mishra<br/>
 * <i>Now, classes are compitable to JDK7 and HttpClient 4.3.x api.</i>
 *  
 * @author aayala
 */
public class MarkLogicChannelType extends AbstractChannelType {
    
    /** The Constant log. */
    private final static Log LOG = LogFactory.getLog(MarkLogicChannelType.class);

    /** The Constant ID. */
    public final static String ID = "marklogic";
    
    /** The Constant STATUS_DOCUMENT_INSERTED. */
    private final static int STATUS_DOCUMENT_INSERTED = 204;
    
    /** The Constant STATUS_DOCUMENT_DELETED. */
    private final static int STATUS_DOCUMENT_DELETED = 200;
    
    /** The Constant DEFAULT_SUPPORTED_MIME_TYPES. */
 	private final static Set<String> DEFAULT_SUPPORTED_MIME_TYPES = MarkLogicPublishingHelper.getMimeTypesToBeSupported();
 	
    /** The publishing helper. */
    private MarkLogicPublishingHelper publishingHelper;
    
    /** The content service. */
    private ContentService contentService;

    /** The supported mime types. */
    private Set<String> supportedMimeTypes = DEFAULT_SUPPORTED_MIME_TYPES;

    /**
     * Sets the supported mime types.
     *
     * @param mimeTypes the new supported mime types
     */
    public void setSupportedMimeTypes(final Set<String> mimeTypes) {
        supportedMimeTypes = Collections.unmodifiableSet(new TreeSet<String>(mimeTypes));
    }

    /**
     * Sets the publishing helper.
     *
     * @param markLogicPublishingHelper the new publishing helper
     */
	public void setPublishingHelper(final MarkLogicPublishingHelper markLogicPublishingHelper) {
		this.publishingHelper = markLogicPublishingHelper;
	}

    /**
     * Sets the content service.
     *
     * @param contentService the new content service
     */
	public void setContentService(final ContentService contentService) {
		this.contentService = contentService;
	}

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.publishing.channels.ChannelType#canPublish()
     */
	public boolean canPublish() {
		return true;
	}

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.publishing.channels.ChannelType#canPublishStatusUpdates()
     */
	public boolean canPublishStatusUpdates() {
		return false;
	}

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.publishing.channels.ChannelType#canUnpublish()
     */
	public boolean canUnpublish() {
		return true;
	}

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.publishing.channels.ChannelType#getChannelNodeType()
     */
	public QName getChannelNodeType() {
		return MarkLogicPublishingModel.TYPE_DELIVERY_CHANNEL;
	}

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.publishing.channels.ChannelType#getId()
     */
	public String getId() {
		return ID;
	}

    /* (non-Javadoc)
     * @see org.alfresco.repo.publishing.AbstractChannelType#getSupportedMimeTypes()
     */
    @Override
	public Set<String> getSupportedMimeTypes() {
		return supportedMimeTypes;
	}

    /* (non-Javadoc)
     * @see org.alfresco.repo.publishing.AbstractChannelType#publish(org.alfresco.service.cmr.repository.NodeRef, java.util.Map)
     */
    @Override
	public void publish(final NodeRef nodeToPublish,
			final Map<QName, Serializable> channelProperties) {
        LOG.info("publish() invoked...");
        
        final ContentReader reader = contentService.getReader(nodeToPublish, ContentModel.PROP_CONTENT);
        if (reader.exists()) {
            File contentFile;
            boolean deleteContentFileOnCompletion = false;
            if (FileContentReader.class.isAssignableFrom(reader.getClass())) {
                // Grab the content straight from the content store if we can
                contentFile = ((FileContentReader) reader).getFile();
            }
            else {
                // Otherwise copy it to a temp file and use the copy
                final File tempDir = TempFileProvider.getLongLifeTempDir("marklogic");
                contentFile = TempFileProvider.createTempFile("marklogic", "", tempDir);
                reader.getContent(contentFile);
                deleteContentFileOnCompletion = true;
            }

            final CredentialsProvider credsProvider = new BasicCredentialsProvider();
			final AuthScope authscope = new AuthScope(
					(String) channelProperties.get(MarkLogicPublishingModel.PROP_HOST),
					(int) channelProperties.get(MarkLogicPublishingModel.PROP_PORT));
    		final UsernamePasswordCredentials credential = new UsernamePasswordCredentials(
    				(String) channelProperties.get(MarkLogicPublishingModel.PROP_USER), 
    				(String) channelProperties.get(MarkLogicPublishingModel.PROP_PASS));
    		// Setting the credentials in AuthScope.
    		credsProvider.setCredentials(authscope, credential);
    		//Getting the httpClient object with authentication header
    		final CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    		
            try {
            	final String mimeType=reader.getMimetype();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Publishing node: " + nodeToPublish);
                    LOG.debug("ContentFile_MIMETYPE: "+mimeType);
                }
                                
				final String putUri = publishingHelper.getPutURIFromNodeRefAndChannelProperties(
								nodeToPublish, channelProperties);

                final HttpPut httpput = new HttpPut(putUri);                
                final FileEntity filenEntity = new FileEntity(contentFile, ContentType.create(mimeType));
                httpput.setEntity(filenEntity);

                final HttpResponse response = httpclient.execute(httpput);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Response Status: " + response.getStatusLine().getStatusCode() + " - Message: "
                            + response.getStatusLine().getReasonPhrase() + " - NodeRef: " + nodeToPublish.toString());
                }
                if (response.getStatusLine().getStatusCode() != STATUS_DOCUMENT_INSERTED) {
                    throw new AlfrescoRuntimeException(response.getStatusLine().getReasonPhrase());
				}
			} catch (IllegalStateException illegalEx) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Exception in publish(): ", illegalEx);
				}
				throw new AlfrescoRuntimeException(illegalEx.getLocalizedMessage());
			} catch (IOException ioex) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Exception in publish(): ", ioex);
				}
				throw new AlfrescoRuntimeException(ioex.getLocalizedMessage());
			} catch (URISyntaxException uriSynEx) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Exception in publish(): ", uriSynEx);
				}
				throw new AlfrescoRuntimeException(uriSynEx.getLocalizedMessage());
			}finally {
				if (deleteContentFileOnCompletion) {
					contentFile.delete();
				}
			}
		}
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.publishing.AbstractChannelType#unpublish(org.alfresco.service.cmr.repository.NodeRef, java.util.Map)
     */
    @Override
	public void unpublish(final NodeRef nodeToUnpublish,
			final Map<QName, Serializable> channelProperties) {
    	
        LOG.info("unpublish() invoked...");

        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
		final AuthScope authscope = new AuthScope(
				(String) channelProperties.get(MarkLogicPublishingModel.PROP_HOST),
				(int) channelProperties.get(MarkLogicPublishingModel.PROP_PORT));
		final UsernamePasswordCredentials credential = new UsernamePasswordCredentials(
				(String) channelProperties.get(MarkLogicPublishingModel.PROP_USER), 
				(String) channelProperties.get(MarkLogicPublishingModel.PROP_PASS));
		// Setting the credentials in AuthScope.
		credsProvider.setCredentials(authscope, credential);
		//Getting the httpClient object with authentication header
		final CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		
        try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Unpublishing node: " + nodeToUnpublish);
			}

			final String uriDelete = publishingHelper.getDeleteURIFromNodeRefAndChannelProperties(
							nodeToUnpublish, channelProperties);
            final HttpDelete httpDelete = new HttpDelete(uriDelete);
            final HttpResponse response = httpclient.execute(httpDelete);

            if (LOG.isDebugEnabled()) {
            	LOG.debug("Response Status: " + response.getStatusLine().getStatusCode() + " - Message: "
                        + response.getStatusLine().getReasonPhrase() + " - NodeRef: " + nodeToUnpublish.toString());
			}
            
			if (response.getStatusLine().getStatusCode() != STATUS_DOCUMENT_DELETED) {
				throw new AlfrescoRuntimeException(response.getStatusLine().getReasonPhrase());
			}
		} catch (IllegalStateException illegalEx) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Exception in Unpublish(): ", illegalEx);
			}
			throw new AlfrescoRuntimeException(illegalEx.getLocalizedMessage());
		} catch (IOException ioex) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Exception in Unpublish(): ", ioex);
			}
			throw new AlfrescoRuntimeException(ioex.getLocalizedMessage());
		} catch (URISyntaxException uriSynEx) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Exception in Unpublish(): ", uriSynEx);
			}
			throw new AlfrescoRuntimeException(uriSynEx.getLocalizedMessage());
		} 
    }
}
