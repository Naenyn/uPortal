package org.apereo.portal.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test for {@link UrlMultiServerNameCustomizer}. */
public class UrlMultiServerNameCustomizerTest {

    private UrlMultiServerNameCustomizer customizer;
    private HttpServletRequest request;
    private Set<String> serverNames;

    @BeforeEach
    public void setUp() {
        customizer = new UrlMultiServerNameCustomizer();
        request = mock(HttpServletRequest.class);
        
        serverNames = new LinkedHashSet<>();
        serverNames.add("server1.example.com");
        serverNames.add("server2.example.com");
        serverNames.add("server3.example.com");
        
        customizer.setAllServerNames(serverNames);
    }

    @Test
    public void testSupports() {
        assertTrue(customizer.supports(request, "http://_CURRENT_SERVER_NAME_/portal"));
        assertFalse(customizer.supports(request, "http://example.com/portal"));
    }

    @Test
    public void testCustomizeUrlWithNoMatch() {
        when(request.getHeader("Host")).thenReturn("unknown.example.com");
        
        String url = "http://_CURRENT_SERVER_NAME_/portal";
        String result = customizer.customizeUrl(request, url);
        
        // Should use the first server name when no match is found
        assertEquals("http://server1.example.com/portal", result);
    }

    @Test
    public void testCustomizeUrlWithMatch() {
        when(request.getHeader("Host")).thenReturn("server2.example.com");
        
        String url = "http://_CURRENT_SERVER_NAME_/portal";
        String result = customizer.customizeUrl(request, url);
        
        assertEquals("http://server2.example.com/portal", result);
    }

    @Test
    public void testCustomizeUrlWithPartialMatch() {
        when(request.getHeader("Host")).thenReturn("server2");
        
        String url = "http://_CURRENT_SERVER_NAME_/portal";
        String result = customizer.customizeUrl(request, url);
        
        assertEquals("http://server2.example.com/portal", result);
    }

    @Test
    public void testCustomizeUrlWithNullUrl() {
        String result = customizer.customizeUrl(request, null);
        assertEquals(null, result);
    }

    @Test
    public void testCustomizeUrlWithEmptyUrl() {
        String result = customizer.customizeUrl(request, "");
        assertEquals("", result);
    }

    @Test
    public void testCustomizeUrlWithNoReplacement() {
        String url = "http://example.com/portal";
        String result = customizer.customizeUrl(request, url);
        
        // URL doesn't contain the replacement text, so it should be unchanged
        assertEquals(url, result);
    }
}