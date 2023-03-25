package com.tafakkoor.e_learn.utils.helpers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class QuestionHelper {
    private Long questionId;
    @NotBlank(message = "content.question.cannot.be.blank")
    @Size(message = "content.question.must.have.at.least.x.characters")
    private String title;
    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String option1;
    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String option2;
    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String option3;
    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String option4;
    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String correctAnswer;
}
