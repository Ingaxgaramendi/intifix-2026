package com.intifix.modules.chat.mapper;

import com.intifix.modules.chat.dto.response.AdjuntoResponse;
import com.intifix.modules.chat.dto.response.MensajeResponse;
import com.intifix.modules.chat.entity.MensajeDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MensajeMapper {

    MensajeResponse toResponse(MensajeDocument document);

    AdjuntoResponse toAdjuntoResponse(MensajeDocument.Adjunto adjunto);
}
