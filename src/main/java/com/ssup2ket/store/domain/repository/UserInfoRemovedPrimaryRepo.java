package com.ssup2ket.store.domain.repository;

import com.ssup2ket.store.domain.model.UserInfoRemoved;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRemovedPrimaryRepo extends JpaRepository<UserInfoRemoved, UUID> {}
