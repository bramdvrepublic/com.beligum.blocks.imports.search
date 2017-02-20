package com.beligum.blocks.imports.search;

import com.beligum.base.cache.CacheKey;
import com.beligum.base.server.R;
import com.beligum.base.utils.Logger;
import com.beligum.blocks.config.StorageFactory;
import com.beligum.blocks.filesystem.index.LucenePageIndexer;
import com.beligum.blocks.filesystem.index.entries.pages.IndexSearchRequest;
import com.beligum.blocks.filesystem.index.entries.pages.IndexSearchResult;
import com.beligum.blocks.filesystem.index.entries.pages.PageIndexEntry;
import com.beligum.blocks.filesystem.index.ifaces.PageIndexConnection;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;

import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

import static com.beligum.blocks.imports.search.ReverseController.CacheKeys.MOT_SEARCH_REVERSE_REQUEST;
import static com.beligum.blocks.imports.search.ReverseController.CacheKeys.MOT_SEARCH_REVERSE_RESULT;
import static gen.com.beligum.blocks.core.constants.blocks.core.SEARCH_RESULTS_FORMAT_LIST;

/**
 * Created by wouter on 14/09/15.
 */
public class ReverseController extends Controller
{
    //-----CONSTANTS-----
    public enum CacheKeys implements CacheKey
    {
        MOT_SEARCH_REVERSE_REQUEST,
        MOT_SEARCH_REVERSE_RESULT,
    }

    //-----VARIABLES-----

    //-----CONSTRUCTORS-----

    //-----PUBLIC METHODS-----
    @Override
    public IndexSearchRequest getSearchRequest()
    {
        if (!R.cacheManager().getRequestCache().containsKey(MOT_SEARCH_REVERSE_REQUEST)) {
            R.cacheManager().getRequestCache().put(MOT_SEARCH_REVERSE_REQUEST, new IndexSearchRequest());
        }

        return (IndexSearchRequest) R.cacheManager().getRequestCache().get(MOT_SEARCH_REVERSE_REQUEST);
    }
    @Override
    public IndexSearchResult getSearchResult()
    {
        if (!R.cacheManager().getRequestCache().containsKey(MOT_SEARCH_REVERSE_RESULT)) {
            try {
                IndexSearchRequest searchRequest = this.getSearchRequest();

                //set some defaults if still empty..
                if (searchRequest.getPageIndex() == null) {
                    searchRequest.setPageIndex(FIRST_PAGE_INDEX);
                }
                if (searchRequest.getPageSize() == null) {
                    //use a larger default for the reverse search
                    searchRequest.setPageSize(MAX_PAGE_SIZE);
                }
                if (searchRequest.getFormat() == null) {
                    searchRequest.setFormat(SEARCH_RESULTS_FORMAT_LIST);
                }

                //first, look up the resource URI for the current page (because that's the one used in RDF-indexation)
                URI requestedUri = R.requestContext().getJaxRsRequest().getUriInfo().getRequestUri();
                PageIndexConnection pageIndexConnection = StorageFactory.getMainPageIndexer().connect();

                //let's not return nulls, so we can always use .size() and so on
                IndexSearchResult searchResult = new IndexSearchResult(new ArrayList<>());

                //note: this will return null on a newly created page
                PageIndexEntry indexedPage = pageIndexConnection.get(requestedUri);
                if (indexedPage!=null) {
                    org.apache.lucene.search.BooleanQuery pageQuery = new org.apache.lucene.search.BooleanQuery();

                    //Note: the URIs are indexed relatively (as opposed to the SPARQL endpoint)
                    pageQuery.add(new TermQuery(new Term(LucenePageIndexer.CUSTOM_FIELD_ALL, indexedPage.getResource())), BooleanClause.Occur.FILTER);

                    //makes sense not to return all languages this resource appears in (or we'll have a lot of doubles)
                    Locale locale = R.i18n().getOptimalLocale();
                    pageQuery.add(new TermQuery(new Term(PageIndexEntry.Field.language.name(), locale.getLanguage())), BooleanClause.Occur.FILTER);

                    searchResult = StorageFactory.getMainPageQueryConnection().search(pageQuery, null, false, searchRequest.getPageSize(), searchRequest.getPageIndex());
                }

                R.cacheManager().getRequestCache().put(MOT_SEARCH_REVERSE_RESULT, searchResult);
            }
            catch (Exception e) {
                Logger.error("Error while executing reverse search query", e);
            }
        }

        return (IndexSearchResult) R.cacheManager().getRequestCache().get(MOT_SEARCH_REVERSE_RESULT);
    }

    //-----PROTECTED METHODS-----

    //-----PRIVATE METHODS-----

}
