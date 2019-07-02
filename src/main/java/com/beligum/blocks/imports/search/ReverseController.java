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
import com.beligum.blocks.config.StorageFactory;
import com.beligum.blocks.index.entries.SimpleIndexSearchResult;
import com.beligum.blocks.index.ifaces.*;
import gen.com.beligum.blocks.imports.search.constants.blocks.imports.search;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static gen.com.beligum.blocks.imports.search.constants.blocks.imports.search.SEARCH_RESULTS_FORMAT_LIST;

/**
 * Created by wouter on 14/09/15.
 */
public class ReverseController extends Controller
{
    //-----CONSTANTS-----
    public enum CacheKeys implements CacheKey
    {
        SEARCH_REVERSE_REQUEST,
        SEARCH_REVERSE_RESULT,
    }

    //-----VARIABLES-----

    //-----CONSTRUCTORS-----

    //-----PUBLIC METHODS-----
    @Override
    public SearchRequestDAO getSearchRequest()
    {
        return R.requestManager().getCurrentRequest().getRequestCache().getAndInitIfAbsent(CacheKeys.SEARCH_REVERSE_REQUEST, new CacheFunction<CacheKey, SearchRequestDAO>()
        {
            @Override
            public SearchRequestDAO apply(CacheKey cacheKey) throws IOException
            {
                return new SearchRequestDAO();
            }
        });
    }
    @Override
    public IndexSearchResult getSearchResult()
    {
        return R.requestManager().getCurrentRequest().getRequestCache().getAndInitIfAbsent(CacheKeys.SEARCH_REVERSE_RESULT, new CacheFunction<CacheKey, IndexSearchResult>()
        {
            @Override
            public IndexSearchResult apply(CacheKey cacheKey) throws IOException
            {
                SearchRequestDAO searchParams = ReverseController.this.getSearchRequest();

                //set some defaults if still empty..
                if (searchParams.getSortDescending() == null) {
                    searchParams.setSortDescending(false);
                }
                if (searchParams.getPageIndex() == null) {
                    searchParams.setPageIndex(FIRST_PAGE_INDEX);
                }
                if (searchParams.getPageSize() == null) {
                    //use a larger default for the reverse search
                    searchParams.setPageSize(Integer.parseInt(search.SEARCH_MAX_PAGE_SIZE));
                }
                if (searchParams.getFormat() == null) {
                    searchParams.setFormat(SEARCH_RESULTS_FORMAT_LIST);
                }

                IndexConnection indexConnection = StorageFactory.getJsonIndexer().connect();

                //let's not return nulls, so we can always use .size() and so on
                IndexSearchResult retVal = new SimpleIndexSearchResult(new ArrayList<>());

                //first, look up the resource URI for the current page (because that's the one used in RDF-indexation)
                //note: this will return null on a newly created page
                ResourceIndexEntry indexedPage = indexConnection.get(R.requestManager().getCurrentRequest().getRequestContext().getUriInfo().getRequestUri());
                if (indexedPage != null) {

                    IndexSearchRequest searchRequest = IndexSearchRequest.createFor(indexConnection);

                    //Note: the URIs are indexed relatively (as opposed to the SPARQL endpoint)
                    //Do a full  text search on everything
                    searchRequest.all(indexedPage.getResource(), IndexSearchRequest.FilterBoolean.AND);
                    //only seach for non-sub objects and non-proxy objects
                    searchRequest.filter(PageIndexEntry.resourceTypeField, PageIndexEntry.resourceTypeField.DEFAULT_VALUE, IndexSearchRequest.FilterBoolean.AND);
                    //Exlude self
                    searchRequest.filter(PageIndexEntry.resourceField, indexedPage.getResource(), IndexSearchRequest.FilterBoolean.NOT);

                    //makes sense not to return all languages this resource appears in (or we'll have a lot of doubles)
                    searchRequest.language(ReverseController.this.getSearchLanguage());

                    searchRequest.sort(searchParams.getSortField(), !searchParams.getSortDescending());
                    searchRequest.pageSize(searchParams.getPageSize());
                    searchRequest.pageOffset(searchParams.getPageIndex());

                    retVal = indexConnection.search(searchRequest);
                }

                return retVal;
            }
        });
    }

    //-----PROTECTED METHODS-----

    //-----PRIVATE METHODS-----

}
