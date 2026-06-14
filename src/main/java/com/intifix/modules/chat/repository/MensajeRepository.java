package com.intifix.modules.chat.repository;

import com.intifix.modules.chat.entity.EstadoMensaje;
import com.intifix.modules.chat.entity.MensajeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MensajeRepository extends MongoRepository<MensajeDocument, UUID> {

    /**
     * Historial paginado de una conversación (scroll infinito). Ordenar por
     * 'creadoEn' desc vía el Pageable.
     */
    Page<MensajeDocument> findByIdConversacion(UUID idConversacion, Pageable pageable);

    /**
     * No leídos para un usuario: mensajes de la conversación que NO envió él y
     * cuyo estado aún no es LEIDO.
     */
    long countByIdConversacionAndIdEmisorNotAndEstadoNot(UUID idConversacion, UUID idEmisor, EstadoMensaje estado);

    /**
     * Mensajes pendientes de marcar como leídos para un usuario (los que recibió
     * y aún no están en estado LEIDO).
     */
    List<MensajeDocument> findByIdConversacionAndIdEmisorNotAndEstadoNot(UUID idConversacion, UUID idEmisor, EstadoMensaje estado);

    void deleteByIdConversacion(UUID idConversacion);
}
