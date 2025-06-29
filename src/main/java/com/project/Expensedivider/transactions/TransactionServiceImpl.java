package com.project.Expensedivider.transactions;

import com.project.Expensedivider.General.ApiResponse;
import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.Groups.GroupException;
import com.project.Expensedivider.Groups.GroupRepository;
import com.project.Expensedivider.category.Categorrepository;
import com.project.Expensedivider.category.Category;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
   private final Categorrepository categorrepository;

    @Override
    @Transactional
    public Transaction addTransaction(PostTransactionDto postTransactionDto) throws TransactionException, ExpenseException {
        System.out.println("postTransactionDTO"+postTransactionDto);
        String userId=this.userService.getAuthenticatedUserId();
        User fromuser=this.userRepository.findById(userId).orElseThrow(() -> new TransactionException("User not found"));
        List<User> toUsers=this.userRepository.findAllById(postTransactionDto.getTouserids());
        System.out.println("tousers"+toUsers);
        byte[] decodedInvoice =null;
        if(postTransactionDto.getInvoice()!=null)
            decodedInvoice=Base64.getDecoder().decode(postTransactionDto.getInvoice());
        Category category=categorrepository.findById(postTransactionDto.getCategory()).get();
        Group groupData=this.groupRepository.findById(postTransactionDto.getGroupid()).orElseThrow(() -> new TransactionException("Group not found"));

        BigDecimal split=this.expenseService.handleExpense(groupData,fromuser,toUsers,postTransactionDto.getAmount());
        var transaction = Transaction.builder().name(postTransactionDto.getName()).amount(postTransactionDto.getAmount()).fromuser(fromuser).touserdata(toUsers).category(category).invoice(decodedInvoice).group(groupData).share(split).build();
        return this.transactionRepository.save(transaction);
    }


    @SneakyThrows
    @Override
    public List<ListTransactionDateDto> listgrouptransaction(String groupId) throws TransactionException {

        Group groupData = this.groupRepository.findById(groupId)
                .orElseThrow(() -> new TransactionException("Group not found"));

        List<Transaction> transactionsData = this.transactionRepository.findByGroup(groupData);

        List<ListTransactionDateDto> resultList;
        resultList= handleGetListTransaction(transactionsData);
        return resultList;
    }
    @SneakyThrows
    public  List<ListTransactionDateDto> handleGetListTransaction(List<Transaction> transactionsData)  throws TransactionException{
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
                    .groupname(transaction.getGroup().getName())
                    .category(transaction.getCategory().getName())
                    .createddate(transaction.getCreatedAt())
                    .groupId(transaction.getGroup().getId())
                    .amount(transaction.getAmount())
                    .fromuser(fromUserDetails)
                    .tousers(toUserDetails)
                    .invoice(encodeImage(transaction.getInvoice()))
                    .build());
        }
        Map<String, List<ListTransactionDto>> groupedMap = resultList.stream()
                .collect(Collectors.groupingBy(t ->
                        new SimpleDateFormat("yyyy-MM-dd").format(t.getCreateddate())
                ));

        // Convert the map into a list of TransactionsByDate objects
        return groupedMap.entrySet().stream()
                .map(entry -> new ListTransactionDateDto(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // descending by date
                .collect(Collectors.toList());

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
        System.out.println("receivers"+ receivers);
        System.out.println("senders"+ senders);
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
    public ApiResponse getListUserTransactions(LocalDate date) throws TransactionException,UserException {
        System.out.println("listenters"+date);
        String userId=this.userService.getAuthenticatedUserId();
        User user=this.userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        List<Transaction> userTransactions;
            int month = date.getMonthValue();
            int year = date.getYear();
        userTransactions = (date == null) ? this.transactionRepository.findByFromuser(user) : this.transactionRepository.findByFromuserAndMonth(user, month, year);
        List<ListTransactionDateDto> resultList;
        resultList= handleGetListTransaction(userTransactions);
        return ApiResponse.builder().message("data listed successfully").data(resultList).success(true).build();
    }


}
