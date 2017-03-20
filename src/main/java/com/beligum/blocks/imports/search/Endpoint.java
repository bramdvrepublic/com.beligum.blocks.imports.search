package com.beligum.blocks.imports.search;

import com.beligum.blocks.config.RdfFactory;
import com.beligum.blocks.endpoints.RdfEndpoint;
import com.beligum.blocks.rdf.ifaces.RdfClass;
import org.apache.shiro.authz.annotation.RequiresRoles;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import static gen.com.beligum.base.core.constants.base.core.ADMIN_ROLE_NAME;
import static gen.com.beligum.blocks.imports.search.constants.blocks.imports.search.SEARCH_PARAM_PROPERTY;
import static gen.com.beligum.blocks.imports.search.constants.blocks.imports.search.SEARCH_PARAM_TYPE;

/**
 * Created by bram on 27/02/17.
 */
@Path("/blocks/imports/search")
@RequiresRoles(ADMIN_ROLE_NAME)
public class Endpoint
{
    //-----CONSTANTS-----

    //-----VARIABLES-----

    //-----CONSTRUCTORS-----

    //-----PUBLIC METHODS-----
    @GET
    @Path("/classes/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClasses() throws IOException
    {
        Set<RdfClass> retVal = RdfFactory.getLocalPublicClasses();
        retVal.add(RdfEndpoint.ALL_CLASSES);
        return Response.ok(retVal).build();
    }

    @GET
    @Path("/values/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValues(@QueryParam(SEARCH_PARAM_TYPE) URI typeCurie, @QueryParam(SEARCH_PARAM_PROPERTY) URI propertyCurie) throws IOException
    {
        return Response.ok(new Controller().searchAllFilterValues(typeCurie, propertyCurie, false, 1000)).build();
    }

    //-----PROTECTED METHODS-----

    //-----PRIVATE METHODS-----

}
