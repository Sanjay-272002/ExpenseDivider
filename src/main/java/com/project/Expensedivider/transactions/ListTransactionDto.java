package com.project.Expensedivider.transactions;

import com.project.Expensedivider.user.RegisterUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListTransactionDto {
    private String name;
    private String groupname;
    private String category;
    private BigDecimal amount;
    private RegisterUserDto fromuser;
    private List<RegisterUserDto> tousers;
    private String invoice;
    private String groupId;
    private Date createddate;
}
