/**
 * Licensed to Apereo under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. Apereo
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at the
 * following location:
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apereo.portal.portlet.container;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pluto.container.HeaderData;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletEventResponseContext;
import org.apache.pluto.container.driver.PortletContextService;
import org.apereo.portal.portlet.container.properties.IRequestPropertiesManager;
import org.apereo.portal.portlet.container.services.IPortletCookieService;
import org.apereo.portal.portlet.om.IPortletWindow;
import org.apereo.portal.url.IPortletUrlBuilder;

/** */
public class PortletEventResponseContextImpl extends PortletStateAwareResponseContextImpl
        implements PortletEventResponseContext {

    public PortletEventResponseContextImpl(
            PortletContainer portletContainer,
            IPortletWindow portletWindow,
            HttpServletRequest containerRequest,
            HttpServletResponse containerResponse,
            IRequestPropertiesManager requestPropertiesManager,
            IPortletUrlBuilder portletUrlBuider,
            PortletContextService portletContextService,
            IPortletCookieService portletCookieService) {

        super(
                portletContainer,
                portletWindow,
                containerRequest,
                containerResponse,
                requestPropertiesManager,
                portletUrlBuider,
                portletContextService,
                portletCookieService);
    }
    
    @Override
    public void reset() {
        // No-op for Portlet 2.0 compatibility - reset not used
    }
    
    @Override
    public javax.portlet.MutableRenderParameters getRenderParameters(String portletMode) {
        // Return null for Portlet 2.0 compatibility - maintains existing behavior
        return null;
    }
    
    @Override
    public HeaderData getHeaderData() {
        // Use translation helper for Portlet 2.0 compatibility
        return (HeaderData) ServletTypeMapper.toJavaxHeaderData(null);
    }
    

}
