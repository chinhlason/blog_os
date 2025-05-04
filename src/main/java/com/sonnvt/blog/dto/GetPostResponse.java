package com.sonnvt.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnvt.blog.exception.ex.MappingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPostResponse {
    private long id;
    private String title;
    private String content;
    private int views;
    private int commentCount;
    private Author author;
    private List<Tag> tags;
    private String createdAt;
    private String updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Author {
        private long id;
        private String firstName;
        private String lastName;
        private String username;
        private String avatar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag {
        private long id;
        private String name;
    }

    public static Author mapAuthor(String author) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(author, Author.class);
        } catch (Exception e) {
            throw new MappingException("Error when convert String to Author class");
        }
    }

    public static List<Tag> mapTags(String tags) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(tags, new TypeReference<List<Tag>>() {});
        } catch (Exception e) {
            throw new MappingException("Error when convert String to List<Tag> class " + e.getMessage());
        }
    }
}
