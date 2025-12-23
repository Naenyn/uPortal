/*
 * Modern replacement for Fluid-based LayoutPreferences component
 * Replaces up-layout-preferences.js with vanilla JavaScript implementation
 */
'use strict';

class LayoutPreferences {
    constructor(container, options = {}) {
        this.container = typeof container === 'string' ? document.querySelector(container) : container;
        this.options = {
            tabContext: 'header',
            numberOfPortlets: 0,
            portalContext: '/uPortal',
            layoutPersistenceUrl: '/uPortal/api/layout',
            channelRegistryUrl: '/uPortal/api/portletList',
            mediaPath: null,
            currentSkin: null,
            gallerySelector: '.up-gallery',
            columnWidthClassPattern: /col-md-/,
            messages: {
                persistenceError: 'Error persisting layout change',
                confirmRemoveTab: 'Are you sure you want to remove this tab?',
                confirmRemovePortlet: 'Are you sure you want to remove this portlet?',
                movePortletError: 'Error moving portlet. Reload page?',
                addTabLabel: 'Add Tab'
            },
            ...options
        };
        
        this.components = {};
        this.init();
    }

    init() {
        // Initialize persistence and URL provider
        this.persistence = new LayoutPreferencesPersistence(this.container, {
            saveLayoutUrl: this.options.layoutPersistenceUrl,
            messages: { error: this.options.messages.persistenceError }
        });

        this.urlProvider = new UrlProvider(this.container, {
            portalContext: this.options.portalContext
        });

        // Initialize components
        this.initializeGallery();
        this.initializeTabManager();
        this.initializePortletReorderer();
        this.initializePortletDeletion();
    }

    initializeGallery() {
        if (this.options.gallerySelector && document.querySelector(this.options.gallerySelector)) {
            this.components.gallery = new PortalGallery(this.options.gallerySelector, {
                isOpen: false
            });
            // Store global reference for PortletBrowser access
            window.up.gallery = this.components.gallery;
        }
    }

    initializeTabManager() {
        const tabNavigation = document.querySelector('#portalNavigation');
        if (tabNavigation) {
            this.components.tabManager = new TabManager('#portalNavigation', {
                onTabEdit: (newValue) => {
                    this.persistence.update({
                        action: 'renameTab',
                        tabId: this.getActiveTabId(),
                        tabName: newValue
                    }, () => {
                        // Success callback - stay on current page, no navigation needed
                    }, (error) => {
                        // Error callback - handle failure
                        console.error('Tab rename failed:', error);
                    });
                },
                onTabRemove: (tabContainer) => {
                    if (!confirm(this.options.messages.confirmRemoveTab)) {
                        return false;
                    }
                    
                    const id = window.up.defaultNodeIdExtractor(tabContainer);
                    this.persistence.update({
                        action: 'removeElement',
                        elementID: id
                    }, () => {
                        window.location = this.urlProvider.getPortalHomeUrl();
                    });
                },
                onTabAdd: (tabLabel, columns, tabGroup) => {
                    this.persistence.update({
                        action: 'addTab',
                        tabName: tabLabel,
                        widths: columns,
                        tabGroup: tabGroup
                    }, (data) => {
                        const tabUrl = this.urlProvider.getTabUrl(data.tabId);
                        window.location = tabUrl;
                    });
                },
                onTabMove: (sourceId, method, elementId, tabPosition) => {
                    this.persistence.update({
                        action: 'moveTab',
                        sourceID: sourceId,
                        method: method,
                        elementID: elementId,
                        tabPosition: tabPosition
                    });
                },
                tabContext: this.options.tabContext,
                numberOfPortlets: this.options.numberOfPortlets,
                addTabLabel: this.options.messages.addTabLabel
            });
        }
    }

    initializePortletReorderer() {
        // Check if chrome toolbars exist on the layout
        if (document.querySelectorAll('[id*=toolbar_]').length > 0) {
            this.components.portletReorderer = new PortletReorderer('#portalPageBodyColumns', {
                selectors: {
                    columns: '.portal-page-column-inner',
                    modules: '.up-portlet-wrapper',
                    lockedModules: '.locked:not(.up-fragment-admin)',
                    dropWarning: '#portalDropWarning',
                    grabHandle: '[id*=toolbar_] .grab-handle'
                },
                onAfterMove: (movedNode) => {
                    let method = 'insertBefore';
                    let target = null;
                    
                    const nextPortlets = movedNode.parentNode.querySelectorAll('[id^=portlet_]');
                    const movedIndex = Array.from(nextPortlets).indexOf(movedNode);
                    
                    if (movedIndex < nextPortlets.length - 1) {
                        target = nextPortlets[movedIndex + 1];
                    } else if (movedIndex > 0) {
                        target = nextPortlets[movedIndex - 1];
                        method = 'appendAfter';
                    } else {
                        target = movedNode.parentNode;
                    }

                    const options = {
                        action: 'movePortlet',
                        method: method,
                        elementID: window.up.defaultNodeIdExtractor(target),
                        sourceID: window.up.defaultNodeIdExtractor(movedNode)
                    };
                    
                    // Handle the persistence result properly with async callbacks
                    this.persistence.update(options, (data) => {
                        // Success - operation completed successfully
                    }).catch(error => {
                        // Error - show error dialog and offer reload
                        console.error('Move failed:', error);
                        if (confirm(this.options.messages.movePortletError)) {
                            location.reload();
                        }
                    });

                    // Revert the Move Portlet menu item and hide the grab handle
                    const moveOptionsItem = movedNode.querySelector('.up-portlet-control.move');
                    if (moveOptionsItem) {
                        moveOptionsItem.textContent = moveOptionsItem.getAttribute('data-move-text');
                    }
                    const grabHandle = movedNode.querySelector('.up-portlet-titlebar .grab-handle');
                    if (grabHandle) {
                        grabHandle.classList.add('hidden');
                    }
                }
            });
        }
    }

