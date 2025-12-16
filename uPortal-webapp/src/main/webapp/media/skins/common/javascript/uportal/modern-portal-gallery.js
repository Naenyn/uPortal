/*
 * Modern replacement for Fluid-based PortalGallery component
 * Replaces up-layout-gallery.js with vanilla JavaScript implementation
 */
'use strict';

class PortalGallery {
    constructor(container, options = {}) {
        console.log('PortalGallery constructor called with options:', options);
        this.container = typeof container === 'string' ? document.querySelector(container) : container;
        this.options = { ...this.defaults, ...options };
        console.log('Final options after merge:', this.options);
        this.panes = new Map();
        this.isOpen = false;
        console.log('Initial isOpen set to false in constructor');
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
        console.log('PortalGallery.init() called');
        console.log('Initial options:', this.options);
        console.log('Initial isOpen state:', this.isOpen);
        
        this.createPanes();
        this.bindEvents();
        
        // Ensure gallery starts closed
        this.isOpen = false;
        console.log('Set isOpen to false');
        
        const outer = document.querySelector('#customizeOptions');
        const inner = this.container.querySelector('.gallery-inner');
        
        console.log('Found outer element:', !!outer, outer ? outer.style.display : 'null');
        console.log('Found inner element:', !!inner, inner ? inner.style.display : 'null');
        
        if (outer) {
            outer.style.display = 'none';
            console.log('Set outer display to none');
        }
        if (inner) {
            inner.style.display = 'none';
            console.log('Set inner display to none');
        }
        
        // Remove any handle arrow up class that might be set
        const handle = this.container.querySelector('.handle span');
        if (handle) {
            handle.classList.remove('handle-arrow-up');
            console.log('Removed handle-arrow-up class');
        }
        
        // Only open if explicitly requested (should not happen on admin page)
        if (this.options.isOpen) {
            console.log('Opening gallery because options.isOpen is true');
            this.openGallery();
        } else {
            console.log('Gallery should remain closed');
        }
        
        console.log('PortalGallery.init() completed');
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

        // Customize button click
        setTimeout(() => {
            const customizeBtn = document.getElementById('customizeButton');
            console.log('Looking for customizeButton:', !!customizeBtn);
            if (customizeBtn) {
                customizeBtn.addEventListener('click', (e) => {
                    console.log('Customize button clicked');
                    e.preventDefault();
                    e.stopPropagation();
                    
                    // Use the proper gallery methods instead of direct DOM manipulation
                    if (this.isOpen) {
                        console.log('Closing drawer via customize button');
                        this.closeGallery();
                    } else {
                        console.log('Opening drawer via customize button');
                        this.openGallery();
                    }
                });
            }
        }, 100);

        // Close button
        const closeBtn = this.container.querySelector('.close-button');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.closeGallery());
        }
    }

    openGallery() {
        console.log('FLOW: openGallery() called');
        this.isOpen = true;
        const handle = this.container.querySelector('.handle span');
        const outer = document.querySelector('#customizeOptions');
        const inner = this.container.querySelector('.gallery-inner');
        
        console.log('FLOW: Setting up loading spinner before animation');
        
        // Show loading spinner BEFORE opening drawer
        const canAddChildren = document.querySelector('#portalPageBodyColumns .portal-page-column.canAddChildren, #portalPageBodyColumns .portal-page-column.up-fragment-admin');
        if (canAddChildren) {
            console.log('FLOW: Pre-initializing add-content pane with loading');
            this.panes.get('add-content').showLoadingOnly();
        } else {
            console.log('FLOW: Pre-initializing use-content pane with loading');
            this.panes.get('use-content').showLoadingOnly();
            this.hidePaneLink('add-content');
        }
        
        console.log('FLOW: Starting drawer animation - outer:', !!outer, 'inner:', !!inner);
        
        if (handle) handle.classList.add('handle-arrow-up');
        
        // Update customize button arrow
        const customizeBtn = document.getElementById('customizeButton');
        const arrow = customizeBtn?.querySelector('i');
        if (arrow) {
            arrow.className = 'fa fa-caret-up';
        }

        if (outer && inner) {
            inner.style.display = 'block';
            up.jQuery(outer).slideDown(300, 'swing', () => {
                console.log('FLOW: Animation complete, initializing content');
                // Now initialize the actual content
                if (canAddChildren) {
                    this.panes.get('add-content').initializeContent();
                } else {
                    this.panes.get('use-content').initializeContent();
                }
            });
        }
    }

    closeGallery() {
        console.log('closeGallery() called');
        this.isOpen = false;
        const handle = this.container.querySelector('.handle span');
        const outer = document.querySelector('#customizeOptions');
        const inner = this.container.querySelector('.gallery-inner');
        
        console.log('Closing gallery - handle:', !!handle, 'outer:', !!outer, 'inner:', !!inner);
        
        if (handle) handle.classList.remove('handle-arrow-up');
        
        // Update customize button arrow
        const customizeBtn = document.getElementById('customizeButton');
        const arrow = customizeBtn?.querySelector('i');
        if (arrow) {
            arrow.className = 'fa fa-caret-down';
        }
        if (outer && inner) {
            up.jQuery(outer).slideUp(300, 'swing', () => {
                inner.style.display = 'none';
            });
        }
        
        console.log('closeGallery() completed');
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
        console.log('FLOW: showLoading() called');
        // Find the portlet list container
        const portletList = this.container.querySelector('#addContentPortletList, #useContentPortletList, .portlet-list');
        
        if (portletList) {
            console.log('FLOW: Found portlet list, showing loading spinner');
            portletList.innerHTML = `
                <div class="loading-indicator" style="
                    width: 100%;
                    height: 200px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    flex-direction: column;
                    color: #666;
                    font-size: 14px;
                ">
                    <div style="
                        width: 24px;
                        height: 24px;
                        border: 3px solid #ddd;
                        border-top: 3px solid #007bff;
                        border-radius: 50%;
                        animation: spin 1s linear infinite;
                        margin-bottom: 15px;
                    "></div>
                    Loading portlets...
                </div>
                <style>
                    @keyframes spin {
                        0% { transform: rotate(0deg); }
                        100% { transform: rotate(360deg); }
                    }
                </style>
            `;
        } else {
            console.log('FLOW: No portlet list found for loading indicator');
        }
    }

    hideLoading() {
        console.log('FLOW: hideLoading() called');
        // The loading indicator will be replaced by actual content in PortletListView.renderPortlets()
        // No need to explicitly hide it since renderPortlets() clears innerHTML
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
        const pane = this.container.querySelector(this.options.selectors.pane);
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        
        if (pane) pane.style.display = 'block';
        if (paneLink) paneLink.classList.add('active');
        
        if (!this.initialized) {
            if (this.options.onInitialize) {
                this.options.onInitialize();
            }
            this.initialized = true;
        }
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

    showLoadingOnly() {
        console.log('FLOW: BrowseContentPane.showLoadingOnly()');
        const pane = this.container.querySelector(this.options.selectors.pane);
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        
        if (pane) pane.style.display = 'block';
        if (paneLink) paneLink.classList.add('active');
        
        if (!this.initialized) {
            this.gallery.showLoading();
        }
    }
    
    initializeContent() {
        console.log('FLOW: BrowseContentPane.initializeContent()');
        if (!this.initialized) {
            const pane = this.container.querySelector(this.options.selectors.pane);
            const startTime = Date.now();
            
            this.portletBrowser = new PortletBrowser(pane, this.gallery, {
                portletListUrl: 'v4-3/dlm/portletRegistry.json',
                buttonText: 'Add',
                buttonAction: 'add',
                onLoad: () => {
                    const elapsed = Date.now() - startTime;
                    const minTime = 1000; // Minimum 1000ms spinner display
                    const delay = Math.max(0, minTime - elapsed);
                    
                    console.log('FLOW: Content ready, enforcing minimum spinner time:', delay + 'ms');
                    setTimeout(() => {
                        console.log('FLOW: Hiding spinner after minimum time');
                        this.gallery.hideLoading();
                    }, delay);
                }
            });
            this.initialized = true;
        }
    }
    
    showPane() {
        // For tab switching after initialization
        const pane = this.container.querySelector(this.options.selectors.pane);
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        
        if (pane) pane.style.display = 'block';
        if (paneLink) paneLink.classList.add('active');
    }
}

