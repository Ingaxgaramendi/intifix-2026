package com.intifix.modules.auth.dto;

import com.intifix.modules.users.entity.RolUsuario;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class RegisterRequest {
    private String correo;
    private String clave;
    private String nombresCompletos;
    private String dniRuc;
    private String telefono;
    private Set<RolUsuario> roles;
    private String dniFrontalUrl;
    private String dniTraseroUrl;
    private String antecedentePenalUrl;
    private String certificadoTecnicoUrl;
    private Integer experienciaAnios;
    private BigDecimal tarifaBase;

    public String getNombre() {
        return nombresCompletos;
    }

    public void setNombre(String nombre) {
        this.nombresCompletos = nombre;
    }

    public String getDni() {
        return dniRuc;
    }

    public void setDni(String dni) {
        this.dniRuc = dni;
    }

    public String getRuc() {
        return dniRuc;
    }

    public void setRuc(String ruc) {
        this.dniRuc = ruc;
    }
}
