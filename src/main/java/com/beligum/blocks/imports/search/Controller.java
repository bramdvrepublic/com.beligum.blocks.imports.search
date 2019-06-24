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

package com.beligum.blocks.imports.search;

import com.beligum.base.cache.CacheFunction;
import com.beligum.base.cache.CacheKey;
import com.beligum.base.server.R;
import com.beligum.base.utils.Logger;
import com.beligum.base.utils.json.Json;
import com.beligum.blocks.config.StorageFactory;
import com.beligum.blocks.index.entries.SimpleIndexSearchResult;
import com.beligum.blocks.index.fields.ResourceTypeField;
import com.beligum.blocks.index.ifaces.*;
import com.beligum.blocks.index.results.AutoTupleRdfResult;
import com.beligum.blocks.index.sparql.SesamePageIndexConnection;
import com.beligum.blocks.index.sparql.SparqlIndexSelectResult;
import com.beligum.blocks.rdf.RdfFactory;
import com.beligum.blocks.rdf.ifaces.RdfClass;
import com.beligum.blocks.rdf.ifaces.RdfEndpoint;
import com.beligum.blocks.rdf.ifaces.RdfProperty;
import com.beligum.blocks.rdf.ontologies.Main;
import com.beligum.blocks.rdf.ontologies.Meta;
import com.beligum.blocks.templating.blocks.DefaultTemplateController;
import gen.com.beligum.blocks.imports.search.constants.blocks.imports.search;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.beligum.blocks.index.sparql.SesamePageIndexConnection.SPARQL_OBJECT_BINDING_NAME;
import static com.beligum.blocks.index.sparql.SesamePageIndexConnection.SPARQL_SUBJECT_BINDING_NAME;
import static gen.com.beligum.blocks.imports.search.constants.blocks.imports.search.*;

/**
 * Created by bram on 6/6/16.
 */
public class Controller extends DefaultTemplateController
{
    //-----CONSTANTS-----
    public static final int FIRST_PAGE_INDEX = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = Integer.parseInt(search.SEARCH_MAX_PAGE_SIZE);

    public enum CacheKeys implements CacheKey
    {
        SEARCH_REQUEST,
        SEARCH_RESULT
    }

    //-----VARIABLES-----

