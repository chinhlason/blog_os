package com.sonnvt.blog.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnvt.blog.exception.ex.MappingException;
import com.sonnvt.blog.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCommentResponse {
    private long id;
    private long idPost;
    private Author Author;
    private long idMasterParent;
    private long idParent;
    private long childCount;
    private String content;
    private List<GetCommentResponse> replies;
    private String createdAt;
    private String updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Author {
        private long id;
        private String username;
        private String firstName;
        private String lastName;
        private String avatar;
    }

    public static Author convertToAuthor(String author) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(author, Author.class);
        } catch (Exception e) {
            throw new MappingException("Error when convert String to Author class");
        }
    }

    public static List<GetCommentResponse> convertToReplies(String replies) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<GetCommentResponse> repliesMapped = mapper.readValue(replies, new TypeReference<List<GetCommentResponse>>() {});
            for (GetCommentResponse reply : repliesMapped) {
                reply.setCreatedAt(Utils.timeFromNow(reply.getCreatedAt()));
                reply.setUpdatedAt(Utils.timeFromNow(reply.getUpdatedAt()));
            }
            return repliesMapped;
        } catch (Exception e) {
            throw new MappingException("Error when convert String to List<GetCommentResponse> class " + e.getMessage());
        }
    }
}
