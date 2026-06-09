package com.intifix.modules.ai.service;

import com.intifix.modules.ai.document.RecomendacionIaDocument;
import com.intifix.modules.ai.dto.AiDiagnosisRequest;
import com.intifix.modules.ai.dto.AiDiagnosisResponse;
import com.intifix.modules.ai.entity.DiagnosticoIa;
import com.intifix.modules.ai.entity.SugerenciaEspecialidad;
import com.intifix.modules.ai.repository.DiagnosticoIaRepository;
import com.intifix.modules.ai.repository.RecomendacionIaMongoRepository;
import com.intifix.modules.ai.repository.SugerenciaEspecialidadRepository;
import com.intifix.modules.technicians.entity.EspecialidadTecnico;
import com.intifix.modules.services.entity.Servicio;
import com.intifix.modules.services.repository.ServicioRepository;
import com.intifix.shared.exception.ApiException;
import com.intifix.shared.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AiDiagnosisService {

    private final ServicioRepository servicioRepository;
    private final DiagnosticoIaRepository diagnosticoRepository;
    private final SugerenciaEspecialidadRepository sugerenciaRepository;
    private final RecomendacionIaMongoRepository recomendacionMongoRepository;

    public AiDiagnosisService(
            ServicioRepository servicioRepository,
            DiagnosticoIaRepository diagnosticoRepository,
            SugerenciaEspecialidadRepository sugerenciaRepository,
            RecomendacionIaMongoRepository recomendacionMongoRepository
    ) {
        this.servicioRepository = servicioRepository;
        this.diagnosticoRepository = diagnosticoRepository;
        this.sugerenciaRepository = sugerenciaRepository;
        this.recomendacionMongoRepository = recomendacionMongoRepository;
    }

    @Transactional
    public AiDiagnosisResponse diagnose(AiDiagnosisRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        Servicio servicio = servicioRepository.findByIdServicioAndIdCliente(req.servicioId(), userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

        AiClassifier.ClassificationResult result = AiClassifier.classify(req.problema());

        DiagnosticoIa diag = new DiagnosticoIa();
        diag.setServicioId(servicio.getIdServicio());
        diag.setCategoria(result.categoria());
        diag.setConfianza(result.confianza());
        diag.setResumen(result.resumen());
        diagnosticoRepository.save(diag);

        List<AiDiagnosisResponse.SpecialtySuggestion> suggestions = new ArrayList<>();
        for (Map.Entry<EspecialidadTecnico, BigDecimal> entry : result.sugerencias().entrySet()) {
            SugerenciaEspecialidad s = new SugerenciaEspecialidad();
            s.setDiagnosticoId(diag.getId());
            s.setEspecialidad(entry.getKey());
            s.setScore(entry.getValue());
            sugerenciaRepository.save(s);
            suggestions.add(new AiDiagnosisResponse.SpecialtySuggestion(entry.getKey(), entry.getValue()));
        }

        servicioRepository.save(servicio);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("categoria", result.categoria().name());
        payload.put("confianza", result.confianza());
        payload.put("sugerencias", suggestions);
        recomendacionMongoRepository.save(new RecomendacionIaDocument(
                servicio.getIdServicio().toString(),
                diag.getId().toString(),
                payload
        ));

        return new AiDiagnosisResponse(
                diag.getId(),
                diag.getCategoria(),
                diag.getConfianza(),
                diag.getResumen(),
                suggestions
        );
    }

}
