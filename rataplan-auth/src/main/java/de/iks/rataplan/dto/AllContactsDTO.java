package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllContactsDTO {
    private List<ContactGroupDTO> groups;
    private List<Integer> ungrouped;
}
