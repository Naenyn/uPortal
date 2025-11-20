/*
 * Modern replacement for Fluid-based PortalGallery component
 * Replaces up-layout-gallery.js with vanilla JavaScript implementation
 */
'use strict';

class PortalGallery {
    constructor(container, options = {}) {
        this.container = typeof container === 'string' ? document.querySelector(container) : container;
        this.options = { ...this.defaults, ...options };
        this.panes = new Map();
        this.isOpen = false;
        this.init();
    }

    get defaults() {
        return {
            isOpen: false,
            openSpeed: 500,
            closeSpeed: 50
        };
    }

    init() {
        this.createPanes();
        this.bindEvents();
        
        if (this.options.isOpen) {
            this.openGallery();
        }
    }

    createPanes() {
        // Create browse content pane
        this.panes.set('add-content', new BrowseContentPane(this.container, this, {
            key: 'add-content',
            selectors: {
                pane: '.add-content',
                paneLink: '.add-content-link'
            }
        }));

        // Create use content pane
        this.panes.set('use-content', new UseContentPane(this.container, this, {
            key: 'use-content',
            selectors: {
                pane: '.use-content',
                paneLink: '.use-content-link'
            }
        }));

        this.panes.set('skin', new SkinPane(this.container, this, {
            key: 'skin',
            selectors: {
                pane: '.skins',
                paneLink: '.skin-link'
            }
        }));

        this.panes.set('layout', new LayoutPane(this.container, this, {
            key: 'layout',
            selectors: {
                pane: '.layouts',
                paneLink: '.layout-link'
            }
        }));
    }

    bindEvents() {
        // Gallery handle click
        const handle = this.container.querySelector('.handle span');
        if (handle) {
            handle.addEventListener('click', () => {
                this.isOpen ? this.closeGallery() : this.openGallery();
            });
        }

        // Close button
        const closeBtn = this.container.querySelector('.close-button');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.closeGallery());
        }
    }

    openGallery() {
        this.isOpen = true;
        const handle = this.container.querySelector('.handle span');
        const inner = this.container.querySelector('.gallery-inner');
        
        if (handle) handle.classList.add('handle-arrow-up');
        if (inner) {
            inner.style.display = 'block';
            // Simple slide down effect
            inner.style.height = '0px';
            inner.style.overflow = 'hidden';
            const height = inner.scrollHeight;
            inner.style.transition = `height ${this.options.openSpeed}ms ease`;
            inner.style.height = height + 'px';
            
            setTimeout(() => {
                inner.style.height = 'auto';
                inner.style.overflow = 'visible';
            }, this.options.openSpeed);
        }

        // Show appropriate pane based on permissions
        const canAddChildren = document.querySelector('#portalPageBodyColumns .portal-page-column.canAddChildren, #portalPageBodyColumns .portal-page-column.up-fragment-admin');
        if (canAddChildren) {
            this.showPane('add-content');
        } else {
            this.showPane('use-content');
            this.hidePaneLink('add-content');
        }
    }

    closeGallery() {
        this.isOpen = false;
        const handle = this.container.querySelector('.handle span');
        const inner = this.container.querySelector('.gallery-inner');
        
        if (handle) handle.classList.remove('handle-arrow-up');
        if (inner) {
            inner.style.transition = `height ${this.options.closeSpeed}ms ease`;
            inner.style.height = '0px';
            setTimeout(() => {
                inner.style.display = 'none';
            }, this.options.closeSpeed);
        }
    }

    showPane(key) {
        this.panes.forEach((pane, paneKey) => {
            if (paneKey === key) {
                pane.showPane();
            } else {
                pane.hidePane();
            }
        });
    }

    hidePaneLink(key) {
        const pane = this.panes.get(key);
        if (pane && pane.hidePaneLink) {
            pane.hidePaneLink();
        }
    }

    showPaneLink(key) {
        const pane = this.panes.get(key);
        if (pane && pane.showPaneLink) {
            pane.showPaneLink();
        }
    }

    showLoading() {
        const ui = this.container.querySelector('.content-wrapper .content');
        const loading = this.container.querySelector('.gallery-loader');
        
        if (ui) ui.style.display = 'none';
        if (loading) loading.style.display = 'block';
    }

    hideLoading() {
        const ui = this.container.querySelector('.content-wrapper .content');
        const loading = this.container.querySelector('.gallery-loader');
        
        if (ui) ui.style.display = 'block';
        if (loading) {
            loading.style.transition = 'opacity 0.5s ease';
            loading.style.opacity = '0';
            setTimeout(() => {
                loading.style.display = 'none';
                loading.style.opacity = '1';
            }, 500);
        }
    }
}

