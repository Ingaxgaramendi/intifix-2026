package com.intifix.modules.chat.repository;

import com.intifix.modules.chat.entity.ConversacionDocument;
import com.intifix.modules.chat.entity.TipoConversacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversacionRepository extends MongoRepository<ConversacionDocument, UUID> {

    boolean existsByIdServicio(UUID idServicio);

    Optional<ConversacionDocument> findByIdServicio(UUID idServicio);

    /**
     * Inbox de un usuario: todas las conversaciones donde participa como cliente
     * o como técnico, ordenadas por actividad reciente (el orden lo aporta el
     * {@link Pageable}). Para inbox usar sort por 'actualizadoEn' desc.
     */
    Page<ConversacionDocument> findByIdClienteOrIdTecnico(UUID idCliente, UUID idTecnico, Pageable pageable);

    Optional<ConversacionDocument> findByIdClienteAndIdTecnicoAndTipo(UUID idCliente, UUID idTecnico, TipoConversacion tipo);
}
