# liquibase-starter

## Gradle
### Liquibase Plugin
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
>         main {
>             url "jdbc:mariadb://localhost:3306/liquibase_test"
>             username "liquibase"
>             password "liquibase"
>             changeLogFile "src/main/resources/db/changelog/changelog-1.0.yml"
>         }
>     }
>     runList = "main"
> } 
> ```
