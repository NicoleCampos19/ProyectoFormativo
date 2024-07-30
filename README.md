--TABLA DE LOGIN ENFERMERA--
CREATE TABLE tbEnfermera(
ID_enfermera INT PRIMARY KEY,
Correo VARCHAR2(30) NOT NULL,
Contraseña VARCHAR2(15) NOT NULL
);

CREATE SEQUENCE identity_Enfermera
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE OR REPLACE TRIGGER trg_Enfermera
BEFORE INSERT ON tbEnfermera
FOR EACH ROW
BEGIN
    SELECT identity_Enfermera.NEXTVAL INTO :new.ID_enfermera FROM dual;
END;

INSERT ALL
INTO tbEnfermera(Correo, Contraseña) values ('luisajosefina@gmail.com','LuisaJosefina12' )
SELECT * FROM dual;

--TABLA DE MEDICAMENTO--
CREATE TABLE tbMedicamento(
ID_medicamento INT PRIMARY KEY,
Nombre_medicamento VARCHAR2(30),
Hora_Aplicación VARCHAR2(10) NOT NULL
);

CREATE SEQUENCE identity_medicamento
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE OR REPLACE TRIGGER trg_medicamento
BEFORE INSERT ON tbMedicamento
FOR EACH ROW
BEGIN
    SELECT identity_medicamento.NEXTVAL INTO :new.ID_medicamento FROM dual;
END;

INSERT ALL
INTO tbMedicamento(Nombre_medicamento, Hora_Aplicación) values ('Acetaminofen','03:00 pm')
INTO tbMedicamento(Nombre_medicamento, Hora_Aplicación) values ('Amoxicilina','05:00 pm')
INTO tbMedicamento(Nombre_medicamento, Hora_Aplicación) values ('Ibuprofeno','06:00 pm')
INTO tbMedicamento(Nombre_medicamento, Hora_Aplicación) values ('Metformina','10:00 am')
INTO tbMedicamento(Nombre_medicamento, Hora_Aplicación) values ('Paracetamol','08:00 pm')
SELECT * FROM dual;

--TABLA DE ENFERMEDADES--
CREATE TABLE tbEnfermedad(
ID_enfermedad INT PRIMARY KEY,
Nombre_enfermedad VARCHAR2(30)
);

CREATE SEQUENCE identity_enfermedad
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE OR REPLACE TRIGGER trg_enfermedad
BEFORE INSERT ON tbEnfermedad
FOR EACH ROW
BEGIN
    SELECT identity_enfermedad.NEXTVAL INTO :new.ID_enfermedad FROM dual;
END;

INSERT ALL
INTO tbEnfermedad(Nombre_enfermedad) values ('Diabetes')
INTO tbEnfermedad(Nombre_enfermedad) values ('Cáncer')
INTO tbEnfermedad(Nombre_enfermedad) values ('Infección en la garganta')
INTO tbEnfermedad(Nombre_enfermedad) values ('Bronquitis')
INTO tbEnfermedad(Nombre_enfermedad) values ('Resfriado')
SELECT * FROM dual;

--TABLA DE PACIENTES--
CREATE TABLE tbPacientes(
ID_Paciente INT PRIMARY KEY,
Nombres VARCHAR2(30) NOT NULL,
Apellidos VARCHAR2(30) NOT NULL,
Edad INT NOT NULL,
FechaNacimiento VARCHAR2(10) NOT NULL,
Número_habitación INT NOT NULL,
Número_cama INT NOT NULL,
ID_medicamento INT,
ID_enfermedad INT,
CONSTRAINT fk_medicamento_paciente FOREIGN KEY(ID_medicamento)REFERENCES tbMedicamento(ID_medicamento)ON DELETE CASCADE,
CONSTRAINT fk_enfermedad_paciente FOREIGN KEY(ID_enfermedad)REFERENCES tbEnfermedad(ID_enfermedad)ON DELETE CASCADE
);

INSERT INTO tbPacientes(Nombres, Apellidos, Edad, FechaNacimiento, Número_habitación, Número_cama, ID_Medicamento, ID_Enfermedad) VALUES ('Emily', 'Jacobo', 16, '19/09/2007', 4, 2, 2, 1);
INSERT INTO tbPacientes(Nombres, Apellidos, Edad, FechaNacimiento, Número_habitación, Número_cama, ID_Medicamento, ID_Enfermedad) VALUES ('Santiago Andres', 'Martinez Delgado', 17, '26/06/2007', 1, 4, 4, 2);
INSERT INTO tbPacientes(Nombres, Apellidos, Edad, FechaNacimiento, Número_habitación, Número_cama, ID_Medicamento, ID_Enfermedad) VALUES ('Dennis Alexander', 'Rivas Villalta', 18, '10/02/2006', 2, 1, 3, 3);

CREATE SEQUENCE identity_paciente
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE OR REPLACE TRIGGER trg_paciente
BEFORE INSERT ON tbPacientes
FOR EACH ROW
BEGIN
    SELECT identity_paciente.NEXTVAL INTO :new.ID_Paciente FROM dual;
END;

--TABLA DE DETALLE PACIENTES--
CREATE TABLE tbDetallePaciente(
ID_detalle INT PRIMARY KEY,
ID_Paciente INT,
ID_enfermedad INT,
ID_medicamento INT,
CONSTRAINT fk_paciente_detalle FOREIGN KEY(ID_Paciente)REFERENCES tbPacientes(ID_Paciente)ON DELETE CASCADE,
CONSTRAINT fk_enfermedad_detalle FOREIGN KEY(ID_enfermedad)REFERENCES tbEnfermedad(ID_enfermedad)ON DELETE CASCADE,
CONSTRAINT fk_medicamento_detalle FOREIGN KEY(ID_medicamento)REFERENCES tbMedicamento(ID_medicamento)ON DELETE CASCADE
);

CREATE SEQUENCE identity_detalle_paciente
START WITH 1
INCREMENT BY 1
NOCACHE;

CREATE OR REPLACE TRIGGER trg__detalle_paciente
BEFORE INSERT ON tbDetallePaciente
FOR EACH ROW
BEGIN
    SELECT identity_detalle_paciente.NEXTVAL INTO :new.ID_detalle FROM dual;
END;

SELECT 
m.Nombre_medicamento,
m.Hora_Aplicación
FROM
    tbPacientes p
INNER JOIN 
    tbMedicamento m ON p.ID_Medicamento = m.ID_Medicamento
WHERE p.ID_Paciente = 14;

COMMIT;
