package com.sith.api.controller;

import com.sith.api.dto.request.RegisterAppUserRequestDto;
import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.ClientAppUserResponseDto;
import com.sith.api.entity.ClientApp;
import com.sith.api.service.ClientAppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class ApiClientAppUserController {


    private final ClientAppUserService clientAppUserService;

    public ApiClientAppUserController(ClientAppUserService clientAppUserService) {
        this.clientAppUserService = clientAppUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ClientAppUserResponseDto>> register(
            @RequestHeader("x-api-key") String apiKey,
            @Valid @RequestBody RegisterAppUserRequestDto requestDto
    ){

        ClientAppUserResponseDto appUserResponseDto = clientAppUserService.register(requestDto, apiKey);

        ApiResponse<ClientAppUserResponseDto> response = new ApiResponse<>(
                true,
                "User registered successfully.",
                appUserResponseDto,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }
}
