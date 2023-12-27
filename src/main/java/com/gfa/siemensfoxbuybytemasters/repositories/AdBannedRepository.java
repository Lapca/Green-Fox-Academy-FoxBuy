package com.gfa.siemensfoxbuybytemasters.repositories;

import com.gfa.siemensfoxbuybytemasters.models.AdBanned;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdBannedRepository extends JpaRepository<AdBanned, Long> {
    List<AdBanned> findAllByUserID(UUID id);
}
