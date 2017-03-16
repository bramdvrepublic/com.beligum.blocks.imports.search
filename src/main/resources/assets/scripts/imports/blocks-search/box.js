/**
 * Created by wouter on 25/08/15.
 */
base.plugin("blocks.imports.SearchBox", ["base.core.Class", "blocks.imports.Block", "blocks.core.Sidebar", "base.core.Commons", "constants.blocks.core", "messages.blocks.core", "constants.blocks.imports.search", "messages.blocks.imports.search", function (Class, Block, Sidebar, Commons, BlocksConstants, BlocksMessages, SearchConstants, SearchMessages)
{
    var SearchBox = this;

    //-----CONSTANTS-----
    var TAGS = ["blocks-search-box"];
    var TEMP_FILTER_ATTR = 'data-value';

    (this.Class = Class.create(Block.Class, {

        //-----VARIABLES-----
        block: null,
        searchClassCombo: null,
        activeFiltersList: null,
        addFilterCombo: null,
        lastSearchClass: null,

        //-----CONSTRUCTORS-----
        constructor: function ()
        {
            SearchBox.Class.Super.call(this);
        },

        //-----IMPLEMENTED METHODS-----
        init: function ()
        {
        },
        focus: function (block, element, hotspot, event)
        {
            SearchBox.Class.Super.prototype.focus.call(this);
        },
        blur: function (block, element)
        {
            SearchBox.Class.Super.prototype.blur.call(this);
        },
        getConfigs: function (block, element)
        {
            var retVal = SearchBox.Class.Super.prototype.getConfigs.call(this, block, element);

            var _this = this;
            this.block = block;
            this.addFilterCombo = this.createCombobox(Sidebar, SearchMessages.boxFiltersPropertiesAdd, []);
            this.activeFiltersList = this.createActiveFilters();
            this.searchClassCombo = this.addUniqueAttributeValueAsync(Sidebar, block.element, SearchMessages.boxFiltersClassLabel, SearchConstants.SEARCH_BOX_TYPE_ARG, SearchConstants.SEARCH_CLASSES_ENDPOINT, 'title', 'curieName',
                function changeListener(oldValueTerm, newValueTerm)
                {
                    var force = true;

                    //we don't force a reload of the filters when the class didn't change
                    var attr = _this.block.element.attr(SearchConstants.SEARCH_BOX_TYPE_ARG);
                    if (newValueTerm && newValueTerm.curieName && newValueTerm.curieName === attr) {
                        force = false;
                    }

                    _this.reinitAddFilterCombo(force);
                }
            );

            //bootstrap the controls
            this.reinitAddFilterCombo();

            var filterContainer = $('<div class="group"></div>');
            filterContainer.append($('<div class="title">' + SearchMessages.boxFiltersLabel + '</div>'));
            filterContainer.append(this.searchClassCombo);
            filterContainer.append(this.addFilterCombo);
            filterContainer.append(this.activeFiltersList);
            retVal.push(filterContainer);

            retVal.push(this.addOptionalClass(Sidebar, block.element, SearchMessages.boxInline, SearchConstants.SEARCH_BOX_CLASS_INLINE));

            return retVal;
        },
        getWindowName: function ()
        {
            return SearchMessages.widgetTitle;
        },

        //-----PRIVATE METHODS-----
        createActiveFilters: function ()
        {
            var content = $('<div class="' + BlocksConstants.INPUT_TYPE_WRAPPER_CLASS + '"/>');
            var id = Commons.generateId();
            var label = $('<label for="' + id + '">' + SearchMessages.boxFiltersPropertiesActive + '</label>').appendTo(content);
            var listGroup = $('<div id="' + id + '" class="list-group"/>').appendTo(content);

            if (Sortable) {
                Sortable.create(listGroup.get(0), {
                    //works nicer when the whole line is draggable, but works
                    //handle: ".handle",
                    animation: 250,
                    //this will create an empty space where we will drop
                    ghostClass: 'invisible',
                    onUpdate: $.proxy(this.filtersListReordered, this)
                });
            }
            if (jQuery().perfectScrollbar) {
                listGroup.perfectScrollbar();
            }

            return content;
        },
        reinitAddFilterCombo: function (forceReload)
        {
            var _this = this;

            var searchClassCurie = this.block.element.attr(SearchConstants.SEARCH_BOX_TYPE_ARG);
            if (searchClassCurie) {
                _this.addFilterCombo.removeClass('hidden');

                //this will make sure the filter list is not updated, but wiped
                if (forceReload) {
                    this.resetActiveFilters();
                }

                $.getJSON(BlocksConstants.RDF_PROPERTIES_ENDPOINT + "?" + BlocksConstants.RDF_RES_TYPE_CURIE_PARAM + "=" + searchClassCurie)
                    .done(function (data)
                    {
                        var activeFilters = [];
                        var activeFiltersAttr = _this.block.element.attr(SearchConstants.SEARCH_BOX_FILTER_TERMS_ARG);
                        if (activeFiltersAttr) {
                            activeFilters = JSON.parse(atob(activeFiltersAttr));
                        }

                        var allClassProps = [];
                        $.each(data, function (idx, entry)
                        {
                            var obj = {
                                name: entry['title'],
                                //note: null values aren't handled very well, force-switch to empty string
                                value: entry['curieName'] === null ? '' : entry['curieName']
                            };

                            //if the value is not already in the active list, add it to the add-combobox
                            var filterIdx = $.inArray(obj.value, activeFilters);
                            if (filterIdx < 0) {
                                allClassProps.push(obj);
                            }
                            //while we're iterating the data, we might as well 'augment' the filters array
                            // to be prepared for the next loop
                            else {
                                activeFilters[filterIdx] = obj;
                            }
                        });

                        //check if we need to change the filter DOM
                        var currentFilterState = _this.buildActiveFiltersValue();
                        if (currentFilterState !== activeFiltersAttr) {
                            //this will hide the active list on last deletion
                            _this.resetActiveFilters();

                            //we'll do one extra iteration because we need to preserve the order
                            for (var i = 0; i < activeFilters.length; i++) {
                                var obj = activeFilters[i];
                                _this.addActiveFilter(obj.name, obj.value);
                            }
                        }

                        //this will enable/disable the addFilter toggle if nothing is left
                        var addFilterToggle = _this.addFilterCombo.find('.dropdown-toggle');
                        if (allClassProps.length) {
                            addFilterToggle.attr("disabled", null);
                        }
                        else {
                            addFilterToggle.attr("disabled", "true");
                        }

                        //sort alphabetically on title
                        _this.sortComboEntries(allClassProps);

                        //populate the combobox
                        _this.reinitCombobox(_this.addFilterCombo, allClassProps, $.proxy(_this.filterComboInit, _this), $.proxy(_this.filterComboChanged, _this));
                    })
                    .fail(function (xhr, textStatus, exception)
                    {
                        Notification.error(BlocksMessages.generalServerDataError + (exception ? "; " + exception : ""), xhr);
                    });
            }
            else {
                _this.addFilterCombo.addClass('hidden');
                _this.reinitCombobox(_this.addFilterCombo, []);
                //already reset when forced
                if (!forceReload) {
                    _this.resetActiveFilters();
                }
            }
        },
        filterComboInit: function (testValue)
        {
            var retVal = false;

            // //make sure we don't set the attribute to "undefined"
            // if (typeof testValue !== typeof undefined) {
            //     retVal = block.element.attr(sortPropComboAttr) == testValue;
            // }

            //return true if this element needs to be selected
            return retVal;
        },
        filterComboChanged: function (oldValueTerm, newValueTerm, event)
        {
            //when a combobox entry is selected,
            // we add it to the list of active filters
            if (newValueTerm) {
                this.addActiveFilter($(event.target).text(), newValueTerm);
            }
        },
        filtersListReordered: function (event)
        {
            this.updateActiveFiltersAttr();
        },
        addActiveFilter: function (classPropName, classPropValue)
        {
            var _this = this;

            var listGroup = this.activeFiltersList.find(".list-group");

            //this is needed for perfect scrollbar support (the rails need to come last)
            var listGroupItem;
            var firstItem = listGroup.find('.list-group-item').last();
            if (firstItem.length) {
                listGroupItem = $('<div class="list-group-item" />').insertAfter(firstItem);
            }
            else {
                listGroupItem = $('<div class="list-group-item" />').prependTo(listGroup);
            }

            //we'll save the data in the listGroupItem element so all members have access to it
            listGroupItem.attr(TEMP_FILTER_ATTR, classPropValue);

            var handle = $('<a href="javascript:void(0)" class="handle">☰</a>').appendTo(listGroupItem);
            //note that for the overflow with floats to work, the buttons need to come before the title
            var buttons = $('<div class="btn-group" role="group"></div>').appendTo(listGroupItem);
            //Note: the FORCE_CLICK attribute is needed because the click below rebuilds the entire sidebar and the controls is not detected as child of the sidebar anymore
            var title = $('<a disabled href="javascript:void(0)" title="' + classPropName + '" class="title" ' + BlocksConstants.FORCE_CLICK_ATTR + '>' + classPropName + '</a>').appendTo(listGroupItem);

            var deleteBtn = $('<a href="javascript:void(0)" class="btn btn-xs"><i class="fa fa-fw fa-trash-o"></i></a>').appendTo(buttons);
            deleteBtn.on('click', function (e)
            {
                _this.removeActiveFilter(listGroupItem, classPropName, classPropValue);
            });

            //update the scrollbar
            if (jQuery().perfectScrollbar) {
                listGroup.perfectScrollbar('update');
            }

            //make sure the list is visible
            this.activeFiltersList.removeClass('hidden');

            //this is needed because the perfect scrollbar adds elements and the last-of-type selector is messed up
            listGroup.find('.list-group-item').removeClass('last').last().addClass('last');

            //revert the combo toggle button back to nothing selected (reinitAddFilterCombo will also do this, but this will avoid flickering)
            this.addFilterCombo.find("button.dropdown-toggle").find('.text').text(BlocksMessages.comboboxEmptySelection);
            this.updateActiveFiltersAttr();
            this.reinitAddFilterCombo();
        },
        removeActiveFilter: function (listGroupItem)
        {
            listGroupItem.remove();
            this.updateActiveFiltersAttr();
            this.reinitAddFilterCombo();
        },
        resetActiveFilters: function ()
        {
            this.activeFiltersList.addClass('hidden');
            var listGroup = this.activeFiltersList.find(".list-group");
            listGroup.empty();
            this.updateActiveFiltersAttr();
        },
        updateActiveFiltersAttr: function ()
        {
            this.block.element.attr(SearchConstants.SEARCH_BOX_FILTER_TERMS_ARG, this.buildActiveFiltersValue());
        },
        buildActiveFiltersValue: function ()
        {
            var attrObj = [];

            this.activeFiltersList.find('.list-group-item').each(function (index)
            {
                attrObj.push($(this).attr(TEMP_FILTER_ATTR));
            });

            return btoa(JSON.stringify(attrObj));
        }

    })).register(TAGS);

}]);