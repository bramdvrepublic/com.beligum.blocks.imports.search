/**
 * Created by wouter on 25/08/15.
 */
base.plugin("blocks.imports.SearchBox", ["base.core.Class", "blocks.imports.Block", "blocks.core.Sidebar", "constants.blocks.imports.search", "messages.blocks.imports.search", function (Class, Block, Sidebar, SearchConstants, SearchMessages)
{
    var SearchBox = this;
    var TAGS = ["blocks-search-box"];

    (this.Class = Class.create(Block.Class, {

        //-----VARIABLES-----

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

            retVal.push(this.addOptionalClass(Sidebar, block.element, SearchMessages.boxInline, SearchConstants.SEARCH_BOX_CLASS_INLINE));

            return retVal;
        },
        getWindowName: function ()
        {
            return SearchMessages.widgetTitle;
        },

        //-----PRIVATE METHODS-----

    })).register(TAGS);

}]);