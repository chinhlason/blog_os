package com.sonnvt.blog.exception.errMsg;

public class ErrorMessages {
    public static final ErrorMessage Success = new ErrorMessage("ER0000", "success");
    public static final ErrorMessage SystemError = new ErrorMessage("ER1000", "system.error");
    public static final ErrorMessage GoogleOAuth2Error = new ErrorMessage("ER1002", "google.oauth2.error");
    public static final ErrorMessage LoginError = new ErrorMessage("ER1003", "login.error");
    public static final ErrorMessage RegisterError = new ErrorMessage("ER1004", "register.error");
    public static final ErrorMessage TokenExpiredError = new ErrorMessage("ER1005", "token.expired");
    public static final ErrorMessage UpdateDatabaseError = new ErrorMessage("ER1006", "update.error");
    public static final ErrorMessage UploadFileError = new ErrorMessage("ER1007", "upload.file.error");
    public static final ErrorMessage CreateTagError = new ErrorMessage("ER1008", "create.tag.error");
    public static final ErrorMessage CreatePostError = new ErrorMessage("ER1009", "create.post.error");
    public static final ErrorMessage CreateCommentError = new ErrorMessage("ER1010", "create.comment.error");
    public static final ErrorMessage GetCommentError = new ErrorMessage("ER1011", "get.comment.error");
    public static final ErrorMessage BookmarkError = new ErrorMessage("ER1012", "bookmark.error");
    public static final ErrorMessage GetPostError = new ErrorMessage("ER1013", "get.post.error");
    public static final ErrorMessage UserNotFoundError = new ErrorMessage("ER1014", "user.not.found.error");
    public static final ErrorMessage BadRequestError = new ErrorMessage("ER1015", "bad.request.error");
}
