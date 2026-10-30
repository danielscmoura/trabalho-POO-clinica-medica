-- =====================================================================
-- 02_dados.sql  -  Carga de dados de demonstração
-- Sistema de Clínica Médica (Trabalho Prático de POO)
--
-- Rodar DEPOIS do 01_schema.sql.
-- Pode ser executado várias vezes: ele apaga os dados e recarrega tudo,
-- reiniciando os IDs (AUTO_INCREMENT) a partir de 1.
-- Todos os dados são fictícios.
-- =====================================================================

USE clinica_medica;

-- ---------------------------------------------------------------------
-- Limpa as tabelas (ordem não importa com as FKs desligadas)
-- ---------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE consultas;
TRUNCATE TABLE profissionais;
TRUNCATE TABLE pacientes;
TRUNCATE TABLE convenios;
TRUNCATE TABLE especialidades;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- ESPECIALIDADES  (ids 1 a 6)
-- ---------------------------------------------------------------------
INSERT INTO especialidades (nome) VALUES
('Clínica Geral'),   -- 1
('Cardiologia'),     -- 2
('Dermatologia'),    -- 3
('Pediatria'),       -- 4
('Ortopedia'),       -- 5
('Ginecologia');     -- 6

-- ---------------------------------------------------------------------
-- CONVÊNIOS  (ids 1 a 5)  -  nomes fictícios
-- Consulta particular = id_convenio NULL na tabela consultas
-- ---------------------------------------------------------------------
INSERT INTO convenios (nome, telefone) VALUES
('Saúde Mais',       '(61) 3000-1001'),  -- 1
('Vida Plena',       '(61) 3000-1002'),  -- 2
('MedCare',          '(61) 3000-1003'),  -- 3
('Bem Estar Saúde',  '(61) 3000-1004'),  -- 4
('Cuidar Bem',       '(61) 3000-1005');  -- 5

-- ---------------------------------------------------------------------
-- PACIENTES  (ids 1 a 10)
-- Alguns sem e-mail (NULL) para testar campos opcionais
-- ---------------------------------------------------------------------
INSERT INTO pacientes (nome, cpf, email, telefone) VALUES
('Maria Silva',         '111.111.111-11', 'maria.silva@exemplo.com',     '(61) 99111-0001'),  -- 1
('João Pereira',        '222.222.222-22', 'joao.pereira@exemplo.com',    '(61) 99111-0002'),  -- 2
('Ana Souza',           '333.333.333-33', 'ana.souza@exemplo.com',       '(61) 99111-0003'),  -- 3
('Carlos Oliveira',     '444.444.444-44', NULL,                          '(61) 99111-0004'),  -- 4
('Fernanda Lima',       '555.555.555-55', 'fernanda.lima@exemplo.com',   '(61) 99111-0005'),  -- 5
('Rafael Costa',        '666.666.666-66', 'rafael.costa@exemplo.com',    NULL),               -- 6
('Juliana Martins',     '777.777.777-77', 'juliana.martins@exemplo.com', '(61) 99111-0007'),  -- 7
('Pedro Almeida',       '888.888.888-88', NULL,                          '(61) 99111-0008'),  -- 8
('Beatriz Rocha',       '999.999.999-99', 'beatriz.rocha@exemplo.com',   '(61) 99111-0009'),  -- 9
('Lucas Fernandes',     '123.456.789-00', 'lucas.fernandes@exemplo.com', '(61) 99111-0010');  -- 10

-- ---------------------------------------------------------------------
-- PROFISSIONAIS  (ids 1 a 8)  -  cada um ligado a uma especialidade
-- ---------------------------------------------------------------------
INSERT INTO profissionais (nome, email, id_especialidade) VALUES
('Dr. Carlos Souza',      'carlos.souza@clinica.exemplo.com',    2),  -- 1 Cardiologia
('Dra. Helena Ribeiro',   'helena.ribeiro@clinica.exemplo.com',  1),  -- 2 Clínica Geral
('Dr. Marcos Teixeira',   'marcos.teixeira@clinica.exemplo.com', 5),  -- 3 Ortopedia
('Dra. Patrícia Gomes',   'patricia.gomes@clinica.exemplo.com',  3),  -- 4 Dermatologia
('Dr. André Barbosa',     'andre.barbosa@clinica.exemplo.com',   4),  -- 5 Pediatria
('Dra. Camila Duarte',    'camila.duarte@clinica.exemplo.com',   6),  -- 6 Ginecologia
('Dr. Ricardo Moreira',   'ricardo.moreira@clinica.exemplo.com', 1),  -- 7 Clínica Geral
('Dra. Larissa Campos',   'larissa.campos@clinica.exemplo.com',  2);  -- 8 Cardiologia

