package com.tafakkoor.e_learn.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;



@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
public class DocumentCreateDTO  {

    public DocumentCreateDTO(MultipartFile file){
        this.file = file;
    }

    private MultipartFile file;
}
