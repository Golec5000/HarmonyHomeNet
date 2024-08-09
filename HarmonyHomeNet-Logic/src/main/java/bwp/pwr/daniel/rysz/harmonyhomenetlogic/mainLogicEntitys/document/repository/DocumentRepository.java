package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
}
