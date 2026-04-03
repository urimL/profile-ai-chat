package com.example.spring_ai_tutorial.service;

import com.example.spring_ai_tutorial.repository.QdrantDocumentVectorStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 앱 시작 시 profile-context.md를 읽어 벡터 스토어에 적재하는 초기화 컴포넌트
 */
@Slf4j
@Component
public class ProfileContextInitializer implements CommandLineRunner {

    private final QdrantDocumentVectorStore vectorStore;

    public ProfileContextInitializer(QdrantDocumentVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("프로필 컨텍스트 로딩 시작...");

        ClassPathResource resource = new ClassPathResource("profile/profile-context.md");
        if (!resource.exists()) {
            log.warn("profile-context.md 파일을 찾을 수 없습니다. 프로필 RAG가 동작하지 않습니다.");
            return;
        }

        String profileText = resource.getContentAsString(StandardCharsets.UTF_8);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("originalFilename", "profile-context.md");
        metadata.put("source", "profile");

        String documentId = UUID.randomUUID().toString();
        vectorStore.addDocument(documentId, profileText, metadata);

        log.info("프로필 컨텍스트 로딩 완료 (ID: {})", documentId);
    }
}
