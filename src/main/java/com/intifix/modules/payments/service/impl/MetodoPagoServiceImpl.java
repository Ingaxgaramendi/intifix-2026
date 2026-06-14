package com.intifix.modules.payments.service.impl;

import com.intifix.modules.payments.dto.request.CrearMetodoPagoRequest;
import com.intifix.modules.payments.dto.response.MetodoPagoResponse;
import com.intifix.modules.payments.entity.MetodoPago;
import com.intifix.modules.payments.exception.MetodoPagoNoEncontradoException;
import com.intifix.modules.payments.mapper.MetodoPagoMapper;
import com.intifix.modules.payments.repository.MetodoPagoRepository;
import com.intifix.modules.payments.service.interfaces.MetodoPagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final MetodoPagoMapper metodoPagoMapper;

    @Override
    @Transactional
    public MetodoPagoResponse crearMetodoPago(CrearMetodoPagoRequest request) {
        log.info("Creando método de pago: {}", request.getNombre());

        if (metodoPagoRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new MetodoPagoNoEncontradoException("Ya existe un método de pago con el nombre: " + request.getNombre());
        }

        MetodoPago metodoPago = metodoPagoMapper.toEntity(request);
        MetodoPago metodoPagoGuardado = metodoPagoRepository.save(metodoPago);

        log.info("Método de pago creado exitosamente con ID: {}", metodoPagoGuardado.getIdMetodoPago());
        return metodoPagoMapper.toResponse(metodoPagoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPagoResponse obtenerMetodoPagoPorId(UUID idMetodoPago) {
        MetodoPago metodoPago = metodoPagoRepository.findById(idMetodoPago)
                .orElseThrow(() -> new MetodoPagoNoEncontradoException(idMetodoPago));
        return metodoPagoMapper.toResponse(metodoPago);
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPagoResponse obtenerMetodoPagoPorNombre(String nombre) {
        MetodoPago metodoPago = metodoPagoRepository.findByNombre(nombre)
                .orElseThrow(() -> new MetodoPagoNoEncontradoException("Método de pago no encontrado: " + nombre));
        return metodoPagoMapper.toResponse(metodoPago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listarMetodosPago() {
        return metodoPagoRepository.findAll().stream()
                .map(metodoPagoMapper::toResponse)
                .toList();
    }
}
