package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactGroupDTO {
    private Long id;
    private String name;
    private List<Integer> contacts;
}
