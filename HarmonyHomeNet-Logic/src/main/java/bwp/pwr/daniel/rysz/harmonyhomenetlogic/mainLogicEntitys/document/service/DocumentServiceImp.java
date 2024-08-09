package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.entity.Document;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImp implements DocumentService{

    private final DocumentRepository documentRepository;

    @Override
    public void save(Document document) throws IOException {
        documentRepository.save(document);
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return documentRepository.findById(id);
    }

    @Override
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        documentRepository.deleteById(id);
    }
}
