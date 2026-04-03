package com.example.spring_ai_tutorial.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 채팅 응답")
public class ProfileChatResponseDto {

    @Schema(description = "AI 답변")
    private String answer;

    public ProfileChatResponseDto(String answer) {
        this.answer = answer;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

}

