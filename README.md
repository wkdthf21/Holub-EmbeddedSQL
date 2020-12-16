## Holub-SQL 확장 프로젝트 :smile:
---
### Intro
---
Holub-SQL에 디자인 패턴을 적용하여 기능을 확장하는 프로젝트 <br>
Link : https://holub.com/software/holubSQL/index.html
<br>

### Original Function
---
- Database의 Table을 CSV 파일로 Export
- CSV 파일을 Database Table로 Import
- SQL 최소한의 기능 지원

```sql
INSERT INTO IDENTIFIER [LP idList RP] VALUES LP exprList RP
CREATE  DATABASE IDENTIFIER
CREATE  TABLE    IDENTIFIER LP declarations RP
DROP    TABLE    IDENTIFIER
BEGIN    [WORK|TRAN[SACTION]]
COMMIT   [WORK|TRAN[SACTION]]
ROLLBACK [WORK|TRAN[SACTION]]
DUMP
USE     DATABASE IDENTIFIER
UPDATE  IDENTIFIER SET IDENTIFIER EQUAL expr WHERE expr
DELETE  FROM IDENTIFIER WHERE expr
SELECT  idList [INTO identifier] FROM idList [WHERE expr]
```

<br>

### Extension Function
---

- Database의 Table을 HTML 파일로 Export
<p align="center"><img src="/assets/111_xagr5fn4g.png" width="350" height="200"></p>

<br>

- XML Export와 XML Import
<p align="center"><img src="/assets/111_nrbovdy0x.png" width="300" height="300"></p>

<br>

- 기존 Holub-SQL에서 아래 쿼리문 실행 시 발생하는 오류 해결
  <p align="center"><img src="/assets/111.png" width="500" height="30"></p>

<br>

- Visitor 패턴을 이용한 기능 개발
  - Holub-SQL에서 지원하는 SQL Statement Keyword를 Console에 모두 출력하는 기능
  - Holub SQL에 정의된 모든 Token을 Token 종류와 함께 출력하는 기능
<p align="center"><img src="/assets/111_wavoxwk2g.png" width="350" height="400"></p>

<br>

- distinct 키워드 지원
- order by 키워드 지원
```SQL
SELECT [DISTINCT] idList
       [INTO identifier]
       FROM idList [WHERE expr]
       [ORDER BY identifier [ASC | DESC]]
```

<br>

### Usage
---
#### Run Test
테스트 코드를 실행하기 위해서 **프로젝트 폴더 내의 Dbase에 있는 파일들을 c:/dp2020 폴더 안에 복사**해줍니다.
<br>
#### Run Console
src/com/holub/database/jdbc/Console.java 실행

<br>


### Environment
---
- JDK 14.0.1
- Junit5

<br>

### Architecture
---

#### Holub-SQL 전체 구조
<p align="center"><img src="/assets/111_83bdcaio4.png" width="500" height="400"></p>
<br>


#### Data Storage Layer
<p align="center"><img src="/assets/KakaoTalk_20201216_005510106.jpg" width="600" height="700"></p>
<br>


#### SQL Layer
<p align="center"><img src="/assets/KakaoTalk_20201216_005551829.jpg" width="700" height="700"></p>

<br>


#### JDBC Layer
<p align="center"><img src="/assets/KakaoTalk_20201216_005713600.jpg" width="700" height="500"></p>


#### 확장한 부분

- Exporter
  - Builder 패턴과 Template Method 패턴 적용
<p align="center"><img src="/assets/111_z4xl5yxrn.png" width="400" height="200"></p>

- Importer
  - Builder 패턴 적용
<p align="center"><img src="/assets/111_jaj4nqqn5.png" width="500" height="180"></p>

- Select * from Table List 오류 해결 및 distinct / order by 키워드 지원
  - Decorator 패턴 적용
<p align="center"><img src="/assets/111_9fwzpexpm.png" width="600" height="300"></p>

- SQL Statement Keyword를 Console에 모두 출력하는 기능
  - Visitor 패턴 적용
<p align="center"><img src="/assets/111_xmvesneuk.png" width="620" height="280"></p>

<br>
