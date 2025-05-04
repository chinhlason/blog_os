package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.ReportHistory;
import com.sonnvt.blog.database.projection.ReportProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportHistoryJpa extends JpaRepository<ReportHistory, Long> {
    @Query(value = """
            SELECT\s
                    rh.id_user AS idUser,
                    rh.id_post AS idPost,
                    (SELECT COUNT(*) FROM report_history rh WHERE rh.id_user = :idUser AND rh.id_post = :postId) AS count,
                    rh.created_at AS createdAt
                FROM report_history rh
                WHERE rh.id_user = :idUser AND rh.id_post = :postId ORDER BY rh.id DESC LIMIT 1
   \s""", nativeQuery = true)
    ReportProjection getReportProjectionByIdUserAndIdPost(@Param("idUser") Long idUser, @Param("postId") Long postId);
}
