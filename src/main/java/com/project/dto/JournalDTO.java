package com.project.dto;

import com.project.enums.Sentiment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull(message = "Sentiment is mandatory")
    private Sentiment sentiment;
}