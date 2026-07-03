package com.intifix.modules.users.mapper;

import com.intifix.modules.users.dto.request.ActualizarClienteRequest;
import com.intifix.modules.users.dto.request.CrearClienteRequest;
import com.intifix.modules.users.dto.response.ClienteDetalleResponse;
import com.intifix.modules.users.dto.response.ClienteResponse;
import com.intifix.modules.users.entity.PerfilCliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClienteMapper {

    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "idUbicacion", ignore = true)
    PerfilCliente toEntity(CrearClienteRequest request);

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "idUbicacion", ignore = true)
    void updateEntityFromDto(ActualizarClienteRequest request, @MappingTarget PerfilCliente entity);

    @Mapping(target = "estadoUsuario", ignore = true)
    ClienteResponse toResponse(PerfilCliente entity);

    @Mapping(target = "tieneDniRuc", expression = "java(entity.getDniRuc() != null && !entity.getDniRuc().isBlank())")
    @Mapping(target = "tieneFotoPerfil", expression = "java(entity.getFotoPerfilUrl() != null && !entity.getFotoPerfilUrl().isBlank())")
    ClienteDetalleResponse toDetalleResponse(PerfilCliente entity);
}
