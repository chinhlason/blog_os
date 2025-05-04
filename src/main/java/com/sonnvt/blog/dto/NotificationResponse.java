package com.sonnvt.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnvt.blog.enums.ENotificationType;
import com.sonnvt.blog.exception.ex.MappingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResponse {
    private long id;
    private Sender sender;
    private String content;
    private long idRecipient;
    private Object metadata;
    private Boolean seen;
    private Boolean read;
    private ENotificationType notificationType;
    private String createdAt;
    private String updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sender {
        private long id;
        private String firstName;
        private String lastName;
        private String username;
        private String avatar;
    }

    public static Sender maptoSender(String sender) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(sender, Sender.class);
        } catch (Exception e) {
            throw new MappingException("Error when convert String to Author class");
        }
    }
}