    //-----CONSTRUCTORS-----
    @Override
    public void created()
    {
        SearchRequestDAO searchRequest = this.getSearchRequest();

        if (searchRequest.getSearchTerm() == null) {
            searchRequest.setSearchTerm(getQueryParam(SEARCH_PARAM_QUERY));
        }

        if (searchRequest.getTypeOf() == null) {
            RdfClass typeOf = null;
            String typeOfParam = null;
            try {
                typeOfParam = this.getParam(SEARCH_PARAM_TYPE, SEARCH_BOX_TYPE_ARG, null);
                if (!StringUtils.isEmpty(typeOfParam)) {
                    typeOf = RdfFactory.getClass(typeOfParam);
                }
            }
            catch (Exception e) {
                Logger.warn("Invalid type of class; " + typeOfParam);
            }

            searchRequest.setTypeOf(typeOf);
        }

        if (searchRequest.getFieldFilters() == null) {
            searchRequest.setFieldFilters(R.requestManager().getCurrentRequest().getRequestContext().getUriInfo().getQueryParameters().get(SEARCH_PARAM_FILTER));
        }

        if (searchRequest.getSortField() == null) {
            RdfProperty sortField = null;
            String sortParam = this.getParam(SEARCH_PARAM_SORT, SEARCH_BOX_SORT_ARG, null);
            if (!StringUtils.isEmpty(sortParam)) {
                sortField = RdfFactory.getProperty(sortParam);
            }

            searchRequest.setSortField(sortField);
        }

        if (searchRequest.getSortDescending() == null) {
            String descParam = this.getParam(SEARCH_PARAM_SORT_DESC, SEARCH_BOX_DESC_ARG, null);
            if (!StringUtils.isEmpty(descParam)) {
                searchRequest.setSortDescending(Boolean.valueOf(descParam));
            }
            else {
                searchRequest.setSortDescending(false);
            }
        }

        if (searchRequest.getPageIndex() == null) {
            Integer pageIndex = null;
            String pageIndexParam = null;
            try {
                pageIndexParam = this.getParam(SEARCH_PARAM_INDEX, SEARCH_RESULTS_INDEX_ARG, null);
                if (!StringUtils.isEmpty(pageIndexParam)) {
                    pageIndex = Math.max(Integer.parseInt(pageIndexParam), FIRST_PAGE_INDEX);
                }
            }
            catch (Exception e) {
                Logger.warn("Invalid page index; " + pageIndexParam);
            }

            searchRequest.setPageIndex(pageIndex);
        }

        if (searchRequest.getPageSize() == null) {
            Integer pageSize = null;
            String pageSizeParam = null;
            try {
                pageSizeParam = this.getParam(SEARCH_PARAM_SIZE, SEARCH_RESULTS_SIZE_ARG, null);
                if (!StringUtils.isEmpty(pageSizeParam)) {
                    pageSize = Math.min(Integer.parseInt(pageSizeParam), MAX_PAGE_SIZE);
                }
            }
            catch (Exception e) {
                Logger.warn("Invalid page size; " + pageSizeParam);
            }

            searchRequest.setPageSize(pageSize);
        }

        if (searchRequest.getFormat() == null) {
            String format = null;
            String resultsFormatConfig = this.config.get(SEARCH_RESULTS_FORMAT_ARG);
            if (!StringUtils.isEmpty(resultsFormatConfig)) {
                format = resultsFormatConfig;
            }

            searchRequest.setFormat(format);
        }

        if (searchRequest.getHideHeader() == null) {
            String resultsHideHeaderConfig = this.config.get(SEARCH_RESULTS_HIDE_HEADER_ARG);
            if (!StringUtils.isEmpty(resultsHideHeaderConfig)) {
                searchRequest.setHideHeader(Boolean.parseBoolean(resultsHideHeaderConfig));
            }
            else {
                searchRequest.setHideHeader(false);
            }
        }

        if (searchRequest.getHidePager() == null) {
            String resultsHidePagerConfig = this.config.get(SEARCH_RESULTS_HIDE_PAGER_ARG);
            if (!StringUtils.isEmpty(resultsHidePagerConfig)) {
                searchRequest.setHidePager(Boolean.parseBoolean(resultsHidePagerConfig));
            }
            else {
                searchRequest.setHidePager(false);
            }
        }
    }