-- ---------------------------------------------------------------------
-- CONSULTAS  (rotina principal)
-- Mistura de consultas passadas (Realizada / Cancelada) e futuras (Agendada),
-- com e sem convênio (NULL = particular).
-- Nenhum profissional tem duas consultas no mesmo dia e horário.
-- ---------------------------------------------------------------------
INSERT INTO consultas (id_paciente, id_profissional, id_convenio, data_consulta, horario, situacao) VALUES
-- Outubro/2026 (já passaram)
(1,  1, 1,    '2026-10-05', '08:00:00', 'Realizada'),
(2,  2, NULL, '2026-10-05', '09:00:00', 'Realizada'),
(3,  4, 2,    '2026-10-08', '10:30:00', 'Realizada'),
(4,  3, 3,    '2026-10-12', '14:00:00', 'Cancelada'),
(5,  6, 1,    '2026-10-15', '08:30:00', 'Realizada'),
(6,  7, NULL, '2026-10-19', '15:00:00', 'Realizada'),
(1,  2, 1,    '2026-10-22', '09:30:00', 'Realizada'),
(7,  5, 4,    '2026-10-26', '11:00:00', 'Cancelada'),

-- Novembro/2026 (agendadas)
(8,  1, 2,    '2026-11-03', '08:00:00', 'Agendada'),
(9,  8, 5,    '2026-11-03', '10:00:00', 'Agendada'),
(10, 2, NULL, '2026-11-05', '14:30:00', 'Agendada'),
(2,  3, 3,    '2026-11-10', '09:00:00', 'Agendada'),
(3,  1, 2,    '2026-11-12', '15:30:00', 'Agendada'),
(5,  4, 1,    '2026-11-17', '13:00:00', 'Agendada'),
(1,  1, 1,    '2026-11-24', '08:00:00', 'Agendada'),
(6,  7, 4,    '2026-11-26', '16:00:00', 'Agendada');

-- Todos os 10 pacientes têm pelo menos uma consulta, então NENHUM deles pode ser
-- excluído (bom para testar a mensagem do erro 1451).
-- Para testar uma exclusão que funciona, cadastre um paciente novo pela tela e exclua-o.
-- O mesmo vale para profissionais, especialidades e convênios que estão em uso.

-- =====================================================================
-- Consultas para conferir a carga (rode uma de cada vez se quiser)
-- =====================================================================

-- Quantidade de registros em cada tabela
-- SELECT 'especialidades' AS tabela, COUNT(*) AS total FROM especialidades
-- UNION ALL SELECT 'convenios',     COUNT(*) FROM convenios
-- UNION ALL SELECT 'pacientes',     COUNT(*) FROM pacientes
-- UNION ALL SELECT 'profissionais', COUNT(*) FROM profissionais
-- UNION ALL SELECT 'consultas',     COUNT(*) FROM consultas;

-- Consultas com INNER JOIN (nomes em vez de IDs)
-- SELECT c.id,
--        pa.nome AS paciente,
--        pr.nome AS profissional,
--        e.nome  AS especialidade,
--        COALESCE(cv.nome, 'Particular') AS convenio,
--        c.data_consulta, c.horario, c.situacao
-- FROM consultas c
-- INNER JOIN pacientes      pa ON c.id_paciente      = pa.id
-- INNER JOIN profissionais  pr ON c.id_profissional  = pr.id
-- INNER JOIN especialidades e  ON pr.id_especialidade = e.id
-- LEFT  JOIN convenios      cv ON c.id_convenio      = cv.id
-- ORDER BY c.data_consulta, c.horario;