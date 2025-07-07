# uPortal Search API

This module provides the search API for uPortal.

## XML Schema Binding

This module uses Jakarta XML Binding (JAXB) to generate Java classes from XML schema definitions.

### Implementation Details

The module uses the following approach for XML schema binding:

1. **Dependencies**:
   - `jakarta.xml.bind:jakarta.xml.bind-api:4.0.0` - Jakarta XML Binding API
   - `org.glassfish.jaxb:jaxb-runtime:4.0.3` - Runtime implementation
   - `org.glassfish.jaxb:jaxb-xjc:4.0.3` - XJC compiler for code generation

2. **Code Generation**:
   - Source XSD: `src/main/resources/xsd/portal-search-4.0.xsd`
   - Binding customization: `src/main/binding/bindings.xjb`
   - Generated sources: `build/generated-sources/jaxb`

3. **Build Process**:
   - The `generateJaxb` Gradle task generates Java classes from the XSD
   - The generated sources are included in the compilation classpath
   - The task runs automatically before compilation

### Migration Notes

This implementation replaces the previous approach that used the `org.openrepose.gradle.plugins.jaxb` Gradle plugin. The new implementation:

- Uses Jakarta EE 9+ XML Binding APIs
- Simplifies the build configuration
- Eliminates external plugin dependencies
- Maintains compatibility with existing XSD and binding files

### Testing

A JUnit test (`JaxbBindingTest`) is provided to verify that the JAXB bindings work correctly.