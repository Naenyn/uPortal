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
    return new PortalGallery(container, options);
};

// Export for use by up.LayoutPreferences