/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.midpath.demo.svg;

import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGAnimationElement;
import org.w3c.dom.svg.SVGElement;

/**
 * The <code>SVGList</code> class displays the items of an arbitrary long list
 * of data items in a limited number of visual elements. The list triggers
 * a scrolling effect to move up or down in the list and has a notion of 
 * selected item (i.e., the one which reprents the current user selection).
 *
 * The <code>SVGList</code> class facilitates creating a list where the 
 * appearance of the list and the list animations are defined in SVG markup and
 * the data displayed by the list is controlled by the <code>ListModel</code>
 * implementation associated to the <code>SVGList</code>.
 *
 * The conventions for the SVG this class can hook into are:
 *
 * - The various elements which compose the list share the same identifier 
 *   prefix which is given to the SVGList at construction time (e.g., 'myList').
 *   This prefix is call 'listIdPrefix' in the following.
 *
 * - The animations which produce the list's scroll up effect are started by
 *   the animation with id <listIdPrefix + "_scrollUp_anim">. The scroll down 
 *   effect animations are started with the animation with id: 
 *   <listIdPrefix + "_scrollDown_anim">.
 *
 * - The SVG elements used to display the list items have an id of the form:
 *   <listIdPrefix + "_item_" + n>. The display items must be in consecutive 
 *   order starting at zero.
 *
 * - The item which displays the currently selected item should have the 
 *   additional "_selectedItem" suffix, so its id should be formed as:
 *   <listIdPrefix + "_item_" + n + "_selectedItem">. If not selected item is
 *   specified, the selected item index defaults to zero.
 * 
 * By default, <code>SVGList</code> assumes that the list items are SVG 
 * <text> elements. However, the list can be associated to a list item binders
 * to handle more sophisticated item rendering.
 *
 * Example of SVG content which can be bound by this class:
 *
 * <pre>
 * <g id="listA">
 *      <g>
 *          <text id="listA_item_0" x="20" y="-60">item 0</text>
 *          <text id="listA_item_1" x="20" y="40">item 1</text>
 *          <text id="listA_item_2" x="20" y="70">item 2</text>
 *          <text id="listA_item_3" x="20" y="100">item 3</text>
 *      </g>
 *
 *     <g id="listA_item_4_selectedItem">
 *          <text x="40" y="130">item 4</text>
 *          <text x="40" y="150">item 4 details</text>
 *     </g>
 *
 *      <g>
 *          <text id="listA_item_5" x="20" y="180">item 5</text>
 *          <text id="listA_item_6" x="20" y="210">item 6</text>
 *          <text id="listA_item_7" x="20" y="240">item 7</text>
 *          <text id="listA_item_8" x="20" y="270">item 8</text>
 *          <text id="listA_item_9" x="20" y="340">item 8</text>
 *      </g>
 *
 *      <animate id="listA_scrollDown_anim" ... />
 *      <animate id="listA_scrollUp_anim" ... />
 * </g>
 * </pre>
 */
public class SVGList {
    /**
     * Interface that list data sources must implement.
     */
    public interface ListModel {
        /**
         * @param index - the requested index.
         * @return the data at the given index.
         */
        public Object getElementAt(int index);
        
        /**
         * Returns the list's length.
         * @return the length of the list.
         */
        public int getSize();
    }
    
    /**
     * Interface that item binders must implement. An item binder is responsible
     * for transfering a list element value (see ListModel) to the SVG 
     * element displaying the list.
     */
    public interface ListItemBinder {
        /**
         * @param itemValue - the item value to transfer to the SVG element.
         * @param itemElement - the item element where the item value is 
         * displayed.
         */
        public void bindItem(Object itemValue, SVGElement itemElement);
    }
    
    /** 
     * Default ListItemBinder. Assumes that the SVG element displaying the 
     * list is a <text> element.
     */
    public class DefaultListItemBinder implements ListItemBinder {
        /**
         * @param itemValue - the item value to transfer to the SVG element.
         * @param itemElement - the item element where the item value is 
         * displayed.
         */
        public void bindItem(Object itemValue, SVGElement itemElement) {
            itemElement.setTrait("#text", itemValue.toString());
        }
        
    }
    
