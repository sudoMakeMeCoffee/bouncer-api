package com.sith.api.controller;

import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.DashboardOverviewResponseDto;
import com.sith.api.service.ClientAppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client/dashboard")
public class DashboardOverviewController {

    private final ClientAppUserService.DashboardOverviewService dashboardOverviewService;

    public DashboardOverviewController(ClientAppUserService.DashboardOverviewService dashboardOverviewService) {
        this.dashboardOverviewService = dashboardOverviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardOverviewResponseDto>> getDashboardOverviewData(){
        DashboardOverviewResponseDto responseDto = dashboardOverviewService.getDashboardOverviewData();

        ApiResponse<DashboardOverviewResponseDto> response = new ApiResponse<>(
                true,
                "Dashboard data fetched successfully",
                responseDto,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