    //-----PUBLIC METHODS-----
    public SearchConfig getSearchConfig()
    {
        return new SearchConfig(this, this.config.containsKey(SEARCH_BOX_SORT_ARG), this.parseSearchFilters(this.config.get(SEARCH_BOX_FILTERS_ARG)), this.getSearchLanguage());
    }
    public SearchRequestDAO getSearchRequest()
    {
        return R.requestManager().getCurrentRequest().getRequestCache().getAndInitIfAbsent(CacheKeys.SEARCH_REQUEST, new CacheFunction<CacheKey, SearchRequestDAO>()
        {
            @Override
            public SearchRequestDAO apply(CacheKey cacheKey) throws IOException
            {
                return new SearchRequestDAO();
            }
        });
    }
    /**
     * Note: the concept for the lazy loaded results is to postpone the use (the effective query) of the request object as long as possible
     * up until the moment it's actually requested (from the template engine), and use the constructor of each controller instance to
     * add more config variables to the request object, so we effectively 'merge' the configs of the search-blocks on a page.
     */
    public IndexSearchResult getSearchResult()
    {
        return R.requestManager().getCurrentRequest().getRequestCache().getAndInitIfAbsent(CacheKeys.SEARCH_RESULT, new CacheFunction<CacheKey, IndexSearchResult>()
        {
            @Override
            public IndexSearchResult apply(CacheKey cacheKey) throws IOException
            {
                //let's not return nulls, so we can always use .size() and so on
                IndexSearchResult retVal = new SimpleIndexSearchResult(new ArrayList<>());

                try {
                    SearchRequestDAO searchParams = Controller.this.getSearchRequest();

                    //set some defaults if still empty..
                    if (searchParams.getSortDescending() == null) {
                        searchParams.setSortDescending(false);
                    }
                    if (searchParams.getPageIndex() == null) {
                        searchParams.setPageIndex(FIRST_PAGE_INDEX);
                    }
                    if (searchParams.getPageSize() == null) {
                        searchParams.setPageSize(DEFAULT_PAGE_SIZE);
                    }
                    if (searchParams.getFormat() == null) {
                        searchParams.setFormat(SEARCH_RESULTS_FORMAT_LIST);
                    }

                    IndexSearchRequest searchRequest = IndexSearchRequest.createFor(StorageFactory.getJsonIndexer().connect());

                    Locale locale = Controller.this.getSearchLanguage();

                    searchRequest.filter(ResourceIndexEntry.languageField, locale.getLanguage(), IndexSearchRequest.FilterBoolean.AND);

                    //Note: by default, we don't include sub-resources as search results. This might be made configurable in the future,
                    // but for now, we filter them out. Also note that they 'work', eg. the link they generate resolves correctly to it's containing page.
                    //searchRequest.missing(ResourceIndexEntry.parentUriField, IndexSearchRequest.FilterBoolean.AND);
                    searchRequest.filter(ResourceIndexEntry.resourceTypeField, ResourceIndexEntry.resourceTypeField.DEFAULT_VALUE, IndexSearchRequest.FilterBoolean.AND);

                    if (searchParams.getTypeOf() != null) {
                        searchRequest.filter(ResourceIndexEntry.typeOfField, searchParams.getTypeOf().getCurie().toString(), IndexSearchRequest.FilterBoolean.AND);
                    }

                    if (!StringUtils.isEmpty(searchParams.getSearchTerm())) {
                        searchRequest.query(searchParams.getSearchTerm(), IndexSearchRequest.FilterBoolean.OR);
                    }

                    if (searchParams.getFieldFilters() != null) {
                        Controller.this.addFieldFilters(searchParams.getFieldFilters(), searchRequest);
                    }

                    searchRequest.sort(searchParams.getSortField(), !searchParams.getSortDescending());
                    searchRequest.pageSize(searchParams.getPageSize());
                    searchRequest.pageOffset(searchParams.getPageIndex());

                    retVal = StorageFactory.getJsonIndexer().connect().search(searchRequest);
                }
                catch (Exception e) {
                    Logger.error("Error while executing search query", e);
                }

                return retVal;
            }
        });
    }

