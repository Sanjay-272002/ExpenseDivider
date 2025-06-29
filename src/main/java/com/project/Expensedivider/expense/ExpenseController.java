package com.project.Expensedivider.expense;

import com.project.Expensedivider.Groups.GroupException;
import com.project.Expensedivider.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;
    @GetMapping("/gettotalexpense")
    public ResponseEntity<BigDecimal> gettotalExpense(@RequestParam String groupId) throws ExpenseException, GroupException {
        return ResponseEntity.ok(this.expenseService.getTotalExpense(groupId));
    }

    @GetMapping("/getindividualexpense")
    public ResponseEntity<List<UserExpensedto>> getIndividualExpense(@RequestParam String groupId) throws ExpenseException, GroupException {
        return ResponseEntity.ok(this.expenseService.getindividualExpense(groupId));
    }

    @GetMapping("/userexpensedata")
    public ResponseEntity<UserExpensedto> getUserExpense() throws ExpenseException, UserException {
        return ResponseEntity.ok(this.expenseService.getUserExpense());
    }
}
