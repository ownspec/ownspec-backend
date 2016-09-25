-- Projects
INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('My first dummy project', 'first project content');
INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Second dummy project', 'second project content');
INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Third dummy project', 'Third project content');
INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Ownspec project', 'shuuuuutttt');

-- Components
INSERT INTO COMPONENT (TITLE,file_path) VALUES(
'My first dummy component', 'dummy/gitrepo/requirement/req1.html');
INSERT INTO COMPONENT (TITLE) VALUES(
'Second dummy component');
INSERT INTO COMPONENT(TITLE) VALUES(
'Third dummy component');
INSERT INTO COMPONENT (TITLE) VALUES(
'Ownspec COMPONENT');


-- users
INSERT INTO USER (username, password,role, first_name,last_name)
VALUES('admin','password','ADMIN','admnistrator','admnistrator');

INSERT INTO USER (username, password,role, first_name,last_name, category)
VALUES('lyrold','$2a$08$KemzMdJ3dYol9J/MyZrvUOR0JuJZCXzcXb9ptOWggUv63dW1Mrx/i','USER','Lyrold-Boris','Careto', 'DEVELOPER');

INSERT INTO USER (username, password,role, first_name,last_name)
VALUES('bruno','$2a$08$0B/AHx5MUfXxDuFt7gLgyuIix5qRXleYlDt0MX1tEwTzLPH4vA.xW','USER','Bruno','Ramos');

INSERT INTO USER (username, password,role, first_name,last_name)
VALUES('guillaume','$2a$08$NUpo1R9lkXgmhAD8tHOcXOLkjn3qj7KiUZXAlUCSql55L7tilip3q','USER','Guillaume','Beisel');
