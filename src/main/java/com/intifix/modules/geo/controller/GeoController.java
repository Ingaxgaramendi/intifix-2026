package com.intifix.modules.geo.controller;

import com.intifix.modules.geo.dto.LiveLocationDto;
import com.intifix.modules.geo.dto.UpdateLocationRequest;
import com.intifix.modules.geo.service.TechnicianLiveLocationService;
import com.intifix.shared.api.ApiResponse;
import com.intifix.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geo")
public class GeoController {

    private final TechnicianLiveLocationService locationService;

    public GeoController(TechnicianLiveLocationService locationService) {
        this.locationService = locationService;
    }

    @PutMapping("/technicians/me/location")
    @PreAuthorize("hasRole('TECNICO')")
    public ApiResponse<LiveLocationDto> updateMyLocation(@Valid @RequestBody UpdateLocationRequest req) {
        return ApiResponse.ok(locationService.upsert(
                SecurityUtils.currentUserId(),
                req.latitud(),
                req.longitud()
        ));
    }

    @GetMapping("/technicians/nearby")
    @PreAuthorize("hasAnyRole('CLIENTE','TECNICO','ADMIN')")
    public ApiResponse<List<LiveLocationDto>> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radiusKm
    ) {
        return ApiResponse.ok(locationService.searchNearby(lat, lng, radiusKm));
    }
}
