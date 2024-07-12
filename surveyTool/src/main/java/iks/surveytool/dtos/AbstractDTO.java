package iks.surveytool.dtos;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractDTO {
    private Long id;
    
    public void resetId() {
        this.id = null;
    }
    
    public abstract void trimAndNull();
    
    static String trimAndNull(String s) {
        if(s == null || s.isBlank()) return null;
        return s.trim();
    }
    
    public abstract void valid() throws DTOValidationException;
}