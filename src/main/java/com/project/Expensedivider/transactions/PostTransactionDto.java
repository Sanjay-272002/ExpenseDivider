package com.project.Expensedivider.transactions;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostTransactionDto {

   private String name;

  private  BigDecimal amount;

   private   List<String> touserids;

   private  String Groupid;

    private String invoice;

}
