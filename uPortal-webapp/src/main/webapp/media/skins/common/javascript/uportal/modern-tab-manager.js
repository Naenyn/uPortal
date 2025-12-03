/*
 * Modern replacement for Fluid-based TabManager component
 * Replaces up-tab-manager.js with vanilla JavaScript implementation
 */
'use strict';

class ModernTabManager {
    constructor(container, options = {}) {
        this.container = typeof container === 'string' ? document.querySelector(container) : container;
        this.options = {
            selectors: {
                text: '.flc-inlineEdit-text',
                edit: '.flc-inlineEditable', 
                remove: '.portal-navigation-delete',
                add: '.portal-navigation-add',
                columns: '.flc-reorderer-column',
                modules: '.movable,.up-fragment-admin',
                lockedModules: '.locked:not(.up-fragment-admin)',
                grabHandle: '.portal-navigation-gripper',
                tabList: '#portalNavigationList',
                tabListItems: '.portal-navigation',
                tabGroup: '#activeTabGroup'
            },
            styles: {
                lockedTab: 'locked',
                singleTab: 'single', 
                firstTab: 'first',
                lastTab: 'last'
            },
            addTabLabel: 'My Tab',
            addTabWidths: [50, 50],
            insertBefore: 'insertBefore',
            appendAfter: 'appendAfter',
            tabContext: 'header',
            numberOfPortlets: 0,
            submitOnEnter: true,
            ...options
        };

        this.events = {
            onTabEdit: options.onTabEdit || (() => {}),
            onTabRemove: options.onTabRemove || (() => {}),
            onTabAdd: options.onTabAdd || (() => {}),
            onTabMove: options.onTabMove || (() => {})
        };

        this.inlineEditor = null;
        this.reorderLayout = null;
        this.init();
    }

    init() {
        this.initializeInlineEdit();
        this.initializeRemoveHandler();
        this.initializeAddHandler();
        this.initializeTabReordering();
    }

    initializeInlineEdit() {
        const editElement = this.container.querySelector(this.options.selectors.edit);
        const textElement = this.container.querySelector(this.options.selectors.text);
        
        if (!editElement || !textElement) return;

        // Create inline editor using existing Fluid implementation for now
        if (window.up && window.up.fluid && window.up.fluid.inlineEdits) {
            this.inlineEditor = window.up.fluid.inlineEdits(this.container, {
                selectors: {
                    text: this.options.selectors.text,
                    edit: this.options.selectors.edit
                },
                listeners: {
                    afterBeginEdit: () => {
                        this.hideEditControls();
                    },
                    afterFinishEdit: (newValue, oldValue, editNode, viewNode) => {
                        this.showEditControls();
                        this.events.onTabEdit(newValue, oldValue, editNode, viewNode);
                    }
                },
                submitOnEnter: this.options.submitOnEnter,
                useTooltip: false
            });
        }

        // Add hover effects for text editing
        textElement.addEventListener('mouseenter', () => {
            textElement.style.cursor = 'text';
            textElement.focus();
        });

        textElement.addEventListener('mouseleave', () => {
            textElement.style.cursor = 'pointer';
            textElement.blur();
        });

        // Auto-trigger edit mode for new tabs
        const numberOfPortlets = parseInt(this.options.numberOfPortlets);
        if (numberOfPortlets === 0 && textElement.textContent.trim() === this.options.addTabLabel) {
            textElement.click();
        }
    }

    hideEditControls() {
        const removeEl = this.container.querySelector(this.options.selectors.remove);
        const gripperEl = this.container.querySelector(this.options.selectors.grabHandle);
        const editEl = this.container.querySelector(this.options.selectors.edit);
        
        if (removeEl) removeEl.style.display = 'none';
        if (gripperEl) gripperEl.style.display = 'none';
        if (editEl) editEl.style.display = 'none';
    }

    showEditControls() {
        const removeEl = this.container.querySelector(this.options.selectors.remove);
        const gripperEl = this.container.querySelector(this.options.selectors.grabHandle);
        const editEl = this.container.querySelector(this.options.selectors.edit);
        
        if (removeEl) removeEl.style.display = '';
        if (gripperEl && gripperEl.classList.contains('active')) gripperEl.style.display = '';
        if (editEl) editEl.style.display = '';
    }

    initializeRemoveHandler() {
        const removeElement = this.container.querySelector(this.options.selectors.remove);
        if (removeElement) {
            removeElement.addEventListener('click', (e) => {
                e.preventDefault();
                this.events.onTabRemove(e.target);
            });
        }
    }

