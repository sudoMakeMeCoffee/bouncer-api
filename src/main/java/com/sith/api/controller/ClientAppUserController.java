package com.sith.api.controller;

import com.sith.api.dto.request.CreateClientAppUserRequestDto;
import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.ClientAppUserResponseDto;
import com.sith.api.service.ClientAppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client/app/user")
public class ClientAppUserController {

    private final ClientAppUserService clientAppUserService;

    public ClientAppUserController(ClientAppUserService clientAppUserService) {
        this.clientAppUserService = clientAppUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ClientAppUserResponseDto>> registerClientAppUser(@Valid @RequestBody CreateClientAppUserRequestDto requestDto){
        ClientAppUserResponseDto responseDto = clientAppUserService.registerAppUser(requestDto);

        ApiResponse<ClientAppUserResponseDto> response = new ApiResponse<>(
                true,
                "App User Created Successfully.",
                responseDto,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{appId}")
    public ResponseEntity<ApiResponse<List<ClientAppUserResponseDto>>> getAllAppUsersByAppId(@PathVariable("appId") UUID appId){
        List<ClientAppUserResponseDto> appUsers = clientAppUserService.getAllAppUsersByAppId(appId);

        ApiResponse<List<ClientAppUserResponseDto>> response = new ApiResponse<>(
                true,
                "App users fetched successfully",
                appUsers,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
