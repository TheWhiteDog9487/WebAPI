package xyz.thewhitedog9487.WebAPI.Data.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

}