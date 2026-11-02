CREATE DATABASE IF NOT EXISTS uvgqueue
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE uvgqueue;

-- =========================================
-- TABLA USUARIO
-- =========================================

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    correo VARCHAR(150) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    tipo ENUM(
        'ESTUDIANTE',
        'COLABORADOR'
    ) NOT NULL DEFAULT 'ESTUDIANTE'
);

-- =========================================
-- TABLA RESTAURANTE
-- =========================================

CREATE TABLE IF NOT EXISTS restaurante (
    id_restaurante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    ubicacion VARCHAR(150) NOT NULL,
    estado ENUM(
        'ABIERTO',
        'CERRADO'
    ) NOT NULL DEFAULT 'CERRADO',
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL
);

-- =========================================
-- TABLA REPORTE
-- =========================================

CREATE TABLE IF NOT EXISTS reporte (
    id_reporte INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_restaurante INT NOT NULL,
    cantidad_personas INT NOT NULL,
    tiempo_espera INT NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_cantidad_personas
        CHECK (cantidad_personas >= 0),

    CONSTRAINT chk_tiempo_espera
        CHECK (tiempo_espera >= 0),

    CONSTRAINT fk_reporte_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_reporte_restaurante
        FOREIGN KEY (id_restaurante)
        REFERENCES restaurante(id_restaurante)
);

-- =========================================
-- TABLA FAVORITO
-- =========================================

CREATE TABLE IF NOT EXISTS favorito (
    id_usuario INT NOT NULL,
    id_restaurante INT NOT NULL,

    PRIMARY KEY (id_usuario, id_restaurante),

    CONSTRAINT fk_favorito_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_favorito_restaurante
        FOREIGN KEY (id_restaurante)
        REFERENCES restaurante(id_restaurante)
        ON DELETE CASCADE
);

-- =========================================
-- TABLA REPORTES_CIERRE
-- =========================================

CREATE TABLE IF NOT EXISTS reporte_cierre (
    id_reporte_cierre INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_restaurante INT NOT NULL,
    confirmacion ENUM('ABIERTO', 'CERRADO') NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario),

    FOREIGN KEY (id_restaurante)
        REFERENCES restaurante(id_restaurante)
);

-- =========================================
-- RESTAURANTES INICIALES
-- =========================================

INSERT IGNORE INTO restaurante (
    nombre, ubicacion, estado, hora_apertura, hora_cierre
) VALUES
('Gitane', 'Cafetería CIT', 'ABIERTO', '06:00:00', '24:00:00'),
('GoGreen', 'Cafetería CIT', 'ABIERTO', '06:00:00', '24:00:00'),
('Mixtas Frankfurt', 'Cafetería CIT', 'ABIERTO', '06:00:00', '24:00:00'),
('Panitos', 'Cafetería CIT', 'ABIERTO', '06:00:00', '24:00:00'),
('&Cafe', 'CIT 6', 'ABIERTO', '06:00:00', '24:00:00'),
('Barista', 'Patio CIT', 'ABIERTO', '06:00:00', '24:00:00'),
('Golden Harvest', 'Puerta 6', 'ABIERTO', '06:00:00', '24:00:00'),
('Sarita', 'Edificio F', 'ABIERTO', '06:00:00', '24:00:00');