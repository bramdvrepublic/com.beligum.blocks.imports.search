package com.beligum.blocks.imports.search;

import com.beligum.blocks.config.RdfFactory;
import com.beligum.blocks.endpoints.RdfEndpoint;
import com.beligum.blocks.rdf.ifaces.RdfClass;
import org.apache.shiro.authz.annotation.RequiresRoles;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Set;

import static gen.com.beligum.base.core.constants.base.core.ADMIN_ROLE_NAME;

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

    //-----PROTECTED METHODS-----

    //-----PRIVATE METHODS-----

}
