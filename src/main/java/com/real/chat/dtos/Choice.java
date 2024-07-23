package com.real.chat.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Choice {
    public int index;
    public Message message;
    public Object logprobs; // Assuming we don't have a specific structure for logprobs
    public String finish_reason;
}