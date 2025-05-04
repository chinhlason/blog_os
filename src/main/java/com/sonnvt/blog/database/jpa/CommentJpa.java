package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.Comment;
import com.sonnvt.blog.database.projection.CommentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentJpa extends JpaRepository<Comment, Long> {
    @Query(value = """
        SELECT
            master.id AS id,
            master.id_post AS idPost,
            master.content AS content,
            master.created_at AS createdAt,
            master.updated_at AS updatedAt,
            master.children_count AS childrenCount,
            jsonb_build_object(
                'id', author.id,
                'username', author.username,
                'firstName', author.first_name,
                'lastName', author.last_name,
                'avatar', author.avatar
            ) AS author,
            COALESCE(
                (
                    SELECT jsonb_agg(
                        jsonb_build_object(
                            'id', reply.id,
                            'idPost', reply.id_post,
                            'content', reply.content,
                            'idParent', reply.id_parent,
                            'idMasterParent', reply.id_master_parent,
                            'createdAt', reply.created_at,
                            'updatedAt', reply.updated_at,
                            'author', jsonb_build_object(
                                'id', reply_author.id,
                                'username', reply_author.username,
                                'firstName', reply_author.first_name,
                                'lastName', reply_author.last_name,
                                'avatar', reply_author.avatar
                            )
                        )
                        ORDER BY reply.id_parent ASC
                    )\s
                    FROM comments reply
                    JOIN users reply_author ON reply.id_author = reply_author.id
                    WHERE reply.id_master_parent = master.id
                ), '[]'::jsonb
            ) AS replies
        FROM comments master
        JOIN users author ON master.id_author = author.id
        WHERE master.id_master_parent = 0\s
            AND master.id_post = :idPost
        ORDER BY master.id DESC
        LIMIT :limit OFFSET :offset
       \s""", nativeQuery = true)
    List<CommentProjection> getCommentInPost(@Param("idPost") long idPost, @Param("limit") int limit, @Param("offset") int offset);

    int countAllByIdPost(@Param("idPost") long idPost);
}
