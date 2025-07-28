package com.sith.api.controller;

import com.sith.api.dto.request.CreateClientAppRequestDto;
import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.ClientAppResponseDto;
import com.sith.api.service.ClientAppService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client/app")
public class ClientAppController {

    private final ClientAppService clientAppService;

    public ClientAppController(ClientAppService clientAppService) {
        this.clientAppService = clientAppService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createClientApp(@Valid @RequestBody CreateClientAppRequestDto requestDto){
        String apiKey = clientAppService.createClientApp(requestDto);

        ApiResponse<String> response = new ApiResponse<>(
                true,
                "App created successfully.",
                apiKey,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
