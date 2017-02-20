/**
 * Created by wouter on 25/08/15.
 */
base.plugin("mot.blocks.results", ["base.core.Class", "blocks.imports.Block", "blocks.core.Sidebar", "constants.blocks.imports.search", "messages.blocks.imports.search", function (Class, Block, Sidebar, SearchConstants, SearchMessages)
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
        init: function ()
        {
        },
        focus: function (block, element, hotspot, event)
        {
            SearchResults.Class.Super.prototype.focus.call(this);
        },
        blur: function (block, element)
        {
            SearchResults.Class.Super.prototype.blur.call(this);
        },
        getConfigs: function (block, element)
        {
            var retVal = SearchResults.Class.Super.prototype.getConfigs.call(this, block, element);

            var VAL_PROP = "curieName";
            retVal.push(this.addUniqueAttributeValueAsync(Sidebar, block.element, SearchMessages.boxType, SearchConstants.SEARCH_BOX_TYPE_ARG, "/blocks/admin/rdf/search/classes/", "title", VAL_PROP,
                null
                // function changeListener(oldValueTerm, newValueTerm)
                // {
                //     if (oldValueTerm) Logger.info(oldValueTerm[VAL_PROP]);
                //     if (newValueTerm) Logger.info(newValueTerm[VAL_PROP]);
                //
                //     //for now, we don't allow the combobox to switch to an "empty" value, so ignore if that happens (probably during initialization)
                //     if (!newValueTerm) {
                //         return;
                //     }
                //
                //     // don't change anything if they're both the same
                //     if (oldValueTerm && oldValueTerm[VAL_PROP] == newValueTerm[VAL_PROP]) {
                //         return;
                //     }
                // }
            ));

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