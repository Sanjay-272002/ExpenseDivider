package com.project.Expensedivider.transactions;

import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.Groups.GroupException;
import com.project.Expensedivider.Groups.GroupRepository;
import com.project.Expensedivider.expense.Expense;
import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.expense.ExpenseRepository;
import com.project.Expensedivider.expense.ExpenseService;
import com.project.Expensedivider.user.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
   private final UserService userService;
   private final UserRepository userRepository;
   private final TransactionRepository transactionRepository;
   private final ExpenseRepository expenseRepository;
   private final GroupRepository groupRepository;
   private final ExpenseService expenseService;


    @Override
    @Transactional
    public Transaction addTransaction(PostTransactionDto postTransactionDto) throws TransactionException, ExpenseException {
        String userId=this.userService.getAuthenticatedUserId();
        User fromuser=this.userRepository.findById(userId).orElseThrow(() -> new TransactionException("User not found"));
        List<User> toUsers=this.userRepository.findAllById(postTransactionDto.getTouserids());
        byte[] decodedInvoice =null;
        if(postTransactionDto.getInvoice()!=null)
            decodedInvoice=Base64.getDecoder().decode(postTransactionDto.getInvoice());
        Group groupData=this.groupRepository.findById(postTransactionDto.getGroupid()).orElseThrow(() -> new TransactionException("Group not found"));
        var transaction = Transaction.builder().name(postTransactionDto.getName()).amount(postTransactionDto.getAmount()).fromuser(fromuser).touserdata(toUsers).invoice(decodedInvoice).group(groupData).build();
        this.expenseService.handleExpense(groupData,fromuser,toUsers,postTransactionDto.getAmount());
        return this.transactionRepository.save(transaction);
    }


    @SneakyThrows
    @Override
    public List<ListTransactionDto> listgrouptransaction(String groupId) throws TransactionException {

        Group groupData = this.groupRepository.findById(groupId)
                .orElseThrow(() -> new TransactionException("Group not found"));

        List<Transaction> transactionsData = this.transactionRepository.findByGroup(groupData);

        List<ListTransactionDto> resultList;
        resultList= handleGetListTransaction(transactionsData);
        return resultList;
    }
    @SneakyThrows
    public List<ListTransactionDto> handleGetListTransaction(List<Transaction> transactionsData)  throws TransactionException{
        List<ListTransactionDto> resultList =new ArrayList<>();
        for (Transaction transaction : transactionsData) {

            User fromUser = this.userRepository.findById(transaction.getFromuser().getId())
                    .orElseThrow(() -> new TransactionException("User not found"));

            RegisterUserDto fromUserDetails = convertToRegisterUserDto(fromUser);

            List<User> toUsers = this.userRepository.findAllById(
                    transaction.getTouserdata().stream()
                            .map(User::getId)
                            .toList()
            );

            List<RegisterUserDto> toUserDetails = toUsers.stream()
                    .map(user -> {
                        try {
                            return convertToRegisterUserDto(user);  // Apply transformation
                        } catch (TransactionException e) {
                            throw new RuntimeException("Error converting user", e);  // Rethrow as RuntimeException
                        }
                    })
                    .collect(Collectors.toList());
            resultList.add(ListTransactionDto.builder()
                    .name(transaction.getName())
                    .groupId(transaction.getGroup().getId())
                    .amount(transaction.getAmount())
                    .fromuser(fromUserDetails)
                    .tousers(toUserDetails)
                    .invoice(encodeImage(transaction.getInvoice()))
                    .build());
        }
        return resultList;
    }
    @Override
    public  RegisterUserDto convertToRegisterUserDto(User user) throws TransactionException{
        return RegisterUserDto.builder()
                .name(user.getName())
                .profileImage(encodeImage(user.getProfileImage()))
                .build();
    }
    private String encodeImage(byte[] imageData) {
        return (imageData != null) ? Base64.getEncoder().encodeToString(imageData) : null;
    }
    @Override
    public List<DueDataDto> getDueList(String groupId) throws TransactionException {
        Group group=this.groupRepository.findById(groupId).orElseThrow(() -> new TransactionException("Group not found"));
        List<Expense> expenseUsers=this.expenseRepository.findByGroup(group);
        List<Expense> receivers = new ArrayList<>();
        List<Expense> senders = new ArrayList<>();

        for (Expense expenseUsr: expenseUsers) {
            if ((expenseUsr.getNetamount().compareTo(BigDecimal.ZERO) > 0)){
                receivers.add(expenseUsr);
            } else if ((expenseUsr.getNetamount().compareTo(BigDecimal.ZERO) < 0)) {
                senders.add(expenseUsr);
            }
        }
        receivers.sort((e1, e2) -> e2.getNetamount().compareTo(e1.getNetamount()));
        senders.sort(Comparator.comparing(Expense::getNetamount));
        List<DueDataDto> duelist =new ArrayList<>();
        // Balancing process
        while (!receivers.isEmpty()) {
            Expense sender = senders.get(0);
            Expense  receiver = receivers.get(0);
            BigDecimal senderAmount = sender.getNetamount();
            BigDecimal receiverAmount = receiver.getNetamount();
            BigDecimal transferAmount = senderAmount.abs().min(receiverAmount);
            RegisterUserDto payerData = RegisterUserDto.builder().userid(sender.getUser().getId()).name(sender.getUser().getName()).profileImage(encodeImage(sender.getUser().getProfileImage())).build();
            RegisterUserDto receiverData= RegisterUserDto.builder().userid(receiver.getUser().getId()).name(receiver.getUser().getName()).profileImage(encodeImage(receiver.getUser().getProfileImage())).build();
            var dueDataDto=DueDataDto.builder().fromuser(payerData).touser(receiverData).amount(transferAmount).build();
            duelist.add(dueDataDto);
            receiver.setNetamount(receiverAmount.subtract(transferAmount));
            sender.setNetamount(senderAmount.add(transferAmount));
            if (receiver.getNetamount().compareTo(BigDecimal.ZERO) == 0) {
                receivers.remove(0);
            }
            if (sender.getNetamount().compareTo(BigDecimal.ZERO) == 0) {
                senders.remove(0);
            }
        }
        return duelist;
    }

    @Override
    public List<ListTransactionDto> getListUserTransactions() throws TransactionException,UserException {
        String userId=this.userService.getAuthenticatedUserId();
        User user=this.userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        List<Transaction> userTransactions=this.transactionRepository.findByFromuser(user);
        List<ListTransactionDto> resultList;
        resultList= handleGetListTransaction(userTransactions);
        return resultList;
    }


}
