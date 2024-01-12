# Java-user-interface
## DATABASE :  
**The database is already implemented with H2**. Once you run the project the files will be automatically create.  
It is located in your "root/\~/" (for windows it's : "C:/\~/").  
The data in  the following tables are here as example. For this example you'll find the case of a Group Project with 1 Team assigned. The team is composed of 2  students.
### Student  
| id | code | firstName | lastName | password | accountCreation |
|:--:|:----:|:---------:|:--------:|:--------:|:---------------:|
|  A |  abc |    fred   |  mercury |  ******  |     Created     |
|  B |  def |   michel  | polnaref |  ******  |    NotCreated   |  
### Group  
| id |     name   | student |
|:--:|:----------:|:-------:|
|  D |   BroTeam  |   A:B:  |  
### Team  
| id | teammate |  submit  |
|:--:|:--------:|:--------:|
|  C |    D:    | Pending: |  
### Project  
| id |       description       |    name    |  type |
|:--:|:-----------------------:|:----------:|:-----:|
|  C | Create a Web Design app | Web Design | Group |  

## Architecture of class dependencies
- Project
  - Project Team (Interface)
    - Team
      - Student
    - Student  
  
## Dependencies : 
- Prime Faces : 12.0.0
- Java Server Faces : 2.2.18
- JDK : 17
- Tomcat : 9.0.84
# TODO : Remaining List 
- Add the login Page (Kilyan)
- Finish the implementation of Team (Kilyan)
- Finish the implementation of a Team Project (Robin)
- Student View for submission of files (or directly when files are put on submission folder, so no Student View needed)
- Manage import of Students via CSV file
- Manage import of Projects via CSV file
- Manage import of Teams via CSV file
- Manage Export of Project
- Manage Export of Students
- Manage Export of Teams
- Send password via Email when an account is create (maybe too hard to manage)
- **Create folder for Group project**
- **Create folder for Individual project**
- **Create folder for Submission project**
- **Creation of user**
- **Creation of userGroup**
- **Manage Firewall rule (See if it's useful)**

**The bold tasks needed a working Linux server.**  
**NB:** The bold tasks can be realize using @Schedule in a @Singleton bean and asking periodically the Database to create user / userGroup / folders.
