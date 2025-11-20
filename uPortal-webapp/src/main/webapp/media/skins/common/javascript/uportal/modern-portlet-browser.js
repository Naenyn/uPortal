/*
 * Modern replacement for Fluid-based PortletBrowser component
 * Replaces up-portlet-browser.js with vanilla JavaScript implementation
 */
'use strict';

class PortletBrowser {
    constructor(container, gallery, options = {}) {
        this.container = container;
        this.gallery = gallery;
        this.options = { buttonText: 'Add', buttonAction: 'add', ...options };
        this.registry = null;
        this.dragManager = null;
        this.state = {
            currentCategory: '',
            portletRegex: null
        };
        this.init();
    }

    async init() {
        try {
            // Load portlet registry
            this.registry = new PortletRegistry(this.options.portletListUrl);
            await this.registry.load();
            
            // Initialize drag manager for Add Content tab
            if (this.options.buttonAction === 'add') {
                this.dragManager = new LayoutDraggableManager(this.container, {
                    onDropTarget: (method, targetID, portletData) => {
                        this.onPortletDrop(portletData, method, targetID);
                    }
                });
            }
            
            // Initialize category and portlet views
            this.categoryView = new CategoryListView(this.container, this);
            this.portletView = new PortletListView(this.container, this);
            
            // Set up search
            this.setupSearch();
            
        } catch (error) {
            console.error('Failed to initialize PortletBrowser:', error);
        }
    }

    setupSearch() {
        const searchInput = this.container.querySelector('.portlet-search-input');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                const query = e.target.value.trim();
                this.state.portletRegex = query ? new RegExp(query, 'i') : null;
                this.portletView.refresh();
            });
        }
    }

    onCategorySelect(category) {
        this.state.currentCategory = category.id;
        this.categoryView.refresh();
        this.portletView.refresh();
    }

    onPortletSelect(portlet) {
        if (this.options.buttonAction === 'use') {
            // "Use It" functionality - redirect to portlet's maximized view
            window.location = `/uPortal/p/${portlet.fname}`;
            return;
        }
        
        // "Add" functionality - use the same persistence mechanism as the original Fluid implementation
        this.addPortletToPage(portlet);
    }
    
    onPortletDrop(portlet, method, targetID) {
        // Handle drag and drop portlet addition
        const options = {
            action: 'addPortlet',
            channelID: portlet.id,
            elementID: targetID,
            position: method
        };
        
        // Use the existing persistence component
        if (window.up && window.up.LayoutPreferencesPersistence) {
            const persistence = window.up.LayoutPreferencesPersistence(document.body, {
                saveLayoutUrl: '/uPortal/api/layout'
            });
            
            persistence.update(options, (data) => {
                if (data.error) {
                    console.error('Error adding portlet:', data.error);
                } else {
                    // Reload page to show new portlet
                    window.location.reload();
                }
            });
        }
    }
    
    addPortletToPage(portlet) {
        const getActiveTabId = () => {
            const activeTab = document.querySelector('#portalNavigationList li.active');
            return activeTab ? window.up.defaultNodeIdExtractor(activeTab) : null;
        };
        
        const options = {
            action: 'addPortlet',
            channelID: portlet.id
        };
        
        // Find the first movable portlet to insert before
        const firstChannel = document.querySelector('[id^=portlet_].movable, [id^=portlet_].up-fragment-admin');
        
        if (!firstChannel) {
            // No content on page, add to tab
            options.elementID = getActiveTabId();
        } else {
            // Insert before first movable portlet
            options.elementID = window.up.defaultNodeIdExtractor(firstChannel);
            options.position = 'insertBefore';
        }
        
        // Use the existing persistence component
        if (window.up && window.up.LayoutPreferencesPersistence) {
            const persistence = window.up.LayoutPreferencesPersistence(document.body, {
                saveLayoutUrl: '/uPortal/api/layout'
            });
            
            persistence.update(options, (data) => {
                if (data.error) {
                    console.error('Error adding portlet:', data.error);
                } else {
                    // Reload page to show new portlet
                    window.location.reload();
                }
            });
        }
    }
}

class PortletRegistry {
    constructor(url) {
        this.url = url;
        this.categories = [];
        this.portlets = [];
    }

