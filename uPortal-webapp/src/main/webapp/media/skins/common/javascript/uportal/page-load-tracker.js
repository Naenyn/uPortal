/*
 * Page Load Tracker - Debug script to track what happens during page loads
 */
'use strict';

console.log('📄 PAGE-LOAD: Script loaded at', new Date().toISOString());
console.log('📄 PAGE-LOAD: Current URL:', window.location.href);
console.log('📄 PAGE-LOAD: Document ready state:', document.readyState);

// Track when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        console.log('📄 PAGE-LOAD: DOMContentLoaded fired');
        console.log('📄 PAGE-LOAD: Current URL:', window.location.href);
    });
} else {
    console.log('📄 PAGE-LOAD: DOM already ready');
}

// Track when page is fully loaded
window.addEventListener('load', function() {
    console.log('📄 PAGE-LOAD: Window load event fired');
    console.log('📄 PAGE-LOAD: Current URL:', window.location.href);
});

// Track any navigation events
window.addEventListener('beforeunload', function(event) {
    console.log('📄 PAGE-LOAD: beforeunload event - page is about to navigate away');
    console.log('📄 PAGE-LOAD: Current URL:', window.location.href);
    console.log('📄 PAGE-LOAD: Event:', event);
    
    // Log stack trace to see what triggered the navigation
    console.trace('📄 PAGE-LOAD: beforeunload stack trace');
});



// Track any errors that might trigger reloads
window.addEventListener('error', function(event) {
    console.log('📄 PAGE-LOAD: Global error event:', event.error);
    console.log('📄 PAGE-LOAD: Error message:', event.message);
    console.log('📄 PAGE-LOAD: Error filename:', event.filename);
    console.log('📄 PAGE-LOAD: Error line:', event.lineno);
});

// Track unhandled promise rejections
window.addEventListener('unhandledrejection', function(event) {
    console.log('📄 PAGE-LOAD: Unhandled promise rejection:', event.reason);
});

// Monitor for programmatic navigation without interfering
let lastHref = window.location.href;
setInterval(() => {
    if (window.location.href !== lastHref) {
        console.log('📄 PAGE-LOAD: URL changed from', lastHref, 'to', window.location.href);
        lastHref = window.location.href;
    }
}, 50);

console.log('📄 PAGE-LOAD: All event listeners and overrides attached');