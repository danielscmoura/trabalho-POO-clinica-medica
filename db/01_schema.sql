CREATE SCHEMA IF NOT EXISTS clinica_medica;

USE clinica_medica;

CREATE TABLE IF NOT EXISTS profissionais (
   
   id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
   nome VARCHAR(50) NOT NULL,
   email VARCHAR(50)

) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pacientes (

   id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
   nome VARCHAR(50) NOT NULL,
   email VARCHAR(50) NOT NULL

) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS consultas(

    id_consulta INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_profissional INT NOT NULL,
    
    CONSTRAINT fk_consulta_paciente
       FOREIGN KEY (id_paciente) REFERENCES pacientes(id),
	CONSTRAINT fk_consulta_profissional
       FOREIGN KEY (id_profissional) REFERENCES profissionais(id)

) ENGINE = InnoDB;