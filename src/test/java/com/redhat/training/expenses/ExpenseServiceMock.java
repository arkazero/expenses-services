package com.redhat.training.expenses;

import java.util.UUID;

import io.quarkus.test.Mock;

@Mock
public class ExpenseServiceMock extends ExpenseService{
    @Override
    public boolean exists(UUID uuid) {
        return !uuid.equals(UUID.fromString(CrudTest.NON_EXISTING_UUID));
    }    
}
