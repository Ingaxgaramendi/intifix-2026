-- ============================================================================
-- Catálogo inicial de especialidades técnicas.
-- Idempotente: ON CONFLICT (nombre) DO NOTHING, así puede re-aplicarse sin
-- duplicar ni fallar si alguna ya existe.
-- ============================================================================

INSERT INTO table_especialidades (nombre, descripcion) VALUES
    ('Laptops y computadoras',      'Reparación y mantenimiento de laptops y PC de escritorio'),
    ('Celulares y tablets',         'Reparación de smartphones, tablets y cambio de pantallas'),
    ('Refrigeradoras y congeladoras','Reparación de sistemas de refrigeración doméstica'),
    ('Lavadoras y secadoras',       'Reparación de lavadoras, secadoras y centros de lavado'),
    ('Televisores y pantallas',     'Reparación de TV LED, OLED, Smart TV y monitores'),
    ('Aire acondicionado',          'Instalación y mantenimiento de equipos de climatización'),
    ('Microondas y hornos',         'Reparación de microondas, hornos y cocinas eléctricas'),
    ('Electrodomésticos de cocina', 'Licuadoras, batidoras, freidoras y similares'),
    ('Instalaciones eléctricas',    'Cableado, tableros, tomacorrientes e iluminación'),
    ('Redes e internet',            'Configuración de redes, routers, Wi-Fi y cableado de datos'),
    ('Cámaras de seguridad',        'Instalación y configuración de CCTV y videovigilancia'),
    ('Impresoras y multifuncionales','Reparación y mantenimiento de impresoras y escáneres'),
    ('Consolas de videojuegos',     'Reparación de PlayStation, Xbox, Nintendo y controles'),
    ('Audio y video',               'Equipos de sonido, parlantes, amplificadores y proyectores')
ON CONFLICT (nombre) DO NOTHING;
