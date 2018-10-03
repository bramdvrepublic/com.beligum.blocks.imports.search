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

import com.beligum.blocks.config.RdfFactory;
import com.beligum.blocks.endpoints.RdfEndpoint;
import com.beligum.blocks.rdf.ifaces.RdfClass;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Set;

import static gen.com.beligum.blocks.core.constants.blocks.core.RDF_CLASS_READ_ALL_PERM;

/**
 * Created by bram on 27/02/17.
 */
@Path("/blocks/imports/search")
public class Endpoint
{
    //-----CONSTANTS-----

    //-----VARIABLES-----

    //-----CONSTRUCTORS-----

    //-----PUBLIC METHODS-----
    @GET
    @Path("/classes/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(RDF_CLASS_READ_ALL_PERM)
    public Response getClasses() throws IOException
    {
        //this is basically the same as RdfEndpoint.getClasses(), but with the ALL_CLASSES added
        Set<RdfClass> retVal = RdfFactory.getLocalPublicClasses();
        retVal.add(RdfEndpoint.ALL_CLASSES);
        return Response.ok(retVal).build();
    }

    //-----PROTECTED METHODS-----

    //-----PRIVATE METHODS-----

}
