package com.ssup2ket.store.domain.repository;

import com.ssup2ket.store.domain.entity.Inbox;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxPrimaryRepo extends JpaRepository<Inbox, UUID> {}
