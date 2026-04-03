package com.example.spring_ai_tutorial.controller;

import com.example.spring_ai_tutorial.domain.dto.DocumentSearchResultDto;
import com.example.spring_ai_tutorial.domain.dto.ProfileChatRequestDto;
import com.example.spring_ai_tutorial.domain.dto.ProfileChatResponseDto;
import com.example.spring_ai_tutorial.service.ChatService;
import com.example.spring_ai_tutorial.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 포트폴리오 방문자가 이유림 개발자에 대해 질문할 수 있는 Profile RAG 채팅 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile Chat API", description = "이유림 개발자 프로필 기반 AI 채팅 API")
public class ProfileChatController {

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            당신은 이유림 개발자의 AI 소개 도우미입니다.
            반드시 아래 [제공된 정보] 안에 있는 내용만 사용하여 답변하세요.
            [제공된 정보]에 없는 내용은 절대 추측하거나 만들어내지 마세요.
            질문이 [제공된 정보]로 답할 수 없는 경우, 반드시 다음 문장만 출력하세요:
            "해당 내용은 제공된 정보에 없습니다. 자세한 내용은 포트폴리오 사이트(https://uriml.github.io)를 참고해 주세요."
            답변은 간결하고 읽기 쉽게 한국어로 작성해 주세요.

            [제공된 정보]:
            %s
            """;

    private final RagService ragService;
    private final ChatService chatService;

    public ProfileChatController(RagService ragService, ChatService chatService) {
        this.ragService = ragService;
        this.chatService = chatService;
    }

    @Operation(summary = "프로필 AI 채팅",
            description = "이유림 개발자에 대한 질문을 입력하면 RAG 기반 AI 답변을 반환합니다.")
    @PostMapping("/chat")
    public ResponseEntity<ProfileChatResponseDto> chat(@RequestBody ProfileChatRequestDto request) {
        String question = request.getQuestion();
        log.info("프로필 채팅 요청: {}", question);

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ProfileChatResponseDto("질문을 입력해 주세요."));
        }

        List<DocumentSearchResultDto> relevantDocs = ragService.retrieve(question, 10);

        if (relevantDocs.isEmpty()) {
            return ResponseEntity.ok(new ProfileChatResponseDto(
                    "관련 정보를 찾지 못했습니다. 포트폴리오 사이트(https://uriml.github.io)를 방문해 주세요."));
        }

        String context = IntStream.range(0, relevantDocs.size())
                .mapToObj(i -> "[" + (i + 1) + "] " + relevantDocs.get(i).getContent())
                .collect(Collectors.joining("\n\n"));

        String systemPrompt = SYSTEM_PROMPT_TEMPLATE.formatted(context);

        try {
            var response = chatService.openAiChat(question, systemPrompt, "gpt-3.5-turbo");
            String answer = (response != null && response.getResult() != null)
                    ? response.getResult().getOutput().getText()
                    : "답변을 생성할 수 없습니다.";
            log.info("프로필 채팅 응답 생성 완료");
            return ResponseEntity.ok(new ProfileChatResponseDto(answer));
        } catch (Exception e) {
            log.error("프로필 채팅 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ProfileChatResponseDto("오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
        }
    }
}
