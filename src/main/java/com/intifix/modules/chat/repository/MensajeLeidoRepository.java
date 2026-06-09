package com.intifix.modules.chat.repository;

import com.intifix.modules.chat.document.MensajeLeidoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MensajeLeidoRepository extends MongoRepository<MensajeLeidoDocument, String> {
    boolean existsByMensajeIdAndUsuarioId(String mensajeId, String usuarioId);
}
