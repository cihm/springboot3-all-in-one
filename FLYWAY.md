以下是Spring Boot專案中使用flyway-core的詳細範例，示範如何設定與使用Flyway來管理資料庫版本遷移。

1. 加入Maven依賴
   在pom.xml中加入flyway-core依賴：

xml
<dependency>
<groupId>org.flywaydb</groupId>
<artifactId>flyway-core</artifactId>
<version>最新版建議使用Spring Boot搭配的flyway版本</version>
</dependency>
2. 在 application.properties 或 application.yml 設定 Flyway
   以 application.properties 為例：

text
spring.flyway.enabled=true
spring.flyway.locations=classpath:/db/migration
spring.flyway.baseline-on-migrate=true
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
或 application.yml 範例：

text
spring:
datasource:
url: jdbc:mysql://localhost:3306/your_database
username: root
password: your_password
driver-class-name: com.mysql.cj.jdbc.Driver
flyway:
enabled: true
locations: classpath:/db/migration
baseline-on-migrate: true
3. 建立Migration SQL檔案
   在資源路徑中建立資料夾src/main/resources/db/migration，並新增SQL檔案，檔名遵守Flyway的命名規則：

前綴：V，表示版本Migration

版本號：整數或用點(.)分隔的版本號，如1、2、2.1

連接符：使用兩個底線__

描述：任意描述文字（可使用底線_分隔）

副檔名：.sql

範例：
V1__create_person_table.sql

sql
CREATE TABLE PERSON (
ID INT NOT NULL,
NAME VARCHAR(100) NOT NULL,
PRIMARY KEY (ID)
);
V2__insert_sample_data.sql

sql
INSERT INTO PERSON (ID, NAME) VALUES (1, 'Axel');
INSERT INTO PERSON (ID, NAME) VALUES (2, 'Mr. Foo');
INSERT INTO PERSON (ID, NAME) VALUES (3, 'Ms. Bar');
4. 啟動Spring Boot應用時，自動執行Migration
   只要啟動Spring Boot專案，Flyway會自動執行db/migration目錄下還未執行的版本SQL，並自動在資料庫建立flyway_schema_history表來記錄執行狀態，避免重複執行。

5. 進階：透過Java程式碼手動控制Flyway
   若需程式碼中控制Flyway遷移，也可在@Configuration建立Flyway Bean，比如：

java
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }
}
這樣在Spring容器啟動時，migrate()會自動執行，達到資料庫遷移目的。

總結
加入flyway-core依賴

配置資料庫連線與Flyway參數

在src/main/resources/db/migration放版本化SQL檔案（命名必須按規則）

啟動Spring Boot時自動執行資料庫版本遷移

可以用Java程式碼自訂Flyway行為

此流程適合Spring Boot的Flyway版本管理，方便可靠且自動化.​


目前程式只有這樣為啥能跑 spring.datasource.url=jdbc:postgresql://localhost:5432/postgres spring.datasource.username=postgres spring.datasource.password=postgres
幫助改善我們的產品
我們生成了兩個版本的答案。您更喜歡哪一個？
您提供的Spring Boot資料庫連線設定：

text
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
能夠正常運行的原因包括以下幾點：

1. 預設資料庫名稱與連線
   jdbc:postgresql://localhost:5432/postgres指的是連接本機的PostgreSQL伺服器上，預設的postgres資料庫。

PostgreSQL預設初始會有一個名稱為postgres的資料庫，通常系統安裝後就存在，不需自行建立。

2. 預設的使用者帳號
   預設的使用者名稱postgres是PostgreSQL安裝時會自帶的超級使用者，擁有完整權限。

密碼postgres是您設定的（或預設），只要密碼匹配即可成功認證。

3. Spring Boot自動配置
   Spring Boot 利用這些參數自動建立資料庫連線池(DataSource)。

如果資料庫postgres存在且帳密正確，連線就能成功。

因此，這組參數能正常跑是因為：

資料庫postgres本來就存在（系統預設）

使用者postgres也存在並有權限

密碼與此帳號相符

如果使用其他資料庫名稱或使用者不存在，就會連線失敗。這是正常行為


為什麼你的程式要多這幾行
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.flyway.enabled=true
spring.flyway.locations=classpath:/db/migration
spring.flyway.baseline-on-migrate=true

這幾行設定主要是為了讓Spring Boot和Flyway能更正確和有控制地執行資料庫遷移，以下說明各行的用途：

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

指定JDBC驅動程式類別，告訴Spring Boot應用程式使用哪個驅動來連接資料庫。

有時Spring Boot可以自動推斷，但明確指定可以避免連線驅動錯誤，特別是使用MySQL時。

spring.flyway.enabled=true

明確啟用Flyway的功能，雖然預設是啟用的，但寫上這行可以清楚表示專案使用Flyway進行版本控制。

spring.flyway.locations=classpath:/db/migration

指定Flyway搜尋版本遷移腳本SQL檔案的路徑，預設是classpath:db/migration，寫出來讓人一目了然，也方便變更位置。

spring.flyway.baseline-on-migrate=true

這個參數用來讓Flyway在現有資料庫已有資料但沒有版本控制紀錄時，將當前版本基準化，避免第一次遷移因已有表格出錯。

也適用於將既有資料庫導入Flyway管理時，確保遷移順利進行。

總結來說，這些設定用來確保資料庫連線正確（driver）、明確開啟Flyway、告訴Flyway去哪找SQL遷移檔、以及保護初次執行遷移時的資料庫狀態，提升穩定性與可控性


URL
http://localhost:8080/?
http://localhost:8080/?/todos
