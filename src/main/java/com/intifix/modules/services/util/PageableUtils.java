package com.intifix.modules.services.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Utilidades para sanear {@link Pageable} provenientes de la capa web.
 *
 * <p>El binding por defecto de Spring permite ordenar por CUALQUIER propiedad
 * vía {@code ?sort=...}, lo que expone columnas no indexadas a escaneos costosos
 * y filtra nombres internos de campos. Esta utilidad descarta propiedades de
 * orden no incluidas en una whitelist y cae a un orden por defecto seguro.</p>
 */
public final class PageableUtils {

    private PageableUtils() {
    }

    /**
     * Devuelve un {@link Pageable} cuyo {@link Sort} solo contiene propiedades
     * permitidas. Si tras el filtrado no queda ninguna, aplica {@code porDefecto}.
     *
     * @param pageable   el pageable original (page/size se conservan)
     * @param permitidos nombres de propiedad ordenables admitidos
     * @param porDefecto orden a usar cuando el solicitado es vacío o inválido
     */
    public static Pageable sanitize(Pageable pageable, Set<String> permitidos, Sort porDefecto) {
        Sort solicitado = pageable.getSort();

        Sort filtrado = Sort.by(
            solicitado.stream()
                .filter(order -> permitidos.contains(order.getProperty()))
                .toList()
        );

        Sort efectivo = filtrado.isSorted() ? filtrado : porDefecto;

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), efectivo);
    }
}
