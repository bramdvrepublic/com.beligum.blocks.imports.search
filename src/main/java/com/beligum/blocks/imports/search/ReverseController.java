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

/**
 * Created by wouter on 14/09/15.
 */
public class ReverseController extends Controller
{
//    //-----CONSTANTS-----
//    public enum CacheKeys implements CacheKey
//    {
//        SEARCH_REVERSE_REQUEST,
//        SEARCH_REVERSE_RESULT,
//    }
//
//    //-----VARIABLES-----
//
//    //-----CONSTRUCTORS-----
//
//    //-----PUBLIC METHODS-----
//    @Override
//    public IndexSearchRequest getSearchRequest()
//    {
//        if (!R.requestManager().getCurrentRequest().getRequestCache().containsKey(SEARCH_REVERSE_REQUEST)) {
//            R.requestManager().getCurrentRequest().getRequestCache().put(SEARCH_REVERSE_REQUEST, new IndexSearchRequest());
//        }
//
//        return R.requestManager().getCurrentRequest().getRequestCache().get(SEARCH_REVERSE_REQUEST);
//    }
//    @Override
//    public IndexSearchResult getSearchResult()
//    {
//        if (!R.requestManager().getCurrentRequest().getRequestCache().containsKey(SEARCH_REVERSE_RESULT)) {
//            try {
//                IndexSearchRequest searchRequest = this.getSearchRequest();
//
//                //set some defaults if still empty..
//                if (searchRequest.getSortDescending() == null) {
//                    searchRequest.setSortDescending(false);
//                }
//                if (searchRequest.getPageIndex() == null) {
//                    searchRequest.setPageIndex(FIRST_PAGE_INDEX);
//                }
//                if (searchRequest.getPageSize() == null) {
//                    //use a larger default for the reverse search
//                    searchRequest.setPageSize(MAX_PAGE_SIZE);
//                }
//                if (searchRequest.getFormat() == null) {
//                    searchRequest.setFormat(SEARCH_RESULTS_FORMAT_LIST);
//                }
//
//                PageIndexConnection pageIndexConnection = StorageFactory.getJsonIndexer().connect(null);
//
//                //let's not return nulls, so we can always use .size() and so on
//                IndexSearchResult searchResult = new SimpleIndexSearchResult(new ArrayList<>());
//
//                //first, look up the resource URI for the current page (because that's the one used in RDF-indexation)
//                //note: this will return null on a newly created page
//                PageIndexEntry indexedPage = pageIndexConnection.get(R.requestManager().getCurrentRequest().getRequestContext().getUriInfo().getRequestUri());
//                if (indexedPage != null) {
//                    org.apache.lucene.search.BooleanQuery pageQuery = new org.apache.lucene.search.BooleanQuery();
//
//                    //Note: the URIs are indexed relatively (as opposed to the SPARQL endpoint)
//                    pageQuery.add(new TermQuery(new Term(LucenePageIndexer.CUSTOM_FIELD_ALL_VERBATIM, indexedPage.getResource())), BooleanClause.Occur.FILTER);
//
//                    //makes sense not to return all languages this resource appears in (or we'll have a lot of doubles)
//                    Locale locale = R.i18n().getOptimalLocale();
//                    pageQuery.add(new TermQuery(new Term(PageIndexEntry.language.name(), locale.getLanguage())), BooleanClause.Occur.FILTER);
//
//                    searchResult = StorageFactory.getJsonQueryConnection().search(pageQuery, null, false, searchRequest.getPageSize(), searchRequest.getPageIndex());
//                }
//
//                R.requestManager().getCurrentRequest().getRequestCache().put(SEARCH_REVERSE_RESULT, searchResult);
//            }
//            catch (Exception e) {
//                Logger.error("Error while executing reverse search query", e);
//            }
//        }
//
//        return R.requestManager().getCurrentRequest().getRequestCache().get(SEARCH_REVERSE_RESULT);
//    }
//
//    //-----PROTECTED METHODS-----
//
//    //-----PRIVATE METHODS-----

}
