package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyDTO {
    private String algorithm;
    private byte[] encoded;
}
