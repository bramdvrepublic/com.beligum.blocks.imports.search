package com.beligum.blocks.imports.search;

import com.beligum.base.cache.CacheKey;
import com.beligum.base.server.R;
import com.beligum.base.utils.Logger;
import com.beligum.base.utils.json.Json;
import com.beligum.blocks.config.RdfFactory;
import com.beligum.blocks.config.Settings;
import com.beligum.blocks.config.StorageFactory;
import com.beligum.blocks.endpoints.ifaces.RdfQueryEndpoint;
import com.beligum.blocks.filesystem.index.BooleanRdfResult;
import com.beligum.blocks.filesystem.index.LucenePageIndexer;
import com.beligum.blocks.filesystem.index.StringTupleRdfResult;
import com.beligum.blocks.filesystem.index.entries.pages.IndexSearchRequest;
import com.beligum.blocks.filesystem.index.entries.pages.IndexSearchResult;
import com.beligum.blocks.filesystem.index.entries.pages.PageIndexEntry;
import com.beligum.blocks.filesystem.index.ifaces.LuceneQueryConnection;
import com.beligum.blocks.filesystem.index.ifaces.RdfTupleResult;
import com.beligum.blocks.rdf.ifaces.RdfClass;
import com.beligum.blocks.rdf.ifaces.RdfProperty;
import com.beligum.blocks.rdf.ontology.factories.Terms;
import com.beligum.blocks.rdf.ontology.vocabularies.RDF;
import com.beligum.blocks.rdf.ontology.vocabularies.XSD;
import com.beligum.blocks.templating.blocks.DefaultTemplateController;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static com.beligum.blocks.filesystem.index.SesamePageIndexConnection.SPARQL_OBJECT_BINDING_NAME;
import static com.beligum.blocks.filesystem.index.SesamePageIndexConnection.SPARQL_SUBJECT_BINDING_NAME;
import static com.beligum.blocks.imports.search.Controller.CacheKeys.SEARCH_REQUEST;
import static com.beligum.blocks.imports.search.Controller.CacheKeys.SEARCH_RESULT;
import static gen.com.beligum.blocks.imports.search.constants.blocks.imports.search.*;

/**
 * Created by bram on 6/6/16.
 */
public class Controller extends DefaultTemplateController
{
    //-----CONSTANTS-----
    public static final int FIRST_PAGE_INDEX = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

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
        IndexSearchRequest searchRequest = this.getSearchRequest();

        if (searchRequest.getSearchTerm() == null) {
            searchRequest.setSearchTerm(getQueryParam(SEARCH_PARAM_QUERY));
        }

        if (searchRequest.getTypeOf() == null) {
            RdfClass typeOf = null;
            String typeOfParam = null;
            try {
                typeOfParam = this.getParam(SEARCH_PARAM_TYPE, SEARCH_BOX_TYPE_ARG, null);
                if (!StringUtils.isEmpty(typeOfParam)) {
                    typeOf = RdfFactory.getClassForResourceType(URI.create(typeOfParam));
                }
            }
            catch (Exception e) {
                Logger.warn("Invalid type of class; " + typeOfParam);
            }

            searchRequest.setTypeOf(typeOf);
        }

        if (searchRequest.getFieldFilters() == null) {
            searchRequest.setFieldFilters(R.requestContext().getJaxRsRequest().getUriInfo().getQueryParameters().get(SEARCH_PARAM_FILTER));
        }

        if (searchRequest.getSortField() == null) {
            RdfProperty sortField = null;
            String sortParam = this.getQueryParam(SEARCH_PARAM_SORT);
            if (!StringUtils.isEmpty(sortParam)) {
                sortField = (RdfProperty) RdfFactory.getForResourceType(URI.create(sortParam));
            }

            searchRequest.setSortField(sortField);
        }

