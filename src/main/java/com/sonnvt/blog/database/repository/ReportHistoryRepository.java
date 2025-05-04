package com.sonnvt.blog.database.repository;

import com.sonnvt.blog.database.projection.ReportProjection;

public interface ReportHistoryRepository {
    void create(Long idUser, Long idPost);
    ReportProjection getReportHistoryById(Long idUser, Long id);
}
