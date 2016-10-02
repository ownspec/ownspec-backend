-- users
INSERT INTO USER (username, password,role, first_name,last_name, category, email, language)
VALUES( 'admin','password','ADMIN','admnistrator','admnistrator', 'ADMINISTRATOR', 'admin@ownspec.com', 'en');

INSERT INTO USER (username, password,role, first_name,last_name, category, email)
VALUES( 'lyrold','$2a$08$KemzMdJ3dYol9J/MyZrvUOR0JuJZCXzcXb9ptOWggUv63dW1Mrx/i','USER','Lyrold-Boris','Careto', 'DEVELOPER', 'lyrold.c@ownpsec.com');

INSERT INTO USER ( username, password,role, first_name,last_name, email)
VALUES( 'bruno','$2a$08$0B/AHx5MUfXxDuFt7gLgyuIix5qRXleYlDt0MX1tEwTzLPH4vA.xW','USER','Bruno','Ramos', 'bruno.r@ownspec.com');

INSERT INTO USER ( username, password,role, first_name,last_name, email)
VALUES( 'guillaume','$2a$08$NUpo1R9lkXgmhAD8tHOcXOLkjn3qj7KiUZXAlUCSql55L7tilip3q','USER','Guillaume','Beisel', 'guillaume.b@ownspec.com');


-- Projects
INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('My first dummy project', 'first project content');
INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Second dummy project', 'second project content');
INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Third dummy project', 'Third project content');
INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Ownspec project', 'shuuuuutttt');

-- Components
INSERT INTO COMPONENT (TITLE,file_path, created_date, CREATED_USER_ID, TYPE, project_id) VALUES(
'My first dummy component', 'dummy/gitrepo/components/req1.html', '2016-01-01 12:10:10', 1 , 'REQUIREMENT',1);

INSERT INTO COMPONENT (TITLE,created_date, CREATED_USER_ID,TYPE) VALUES(
'Second dummy component', '2016-01-01 12:10:10', 3, 'TEMPLATE');

INSERT INTO COMPONENT(TITLE,file_path,created_date, CREATED_USER_ID,TYPE) VALUES(
'Third dummy component', 'dummy/gitrepo/components/spec1.html', '2016-01-01 12:10:10', 2, 'DOCUMENT');

INSERT INTO COMPONENT (TITLE,created_date, CREATED_USER_ID,TYPE) VALUES(
'Ownspec COMPONENT', '2016-01-01 12:10:10', 1, 'COMPONENT');


