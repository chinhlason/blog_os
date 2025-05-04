package com.sonnvt.blog.api;

import com.sonnvt.blog.dto.BaseResponse;
import com.sonnvt.blog.dto.NotificationResponse;
import com.sonnvt.blog.exception.errMsg.ErrorMessages;
import com.sonnvt.blog.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping("/notifications")
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> get(@RequestParam(required = false) Long idPivot) {
        if (idPivot == null) {
            idPivot = 0L;
        }
        return ResponseEntity.ok(BaseResponse.<List<NotificationResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(notificationService.get(idPivot))
                .build());
    }
}