class UseContentPane extends GalleryPane {
    constructor(container, gallery, options) {
        super(container, gallery, options);
        this.portletBrowser = null;
    }

    showLoadingOnly() {
        console.log('FLOW: UseContentPane.showLoadingOnly()');
        const pane = this.container.querySelector(this.options.selectors.pane);
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        
        if (pane) pane.style.display = 'block';
        if (paneLink) paneLink.classList.add('active');
        
        if (!this.initialized) {
            this.gallery.showLoading();
        }
    }
    
    initializeContent() {
        console.log('FLOW: UseContentPane.initializeContent()');
        if (!this.initialized) {
            const pane = this.container.querySelector(this.options.selectors.pane);
            const startTime = Date.now();
            
            this.portletBrowser = new PortletBrowser(pane, this.gallery, {
                portletListUrl: 'v4-3/dlm/portletRegistry.json',
                buttonText: 'Use',
                buttonAction: 'use',
                onLoad: () => {
                    const elapsed = Date.now() - startTime;
                    const minTime = 1000; // Minimum 1000ms spinner display
                    const delay = Math.max(0, minTime - elapsed);
                    
                    console.log('FLOW: Content ready, enforcing minimum spinner time:', delay + 'ms');
                    setTimeout(() => {
                        console.log('FLOW: Hiding spinner after minimum time');
                        this.gallery.hideLoading();
                    }, delay);
                }
            });
            this.initialized = true;
        }
    }
    
    showPane() {
        // For tab switching after initialization
        const pane = this.container.querySelector(this.options.selectors.pane);
        const paneLink = this.container.querySelector(this.options.selectors.paneLink);
        
        if (pane) pane.style.display = 'block';
        if (paneLink) paneLink.classList.add('active');
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

// PortletBrowser is now in modern-portlet-browser.js

// PortletRegistry is now in modern-portlet-browser.js

// CategoryListView is now in modern-portlet-browser.js

// PortletListView is now in modern-portlet-browser.js

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

// LayoutDraggableManager is now in modern-layout-draggable-manager.js

// Global initialization function to replace Fluid component
window.up = window.up || {};
window.up.PortalGallery = function(container, options) {
    const gallery = new PortalGallery(container, options);
    window.up.gallery = gallery; // Store global reference
    return gallery;
};

// Export for use by up.LayoutPreferences