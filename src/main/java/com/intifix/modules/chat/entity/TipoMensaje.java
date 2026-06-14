package com.intifix.modules.chat.entity;

/**
 * Tipo de contenido de un mensaje. Los binarios nunca se guardan en Mongo:
 * para tipos de archivo solo se almacena la URL del objeto (Cloudinary/S3/MinIO).
 */
public enum TipoMensaje {
    TEXTO,
    IMAGEN,
    VIDEO,
    AUDIO,
    PDF
}
