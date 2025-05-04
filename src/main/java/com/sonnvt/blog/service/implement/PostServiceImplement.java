package com.sonnvt.blog.service.implement;

import com.sonnvt.blog.database.dao.PostResponseDao;
import com.sonnvt.blog.database.entity.Bookmark;
import com.sonnvt.blog.database.entity.Comment;
import com.sonnvt.blog.database.entity.Post;
import com.sonnvt.blog.database.entity.Tag;
import com.sonnvt.blog.database.jpa.ReportHistoryJpa;
import com.sonnvt.blog.database.projection.CommentProjection;
import com.sonnvt.blog.database.projection.PostProjection;
import com.sonnvt.blog.database.projection.ReportProjection;
import com.sonnvt.blog.database.repository.BookmarkRepository;
import com.sonnvt.blog.database.repository.CommentRepository;
import com.sonnvt.blog.database.repository.PostRepository;
import com.sonnvt.blog.database.repository.ReportHistoryRepository;
import com.sonnvt.blog.database.repository.TagRepository;
import com.sonnvt.blog.database.repository.UserRepository;
import com.sonnvt.blog.dto.*;
import com.sonnvt.blog.enums.ENotificationType;
import com.sonnvt.blog.exception.ex.BadRequestException;
import com.sonnvt.blog.exception.ex.BookMarkException;
import com.sonnvt.blog.exception.ex.CommentException;
import com.sonnvt.blog.exception.ex.CreatePostException;
import com.sonnvt.blog.exception.ex.GetPostException;
import com.sonnvt.blog.exception.ex.SystemException;
import com.sonnvt.blog.security.UserPrincipal;
import com.sonnvt.blog.service.FileService;
import com.sonnvt.blog.service.NotificationService;
import com.sonnvt.blog.service.PostService;
import com.sonnvt.blog.utils.Utils;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImplement implements PostService {
    private final TagRepository tagRepository;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ReportHistoryRepository reportHistoryRepository;
    private final ReportHistoryJpa reportHistoryJpa;

    @Value("${app.max-report}")
    private int MAX_REPORT;

    @Value("${app.report-duration}")
    private long REPORT_DURATION;

    @Value("${app.admin-username}")
    private String ADMIN_USERNAME;

    @Override
    public TagResponse createTag(String name) {
        Tag resp = tagRepository.create(name);
        return TagResponse.builder().id(resp.getId()).name(resp.getName())
                .postsNumber(resp.getPostsNumber())
                .createdAt(resp.getCreatedAt().toString())
                .updatedAt(resp.getUpdatedAt().toString())
                .build();
    }

    @Override
    public List<TagResponse> getTag(int limit) {
        List<Tag> tags = tagRepository.getByPostsNumberDesc(limit);
        List<TagResponse> tagResponses = new ArrayList<>();
        tags.forEach(tag -> tagResponses.add(TagResponse.builder().id(tag.getId()).name(tag.getName())
                .postsNumber(tag.getPostsNumber())
                .createdAt(tag.getCreatedAt() == null ? "" : tag.getCreatedAt().toString())
                .updatedAt(tag.getUpdatedAt() == null ? "" : tag.getUpdatedAt().toString())
                .build()));
        return tagResponses;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (Utils.isEmptyFile(file)) {
            throw new CreatePostException("File is empty");
        }
        return fileService.upload(file, true);
    }

    @Override
    @Transactional
    public CreatePostResponse createPost(CreatePostRequest createPostRequest) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long authorId = userPrincipal.getId();
        List<Tag> tags = tagRepository.getInList(createPostRequest.getTagIds());
        List<Long> tagIds = new ArrayList<>();
        List<CreatePostResponse.Tag> tagsResp = new ArrayList<>();
        tags.forEach(tag -> {
            tagIds.add(tag.getId());
            tagsResp.add(new CreatePostResponse.Tag(tag.getId(), tag.getName()));
        });

        Post post = postRepository.save(authorId, createPostRequest.getTitle(), createPostRequest.getContent());

        postRepository.savePostTag(post.getId(), tagIds);

        notificationService.send(NotificationRequest.builder()
                        .idRecipient(authorId)
                        .metadata(new NotificationServiceImplement.PostNoti(post.getId(), post.getTitle()))
                        .notificationType(ENotificationType.NEW_POST).build());

        return CreatePostResponse.builder()
                .id(post.getId())
                .title(createPostRequest.getTitle())
                .views(post.getViews())
                .idAuthor(authorId)
                .tags(tagsResp)
                .createdAt(post.getCreatedAt().toString())
                .updatedAt(post.getUpdatedAt().toString())
                .build();
    }

    @Override
    public CreatePostResponse createGlobalPost(CreatePostRequest createPostRequest) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long authorId = userPrincipal.getId();
        List<Tag> tags = tagRepository.getInList(createPostRequest.getTagIds());
        List<Long> tagIds = new ArrayList<>();
        List<CreatePostResponse.Tag> tagsResp = new ArrayList<>();
        tags.forEach(tag -> {
            tagIds.add(tag.getId());
            tagsResp.add(new CreatePostResponse.Tag(tag.getId(), tag.getName()));
        });

        Post post = postRepository.save(authorId, createPostRequest.getTitle(), createPostRequest.getContent());

        postRepository.savePostTag(post.getId(), tagIds);

        notificationService.send(NotificationRequest.builder()
                .idRecipient(authorId)
                .metadata(new NotificationServiceImplement.PostNoti(post.getId(), post.getTitle()))
                .notificationType(ENotificationType.GLOBAL).build());

        return CreatePostResponse.builder()
                .id(post.getId())
                .title(createPostRequest.getTitle())
                .views(post.getViews())
                .idAuthor(authorId)
                .tags(tagsResp)
                .createdAt(post.getCreatedAt().toString())
                .updatedAt(post.getUpdatedAt().toString())
                .build();
    }

    @Override
    public GetPostResponseAndTotalRecord getPost(int page, int size) {
        int offset = (page - 1) * size;
        List<PostProjection> posts = postRepository.getPost(size, offset);
        List<GetPostResponse> postResponses = new ArrayList<>();
        for (PostProjection post : posts) {
            List<GetPostResponse.Tag> tags = new ArrayList<>();
            GetPostResponse.Author author = new GetPostResponse.Author();
            if (!Utils.isEmpty(post.getTags())) {
                tags = GetPostResponse.mapTags(post.getTags());
            }
            if (!Utils.isEmpty(post.getAuthor())) {
                author = GetPostResponse.mapAuthor(post.getAuthor());
            }
            GetPostResponse temp = GetPostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .views(post.getViews())
                    .commentCount(post.getCommentCount())
                    .author(author)
                    .tags(tags)
                    .createdAt(Utils.timeFromNow(post.getCreatedAt()))
                    .updatedAt(Utils.timeFromNow(post.getUpdatedAt()))
                    .build();
            postResponses.add(temp);
        }
        long totalRecords = postRepository.countPost();
        return new GetPostResponseAndTotalRecord(postResponses, totalRecords);
    }

    @Override
    public GetPostResponse getPostById(Long id) {
        PostProjection resp = postRepository.getPostById(id);
        List<GetPostResponse.Tag> tags = new ArrayList<>();
        GetPostResponse.Author author = new GetPostResponse.Author();
        if (!Utils.isEmpty(resp.getTags())) {
            tags = GetPostResponse.mapTags(resp.getTags());
        }
        if (!Utils.isEmpty(resp.getAuthor())) {
            author = GetPostResponse.mapAuthor(resp.getAuthor());
        }
        return GetPostResponse.builder()
                .id(resp.getId())
                .title(resp.getTitle())
                .content(resp.getContent())
                .views(resp.getViews())
                .commentCount(resp.getCommentCount())
                .author(author)
                .tags(tags)
                .createdAt(Utils.timeFromNow(resp.getCreatedAt()))
                .updatedAt(Utils.timeFromNow(resp.getUpdatedAt()))
                .build();
    }

    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    public CreateCommentResponse createComment(CreateCommentRequest request) {
        long postId = request.getIdPost();
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long authorId = userPrincipal.getId();
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new CommentException("Post not found");
        }
        Comment createdCmt = commentRepository.create(postId, authorId, request.getContent());
        postRepository.increaseCommentCount(postId);

        checkIsMentionUser(createdCmt);

        notificationService.send(NotificationRequest.builder()
                        .idRecipient(post.getIdAuthor())
                        .metadata(new NotificationServiceImplement.PostNoti(post.getId(), post.getTitle()))
                        .notificationType(ENotificationType.COMMENT).build());

        return CreateCommentResponse.builder()
                .id(createdCmt.getId())
                .idPost(createdCmt.getIdPost())
                .idMasterParent(createdCmt.getIdMasterParent())
                .idParent(createdCmt.getIdParent())
                .idAuthor(createdCmt.getIdAuthor())
                .content(createdCmt.getContent())
                .createdAt(createdCmt.getCreatedAt().toString())
                .updatedAt(createdCmt.getUpdatedAt().toString())
                .build();
    }

    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    public CreateCommentResponse replyComment(ReplyCommentRequest request) {
        long idComment = request.getIdComment();
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long authorId = userPrincipal.getId();
        Comment comment = commentRepository.get(idComment);
        if (comment == null) {
            throw new CommentException("Comment not found");
        }

        long idMasterParent = comment.getIdMasterParent() == 0 ? comment.getId() : comment.getIdMasterParent();

        commentRepository.increaseChildCount(idMasterParent);
        postRepository.increaseCommentCount(comment.getIdPost());

        Comment createdCmt = commentRepository.reply(comment.getIdPost(), authorId, idComment,
                idMasterParent , request.getContent());

        checkIsMentionUser(createdCmt);

        notificationService.send(NotificationRequest.builder()
                        .idRecipient(comment.getIdAuthor())
                        .metadata(new NotificationServiceImplement.CommentNoti(comment.getId(), comment.getContent()))
                        .notificationType(ENotificationType.REPLY_COMMENT).build());

        return CreateCommentResponse.builder()
                .id(createdCmt.getId())
                .idPost(createdCmt.getIdPost())
                .idMasterParent(idMasterParent)
                .idParent(createdCmt.getIdParent())
                .idAuthor(createdCmt.getIdAuthor())
                .content(createdCmt.getContent())
                .createdAt(createdCmt.getCreatedAt().toString())
                .updatedAt(createdCmt.getUpdatedAt().toString())
                .build();
    }

    @Override
    public GetCommentResponseAndTotalRecord getCommentsInPost(Long idPost, int page, int size) {
        int offset = (page - 1) * size;
        List<CommentProjection> comments = commentRepository.getInPost(idPost, size, offset);
        List<GetCommentResponse> commentResponses = new ArrayList<>();
        for (CommentProjection comment : comments) {
            GetCommentResponse temp = new GetCommentResponse();
            long childCount = 0;
            List<GetCommentResponse> replies = new ArrayList<>();
            GetCommentResponse.Author author = new GetCommentResponse.Author();
            if (comment.getChildCount() != null) {
                childCount = comment.getChildCount();
            }
            if (comment.getReplies() != null) {
                replies = GetCommentResponse.convertToReplies(comment.getReplies());
            }
            if (comment.getAuthor() != null) {
                author = GetCommentResponse.convertToAuthor(comment.getAuthor());
            }
            temp.setId(comment.getId());
            temp.setIdPost(comment.getIdPost());
            temp.setContent(comment.getContent());
            temp.setChildCount(childCount);
            temp.setAuthor(author);
            temp.setReplies(replies);
            temp.setCreatedAt(Utils.timeFromNow(comment.getCreatedAt()));
            temp.setUpdatedAt(Utils.timeFromNow(comment.getUpdatedAt()));
            commentResponses.add(temp);
        }
        int totalRecords = commentRepository.countInPost(idPost);
        return new GetCommentResponseAndTotalRecord(commentResponses, totalRecords);
    }

    @Override
    public Map<String, Object> findCommentPosition(Long idComment) {
        Comment comment = commentRepository.get(idComment);
        if (comment == null) {
            throw new CommentException("Comment not found");
        }
        Post post = postRepository.findById(comment.getIdPost());
        if (post == null) {
            throw new CommentException("Post not found");
        }
        int page = commentRepository.findPageById(idComment);
        Map<String, Object> resp = new HashMap<>();
        resp.put("idPost", post.getId());
        resp.put("page", page);
        resp.put("size", 15);
        return resp;
    }

    @Override
    public GetPostResponseAndTotalRecord searchPost(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<PostProjection> posts = postRepository.searchPost(keyword, size, offset);
        List<GetPostResponse> postResponses = new ArrayList<>();
        for (PostProjection post : posts) {
            List<GetPostResponse.Tag> tags = new ArrayList<>();
            GetPostResponse.Author author = new GetPostResponse.Author();
            if (!Utils.isEmpty(post.getTags())) {
                tags = GetPostResponse.mapTags(post.getTags());
            }
            if (!Utils.isEmpty(post.getAuthor())) {
                author = GetPostResponse.mapAuthor(post.getAuthor());
            }
            GetPostResponse temp = GetPostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .views(post.getViews())
                    .commentCount(post.getCommentCount())
                    .author(author)
                    .tags(tags)
                    .createdAt(Utils.timeFromNow(post.getCreatedAt()))
                    .updatedAt(Utils.timeFromNow(post.getUpdatedAt()))
                    .build();
            postResponses.add(temp);
        }
        long totalRecords = postRepository.countPostByKeyword(keyword);
        return new GetPostResponseAndTotalRecord(postResponses, totalRecords);
    }

    @Override
    public void bookmark(Long idPost) {
        PostProjection post = postRepository.getPostById(idPost);
        if (post == null) {
            throw new BookMarkException("Post not found");
        }
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long userId = userPrincipal.getId();

        Bookmark bookmark = bookmarkRepository.findByUserIdAndPostId(userId, idPost);
        if (bookmark != null) {
            throw new BookMarkException("Post already bookmarked");
        }

        try {
            bookmarkRepository.save(userId, idPost);
        } catch (Exception e) {
            throw new BookMarkException("Error when saving bookmark");
        }
    }

    @Override
    public GetPostResponseAndTotalRecord getPostsByOption(String option, int page, int size) {
        long authorId = -1;
        int offset = (page - 1) * size;
        String[] parts = option.contains(":=") ? option.split(":=") : new String[]{option, ""};
        if (parts[0].equals("bookmark")) {
            throw new GetPostException("Option is invalid");
        }
        List<PostResponseDao> posts = postRepository.getPostByOption(parts[0].trim(), parts[1].trim(), authorId, size, offset);
        List<GetPostResponse> result = new ArrayList<>();
        for (PostResponseDao post : posts) {
            GetPostResponse.Author author = new GetPostResponse.Author();
            List<GetPostResponse.Tag> tags = new ArrayList<>();
            if (!Utils.isEmpty(post.getAuthor())) {
                author = GetPostResponse.mapAuthor(post.getAuthor());
            }
            if (!Utils.isEmpty(post.getTags())) {
                tags = GetPostResponse.mapTags(post.getTags());
            }
            result.add(GetPostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .commentCount(post.getCommentCount())
                    .author(author)
                    .tags(tags)
                    .views(post.getViews())
                    .createdAt(Utils.timeFromNow(post.getCreatedAt()))
                    .updatedAt(Utils.timeFromNow(post.getUpdatedAt()))
                    .build());
        }

        long total = postRepository.countPostByOption(parts[0].trim(), parts[1].trim(), authorId);
        return new GetPostResponseAndTotalRecord(result, total);
    }

    @Override
    public GetPostResponseAndTotalRecord getPostsBookmark(int page, int size) {
        int offset = (page - 1) * size;
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long authorId = userPrincipal.getId();
        List<PostResponseDao> posts = postRepository.getPostByOption("bookmark", "", authorId, size, offset);
        List<GetPostResponse> result = new ArrayList<>();
        for (PostResponseDao post : posts) {
            GetPostResponse.Author author = new GetPostResponse.Author();
            List<GetPostResponse.Tag> tags = new ArrayList<>();
            if (!Utils.isEmpty(post.getAuthor())) {
                author = GetPostResponse.mapAuthor(post.getAuthor());
            }
            if (!Utils.isEmpty(post.getTags())) {
                tags = GetPostResponse.mapTags(post.getTags());
            }
            result.add(GetPostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .commentCount(post.getCommentCount())
                    .author(author)
                    .tags(tags)
                    .views(post.getViews())
                    .createdAt(Utils.timeFromNow(post.getCreatedAt()))
                    .updatedAt(Utils.timeFromNow(post.getUpdatedAt()))
                    .build());
        }

        long total = postRepository.countPostByOption("bookmark", "", authorId);
        return new GetPostResponseAndTotalRecord(result, total);
    }

    @Override
    public void increaseView(Long id) {
        Post post = postRepository.findById(id);
        if (post == null) {
            throw new BadRequestException("Post not found");
        }
        postRepository.increaseViewCount(id);
    }

    @Override
    @Transactional
    public void update(UpdatePostRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long authorId = userPrincipal.getId();
        Post post = postRepository.findById(request.getId());
        if (post == null) {
            throw new BadRequestException("Post not found");
        }
        if (post.getIdAuthor() != authorId) {
            throw new BadRequestException("You do not have permission to update this post!");
        }
        try {
            postRepository.update(request.getId(), request.getTitle(), request.getContent());
            tagRepository.updateInPost(request.getId(), request.getTagIds());
        } catch (Exception e) {
            throw new SystemException("Error when updating post " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long authorId = userPrincipal.getId();
        Post post = postRepository.findById(id);
        if (post == null) {
            throw new BadRequestException("Post not found");
        }
        if (post.getIdAuthor() != authorId) {
            throw new BadRequestException("You do not have permission to delete this post!");
        }
        postRepository.delete(id);
    }

    @Override
    public void forceDelete(Long id) {
        Post post = postRepository.findById(id);
        if (post == null) {
            throw new BadRequestException("Post not found");
        }
        postRepository.delete(id);
    }

    @Override
    public String report(Long idPost) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long idUser = userPrincipal.getId();
        Post post = postRepository.findById(idPost);
        if (post == null) {
            throw new BadRequestException("Post not found");
        }
        UserInfoResponse userAdmin = userRepository.findByUsernameOrEmail(ADMIN_USERNAME, "").orElse(null);
        ReportProjection report = reportHistoryRepository.getReportHistoryById(idUser, post.getId());
        if (report == null) {
            reportHistoryRepository.create(idUser, post.getId());
            assert userAdmin != null;
            notificationService.send(NotificationRequest.builder()
                    .idRecipient(userAdmin.getId())
                    .metadata(new NotificationServiceImplement.PostNoti(post.getId(), post.getContent()))
                    .notificationType(ENotificationType.REPORT).build());
            return "Report success!";
        }
        if (report.getCount() >= MAX_REPORT) {
            return "You report this post too much!";
        }
        Long cooldown = Utils.cooldown(report.getCreatedAt(), REPORT_DURATION);
        if (cooldown == null) {
            throw new SystemException("Invalid data!");
        } else if (cooldown > 0) {
            long hour = cooldown / 3600;
            long min = cooldown % 3600 / 60;
            return "You only can report this post after " + hour + " hours " + min + " minutes";
        }
        reportHistoryRepository.create(idUser, post.getId());
        notificationService.send(NotificationRequest.builder()
                .idRecipient(userAdmin.getId())
                .metadata(new NotificationServiceImplement.PostNoti(post.getId(), post.getContent()))
                .notificationType(ENotificationType.REPORT).build());
        return "Report success!";
    }

    private void checkIsMentionUser(Comment comment) {
        Pattern pattern = Pattern.compile("@([\\w\\d_]+)");
        Matcher matcher = pattern.matcher(comment.getContent());
        List<String> mentions = new ArrayList<>();
        while (matcher.find()) {
            mentions.add(matcher.group(1));
        }
        for (String mention : mentions) {
            userRepository.findByUsernameOrEmail(mention, "").ifPresent(user -> notificationService.send(NotificationRequest.builder()
                    .idRecipient(user.getId())
                    .metadata(new NotificationServiceImplement.CommentNoti(comment.getId(), comment.getContent()))
                    .notificationType(ENotificationType.MENTION).build()));
        }
    }

    @Data
    @AllArgsConstructor
    public static class GetCommentResponseAndTotalRecord {
        List<GetCommentResponse> comments;
        int totalRecords;
    }

    @Data
    @AllArgsConstructor
    public static class GetPostResponseAndTotalRecord {
        List<GetPostResponse> posts;
        long totalRecords;
    }
}
