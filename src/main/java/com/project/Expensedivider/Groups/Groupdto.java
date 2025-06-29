package com.project.Expensedivider.Groups;

import com.project.Expensedivider.category.Category;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private List<String> friends;
    private String category;
    private Typeenum type;
}