    /**
     * Id suffix used for the list scrollUp animation effect.
     */
    public static final String SCROLL_UP_ANIM_SUFFIX = "_scrollUp_anim";
    
    /**
     * Id suffix used for the list scrollDown animation effect.
     */
    public static final String SCROLL_DOWN_ANIM_SUFFIX = "_scrollDown_anim";
    
    /**
     * The animation to play to scroll up the list.
     */
    protected SVGAnimationElement scrollUpAnim;
    
    /**
     * The animation to play to scroll down the list.
     */
    protected SVGAnimationElement scrollDownAnim;
    
    /**
     * The number of items displayed in the list.
     */
    protected int nDisplayedItems;
    
    /**
     * The model providing the list data.
     */
    protected ListModel listModel;
    
    /**
     * Index of the currently selected list item.
     */
    protected int curIndex = 0;
    
    /**
     * Index of the list item which corresponds to the selected item.
     */
    private int selectedItem = 0;
            
    /**
     * This vector holds all the <text> SVGElement instances which represent
     * items in the list.
     */
    protected Vector listItems = new Vector();
    
    /**
     * The prefix for the identifiers which make the various list elements.
     */
    protected String listIdPrefix;
    
    /**
     * The list item binder used to display common list elements.
     */
    protected ListItemBinder commonItemBinder;
    
    /**
     * The list item binder used to display the selected list element.
     */
    protected ListItemBinder selectedItemBinder;
    /**
     *
     */
    protected SVGElement selectedItemDetails; 

    /**
     * The minimal number of items used to display list entries.
     */
    private static final int MIN_N_DISPLAYED_ITEMS = 1;
    
    /** 
     * Creates a new instance of SVGList.
     * @param listModel - the <code>ListModel</code> which will provide the 
     * data for the list.
     * @param listIdPrefix - the prefix used for the various elements which make the
     * list.
     */
    public SVGList(final ListModel listModel, final String listIdPrefix) {
        if (listModel == null) {
            throw new NullPointerException();
        }
        
        if (listIdPrefix == null || "".equals(listIdPrefix)) {
            throw new IllegalArgumentException("List identifiers prefix cannot" +
                    "be null or empty string.");
        }
        
        this.listModel = listModel;
        this.listIdPrefix = listIdPrefix;
        this.commonItemBinder = new DefaultListItemBinder();
        this.selectedItemBinder = new DefaultListItemBinder();
    }
    
    /**
     * Sets a new <code>ListItemBinder</code> for rendering common list 
     * entries.
     *
     * @param commonItemBinder - the new ListItemBinder to use when 
     * rendering common list elements.
     */
    public void setCommonItemBinder(final ListItemBinder commonItemBinder) {
        if (commonItemBinder == null) {
            throw new NullPointerException();
        }
        
        this.commonItemBinder = commonItemBinder;
    }
    
    /**
     * Sets a new <code>ListItemBinder</code> for rendering the selected list
     * entry.
     *
     * @param selectedItemBinder - the new ListItemBinder to use when 
     * rendering the selected list element.
     */
    public void setSelectedItemBinder(final ListItemBinder selectedItemBinder) {
        if (selectedItemBinder == null) {
            throw new NullPointerException();
        }
        
        this.selectedItemBinder = selectedItemBinder;
    }
    