    async load() {
        return new Promise((resolve, reject) => {
            up.jQuery.ajax({
                url: '/uPortal/api/portletList',
                success: (data) => {
                    this.processRegistryData(data);
                    resolve();
                },
                error: (xhr, status, error) => {
                    console.error('Failed to load portlet registry:', error);
                    this.categories = [];
                    this.portlets = [];
                    resolve();
                },
                dataType: 'json'
            });
        });
    }

    processRegistryData(data) {
        this.categories = [];
        this.portlets = [];
        
        if (data.registry && data.registry.categories) {
            data.registry.categories.forEach(category => {
                this.processCategory(category);
            });
        }
        
        if (data.registry && data.registry.channels) {
            data.registry.channels.forEach(channel => {
                this.portlets.push(this.createPortlet(channel));
            });
        }
    }
    
    processCategory(categoryData) {
        const category = {
            id: categoryData.id,
            name: categoryData.name,
            description: categoryData.description,
            deepPortlets: []
        };
        
        if (categoryData.channels) {
            categoryData.channels.forEach(channel => {
                const portlet = this.createPortlet(channel);
                this.portlets.push(portlet);
                category.deepPortlets.push(portlet);
            });
        }
        
        if (categoryData.categories) {
            categoryData.categories.forEach(subCat => {
                this.processCategory(subCat);
            });
        }
        
        this.categories.push(category);
    }
    
    createPortlet(channel) {
        return {
            id: channel.id,
            title: channel.title,
            name: channel.name,
            fname: channel.fname,
            description: channel.description,
            iconUrl: channel.iconUrl || '/ResourceServingWebapp/rs/tango/0.8.90/32x32/categories/applications-other.png'
        };
    }

    getAllCategories() {
        return this.categories;
    }

    getAllPortlets() {
        return this.portlets;
    }

    getMemberPortlets(categoryId, deep = false) {
        return this.portlets.filter(portlet => {
            if (deep) {
                return portlet.categories && portlet.categories.includes(categoryId);
            }
            return portlet.categoryId === categoryId;
        });
    }
}

class CategoryListView {
    constructor(container, browser) {
        this.container = container.querySelector('.categories');
        this.browser = browser;
        this.refresh();
    }

    refresh() {
        if (!this.container || !this.browser.registry) return;

        // Build categories list
        const categories = [
            { id: '', name: 'ALL', description: 'All Categories' },
            ...this.browser.registry.getAllCategories()
                .filter(cat => cat.id !== 'local.1' && cat.deepPortlets && cat.deepPortlets.length > 0)
                .sort((a, b) => a.name.localeCompare(b.name))
        ];

        // Clear and rebuild
        this.container.innerHTML = '';
        
        categories.forEach(category => {
            const isActive = category.id === this.browser.state.currentCategory;
            const categoryEl = this.createCategoryElement(category, isActive);
            this.container.appendChild(categoryEl);
        });
    }

    createCategoryElement(category, isActive) {
        const template = this.container.querySelector('.category-choice-container');
        const element = template ? template.cloneNode(true) : document.createElement('div');
        
        element.className = `category-choice-container ${isActive ? 'active' : ''}`;
        
        const nameEl = element.querySelector('.category-choice-name');
        if (nameEl) nameEl.textContent = category.name;
        
        const linkEl = element.querySelector('.category-choice-link');
        if (linkEl) {
            linkEl.addEventListener('click', (e) => {
                e.preventDefault();
                this.browser.onCategorySelect(category);
            });
        }
        
        return element;
    }
}

class PortletListView {
    constructor(container, browser) {
        this.container = container.querySelector('.portlet-results');
        this.browser = browser;
        this.pageSize = 6;
        this.currentPage = 0;
        this.refresh();
    }

    refresh() {
        if (!this.container || !this.browser.registry) return;

        // Get filtered portlets
        const portlets = this.getFilteredPortlets();
        
        // Simple pagination
        const startIdx = this.currentPage * this.pageSize;
        const endIdx = startIdx + this.pageSize;
        const pagePortlets = portlets.slice(startIdx, endIdx);
        
        // Render portlets
        this.renderPortlets(pagePortlets);
        this.renderPagination(portlets.length);
    }

