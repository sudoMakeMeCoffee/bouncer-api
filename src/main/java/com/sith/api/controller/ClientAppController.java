package com.sith.api.controller;

import com.sith.api.dto.request.CreateClientAppRequestDto;
import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.ClientAppResponseDto;
import com.sith.api.service.ClientAppService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientAppResponseDto>>> getClientsAppByClientId(){
        List<ClientAppResponseDto> apps = clientAppService.getAllAppsByClientId();

        ApiResponse<List<ClientAppResponseDto>> response = new ApiResponse<>(
                true,
                "Apps fetched successfully",
                apps,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
