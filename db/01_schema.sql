CREATE SCHEMA IF NOT EXISTS clinica_medica
    DEFAULT CHARACTER SET utf8mb4;

USE clinica_medica;

CREATE TABLE IF NOT EXISTS especialidades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS convenios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    telefone VARCHAR(20)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100),
    telefone VARCHAR(20)
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS profissionais (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    id_especialidade INT NOT NULL,
    CONSTRAINT fk_profissional_especialidade
        FOREIGN KEY (id_especialidade) REFERENCES especialidades(id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS consultas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_profissional INT NOT NULL,
    id_convenio INT NULL,                 
    data_consulta DATE NOT NULL,
    horario TIME NOT NULL,
    situacao VARCHAR(20) NOT NULL DEFAULT 'Agendada',

    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (id_paciente) REFERENCES pacientes(id),
    CONSTRAINT fk_consulta_profissional
        FOREIGN KEY (id_profissional) REFERENCES profissionais(id),
    CONSTRAINT fk_consulta_convenio
        FOREIGN KEY (id_convenio) REFERENCES convenios(id),


    CONSTRAINT uk_profissional_horario
        UNIQUE (id_profissional, data_consulta, horario)
) ENGINE = InnoDB;