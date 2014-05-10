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
 * MarkLogic Publishing Model.
 *
 * @author aayala
 */
public interface MarkLogicPublishingModel
{
    
    /** The Constant NAMESPACE. */
    public static final String NAMESPACE = "http://www.alfresco.org/model/publishing/marklogic/1.0";
    
    /** The Constant PREFIX. */
    public static final String PREFIX = "marklogic";
    
    /** The Constant PROP_HOST. */
    public static final QName PROP_HOST = QName.createQName(NAMESPACE, "host");
    
    /** The Constant PROP_PORT. */
    public static final QName PROP_PORT = QName.createQName(NAMESPACE, "port");
    
    /** The Constant TYPE_DELIVERY_CHANNEL. */
    public static final QName TYPE_DELIVERY_CHANNEL = QName.createQName(NAMESPACE, "DeliveryChannel");
    
    /** The Constant ASPECT_DELIVERY_CHANNEL. */
    public static final QName ASPECT_DELIVERY_CHANNEL = QName.createQName(NAMESPACE, "DeliveryChannelAspect");
    
    /** The Constant PUBLISH_URI_KEY. */
    public static final String PUBLISH_URI_KEY = "alfrescopub/publish";
    
    /** The Constant UNPUBLISH_URI_KEY. */
    public static final String UNPUBLISH_URI_KEY = "alfrescopub/unpublish";

}