        if (searchRequest.getSortDescending() == null) {
            String descParam = this.getQueryParam(SEARCH_PARAM_SORT_DESC);
            if (!StringUtils.isEmpty(descParam)) {
                searchRequest.setSortDescending(Boolean.valueOf(descParam));
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
    }

    //-----PUBLIC METHODS-----
    public SearchConfig getSearchConfig()
    {
        return new SearchConfig(this, this.config.containsKey(SEARCH_BOX_SORT_ARG), this.parseSearchFilters(this.config.get(SEARCH_BOX_FILTERS_ARG)));
    }
    public IndexSearchRequest getSearchRequest()
    {
        if (!R.cacheManager().getRequestCache().containsKey(SEARCH_REQUEST)) {
            R.cacheManager().getRequestCache().put(SEARCH_REQUEST, new IndexSearchRequest());
        }

        return (IndexSearchRequest) R.cacheManager().getRequestCache().get(SEARCH_REQUEST);
    }
    /**
     * Note: the concept for the lazy loaded results is to postpone the use (the effective query) of the request object as long as possible
     * up until the moment it's actually requested (from the template engine), and use the constructor of each controller instance to
     * add more config variables to the request object, so we effectively 'merge' the configs of the search-blocks on a page.
     */
    public IndexSearchResult getSearchResult()
    {
        if (!R.cacheManager().getRequestCache().containsKey(SEARCH_RESULT)) {

            //let's not return nulls, so we can always use .size() and so on
            IndexSearchResult searchResult = new IndexSearchResult(new ArrayList<>());
            try {
                IndexSearchRequest searchRequest = this.getSearchRequest();
                Locale locale = R.i18n().getOptimalLocale();

                //set some defaults if still empty..
                if (searchRequest.getSortDescending() == null) {
                    searchRequest.setSortDescending(false);
                }
                if (searchRequest.getPageIndex() == null) {
                    searchRequest.setPageIndex(FIRST_PAGE_INDEX);
                }
                if (searchRequest.getPageSize() == null) {
                    searchRequest.setPageSize(DEFAULT_PAGE_SIZE);
                }
                if (searchRequest.getFormat() == null) {
                    searchRequest.setFormat(SEARCH_RESULTS_FORMAT_LIST);
                }

                LuceneQueryConnection queryConnection = StorageFactory.getMainPageQueryConnection();

                org.apache.lucene.search.BooleanQuery pageQuery = new org.apache.lucene.search.BooleanQuery();

                pageQuery.add(new TermQuery(new Term(PageIndexEntry.Field.language.name(), locale.getLanguage())), BooleanClause.Occur.FILTER);

                if (searchRequest.getTypeOf() != null) {
                    pageQuery.add(new TermQuery(new Term(PageIndexEntry.Field.typeOf.name(), searchRequest.getTypeOf().getCurieName().toString())), BooleanClause.Occur.FILTER);
                }

                if (searchRequest.getFieldFilters() != null) {
                    this.addFieldFilters(searchRequest.getFieldFilters(), pageQuery, locale);
                }

                if (!StringUtils.isEmpty(searchRequest.getSearchTerm())) {
                    pageQuery.add(queryConnection.buildWildcardQuery(null, searchRequest.getSearchTerm(), false), BooleanClause.Occur.MUST);
                }

                //this.searchResult = StorageFactory.getTriplestoreQueryConnection().search(rdfClass, searchTerm, new HashMap<RdfProperty, String>(), sortField, false, RESOURCES_ON_PAGE, selectedPage, R.i18n().getOptimalLocale());
                searchResult = queryConnection.search(pageQuery, searchRequest.getSortField(), searchRequest.getSortDescending(), searchRequest.getPageSize(), searchRequest.getPageIndex());
            }
            catch (Exception e) {
                Logger.error("Error while executing search query", e);
            }

            R.cacheManager().getRequestCache().put(SEARCH_RESULT, searchResult);
        }

        return (IndexSearchResult) R.cacheManager().getRequestCache().get(SEARCH_RESULT);
    }

    //-----PROTECTED METHODS-----
    protected String getQueryParam(String name)
    {
        String retVal = null;

        if (!StringUtils.isEmpty(name)) {
            List<String> query = R.requestContext().getJaxRsRequest().getUriInfo().getQueryParameters().get(name);
            if (query != null && query.size() > 0) {
                retVal = query.get(0).trim();
            }
        }

        return retVal;
    }
    protected List<String> getQueryParams(String name)
    {
        List<String> retVal = R.requestContext().getJaxRsRequest().getUriInfo().getQueryParameters().get(name);
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
    protected Map<RdfProperty, List<String>> addFieldFilters(List<String> filters, org.apache.lucene.search.BooleanQuery query, Locale locale) throws IOException, ParseException
    {
        //TODO it probably makes sense to activate this (working code!) for some cases;
        // eg if the filter-field is a boolean with value 'false', you may want to include the entries without such a field at all too
        boolean includeNonExisting = false;

        Map<RdfProperty, List<String>> retVal = new LinkedHashMap<>();

        if (filters != null) {
            for (String filter : filters) {
                if (!StringUtils.isEmpty(filter)) {
                    String[] keyVal = filter.split(SEARCH_PARAM_FILTER_DELIM);

                    if (keyVal.length == 2) {
                        RdfProperty key = (RdfProperty) RdfFactory.getForResourceType(URI.create(keyVal[0]));
                        if (key != null) {
                            //prepare the raw value for lookup in the index
                            Object val = key.prepareIndexValue(keyVal[1], locale);

                            String valStr = val == null ? null : val.toString();
                            if (!StringUtils.isEmpty(valStr)) {
                                //TODO if you want to use this: make sure you rewrite the term query first (see else())
                                if (includeNonExisting) {
                                    //the following is the Lucene logic for: if you find a field, it should match x, but if you don't find such a field, include it as well
                                    String fieldName = key.getCurieName().toString();
                                    org.apache.lucene.search.BooleanQuery subQuery = new org.apache.lucene.search.BooleanQuery();
                                    subQuery.add(new TermQuery(new Term(fieldName, valStr)), BooleanClause.Occur.SHOULD);

                                    //see https://kb.ucla.edu/articles/pure-negation-query-in-lucene
                                    //and https://wiki.apache.org/lucene-java/LuceneFAQ#How_does_one_determine_which_documents_do_not_have_a_certain_term.3F
                                    org.apache.lucene.search.BooleanQuery fakeNegationQuery = new org.apache.lucene.search.BooleanQuery();
                                    fakeNegationQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
                                    fakeNegationQuery.add(new TermQuery(new Term(LucenePageIndexer.CUSTOM_FIELD_FIELDS, fieldName)), BooleanClause.Occur.MUST_NOT);
                                    subQuery.add(fakeNegationQuery, BooleanClause.Occur.SHOULD);

                                    query.add(subQuery, BooleanClause.Occur.FILTER);
                                }
                                else {
                                    if (key.getDataType().equals(XSD.STRING)
                                        || key.getDataType().equals(XSD.NORMALIZED_STRING)
                                        || key.getDataType().equals(RDF.LANGSTRING)
                                        || key.getDataType().equals(RDF.HTML)) {

                                        //TODO as long as we haven't re-indexed everything after 20/03/17 (to index strings as constants too), we have to use this for strings
                                        //this is a text-indexed alternative to the (more exact) term query above
                                        ComplexPhraseQueryParser queryParser = new ComplexPhraseQueryParser(key.getCurieName().toString(), LucenePageIndexer.DEFAULT_ANALYZER);
                                        queryParser.setInOrder(true);
                                        query.add(queryParser.parse("\"" + LucenePageIndexer.removeEscapedChars(valStr, "") + "\""), BooleanClause.Occur.FILTER);
                                    }
                                    else {
                                        query.add(new TermQuery(new Term(key.getCurieName().toString(), valStr)), BooleanClause.Occur.FILTER);
                                    }
                                }

                                List<String> values = retVal.get(key);
                                if (values == null) {
                                    retVal.put(key, values = new ArrayList<>());
                                }
                                values.add(valStr);
                            }
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

        return retVal;
    }
    /**
     * Returns a list of all distinct possible values for the given filter in the current language
     */
    protected RdfTupleResult<String, String> searchAllFilterValues(URI resourceTypeCurie, URI resourcePropertyCurie, boolean onlyLiteral, int limit) throws IOException
    {
        RdfTupleResult<String, String> retVal = null;

        RdfClass type = RdfFactory.getClassForResourceType(resourceTypeCurie);
        if (type == null) {
            Logger.warn("Encountered unknown resource type; " + resourceTypeCurie);
        }

        RdfProperty property = (RdfProperty) RdfFactory.getClassForResourceType(resourcePropertyCurie);
        if (property == null) {
            Logger.warn("Encountered unknown resource property; " + resourcePropertyCurie);
        }

        if (type != null && property != null) {

            RdfClass propertyDatatype = property.getDataType();

            if (propertyDatatype.equals(XSD.BOOLEAN)) {
                retVal = new BooleanRdfResult();
            }
            else {
                Locale lang = R.i18n().getOptimalLocale();

                //this means we're dealing with a property that has URI values (internal or external)
                boolean resource = property.getDataType().getType().equals(RdfClass.Type.CLASS);

                //this means the endpoint of this property is external (or the property is external)
                RdfQueryEndpoint endpoint = property.getDataType().getEndpoint();
                boolean external = endpoint != null && endpoint.isExternal();

                //this is a array of ordered properties that are possible human-readable label candidates
                RdfProperty[] labelProps = endpoint.getLabelCandidates(property.getDataType());
                final String internalObjBinding = SPARQL_OBJECT_BINDING_NAME + "In";
                final String externalObjBinding = SPARQL_OBJECT_BINDING_NAME + "Ex";

                StringBuilder queryBuilder = new StringBuilder();
                //final String searchPrefix = "search";
                queryBuilder.append("PREFIX ").append(Settings.instance().getRdfOntologyPrefix()).append(": <").append(Settings.instance().getRdfOntologyUri()).append("> \n");
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
                queryBuilder.append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" a <").append(type.getFullName().toString()).append("> . \n");

                //if we're dealing with an external ontology property, we need a little bit more plumbing
                //Reasoning behind this is like so:
                // - we build optional blocks of both the label property with language set and without language set
                // - the priority in COALESCE is set to search for all properties with an explicit language set,
                //   then all properties again without language set
                // - in the end, the result is bound to the same variable as an internal query
                if (resource) {
                    //filter on property
                    queryBuilder.append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getFullName().toString()).append("> ?").append(internalObjBinding)
                                .append(" .\n");

                    //select the label subject
                    String labelSubject = external ? externalObjBinding : internalObjBinding;

                    //if we're dealing with an external reference, we need to extra joining to bind the local resource to the external one
                    //and selecting the right class of the external resource
                    if (external) {
                        //bind the external resource
                        queryBuilder.append("\t").append("?").append(internalObjBinding).append(" <").append(Terms.sameAs.getFullName()).append("> ?").append(externalObjBinding).append(" .\n");
                        //make sure the right external type is selected
                        queryBuilder.append("\t").append("?").append(externalObjBinding).append(" a <").append(endpoint.getExternalClasses(property.getDataType()).getFullName()).append(">")
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
                        queryBuilder.append("\t").append("\t").append("?").append(labelSubject).append(" <").append(labelProp.getFullName()).append("> ?").append(labelNameLang).append(" .\n");
                        queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.LANG, lang, labelNameLang));
                        queryBuilder.append("\t").append("}").append("\n");

                        queryBuilder.append("\t").append("OPTIONAL {").append("\n");
                        queryBuilder.append("\t").append("\t").append("?").append(labelSubject).append(" <").append(labelProp.getFullName()).append("> ?").append(labelNameNolang).append(" .\n");
                        queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.NOLANG, lang, labelNameNolang));
                        queryBuilder.append("\t").append("}").append("\n");
                    }

                    //merge them together, but add all the optionals without language only after all the optionals with language
                    if (coalesceBuilderNolang.length() > 0) {
                        coalesceBuilderLang.append(", ").append(coalesceBuilderNolang);
                    }

                    // COALESCE takes a list of arguments as input, and outputs the first of those arguments that does not correspond to an error.
                    // Since an unbound variable corresponds to an error, this will return the xxx if it exists, otherwise the yyy if it exists, otherwise...
                    queryBuilder.append("\t").append("BIND( COALESCE(").append(coalesceBuilderLang).append(") as ?").append(SPARQL_OBJECT_BINDING_NAME).append(" ) ").append("\n");
                }
                else {
                    //this means the datatype is a URI (eg. a Person) and we need to find it's label (title)
                    if (property.getDataType().getType().equals(RdfClass.Type.CLASS)) {
                        queryBuilder.append("\t")
                                    .append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getFullName().toString()).append("> ?").append(internalObjBinding)
                                    .append(" .\n");
                        queryBuilder.append("\t")
                                    .append("?").append(internalObjBinding).append(" <").append(Terms.title.getFullName()).append("> ?").append(SPARQL_OBJECT_BINDING_NAME)
                                    .append(" .\n");
                    }
                    else {
                        //directly filter on the literal property (eg. a string)
                        queryBuilder.append("\t")
                                    .append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getFullName().toString()).append("> ?").append(SPARQL_OBJECT_BINDING_NAME)
                                    .append(" .\n");
                    }

                    queryBuilder.append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.BOTH, lang, SPARQL_OBJECT_BINDING_NAME)).append("\n");
                }

