// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/api/CurrentAccountsApiResource.java

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
import org.apache.fineract.portfolio.currentaccount.data.CurrentAccountData;
import org.apache.fineract.portfolio.currentaccount.service.CurrentAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * REST API Controller for Current Accounts
 * This class exposes HTTP endpoints for managing current account instances
 * <p>
 * Available endpoints:
 * GET /currentaccounts - List all accounts (with optional client filter)
 * GET /currentaccounts/template - Get template for creating account
 * GET /currentaccounts/{accountId} - Get specific account
 * POST /currentaccounts - Create new account
 * PUT /currentaccounts/{accountId} - Update existing account
 * POST /currentaccounts/{accountId}?command=approve - Approve account
 * POST /currentaccounts/{accountId}?command=activate - Activate account
 * POST /currentaccounts/{accountId}?command=close - Close account
 * POST /currentaccounts/{accountId}/transactions?command=deposit - Make deposit
 * POST /currentaccounts/{accountId}/transactions?command=withdrawal - Make withdrawal
 */
@Path("/v1/currentaccounts")
@Component
@Scope("singleton")
@Tag(name = "Current Accounts", description = "Current Accounts API")
public class CurrentAccountsApiResource {

    private final PlatformSecurityContext context;
    private final CurrentAccountReadPlatformService readPlatformService;
    private final ToApiJsonSerializer<CurrentAccountData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public CurrentAccountsApiResource(final PlatformSecurityContext context,
                                      final CurrentAccountReadPlatformService readPlatformService,
                                      final ToApiJsonSerializer<CurrentAccountData> toApiJsonSerializer,
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
    @Operation(summary = "List Current Accounts",
            description = "Lists current accounts. Optional clientId query parameter limits results to accounts for that client.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String retrieveAll(@Context final UriInfo uriInfo,
                              @QueryParam("clientId") @Parameter(description = "clientId") final Long clientId) {

        this.context.authenticatedUser().validateHasReadPermission("CURRENTACCOUNT");

        final Collection<CurrentAccountData> accounts;
        if (clientId != null) {
            accounts = this.readPlatformService.retrieveAllForClient(clientId);
        } else {
            accounts = this.readPlatformService.retrieveAll();
        }

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
                .process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, accounts);
    }

    @GET
    @Path("template")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve Current Account Template",
            description = "Template for creating a current account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String retrieveTemplate(@Context final UriInfo uriInfo,
                                   @QueryParam("clientId") @Parameter(description = "clientId") final Long clientId,
                                   @QueryParam("groupId") @Parameter(description = "groupId") final Long groupId) {

        this.context.authenticatedUser().validateHasReadPermission("CURRENTACCOUNT");

        final CurrentAccountData account = this.readPlatformService.retrieveTemplate(clientId, groupId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
                .process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, account);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Submit new current account application",
            description = "Submits new current account application")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String submitCurrentAccountApplication(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .createCurrentAccount()
                .withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{accountId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve a Current Account",
            description = "Retrieves a current account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String retrieveOne(@PathParam("accountId") @Parameter(description = "accountId") final Long accountId,
                              @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission("CURRENTACCOUNT");

        final CurrentAccountData account = this.readPlatformService.retrieveOne(accountId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
                .process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, account);
    }

    @PUT
    @Path("{accountId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Modify a current account application",
            description = "Modifies a current account application")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String modifyCurrentAccountApplication(@PathParam("accountId") @Parameter(description = "accountId") final Long accountId,
                                                  final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder()
                .updateCurrentAccount(accountId)
                .withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{accountId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Approve current account application | Activate a current account | Close a current account",
            description = "Approve current account application:\n\n" +
                    "Approves current account application so long as its in 'Submitted and pending approval' state.\n\n" +
                    "Activate a current account:\n\n" +
                    "Activates current account application so long as its in 'Approved' state.\n\n" +
                    "Close a current account:\n\n" +
                    "Closes current account so long as its in 'Active' state and has zero balance.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String handleCommands(@PathParam("accountId") @Parameter(description = "accountId") final Long accountId,
                                 @QueryParam("command") @Parameter(description = "command") final String commandParam,
                                 final String apiRequestBodyAsJson) {

        String json = "";
        if (apiRequestBodyAsJson != null) {
            json = apiRequestBodyAsJson;
        }

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(json);

        CommandWrapper commandRequest = null;
        if ("approve".equals(commandParam)) {
            commandRequest = builder.approveCurrentAccountApplication(accountId).build();
        } else if ("activate".equals(commandParam)) {
            commandRequest = builder.activateCurrentAccount(accountId).build();
        } else if ("close".equals(commandParam)) {
            commandRequest = builder.closeCurrentAccount(accountId).build();
        }

        if (commandRequest == null) {
            throw new RuntimeException("Unrecognized command parameter: " + commandParam);
        }

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{accountId}/transactions")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Deposit to current account | Withdraw from current account",
            description = "Deposit to current account:\n\n" +
                    "Deposits money to current account.\n\n" +
                    "Withdraw from current account:\n\n" +
                    "Withdraws money from current account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String transaction(@PathParam("accountId") @Parameter(description = "accountId") final Long accountId,
                              @QueryParam("command") @Parameter(description = "command") final String commandParam,
                              final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandWrapper commandRequest = null;
        if ("deposit".equals(commandParam)) {
            commandRequest = builder.currentAccountDeposit(accountId).build();
        } else if ("withdrawal".equals(commandParam)) {
            commandRequest = builder.currentAccountWithdrawal(accountId).build();
        }

        if (commandRequest == null) {
            throw new RuntimeException("Unrecognized command parameter: " + commandParam);
        }

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{accountId}/transactions")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "List current account transactions",
            description = "Lists current account transactions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")})
    public String retrieveTransactions(@PathParam("accountId") @Parameter(description = "accountId") final Long accountId,
                                       @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission("CURRENTACCOUNT");

        // This would be implemented in the read service
        // For now, return empty array
        return "[]";
    }
}