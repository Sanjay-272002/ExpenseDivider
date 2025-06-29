package com.project.Expensedivider.expense;

import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.Groups.GroupException;
import com.project.Expensedivider.Groups.GroupRepository;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;
import com.project.Expensedivider.user.UserRepository;
import com.project.Expensedivider.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService{

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private  final UserService userService;
    private final UserRepository userRepository;
    @Override
    public void createExpense(Group group, List<User> users) throws ExpenseException {
        for(User userData : users){
            var expenseObj=Expense.builder().group(group).user(userData).netamount(BigDecimal.valueOf(0)).spentamount(BigDecimal.valueOf(0)).Expenseamount(BigDecimal.valueOf(0)).build();
            this.expenseRepository.save(expenseObj);
        }
    }

    @Override
    public void removeExpense(Group group,  List<User> users) throws ExpenseException {
        for(User userData : users){
            Expense expense=this.expenseRepository.findByGroupAndUser(group,userData);
            if(expense!=null){
                this.expenseRepository.delete(expense);
            }else{
                throw new ExpenseException("Expense object for user not found");
            }
        }
    }

    @Override
    public BigDecimal handleExpense(Group group, User fromuser, List<User> users, BigDecimal amount) throws ExpenseException {
        Expense payerexpense=this.expenseRepository.findByGroupAndUser(group,fromuser);
        if(payerexpense==null) throw  new ExpenseException("User doesn't belong to any groupExpense");
        int totalPeople = users.size()+1;
        BigDecimal equalSplit = amount.divide(BigDecimal.valueOf(totalPeople), 2, RoundingMode.HALF_UP);
        updateExpense(payerexpense, equalSplit, amount);
        for(User user:users){
            Expense lenduserexpense=this.expenseRepository.findByGroupAndUser(group,user);
            if(lenduserexpense==null) throw  new ExpenseException("User doesn't belong to any groupExpense");
            updateExpense(lenduserexpense, equalSplit, null);
        }
        return equalSplit;
    }

    @Override
    public BigDecimal getTotalExpense(String groupId) throws ExpenseException, GroupException {
        Group group =this.groupRepository.findById(groupId).orElseThrow(() -> new GroupException("User not found"));;
        List<Expense> expenselist=this.expenseRepository.findByGroup(group);
        return expenselist.stream()
                .map(Expense::getSpentamount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    @Override
    public UserExpensedto getUserExpense() throws ExpenseException,UserException {
        String userId=this.userService.getAuthenticatedUserId();
        User user=this.userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));

        List <Expense> userExpense= this.expenseRepository.findByUser(user);
        System.out.println(userExpense);
        BigDecimal totalspent= BigDecimal.valueOf(0);
        BigDecimal totalowe=BigDecimal.valueOf(0);
        BigDecimal totalowed = BigDecimal.valueOf(0);
        for(Expense expense: userExpense){
           totalspent= totalspent.add(expense.getSpentamount());
            if(expense.getNetamount().compareTo(BigDecimal.ZERO) > 0){
                totalowed = totalowed.add(expense.getNetamount());
            }else{
               totalowe= totalowe.add(expense.getNetamount());
            }
        }
        var userExpenseData=UserExpensedto.builder().totalSpent(totalspent).totalOwe(totalowe).totalOwed(totalowed).build();
        System.out.println(userExpenseData);
        return userExpenseData;
    }

    private void updateExpense(Expense expense, BigDecimal equalSplit, BigDecimal totalAmount) {
        expense.setExpenseamount(expense.getExpenseamount().add(equalSplit));
        if(totalAmount!=null)expense.setSpentamount(expense.getSpentamount().add(totalAmount));
        expense.setNetamount(expense.getSpentamount().subtract(expense.getExpenseamount()));
        this.expenseRepository.save(expense);
    }

    @Override
    public List<UserExpensedto> getindividualExpense(String groupId) throws ExpenseException, GroupException {
        Group group =this.groupRepository.findById(groupId).orElseThrow(() -> new GroupException("User not found"));;
        List<Expense> expensedata= this.expenseRepository.findByGroup(group);
        List<UserExpensedto> userexpensedata=new ArrayList<>();
        for(Expense data:expensedata){
            var expdata =UserExpensedto.builder().user_name(data.getUser().getName()).totalSpent(data.getSpentamount()).totalOwe(data.getNetamount().compareTo(BigDecimal.ZERO)<0?data.getNetamount(): BigDecimal.valueOf(0))
                    .totalOwed(data.getNetamount().compareTo(BigDecimal.ZERO)>0?data.getNetamount(): BigDecimal.valueOf(0)).totalExpense(data.getExpenseamount()).build();
            userexpensedata.add(expdata);
        }
        return userexpensedata;
    }


}
