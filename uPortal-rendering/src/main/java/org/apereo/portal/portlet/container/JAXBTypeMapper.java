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

import javax.xml.namespace.QName;

/**
 * Utility class to map between javax.xml.bind and jakarta.xml.bind types
 * for JAXB compatibility while using Jakarta EE with legacy code
 */
public class JAXBTypeMapper {
    
    /**
     * Convert jakarta JAXBElement to javax JAXBElement
     */
    @SuppressWarnings("unchecked")
    public static <T> javax.xml.bind.JAXBElement<T> toJavax(jakarta.xml.bind.JAXBElement<T> jakartaElement) {
        if (jakartaElement == null) return null;
        return new javax.xml.bind.JAXBElement<T>(
            jakartaElement.getName(),
            (Class<T>) jakartaElement.getDeclaredType(),
            jakartaElement.getValue()
        );
    }
    
    /**
     * Convert javax JAXBElement to jakarta JAXBElement
     */
    @SuppressWarnings("unchecked")
    public static <T> jakarta.xml.bind.JAXBElement<T> toJakarta(javax.xml.bind.JAXBElement<T> javaxElement) {
        if (javaxElement == null) return null;
        return new jakarta.xml.bind.JAXBElement<T>(
            javaxElement.getName(),
            (Class<T>) javaxElement.getDeclaredType(),
            javaxElement.getValue()
        );
    }
    
    /**
     * Create javax JAXBElement from components
     */
    public static <T> javax.xml.bind.JAXBElement<T> createJavax(QName name, Class<T> declaredType, T value) {
        return new javax.xml.bind.JAXBElement<T>(name, declaredType, value);
    }
    
    /**
     * Create jakarta JAXBElement from components
     */
    public static <T> jakarta.xml.bind.JAXBElement<T> createJakarta(QName name, Class<T> declaredType, T value) {
        return new jakarta.xml.bind.JAXBElement<T>(name, declaredType, value);
    }
}