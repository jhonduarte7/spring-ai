package com.venefast.springboot.springai.app.repositories;

import com.venefast.springboot.springai.app.models.entities.AiInteraction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiInteractionRepository extends JpaRepository<AiInteraction, Long> {

    List<AiInteraction> findTop10ByOrderByCreatedAtDesc();
}
