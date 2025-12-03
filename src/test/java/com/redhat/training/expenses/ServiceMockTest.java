package com.redhat.training.expenses;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.startsWith;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
public class ServiceMockTest {
    
    @InjectMock
    ExpenseService mockExpenseService;

    

    @Test
    public void creatingAnExpenseReturns400OnInvalidAmountAndType(){
        // Mockito
        //     .when(mockExpenseService.meetsMinimumAmount(Mockito.anyDouble()))
        //     .thenReturn(false);

        given()
            .body(
                CrudTest.generateExpenseJson(
                    "", 
                    "Expense 1", 
                    "CASH", 
                    501)
            )
            .contentType(ContentType.JSON)
        .when()
            .post("/expenses")
        .then()
            .statusCode(400);
    }

    // @Test
    // public void creatingAnExpenseReturns200OnValidAmountAndType(){
    //     Mockito
    //         .when(mockExpenseService.meetsMinimumAmount(Mockito.anyDouble()))
    //         .thenReturn(true);

    //     given()
    //         .body(
    //             CrudTest.generateExpenseJson(
    //                 "", 
    //                 "Expense 1", 
    //                 "CASH", 
    //                 99999)
    //         )
    //         .contentType(ContentType.JSON)
    //     .when()
    //         .post("/expenses")
    //     .then()
    //         .statusCode(201);
    // }

}
