/* Populate tables */
INSERT INTO usuarios (id, email, nombre, password, fecha_nacimiento, image) VALUES('1', 'user@ua', 'Usuario Ejemplo', '123', '2001-02-10', 'descarga.jpg');
INSERT INTO tareas (id, titulo, usuario_id, estado) VALUES('1', 'Lavar coche', '1', 'To Do');
INSERT INTO tareas (id, titulo, usuario_id, estado) VALUES('2', 'Renovar DNI', '1', 'In Progress');
INSERT INTO usuarios (id, email, nombre, password, fecha_nacimiento) VALUES('2', 'juan.gutierrez@gmail.com', 'Juan Gutiérrez', '123', '1997-02-20');
INSERT INTO usuarios (id, email, nombre, password, fecha_nacimiento, image) VALUES('3', 'carlos@gmail.com', 'Carlos', '123', '1994-09-20', 'carlos.jpg');
INSERT INTO equipos (id,nombre) VALUES ('1','EQUIPO1');
INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('1', '1');
INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('1', '2');


