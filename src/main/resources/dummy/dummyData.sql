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
Insert into "project"(id,created_date, title, description, created_user_id, manager_id)
values(10,'2016-10-17 17:34:17.782000', 'JPMichel', 'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis', 5, 5);

Insert into "project"(id,created_date,title, description, created_user_id, manager_id)
values(20,'2016-11-18 17:34:17.782000', 'OS-center', 'Nemo enim ipsam voluptatem quia voluptas sit aspernatur', 4, 3);

Insert into "project"(id,created_date,title, description, created_user_id, manager_id)
values(30,'2016-12-19 09:35:17.782000', 'OS-validation', 'Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur', 2, 4);

Insert into "project"(id,created_date,title, description, created_user_id, manager_id)
values(40,'2016-12-19 10:37:17.782000', 'TravailZen', 'At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint', 5, 5);


-- -- User's projects
INSERT INTO user_project (id, created_date, favorite, last_modified_date, visited_time, project_id, user_id)
VALUES (10, '2016-12-17 17:34:17.782000', false, '2016-12-17 17:40:52.062000', 2, 10, 5);
INSERT INTO user_project (id, created_date, favorite, last_modified_date, visited_time, project_id, user_id)
VALUES (20, '2016-12-17 17:34:21.551000', false, '2016-12-17 17:34:21.551000', 1, 20, 5);
INSERT INTO user_project (id, created_date, favorite, last_modified_date, visited_time, project_id, user_id)
VALUES (30, '2016-12-17 17:34:23.520000', true, '2016-12-17 17:34:23.520000', 1, 30, 5);
INSERT INTO user_project (id, created_date, favorite, last_modified_date, visited_time, project_id, user_id)
VALUES (40, '2016-12-17 17:34:26.155000', false, '2016-12-17 17:34:26.155000', 1, 40, 5);

INSERT INTO user_project (id, created_date, favorite, last_modified_date, visited_time, project_id, user_id)
VALUES (50, '2016-12-17 17:34:17.782000', false, '2016-12-17 17:40:52.062000', 2, 10, 1);
INSERT INTO user_project (id, created_date, favorite, last_modified_date, visited_time, project_id, user_id)
VALUES (60, '2016-12-17 17:34:21.551000', false, '2016-12-17 17:34:21.551000', 1, 20, 1);
INSERT INTO user_project (id, created_date, favorite, last_modified_date, visited_time, project_id, user_id)
VALUES (70, '2016-12-17 17:34:23.520000', true, '2016-12-17 17:34:23.520000', 1, 30, 1);
INSERT INTO user_project (id, created_date, favorite, last_modified_date, visited_time, project_id, user_id)
VALUES (80, '2016-12-17 17:34:26.155000', false, '2016-12-17 17:34:26.155000', 1, 40, 1);

