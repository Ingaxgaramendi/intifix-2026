package com.intifix.modules.ai.controller;

import com.intifix.modules.ai.dto.AiDiagnosisRequest;
import com.intifix.modules.ai.dto.AiDiagnosisResponse;
import com.intifix.modules.ai.service.AiDiagnosisService;
import com.intifix.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiDiagnosisService aiDiagnosisService;

    public AiController(AiDiagnosisService aiDiagnosisService) {
        this.aiDiagnosisService = aiDiagnosisService;
    }

    @PostMapping("/diagnose")
    @PreAuthorize("hasRole('CLIENTE')")
    public ApiResponse<AiDiagnosisResponse> diagnose(@Valid @RequestBody AiDiagnosisRequest req) {
        return ApiResponse.ok(aiDiagnosisService.diagnose(req));
    }
}
