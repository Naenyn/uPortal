// Comprehensive reload detection script
console.log('🔍 RELOAD DETECTION: Starting comprehensive reload tracking');

// Track all possible reload triggers
window.reloadTriggers = window.reloadTriggers || [];

// 1. Override window.location setters
let originalLocation = window.location;
Object.defineProperty(window, 'location', {
    get: function() {
        return originalLocation;
    },
    set: function(value) {
        console.log('🚨 RELOAD TRIGGER: window.location set to:', value);
        console.trace('window.location setter stack trace:');
        window.reloadTriggers.push({type: 'location.set', value: value, time: Date.now()});
        originalLocation = value;
    }
});

// 2. Override location.href setter
const originalHref = Object.getOwnPropertyDescriptor(Location.prototype, 'href');
Object.defineProperty(Location.prototype, 'href', {
    get: originalHref.get,
    set: function(value) {
        console.log('🚨 RELOAD TRIGGER: location.href set to:', value);
        console.trace('location.href setter stack trace:');
        window.reloadTriggers.push({type: 'href.set', value: value, time: Date.now()});
        originalHref.set.call(this, value);
    }
});

// 3. Override location.reload
const originalReload = Location.prototype.reload;
Location.prototype.reload = function() {
    console.log('🚨 RELOAD TRIGGER: location.reload() called');
    console.trace('location.reload stack trace:');
    window.reloadTriggers.push({type: 'reload', time: Date.now()});
    return originalReload.apply(this, arguments);
};

// 4. Override location.replace
const originalReplace = Location.prototype.replace;
Location.prototype.replace = function(url) {
    console.log('🚨 RELOAD TRIGGER: location.replace() called with:', url);
    console.trace('location.replace stack trace:');
    window.reloadTriggers.push({type: 'replace', url: url, time: Date.now()});
    return originalReplace.apply(this, arguments);
};

// 5. Override location.assign
const originalAssign = Location.prototype.assign;
Location.prototype.assign = function(url) {
    console.log('🚨 RELOAD TRIGGER: location.assign() called with:', url);
    console.trace('location.assign stack trace:');
    window.reloadTriggers.push({type: 'assign', url: url, time: Date.now()});
    return originalAssign.apply(this, arguments);
};

// 6. Track form submissions that might cause reloads
document.addEventListener('submit', function(e) {
    console.log('🚨 RELOAD TRIGGER: Form submission detected:', e.target);
    console.trace('Form submission stack trace:');
    window.reloadTriggers.push({type: 'form.submit', target: e.target, time: Date.now()});
});

// 7. Track beforeunload events
window.addEventListener('beforeunload', function(e) {
    console.log('🚨 RELOAD TRIGGER: beforeunload event fired');
    console.log('📊 RELOAD SUMMARY: Total triggers detected:', window.reloadTriggers.length);
    window.reloadTriggers.forEach((trigger, index) => {
        console.log(`  ${index + 1}. ${trigger.type}:`, trigger);
    });
});

// 8. Track unload events
window.addEventListener('unload', function(e) {
    console.log('🚨 RELOAD TRIGGER: unload event fired');
});

// 9. Track popstate events (back/forward navigation)
window.addEventListener('popstate', function(e) {
    console.log('🚨 RELOAD TRIGGER: popstate event fired:', e.state);
    console.trace('popstate stack trace:');
    window.reloadTriggers.push({type: 'popstate', state: e.state, time: Date.now()});
});

// 10. Track hashchange events
window.addEventListener('hashchange', function(e) {
    console.log('🚨 RELOAD TRIGGER: hashchange event fired:', e.oldURL, '->', e.newURL);
    window.reloadTriggers.push({type: 'hashchange', oldURL: e.oldURL, newURL: e.newURL, time: Date.now()});
});

console.log('🔍 RELOAD DETECTION: All reload triggers are now being monitored');