    initializeAddHandler() {
        // Look for add button in the navigation list, not just within this container
        const addElement = document.querySelector(this.options.selectors.add);
        if (addElement) {
            const tabGroupElement = this.container.querySelector(this.options.selectors.tabGroup);
            const tabGroup = tabGroupElement ? tabGroupElement.textContent.trim() : '';
            
            addElement.addEventListener('click', (e) => {
                e.preventDefault();
                console.log('Add tab clicked:', this.options.addTabLabel, this.options.addTabWidths, tabGroup);
                this.events.onTabAdd(
                    this.options.addTabLabel,
                    this.options.addTabWidths,
                    tabGroup
                );
            });
        } else {
            console.warn('Add tab button not found with selector:', this.options.selectors.add);
        }
    }

    initializeTabReordering() {
        this.manageLockedTabs();
        this.setupReorderLayout();
    }

    manageLockedTabs() {
        const tabList = this.container.querySelector(this.options.selectors.tabList);
        if (!tabList) return;

        const lockedTabs = tabList.querySelectorAll(this.options.selectors.lockedModules);
        const lastLockedTab = lockedTabs[lockedTabs.length - 1];

        if (lastLockedTab) {
            // Hide grab handle on last locked tab
            const grabHandle = lastLockedTab.querySelector(this.options.selectors.grabHandle);
            if (grabHandle) grabHandle.style.display = 'none';

            // Add locked class and hide grab handles on all previous tabs
            let current = lastLockedTab.previousElementSibling;
            while (current) {
                current.classList.add(this.options.styles.lockedTab);
                const handle = current.querySelector(this.options.selectors.grabHandle);
                if (handle) handle.style.display = 'none';
                current = current.previousElementSibling;
            }
        }
    }

    setupReorderLayout() {
        // Use existing Fluid reorderLayout for now
        if (window.up && window.up.fluid && window.up.fluid.reorderLayout) {
            this.reorderLayout = window.up.fluid.reorderLayout(this.container, {
                selectors: {
                    columns: this.options.selectors.columns,
                    modules: this.options.selectors.modules,
                    lockedModules: this.options.selectors.lockedModules,
                    grabHandle: this.options.tabContext === 'header' ? this.options.selectors.grabHandle : ''
                },
                styles: {
                    defaultStyle: `fl-reorderer-${this.options.tabContext}-movable-default`,
                    selected: `fl-reorderer-${this.options.tabContext}-movable-selected`,
                    dragging: `fl-reorderer-${this.options.tabContext}-movable-dragging`,
                    mouseDrag: `fl-reorderer-${this.options.tabContext}-movable-dragging`,
                    hover: `fl-reorderer-${this.options.tabContext}-movable-hover`,
                    dropMarker: `fl-reorderer-${this.options.tabContext}-dropMarker`,
                    avatar: `fl-reorderer-${this.options.tabContext}-avatar`
                },
                listeners: {
                    afterMove: (item) => {
                        this.handleTabMove(item);
                    }
                }
            });
        }
    }

    handleTabMove(item) {
        const tab = item;
        const tabShortId = window.up.defaultNodeIdExtractor(tab);
        let method = this.options.insertBefore;
        let targetTabShortId = null;
        let tabPosition = 1;

        const listItems = this.container.querySelectorAll(this.options.selectors.tabListItems);

        // Determine method and target based on position
        if (tab === tab.parentNode.lastElementChild) {
            method = this.options.appendAfter;
            const prevTab = tab.previousElementSibling;
            if (prevTab && prevTab.id) {
                targetTabShortId = prevTab.id.split('_')[1];
            }
        } else {
            const nextTab = tab.nextElementSibling;
            if (nextTab && nextTab.id) {
                targetTabShortId = nextTab.id.split('_')[1];
            }
        }

        // If we couldn't determine a valid target, don't proceed
        if (!targetTabShortId) {
            console.warn('TabManager: Invalid tab move - no valid target found');
            return;
        }

        // Update tab positions and styles
        listItems.forEach((li, index) => {
            if (li.id === tab.id) {
                tabPosition = index + 1;
            }

            // Apply appropriate styles based on position
            li.classList.remove(
                this.options.styles.singleTab,
                this.options.styles.firstTab,
                this.options.styles.lastTab
            );

            if (listItems.length === 1) {
                li.classList.add(this.options.styles.singleTab);
            } else if (index === 0) {
                li.classList.add(this.options.styles.firstTab);
            } else if (index === listItems.length - 1) {
                li.classList.add(this.options.styles.lastTab);
            }
        });

        // Fire tab move event
        this.events.onTabMove(tabShortId, method, targetTabShortId, tabPosition);
    }

    locate(selector) {
        const selectorPath = this.options.selectors[selector];
        return selectorPath ? this.container.querySelector(selectorPath) : null;
    }

    refresh() {
        // Refresh the reorder layout if it exists
        if (this.reorderLayout && this.reorderLayout.refresh) {
            this.reorderLayout.refresh();
        }
    }
}

// Global initialization function to replace Fluid component
window.up = window.up || {};
window.up.TabManager = function(container, options) {
    return new ModernTabManager(container, options);
};