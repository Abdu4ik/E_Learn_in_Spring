package com.tafakkoor.e_learn.dto;

import com.tafakkoor.e_learn.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrammarCreateDTO {

    @NotBlank(message = "content.title.cannot.be.blank")
    @Size(message = "content.title.must.have.at.least.x.characters")
    private String title;

    @NotBlank(message = "content.author.cannot.be.blank")
    @Size(message = "content.author.must.have.at.least.x.characters")
    private String author;
    private String level;

    private ContentType contentType;

    @NotBlank(message = "content.score.cannot.be.blank")
    @Size(message = "content.score.must.have.at.least.x.characters") // todo scorega min va max berish kerak
    private int score;
    @NotNull(message = "content.document.cannot.be.null")
    private MultipartFile file;

    @NotBlank(message = "content.question.cannot.be.blank")
    @Size(message = "content.question.must.have.at.least.x.characters")
    private String question;

    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String option1;

    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String option2;

    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String option3;

    @NotBlank(message = "content.question.options.cannot.be.blank")
    private String option4;
    private String correctAnswer;
}
