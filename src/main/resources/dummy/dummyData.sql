-- users
INSERT INTO "user" (ID, username, password,role, first_name,last_name, category, email)
VALUES(0, 'admin','$2a$06$5uTQvTwHAHkVqUaVakAB6ui0ljYU5R0W5Qzjhj9UIVXs0LHIKCzda','ADMIN','admnistrator','admnistrator', 'ADMINISTRATOR', 'admin@ownspec.com');

INSERT INTO "user" (ID, username, password,role, first_name,last_name, email,category)
VALUES(1, 'lyrold','$2a$08$KemzMdJ3dYol9J/MyZrvUOR0JuJZCXzcXb9ptOWggUv63dW1Mrx/i','USER',
'Lyrold-Boris','Careto', 'lyrold.c@ownpsec.com','Developer');

INSERT INTO "user" (ID,  username, password,role, first_name,last_name, email,category)
VALUES(2, 'b.ramos@ownspec.com','$2a$06$5uTQvTwHAHkVqUaVakAB6ui0ljYU5R0W5Qzjhj9UIVXs0LHIKCzda','USER',
'Bruno','Ramos', 'b.ramos@ownspec.com', 'Analyst');

INSERT INTO "user" (ID, username, password,role, first_name,last_name, email,category)
VALUES(3, 'g.beisel@ownspec.com','$2a$06$5uTQvTwHAHkVqUaVakAB6ui0ljYU5R0W5Qzjhj9UIVXs0LHIKCzda','USER',
'Guillaume','Beisel', 'g.beisel@ownspec.com', 'Tester');

INSERT INTO "user" (ID, username, password,role, first_name,last_name, email,category)
VALUES(4, 'n.labrot@ownspec.com','$2a$06$5uTQvTwHAHkVqUaVakAB6ui0ljYU5R0W5Qzjhj9UIVXs0LHIKCzda','USER',
'Nicolas','Labrot', 'n.labrot@ownspec.com','Developer');

INSERT INTO "user" (ID, username, password,role, first_name,last_name, email,category)
VALUES(5, 'l.careto@ownspec.com','$2a$06$5uTQvTwHAHkVqUaVakAB6ui0ljYU5R0W5Qzjhj9UIVXs0LHIKCzda','USER',
'Lyrold','Careto', 'l.careto@ownspec.com', 'Developer');


-- Projects
Insert into "project"(id,title, description, created_user_id, manager_id)
values(1, 'JPMichel', 'Green standardization project', 5, 5);

Insert into "project"(id,title, description, created_user_id, manager_id)
values(2, 'OS-center', 'Req and spec centralization module', 4, 3);

Insert into "project"(id,title, description, created_user_id, manager_id)
values(3, 'OS-validation', 'Req test and validation module', 2, 4);

Insert into "project"(id,title, description, created_user_id, manager_id)
values(4, 'TravailZen', 'Reprise impression', 5, 5);