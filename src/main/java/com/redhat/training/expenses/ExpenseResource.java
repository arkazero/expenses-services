package com.redhat.training.expenses;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/expenses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExpenseResource {
    
    @Inject
    @RestClient
    FraudScoreService fraudScoreService;

    @Inject
    public ExpenseService expenseService;

    @GET
    @Operation(summary = "Retrieves the list of expenses")
    @APIResponse(responseCode = "200")
    public List<Expense> list(){
        return expenseService.list();
    }

    @POST
    @Transactional
    @Operation(summary = "Adds an expense")
    @APIResponse(
        responseCode = "201",
        headers = {
            @Header(
                name = "uuid",
                description = "ID of the created entity",
                schema = @Schema(implementation = String.class)
            ),
            @Header(
                name = "location", 
                description = "URI of the create entity",
                schema = @Schema(implementation = String.class)
            )
        },
        description = "Entity sucessfully created"    
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid entity"
    )
    public Response create(Expense expense, @Context UriInfo uriInfo){

        System.out.println("++++++++++++++ " + expense.amount);
        if (!expenseService.meetsMinimumAmount(expense.amount)){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Expense newExpense = expenseService.create(expense);

        System.out.println("++++++newExpense++++++++ " + newExpense);

        return Response.created(generateUriForExpense(newExpense, uriInfo))
                .header("uuid", newExpense.uuid)
                .build();
    }    

    @PUT
    @Transactional
    @Operation(summary = "update an expense")
    @APIResponse(
        responseCode = "200",
        description = "Entity successfully updated"
    )
    @APIResponse(
        responseCode = "404",
        description = "Entity not found"
    )
    public void update(Expense expense){

        System.out.println("El valor interno del objeto a actualizar es: "+expense.name);
        
        if(expenseService.exists(expense.uuid)){
            expenseService.update(expense);
        } else {
            throw new NotFoundException();
        }
    }

    @DELETE
    @Path("{uuid}")
    @Transactional
    @Operation(summary = "Deletes an expense")
    @APIResponse(
        responseCode = "204",
        description = "Entity successfully deleted"
    )
    @APIResponse(
        responseCode = "404",
        description = "Entity not found"
    )
    public Response delete(@PathParam("uuid") UUID uuid) {
        expenseService.delete(uuid);

        return Response.noContent().build();
    }

    @POST
    @Path("/score")
    @Operation(summary = "Provides the fraud score from an external services")
    @APIResponse(
        responseCode = "200",
        description = "Entity sucessfully scored"
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid entity"
    )
    public Response fraudScore(Expense expense){
        FraudScore fraud = fraudScoreService.getByAmount(expense.amount);

        if (fraud.score > 200){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }


    private URI generateUriForExpense(Expense expense, UriInfo uriInfo){
        System.out.println("ExpenseResource " + expense);
        return uriInfo.getAbsolutePathBuilder().path("/{uuid}").build(expense.uuid);
    }

    
}
