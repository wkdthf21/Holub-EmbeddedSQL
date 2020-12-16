## Holub-SQL 확장 프로젝트
---
### Intro
---
Holub-SQL에 디자인 패턴을 적용하여 기능을 확장하는 프로젝트
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
