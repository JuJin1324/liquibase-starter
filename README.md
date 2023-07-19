# liquibase-starter

## Install
### macOS
> 설치 Command: `brew install liquibase`  

### Linux
> Ubuntu 설치 Command: `sudo snap install liquibase`

---

## Gradle
### Liquibase Plugin
> build.gradle  
> build.gradle 의 liquibase.activities.main.changeLogFile 의 경로는 application.yml 과 동일하게 맞춰준다.   
> ```groovy
> plugins {
>     id 'org.liquibase.gradle' version '2.2.0'
> }
> 
> dependencies {
>     // Liquibase
>     //implementation 'org.liquibase:liquibase-core:4.16.1'
>     liquibaseRuntime 'org.liquibase:liquibase-core:4.16.1'
>     liquibaseRuntime 'org.liquibase:liquibase-groovy-dsl:3.0.2'
>     liquibaseRuntime 'info.picocli:picocli:4.6.1'
>     liquibaseRuntime 'mysql:mysql-connector-java:8.0.31'
> }
> 
> liquibase {
>     activities {
>        local {
>            contexts 'local'
>            changelogFile "src/main/resources/db/changelog/db.changelog-master.xml"
>            driver "com.mysql.cj.jdbc.Driver"
>            url "jdbc:mysql://localhost:3312/liquibase_local?serverTimezone=UTC&characterEncoding=UTF-8"
>            username "liquibase_starter"
>            password "liquibase_starter"
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
> `liquibaseRuntime 'org.liquibase:liquibase-core:4.16.1'` 이 아닌   
> `implementation 'org.liquibase:liquibase-core:4.16.1'` 로 선언되어야 한다.  

### context
> `changeSet` 태그 및 `include` 태그에 `context` 애트리뷰트를 추가해서 컨텍스트에 따른 관리 가능  
> ex. local, dev, prod 로 관리 가능

### gradle update command
> **Update**  
> databasechangelog 테이블의 changelog 보다 db.changelog-master.xml 의 내용이 앞서가는 경우
> 앞서가는 버전에 대해서 DBMS 에 반영한다.   
> `gradle update`
>
> **UpdateSql**  
> databasechangelog 테이블의 changelog 보다 db.changelog-master.xml 의 내용이 앞서가는 경우
> 앞서가는 버전에 대해서 DBMS 에 반영하기 위한 쿼리의 내용을 콘솔에 출력한다. DBMS 에 업데이트는 반영되지 않는다.
> 사용자가 출력된 업데이트 쿼리로 직접 작업하길 원할 때 사용한다.  
> `gradle updateSql`

### rollback
> liquibase 의 changeSet 로 변경한 부분을 다시 되돌리는게 rollback 이다.  
> 하지만 rollback 커맨드를 실행한다고 해서 liquibase 에서 changeSet 의 내용을 자동으로 rollback 해주지 않는다.  
> rollback 을 위해서는 먼저 changeSet 에 rollback 커맨드 실행시 실행할 내용을 `<rollback>...</rollback>`
> 태그 안에 정의해야한다.   
> ex. USER 테이블에 PHONE_NO 칼럼을 추가하는 changeSet 안에 rollback 태그로 롤백 시 실행되어야할 sql 을 정의하였다.
> ```xml
> <changeSet id="changelog-3.0" author="jujin">
>     <comment>회원 폰 번호 칼럼 추가</comment>
>     <sql>
>         ALTER TABLE USER
>             ADD COLUMN PHONE_NO VARCHAR(32) COMMENT '회원 폰 번호', ALGORITHM=INSTANT
>     </sql>
>     <rollback>
>         ALTER TABLE USER DROP COLUMN PHONE_NO, ALGORITHM=INSTANT
>     </rollback>
> </changeSet>
> ``` 

### gradle rollback command
> rollback 은 다음과 같이 Gradle 의 rollbackCount Task 를 통해서 rollback 한다.
> `gradle rollbackCount -PliquibaseCommandValue=1`

### 참조사이트
> [Passing parameter and value to Gradle liquibase plugin](https://stackoverflow.com/questions/33620287/passing-parameter-and-value-to-gradle-liquibase-plugin)

---

## db.changelog-master.xml
> 가장 먼저 마스터 파일인 `db.changelog-master.xml` 을 생성한 후에 각 DB 테이블 별로 xml 파일을 만들어서 `changelog-master.xml` 에 `<include>` 한다.  
> db.changelog-master.xml
> ```xml
> <?xml version="1.0" encoding="utf-8"?>
> <databaseChangeLog
> xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
> xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
> xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.6.xsd">
> 
>     <property name="usingAutoIncrement" value="true"/>
>     <property name="now" value="now(3)"/>
>     <property name="uuid" value="uuid()"/>
> 
>     <include file="db.changelog-1.0-CreateUser.xml" relativeToChangelogFile="true"/>
>     <include file="db.changelog-2.0-CreateUserProfile.xml" relativeToChangelogFile="true"/>
>     <include file="db.changelog-3.0_AlterUser.xml" relativeToChangelogFile="true"/>
>     <include file="db.changelog-3.1-AlterUser.xml" relativeToChangelogFile="true"/>
>     <include file="db.changelog-4.0-InsertUser.xml" relativeToChangelogFile="true"/>
>     <include file="db.changelog-5.0-InsertUserProfile.xml" relativeToChangelogFile="true"/>
> </databaseChangeLog>
> ```

### property
> changelog-master.xml 파일에 property 태그로 변수화하여 include 한 파일에 사용한다.    
> 여기서 사용한 `usingAutoIncrement` 는 autoIncrement 사용 여부를 property 에 정의하여 변수화하였다.  

### include
> db.changelog-master.xml 파일 안에 직접 changeSet 를 정의하지 않고 파일로 나눠서 include 시키는 방식으로 관리한다.  
> relativeToChangelogFile="true" 를 주어서 `file` 값에 db.changelog-master.xml 파일을 기준으로 상대경로로 파일명만 적는다.  

---

## ChangeSet 파일
### 파일명 형식
> `db.changelog-<version>-<쿼리 제목>.xml`    
> version 은 1.0 -> 2.0 -> 3.0 으로 앞의 숫자만 늘린다.    
> version 에서 뒤의 숫자가 늘어나는 경우는 파일 내부에서 ChangeSet 이 2개 이상인 경우에 첫번째 ChangeSet 에는 1.0, 두번째 ChangeSet 에는 1.1 로
> 늘려나가게 관리한다.  

### changeSet
> 변경점이 일어난 부분이다. 일반적으로 하나의 ChangeSet 에는 하나의 쿼리만 사용하자.  
> `id`: ChangeSet 의 ID 이다. "changelog-1.0" 과 같이 파일명으로 사용한 버전을 적시한다. 하나의 파일에 ChangeSet 이 2개 이상인 경우 
> 각 id 에 "changelog-1.0", "changelog-1.1", "changelog-1.2", ... 이런식으로 마이너 버전을 늘려나간다.  
> `author`: 작성자의 이름을 적는다.  

### comment
> liquibase 를 통해서 쿼리를 실행하게 되면 `databasechangelog` 테이블이 생성되게 된다.  
> databasechangelog 테이블의 comment 칼럼에 `comment` 태그 안에 적은 내용이 담기게 된다.  
> ```xml
> <changeSet id="changelog-X.0" author="jujin">
>     <comment>회원 프로필 테이블 추가</comment>
>     ...
> </changeSet>
> ```

### createTable
> 예시
> ```xml
> <changeSet id="changelog-X.0" author="jujin">
>     <createTable tableName="user_profile">
>         <column name="id" type="bigint" autoIncrement="${usingAutoIncrement}" remarks="회원 프로필 ID">
>             <constraints primaryKey="true" nullable="false"/>
>         </column>
>         <column name="nickname" type="varchar(32)" remarks="프로필 닉네임"/>
>         <column name="imageUrl" type="text" remarks="프로필 이미지 URL"/>
>         <column name="user_id" type="bigint" remarks="회원 ID">
>             <constraints nullable="false"/>
>         </column>
>     </createTable>
> </changeSet>
> ```
> column 태그로 칼럼을 정의한다.  
> primary key 지정의 경우 column 태그 안에 constraints 태그를 통해서 지정한다.    
> 칼럼의 not null 지정의 경우 column 태그 안에 constraints 태그를 통해서 지정한다.  
>
> **참조사이트**  
> [Liquibase - createTable](https://docs.liquibase.com/change-types/create-table.html)


### addForeignKeyConstraint
> 예시
> ```xml
> <changeSet id="changelog-X.0" author="jujin">
>     <createTable tableName="user_profile">
>         ...
>     </createTable>
> 
>     <addForeignKeyConstraint constraintName="user_profile_user_id_fk"
>                              baseTableName="user_profile"
>                              baseColumnNames="user_id"
>                              referencedTableName="user"
>                              referencedColumnNames="id"/>
> </changeSet>
> ```
> foreign key 지정의 경우 column 태그에 직접 지정할 순 없고 addForeignKeyConstraint 태그를 사용하여 지정한다.  
>  
> **참조사이트**  
> [Liquibase - addForeignKeyConstraint](https://docs.liquibase.com/change-types/add-foreign-key-constraint.html)


### createIndex 
> 예시
> ```xml
> <changeSet id="changelog-X.0" author="jujin">
>     <createIndex indexName="user_uk_uuid" schemaName="liquibase_local"
>                  tableName="user" unique="true">
>         <column name="uuid"/>
>     </createIndex>
>     <createIndex indexName="user_ix_name" schemaName="liquibase_local"
>                  tableName="user">
>         <column name="name"/>
>     </createIndex>
> </changeSet>
> ```
> 
> **참조사이트**  
> [Liquibase - createIndex](https://docs.liquibase.com/change-types/create-index.html)

### sql
> 예시
> ```xml
> <changeSet id="changelog-X.0" author="jujin">
>     <sql>
>         ALTER TABLE USER
>             ADD COLUMN PHONE_NO VARCHAR(20) COMMENT '회원 폰 번호', ALGORITHM=INSTANT;
>     </sql>
> </changeSet>
> ```
>
> **참조사이트**  
> [Liquibase - sql](https://docs.liquibase.com/change-types/sql.html)
 

### rollback
> sql 태그의 rollback 예시
> ```xml
> <changeSet id="changelog-X.0" author="jujin">
>     <sql>
>         ALTER TABLE USER
>             ADD COLUMN PHONE_NO VARCHAR(20) COMMENT '회원 폰 번호', ALGORITHM=INSTANT;
>     </sql> 
>     <rollback>
>         ALTER TABLE USER DROP COLUMN EMAIL_ADDRESS, ALGORITHM=INSTANT;
>     </rollback>
> </changeSet>
> ```
>
> createTable 태그의 rollback 예시  
> ```xml
> <changeSet id="changelog-X.0" author="jujin">
>     <createTable tableName="user">
>         ...
>     </createTable>
>     <rollback>
>         <dropTable tableName="user"/>
>     </rollback>
> </changeSet>
> ```
>
> **참조사이트**  
> [Liquibase - rollback](https://docs.liquibase.com/workflows/liquibase-community/automatic-custom-rollbacks.html)

### sqlFile
> 예제
> ```xml
> <changeSet id="changelog-X.0" author="jujin">
>     <sqlFile path="data/local/4.0-InsertUser.sql"
>              relativeToChangelogFile="true"/>
> </changeSet>
> ```
> relativeToChangelogFile="true" 를 주어서 `path` 값에 changeSet 을 담은 파일을 기준으로 상대경로로 적는다.  
>
> **참조사이트**  
> [Liquibase - sqlFile](https://docs.liquibase.com/change-types/sql-file.html)
