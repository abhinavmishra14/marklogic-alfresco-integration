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

import org.alfresco.service.namespace.QName;

/**
 * MarkLogic Publishing Model.<br/>
 * <b>Note:</b> This class file is forked form https://github.com/zaizi/marklogic-alfresco-integration.git
 * Added the custom constants<br/>
 * <b>Modified by-</b> Abhinav Kumar Mishra
 *
 * @author aayala
 */
public interface MarkLogicPublishingModel {

	 /** The protocol. */
 	String PROTOCOL = "http";
	 
	 /** The uri. */
 	String URI = "uri=";
	 
	 /** The mimetypes from propertiesfile. */
 	String MIMETYPES_PROPERTIESFILE = "alfresco/module/marklogic-integration/alfresco-global.properties";
	 
 	/** The supportd mime key. */
	 String SUPPORTD_MIME_KEY = "supportedMimeTypes";

	/** The Constant NAMESPACE. */
	 String NAMESPACE = "http://www.alfresco.org/model/publishing/marklogic/1.0";

	/** The Constant PREFIX. */
	 String PREFIX = "marklogic";

	/** The Constant PROP_HOST. */
	 QName PROP_HOST = QName.createQName(NAMESPACE, "host");

	/** The Constant PROP_PORT. */
	 QName PROP_PORT = QName.createQName(NAMESPACE, "port");

	/** The Constant TYPE_DELIVERY_CHANNEL. */
	 QName TYPE_DELIVERY_CHANNEL = QName.createQName(NAMESPACE, "DeliveryChannel");

	/** The Constant ASPECT_DELIVERY_CHANNEL. */
	 QName ASPECT_DELIVERY_CHANNEL = QName.createQName(NAMESPACE, "DeliveryChannelAspect");

	/** The Constant PUBLISH_URI_KEY. */
	 String PUBLISH_URI_KEY = "alfrescopub/publish";

	/** The Constant UNPUBLISH_URI_KEY. */
	 String UNPUBLISH_URI_KEY = "alfrescopub/unpublish";
}
