<hr>
<h1><strong>1. 패키지 구조</strong></h1>
<h2><strong>1.1. 도메인 패키지</strong></h2>
<pre><code>.

├── post
│ ├── controller
│ ├── dto
│ ├── repository
│ └── service
└── user
│ ├── controller
│ ├── dto
│ ├── repository
│ └── service
├── common
│ ├── config
│ ├── entity
│ ├── exception
│ ├── infrastructure
│ ├── log
│ └── utils

</code></pre>
<hr>
<h1><strong>2. 테스트 코드</strong></h1>
<h2><strong>2.1. 네이밍</strong></h2>
<h3><strong>2.1.1. @DisplayName</strong></h3>
<ul>
<li>형식 메서드명_성공여부_조건</li>
<li>메서드명은 @Nested 사용 시 클래스명으로 이동</li>
</ul>
<h3><strong>2.1.2. 성공여부 표기</strong></h3>
<ul>
<li>success</li>
<li>error</li>
</ul>

 항목           | 규칙            
--------------|---------------
 @Nested      | ⭕             
 @DisplayName | 시나리오·조건·결과 명시 

## **2.2. BDD 패턴**

```
// given (불필요하면 생략)
// when
// then
```

- before-then 패턴 도입 여부: 추후 필요 시

## **2.3. Fixture**

- 칼리 방식(정팩메) ⭕

## 2.4. 테스트 환경 및 전략

사용자 인증 / 인가 필요하면 컨트롤러에서 테스트 → `MockMVC` 사용해서 톰캣 띄우지 않기

서비스는 통합 테스트 `@SpringBootTest(WebEnvironment.MOCK)` 로 톰캣 띄우지 않기

`JPQL` / `Query Method` 인 경우 테스트

assertSoftly 로 감싸기

<hr>
<h1><strong>3. 네이밍 규칙</strong></h1>

 구분                      | 규칙                                                       
-------------------------|----------------------------------------------------------
 메서드                     | 동사 시작, 엔티티명 미사용 ex) memberService.findById(Long id)      
 파라미터                    | Path Variable 명확화 ex) /api/{documentId}                  
 DTO                     | Request/Response 접미사 ⭕ 등록·수정용은 Register / Update 등 사용 가능 
 ex) PostRegisterRequest |

<hr>

<h1><strong>4. 객체지향 생활체조</strong></h1>

| 규칙                | 적용 여부        |
|-------------------|--------------|
| 1. 한 메서드 한 indent | 🔺 (시도 후 공유) |
| 2. else 금지        | ⭕            |
| 3. 원시값·문자열 포장     | 🔺           |
| 4. 한 줄에 점 하나      | ⭕            |
| 5. 축약 금지          | ⭕            |
| 6. 작은 엔티티         | ⭕            |
| 7. 인스턴스 변수 ≤ 2    | 🔺           |
| 8. 일급 컬렉션         | ⭕            |
| 9. 게터/세터 금지       | ⭕            |

<h1><strong>5. 예외 처리</strong></h1>
<ul>
<li>에러 코드를 포함해 반환</li>
</ul>
<hr>
<h1><strong>6. 어노테이션 순서</strong></h1>
<ol>
<li>로그</li>
<li>롬복</li>
<li>스프링 메타</li>
<li>스프링 컴포넌트 (가장 중요도 높음, 가장 아래)</li>
</ol>
<hr>
<h1><strong>7. 도메인 외부 노출 범위</strong></h1>
<ul>
<li>서비스 외부에는 DTO만 노출</li>
</ul>
<h1><strong>8. API ↔ DTO</strong></h1>
<ul>
<li>1 : 1 매핑</li>
</ul>

# 9. 코드 스타일

- 클래스 선언 전 공백 한 줄
- 메서드 정의 파라미터 2개 이상 시 개행
    - **메서드 호출에서는 적용하지 않는다.**
- 메서드 라인 수 제한 없음 (개행 규칙 우선)

<hr>

# 10. 검증 위치

> 개발 시점에 협의

<hr>

# 11. import

## 11.1. 와일드카드

- 금지
- IntelliJ 코드 컨벤션 적용

<hr>

# 12. 클래스·메서드 명명

 동작 | 메서드명                                   
----|----------------------------------------
 생성 | create·save·register·add               
 읽기 | read(다건) / get(항상 존재) / find(Optional) 
 수정 | update(전체, PUT) / modify(일부, PATCH)    
 삭제 | delete·remove                          

# 13. 기타

## 13.1. final 선언

사용하지 않음

## 13.2. 정적 팩토리 메서드

사용하지 않음

- 도메인·DTO 생성 시 new 연산자 사용

## 13.3. VO 사용

검증이 필요한 도메인에 대해서 적용

## 13.4. 상수

- 대문자 + _

## 13.5. 접근자·메서드 정렬

- public
- protected
- private

호출 순서를 따라 배치