    //-----PROTECTED METHODS-----
    protected String getQueryParam(String name)
    {
        String retVal = null;

        if (!StringUtils.isEmpty(name)) {
            List<String> query = R.requestManager().getCurrentRequest().getRequestContext().getUriInfo().getQueryParameters().get(name);
            if (query != null && query.size() > 0) {
                retVal = query.get(0).trim();
            }
        }

        return retVal;
    }
    protected List<String> getQueryParams(String name)
    {
        List<String> retVal = R.requestManager().getCurrentRequest().getRequestContext().getUriInfo().getQueryParameters().get(name);
        return retVal == null ? new ArrayList<>() : retVal;
    }
    protected String getParam(String queryParam, String configParam, String defaultValue)
    {
        //first, we try the query param
        String retVal = this.getQueryParam(queryParam);

        //then the config value in the html
        //Note: an empty value is also a value, so don't check for empty values here
        if (retVal == null) {
            if (configParam != null) {
                retVal = this.config.get(configParam);
            }
        }

        if (retVal == null) {
            retVal = defaultValue;
        }

        return retVal;
    }
    protected Filter[] parseSearchFilters(String base64JsonConfigAttr)
    {
        Filter[] retVal = null;

        if (!StringUtils.isEmpty(base64JsonConfigAttr)) {
            try {
                String jsonConfig = org.apache.shiro.codec.Base64.decodeToString(base64JsonConfigAttr);
                retVal = Json.getObjectMapper().readValue(jsonConfig, Filter[].class);
            }
            catch (Exception e) {
                Logger.error("Error while parsing filters config argument; " + base64JsonConfigAttr, e);
            }
        }

        return retVal;
    }
    protected void addFieldFilters(List<String> filters, IndexSearchRequest searchRequest)
    {
        if (filters != null) {
            for (String filter : filters) {
                if (!StringUtils.isEmpty(filter)) {
                    String[] keyVal = filter.split(SEARCH_PARAM_FILTER_DELIM);

                    if (keyVal.length == 2) {
                        RdfProperty rdfProperty = RdfFactory.getProperty(URI.create(keyVal[0]));
                        if (rdfProperty != null) {
                            searchRequest.filter(rdfProperty, keyVal[1], IndexSearchRequest.FilterBoolean.AND);
                        }
                        else {
                            Logger.warn("Encountered unknown RDF property in search filter; ignoring filter" + filter);
                        }
                    }
                    else {
                        Logger.warn("Encountered search filter value with a wrong syntax (not parsable to key/value); ignoring filter; " + filter);
                    }
                }
            }
        }
    }
    /**
     * Returns a list of all distinct possible values for the given filter in the current language
     */
    protected RdfTupleResult<String, String> searchAllFilterValues(URI resourceTypeCurie, URI resourcePropertyCurie, boolean onlyLiteral, boolean caseInsensitive, int limit, Locale language)
                    throws IOException
    {
        RdfTupleResult<String, String> retVal = null;

        RdfClass type = RdfFactory.getClass(resourceTypeCurie);
        if (type == null) {
            Logger.warn("Encountered unknown resource type; " + resourceTypeCurie);
        }

        RdfProperty property = RdfFactory.getProperty(resourcePropertyCurie);
        if (property == null) {
            Logger.warn("Encountered unknown resource property; " + resourcePropertyCurie);
        }

        if (type != null && property != null) {

            //this means we're dealing with a property that has URI values (internal or external)
            boolean resource = property.getDataType().getType().equals(RdfClass.Type.CLASS);

            //this means the endpoint of this property is external (or the property is external)
            RdfEndpoint endpoint = property.getDataType().getEndpoint();
            boolean external = endpoint != null && endpoint.isExternal();

            final String internalObjBinding = SPARQL_OBJECT_BINDING_NAME + "In";
            final String externalObjBinding = SPARQL_OBJECT_BINDING_NAME + "Ex";

            StringBuilder queryBuilder = new StringBuilder();
            //final String searchPrefix = "search";
            queryBuilder.append("PREFIX ").append(Main.NAMESPACE.getPrefix()).append(": <").append(Main.NAMESPACE.getUri()).append("> \n");
            //queryBuilder.append("PREFIX ").append(searchPrefix).append(": <").append(LuceneSailSchema.NAMESPACE).append("> \n");
            queryBuilder.append("\n");
            queryBuilder.append("SELECT DISTINCT")/*.append(" ?").append(SPARQL_SUBJECT_BINDING_NAME).append(" ?").append(SPARQL_PREDICATE_BINDING_NAME)*/;
            //we'll need this URL of the value later on
            if (resource) {
                queryBuilder.append(" ?").append(internalObjBinding);
            }
            queryBuilder.append(" ?").append(SPARQL_OBJECT_BINDING_NAME);

            queryBuilder.append(" WHERE {\n");

            //filter on class
            queryBuilder.append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" a <").append(type.getUri().toString()).append("> . \n");

            //if we're dealing with an external ontology property, we need a little bit more plumbing
            //Reasoning behind this is like so:
            // - we build optional blocks of both the label property with language set and without language set
            // - the priority in COALESCE is set to search for all properties with an explicit language set,
            //   then all properties again without language set
            // - in the end, the result is bound to the same variable as an internal query
            if (resource) {
                //filter on property
                queryBuilder.append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getUri().toString()).append("> ?").append(internalObjBinding)
                            .append(" .\n");

                //this is a array of ordered properties that are possible human-readable label candidates
                RdfProperty[] labelProps = endpoint.getLabelCandidates(property.getDataType());

                //select the label subject
                String labelSubject = external ? externalObjBinding : internalObjBinding;

                //if we're dealing with an external reference, we need to extra join to bind the local resource to the external one
                //and selecting the right class of the external resource
                if (external) {
                    //bind the external resource
                    queryBuilder.append("\t").append("?").append(internalObjBinding).append(" <").append(Meta.sameAs.getUri()).append("> ?").append(externalObjBinding).append(" .\n");
                    //make sure the right external type is selected
                    queryBuilder.append("\t").append("?").append(externalObjBinding).append(" a <").append(endpoint.getExternalClasses(property.getDataType()).getUri()).append(">")
                                .append(" .\n");
                }

                StringBuilder coalesceBuilderLang = new StringBuilder();
                StringBuilder coalesceBuilderNolang = new StringBuilder();
                for (int i = 0; i < labelProps.length; i++) {

                    RdfProperty labelProp = labelProps[i];
                    String labelNameLang = String.valueOf(i + 1) + "l";
                    String labelNameNolang = String.valueOf(i + 1) + "n";

                    if (i > 0) {
                        coalesceBuilderLang.append(", ");
                        coalesceBuilderNolang.append(", ");
                    }
                    coalesceBuilderLang.append("?").append(labelNameLang);
                    coalesceBuilderNolang.append("?").append(labelNameNolang);

                    queryBuilder.append("\t").append("OPTIONAL {").append("\n");
                    queryBuilder.append("\t").append("\t").append("?").append(labelSubject).append(" <").append(labelProp.getUri()).append("> ?").append(labelNameLang).append(" .\n");
                    queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.LANG, language, labelNameLang));
                    queryBuilder.append("\t").append("}").append("\n");