    getFilteredPortlets() {
        let portlets = this.browser.state.currentCategory 
            ? this.browser.registry.getMemberPortlets(this.browser.state.currentCategory, true)
            : this.browser.registry.getAllPortlets();

        // Apply search filter
        if (this.browser.state.portletRegex) {
            portlets = portlets.filter(portlet => 
                this.browser.state.portletRegex.test(portlet.title) ||
                this.browser.state.portletRegex.test(portlet.name) ||
                this.browser.state.portletRegex.test(portlet.fname) ||
                this.browser.state.portletRegex.test(portlet.description)
            );
        }

        return portlets.sort((a, b) => a.title.localeCompare(b.title));
    }

    renderPortlets(portlets) {
        const listContainer = this.container.querySelector('.portlet-list');
        if (!listContainer) return;

        listContainer.innerHTML = '';
        
        portlets.forEach(portlet => {
            const portletEl = this.createPortletElement(portlet);
            listContainer.appendChild(portletEl);
        });
        
        // Notify drag manager if it exists
        if (this.browser.dragManager) {
            // Small delay to ensure DOM is ready
            setTimeout(() => {
                this.browser.dragManager.initializeDraggables();
            }, 0);
        }
    }

    createPortletElement(portlet) {
        const portletEl = document.createElement('li');
        portletEl.className = 'result-item portlet';
        portletEl.title = `${portlet.title} (${portlet.name})`;
        portletEl.setAttribute('data-portlet-id', portlet.id);
        
        portletEl.innerHTML = `
            <div class="ri-wrapper portlet-wrapper">
                <a class="ri-utility portlet-thumb-gripper" href="#" title="Drag to add content"><span>Drag Handle</span></a>
                <a href="#" class="ri-link portlet-thumb-link">
                    <span>${this.browser.options.buttonText}</span>
                </a>
                <div class="ri-content portlet-thumb-content ui-helper-clearfix">
                    <div class="ri-titlebar portlet-thumb-titlebar">${portlet.title}</div>
                    <div class="ri-icon portlet-thumb-icon" style="background: url(${portlet.iconUrl || '/ResourceServingWebapp/rs/tango/0.8.90/32x32/categories/applications-other.png'}) top left no-repeat;"><span>Thumbnail</span></div>
                    <div class="ri-description portlet-thumb-description">${portlet.description || ''}</div>
                </div>
            </div>
        `;
        
        const linkEl = portletEl.querySelector('.portlet-thumb-link');
        if (linkEl) {
            linkEl.addEventListener('click', (e) => {
                e.preventDefault();
                this.browser.onPortletSelect(portlet);
            });
        }
        
        return portletEl;
    }

    renderPagination(totalItems) {
        const totalPages = Math.ceil(totalItems / this.pageSize);
        if (totalPages <= 1) return;

        const pagerEl = this.container.querySelector('.pager');
        if (pagerEl) {
            pagerEl.innerHTML = `
                <div class="pager-button-up flc-pager-previous">
                    <a class="pager-button-up-inner" href="#" ${this.currentPage === 0 ? 'style="opacity: 0.5; pointer-events: none;"' : ''}>
                        <span>up</span>
                    </a>
                </div>
                <div class="pager-pagination">
                    Page ${this.currentPage + 1} of ${totalPages}
                </div>
                <div class="pager-button-down flc-pager-next">
                    <a class="pager-button-down-inner" href="#" ${this.currentPage === totalPages - 1 ? 'style="opacity: 0.5; pointer-events: none;"' : ''}>
                        <span>down</span>
                    </a>
                </div>
            `;
            
            const prevBtn = pagerEl.querySelector('.pager-button-up-inner');
            const nextBtn = pagerEl.querySelector('.pager-button-down-inner');
            
            prevBtn?.addEventListener('click', (e) => {
                e.preventDefault();
                if (this.currentPage > 0) {
                    this.currentPage--;
                    this.refresh();
                }
            });
            
            nextBtn?.addEventListener('click', (e) => {
                e.preventDefault();
                if (this.currentPage < totalPages - 1) {
                    this.currentPage++;
                    this.refresh();
                }
            });
        }
    }
}

// Global initialization function to replace Fluid component
window.up = window.up || {};
window.up.PortletBrowser = function(container, gallery, options) {
    return new PortletBrowser(container, gallery, options);
};