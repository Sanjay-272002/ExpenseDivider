package com.project.Expensedivider.transactions;

import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping("/add")
    ResponseEntity<Transaction> postTransaction(@RequestBody  PostTransactionDto postTransactionDto) throws TransactionException, ExpenseException {
        return ResponseEntity.ok(this.transactionService.addTransaction(postTransactionDto));
    }

    @GetMapping("/listtransaction")
    ResponseEntity<List<ListTransactionDto>> listGroupTransaction(@RequestParam String groupId) throws TransactionException{
        return ResponseEntity.ok(this.transactionService.listgrouptransaction(groupId));
    }

    @GetMapping("/getduelist")
    ResponseEntity<List<DueDataDto>> getDueList(@RequestParam String groupId) throws TransactionException{
        return ResponseEntity.ok(this.transactionService.getDueList(groupId));
    }
    @GetMapping("/listusertransaction")
    ResponseEntity<List<ListTransactionDto>> listUsertransactions() throws TransactionException, UserException {
        return ResponseEntity.ok(this.transactionService.getListUserTransactions());
    }


}
