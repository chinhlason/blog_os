package com.sonnvt.blog.exception;

import com.sonnvt.blog.dto.BaseResponse;
import com.sonnvt.blog.exception.errMsg.ErrorMessages;
import com.sonnvt.blog.exception.ex.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerGlobal {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorMessages.SystemError.getErrorMessage(), ErrorMessages.SystemError.getErrorCode());
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<BaseResponse<?>> handleSystemException(SystemException e) {
        log.error("SystemException: {}", e.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SystemError.getErrorMessage(),
                ErrorMessages.SystemError.getErrorCode());
    }

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<BaseResponse<?>> handleMappingException(MappingException e) {
        log.error("MappingException: {}", e.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SystemError.getErrorMessage(),
                ErrorMessages.SystemError.getErrorCode());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<?>> handleBadRequestException(BadRequestException e) {
        log.error("BadRequestException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.BadRequestError.getErrorMessage(),
                ErrorMessages.BadRequestError.getErrorCode());
    }

    @ExceptionHandler(GoogleOauth2Exception.class)
    public ResponseEntity<BaseResponse<?>> handleGoogleOauth2Exception(GoogleOauth2Exception e) {
        log.error("GoogleOauth2Exception: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.GoogleOAuth2Error.getErrorMessage(),
                ErrorMessages.GoogleOAuth2Error.getErrorCode());
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<BaseResponse<?>> handleLoginException(LoginException e) {
        log.error("LoginException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.LoginError.getErrorMessage(),
                ErrorMessages.LoginError.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return buildResponse(HttpStatus.BAD_REQUEST, errors, ErrorMessages.RegisterError.getErrorCode());
    }

    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<BaseResponse<?>> handleRegisterException(RegisterException e) {
        log.error("RegisterException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.RegisterError.getErrorMessage(),
                ErrorMessages.RegisterError.getErrorCode());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleUserNotFoundException(UserNotFoundException e) {
        log.error("UserNotFoundException: {}", e.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), ErrorMessages.UserNotFoundError.getErrorMessage(),
                ErrorMessages.UserNotFoundError.getErrorCode());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<BaseResponse<?>> handleTokenExpiredException(TokenExpiredException e) {
        log.error("TokenExpiredException: {}", e.getMessage());
        return buildResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage(), ErrorMessages.TokenExpiredError.getErrorMessage(),
                ErrorMessages.TokenExpiredError.getErrorCode());
    }

    @ExceptionHandler(UploadFileException.class)
    public ResponseEntity<BaseResponse<?>> handleUploadFileException(UploadFileException e) {
        log.error("UploadFileException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.UploadFileError.getErrorMessage(),
                ErrorMessages.UploadFileError.getErrorCode());
    }

    @ExceptionHandler(CreateTagException.class)
    public ResponseEntity<BaseResponse<?>> handleCreateTagException(CreateTagException e) {
        log.error("CreateTagException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.CreateTagError.getErrorMessage(),
                ErrorMessages.CreateTagError.getErrorCode());
    }

    @ExceptionHandler(CreatePostException.class)
    public ResponseEntity<BaseResponse<?>> handleCreatePostException(CreatePostException e) {
        log.error("CreatePostException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.CreatePostError.getErrorMessage(),
                ErrorMessages.CreatePostError.getErrorCode());
    }

    @ExceptionHandler(CreatePostTagException.class)
    public ResponseEntity<BaseResponse<?>> handleSavePostTags(CreatePostTagException e) {
        log.error("SavePostTags: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorMessages.CreatePostError.getErrorMessage(),
                ErrorMessages.CreatePostError.getErrorCode());
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<BaseResponse<?>> handleCommentException(CommentException e) {
        log.error("CommentException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.CreateCommentError.getErrorMessage(),
                ErrorMessages.CreateCommentError.getErrorCode());
    }

    @ExceptionHandler(BookMarkException.class)
    public ResponseEntity<BaseResponse<?>> handleBookMarkException(BookMarkException e) {
        log.error("BookMarkException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.BookmarkError.getErrorMessage(),
                ErrorMessages.BookmarkError.getErrorCode());
    }

    @ExceptionHandler(GetPostException.class)
    public ResponseEntity<BaseResponse<?>> handleGetPostException(GetPostException e) {
        log.error("GetPostException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), ErrorMessages.GetPostError.getErrorMessage(),
                ErrorMessages.GetPostError.getErrorCode());
    }

    private ResponseEntity<BaseResponse<?>> buildResponse(HttpStatus status, String message, String errMsg, String errCode) {
        return ResponseEntity.status(status).body(BaseResponse.builder().code(errCode).message(message).errMsg(errMsg).build());
    }

    private ResponseEntity<BaseResponse<?>> buildResponse(HttpStatus status, Object message, String errCode) {
        return ResponseEntity.status(status).body(BaseResponse.builder().code(errCode).message(message).build());
    }
}
