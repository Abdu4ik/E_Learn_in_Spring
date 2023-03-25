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
public class ContentUpdateDTO {
    @NotBlank(message = "content.title.cannot.be.blank")
    @Size(message = "content.title.must.have.at.least.x.characters")
    private String title;

    @NotBlank(message = "content.author.cannot.be.blank")
    @Size(message = "content.author.must.have.at.least.x.characters")
    private String author;
    private String level;

    private ContentType contentType;

    @NotBlank(message = "content.score.cannot.be.blank")
    @Size(message = "content.score.must.have.at.least.x.characters")
    private int score;
    @NotNull(message = "content.document.cannot.be.null")
    private MultipartFile file;
}
