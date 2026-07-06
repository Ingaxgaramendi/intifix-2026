-- Add intermediate state: technician finalized, waiting for client confirmation
ALTER TYPE estado_servicio ADD VALUE IF NOT EXISTS 'PENDIENTE_CONFIRMACION' BEFORE 'FINALIZADO';
