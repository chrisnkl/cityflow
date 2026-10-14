package com.chrisnkl.cfbackend.infrastructure.persistence.repository;

import com.chrisnkl.cfbackend.infrastructure.persistence.entity.report.ReportEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaReportRepository extends JpaRepository<ReportEntity, UUID> {

    @Query(
            value = """
                SELECT r
                FROM ReportEntity r
                JOIN FETCH r.category c
                WHERE 6371.0 * ACOS(
                    COS(RADIANS(:latitude)) * COS(RADIANS(r.latitude)) *
                    COS(RADIANS(r.longitude) - RADIANS(:longitude)) +
                    SIN(RADIANS(:latitude)) * SIN(RADIANS(r.latitude))
                ) <= COALESCE(c.radius, 5.0)
                AND (:email IS NULL OR r.createdBy.email = :email)
                ORDER BY 6371.0 * ACOS(
                    COS(RADIANS(:latitude)) * COS(RADIANS(r.latitude)) *
                    COS(RADIANS(r.longitude) - RADIANS(:longitude)) +
                    SIN(RADIANS(:latitude)) * SIN(RADIANS(r.latitude))
                )
                """,
            countQuery = """
                SELECT COUNT(r)
                FROM ReportEntity r
                JOIN r.category c
                WHERE 6371.0 * ACOS(
                    COS(RADIANS(:latitude)) * COS(RADIANS(r.latitude)) *
                    COS(RADIANS(r.longitude) - RADIANS(:longitude)) +
                    SIN(RADIANS(:latitude)) * SIN(RADIANS(r.latitude))
                ) <= COALESCE(c.radius, 5.0)
                AND (:email IS NULL OR r.createdBy.email = :email)
                """
    )
    Page<ReportEntity> findNearbyReports(
            @Param("email") String email,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM ReportEntity r
            JOIN FETCH r.category c
            WHERE c.enabled = TRUE
              AND (r.createdBy IS NULL OR LOWER(r.createdBy.email) <> LOWER(:email))
              AND 6371.0 * ACOS(LEAST(1.0, GREATEST(-1.0,
                    COS(RADIANS(:latitude)) * COS(RADIANS(r.latitude)) *
                    COS(RADIANS(r.longitude) - RADIANS(:longitude)) +
                    SIN(RADIANS(:latitude)) * SIN(RADIANS(r.latitude))
              ))) <= c.radius
            ORDER BY r.id
            """)
    List<ReportEntity> findNearbyReportsForNotifications(
            @Param("email") String email,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT report FROM ReportEntity report WHERE report.id = :reportId")
    java.util.Optional<ReportEntity> findByIdForNotificationUpdate(@Param("reportId") UUID reportId);
}