/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.entityaccess.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.entityaccess.data.FineractEntityRelationData;
import org.apache.fineract.infrastructure.entityaccess.data.FineractEntityToEntityMappingData;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessReadService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.stereotype.Component;

@Path("/v1/entitytoentitymapping")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Component
@Tag(name = "Fineract Entity", description = "")
@RequiredArgsConstructor
public class FineractEntityApiResource {

    private final PlatformSecurityContext context;
    private final FineractEntityAccessReadService readPlatformService;
    private final DefaultToApiJsonSerializer<FineractEntityRelationData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<FineractEntityToEntityMappingData> toApiJsonSerializerOfficeToLoanProducts;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(FineractEntityApiResourceConstants.FINERACT_ENTITY_RESOURCE_NAME);

        final Collection<FineractEntityRelationData> entityMappings = this.readPlatformService.retrieveAllSupportedMappingTypes();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, entityMappings, FineractEntityApiResourceConstants.RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("/{mapId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("mapId") final Long mapId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(FineractEntityApiResourceConstants.FINERACT_ENTITY_RESOURCE_NAME);

        final Collection<FineractEntityToEntityMappingData> entityToEntityMappings = this.readPlatformService.retrieveOneMapping(mapId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerOfficeToLoanProducts.serialize(settings, entityToEntityMappings,
                FineractEntityApiResourceConstants.FETCH_ENTITY_TO_ENTITY_MAPPINGS);
    }

    @GET
    @Path("/{mapId}/{fromId}/{toId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getEntityToEntityMappings(@PathParam("mapId") final Long mapId, @PathParam("fromId") final Long fromId,
            @PathParam("toId") final Long toId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(FineractEntityApiResourceConstants.FINERACT_ENTITY_RESOURCE_NAME);

        final Collection<FineractEntityToEntityMappingData> entityToEntityMappings = this.readPlatformService
                .retrieveEntityToEntityMappings(mapId, fromId, toId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerOfficeToLoanProducts.serialize(settings, entityToEntityMappings,
                FineractEntityApiResourceConstants.FETCH_ENTITY_TO_ENTITY_MAPPINGS);
    }

    @POST
    @Path("/{relId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createMap(@PathParam("relId") final Long relId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createMap(relId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);

    }

    @PUT
    @Path("/{mapId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateMap(@PathParam("mapId") final Long mapId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateMap(mapId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);

    }

    @DELETE
    @Path("{mapId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("mapId") final Long mapId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteMap(mapId) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}
