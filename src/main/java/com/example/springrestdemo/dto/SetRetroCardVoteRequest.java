package com.example.springrestdemo.dto;

import com.example.springrestdemo.entity.RetroVoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Retro kartına katılıyorum / katılmıyorum oyu (pano başına en fazla 3)")
public record SetRetroCardVoteRequest(
        @NotBlank @Size(max = 64) String personId,
        @NotNull RetroVoteType voteType) {}