    /**
     * Hooks the specified sking to the application.
     *
     * @param doc the new Document to hook into the application
     */
    public void hookSkin(final Document doc) {
            scrollUpAnim = (SVGAnimationElement) doc.getElementById(listIdPrefix + SCROLL_UP_ANIM_SUFFIX);
            scrollDownAnim = (SVGAnimationElement) doc.getElementById(listIdPrefix + SCROLL_DOWN_ANIM_SUFFIX);   
            
            if (scrollUpAnim == null) {
                throw new IllegalArgumentException("SVGList convention error: no animation with id '" + (listIdPrefix + SCROLL_UP_ANIM_SUFFIX) + "'");
            }
            
            if (scrollDownAnim == null) {
                throw new IllegalArgumentException("SVGList convention error: no animation with id '" + (listIdPrefix + SCROLL_DOWN_ANIM_SUFFIX) + "'");
            }

            // Initialize the item elements which should be filled in with the data item values
            boolean keepSearching = true;
            nDisplayedItems = 0;
            listItems.removeAllElements();
            boolean hasFocusedItem = false;
            
            while (keepSearching) {
                SVGElement item = (SVGElement) doc.getElementById(listIdPrefix + "_item_" + nDisplayedItems);
                if (item != null) {
                    listItems.addElement(item);
                    nDisplayedItems++;
                } else {
                    // Check if the item is the currently selected one:
                    item = (SVGElement) doc.getElementById(listIdPrefix + "_item_" + nDisplayedItems + "_selectedItem");
                    if (item != null && !hasFocusedItem) {
                        listItems.addElement(item);
                        selectedItem = nDisplayedItems;
                        nDisplayedItems++;
                    } else {
                        keepSearching = false;
                    }
                }
            }
            
            // If we did not find any list items, then the list conventions are broken.
            if (nDisplayedItems < MIN_N_DISPLAYED_ITEMS) {
                throw new IllegalArgumentException("SVGList convention error: there are only " + nDisplayedItems + 
                        " elements with id " + (listIdPrefix + "_item_<n>" + " and SVGList requires at least " +
                        MIN_N_DISPLAYED_ITEMS));
            }
            
            // Now, apply the initial set of data items.
            setDataItems();
    }
    
    /**
     * Returns the current index in the list, in the [0, getSize()[ range.
     *
     * @return the first shown list index.
     */
    public int getCurIndex() {
        return curIndex;
    }
    
    /**
     * Sets the new current index, i.e., the index of the first displayed 
     * list item.
     *
     * @param curIndex - the index of the new first displayed list item.
     */
    public void setCurIndex(final int curIndex) {
        if (curIndex < 0 || curIndex >= listModel.getSize()) {
            throw new IllegalArgumentException();
        }
        
        this.curIndex = curIndex;
    }
    
    /**
     * Returns the index of the index in the list with focus.
     *
     * @return the index of the currently selected list index.
     */
    public int getFocusedIndex() {
        return (curIndex + selectedItem) % listModel.getSize();
    }
    
    /**
     * Sets the index of the item with selection focus.
     *
     * @param selectedIndex - the index of the new currently focused list item.
     */
    public void setSelectedIndex(final int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= listModel.getSize()) {
            throw new IllegalArgumentException();
        }
        
        int ci = selectedIndex - selectedItem;
        
        if (ci < 0) {
            ci += listModel.getSize();
        } else if (ci >= listModel.getSize()) {
            ci -= listModel.getSize();
        }
        
        setCurIndex(ci);
    }

    /**
     * Returns the current progress in the list, as a ration of the current
     * index over the number of list items.
     */
    public float getPosition() {
        return 1 + (curIndex - listModel.getSize() + selectedItem + 1) /
                                        (float) (listModel.getSize() -1);
    }

    /**
     * Transfers list data to the XML UI.
     */
    public void setDataItems() {
        for (int i = 0; i < nDisplayedItems; i++) {
            SVGElement uiItem = (SVGElement) listItems.elementAt(i);
            int listIndex = 0;
            if ((curIndex + i) >= listModel.getSize()) {
                listIndex = curIndex + i - listModel.getSize();
            } else if (curIndex + i < 0) {
                listIndex = curIndex + i + listModel.getSize();
            } else {
                listIndex = curIndex + i;
            }
            
            ListItemBinder itemBinder = commonItemBinder;
            if (i == selectedItem) {
                itemBinder = selectedItemBinder;
            }
            
            itemBinder.bindItem(listModel.getElementAt(listIndex), uiItem);
        }
    }
    
    public void next() {
        curIndex = (curIndex + 1) % listModel.getSize();
        setDataItems();
    }
    
    public void scrollDown() {
        next();
        scrollDownAnim.beginElementAt(0);
    }
    
    public void prev() {
        curIndex = (curIndex + listModel.getSize() - 1) % listModel.getSize();
        setDataItems();   
    }
    
    public void scrollUp() {
        prev();
        scrollUpAnim.beginElementAt(0);
    }
}
