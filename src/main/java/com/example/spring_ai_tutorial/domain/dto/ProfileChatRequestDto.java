package com.example.spring_ai_tutorial.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 채팅 요청")
public class ProfileChatRequestDto {

    @Schema(description = "사용자 질문", example = "어떤 프로젝트를 하셨나요?")
    private String question;

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
