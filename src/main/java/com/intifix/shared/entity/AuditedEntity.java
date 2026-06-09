package com.intifix.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditedEntity {

    @CreatedDate
    @Column(name = "creado_en", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime creadoEn;

    @LastModifiedDate
    @Column(name = "actualizado_en", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime actualizadoEn;

    // Si usas Spring Security, puedes descomentar esto para auditoría de usuarios:
    // @CreatedBy
    // @Column(name = "creado_por", updatable = false)
    // private String creadoPor;
}
