# liquibase-starter

## Gradle
### Liquibase Plugin
> build.gradle  
> build.gradle 의 liquibase.activities.main.changeLogFile 의 경로는 application.yml 과 동일하게 맞춰준다.   
> ```groovy
> plugins {
>     id 'org.liquibase.gradle' version '2.1.0'
> }
> 
> task copyResources(type: Copy) {
>     from "${projectDir}/src/main/resources"
>     into "${buildDir}/resources/main"
> }
> compileJava.dependsOn copyResources
> update.dependsOn copyResources
> 
> dependencies {
>     // Liquibase
>     //implementation 'org.liquibase:liquibase-core:4.15.0'
>     liquibaseRuntime 'org.liquibase:liquibase-core:4.15.0'
>     liquibaseRuntime 'org.liquibase:liquibase-groovy-dsl:3.0.2'
>     liquibaseRuntime 'info.picocli:picocli:4.6.3'
>     liquibaseRuntime 'org.liquibase.ext:liquibase-hibernate5:4.15.0'
>     liquibaseRuntime 'mysql:mysql-connector-java:8.0.31'
>     liquibaseRuntime sourceSets.main.compileClasspath
>     liquibaseRuntime sourceSets.main.runtimeClasspath
>     liquibaseRuntime sourceSets.main.output
> }
> 
> liquibase {
>     activities {
>        local {
>            contexts 'local'
>            driver "com.mysql.cj.jdbc.Driver"
>            url "jdbc:mysql://localhost:3312/liquibase_local?serverTimezone=UTC&characterEncoding=UTF-8"
>            username "liquibase_starter"
>            password "liquibase_starter"
>            changeLogFile "classpath:db/changelog/db.changelog-master.xml"
>        }
>     }
>     runList = "local"
> } 
> ```
> 
> application.yml
> ```yaml
> spring:
>     liquibase:
>         change-log: classpath:db/changelog/changelog-master.xml
>         enabled: true
> ``` 
> application.yml 의 `enabled: true` 설정이 적용되려면 build.gradle 의 dependnecies 에서   
> `liquibaseRuntime 'org.liquibase:liquibase-core:4.15.0'` 이 아닌   
> `implementation 'org.liquibase:liquibase-core:4.15.0'` 로 선언되어야 한다.  

### context
> `changeSet` 태그 및 `include` 태그에 `context` 애트리뷰트를 추가해서 컨텍스트에 따른 관리 가능  
> ex. local, dev, prod 로 관리 가능

### update
> DB 테이블 xml 에 changeSet 생성 후에 `gradle update` 를 통해서 변경 내용을 DB 에 반영하거나
> 서버를 구동하면 DB 에 자동 반영된다.

### rollback
> liquibase 의 changeSet 로 변경한 부분을 다시 되돌리는게 rollback 이다.  
> 하지만 rollback 커맨드를 실행한다고 해서 liquibase 에서 changeSet 의 내용을 자동으로 rollback 해주지 않는다.  
> rollback 을 위해서는 먼저 changeSet 에 rollback 커맨드 실행시 실행할 내용을 `<rollback>...</rollback>`
> 태그 안에 정의해야한다.   
> ex. USER 테이블에 PHONE_NO 칼럼을 추가하는 changeSet 안에 rollback 태그로 롤백 시 실행되어야할 sql 을 정의하였다.
> ```xml
> <changeSet id="003" author="jujin">
>     <sql dbms="mariadb, mysql">
>         ALTER TABLE USER
>             ADD COLUMN PHONE_NO VARCHAR(32) COMMENT '회원 폰 번호', ALGORITHM=INSTANT
>     </sql>
>     <rollback>
>         ALTER TABLE USER DROP COLUMN PHONE_NO, ALGORITHM=INSTANT
>     </rollback>
> </changeSet>
> ``` 

### gradle rollbackCount command
> rollback 은 다음과 같이 Gradle 의 rollbackCount Task 를 통해서 rollback 한다.
> `gradle rollbackCount -PliquibaseCommandValue=1`

### 참조사이트
> [Passing parameter and value to Gradle liquibase plugin](https://stackoverflow.com/questions/33620287/passing-parameter-and-value-to-gradle-liquibase-plugin)

---

## changelog
### 관리
> 가장 먼저 마스터 파일인 `changelog-master.xml` 을 생성한 후에 각 DB 테이블 별로 xml 파일을 만들어서 `changelog-master.xml` 에 `<include>` 한다.  
> changelog-master.xml
> ```xml
> <?xml version="1.0" encoding="utf-8"?>
> <databaseChangeLog
>   xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
>   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.6.xsd">
> 
>     <property name="autoIncrement" value="true" dbms="mariadb, mysql" />
>     <property name="now" value="now(6)" dbms="mariadb, mysql"/>
>     <property name="uuid" value="uuid()" dbms="mariadb, mysql"/>
> 
>     <include file="001_CreateUser.xml" relativeToChangelogFile="true" />
>     <include file="002_CreateUserProfile.xml" relativeToChangelogFile="true" />
>     <include file="003_AlterUser.xml" relativeToChangelogFile="true" />
>     <include file="004_AlterUser.xml" relativeToChangelogFile="true" />
>     <include file="005_InsertUserProfile.xml" relativeToChangelogFile="true" />
>     <include file="006_InsertUser.xml" relativeToChangelogFile="true" />
> </databaseChangeLog>
> ```
