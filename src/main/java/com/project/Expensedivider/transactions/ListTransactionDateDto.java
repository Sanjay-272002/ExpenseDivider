package com.project.Expensedivider.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ListTransactionDateDto {
  private String date;
  List<ListTransactionDto> listTransactionDtoList;

}
