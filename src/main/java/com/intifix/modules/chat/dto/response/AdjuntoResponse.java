package com.intifix.modules.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjuntoResponse {
    private String url;
    private String nombreArchivo;
    private String tipoMime;
    private Long tamanoBytes;
}
