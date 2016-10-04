-- users
INSERT INTO USER (ID, username, password,role, first_name,last_name, category, email, language)
VALUES(0, 'admin','password','ADMIN','admnistrator','admnistrator', 'ADMINISTRATOR', 'admin@ownspec.com', 'en');

INSERT INTO USER (ID, username, password,role, first_name,last_name, category, email)
VALUES(1, 'lyrold','$2a$08$KemzMdJ3dYol9J/MyZrvUOR0JuJZCXzcXb9ptOWggUv63dW1Mrx/i','USER','Lyrold-Boris','Careto', 'DEVELOPER', 'lyrold.c@ownpsec.com');

INSERT INTO USER (ID,  username, password,role, first_name,last_name, email)
VALUES(2, 'bruno','$2a$08$0B/AHx5MUfXxDuFt7gLgyuIix5qRXleYlDt0MX1tEwTzLPH4vA.xW','USER','Bruno','Ramos', 'bruno.r@ownspec.com');

INSERT INTO USER (ID, username, password,role, first_name,last_name, email)
VALUES(3, 'guillaume','$2a$08$NUpo1R9lkXgmhAD8tHOcXOLkjn3qj7KiUZXAlUCSql55L7tilip3q','USER','Guillaume','Beisel', 'guillaume.b@ownspec.com');


-- Projects
-- INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('My first dummy project', 'first project content');
-- INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Second dummy project', 'second project content');
-- INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Third dummy project', 'Third project content');
-- INSERT INTO PROJECT (TITLE, DESCRIPTION) VALUES('Ownspec project', 'shuuuuutttt');

-- Components
-- INSERT INTO COMPONENT (ID, TITLE,file_path, created_date, CREATED_USER_ID, TYPE, CURRENT_STATUS)
-- VALUES(1, 'My first dummy component', 'dummy/gitrepo/components/req1.html', '2016-01-01 12:10:10', 0 , 'REQUIREMENT' , 'OPEN');
--
-- INSERT INTO WORKFLOW_STATUS (ID, STATUS, COMPONENT_ID, FIRST_GIT_REFERENCE,LAST_GIT_REFERENCE)
-- VALUES(1, 'DRAFT' , 1 , 'FOO', 'FOO');
--
-- INSERT INTO WORKFLOW_STATUS (ID, STATUS, COMPONENT_ID, FIRST_GIT_REFERENCE,LAST_GIT_REFERENCE)
-- VALUES(2, 'OPEN' , 1 , 'FOO', 'FOO');
--
--
-- INSERT INTO COMPONENT (TITLE,created_date, CREATED_USER_ID,TYPE, CURRENT_STATUS)
-- VALUES('Second dummy component', '2016-01-01 12:10:10', 0, 'TEMPLATE', 'OPEN');
--
-- INSERT INTO COMPONENT(TITLE,created_date, CREATED_USER_ID,TYPE, CURRENT_STATUS)
-- VALUES('Third dummy component', '2016-01-01 12:10:10', 0, 'DOCUMENT', 'OPEN');
--
-- INSERT INTO COMPONENT (TITLE,created_date, CREATED_USER_ID,TYPE, CURRENT_STATUS)
-- VALUES('Ownspec COMPONENT', '2016-01-01 12:10:10', 0, 'COMPONENT', 'OPEN');
--
--
