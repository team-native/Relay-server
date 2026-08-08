package com.teamnative.relayplus.domain.enrollment.controller;

import com.teamnative.relayplus.domain.enrollment.dto.EnrollmentCreateRequest;
import com.teamnative.relayplus.domain.enrollment.dto.EnrollmentResponse;
import com.teamnative.relayplus.domain.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 강의 신청(Enrollment) API
 * 엔드포인트:
 * - POST   /api/enrollments          : 강의 신청
 * - DELETE /api/enrollments/{id}     : 신청 취소
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }


    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(
            Authentication authentication,
            @Valid @RequestBody EnrollmentCreateRequest request
    ) {
        EnrollmentResponse response = enrollmentService.createEnrollment(
                authentication.getName(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelEnrollment(
            Authentication authentication,
            @PathVariable Long id
    ) {
        enrollmentService.cancelEnrollment(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
