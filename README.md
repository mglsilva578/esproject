
  MYDRIVE  
  
  
  $ mysql -p -u root  
  password: rootroot  
  mysql> GRANT ALL PRIVILEGES ON \*.\* TO 'mydrive'@'localhost' IDENTIFIED BY 'mydriv3' WITH GRANT OPTION;  
  mysql> CREATE DATABASE drivedb;  
  musql> \q  
  
  
  $ git clone https://github.com/tecnico-softeng/es16tg_28-project.git  
  $ mvn clean package exec:java  