class GalleryPane {
    constructor(container, gallery, options = {}) {
        this.container = container;
        this.gallery = gallery;
        this.options = options;
        this.initialized = false;
        this.bindEvents();
    }

    bindEvents() {
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        if (paneLink) {
            paneLink.addEventListener('click', () => {
                this.gallery.showPane(this.options.key);
            });
        }
    }

    showPane() {
        if (!this.initialized) {
            this.gallery.showLoading();
            if (this.options.onInitialize) {
                this.options.onInitialize();
            }
            this.initialized = true;
            this.gallery.hideLoading();
        }

        const pane = this.container.querySelector(this.options.selectors.pane);
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        
        if (pane) pane.style.display = 'block';
        if (paneLink) paneLink.classList.add('active');
    }

    hidePane() {
        const pane = this.container.querySelector(this.options.selectors.pane);
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        
        if (pane) pane.style.display = 'none';
        if (paneLink) paneLink.classList.remove('active');
    }

    hidePaneLink() {
        const pane = this.container.querySelector(this.options.selectors.pane);
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        
        if (pane) pane.style.display = 'none';
        if (paneLink) {
            paneLink.style.display = 'none';
            paneLink.classList.remove('active');
        }
    }

    showPaneLink() {
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        if (paneLink) paneLink.style.display = 'block';
    }
}

class BrowseContentPane extends GalleryPane {
    constructor(container, gallery, options) {
        super(container, gallery, options);
        this.portletBrowser = null;
    }

    showPane() {
        if (!this.initialized) {
            this.gallery.showLoading();
            
            // Initialize portlet browser
            const paneElement = this.container.querySelector(this.options.selectors.pane);
            if (paneElement) {
                this.portletBrowser = new PortletBrowser(paneElement, this.gallery, {
                    portletListUrl: 'v4-3/dlm/portletRegistry.json',
                    buttonText: 'Add',
                    buttonAction: 'add'
                });
            }
            
            this.initialized = true;
            this.gallery.hideLoading();
        }

        super.showPane();
    }
}

class UseContentPane extends GalleryPane {
    constructor(container, gallery, options) {
        super(container, gallery, options);
        this.portletBrowser = null;
    }

    showPane() {
        if (!this.initialized) {
            this.gallery.showLoading();
            
            // Initialize portlet browser for "Use It" functionality
            const paneElement = this.container.querySelector(this.options.selectors.pane);
            if (paneElement) {
                this.portletBrowser = new PortletBrowser(paneElement, this.gallery, {
                    portletListUrl: 'v4-3/dlm/portletRegistry.json',
                    buttonText: 'Use',
                    buttonAction: 'use'
                });
            }
            
            this.initialized = true;
            this.gallery.hideLoading();
        }

        super.showPane();
    }
}

class SkinPane extends GalleryPane {
    constructor(container, gallery, options) {
        super(container, gallery, options);
        this.skinSelector = null;
    }

    showPane() {
        if (!this.initialized) {
            this.gallery.showLoading();
            
            // Initialize skin selector
            const paneElement = this.container.querySelector(this.options.selectors.pane);
            if (paneElement) {
                this.skinSelector = new SkinSelector(paneElement, {
                    onSelectSkin: (skin) => {
                        // Use same persistence mechanism as Fluid
                        if (window.up && window.up.LayoutPreferencesPersistence) {
                            const persistence = window.up.LayoutPreferencesPersistence(document.body, {
                                saveLayoutUrl: '/uPortal/api/layout'
                            });
                            
                            persistence.update({
                                action: 'chooseSkin',
                                skinName: skin.key
                            }, () => {
                                window.location.reload();
                            });
                        }
                    }
                });
            }
            
            this.initialized = true;
            this.gallery.hideLoading();
        }

        super.showPane();
    }
}

