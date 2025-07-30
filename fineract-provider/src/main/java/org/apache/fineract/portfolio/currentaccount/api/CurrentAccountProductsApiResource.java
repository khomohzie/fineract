// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/api/CurrentAccountProductsApiResource.java

package org.apache.fineract.portfolio.currentaccount.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.currentaccount.data.CurrentAccountProductData;
import org.apache.fineract.portfolio.currentaccount.service.CurrentAccountProductReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * REST API Controller for Current Account Products
 * This class exposes HTTP endpoints for managing current account products
 * <p>
 * Available endpoints:
 * GET /currentaccountproducts - List all products
 * GET /currentaccountproducts/template - Get template for creating product
 * GET /currentaccountproducts/{productId} - Get specific product
 * POST /currentaccountproducts - Create new product
 * PUT /currentaccountproducts/{productId} - Update existing product
 * DELETE /currentaccountproducts/{productId} - Delete product
 */
@Path("/v1/currentaccountproducts")
@Component
@Scope("singleton")
@Tag(name = "Current Account Products", description = "Current Account Products API")
public class CurrentAccountProductsApiResource {

    private final PlatformSecurityContext context;
    private final CurrentAccountProductReadPlatformService readPlatformService;
    private final ToApiJsonSerializer<CurrentAccountProductData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public CurrentAccountProductsApiResource(final PlatformSecurityContext context,
                                             final CurrentAccountProductReadPlatformService readPlatformService,
                                             final ToApiJsonSerializer<CurrentAccountProductData> toApiJsonSerializer,
                                             final ApiRequestParameterHelper apiRequestParameterHelper,
                                             final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve all Current Account Products", description = "Lists all current account products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission("CURRENTACCOUNTPRODUCT");

        final Collection<CurrentAccountProductData> products = this.readPlatformService.retrieveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
                .process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, products);
    }

    @GET
    @Path("template")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve Current Account Product Template",
            description = "This is a convenience resource. It can be useful when building maintenance user interface screens for current account products. The template data returned consists of any or all of: Field Defaults, Allowed Value Lists")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission("CURRENTACCOUNTPRODUCT");

        final CurrentAccountProductData product = this.readPlatformService.retrieveTemplate();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
                .process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, product);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Create a Current Account Product",
            description = "Creates a new current account product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String createCurrentAccountProduct(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .createCurrentAccountProduct()
                .withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{productId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve a Current Account Product",
            description = "Retrieves a current account product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String retrieveOne(@PathParam("productId") @Parameter(description = "productId") final Long productId,
                              @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission("CURRENTACCOUNTPRODUCT");

        final CurrentAccountProductData product = this.readPlatformService.retrieveOne(productId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
                .process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, product);
    }

    @PUT
    @Path("{productId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Update a Current Account Product",
            description = "Updates a current account product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String updateCurrentAccountProduct(@PathParam("productId") @Parameter(description = "productId") final Long productId,
                                              final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .updateCurrentAccountProduct(productId)
                .withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{productId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Delete a Current Account Product",
            description = "Deletes a current account product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String deleteCurrentAccountProduct(@PathParam("productId") @Parameter(description = "productId") final Long productId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .deleteCurrentAccountProduct(productId)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}