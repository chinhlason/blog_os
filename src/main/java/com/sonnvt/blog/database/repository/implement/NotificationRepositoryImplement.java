package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.entity.Notification;
import com.sonnvt.blog.database.jpa.NotificationJpa;
import com.sonnvt.blog.database.repository.NotificationRepository;
import com.sonnvt.blog.dto.NotificationResponse;
import com.sonnvt.blog.enums.ENotificationType;
import com.sonnvt.blog.exception.ex.UpdateDatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImplement implements NotificationRepository {
    private final NotificationJpa notificationJpa;
    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public Notification save(long idRecipient, long idSender, String content, String metadata, String notificationType) {
        return notificationJpa.save(Notification.builder()
                        .idSender(idSender)
                        .idRecipient(idRecipient)
                        .content(content)
                        .metadata(metadata)
                        .type(notificationType).build());
    }

    @Override
    @Transactional
    public void saveByBatch(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            return;
        }
        StringBuilder query = new StringBuilder("INSERT INTO notifications (id_sender, id_recipient, content, metadata, type) VALUES ");
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            query.append("(")
                    .append(notification.getIdSender()).append(",")
                    .append(notification.getIdRecipient()).append(",'")
                    .append(notification.getContent()).append("','")
                    .append(notification.getMetadata()).append("','")
                    .append(notification.getType()).append("')");
            if (i < notifications.size() - 1) {
                query.append(",");
            }
        }
        int rowEffected = entityManager.createNativeQuery(query.toString()).executeUpdate();
        if (rowEffected != notifications.size()) {
            throw new UpdateDatabaseException("Save notification failed");
        }
    }

    @Override
    public List<NotificationResponse> get(long idRecipient, long idPivot) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder query = new StringBuilder("""
            SELECT\s
                n.id,\s
                n.content,\s
                n.metadata,\s
                n.seen,\s
                n.read,\s
                n.type,\s
                n.created_at,\s
                jsonb_build_object(\s
                    'id', sender.id,\s
                    'username', sender.username,\s
                    'firstName', sender.first_name,\s
                    'lastName', sender.last_name,\s
                    'avatar', sender.avatar) AS sender\s
                FROM notifications n\s
                JOIN users sender ON n.id_sender = sender.id\s
                WHERE n.id_recipient = :idRecipient \s""");
        params.put("idRecipient", idRecipient);
        if (idPivot > 0) {
            query.append("AND n.id < :idPivot ");
            params.put("idPivot", idPivot);
        }
        query.append("ORDER BY n.id DESC LIMIT 10");
        Query nativeQuery = entityManager.createNativeQuery(query.toString(), Tuple.class);
        params.forEach(nativeQuery::setParameter);
        List<Tuple> resultList = nativeQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.stream().map(tuple -> NotificationResponse.builder()
                    .id(tuple.get("id", Integer.class))
                    .content(tuple.get("content", String.class))
                    .metadata(tuple.get("metadata", String.class))
                    .seen(tuple.get("seen", Boolean.class))
                    .read(tuple.get("read", Boolean.class))
                    .notificationType(ENotificationType.valueOf(tuple.get("type", String.class)))
                    .createdAt(tuple.get("created_at", Timestamp.class).toString())
                    .sender(NotificationResponse.maptoSender(tuple.get("sender", String.class)))
                    .build()).toList();
        }
        return List.of();
    }
}
