/*
 * Modern replacement for Fluid LayoutPreferencesPersistence
 * Handles layout state persistence via AJAX
 */
'use strict';

class ModernLayoutPreferencesPersistence {
    constructor(container, options = {}) {
        this.container = container;
        this.options = {
            saveLayoutUrl: '/uPortal/api/layout',
            messages: {
                error: 'Error persisting layout change'
            },
            ...options
        };
        
        this.events = {
            onSuccess: [],
            onError: []
        };
    }
    
    /**
     * Update layout preferences via AJAX
     * @param {Object} data - Layout data to persist
     * @param {Function} success - Success callback
     */
    async update(data, success) {
        try {
            const response = await fetch(this.options.saveLayoutUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(data)
            });
            
            if (response.ok) {
                let result = null;
                
                // Try to parse JSON, but don't fail if it's not JSON
                try {
                    const text = await response.text();
                    if (text.trim()) {
                        result = JSON.parse(text);
                    } else {
                        result = { success: true };
                    }
                } catch (jsonError) {
                    // Server returned non-JSON response, which is OK for some operations
                    result = { success: true };
                }
                
                // Fire success events
                this.fireEvent('onSuccess', result);
                
                // Call success callback if provided
                if (success) {
                    success(result);
                }
                
                return result;
            } else {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
        } catch (error) {
            console.error('Layout persistence error:', error);
            
            // Fire error events
            this.fireEvent('onError', this, null, error.message, error);
            
            // Show error message
            this.showErrorMessage();
            
            throw error;
        }
    }
    
    /**
     * Fire event to registered listeners
     * @param {string} eventName - Name of event to fire
     * @param {...any} args - Arguments to pass to listeners
     */
    fireEvent(eventName, ...args) {
        if (this.events[eventName]) {
            this.events[eventName].forEach(listener => {
                if (typeof listener === 'function') {
                    listener(...args);
                }
            });
        }
    }
    
    /**
     * Add event listener
     * @param {string} eventName - Name of event
     * @param {Function} listener - Listener function
     */
    addEventListener(eventName, listener) {
        if (!this.events[eventName]) {
            this.events[eventName] = [];
        }
        this.events[eventName].push(listener);
    }
    
    /**
     * Show error message to user
     */
    showErrorMessage() {
        const errorElement = this.container.querySelector('.layout-persistence-error-message');
        if (errorElement) {
            errorElement.textContent = this.options.messages.error;
            errorElement.style.display = 'block';
            
            // Hide after 5 seconds
            setTimeout(() => {
                errorElement.style.display = 'none';
            }, 5000);
        }
    }
}

// Global initialization function to replace Fluid component
window.up = window.up || {};
window.up.LayoutPreferencesPersistence = function(container, options) {
    return new ModernLayoutPreferencesPersistence(container, options);
};