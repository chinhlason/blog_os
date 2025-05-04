package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.entity.Comment;
import com.sonnvt.blog.database.jpa.CommentJpa;
import com.sonnvt.blog.database.projection.CommentProjection;
import com.sonnvt.blog.database.repository.CommentRepository;
import com.sonnvt.blog.exception.ex.UpdateDatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImplement implements CommentRepository {
    private final CommentJpa commentJpa;

    private final EntityManager em;

    @Override
    public Comment create(Long idPost, Long idAuthor, String content) {
        return commentJpa.save(Comment.builder()
                .idAuthor(idAuthor)
                .idPost(idPost)
                .content(content).build());
    }

    @Override
    public Comment reply(Long idPost, Long idAuthor, Long idParent, Long idMasterParent, String content) {
        return commentJpa.save(Comment.builder()
                        .idAuthor(idAuthor)
                        .idPost(idPost)
                        .idParent(idParent)
                        .idMasterParent(idMasterParent)
                        .content(content).build());
    }

    @Override
    public Comment get(Long id) {
        return commentJpa.findById(id).orElse(null);
    }

    @Override
    public List<CommentProjection> getInPost(Long idPost, int limit, int offset) {
        return commentJpa.getCommentInPost(idPost, limit, offset);
    }

    @Override
    public int countInPost(Long idPost) {
        return commentJpa.countAllByIdPost(idPost);
    }

    @Override
    public void increaseChildCount(Long idComment) {
        StringBuilder query = new StringBuilder("UPDATE comments SET children_count = children_count + 1, " +
                "updated_at = NOW() " +
                "WHERE id = :id");
        Query q = em.createNativeQuery(query.toString());
        int update = q.setParameter("id", idComment).executeUpdate();
        if (update == 0) {
            throw new  UpdateDatabaseException("Update child count failed");
        }
    }

    @Override
    public int findPageById(Long id) {
        StringBuilder query = new StringBuilder("SELECT CEIL((SELECT COUNT(*) FROM comments WHERE id <= :id) / 10.0) AS page_number");
        Query q = em.createNativeQuery(query.toString());
        BigDecimal rs = (BigDecimal) q.setParameter("id", id).getSingleResult();
        return rs.intValueExact();
    }
}
