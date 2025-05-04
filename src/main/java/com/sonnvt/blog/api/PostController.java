package com.sonnvt.blog.api;

import com.sonnvt.blog.dto.BaseResponse;
import com.sonnvt.blog.dto.CreateCommentRequest;
import com.sonnvt.blog.dto.CreateCommentResponse;
import com.sonnvt.blog.dto.CreatePostRequest;
import com.sonnvt.blog.dto.CreatePostResponse;
import com.sonnvt.blog.dto.GetCommentResponse;
import com.sonnvt.blog.dto.GetPostResponse;
import com.sonnvt.blog.dto.ReplyCommentRequest;
import com.sonnvt.blog.dto.TagResponse;
import com.sonnvt.blog.dto.UpdatePostRequest;
import com.sonnvt.blog.exception.errMsg.ErrorMessages;
import com.sonnvt.blog.service.PostService;
import com.sonnvt.blog.service.implement.PostServiceImplement;
import com.sonnvt.blog.utils.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final int SIZE = Constant.DEFAULT_SIZE;

    //auth required
    @PostMapping("/public/tag/create")
    public ResponseEntity<BaseResponse<TagResponse>> create(@RequestParam String name) {
        return ResponseEntity.ok(BaseResponse.<TagResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.createTag(name)).build());
    }

    @GetMapping("/public/tags")
    public ResponseEntity<BaseResponse<List<TagResponse>>> getTags(@RequestParam(required = false) Integer limit) {
        log.info("limit {}", limit);
        if (limit == null) {
            limit = 30;
        }
        return ResponseEntity.ok(BaseResponse.<List<TagResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.getTag(limit)).build());
    }

    //auth required
    @PostMapping("/public/image/upload")
    public ResponseEntity<BaseResponse<String>> uploadImage(@RequestBody MultipartFile file) {
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.uploadImage(file)).build());
    }

    //auth required
    @PostMapping("/writer/post/create")
    public ResponseEntity<BaseResponse<CreatePostResponse>> createPost(@RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(BaseResponse.<CreatePostResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.createPost(request)).build());
    }

    @PostMapping("/comment/create")
    public ResponseEntity<BaseResponse<CreateCommentResponse>> createComment(@RequestBody @Valid CreateCommentRequest request) {
        return ResponseEntity.ok(BaseResponse.<CreateCommentResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.createComment(request)).build());
    }

    @PostMapping("/comment/reply")
    public ResponseEntity<BaseResponse<CreateCommentResponse>> replyComment(@RequestBody @Valid ReplyCommentRequest request) {
        return ResponseEntity.ok(BaseResponse.<CreateCommentResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.replyComment(request)).build());
    }

    @GetMapping("/public/get/cmts")
    public ResponseEntity<BaseResponse<List<GetCommentResponse>>> getCommentsInPost(@RequestParam Long idPost,
                                                                                    @RequestParam(required = false) Integer page,
                                                                                    @RequestParam(required = false) Integer size
    ) {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = SIZE;
        }
        PostServiceImplement.GetCommentResponseAndTotalRecord resp = postService.getCommentsInPost(idPost, page, size);
        return ResponseEntity.ok(BaseResponse.<List<GetCommentResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(resp.getComments())
                .totalRecords((long) resp.getTotalRecords())
                .build());
    }

    //auth required
    @GetMapping("/public/get/cmt-position")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getComment(@RequestParam Long id) {
        return ResponseEntity.ok(BaseResponse.<Map<String, Object>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.findCommentPosition(id)).build());
    }

    @GetMapping("/public/posts")
    public ResponseEntity<BaseResponse<List<GetPostResponse>>> getPosts(@RequestParam(required = false) Integer page,
                                                                        @RequestParam(required = false) Integer size) {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = SIZE;
        }
        PostServiceImplement.GetPostResponseAndTotalRecord resp = postService.getPost(page, size);
        return ResponseEntity.ok(BaseResponse.<List<GetPostResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(resp.getPosts())
                .totalRecords(resp.getTotalRecords())
                .build());
    }

    @GetMapping("/public/post/{id}")
    public ResponseEntity<BaseResponse<GetPostResponse>> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.<GetPostResponse>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.getPostById(id)).build());
    }

    @GetMapping("/public/search")
    public ResponseEntity<BaseResponse<List<GetPostResponse>>> searchPost(@RequestParam String keyword,
                                                                         @RequestParam(required = false) Integer page,
                                                                         @RequestParam(required = false) Integer size) {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = SIZE;
        }
        PostServiceImplement.GetPostResponseAndTotalRecord resp = postService.searchPost(keyword, page, size);
        return ResponseEntity.ok(BaseResponse.<List<GetPostResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(resp.getPosts())
                .totalRecords(resp.getTotalRecords())
                .build());
    }

    @PostMapping("/post/bookmark/{id}")
    public ResponseEntity<BaseResponse<String>> bookmark(@PathVariable Long id) {
        postService.bookmark(id);
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data("Bookmark success with post " + id)
                .build());
    }

    @GetMapping("/public/posts/by-option")
    public ResponseEntity<BaseResponse<List<GetPostResponse>>> getByOption(@RequestParam(name = "v") String option,
                                                                           @RequestParam(required = false) Integer page,
                                                                           @RequestParam(required = false) Integer size) {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = SIZE;
        }
        PostServiceImplement.GetPostResponseAndTotalRecord resp = postService.getPostsByOption(option, page, size);
        return ResponseEntity.ok(BaseResponse.<List<GetPostResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(resp.getPosts())
                .totalRecords(resp.getTotalRecords())
                .build());
    }

    @GetMapping("/posts/bookmark")
    public ResponseEntity<BaseResponse<List<GetPostResponse>>> getPostsBookmark(@RequestParam(required = false) Integer page,
                                                                                @RequestParam(required = false) Integer size) {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = SIZE;
        }
        PostServiceImplement.GetPostResponseAndTotalRecord resp = postService.getPostsBookmark(page, size);
        return ResponseEntity.ok(BaseResponse.<List<GetPostResponse>>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(resp.getPosts())
                .totalRecords(resp.getTotalRecords())
                .build());
    }

    @PostMapping("/public/post/increase-view/{id}")
    public ResponseEntity<BaseResponse<String>> increaseView(@PathVariable Long id) {
        postService.increaseView(id);
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data("Increase view success with post " + id)
                .build());
    }

    @PostMapping("/writer/post/update")
    public ResponseEntity<BaseResponse<String>> update(@RequestBody UpdatePostRequest request) {
        postService.update(request);
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data("Update post success")
                .build());
    }

    @DeleteMapping("/writer/post/delete/{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data("Delete post success")
                .build());
    }

    @PostMapping("/comment/report/{id}")
    public ResponseEntity<BaseResponse<String>> report(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .code(ErrorMessages.Success.getErrorCode())
                .message(ErrorMessages.Success.getErrorMessage())
                .data(postService.report(id))
                .build());
    }
}
