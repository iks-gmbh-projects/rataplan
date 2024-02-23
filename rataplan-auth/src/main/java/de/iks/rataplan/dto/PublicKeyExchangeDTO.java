package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicKeyExchangeDTO {
    private byte[] encodedKey;
    private Date creationTime;
}
