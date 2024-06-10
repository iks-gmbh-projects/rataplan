package iks.surveytool.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractDTO {
    private Long id;
    
    public abstract void trimAndNull();
    
    protected static String trimAndNull(String s) {
        if(s == null || s.isBlank()) return null;
        return s.trim();
    }
    
    public abstract void valid() throws DTOValidationException;
}