                    queryBuilder.append("\t").append("OPTIONAL {").append("\n");
                    queryBuilder.append("\t").append("\t").append("?").append(labelSubject).append(" <").append(labelProp.getUri()).append("> ?").append(labelNameNolang).append(" .\n");
                    queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.NOLANG, language, labelNameNolang));
                    queryBuilder.append("\t").append("}").append("\n");
                }

                //merge them together, but add all the optionals without language only after all the optionals with language
                if (coalesceBuilderNolang.length() > 0) {
                    coalesceBuilderLang.append(", ").append(coalesceBuilderNolang);
                }

                // COALESCE takes a list of arguments as input, and outputs the first of those arguments that does not correspond to an error.
                // Since an unbound variable corresponds to an error, this will return the xxx if it exists, otherwise the yyy if it exists, and so on.
                queryBuilder.append("\t").append("BIND( COALESCE(").append(coalesceBuilderLang).append(") as ?").append(SPARQL_OBJECT_BINDING_NAME).append(" ) ").append("\n");
            }
            else {

                //directly filter on the literal property (eg. a string)
                //note that this is a simplified version of the COALESCE method above (for one language and one object binding)

                String labelNameLang = "1l";
                String labelNameNolang = "1n";

                queryBuilder.append("\t").append("OPTIONAL {").append("\n");
                queryBuilder.append("\t").append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getUri()).append("> ?").append(labelNameLang).append(" .\n");
                queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.LANG, language, labelNameLang));
                queryBuilder.append("\t").append("}").append("\n");

                queryBuilder.append("\t").append("OPTIONAL {").append("\n");
                queryBuilder.append("\t").append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getUri()).append("> ?").append(labelNameNolang).append(" .\n");
                queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.NOLANG, language, labelNameNolang));
                queryBuilder.append("\t").append("}").append("\n");

                queryBuilder.append("\t").append("BIND( COALESCE(").append("?").append(labelNameLang).append(",?").append(labelNameNolang).append(") as ?").append(SPARQL_OBJECT_BINDING_NAME)
                            .append(" ) ").append("\n");

            }

            //exclude all null values
            queryBuilder.append("\t").append("FILTER( BOUND(").append("?").append(SPARQL_OBJECT_BINDING_NAME).append(") ) ").append("\n");

            queryBuilder.append("}").append("\n");

            if (caseInsensitive) {
                //Sesame extension function that allows for case-invariant sorting (which feels more natural in filter dropdowns)
                //see http://sesame-general.435816.n3.nabble.com/Case-insensitive-ORDER-BY-in-SPARQL-td890845.html
                queryBuilder.append("ORDER BY ASC(fn:lower-case(str(?").append(SPARQL_OBJECT_BINDING_NAME).append(")))").append("\n");
            }
            else {
                queryBuilder.append("ORDER BY ASC(?").append(SPARQL_OBJECT_BINDING_NAME).append(")").append("\n");
            }

            if (limit > 0) {
                queryBuilder.append("LIMIT ").append(limit).append("\n");
            }

            //Logger.info(queryBuilder);

            SparqlIndexSelectResult result = StorageFactory.getSparqlIndexer().connect().search(queryBuilder.toString(), SesamePageIndexConnection.QueryFormat.SPARQL11_SELECT);

            retVal = new AutoTupleRdfResult(property,
                                            result,
                                            SPARQL_OBJECT_BINDING_NAME,
                                            //for resources, we return the URI as the value,
                                            //for literals, we use the literal object
                                            resource ? internalObjBinding : SPARQL_OBJECT_BINDING_NAME,
                                            language);
        }

        return retVal;
    }

    //-----PRIVATE METHODS-----
    private enum FilterLang
    {
        /**
         * Only include values with a language set
         */
        LANG,

        /**
         * Only include values where the language is empty
         */
        NOLANG,

        /**
         * Include all values (specific language and empty language)
         */
        BOTH
    }
    private CharSequence buildFilter(RdfProperty property, boolean onlyLiteral, FilterLang filterLang, Locale lang, String binding)
    {
        //filter on language if we're dealing with a literal
        StringBuilder retVal = new StringBuilder();

        //this means the datatype is a URI (eg. a Person) so it can never have a language
        retVal.append("FILTER(");

        retVal.append("(");
        if (filterLang == FilterLang.LANG || filterLang == FilterLang.BOTH) {
            retVal.append("lang(?").append(binding).append(") = \"").append(lang.getLanguage()).append("\"");
        }
        if (filterLang == FilterLang.BOTH) {
            retVal.append(" || ");
        }
        if (filterLang == FilterLang.NOLANG || filterLang == FilterLang.BOTH) {
            retVal.append("lang(?").append(binding).append(") = \"\"");
        }
        retVal.append(")");

        if (onlyLiteral) {
            retVal.append(" && isLiteral(?").append(binding).append(")");
        }
        else {
            //this will also add the not-literal entities (that don't have a language and don't seem to be selected by the " = ''" above) if arguments allow it
            if (filterLang == FilterLang.NOLANG || filterLang == FilterLang.BOTH) {
                retVal.append(" || !isLiteral(?").append(binding).append(")");
            }
        }
        retVal.append(")\n");

        return retVal;
    }
    protected Locale getSearchLanguage()
    {
        return R.i18n().getOptimalLocale();
    }

    //-----INNER CLASSES-----
    public static class SearchConfig
    {
        private Controller controller;
        private boolean sortEnabled;
        private Filter[] filters;
        private Locale language;

        public SearchConfig(Controller controller, boolean sortEnabled, Filter[] filters, Locale language)
        {
            this.controller = controller;
            this.sortEnabled = sortEnabled;
            this.filters = filters;
            this.language = language;
        }
        public boolean isSortEnabled()
        {
            return sortEnabled;
        }
        public Filter[] getFilters()
        {
            return filters;
        }
        public RdfTupleResult<String, String> getPossibleValuesFor(Filter filter) throws IOException
        {
            //TODO we might want to cache this value across requests
            return this.controller.searchAllFilterValues(this.controller.getSearchRequest().getTypeOf().getCurie(), filter.getProperty().getCurie(), false, true, MAX_PAGE_SIZE, this.language);
        }
    }

    public static class Filter
    {
        //-----VARIABLES-----
        /**
         * Note: the properties of this class should match the one in box.js - buildActiveFiltersValue()
         */
        private URI curie;

        //-----TRANSIENT VARIABLES-----
        private RdfProperty cachedProperty;

        //-----CONSTRUCTORS-----
        public Filter()
        {
        }

        //-----PUBLIC GETTERS/SETTERS-----
        public URI getCurie()
        {
            return curie;
        }
        //Note: this will auto-box the string value to a URI
        public void setCurie(URI curie)
        {
            this.curie = curie;
        }

        //-----PUBLIC METHODS-----
        public RdfProperty getProperty()
        {
            if (this.cachedProperty == null) {
                this.cachedProperty = RdfFactory.getProperty(this.curie);
            }

            return this.cachedProperty;
        }
        /**
         * We had some issues with referrer locales being used instead of the normal locale (because that's the behavior of RdfClass.getLabel()),
         * so we're forcing the locale to make sure the right one is used.
         */
        public String getPropertyLabel()
        {
            return this.getProperty().getLabelMessage().toString(R.i18n().getOptimalLocale());
        }

        //-----PRIVATE METHODS-----

    }
}
