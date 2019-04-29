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

import com.beligum.blocks.templating.blocks.DefaultTemplateController;

/**
 * Created by bram on 6/6/16.
 */
public class Controller extends DefaultTemplateController
{
//    //-----CONSTANTS-----
//    public static final int FIRST_PAGE_INDEX = 0;
//    public static final int DEFAULT_PAGE_SIZE = 10;
//    //sync this with results.js
//    public static final int MAX_PAGE_SIZE = 1000;
//
//    public enum CacheKeys implements CacheKey
//    {
//        SEARCH_REQUEST,
//        SEARCH_RESULT
//    }
//
//    //-----VARIABLES-----
//
//    //-----CONSTRUCTORS-----
    @Override
    public void created()
    {
//        IndexSearchRequest searchRequest = this.getSearchRequest();
//
//        if (searchRequest.getSearchTerm() == null) {
//            searchRequest.setSearchTerm(getQueryParam(SEARCH_PARAM_QUERY));
//        }
//
//        if (searchRequest.getTypeOf() == null) {
//            RdfClass typeOf = null;
//            String typeOfParam = null;
//            try {
//                typeOfParam = this.getParam(SEARCH_PARAM_TYPE, SEARCH_BOX_TYPE_ARG, null);
//                if (!StringUtils.isEmpty(typeOfParam)) {
//                    typeOf = RdfFactory.getClass(typeOfParam);
//                }
//            }
//            catch (Exception e) {
//                Logger.warn("Invalid type of class; " + typeOfParam);
//            }
//
//            searchRequest.setTypeOf(typeOf);
//        }
//
//        if (searchRequest.getFieldFilters() == null) {
//            searchRequest.setFieldFilters(R.requestContext().getJaxRsRequest().getUriInfo().getQueryParameters().get(SEARCH_PARAM_FILTER));
//        }
//
//        if (searchRequest.getSortField() == null) {
//            RdfProperty sortField = null;
//            String sortParam = this.getParam(SEARCH_PARAM_SORT, SEARCH_BOX_SORT_ARG, null);
//            if (!StringUtils.isEmpty(sortParam)) {
//                sortField = (RdfProperty) RdfFactory.getOntologyMember(URI.create(sortParam));
//            }
//
//            searchRequest.setSortField(sortField);
//        }
//
//        if (searchRequest.getSortDescending() == null) {
//            String descParam = this.getParam(SEARCH_PARAM_SORT_DESC, SEARCH_BOX_DESC_ARG, null);
//            if (!StringUtils.isEmpty(descParam)) {
//                searchRequest.setSortDescending(Boolean.valueOf(descParam));
//            }
//            else {
//                searchRequest.setSortDescending(false);
//            }
//        }
//
//        if (searchRequest.getPageIndex() == null) {
//            Integer pageIndex = null;
//            String pageIndexParam = null;
//            try {
//                pageIndexParam = this.getParam(SEARCH_PARAM_INDEX, SEARCH_RESULTS_INDEX_ARG, null);
//                if (!StringUtils.isEmpty(pageIndexParam)) {
//                    pageIndex = Math.max(Integer.parseInt(pageIndexParam), FIRST_PAGE_INDEX);
//                }
//            }
//            catch (Exception e) {
//                Logger.warn("Invalid page index; " + pageIndexParam);
//            }
//
//            searchRequest.setPageIndex(pageIndex);
//        }
//
//        if (searchRequest.getPageSize() == null) {
//            Integer pageSize = null;
//            String pageSizeParam = null;
//            try {
//                pageSizeParam = this.getParam(SEARCH_PARAM_SIZE, SEARCH_RESULTS_SIZE_ARG, null);
//                if (!StringUtils.isEmpty(pageSizeParam)) {
//                    pageSize = Math.min(Integer.parseInt(pageSizeParam), MAX_PAGE_SIZE);
//                }
//            }
//            catch (Exception e) {
//                Logger.warn("Invalid page size; " + pageSizeParam);
//            }
//
//            searchRequest.setPageSize(pageSize);
//        }
//
//        if (searchRequest.getFormat() == null) {
//            String format = null;
//            String resultsFormatConfig = this.config.get(SEARCH_RESULTS_FORMAT_ARG);
//            if (!StringUtils.isEmpty(resultsFormatConfig)) {
//                format = resultsFormatConfig;
//            }
//
//            searchRequest.setFormat(format);
//        }
//
//        if (searchRequest.getHideHeader() == null) {
//            String resultsHideHeaderConfig = this.config.get(SEARCH_RESULTS_HIDE_HEADER_ARG);
//            if (!StringUtils.isEmpty(resultsHideHeaderConfig)) {
//                searchRequest.setHideHeader(Boolean.parseBoolean(resultsHideHeaderConfig));
//            }
//            else {
//                searchRequest.setHideHeader(false);
//            }
//        }
//
//        if (searchRequest.getHidePager() == null) {
//            String resultsHidePagerConfig = this.config.get(SEARCH_RESULTS_HIDE_PAGER_ARG);
//            if (!StringUtils.isEmpty(resultsHidePagerConfig)) {
//                searchRequest.setHidePager(Boolean.parseBoolean(resultsHidePagerConfig));
//            }
//            else {
//                searchRequest.setHidePager(false);
//            }
//        }
    }
//
//    //-----PUBLIC METHODS-----
//    public SearchConfig getSearchConfig()
//    {
//        return new SearchConfig(this, this.config.containsKey(SEARCH_BOX_SORT_ARG), this.parseSearchFilters(this.config.get(SEARCH_BOX_FILTERS_ARG)), this.getSearchLanguage());
//    }
//    public DefaultIndexSearchRequest getSearchRequest()
//    {
//        if (!R.cacheManager().getRequestCache().containsKey(SEARCH_REQUEST)) {
//            R.cacheManager().getRequestCache().put(SEARCH_REQUEST, DefaultIndexSearchRequest.create());
//        }
//
//        return R.cacheManager().getRequestCache().get(SEARCH_REQUEST);
//    }
//    /**
//     * Note: the concept for the lazy loaded results is to postpone the use (the effective query) of the request object as long as possible
//     * up until the moment it's actually requested (from the template engine), and use the constructor of each controller instance to
//     * add more config variables to the request object, so we effectively 'merge' the configs of the search-blocks on a page.
//     */
//    public IndexSearchResult getSearchResult()
//    {
//        if (!R.cacheManager().getRequestCache().containsKey(SEARCH_RESULT)) {
//
//            //let's not return nulls, so we can always use .size() and so on
//            IndexSearchResult searchResult = new SimpleIndexSearchResult(new ArrayList<>());
//            try {
//                DefaultIndexSearchRequest searchRequest = this.getSearchRequest();
//                Locale locale = this.getSearchLanguage();
//
//                //set some defaults if still empty..
//                if (searchRequest.getSortDescending() == null) {
//                    searchRequest.setSortDescending(false);
//                }
//                if (searchRequest.getPageIndex() == null) {
//                    searchRequest.setPageIndex(FIRST_PAGE_INDEX);
//                }
//                if (searchRequest.getPageSize() == null) {
//                    searchRequest.setPageSize(DEFAULT_PAGE_SIZE);
//                }
//                if (searchRequest.getFormat() == null) {
//                    searchRequest.setFormat(SEARCH_RESULTS_FORMAT_LIST);
//                }
//
//                searchRequest.filter(PageIndexEntry.language, locale.getLanguage(), IndexSearchRequest.FilterBoolean.AND);
//
//                //Note: by default, we don't include sub-resources as search results. This might be made configurable in the future, but for now, we filter them out.
//                //Also note that they 'work', eg. the link they generate resolves correctly to it's containing page
//                searchRequest.filter(PageIndexEntry.parentId, PageIndexEntry.NULL_VALUE, IndexSearchRequest.FilterBoolean.AND);
//
//                if (searchRequest.getTypeOf() != null) {
//                    searchRequest.filter(PageIndexEntry.typeOf, searchRequest.getTypeOf().getCurie().toString(), IndexSearchRequest.FilterBoolean.AND);
//                }
//
//                if (searchRequest.getFieldFilters() != null) {
//                    this.addFieldFilters(searchRequest.getFieldFilters(), pageQuery, locale);
//                }
//
//                if (!StringUtils.isEmpty(searchRequest.getSearchTerm())) {
//                    searchRequest.wildcard(searchRequest.getSearchTerm(), IndexSearchRequest.FilterBoolean.OR);
//                }
//
//                searchResult = StorageFactory.getJsonQueryConnection().search(pageQuery, searchRequest.getSortField(), searchRequest.getSortDescending(), searchRequest.getPageSize(), searchRequest.getPageIndex());
//            }
//            catch (Exception e) {
//                Logger.error("Error while executing search query", e);
//            }
//
//            R.cacheManager().getRequestCache().put(SEARCH_RESULT, searchResult);
//        }
//
//        return R.cacheManager().getRequestCache().get(SEARCH_RESULT);
//    }
//
//    //-----PROTECTED METHODS-----
//    protected String getQueryParam(String name)
//    {
//        String retVal = null;
//
//        if (!StringUtils.isEmpty(name)) {
//            List<String> query = R.requestContext().getJaxRsRequest().getUriInfo().getQueryParameters().get(name);
//            if (query != null && query.size() > 0) {
//                retVal = query.get(0).trim();
//            }
//        }
//
//        return retVal;
//    }
//    protected List<String> getQueryParams(String name)
//    {
//        List<String> retVal = R.requestContext().getJaxRsRequest().getUriInfo().getQueryParameters().get(name);
//        return retVal == null ? new ArrayList<>() : retVal;
//    }
//    protected String getParam(String queryParam, String configParam, String defaultValue)
//    {
//        //first, we try the query param
//        String retVal = this.getQueryParam(queryParam);
//
//        //then the config value in the html
//        //Note: an empty value is also a value, so don't check for empty values here
//        if (retVal == null) {
//            if (configParam != null) {
//                retVal = this.config.get(configParam);
//            }
//        }
//
//        if (retVal == null) {
//            retVal = defaultValue;
//        }
//
//        return retVal;
//    }
//    protected Filter[] parseSearchFilters(String base64JsonConfigAttr)
//    {
//        Filter[] retVal = null;
//
//        if (!StringUtils.isEmpty(base64JsonConfigAttr)) {
//            try {
//                String jsonConfig = org.apache.shiro.codec.Base64.decodeToString(base64JsonConfigAttr);
//                retVal = Json.getObjectMapper().readValue(jsonConfig, Filter[].class);
//            }
//            catch (Exception e) {
//                Logger.error("Error while parsing filters config argument; " + base64JsonConfigAttr, e);
//            }
//        }
//
//        return retVal;
//    }
//    protected void addFieldFilters(List<String> filters, org.apache.lucene.search.BooleanQuery query, Locale locale) throws IOException, ParseException
//    {
//        if (filters != null) {
//            for (String filter : filters) {
//                if (!StringUtils.isEmpty(filter)) {
//                    String[] keyVal = filter.split(SEARCH_PARAM_FILTER_DELIM);
//
//                    if (keyVal.length == 2) {
//                        RdfProperty rdfProperty = (RdfProperty) RdfFactory.getOntologyMember(URI.create(keyVal[0]));
//                        if (rdfProperty != null) {
//                            //prepare the raw value for lookup in the index
//                            Object val = rdfProperty.prepareIndexValue(keyVal[1], locale);
//
//                            //Right now, we have three main categories of values:
//                            // - numbers (with different precision)
//                            // - string (constant or not)
//                            // - URIs
//                            String field = rdfProperty.getCurie().toString();
//                            if (val instanceof Number) {
//                                NumericRangeQuery q;
//                                if (val instanceof Double) {
//                                    Double valDouble = (Double) val;
//                                    q = NumericRangeQuery.newDoubleRange(field, valDouble, valDouble, true, true);
//                                }
//                                else if (val instanceof Float) {
//                                    Float valFloat = (Float) val;
//                                    q = NumericRangeQuery.newFloatRange(field, valFloat, valFloat, true, true);
//                                }
//                                else if (val instanceof Long) {
//                                    Long valLong = (Long) val;
//                                    q = NumericRangeQuery.newLongRange(field, valLong, valLong, true, true);
//                                }
//                                //we'll assume all other numbers can be cast to an Integer, hope that's OK
//                                else {
//                                    Integer valInteger = (Integer) val;
//                                    q = NumericRangeQuery.newIntRange(field, valInteger, valInteger, true, true);
//                                }
//
//                                query.add(q, BooleanClause.Occur.FILTER);
//                            }
//                            else if (val instanceof URI) {
//                                //URIs are indexed as constant fields, so we can just look it up
//                                query.add(new TermQuery(new Term(field, String.valueOf(val))), BooleanClause.Occur.FILTER);
//                            }
//                            else {
//                                String valStr = String.valueOf(val);
//
//                                //Here, we assume we don't know how the field was indexed (analyzed or verbatim), so it makes sense to include both and OR them together
//                                org.apache.lucene.search.BooleanQuery termQuery = new org.apache.lucene.search.BooleanQuery();
//                                termQuery.add(new TermQuery(new Term(field, valStr)), BooleanClause.Occur.SHOULD);
//                                termQuery.add(new TermQuery(new Term(LucenePageIndexer.buildVerbatimFieldName(field), valStr)), BooleanClause.Occur.SHOULD);
//
//                                query.add(termQuery, BooleanClause.Occur.FILTER);
//                            }
//
//                            // eg if the filter-field is a boolean with value 'false',
//                            // you may want to include the entries without such a field at all too
//                            //                            String valStr = val == null ? null : val.toString();
//                            //                            if (!StringUtils.isEmpty(valStr)) {
//                            //                                //TODO if you want to use this: make sure you rewrite the term query first (see else())
//                            //                                if (includeNonExisting) {
//                            //                                    //the following is the Lucene logic for: if you find a field, it should match x, but if you don't find such a field, include it as well
//                            //                                    String fieldName = rdfProperty.getCurie().toString();
//                            //                                    org.apache.lucene.search.BooleanQuery subQuery = new org.apache.lucene.search.BooleanQuery();
//                            //                                    subQuery.add(new TermQuery(new Term(fieldName, valStr)), BooleanClause.Occur.SHOULD);
//                            //
//                            //                                    //see https://kb.ucla.edu/articles/pure-negation-query-in-lucene
//                            //                                    //and https://wiki.apache.org/lucene-java/LuceneFAQ#How_does_one_determine_which_documents_do_not_have_a_certain_term.3F
//                            //                                    org.apache.lucene.search.BooleanQuery fakeNegationQuery = new org.apache.lucene.search.BooleanQuery();
//                            //                                    fakeNegationQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
//                            //                                    fakeNegationQuery.add(new TermQuery(new Term(LucenePageIndexer.CUSTOM_FIELD_FIELDS, fieldName)), BooleanClause.Occur.MUST_NOT);
//                            //                                    subQuery.add(fakeNegationQuery, BooleanClause.Occur.SHOULD);
//                            //
//                            //                                    query.add(subQuery, BooleanClause.Occur.FILTER);
//                            //                                }
//                            //                                else {
//                            //                                    //This is old code we keep as a reference, but in some cases, it didn't work as expected.
//                            //                                    //Since we have good reindexing code now, we can easily reindex everything, so the term query works as expected.
//                            //                                    //                                    if (key.getDataType().equals(XSD.string)
//                            //                                    //                                        || key.getDataType().equals(XSD.normalizedString)
//                            //                                    //                                        || key.getDataType().equals(RDF.LANGSTRING)
//                            //                                    //                                        || key.getDataType().equals(RDF.HTML)) {
//                            //                                    //
//                            //                                    //                                        //TODO as long as we haven't re-indexed everything after 20/03/17 (to index strings as constants too), we have to use this for strings
//                            //                                    //                                        //this is a text-indexed alternative to the (more exact) term query below
//                            //                                    //                                        ComplexPhraseQueryParser queryParser = new ComplexPhraseQueryParser(key.getCurie().toString(), LucenePageIndexer.DEFAULT_ANALYZER);
//                            //                                    //                                        queryParser.setInOrder(true);
//                            //                                    //
//                            //                                    //                                        String valStrEsc = LucenePageIndexer.removeEscapedChars(valStr, "");
//                            //                                    //
//                            //                                    //                                        query.add(queryParser.parse("\"" + valStr + "\""), BooleanClause.Occur.FILTER);
//                            //                                    //                                    }
//                            //                                    //                                    else {
//                            //                                    query.add(new TermQuery(new Term(rdfProperty.getCurie().toString(), valStr)), BooleanClause.Occur.FILTER);
//                            //                                    //                                    }
//                            //                                }
//                            //
//                            //                                List<String> values = retVal.get(rdfProperty);
//                            //                                if (values == null) {
//                            //                                    retVal.put(rdfProperty, values = new ArrayList<>());
//                            //                                }
//                            //                                values.add(valStr);
//                            //                            }
//                        }
//                        else {
//                            Logger.warn("Encountered unknown RDF property in search filter; ignoring filter" + filter);
//                        }
//                    }
//                    else {
//                        Logger.warn("Encountered search filter value with a wrong syntax (not parsable to key/value); ignoring filter; " + filter);
//                    }
//                }
//            }
//        }
//    }
//    /**
//     * Returns a list of all distinct possible values for the given filter in the current language
//     */
//    protected RdfTupleResult<String, String> searchAllFilterValues(URI resourceTypeCurie, URI resourcePropertyCurie, boolean onlyLiteral, boolean caseInsensitive, int limit, Locale language)
//                    throws IOException
//    {
//        RdfTupleResult<String, String> retVal = null;
//
//        RdfClass type = RdfFactory.getClass(resourceTypeCurie);
//        if (type == null) {
//            Logger.warn("Encountered unknown resource type; " + resourceTypeCurie);
//        }
//
//        RdfProperty property = RdfFactory.getProperty(resourcePropertyCurie);
//        if (property == null) {
//            Logger.warn("Encountered unknown resource property; " + resourcePropertyCurie);
//        }
//
//        if (type != null && property != null) {
//
//            //this means we're dealing with a property that has URI values (internal or external)
//            boolean resource = property.getDataType().getType().equals(RdfClass.Type.CLASS);
//
//            //this means the endpoint of this property is external (or the property is external)
//            RdfQueryEndpoint endpoint = property.getDataType().getEndpoint();
//            boolean external = endpoint != null && endpoint.isExternal();
//
//            final String internalObjBinding = SPARQL_OBJECT_BINDING_NAME + "In";
//            final String externalObjBinding = SPARQL_OBJECT_BINDING_NAME + "Ex";
//
//            StringBuilder queryBuilder = new StringBuilder();
//            //final String searchPrefix = "search";
//            queryBuilder.append("PREFIX ").append(Local.NAMESPACE.getPrefix()).append(": <").append(Local.NAMESPACE.getUri()).append("> \n");
//            //queryBuilder.append("PREFIX ").append(searchPrefix).append(": <").append(LuceneSailSchema.NAMESPACE).append("> \n");
//            queryBuilder.append("\n");
//            queryBuilder.append("SELECT DISTINCT")/*.append(" ?").append(SPARQL_SUBJECT_BINDING_NAME).append(" ?").append(SPARQL_PREDICATE_BINDING_NAME)*/;
//            //we'll need this URL of the value later on
//            if (resource) {
//                queryBuilder.append(" ?").append(internalObjBinding);
//            }
//            queryBuilder.append(" ?").append(SPARQL_OBJECT_BINDING_NAME);
//
//            queryBuilder.append(" WHERE {\n");
//
//            //filter on class
//            queryBuilder.append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" a <").append(type.getUri().toString()).append("> . \n");
//
//            //if we're dealing with an external ontology property, we need a little bit more plumbing
//            //Reasoning behind this is like so:
//            // - we build optional blocks of both the label property with language set and without language set
//            // - the priority in COALESCE is set to search for all properties with an explicit language set,
//            //   then all properties again without language set
//            // - in the end, the result is bound to the same variable as an internal query
//            if (resource) {
//                //filter on property
//                queryBuilder.append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getUri().toString()).append("> ?").append(internalObjBinding)
//                            .append(" .\n");
//
//                //this is a array of ordered properties that are possible human-readable label candidates
//                RdfProperty[] labelProps = endpoint.getLabelCandidates(property.getDataType());
//
//                //select the label subject
//                String labelSubject = external ? externalObjBinding : internalObjBinding;
//
//                //if we're dealing with an external reference, we need to extra join to bind the local resource to the external one
//                //and selecting the right class of the external resource
//                if (external) {
//                    //bind the external resource
//                    queryBuilder.append("\t").append("?").append(internalObjBinding).append(" <").append(Meta.sameAs.getUri()).append("> ?").append(externalObjBinding).append(" .\n");
//                    //make sure the right external type is selected
//                    queryBuilder.append("\t").append("?").append(externalObjBinding).append(" a <").append(endpoint.getExternalClasses(property.getDataType()).getUri()).append(">")
//                                .append(" .\n");
//                }
//
//                StringBuilder coalesceBuilderLang = new StringBuilder();
//                StringBuilder coalesceBuilderNolang = new StringBuilder();
//                for (int i = 0; i < labelProps.length; i++) {
//
//                    RdfProperty labelProp = labelProps[i];
//                    String labelNameLang = String.valueOf(i + 1) + "l";
//                    String labelNameNolang = String.valueOf(i + 1) + "n";
//
//                    if (i > 0) {
//                        coalesceBuilderLang.append(", ");
//                        coalesceBuilderNolang.append(", ");
//                    }
//                    coalesceBuilderLang.append("?").append(labelNameLang);
//                    coalesceBuilderNolang.append("?").append(labelNameNolang);
//
//                    queryBuilder.append("\t").append("OPTIONAL {").append("\n");
//                    queryBuilder.append("\t").append("\t").append("?").append(labelSubject).append(" <").append(labelProp.getUri()).append("> ?").append(labelNameLang).append(" .\n");
//                    queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.LANG, language, labelNameLang));
//                    queryBuilder.append("\t").append("}").append("\n");
//
//                    queryBuilder.append("\t").append("OPTIONAL {").append("\n");
//                    queryBuilder.append("\t").append("\t").append("?").append(labelSubject).append(" <").append(labelProp.getUri()).append("> ?").append(labelNameNolang).append(" .\n");
//                    queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.NOLANG, language, labelNameNolang));
//                    queryBuilder.append("\t").append("}").append("\n");
//                }
//
//                //merge them together, but add all the optionals without language only after all the optionals with language
//                if (coalesceBuilderNolang.length() > 0) {
//                    coalesceBuilderLang.append(", ").append(coalesceBuilderNolang);
//                }
//
//                // COALESCE takes a list of arguments as input, and outputs the first of those arguments that does not correspond to an error.
//                // Since an unbound variable corresponds to an error, this will return the xxx if it exists, otherwise the yyy if it exists, and so on.
//                queryBuilder.append("\t").append("BIND( COALESCE(").append(coalesceBuilderLang).append(") as ?").append(SPARQL_OBJECT_BINDING_NAME).append(" ) ").append("\n");
//            }
//            else {
//
//                //directly filter on the literal property (eg. a string)
//                //note that this is a simplified version of the COALESCE method above (for one language and one object binding)
//
//                String labelNameLang = "1l";
//                String labelNameNolang = "1n";
//
//                queryBuilder.append("\t").append("OPTIONAL {").append("\n");
//                queryBuilder.append("\t").append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getUri()).append("> ?").append(labelNameLang).append(" .\n");
//                queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.LANG, language, labelNameLang));
//                queryBuilder.append("\t").append("}").append("\n");
//
//                queryBuilder.append("\t").append("OPTIONAL {").append("\n");
//                queryBuilder.append("\t").append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getUri()).append("> ?").append(labelNameNolang).append(" .\n");
//                queryBuilder.append("\t").append("\t").append(this.buildFilter(property, onlyLiteral, FilterLang.NOLANG, language, labelNameNolang));
//                queryBuilder.append("\t").append("}").append("\n");
//
//                queryBuilder.append("\t").append("BIND( COALESCE(").append("?").append(labelNameLang).append(",?").append(labelNameNolang).append(") as ?").append(SPARQL_OBJECT_BINDING_NAME)
//                            .append(" ) ").append("\n");
//
//            }
//
//            //exclude all null values
//            queryBuilder.append("\t").append("FILTER( BOUND(").append("?").append(SPARQL_OBJECT_BINDING_NAME).append(") ) ").append("\n");
//
//            queryBuilder.append("}").append("\n");
//
//            if (caseInsensitive) {
//                //Sesame extension function that allows for case-invariant sorting (which feels more natural in filter dropdowns)
//                //see http://sesame-general.435816.n3.nabble.com/Case-insensitive-ORDER-BY-in-SPARQL-td890845.html
//                queryBuilder.append("ORDER BY ASC(fn:lower-case(str(?").append(SPARQL_OBJECT_BINDING_NAME).append(")))").append("\n");
//            }
//            else {
//                queryBuilder.append("ORDER BY ASC(?").append(SPARQL_OBJECT_BINDING_NAME).append(")").append("\n");
//            }
//
//            if (limit > 0) {
//                queryBuilder.append("LIMIT ").append(limit).append("\n");
//            }
//
//            //Logger.info(queryBuilder);
//
//            TupleQueryResult result = StorageFactory.getSparqlQueryConnection().query(queryBuilder.toString()).evaluate();
//
//            retVal = new AutoTupleRdfResult(property,
//                                            result,
//                                            SPARQL_OBJECT_BINDING_NAME,
//                                            //for resources, we return the URI as the value,
//                                            //for literals, we use the literal object
//                                            resource ? internalObjBinding : SPARQL_OBJECT_BINDING_NAME,
//                                            language);
//
//            //make sure the result iterator will get closed at the end of this request
//            R.requestContext().registerClosable(retVal);
//        }
//
//        return retVal;
//    }
//
//    //-----PRIVATE METHODS-----
//    private enum FilterLang
//    {
//        /**
//         * Only include values with a language set
//         */
//        LANG,
//
//        /**
//         * Only include values where the language is empty
//         */
//        NOLANG,
//
//        /**
//         * Include all values (specific language and empty language)
//         */
//        BOTH
//    }
//    private CharSequence buildFilter(RdfProperty property, boolean onlyLiteral, FilterLang filterLang, Locale lang, String binding)
//    {
//        //filter on language if we're dealing with a literal
//        StringBuilder retVal = new StringBuilder();
//
//        //this means the datatype is a URI (eg. a Person) so it can never have a language
//        retVal.append("FILTER(");
//
//        retVal.append("(");
//        if (filterLang == FilterLang.LANG || filterLang == FilterLang.BOTH) {
//            retVal.append("lang(?").append(binding).append(") = \"").append(lang.getLanguage()).append("\"");
//        }
//        if (filterLang == FilterLang.BOTH) {
//            retVal.append(" || ");
//        }
//        if (filterLang == FilterLang.NOLANG || filterLang == FilterLang.BOTH) {
//            retVal.append("lang(?").append(binding).append(") = \"\"");
//        }
//        retVal.append(")");
//
//        if (onlyLiteral) {
//            retVal.append(" && isLiteral(?").append(binding).append(")");
//        }
//        else {
//            //this will also add the not-literal entities (that don't have a language and don't seem to be selected by the " = ''" above) if arguments allow it
//            if (filterLang == FilterLang.NOLANG || filterLang == FilterLang.BOTH) {
//                retVal.append(" || !isLiteral(?").append(binding).append(")");
//            }
//        }
//        retVal.append(")\n");
//
//        return retVal;
//    }
//    private Locale getSearchLanguage()
//    {
//        return R.i18n().getOptimalLocale();
//    }
//
//    //-----INNER CLASSES-----
//    public static class SearchConfig
//    {
//        private Controller controller;
//        private boolean sortEnabled;
//        private Filter[] filters;
//        private Locale language;
//
//        public SearchConfig(Controller controller, boolean sortEnabled, Filter[] filters, Locale language)
//        {
//            this.controller = controller;
//            this.sortEnabled = sortEnabled;
//            this.filters = filters;
//            this.language = language;
//        }
//        public boolean isSortEnabled()
//        {
//            return sortEnabled;
//        }
//        public Filter[] getFilters()
//        {
//            return filters;
//        }
//        public RdfTupleResult<String, String> getPossibleValuesFor(Filter filter) throws IOException
//        {
//            //TODO we might want to cache this value across requests
//            return this.controller.searchAllFilterValues(this.controller.getSearchRequest().getTypeOf().getCurie(), filter.getProperty().getCurie(), false, true, 1000, this.language);
//        }
//    }
//
//    public static class Filter
//    {
//        //-----VARIABLES-----
//        /**
//         * Note: the properties of this class should match the one in box.js - buildActiveFiltersValue()
//         */
//        private URI curie;
//
//        //-----TRANSIENT VARIABLES-----
//        private RdfProperty cachedProperty;
//
//        //-----CONSTRUCTORS-----
//        public Filter()
//        {
//        }
//
//        //-----PUBLIC GETTERS/SETTERS-----
//        public URI getCurie()
//        {
//            return curie;
//        }
//        //Note: this will auto-box the string value to a URI
//        public void setCurie(URI curie)
//        {
//            this.curie = curie;
//        }
//
//        //-----PUBLIC METHODS-----
//        public RdfProperty getProperty()
//        {
//            if (this.cachedProperty == null) {
//                this.cachedProperty = RdfFactory.getProperty(this.curie);
//            }
//
//            return this.cachedProperty;
//        }
//        /**
//         * We had some issues with referrer locales being used instead of the normal locale (because that's the behavior of RdfClass.getLabel()),
//         * so we're forcing the locale to make sure the right one is used.
//         */
//        public String getPropertyLabel()
//        {
//            return this.getProperty().getLabelMessage().toString(R.i18n().getOptimalLocale());
//        }
//
//        //-----PRIVATE METHODS-----
//
//    }
}