                queryBuilder.append("}").append("\n");

                queryBuilder.append("ORDER BY ASC(?").append(SPARQL_OBJECT_BINDING_NAME).append(")").append("\n");

                if (limit > 0) {
                    queryBuilder.append("LIMIT ").append(limit).append("\n");
                }

                //Logger.info(queryBuilder);

                TupleQueryResult result = StorageFactory.getTriplestoreQueryConnection().query(queryBuilder.toString()).evaluate();

                retVal = new StringTupleRdfResult(result,
                                                  SPARQL_OBJECT_BINDING_NAME,
                                                  //for resources, we return the URI as the value,
                                                  //for literals, we use the literal object
                                                  resource ? internalObjBinding : SPARQL_OBJECT_BINDING_NAME);
            }

            //make sure the result iterator will be closed at the end of this request
            R.requestContext().registerClosable(retVal);
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
        retVal.append(")\n");

        return retVal;
    }

    //-----INNER CLASSES-----
    public static class SearchConfig
    {
        private Controller controller;
        private boolean sortEnabled;
        private Filter[] filters;

        public SearchConfig(Controller controller, boolean sortEnabled, Filter[] filters)
        {
            this.controller = controller;
            this.sortEnabled = sortEnabled;
            this.filters = filters;
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
            return this.controller.searchAllFilterValues(this.controller.getSearchRequest().getTypeOf().getCurieName(), filter.getProperty().getCurieName(), false, 1000);
        }
    }

    public static class Filter
    {
        //-----VARIABLES-----
        /**
         * Note: the properties of this class should match the one in box.js - buildActiveFiltersValue()
         */
        private URI curieName;

        //-----TRANSIENT VARIABLES-----
        private RdfClass cachedProperty;

        //-----CONSTRUCTORS-----
        public Filter()
        {
        }

        //-----PUBLIC GETTERS/SETTERS-----
        public URI getCurieName()
        {
            return curieName;
        }
        //Note: this will auto-box the string value to a URI
        public void setCurieName(URI curieName)
        {
            this.curieName = curieName;
        }

        //-----PUBLIC METHODS-----
        public RdfClass getProperty()
        {
            if (this.cachedProperty == null) {
                this.cachedProperty = RdfFactory.getClassForResourceType(this.curieName);
            }

            return this.cachedProperty;
        }

        //-----PRIVATE METHODS-----

    }
}
