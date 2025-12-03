package com.redhat.training.expenses;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PanacheMocks {
    
    @BeforeAll
    public static void setup(){
        PanacheMock.mock(Expense.class);
    }

    @Test
    public void listOfExpensesReturnsAnEmptyList(){
        Mockito
            .when(Expense.listAll())
            .thenReturn(Collections.emptyList());

        given()
        .when()
            .get("/expenses")
        .then()
            .statusCode(200)
            .body("$.size()", is(0));
    }
}
