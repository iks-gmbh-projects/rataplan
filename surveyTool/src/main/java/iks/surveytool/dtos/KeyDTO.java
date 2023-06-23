package iks.surveytool.dtos;

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
