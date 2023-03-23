package com.tafakkoor.e_learn.domain;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class Quiz extends Auditable {
    private String questionId;
    private String optionId;
    private String contentId;
    private String userId;

}
