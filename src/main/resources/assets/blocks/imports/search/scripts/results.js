/*
 * Copyright 2017 Republic of Reinvention bvba. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Created by wouter on 25/08/15.
 */
base.plugin("blocks.imports.SearchResults", ["base.core.Class", "blocks.imports.Block", "blocks.core.Sidebar", "constants.blocks.core", "messages.blocks.core", "constants.blocks.imports.search", "messages.blocks.imports.search", function (Class, Block, Sidebar, BlocksConstants, BlocksMessages, SearchConstants, SearchMessages)
{
    var SearchResults = this;
    var TAGS = ["blocks-search-results"];

    (this.Class = Class.create(Block.Class, {

        //-----VARIABLES-----

        //-----CONSTRUCTORS-----
        constructor: function ()
        {
            SearchResults.Class.Super.call(this);
        },

        //-----IMPLEMENTED METHODS-----
        getConfigs: function (block, element)
        {
            var retVal = SearchResults.Class.Super.prototype.getConfigs.call(this, block, element);

            var sortPropComboAttr = SearchConstants.SEARCH_BOX_SORT_ARG;
            var sortPropComboInit = function changeListener(testValue)
            {
                var retVal = false;

                //make sure we don't set the attribute to "undefined"
                if (typeof testValue !== typeof undefined) {
                    retVal = block.element.attr(sortPropComboAttr) == testValue;
                }

                //return true if this element needs to be selected
                return retVal;
            };
            var sortPropComboChanged = function changeListener(oldValueTerm, newValueTerm)
            {
                block.element.removeAttr(sortPropComboAttr);

                if (newValueTerm) {
                    block.element.attr(sortPropComboAttr, newValueTerm);
                }
            };

            //we'll init it here to have a pointer in the subject changeListener below
            //note: don't add the changeListener yet, or it will overwrite a valid attribute during initialization
            var sortPropCombo = this.addUniqueAttributeValue(Sidebar, block.element, SearchMessages.resultsSortSubjectTitle, sortPropComboAttr, [], null);

            var _this = this;
            var nameProperty = 'title';
            var valueProperty = 'curieName';
            var searchClassCombo = this.addUniqueAttributeValueAsync(Sidebar, block.element, SearchMessages.resultsSubjectTitle, SearchConstants.SEARCH_BOX_TYPE_ARG, BlocksConstants.RDF_CLASSES_ENDPOINT, nameProperty, valueProperty,
                function changeListener(oldValueTerm, newValueTerm)
                {
                    //Call the change listener manually to reset the sub-property if the class changes,
                    //except during initialization because otherwise, it would wipe the valid saved attribute
                    //Note that 'firstCall' is just a custom property we set to detect initialization, it has no meaning
                    if (!searchClassCombo.firstCall) {
                        searchClassCombo.firstCall = true;
                    }
                    else {
                        sortPropComboChanged(null, null);
                    }

                    //You can use this code to disable to dropdown if you ever would need this
                    //sortPropCombo.find('.dropdown-toggle').addClass("disabled");

                    var valuesEndpoint = BlocksConstants.RDF_PROPERTIES_ENDPOINT;

                    //if we have a specific class, we'll only show the properties of that class,
                    //otherwise we show all properties
                    if (newValueTerm && newValueTerm.curieName) {
                        valuesEndpoint += "?" + BlocksConstants.RDF_RES_TYPE_CURIE_PARAM + "=" + newValueTerm.curieName;
                    }

                    $.getJSON(valuesEndpoint)
                        .done(function (data)
                        {
                            var comboEntries = [];
                            $.each(data, function (idx, entry)
                            {
                                comboEntries.push({
                                    name: entry[nameProperty],
                                    //note: null values aren't handled very well, force-switch to empty string
                                    value: entry[valueProperty] === null ? '' : entry[valueProperty]
                                });
                            });

                            _this.sortComboEntries(comboEntries);

                            _this.reinitCombobox(sortPropCombo, comboEntries, sortPropComboInit, sortPropComboChanged);
                        })
                        .fail(function (xhr, textStatus, exception)
                        {
                            Notification.error(BlocksMessages.generalServerDataError + (exception ? "; " + exception : ""), xhr);
                        });
                },
                {
                    name: BlocksMessages.rdfClassAllTitle,
                    value: ''
                }
            );

            retVal.push(searchClassCombo);
            retVal.push(sortPropCombo);

            retVal.push(this.addUniqueAttributeValue(Sidebar, block.element, SearchMessages.resultsSortOrderTitle, SearchConstants.SEARCH_BOX_DESC_ARG,
                [
                    {
                        name: SearchMessages.resultsSortOrderAsc,
                        value: ''
                    },
                    {
                        name: SearchMessages.resultsSortOrderDesc,
                        value: 'true'
                    },
                ]
            ));

            //retVal.push(this.addUniqueAttributeValueAsync(Sidebar, block.element, SearchMessages.resultsSubjectTitle, SearchConstants.SEARCH_BOX_TYPE_ARG, "/blocks/imports/search/values/", "title", "curieName"));

            retVal.push(this.addUniqueAttributeValue(Sidebar, block.element, SearchMessages.boxResultsFormat, SearchConstants.SEARCH_RESULTS_FORMAT_ARG,
                [
                    {
                        name: SearchMessages.resultsFormatList,
                        value: SearchConstants.SEARCH_RESULTS_FORMAT_LIST
                    },
                    {
                        name: SearchMessages.resultsFormatLetters,
                        value: SearchConstants.SEARCH_RESULTS_FORMAT_LETTERS
                    },
                    {
                        name: SearchMessages.resultsFormatImages,
                        value: SearchConstants.SEARCH_RESULTS_FORMAT_IMAGES
                    }
                ], null));

            retVal.push(this.addUniqueAttributeValue(Sidebar, block.element, SearchMessages.boxResultsSize, SearchConstants.SEARCH_RESULTS_SIZE_ARG,
                [
                    {
                        name: 10,
                        value: 10
                    },
                    {
                        name: 50,
                        value: 50
                    },
                    {
                        name: 100,
                        value: 100
                    },
                    {
                        name: 250,
                        value: 250
                    },
                    {
                        name: 500,
                        value: 500
                    },
                    {
                        //Note: sync this max value with the MAX_PAGE_SIZE in the Controller
                        name: 1000,
                        value: 1000
                    }
                ], null));

            retVal.push(this.addOptionalClass(Sidebar, block.element, SearchMessages.boxResultsHideHeader, null, null, SearchConstants.SEARCH_RESULTS_HIDE_HEADER_ARG));
            retVal.push(this.addOptionalClass(Sidebar, block.element, SearchMessages.boxResultsHidePager, null, null, SearchConstants.SEARCH_RESULTS_HIDE_PAGER_ARG));

            return retVal;
        },
        getWindowName: function ()
        {
            return SearchMessages.widgetTitle;
        },

        //-----PRIVATE METHODS-----

    })).register(TAGS);

}]);