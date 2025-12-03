package com.redhat.training.expenses;

import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ExpenseService {
    public final double MINIMUM_AMOUNT = 500;

    public List<Expense> list() {
        return Expense.listAll();
    }

    public Expense create(Expense expense) {
        Expense newExpense = Expense.of(expense.name, expense.paymentMethod, expense.amount);
        
        newExpense.persist();

        return newExpense;
    }

    public void delete(UUID uuid) {
        long numExpensesDeleted = Expense.delete("uuid", uuid);

        if(numExpensesDeleted == 0){
            throw new NotFoundException();
        }
    }

    public void update(Expense newExpense) {

        System.out.println("El valor interno del objeto a actualizar es *****: "+newExpense.name);
        Expense originalExpense = Expense.find("uuid", newExpense.uuid).firstResult();

        newExpense.id = originalExpense.id;

        Expense.update(newExpense);
    }

    public boolean exists(UUID uuid){
        return Expense.find("uuid", uuid).count() == 1;
    }

    public boolean meetsMinimumAmount(double amount) {

        System.out.println("+++++meetsMinimumAmount+++++++++ " + amount + " ----- " + MINIMUM_AMOUNT);
        return amount >= MINIMUM_AMOUNT;
    }
}
