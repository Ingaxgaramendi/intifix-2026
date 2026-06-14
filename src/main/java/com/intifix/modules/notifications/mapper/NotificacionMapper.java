package com.intifix.modules.notifications.mapper;

import com.intifix.modules.notifications.dto.response.NotificacionResponse;
import com.intifix.modules.notifications.entity.NotificacionDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    NotificacionResponse toResponse(NotificacionDocument document);
}
