-- Projects
INSERT INTO PROJECT (TITLE, DESCRIPTION,CREATION_DATE) VALUES('My first dummy project', 'first project description', '2016-01-12 23:51:32.341');
INSERT INTO PROJECT (TITLE, DESCRIPTION,CREATION_DATE) VALUES('Second dummy project', 'second project description', CURRENT_TIMESTAMP());
INSERT INTO PROJECT (TITLE, DESCRIPTION,CREATION_DATE) VALUES('Third dummy project', 'Third project description', '2016-05-29 23:51:32.341');
INSERT INTO PROJECT (TITLE, DESCRIPTION,CREATION_DATE) VALUES('Ownspec project', 'shuuuuutttt', CURRENT_TIMESTAMP());


-- Requirements
INSERT INTO REQUIREMENT (TITLE, DESCRIPTION,html_description_path,git_reference ,CREATION_DATE,IS_EDITABLE, IS_PRIVATE) VALUES(
'My first dummy requirement', 'first requirement description','target/dummyRepository/req1.html','master', '2016-01-12 23:51:32.341',true,false);
INSERT INTO REQUIREMENT (TITLE, DESCRIPTION,CREATION_DATE,IS_EDITABLE, IS_PRIVATE) VALUES(
'Second dummy requirement', 'second requirement description', CURRENT_TIMESTAMP(),true,false);
INSERT INTO REQUIREMENT (TITLE, DESCRIPTION,CREATION_DATE,IS_EDITABLE, IS_PRIVATE) VALUES(
'Third dummy requirement', 'Third requirement description', '2016-05-29 23:51:32.341',true,false);
INSERT INTO REQUIREMENT (TITLE, DESCRIPTION,CREATION_DATE,IS_EDITABLE, IS_PRIVATE) VALUES(
'Ownspec requirement', 'shuuuuutttt', CURRENT_TIMESTAMP(),true,false);


-- users
INSERT INTO USER (username, password,role, first_name,last_name)
VALUES('admin','password','ADMIN','admnistrator','admnistrator');

INSERT INTO USER (username, password,role, first_name,last_name)
VALUES('lyrold','$2a$08$KemzMdJ3dYol9J/MyZrvUOR0JuJZCXzcXb9ptOWggUv63dW1Mrx/i','USER','Lyrold-Boris','Careto');

INSERT INTO USER (username, password,role, first_name,last_name)
VALUES('bruno','$2a$08$0B/AHx5MUfXxDuFt7gLgyuIix5qRXleYlDt0MX1tEwTzLPH4vA.xW','USER','Bruno','Ramos');

INSERT INTO USER (username, password,role, first_name,last_name)
VALUES('guillaume','$2a$08$NUpo1R9lkXgmhAD8tHOcXOLkjn3qj7KiUZXAlUCSql55L7tilip3q','USER','Guillaume','Beisel');