class PortletBrowser {
    constructor(container, gallery, options = {}) {
        this.container = container;
        this.gallery = gallery;
        this.options = { buttonText: 'Add', buttonAction: 'add', ...options };
        this.registry = null;
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
    }

    createPortletElement(portlet) {
        const portletEl = document.createElement('li');
        portletEl.className = 'result-item portlet';
        portletEl.title = `${portlet.title} (${portlet.name})`;
        
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

class LayoutPane extends GalleryPane {
    constructor(container, gallery, options) {
        super(container, gallery, options);
        this.layoutSelector = null;
    }

    showPane() {
        if (!this.initialized) {
            this.gallery.showLoading();
            
            // Initialize layout selector
            const paneElement = this.container.querySelector(this.options.selectors.pane);
            if (paneElement) {
                this.layoutSelector = new LayoutSelector(paneElement, {
                    onLayoutSelect: (layout) => {
                        // Use same persistence mechanism as Fluid
                        if (window.up && window.up.LayoutPreferencesPersistence) {
                            const persistence = window.up.LayoutPreferencesPersistence(document.body, {
                                saveLayoutUrl: '/uPortal/api/layout'
                            });
                            
                            const getActiveTabId = () => {
                                const activeTab = document.querySelector('#portalNavigationList li.active');
                                return activeTab ? window.up.defaultNodeIdExtractor(activeTab) : null;
                            };
                            
                            // Server expects at least 2 widths, pad single column with 0
                            const widths = layout.columns.length === 1 ? [layout.columns[0], 0] : layout.columns;
                            
                            const options = {
                                action: 'changeColumns',
                                tabId: getActiveTabId(),
                                widths: widths
                            };
                            
                            console.log('Sending layout update:', options);
                            console.log('Layout columns array:', layout.columns);
                            console.log('Widths array length:', layout.columns.length);
                            
                            persistence.update(options, (data) => {
                                console.log('Layout update response:', data);
                                if (data && data.error) {
                                    console.error('Layout update error:', data.error);
                                } else {
                                    window.location.reload();
                                }
                            });
                        }
                    }
                });
            }
            
            this.initialized = true;
            this.gallery.hideLoading();
        }

        super.showPane();
    }
}

class SkinSelector {
    constructor(container, options = {}) {
        this.container = container;
        this.options = options;
        this.skins = [];
        this.init();
    }

    async init() {
        try {
            await this.loadSkins();
            this.render();
        } catch (error) {
            console.error('Failed to load skins:', error);
        }
    }

    async loadSkins() {
        // Try to load skinList.xml from respondr skin directory
        try {
            const response = await fetch('/uPortal/media/skins/respondr/skinList.xml');
            if (response.ok) {
                const xmlText = await response.text();
                const parser = new DOMParser();
                const xmlDoc = parser.parseFromString(xmlText, 'text/xml');
                this.parseSkinListXML(xmlDoc);
            } else {
                this.useDefaultSkins();
            }
        } catch (error) {
            console.warn('Could not load skinList.xml, using default skins:', error);
            this.useDefaultSkins();
        }
    }

    parseSkinListXML(xmlDoc) {
        const skinNodes = xmlDoc.querySelectorAll('skin');
        this.skins = [];
        
        skinNodes.forEach(skinNode => {
            const key = skinNode.querySelector('skin-key')?.textContent;
            const name = skinNode.querySelector('skin-name')?.textContent;
            const description = skinNode.querySelector('skin-description')?.textContent;
            
            if (key && name) {
                this.skins.push({
                    key,
                    name,
                    description: description || name,
                    thumbnailPath: `/uPortal/media/skins/respondr/${key}/thumb.gif`
                });
            }
        });
    }

    useDefaultSkins() {
        // Fallback to available skins
        this.skins = [
            { key: 'defaultSkin', name: 'Default Skin', description: 'Basic responsive skin', thumbnailPath: '/uPortal/media/skins/respondr/defaultSkin/thumb.gif' }
        ];
    }

    render() {
        const skinsList = this.container.querySelector('.skins-list');
        if (!skinsList) return;

        skinsList.innerHTML = '';
        
        this.skins.forEach(skin => {
            const skinEl = this.createSkinElement(skin);
            skinsList.appendChild(skinEl);
        });
    }

    createSkinElement(skin) {
        const skinEl = document.createElement('li');
        skinEl.className = 'results-item skin';
        
        skinEl.innerHTML = `
            <div class="ri-wrapper skins-wrapper">
                <a class="ri-link skin-link" href="#">
                    <div class="ri-titlebar skin-titlebar">${skin.name}</div>
                    <div class="ri-content">
                        <div class="ri-icon skin-thumb" style="background: url(${skin.thumbnailPath}) top left no-repeat;">
                            <span>Thumbnail</span>
                        </div>
                    </div>
                </a>
            </div>
        `;
        
        const linkEl = skinEl.querySelector('.skin-link');
        if (linkEl) {
            linkEl.addEventListener('click', (e) => {
                e.preventDefault();
                
                // Remove active class from all skins
                this.container.querySelectorAll('.skin.selected').forEach(el => {
                    el.classList.remove('selected');
                });
                
                // Add active class to selected skin
                skinEl.classList.add('selected');
                
                // Fire selection event
                if (this.options.onSelectSkin) {
                    this.options.onSelectSkin(skin);
                }
            });
        }
        
        return skinEl;
    }
}

class LayoutSelector {
    constructor(container, options = {}) {
        this.container = container;
        this.options = options;
        this.layouts = [
            {nameKey: 'fullWidth', columns: [100]},
            {nameKey: 'narrowWide', columns: [40, 60]},
            {nameKey: 'even', columns: [50, 50]},
            {nameKey: 'wideNarrow', columns: [60, 40]},
            {nameKey: 'even', columns: [33, 34, 33]},
            {nameKey: 'narrowWideNarrow', columns: [25, 50, 25]},
            {nameKey: 'even', columns: [25, 25, 25, 25]}
        ];
        this.strings = {
            fullWidth: 'Full-width',
            narrowWide: 'Narrow, wide',
            even: 'Even',
            wideNarrow: 'Wide, narrow',
            narrowWideNarrow: 'Narrow, wide, narrow',
            column: 'Column',
            columns: 'Columns'
        };
        this.currentLayout = this.getCurrentLayout().slice();
        this.init();
    }

    getCurrentLayout() {
        const columns = [];
        const columnElements = document.querySelectorAll('#portalPageBodyColumns > [id^=column_]');
        
        console.log('DEBUG: Found', columnElements.length, 'column elements');
        
        columnElements.forEach((col, index) => {
            console.log('DEBUG: Column', index, 'classes:', col.className);
            
            // Try Bootstrap col-md-* classes first
            const colMdClass = col.className.match(/col-md-([0-9]+)/);
            if (colMdClass) {
                // Convert Bootstrap 12-column grid to percentage
                const bootstrapCols = Number(colMdClass[1]);
                const width = Math.round((bootstrapCols / 12) * 100);
                columns.push(width);
                console.log('DEBUG: Column', index, 'Bootstrap cols:', bootstrapCols, 'width:', width);
            } else {
                // Fallback to fl-container-flex classes
                const flClass = col.className.match(/fl-container-flex([0-9]+)/);
                if (flClass) {
                    const width = Number(flClass[1]);
                    columns.push(width);
                    console.log('DEBUG: Column', index, 'flex width:', width);
                }
            }
        });
        
        // If no flex classes found but columns exist, assume equal distribution
        if (columns.length === 0 && columnElements.length > 0) {
            const equalWidth = Math.floor(100 / columnElements.length);
            console.log('DEBUG: No flex classes found, using equal width:', equalWidth);
            for (let i = 0; i < columnElements.length; i++) {
                columns.push(equalWidth);
            }
        }
        
        const result = columns.length > 0 ? columns : [100];
        console.log('DEBUG: getCurrentLayout returning:', result);
        return result;
    }

    init() {
        this.render();
    }

    layoutsMatch(layout1, layout2) {
        if (layout1.length !== layout2.length) return false;
        
        // Allow for small differences due to Bootstrap grid rounding
        for (let i = 0; i < layout1.length; i++) {
            const diff = Math.abs(layout1[i] - layout2[i]);
            if (diff > 5) return false; // Allow up to 5% difference
        }
        return true;
    }

    render() {
        const layoutsList = this.container.querySelector('.layouts-list');
        if (!layoutsList) return;

        // Refresh current layout before rendering
        this.currentLayout = this.getCurrentLayout().slice();
        
        console.log('DEBUG: Rendering', this.layouts.length, 'layouts');
        console.log('DEBUG: Current layout is:', this.currentLayout);
        
        layoutsList.innerHTML = '';
        
        this.layouts.forEach((layout, index) => {
            console.log('DEBUG: Layout', index, ':', layout.columns, 'nameKey:', layout.nameKey);
            const layoutEl = this.createLayoutElement(layout);
            layoutsList.appendChild(layoutEl);
        });
    }

    createLayoutElement(layout) {
        const layoutEl = document.createElement('li');
        const currentLayoutString = this.currentLayout.join('-');
        const layoutString = layout.columns.join('-');
        
        // Debug logging
        console.log('Current layout:', this.currentLayout, 'string:', currentLayoutString);
        console.log('Layout:', layout.columns, 'string:', layoutString);
        
        // More flexible matching - check if layouts are approximately the same
        const isSelected = this.layoutsMatch(this.currentLayout, layout.columns);
        
        layoutEl.className = `results-item layout ${isSelected ? 'selected' : ''}`;
        
        const columnText = layout.columns.length === 1 ? this.strings.column : this.strings.columns;
        const layoutName = this.strings[layout.nameKey] || layout.nameKey;
        
        layoutEl.innerHTML = `
            <div class="ri-wrapper layout-wrapper">
                <a class="ri-link layout-link" href="#">
                    <div class="ri-titlebar layout-titlebar">${layout.columns.length} ${columnText}</div>
                    <div class="ri-content">
                        <div class="ri-icon layout-thumb" style="background: url(/uPortal/media/skins/respondr/common/images/layout_${layoutString}.svg) top left no-repeat;">
                            <span>Thumbnail</span>
                        </div>
                        <div class="ri-description layout-description">${layoutName}</div>
                    </div>
                </a>
            </div>
        `;
        
        const linkEl = layoutEl.querySelector('.layout-link');
        if (linkEl) {
            linkEl.addEventListener('click', (e) => {
                e.preventDefault();
                
                // Check if this is already the current layout
                console.log('DEBUG: User clicked layout:', layout.columns, 'nameKey:', layout.nameKey);
                console.log('DEBUG: Current layout before click:', this.currentLayout);
                
                if (this.layoutsMatch(this.currentLayout, layout.columns)) {
                    console.log('DEBUG: Layout is already current, skipping update');
                    return;
                }
                
                // Remove selected class from all layouts
                this.container.querySelectorAll('.layout.selected').forEach(el => {
                    el.classList.remove('selected');
                });
                
                // Add selected class to clicked layout
                layoutEl.classList.add('selected');
                
                // Fire selection event BEFORE updating current layout
                if (this.options.onLayoutSelect) {
                    this.options.onLayoutSelect(layout);
                }
                
                // Update current layout after successful callback
                this.currentLayout = layout.columns.slice();
            });
        }
        
        return layoutEl;
    }
}

// Global initialization function to replace Fluid component
window.up = window.up || {};
window.up.PortalGallery = function(container, options) {
    return new PortalGallery(container, options);
};

// Export for use by up.LayoutPreferences