package com.cedacri.emailnotification.repository;

import com.cedacri.emailnotification.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    ProcessedEvent findByMessageId(String messageId);

}
