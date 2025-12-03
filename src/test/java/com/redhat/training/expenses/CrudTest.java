package com.redhat.training.expenses;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestHTTPEndpoint(ExpenseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudTest {
    public static final String NON_EXISTING_UUID = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

    @Test
    @Order(1)
    public void initialListOfExpensesIsEmpty() {
        given()
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("$.size()", is(0));
    }

    @Test
    @Order(2)
    public void creatingAnExpenseReturns201WithHeaders() {
        given()
            .body(
                generateExpenseJson(
                    "",
                    "Expense 1", 
                    "CASH", 
                    1000
                )
            )
            .contentType("application/json")
        .when()
            .post()
        .then()
            .statusCode(201)
            .header("location", startsWith("http://"))
            .header("uuid", notNullValue());
    }

    @Test
    @Order(3)
    public void updateNonExistingExpenseReturns404(){
        given()
            .body(
                generateExpenseJson(
                    NON_EXISTING_UUID,
                    "Expense 1", 
                    "CASH", 
                    1000
                )
            )
            .contentType("application/json")
        .when()
            .put()
        .then()
            .statusCode(404);
    }

    @Test
    @Order(3)
    public void updateExistingExpenseReturns200(){
        //Getting the list of expenses
        Response expense = given()
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .body("$.size()", is(1))
                    .extract()
                    .response();
        
        // Extracting the UUID of the stored expense 
        String expenseUuid = expense.jsonPath().getString("[0].uuid");
    
        //Extracting the amount of the stored expense
        double expenseAmount = expense.jsonPath().getDouble("[0].amount");

        double newExpenseAmount = expenseAmount * 10;

        System.out.println("expenseUuid: " + expenseUuid);
        System.out.println("expenseAmount: " + expenseAmount);
        System.out.println("newExpenseAmount: " + newExpenseAmount);

        //Updating the expense
        given()
            .body(
                generateExpenseJson(
                    expenseUuid, 
                    "Expense 1", 
                    "CASH", 
                    newExpenseAmount
                )
            )
            .contentType("application/json")
        .when()
            .put()
        .then()
            .statusCode(204);

        Response updateExpense = given()
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .body("$.size()", is(1))
                    .extract()
                    .response();

        // Extracting the UUID from the latest list
        String actualExpenseUuid = updateExpense.jsonPath().getString("[0].uuid");
        
        // Extracting the amount from the lasest list
        double actualExpenseAmount = updateExpense.jsonPath().getDouble("[0].amount");

        //Comparing result

        Assertions.assertEquals(expenseUuid, actualExpenseUuid);
        Assertions.assertEquals(newExpenseAmount, actualExpenseAmount);
    }

    @Test
    @Order(4)
    public void deleteNonExisitingExpenseReturn404(){
        given()
            .pathParam("uuid", NON_EXISTING_UUID)
        .when()
            .delete("/{uuid}")
        .then()
            .statusCode(404); 
    }

    @Test
    @Order(5)
    public void deleteExistingExpenseReturn204(){

        Response expense = given()
                    .when()
                        .get()
                    .then()
                        .statusCode(200)
                        .body("$.size()", is(1))
                        .extract()
                        .response();

        String expenseUuid = expense.jsonPath().getString("[0].uuid");

        given()
            .pathParam("uuid", expenseUuid)
        .when()
            .delete("/{uuid}")
        .then()
            .statusCode(204);
    }


    public static String generateExpenseJson (String uuid, String expeseName, String paymentType, double amount){
        
        System.out.println("El nombre es : ******* " +"{"
                + (uuid.isEmpty() ? "" : "\"uuid\":\"" + uuid + "\", ")
                + "\"name\":\"" + expeseName + "\","
                + "\"paymentType\":\"" + paymentType + "\","
                + "\"amount\":" + amount + "}");

        return "{"
                + (uuid.isEmpty() ? "" : "\"uuid\":\"" + uuid + "\", ")
                + "\"name\":\"" + expeseName + "\","
                + "\"paymentType\":\"" + paymentType + "\","
                + "\"amount\":" + amount + "}";
    }
    
}
