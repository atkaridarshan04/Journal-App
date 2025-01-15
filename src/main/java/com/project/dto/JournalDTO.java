package com.project.dto;

import com.project.enums.Sentiment;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalDTO {

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private Sentiment sentiment;
}