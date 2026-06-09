package com.intifix.modules.ai.service;

import com.intifix.modules.ai.entity.CategoriaDiagnostico;
import com.intifix.modules.technicians.entity.EspecialidadTecnico;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class AiClassifier {

    private AiClassifier() {
    }

    public static ClassificationResult classify(String problema) {
        String text = problema.toLowerCase(Locale.ROOT);
        CategoriaDiagnostico categoria;
        BigDecimal confianza;
        String resumen;

        if (containsAny(text, "pantalla", "teclado", "bateria", "hardware", "ram", "disco")) {
            categoria = CategoriaDiagnostico.HARDWARE;
            confianza = bd(88);
            resumen = "Probable incidencia de hardware detectada por patrones físicos/periféricos.";
        } else if (containsAny(text, "virus", "software", "windows", "app", "login", "driver")) {
            categoria = CategoriaDiagnostico.SOFTWARE;
            confianza = bd(91);
            resumen = "Probable incidencia de software detectada por patrones de sistema/aplicación.";
        } else {
            categoria = CategoriaDiagnostico.MANTENIMIENTO;
            confianza = bd(76);
            resumen = "Probable necesidad de mantenimiento preventivo o limpieza general.";
        }

        Map<EspecialidadTecnico, BigDecimal> sugerencias = new LinkedHashMap<>();
        switch (categoria) {
            case HARDWARE -> {
                sugerencias.put(EspecialidadTecnico.HARDWARE, bd(95));
                sugerencias.put(EspecialidadTecnico.MANTENIMIENTO, bd(55));
            }
            case SOFTWARE -> {
                sugerencias.put(EspecialidadTecnico.SOFTWARE, bd(93));
                sugerencias.put(EspecialidadTecnico.REDES, bd(45));
            }
            case MANTENIMIENTO -> {
                sugerencias.put(EspecialidadTecnico.MANTENIMIENTO, bd(90));
                sugerencias.put(EspecialidadTecnico.HARDWARE, bd(40));
            }
        }
        return new ClassificationResult(categoria, confianza, resumen, sugerencias);
    }

    private static boolean containsAny(String text, String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private static BigDecimal bd(int value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    public record ClassificationResult(
            CategoriaDiagnostico categoria,
            BigDecimal confianza,
            String resumen,
            Map<EspecialidadTecnico, BigDecimal> sugerencias
    ) {
    }
}
