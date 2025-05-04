package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.Post;
import com.sonnvt.blog.database.entity.PostTags;
import com.sonnvt.blog.database.projection.PostProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostJpa extends JpaRepository<Post, Long> {
    @Query(value = "SELECT " +
            "posts.id AS id, " +
            "posts.title AS title," +
            "posts.views AS views, " +
            "posts.comment_count AS commentCount, " +
            "posts.created_at AS createdAt, " +
            "posts.updated_at AS updatedAt, " +
            "jsonb_build_object(" +
            "        'id', author.id, " +
            "        'username', author.username, " +
            "        'firstName', author.first_name, " +
            "        'lastName', author.last_name, " +
            "        'avatar', author.avatar)::jsonb AS author, " +
            "COALESCE(jsonb_agg(jsonb_build_object('id', tags.id, 'name', tags.name)) FILTER (WHERE tags.id IS NOT NULL), '[]'::jsonb) AS tags " +
            "FROM " +
            "posts " +
            "JOIN users author ON author.id = posts.id_author " +
            "JOIN post_tags pt ON pt.id_post = posts.id " +
            "JOIN tags ON tags.id = pt.id_tag " +
            "GROUP BY author.id, posts.id " +
            "ORDER BY posts.id DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<PostProjection> get(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT " +
            "posts.id AS id, " +
            "posts.title AS title," +
            "posts.views AS views, " +
            "posts.comment_count AS commentCount, " +
            "posts.content AS content, " +
            "posts.created_at AS createdAt, " +
            "posts.updated_at AS updatedAt, " +
            "jsonb_build_object(" +
            "        'id', author.id, " +
            "        'username', author.username, " +
            "        'firstName', author.first_name, " +
            "        'lastName', author.last_name, " +
            "        'avatar', author.avatar)::jsonb AS author, " +
            "COALESCE(jsonb_agg(jsonb_build_object('id', tags.id, 'name', tags.name)) FILTER (WHERE tags.id IS NOT NULL), '[]'::jsonb) AS tags " +
            "FROM " +
            "posts " +
            "JOIN users author ON author.id = posts.id_author " +
            "JOIN post_tags pt ON pt.id_post = posts.id " +
            "JOIN tags ON tags.id = pt.id_tag " +
            "WHERE posts.id = :id " +
            "GROUP BY author.id, posts.id ", nativeQuery = true)
    PostProjection getByPostId(@Param("id") long id);

    @Query(value = "SELECT * FROM search_posts_v5(:keyword, :limit, :offset) ", nativeQuery = true)
    List<PostProjection> search(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT " +
            "count('*') as total " +
            "FROM posts " +
            "WHERE to_tsvector('public.vietnamese', COALESCE(posts.title, '') || ' ' || COALESCE(posts.content, '')) " +
            "@@ to_tsquery(replace(websearch_to_tsquery(:keyword)::TEXT, '&', '|')) ", nativeQuery = true)
    long countBySearch(@Param("keyword") String keyword);


    @Query(value = """
            SELECT *\s
                FROM post_tags
                WHERE id_post = :idPost\s
            """, nativeQuery = true)
    List<PostTags> findAllByIdPost(@Param("idPost") Long idPost);

    @Query(value = """
            DELETE FROM post
            WHERE id = :id
            """, nativeQuery = true)
    void delete(Long id);
}
