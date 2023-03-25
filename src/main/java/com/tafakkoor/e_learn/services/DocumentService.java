package com.tafakkoor.e_learn.services;

import com.tafakkoor.e_learn.config.security.UserSession;
import com.tafakkoor.e_learn.domain.Document;
import com.tafakkoor.e_learn.dto.DocumentCreateDTO;
import com.tafakkoor.e_learn.repository.DocumentRepository;
import com.tafakkoor.e_learn.utils.Util;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class DocumentService {
    private Path rootPath;
    private final DocumentRepository documentRepository;

    private final UserSession userSession;


    public DocumentService( DocumentRepository documentRepository, UserSession userSession ) {
        this.documentRepository = documentRepository;
        this.userSession = userSession;
    }

    @PostConstruct
    public void init() throws IOException {
        rootPath = Path.of(System.getProperty("user.home"), "/uploads");
        if (!Files.exists(rootPath)) {
            Files.createDirectories(rootPath);
        }
    }


    public Document createAndGet(DocumentCreateDTO dto) {
        MultipartFile file = dto.getFile();
        Document document = Document.builder()
                .createdBy(Math.toIntExact(userSession.getUser().getId()))
                .filePath(rootPath.toString())
                .originalFileName(file.getOriginalFilename())
                .generatedFileName(Util.getInstance().generateUniqueName(Objects.requireNonNull(file.getOriginalFilename())))
                .build();
        
        documentRepository.save(document);
        CompletableFuture.runAsync(() -> {
            Path path = rootPath.resolve(document.getGeneratedFileName());
            try {
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return document;
    }




    public Document findById( Long id ) {
        return documentRepository.findById(id).get();
    }
}
