package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PublicKeyExchangeDTO {
    private byte[] encodedKey;
    private Date creationTime;
}
