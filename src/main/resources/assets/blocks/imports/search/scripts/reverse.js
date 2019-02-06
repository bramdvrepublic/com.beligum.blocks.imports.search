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
 * Created by wouter on 17/07/15.
 */
base.plugin("blocks.imports.SearchReverse", ["base.core.Class", "blocks.imports.Block", "blocks.core.Sidebar", "constants.blocks.imports.search", "messages.blocks.imports.search", function (Class, Block, Sidebar, SearchConstants, SearchMessages)
{
    var SearchReverse = this;
    var TAGS = ["blocks-search-reverse"];


    (this.Class = Class.create(Block.Class, {

        //-----VARIABLES-----

        //-----CONSTRUCTORS-----
        constructor: function ()
        {
            SearchReverse.Class.Super.call(this);
        },

        //-----IMPLEMENTED METHODS-----
        getConfigs: function (block, element)
        {
            var retVal = SearchReverse.Class.Super.prototype.getConfigs.call(this, block, element);

            //Note: we re-use the captions of the general search block
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
                        name: 1000,
                        value: 1000
                    }
                ], null));

            return retVal;
        },
        getWindowName: function ()
        {
            return SearchMessages.widgetTitle;
        },

        //-----PRIVATE METHODS-----

    })).register(TAGS);

}]);