package com.beligum.blocks.imports.search;

import com.beligum.base.server.R;
import com.beligum.base.utils.Logger;
import com.beligum.blocks.config.RdfFactory;
import com.beligum.blocks.config.Settings;
import com.beligum.blocks.config.StorageFactory;
import com.beligum.blocks.endpoints.RdfEndpoint;
import com.beligum.blocks.filesystem.index.ifaces.SparqlQueryConnection;
import com.beligum.blocks.rdf.ifaces.RdfClass;
import com.beligum.blocks.rdf.ifaces.RdfProperty;
import com.beligum.blocks.rdf.ontology.factories.Classes;
import com.beligum.blocks.rdf.ontology.factories.Terms;
import com.beligum.blocks.rdf.ontology.vocabularies.XSD;
import com.beligum.blocks.security.Permissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.sail.lucene.LuceneSailSchema;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import static com.beligum.blocks.filesystem.index.SesamePageIndexerConnection.SPARQL_OBJECT_BINDING_NAME;
import static com.beligum.blocks.filesystem.index.SesamePageIndexerConnection.SPARQL_SUBJECT_BINDING_NAME;
import static gen.com.beligum.blocks.core.constants.blocks.core.RDF_RES_TYPE_CURIE_PARAM;

/**
 * Created by bram on 27/02/17.
 */
@Path("/blocks/imports/search")
@RequiresRoles(Permissions.ADMIN_ROLE_NAME)
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
        Set<RdfClass> retVal = RdfFactory.getClasses();
        retVal.add(RdfEndpoint.ALL_CLASSES);
        return Response.ok(retVal).build();
    }

    @GET
    @Path("/values/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValues(@QueryParam(RDF_RES_TYPE_CURIE_PARAM) URI resourceTypeCurie, /*@QueryParam(RDF_RES_TYPE_CURIE_PARAM)*/ URI resourcePropertyCurie) throws IOException
    {
        if (resourceTypeCurie == null) {
            resourceTypeCurie = Classes.Page.getCurieName();
        }

        if (resourcePropertyCurie == null) {
            resourcePropertyCurie = Terms.image.getCurieName();
        }

        RdfClass type = RdfFactory.getClassForResourceType(resourceTypeCurie);
        if (type == null) {
            Logger.warn("Encountered unknown resource type; " + resourceTypeCurie);
        }

        RdfProperty property = (RdfProperty) RdfFactory.getClassForResourceType(resourcePropertyCurie);
        if (property == null) {
            Logger.warn("Encountered unknown resource property; " + resourcePropertyCurie);
        }

        Locale lang = R.i18n().getOptimalLocale();
        boolean onlyLiteral = false;

        StringBuilder queryBuilder = new StringBuilder();
        final String searchPrefix = "search";
        queryBuilder.append("PREFIX ").append(Settings.instance().getRdfOntologyPrefix()).append(": <").append(Settings.instance().getRdfOntologyUri()).append("> \n");
        queryBuilder.append("PREFIX ").append(searchPrefix).append(": <").append(LuceneSailSchema.NAMESPACE).append("> \n");
        queryBuilder.append("\n");
        queryBuilder.append("SELECT DISTINCT")/*.append(" ?").append(SPARQL_SUBJECT_BINDING_NAME).append(" ?").append(SPARQL_PREDICATE_BINDING_NAME)*/.append(" ?").append(SPARQL_OBJECT_BINDING_NAME)
                    .append(" WHERE {\n");
        //filter on class
        queryBuilder.append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" a <").append(type.getFullName().toString()).append("> . \n");
        //triple binding + filter on property
        queryBuilder.append("\t").append("?").append(SPARQL_SUBJECT_BINDING_NAME).append(" <").append(property.getFullName().toString()).append("> ?").append(SPARQL_OBJECT_BINDING_NAME)
                    .append(" .\n");

        //filter on language if we're dealing with a literal
        if (!property.getDataType().equals(XSD.ANY_URI)) {
            queryBuilder.append("\tFILTER(");
            queryBuilder.append("isLiteral(?").append(SPARQL_OBJECT_BINDING_NAME).append(")");
            queryBuilder.append(" && ").append("(lang(?").append(SPARQL_OBJECT_BINDING_NAME).append(") = \"").append(lang.getLanguage()).append("\" || lang(?").append(SPARQL_OBJECT_BINDING_NAME).append(") = \"\")");
            queryBuilder.append(")\n");
        }

        queryBuilder.append("}\n");

        Set<String> values = new TreeSet<>();
        SparqlQueryConnection sparqlQueryConnection = StorageFactory.getTriplestoreQueryConnection();
        TupleQuery query = sparqlQueryConnection.query(queryBuilder.toString());
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                BindingSet triple = result.next();
                values.add(triple.getValue(SPARQL_OBJECT_BINDING_NAME).stringValue());
            }
        }

        return Response.ok(values).build();
    }

    //-----PROTECTED METHODS-----

    //-----PRIVATE METHODS-----

}
