package com.sonnvt.blog.database.repository.implement;

import com.sonnvt.blog.database.entity.ReportHistory;
import com.sonnvt.blog.database.jpa.ReportHistoryJpa;
import com.sonnvt.blog.database.projection.ReportProjection;
import com.sonnvt.blog.database.repository.ReportHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportHistoryImplement implements ReportHistoryRepository {
    private final ReportHistoryJpa reportHistoryJpa;

    @Override
    public void create(Long idUser, Long idPost) {
        reportHistoryJpa.save(ReportHistory.builder()
                .idUser(idUser)
                .idPost(idPost)
                .build());
    }

    @Override
    public ReportProjection getReportHistoryById(Long idUser, Long idPost) {
        return reportHistoryJpa.getReportProjectionByIdUserAndIdPost(idUser, idPost);
    }
}
