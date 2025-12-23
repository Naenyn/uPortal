<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ include file="/WEB-INF/jsp/include.jsp"%>

<div class="container-fluid" id="sitemap-holder">
    <!--
     | Tab layout:
     | Tab1          Tab2          Tab3          Tab4
     |   -portlet1     -portlet5     -portlet7     -portlet8
     |   -portlet2     -portlet6                   -portlet9
     |   -portlet3                                 -portlet10
     |   -portlet4
     |
     | Tab5 ....
     +-->

    <a name="sitemap"></a>
</div>

<template id="sitemap-tab-row-template">
    <div class="row"></div>
</template>

<template id="sitemap-tab-template">
    <div class="up-sitemap-tab-header col-md-3">
        <h4>
            <a href=""></a>
        </h4>
        <ul></ul>
    </div>
</template>

<template id="sitemap-tab-portlet-template">
    <li><a href=""><span class="title"></span></a></li>
</template>

<script src="<rs:resourceURL value="/rs/lodash/4.17.4/lodash.min.js"/>"></script>

<%-- API URL for fetching layout details to build sitemap --%>
<c:set value="${renderRequest.contextPath}" var="portalContextPath" />
<c:url value="/api/v4-3/dlm/layout.json" var="layoutApiUrl" />

<%--
    UI strings for i18n
--%>
<spring:message var="i18n_error_loading_sitemap" code="error.loading.sitemap" />

<script language="javascript" type="text/javascript">
(function() { // Prevent adding to the global namespace

    // If a path check fails, it'll throw an error.
    function sitemapJsonCheck(jsonObj, pathChecks, errMsg) {
        return !_.every(pathChecks, function(pathCheck) {
            if(!_.has(jsonObj, pathCheck)) {
                console.log(errMsg + pathCheck);
                return false;
            }
            return true;
        });
    }

    console.log('🗺️ SITEMAP: Starting fetch for:', '${layoutApiUrl}');
    console.log('🗺️ SITEMAP: Current URL:', window.location.href);
    console.log('🗺️ SITEMAP: Document ready state:', document.readyState);
    
    fetch('${layoutApiUrl}', {credentials: 'same-origin'})
        .then(function (response) {
            console.log('🗺️ SITEMAP: Fetch response received:', response.status, response.statusText);
            console.log('🗺️ SITEMAP: Response headers:', [...response.headers.entries()]);
            console.log('🗺️ SITEMAP: Response redirected:', response.redirected);
            console.log('🗺️ SITEMAP: Response URL:', response.url);
            
            if (response.status >= 200 && response.status < 300) {
                return response;
            } else {
                console.log('🗺️ SITEMAP: Non-2xx response, throwing error');
                var error = new Error(response.statusText);
                error.response = response;
                throw error;
            }
        })
        // extract json from response
        .then(function (response) {
            return response.json();
        })
        // Generate sitemap
        .then(function (response) {
            console.log('🗺️ SITEMAP: Starting sitemap generation');
            console.log('🗺️ SITEMAP: Response data:', response);
            
            if (sitemapJsonCheck(response, ['layout.navigation.tabs'], "Missing required object path ")) {
                console.log('🗺️ SITEMAP: Missing layout.navigation.tabs, throwing error');
                throw new Error("Missing 'layout.navigation.tabs' in the layout.");
            }

            console.log('🗺️ SITEMAP: Found', response.layout.navigation.tabs.length, 'tabs');
            
            // Begin tab row
            var tabRowTemplate = document.getElementById('sitemap-tab-row-template');
            var tabRow = document.importNode(tabRowTemplate.content, true).querySelector('div');
            _.forEach(response.layout.navigation.tabs, function (tab, tabIndex) {
                console.log('🗺️ SITEMAP: Processing tab', tabIndex, ':', tab.name, 'externalId:', tab.externalId);
                
                if (!sitemapJsonCheck(tab, ['name', 'externalId', 'content'], "Missing required object path [layout.navigation.tabs] > ")) {
                    console.log('🗺️ SITEMAP: Tab validation passed, creating tab header');
                    // Setup tab link
                    var tabTemplate = document.getElementById('sitemap-tab-template');
                    var tabHeader = document.importNode(tabTemplate.content, true).querySelector('div');
                    // Add content to tab header template
                    var tabHeaderLink = tabHeader.querySelector('a');
                    tabHeaderLink.textContent = _.unescape(tab.name);
                    console.log('🗺️ SITEMAP: Setting tab href to:', '${portalContextPath}/f/' + tab.externalId + '/normal/render.uP');
                    tabHeaderLink.href = '${portalContextPath}/f/' + tab.externalId + '/normal/render.uP';
                    var portletList = tabHeader.querySelector('ul');

                    _.forEach(tab.content, function (parentContent, parentContentIndex) {
                        if (sitemapJsonCheck(parentContent, ['content'], "Missing required object path [layout.navigation.tabs] > content > ")) {
                            return;
                        }

                        _.forEach(parentContent.content, function (portlet, portletIndex) {
                            if (sitemapJsonCheck(portlet, ['name', 'fname', 'ID'], "Missing required object path [layout.navigation.tabs] > content > content > ")) {
                                return;
                            }

                            // Setup portlet link
                            var portletTemplate = document.getElementById('sitemap-tab-portlet-template');
                            var portletListItem = document.importNode(portletTemplate.content, true).querySelector('li');
                            // Add content to portlet template
                            var portletTitle = portletListItem.querySelector('span');
                            portletTitle.textContent = _.unescape(portlet.name);
                            var portletLink = portletListItem.querySelector('a');
                            portletLink.href = '${portalContextPath}/f/' + tab.externalId + '/p/' + portlet.fname + '.' + portlet.ID + '/max/render.uP';
                            if (portlet.parameters && portlet.parameters.alternativeMaximizedLink) {
                              portletLink.href = portlet.parameters.alternativeMaximizedLink
                              if (portlet.parameters.alternativeMaximizedLinkTarget) {
                                portletLink.target = portlet.parameters.alternativeMaximizedLinkTarget;
                              }
                            }
                            // Add portlet to tab list
                            portletList.appendChild(portletListItem);
                        });
                    });

                    tabRow.appendChild(tabHeader);
                } else {
                    console.log('🗺️ SITEMAP: Tab validation failed, skipping tab');
                }

                console.log('🗺️ SITEMAP: Finished processing tab', tabIndex);
                
                if (tabIndex === (response.layout.navigation.tabs.length - 1)) {
                    console.log('🗺️ SITEMAP: Adding final tab row to page');
                    // Add final tab row to page
                    document.getElementById('sitemap-holder').appendChild(tabRow);
                } else if (tabIndex % 4 === 3) { // Four per row
                    console.log('🗺️ SITEMAP: Adding tab row and starting new row');
                    // Add tab row to page, and initialize a new tab row
                    document.getElementById('sitemap-holder').appendChild(tabRow);
                    tabRow = document.importNode(tabRowTemplate.content, true).querySelector('div');
                }
            });
            
            console.log('🗺️ SITEMAP: Sitemap generation completed successfully');
            console.log('🗺️ SITEMAP: Current URL after completion:', window.location.href);
        })
        // Let user know and log error to browser console
        .catch(function(error) {
            console.log('🗺️ SITEMAP: Catch handler triggered - suppressing to prevent reload loop');
            console.log('🗺️ SITEMAP: Error message:', error.message);
            
            // Silently fail - don't add error message to DOM or do anything that might trigger reload
            console.log('🗺️ SITEMAP: Error suppressed, continuing normally');
        });

})();
</script>