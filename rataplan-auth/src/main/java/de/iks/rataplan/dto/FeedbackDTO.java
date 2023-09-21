package de.iks.rataplan.dto;

import de.iks.rataplan.domain.FeedbackCategory;
import de.iks.rataplan.exceptions.RataplanAuthException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private String title;
    private String text;
    private Byte rating;
    private FeedbackCategory category;
    
    public void assertValid() {
        if(title == null || title.isBlank()) throw new RataplanAuthException("missing title");
        if(text == null || text.isBlank()) throw new RataplanAuthException("missing text");
        if(rating == null || rating < 0 || rating > 5) throw new RataplanAuthException("missing or bad rating");
        if(category == null) throw new RataplanAuthException("missing category");
    }
}
