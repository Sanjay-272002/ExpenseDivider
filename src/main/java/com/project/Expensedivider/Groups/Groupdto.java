package com.project.Expensedivider.Groups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Groupdto {

    private String name;
    private String hostuserId;
    private List<String> userids;
}
