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
> dependencies {
>     // Liquibase
>     liquibaseRuntime 'org.liquibase:liquibase-core:4.15.0'
>     liquibaseRuntime 'org.liquibase:liquibase-groovy-dsl:3.0.2'
>     liquibaseRuntime 'info.picocli:picocli:4.6.3'
>     liquibaseRuntime 'org.liquibase.ext:liquibase-hibernate5:4.15.0'
>     liquibaseRuntime 'org.mariadb.jdbc:mariadb-java-client'
>     liquibaseRuntime sourceSets.main.compileClasspath
>     liquibaseRuntime sourceSets.main.runtimeClasspath
>     liquibaseRuntime sourceSets.main.output
> }
> 
> liquibase {
>     activities {
>         local {
>             contexts 'local'
>             driver "org.mariadb.jdbc.Driver"
>             url "jdbc:mariadb://localhost:3306/liquibase_test"
>             username "liquibase"
>             password "liquibase"
>             // changeLogFile 의 경로는 application.yml 과 동일하게 맞춰준다.
>             changeLogFile "classpath:db/changelog/db.changelog-master.xml"
>         }
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

## changelog
### 관리
> flyway 와 다르게 liquibase 는 각 DB 테이블 별로 xml 을 만들어서 버전 관리가 가능하다.      
> 각 테이블 명으로 xml 을 만든 후 xml 파일 안에서 changeSet 을 생성해서 버전관리를 한다.  
> 가장 먼저 마스터 파일인 `changelog-master.xml` 을 생성한 후에 각 DB 테이블 별로 xml 파일을 만들어서 `changelog-master.xml`
> 에 `<include>` 한다.

### context
> `changeSet` 태그 및 `include` 태그에 `context` 애트리뷰트를 추가해서 컨텍스트에 따른 관리 가능  
> ex. local, dev, prod 로 관리 가능 

## update
> DB 테이블 xml 에 changeSet 생성 후에 `gradle:liquibase update` 를 통해서 변경 내용을 DB 에 반영하거나
> 서버를 구동하면 DB 에 자동 반영된다. 

## rollback
### 관리
> liquibase 의 changeSet 로 변경한 부분을 다시 되돌리는게 rollback 이다.  
> 하지만 rollback 커맨드를 실행한다고 해서 liquibase 에서 changeSet 의 내용을 자동으로 rollback 해주지 않는다.  
> rollback 을 위해서는 먼저 changeSet 에 rollback 커맨드 실행시 실행할 내용을 `<rollback>...</rollback>` 
> 태그 안에 정의해야한다.   
> ex. USER 테이블에 PHONE_NO 칼럼을 추가하는 changeSet 안에 rollback 태그로 롤백 시 실행되어야할 sql 을 정의하였다. 
> ```xml
> <changeSet id="000-2" author="jujin">
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
