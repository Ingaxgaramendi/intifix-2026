package com.intifix.modules.technicians.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "table_especialidades")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Especialidad {

    @Id
    @Column(name = "id_especialidad")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idEspecialidad;

    @Column(name = "nombre", nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
}
