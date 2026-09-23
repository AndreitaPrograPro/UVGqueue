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
    ) NOT NULL DEFAULT 'CERRADO'
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
-- RESTAURANTES INICIALES
-- =========================================

INSERT IGNORE INTO restaurante
(nombre, ubicacion, estado)
VALUES
('Gitane', 'Cafetería CIT', 'ABIERTO'),
('GoGreen', 'Cafetería CIT', 'ABIERTO'),
('Mixtas Frankfurt', 'Cafetería CIT', 'ABIERTO'),
('Panitos', 'Cafetería CIT', 'ABIERTO'),
('&Cafe', 'CIT 6', 'ABIERTO'),
('Barista', 'Patio CIT', 'ABIERTO'),
('Golden Harvest', 'Puerta 6', 'ABIERTO'),
('Sarita', 'Edificio F', 'ABIERTO');