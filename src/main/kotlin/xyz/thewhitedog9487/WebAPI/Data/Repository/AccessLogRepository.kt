package xyz.thewhitedog9487.WebAPI.Data.Repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog

interface AccessLogRepository: JpaRepository<AccessLog, Long>, JpaSpecificationExecutor<AccessLog>