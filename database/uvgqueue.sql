CREATE DATABASE IF NOT EXISTS uvgqueue;

USE uvgqueue;


-- =========================================
-- TABLA USUARIO
-- =========================================

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol ENUM(
        'ESTUDIANTE',
        'ENCARGADO',
        'ADMIN'
    ) NOT NULL
);


-- =========================================
-- TABLA RESTAURANTE
-- =========================================

CREATE TABLE IF NOT EXISTS restaurante (
    id_restaurante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(150) NOT NULL,
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);


-- =========================================
-- TABLA FILA
-- =========================================

CREATE TABLE IF NOT EXISTS fila (
    id_fila INT AUTO_INCREMENT PRIMARY KEY,
    id_restaurante INT NOT NULL,
    estado ENUM(
        'ABIERTA',
        'PAUSADA',
        'CERRADA'
    ) NOT NULL DEFAULT 'CERRADA',
    fecha DATE NOT NULL,

    FOREIGN KEY (id_restaurante)
        REFERENCES restaurante(id_restaurante)
);


-- =========================================
-- TABLA TURNO
-- =========================================

CREATE TABLE IF NOT EXISTS turno (
    id_turno INT AUTO_INCREMENT PRIMARY KEY,
    id_fila INT NOT NULL,
    id_usuario INT NOT NULL,
    numero_turno INT NOT NULL,

    estado ENUM(
        'ESPERANDO',
        'LLAMADO',
        'ATENDIDO',
        'AUSENTE',
        'CANCELADO'
    ) NOT NULL DEFAULT 'ESPERANDO',

    fecha_hora DATETIME
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_fila)
        REFERENCES fila(id_fila),

    FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
);


-- =========================================
-- RESTAURANTES INICIALES
-- =========================================

INSERT INTO restaurante
(nombre, ubicacion, hora_apertura, hora_cierre, activo)
VALUES
('Gitane', 'Cafeteria CIT', '07:00:00', '17:00:00', TRUE),
('GoGreen', 'Cafeteria CIT', '07:00:00', '17:00:00', TRUE),
('Mixtas Frankfourt', 'Cafeteria CIT', '07:00:00', '17:00:00', TRUE),
('Panitos', 'Cafeteria CIT', '07:00:00', '17:00:00', TRUE),
('&Cafe', 'CIT 6', '07:00:00', '17:00:00', TRUE),
('Barista', 'Patio CIT', '07:00:00', '17:00:00', TRUE),
('Golden Harvest', 'Puerta 6', '07:00:00', '17:00:00', TRUE),
('Sarita', 'Edificio F', '07:00:00', '17:00:00', TRUE);