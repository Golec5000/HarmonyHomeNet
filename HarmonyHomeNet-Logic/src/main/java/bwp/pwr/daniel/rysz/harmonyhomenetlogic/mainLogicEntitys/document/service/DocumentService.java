package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.entity.Document;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentService {
    void save(Document document) throws IOException;
    Optional<Document> findById(UUID id);
    List<Document> findAll();
    void deleteById(UUID id);
}
