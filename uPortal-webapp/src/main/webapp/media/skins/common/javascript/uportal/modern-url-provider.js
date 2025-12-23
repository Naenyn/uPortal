/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
'use strict';
var up = up || {};

/**
 * Modern URL provider - no Fluid dependencies
 */
class ModernUrlProvider {
    constructor(container, options = {}) {
        console.log('🔗 URL-PROVIDER: Initializing ModernUrlProvider');
        console.log('🔗 URL-PROVIDER: Container:', container);
        console.log('🔗 URL-PROVIDER: Options:', options);
        
        this.container = container;
        this.options = {
            portalContext: '/uPortal',
            ...options
        };
        
        console.log('🔗 URL-PROVIDER: Final options:', this.options);
    }

    getPortletUrl(fname) {
        return this.options.portalContext + '/p/' + fname;
    }

    getTabUrl(tabId) {
        const url = this.options.portalContext + '/f/' + tabId;
        console.log('🔗 URL-PROVIDER: Generated tab URL for', tabId, ':', url);
        return url;
    }

    getPortalHomeUrl() {
        return this.options.portalContext + '/';
    }
}

// Maintain backward compatibility
up.UrlProvider = function(container, options) {
    return new ModernUrlProvider(container, options);
};