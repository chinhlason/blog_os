package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.dao.PostResponseDao;
import com.sonnvt.blog.database.entity.Post;
import com.sonnvt.blog.database.jpa.PostJpa;
import com.sonnvt.blog.database.projection.PostProjection;
import com.sonnvt.blog.database.repository.PostRepository;
import com.sonnvt.blog.exception.ex.CreatePostTagException;
import com.sonnvt.blog.exception.ex.GetPostException;
import com.sonnvt.blog.exception.ex.UpdateDatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryImplement implements PostRepository {
    private final PostJpa postJpa;
    private final EntityManager em;

    @Override
    public Post save(Long idAuthor, String title, String content) {
        return postJpa.save(Post.builder()
                .idAuthor(idAuthor)
                .title(title)
                .content(content)
                .build());
    }

    @Override
    @Transactional
    public void savePostTag(Long postId, List<Long> tagIds) {
        if (tagIds.isEmpty()) {
            return;
        }
        StringBuilder sql = new StringBuilder("INSERT INTO post_tags (id_post, id_tag) VALUES ");
        for (int i = 0; i < tagIds.size(); i++) {
            sql.append("(").append(postId).append(",").append(tagIds.get(i)).append(")");
            if (i < tagIds.size() - 1) {
                sql.append(",");
            }
        }
        Query query = em.createNativeQuery(sql.toString());
        int rowEffected = query.executeUpdate();
        if (rowEffected != tagIds.size()) {
            throw new CreatePostTagException("Create post tag failed");
        }
    }

    @Override
    public Post findById(Long id) {
        return postJpa.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void increaseCommentCount(Long idPost) {
        StringBuilder query = new StringBuilder("UPDATE posts SET comment_count = comment_count + 1 WHERE id = :id");
        Query q = em.createNativeQuery(query.toString());
        int update = q.setParameter("id", idPost).executeUpdate();
        if (update == 0) {
            throw new UpdateDatabaseException("Update comment count failed");
        }
    }

    @Override
    @Transactional
    public void increaseViewCount(Long idPost) {
        StringBuilder query = new StringBuilder("UPDATE posts SET views = views + 1 WHERE id = :id");
        Query q = em.createNativeQuery(query.toString());
        int update = q.setParameter("id", idPost).executeUpdate();
        if (update == 0) {
            throw new UpdateDatabaseException("Update views failed");
        }
    }

    @Override
    @Transactional
    public void update(Long id, String title, String content) {
        if (title == null && content == null) {
            return;
        }
        StringBuilder query = new StringBuilder("UPDATE posts SET ");
        List<String> setClause = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        if (title != null) {
            setClause.add("title = :title ");
            params.put("title", title);
        }
        if (content != null) {
            setClause.add("content = :content ");
            params.put("content", content);
        }
        setClause.add("updated_at = NOW() ");
        query.append(String.join(", ", setClause));
        query.append("WHERE id = :id");
        params.put("id", id);
        Query q = em.createNativeQuery(query.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue());
        }
        int update = q.executeUpdate();
        if (update == 0) {
            throw new UpdateDatabaseException("Update post failed");
        }
    }

    @Override
    public List<PostProjection> getPost(int limit, int offset) {
        return postJpa.get(limit, offset);
    }

    @Override
    public PostProjection getPostById(Long id) {
        return postJpa.getByPostId(id);
    }

    @Override
    public long countPost() {
        return postJpa.count();
    }

    @Override
    public List<PostProjection> searchPost(String keyword, int limit, int offset) {
        return postJpa.search(keyword, limit, offset);
    }

    @Override
    public long countPostByKeyword(String keyword) {
        return postJpa.countBySearch(keyword);
    }

    @Override
    public List<PostResponseDao> getPostByOption(String option, String value, long idUser, int limit, int offset) {
        StringBuilder query = new StringBuilder("SELECT " +
                "posts.id AS id, " +
                "posts.title AS title," +
                "posts.views AS views, " +
                "posts.content AS content, " +
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
                "JOIN tags ON tags.id = pt.id_tag ");
        Map<String, Object> params = new HashMap<>();
        if (Objects.equals(option, "bookmark")) {
            query.append("JOIN bookmarks b ON b.id_post = posts.id ");
            query.append("JOIN users u ON u.id = b.id_user ");
            query.append("WHERE u.id = :id ");
            query.append("GROUP BY author.id, posts.id ");
            params.put("id", idUser);
        } else if (Objects.equals(option, "tag")) {
            query.append("JOIN post_tags pt2 ON pt2.id_post = posts.id ");
            query.append("JOIN tags tags2 ON tags2.id = pt2.id_tag ");
            query.append("WHERE tags2.id = :id ");
            query.append("GROUP BY author.id, posts.id ");
            params.put("id", Long.parseLong(value));
        } else if (Objects.equals(option, "author")) {
            query.append("WHERE author.id = :id ");
            query.append("GROUP BY author.id, posts.id ");
            params.put("id", Long.parseLong(value));
        } else if (Objects.equals(option, "recent")) {
            query.append("GROUP BY author.id, posts.id ");
            query.append("ORDER BY posts.id DESC ");
        } else if (Objects.equals(option, "popular")) {
            query.append("GROUP BY author.id, posts.id ");
            query.append("ORDER BY posts.views DESC ");
        } else {
            throw new GetPostException("Unknown option: " + option);
        }
        Query q = em.createNativeQuery(query.toString(), Tuple.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue());
        }
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        List<Tuple> dbResp = q.getResultList();
        log.info("dbResp: {}", dbResp);
        return mapTupleToPostResponseDao(dbResp);
    }

    @Override
    public long countPostByOption(String option, String value, long idUser) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM posts ");
        Map<String, Object> params = new HashMap<>();

        if (Objects.equals(option, "bookmark")) {
            query.append("JOIN bookmarks b ON b.id_post = posts.id ")
                    .append("JOIN users u ON u.id = b.id_user ")
                    .append("WHERE u.id = :id ");
            params.put("id", idUser);
        } else if (Objects.equals(option, "tag")) {
            query.append("JOIN post_tags pt ON pt.id_post = posts.id ")
                    .append("JOIN tags t ON t.id = pt.id_tag ")
                    .append("WHERE t.id = :id ");
            params.put("id", Long.parseLong(value));
        } else if (Objects.equals(option, "author")) {
            query.append("WHERE posts.id_author = :id ");
            params.put("id", Long.parseLong(value));
        } else if (Objects.equals(option, "recent")) {
            query.append("WHERE posts.views >= 0 ");
        } else if (Objects.equals(option, "popular")) {
            query.append("WHERE posts.views >= 0 ");
        } else {
            throw new GetPostException("Unknown option: " + option);
        }

        Query q = em.createNativeQuery(query.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            q.setParameter(entry.getKey(), entry.getValue());
        }

        Object result = q.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0;
    }

    @Override
    public void delete(Long id) {
        postJpa.deleteById(id);
    }

    private List<PostResponseDao> mapTupleToPostResponseDao(List<Tuple> dbResp) {
        List<PostResponseDao> result = new ArrayList<>();
        for (Tuple tuple : dbResp) {
            PostResponseDao temp = new PostResponseDao();
            temp.setId(tuple.get("id", Integer.class));
            temp.setTitle(tuple.get("title", String.class));
            temp.setContent(tuple.get("content", String.class));
            temp.setViews(tuple.get("views", Integer.class));
            temp.setCommentCount(tuple.get("commentCount", Integer.class));
            temp.setAuthor(tuple.get("author", String.class));
            temp.setTags(tuple.get("tags", String.class));
            temp.setCreatedAt(tuple.get("createdAt", Timestamp.class).toString());
            temp.setUpdatedAt(tuple.get("updatedAt", Timestamp.class).toString());
            result.add(temp);
        }
        return result;
    }
}
