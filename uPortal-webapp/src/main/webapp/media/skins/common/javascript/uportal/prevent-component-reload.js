// Navigation detection script
(function() {
    'use strict';
    
    // Track all navigation events
    const originalPushState = history.pushState;
    const originalReplaceState = history.replaceState;
    
    history.pushState = function() {
        console.log('🔍 NAVIGATION: history.pushState called with:', arguments[2]);
        console.trace('pushState stack trace:');
        return originalPushState.apply(this, arguments);
    };
    
    history.replaceState = function() {
        console.log('🔍 NAVIGATION: history.replaceState called with:', arguments[2]);
        console.trace('replaceState stack trace:');
        return originalReplaceState.apply(this, arguments);
    };
    
    // Track location changes
    let lastUrl = window.location.href;
    setInterval(function() {
        if (window.location.href !== lastUrl) {
            console.log('🔍 NAVIGATION: URL changed from', lastUrl, 'to', window.location.href);
            console.trace('URL change detected:');
            lastUrl = window.location.href;
        }
    }, 100);
    
    // Track beforeunload
    window.addEventListener('beforeunload', function(e) {
        console.log('🔍 NAVIGATION: beforeunload event triggered');
        console.trace('beforeunload stack trace:');
    });
    
    console.log('Navigation detection initialized');
})();