    initializePortletDeletion() {
        // Portlet deletion
        document.addEventListener('click', (e) => {
            if (e.target.matches('a[id*=removePortlet_]')) {
                e.preventDefault();
                
                const id = window.up.defaultNodeIdExtractor(e.target);
                if (!confirm(this.options.messages.confirmRemovePortlet)) {
                    return false;
                }
                
                const portletEl = document.querySelector(`#portlet_${id}`);
                const subnavEl = document.querySelector(`#portalSubnavLink_${id}`);
                
                if (portletEl) portletEl.remove();
                if (subnavEl) subnavEl.remove();
                
                this.persistence.update({
                    action: 'removeElement',
                    elementID: id
                });
                
                return false;
            }
        });
    }

    getActiveTabId() {
        const activeTab = document.querySelector('#portalNavigationList li.active');
        return activeTab ? window.up.defaultNodeIdExtractor(activeTab) : null;
    }

    showMessage(message, type, callback) {
        const messageDiv = document.querySelector('#portalPageBodyMessage');
        if (message && type && messageDiv) {
            const delay = type === 'error' ? 5000 : 2000;
            messageDiv.innerHTML = `<p>${message}</p>`;
            messageDiv.className = type;
            messageDiv.style.display = 'block';
            
            setTimeout(() => {
                messageDiv.style.opacity = '0';
                setTimeout(() => {
                    messageDiv.style.display = 'none';
                    messageDiv.style.opacity = '1';
                    if (callback) callback();
                }, 400);
            }, delay);
        } else if (callback) {
            callback();
        }
    }
}

class FocusedLayoutPreferences {
    constructor(container, options = {}) {
        this.container = typeof container === 'string' ? document.querySelector(container) : container;
        this.options = {
            portalContext: '/uPortal',
            layoutPersistenceUrl: '/uPortal/api/layout',
            messages: {
                persistenceError: 'Error persisting layout change'
            },
            ...options
        };
        
        this.init();
    }

    init() {
        this.persistence = new LayoutPreferencesPersistence(this.container, {
            saveLayoutUrl: this.options.layoutPersistenceUrl,
            messages: { error: this.options.messages.persistenceError }
        });

        this.urlProvider = new UrlProvider(this.container, {
            portalContext: this.options.portalContext
        });

        this.initializeFocusedContentDialog();
    }

    initializeFocusedContentDialog() {
        const dialogLink = document.querySelector('#focusedContentDialogLink');
        if (dialogLink) {
            dialogLink.addEventListener('click', (event) => {
                event.preventDefault();
                
                // Initialize the dialog (assuming jQuery UI is still available for dialogs)
                const dialog = document.querySelector('.focused-content-dialog');
                if (dialog && window.jQuery) {
                    window.jQuery(dialog).dialog({ width: 500, modal: true });
                    
                    // Wire the form to persist portlet addition
                    const form = dialog.querySelector('form');
                    if (form) {
                        form.addEventListener('submit', (e) => {
                            e.preventDefault();
                            
                            const portletId = form.portletId.value;
                            const tabId = form.querySelector('[name=targetTab]:checked')?.value;
                            
                            this.persistence.update({
                                action: 'addPortlet',
                                channelID: portletId,
                                position: 'insertBefore',
                                elementID: tabId
                            }, () => {
                                window.location = this.urlProvider.getTabUrl(tabId);
                            });
                            
                            return false;
                        });
                    }
                }
                
                // Re-wire the link to open the initialized dialog
                dialogLink.removeEventListener('click', arguments.callee);
                dialogLink.addEventListener('click', () => {
                    if (window.jQuery) {
                        window.jQuery('.focused-content-dialog').dialog('open');
                    }
                });
            });
        }
    }
}

// Placeholder classes for dependencies that need to be modernized separately
class LayoutPreferencesPersistence {
    constructor(container, options = {}) {
        try {
            const modern = new ModernLayoutPreferencesPersistence(container, options);
            Object.setPrototypeOf(this, Object.getPrototypeOf(modern));
            Object.assign(this, modern);
        } catch (error) {
            console.error('Error during LayoutPreferencesPersistence initialization:', error);
            throw error;
        }
    }
}

class UrlProvider {
    constructor(container, options = {}) {
        const modern = new ModernUrlProvider(container, options);
        return modern;
    }
}

class TabManager {
    constructor(selector, options = {}) {
        // Use ModernTabManager instead of old Fluid implementation
        const modern = new ModernTabManager(selector, options);
        return modern;
    }
}

class PortletReorderer {
    constructor(selector, options = {}) {
        const container = typeof selector === 'string' ? document.querySelector(selector) : selector;
        return window.up.LayoutDraggableManager(container, options);
    }
}

// Global initialization function to replace Fluid component
window.up = window.up || {};
window.up.LayoutPreferences = function(container, options) {
    const instance = new LayoutPreferences(container, options);
    return instance;
};

window.up.FocusedLayoutPreferences = function(container, options) {
    const instance = new FocusedLayoutPreferences(container, options);
    return instance;
};