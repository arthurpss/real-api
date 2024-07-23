package com.real.chat.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Usage {
    public int prompt_tokens;
    public int completion_tokens;
    public int total_tokens;
}