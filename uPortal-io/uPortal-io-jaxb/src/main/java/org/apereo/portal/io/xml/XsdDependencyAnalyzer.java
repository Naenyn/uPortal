/**
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
package org.apereo.portal.io.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Simple utility class to analyze XSD dependencies.
 */
public class XsdDependencyAnalyzer {
    
    private final Map<String, Set<String>> dependencies = new HashMap<>();
    private final String xsdDir;
    
    public XsdDependencyAnalyzer(String xsdDir) {
        this.xsdDir = xsdDir;
    }
    
    public void analyze() throws ParserConfigurationException, SAXException, IOException {
        File dir = new File(xsdDir);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(xsdDir + " is not a directory");
        }
        
        File[] xsdFiles = dir.listFiles((d, name) -> name.endsWith(".xsd"));
        if (xsdFiles == null || xsdFiles.length == 0) {
            System.out.println("No XSD files found in " + xsdDir);
            return;
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        for (File xsdFile : xsdFiles) {
            String fileName = xsdFile.getName();
            Set<String> imports = new HashSet<>();
            dependencies.put(fileName, imports);
            
            try (FileInputStream fis = new FileInputStream(xsdFile)) {
                Document doc = builder.parse(fis);
                Element root = doc.getDocumentElement();
                
                // Find import elements
                NodeList importNodes = root.getElementsByTagName("xs:import");
                for (int i = 0; i < importNodes.getLength(); i++) {
                    Element importElement = (Element) importNodes.item(i);
                    String schemaLocation = importElement.getAttribute("schemaLocation");
                    if (schemaLocation != null && !schemaLocation.isEmpty()) {
                        imports.add(schemaLocation);
                    }
                }
            }
        }
        
        // Print dependency tree
        System.out.println("XSD Dependency Tree:");
        for (Map.Entry<String, Set<String>> entry : dependencies.entrySet()) {
            System.out.println(entry.getKey() + " imports:");
            for (String dependency : entry.getValue()) {
                System.out.println("  - " + dependency);
            }
        }
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: XsdDependencyAnalyzer <xsd-directory>");
            return;
        }
        
        try {
            XsdDependencyAnalyzer analyzer = new XsdDependencyAnalyzer(args[0]);
            analyzer.analyze();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}