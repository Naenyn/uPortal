# javax.servlet to jakarta.servlet Conversion Strategy

## Problem Statement

The uPortal Java 17 upgrade has reached 99% completion with only the uPortal-rendering module remaining (36-40 errors). The core issue is a **javax/jakarta servlet API mapping conflict**:

1. **AbstractPortletContextImpl** (parent class) expects `jakarta.servlet.*` types
2. **PortletRequestContext** (interface) expects `javax.servlet.*` types  
3. **Same method names** but incompatible return types create impossible override situation
4. **Java doesn't allow method overloading by return type alone**

This creates a decision loop where fixing for javax breaks jakarta, and vice versa.

## Current Status

- **99% of uPortal** compiles successfully on Java 17
- **64 out of 65 modules** working
- **Only uPortal-rendering** failing with 36-40 errors
- **ServletTypeMapper utility** created for javax/jakarta conversion
- **Root cause identified**: Method signature conflicts in PortletRequestContextImpl

## Strategic Decision: Jakarta-First Approach

### Rationale
- **Future-proof**: Jakarta EE is the future standard
- **Clean architecture**: Minimal conversion overhead
- **Compatibility**: Adapter pattern for Portlet 2.0 needs

### Implementation Strategy

#### 1. Internal Architecture (Jakarta)
- Keep all internal uPortal code using `jakarta.servlet.*`
- Parent class methods return jakarta types
- No attempts to override parent methods with different return types

#### 2. Interface Compliance (Adapter Pattern)
- Create separate javax adapter methods for Portlet 2.0 compatibility
- Use method delegation/composition instead of inheritance conflicts
- Apply ServletTypeMapper only at API boundaries

#### 3. Method Naming Convention
```java
// Parent class compliance (jakarta types)
@Override
public jakarta.servlet.http.HttpServletResponse getServletResponse() {
    return this.servletResponse; // Direct return
}

// Portlet 2.0 interface compliance (javax types)  
public javax.servlet.http.HttpServletResponse getPortletServletResponse() {
    return ServletTypeMapper.toJavax(this.servletResponse); // Conversion
}
```

## Implementation Plan

### Phase 1: Fix Method Conflicts (1-2 hours)
1. **Remove @Override conflicts** in PortletRequestContextImpl
2. **Keep parent class methods** returning jakarta types
3. **Create separate javax methods** for interface compliance
4. **Test compilation** to verify conflict resolution

### Phase 2: Add Missing Abstract Methods (1 hour)
1. **Add missing getHeaderData()** methods to response contexts
2. **Add missing getPortletAsyncContext()** methods
3. **Add missing service methods** with Portlet 2.0 stubs
4. **Use established patterns** from previous session

### Phase 3: Complete Interface Implementation (30 minutes)
1. **Implement remaining abstract methods** as no-op stubs
2. **Maintain Portlet 2.0 behavior** (return null/empty for new features)
3. **Final compilation test** across all modules

## Key Files and Classes

### Core Classes to Fix
- `PortletRequestContextImpl` - Main conflict source
- `PortletResponseContextImpl` - Missing getHeaderData()
- `PortletMimeResponseContextImpl` - Servlet response conflicts
- `PortletStateAwareResponseContextImpl` - Missing methods
- `PortletResourceRequestContextImpl` - Missing async methods

### Utility Classes
- `ServletTypeMapper` - javax/jakarta conversion utility (✅ Created)
- Contains adapters for HttpServletRequest, HttpServletResponse, Cookie

### Missing Abstract Methods Identified
```
getHeaderData() - Multiple response context classes
getPortletAsyncContext() - Resource request context  
getPortletHeaderResponseContext() - Service implementation
createPortletSession() - Environment service
updateFromCookie() - Cookie implementation
```

## Established Patterns

### 1. No-Op Stub Pattern (Portlet 2.0 Compatibility)
```java
@Override
public ReturnType methodName(parameters) {
    // No-op for Portlet 2.0 compatibility - maintains existing behavior
    return null; // or appropriate default
}
```

### 2. ServletTypeMapper Usage
```java
// Convert jakarta to javax
javax.servlet.http.HttpServletRequest javaxRequest = ServletTypeMapper.toJavax(jakartaRequest);
javax.servlet.http.Cookie[] javaxCookies = ServletTypeMapper.toJavax(jakartaCookies);
```

### 3. Interface Compliance Pattern
```java
// Avoid method name conflicts with parent class
public javax.servlet.http.HttpServletResponse getPortletInterfaceMethod() {
    return ServletTypeMapper.toJavax(this.jakartaField);
}
```

## Success Metrics

### Target Achievement
- **100% compilation success** across all 65 modules
- **Zero functional changes** to Portlet 2.0 behavior
- **Clean javax/jakarta separation** with minimal conversion overhead
- **Maintainable architecture** for future development

### Validation Steps
1. **Full project compilation** - `gradle build --no-daemon`
2. **Module-by-module verification** - All 65 modules compile
3. **Basic functionality test** - Portlet rendering works
4. **No regression testing** - Existing features unchanged

## Next Session Checklist

### Immediate Actions
1. **Review current PortletRequestContextImpl** state
2. **Apply Jakarta-first strategy** systematically  
3. **Fix method conflicts** without creating new ones
4. **Add missing abstract methods** using established patterns

### Key Principles
- **No method override conflicts** - Use different names when needed
- **Jakarta internal, javax boundary** - Clear separation
- **Portlet 2.0 compatibility** - All new features stubbed as no-ops
- **Systematic approach** - Fix one class at a time, test frequently

## Current Error Count: 36-40 errors
**Target: 0 errors (100% Java 17 compatibility)**

This represents the final 1% of a highly successful Java 17 modernization effort.