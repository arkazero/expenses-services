package com.redhat.training.expenses;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;

@QuarkusTest
public class SpyTest {
    @InjectSpy
    ExpenseService expenseService;

    @Test
    public void listOfExpensesCallsExpensesList(){
        given()
        .when()
            .get("/expenses")
        .then()
            .statusCode(200)
            .body("$.size()", is(0));

        Mockito.verify(expenseService, Mockito.times(1)).list();
    }

}
