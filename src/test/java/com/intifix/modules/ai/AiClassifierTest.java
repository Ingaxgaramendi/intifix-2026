package com.intifix.modules.ai;

import com.intifix.modules.ai.entity.CategoriaDiagnostico;
import com.intifix.modules.ai.service.AiClassifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiClassifierTest {

    @Test
    void classify_detectsHardware() {
        var result = AiClassifier.classify("La pantalla no enciende y la batería se infla");
        assertEquals(CategoriaDiagnostico.HARDWARE, result.categoria());
    }

    @Test
    void classify_detectsSoftware() {
        var result = AiClassifier.classify("Windows no inicia después de actualizar un driver");
        assertEquals(CategoriaDiagnostico.SOFTWARE, result.categoria());
    }
}
