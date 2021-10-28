## Spring - 파일 업로드-Multipart Resolver

### flieupload - form tag

```jsx
<form> .... enctype = "multipart/form-data" ... </form>
```

파일이나 이미지를 서버로 전송할 경우 이 방식을 사용한다

그렇게 하지 않으면 웹 서버로 데이터를 넘길때 파일의 경로명만 전송되고 파일 내용이 전송되지 않기 때문이다

그리고 이 때 메소드 타입은 post값으로 지정해줘야 한다



### File Upload 전체적인 동작 흐름

1. spring-servlet.xml에서 Multipart Resolver를 설정한다

2.  spring-servlet.xml에 mvc url resource mapping 매핑설정을 한다.

   >  <mvc:resources mapping="/upload/images/**" location="file:/upload-mysite/" />

   (사용자가 선택하는 URL을 이용하여 내 컴퓨터에 저장된 경로의 이미지 파일을 불러온다.)

   ![image-20211027200758275](img/image-20211027200758275.png)

   

3. Dispatcher Servlert을 통해 doGet(req, resp)이 들어오면 Controller가 Request를 구분한다

4. Controller의 메소드를 부르기전에 @RequestParam 어노테이션에서 file이라는 파라미터가 있는지 확인한다

5. common-fileupload 라이브러리의 file 부분과 매핑하여 값이 있으면 MultiparfFile 변수에 담는다

6. MultipartFile 파라미터가 여러 개일 경우 MultipartFile[]로 담는다

```jsx
@Controller
클래스 {
		메소드(@RequestParam("file"), MultipartFile 변수명) {
	
				...

		}
}
```



### 세부설정

1. pom.xml에 common-fileupload, common-io 라이브러리 dependency 추가

```xml
<dependencies>

		...		

		<!-- Common Fileupload -->
		<dependency>
			<groupId>commons-fileupload</groupId>
			<artifactId>commons-fileupload</artifactId>
			<version>1.2.1</version>
		</dependency>

		<!-- Common IO -->
		<dependency>
			<groupId>commons-io</groupId>
			<artifactId>commons-io</artifactId>
			<version>1.4</version>
		</dependency>
		
		...

<dependencies>
```



2. spring-servlet.xml에 MultipartResolver 설정 +**mvc url resource mapping**

```xml
<!-- Multipart Resolver -->
	<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
		<!-- 최대업로드 가능한 바이트크기 -->
		<property name="maxUploadSize" value="52428800" />

		<!-- 디스크에 임시 파일을 생성하기 전에 메모리에 보관할수있는 최대 바이트 크기 -->
		<!-- property name="maxInMemorySize" value="52428800" / -->

		<!-- defaultEncoding -->
		<property name="defaultEncoding" value="utf-8" />

	</bean>
```

```
	<mvc:annotation-driven/>
	
	<!-- mvc url resource mapping  -->
	<mvc:resources location="/images/**" mapping="file:/upload-images/"/>
```



3. Controller 코드 작성

```java
@Controller
public class FileUploadController {
	@Autowired
	private FileUploadService fileUploadService;
	
	@RequestMapping({"", "/form"})
	public String form() {
		return "/WEB-INF/views/form.jsp";
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public String upload(
		@RequestParam(value="email", required=true, defaultValue="") String email,
		@RequestParam(value="file") MultipartFile multipartFile,
		Model model) {
		System.out.println("email:" + email);
		
		String url = fileUploadService.restore(multipartFile);
		model.addAttribute("url", url);
		
		return "/WEB-INF/views/result.jsp";
	}
}
```



4. Service 코드 작성

```java
package com.douzone.fileupload.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {
	private static String SAVE_PATH = "/upload-images";
	private static String URL_BASE = "/images";
	
	public String restore(MultipartFile multipartFile) {
		String url = null;
		
		try {
			if(multipartFile.isEmpty()) {
				return url;
			}
			
			// ex ) image.my.png
			
			String originFilename = multipartFile.getOriginalFilename();
			// png부분만 자르기
			String extName = originFilename.substring(originFilename.lastIndexOf('.')+1);
			String saveFilename = generateSaveFilename(extName);
			long fileSize = multipartFile.getSize();
			
			System.out.println("#############" + originFilename);
			System.out.println("#############" + fileSize);
			System.out.println("#############" + saveFilename);
			
			byte[] data = multipartFile.getBytes();
			OutputStream os = new FileOutputStream(SAVE_PATH + "/" + saveFilename);
			os.write(data);
			os.close();
			
			url = URL_BASE + "/" + saveFilename;
		} catch(IOException ex) {
			throw new RuntimeException("file upload error:" + ex);
		}
		
		return url;
	}

	private String generateSaveFilename(String extName) {
		String filename = "";
		
		Calendar calendar = Calendar.getInstance();
		
		filename += calendar.get(Calendar.YEAR);
		filename += calendar.get(Calendar.MONTH);
		filename += calendar.get(Calendar.DATE);
		filename += calendar.get(Calendar.HOUR);
		filename += calendar.get(Calendar.MINUTE);
		filename += calendar.get(Calendar.SECOND);
		filename += calendar.get(Calendar.MILLISECOND);
		filename += ("." + extName);
		
		return filename;
	}

}
```

form.jsp

```
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>파일 업로드 예제</h1>
<form method="post" action="upload" enctype="multipart/form-data">

	<label>email:</label>
	<input type="text" name="email" value="kickscar@gmail.com">
	<br><br>
	
	<label>파일1:</label>
	<input type="file" name="file">
	<br><br>
	

	<br><br>
	
	<br>
	<input type="submit" value="upload">
</form>
</body>
</html>
```

result.jsp

```
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="fn"%>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>Upload completed</h1>

	<div class="result-images">
		<img src="${pageContext.request.contextPath }${url }" style="width:150px"><br>
	</div>

	<p>
		<a href='${pageContext.request.contextPath }'> 다시 업로드 하기 </a>
	</p>	
</body>
</html>
```





## JPA(Java Persistence API)

자바 ORM의 기술 표준, 대표적인 오픈 소스가 Hibernate이다.



**※ Persistence**

직역하면 지속성, 영속성이란 의미.

예를 들어 데이터 기록을 하는데 RAM은 휘발성이란 특성으로 인해 전력의 공급이 끊기면 데이터는 모두 사라진다.

그러나 하드디스크 같은 경우 갑작스러운 전력 공급의 차단에도 비휘발성이라는 특성으로 데이터는 영구히 저장되게 된다.

이렇게 **데이터가 영구히 저장이 되는 특성**을 영속성, persistence라고 생각하면 된다.

하지만 이때 Java를 사용하는 환경에서 하드디스크가 아닌 DBMS에 저장을 하는 것이다.

결국 JPA란, JAVA에서 데이터를 영구히 기록할 수 있는 환경을 제공하는 API 라고 생각하면 된다.

**JPA의 장점**

1. RDB의 종류에 상관없이 사용가능하다. 추후 DB변경이나 코드 재활용이 용이하다.

2. 기본적인 CRUD제공과 페이징 처리 등 상당 부분 구현되어 있어 비지니스 로직에 집중할 수 있다.

3. 테이블 생성, 변경 등 엔티티 관리가 간편하다.

4. 쿼리에 집중할 필요 없이 빠른 개발이 가능하다.



**JPA의 단점**

어렵다. (단방향, 양방향, 임베디드 관계 등 이해해야할 내용이 많으며, 연관관계 이해없이 잘못 코딩을 하게되면 성능상의 문제와 동작이 원하는대로 되지 않는 일이 발생한다.)



## ORM(Object-relational mapping)

**객체 관계 매핑, 객체와 RDB를 별개로 설계하고 OMR이 중간에서 매핑해주는 역할**

ORM은 SQL문이 아닌 RDB에 데이터 그 자체와 매핑하기 때문에 SQL을 직접 작성할 필요가 없다. 해당 객체와의 매핑에 필요한 SQL문을 알아서 만들어준다. 이로인해 어떤 RDB를 사용해도 상관없다. SQL이 아닌 ORM에서 제공하는 메서드, 코드 등을 이용해서 직관적이다.





## **MyBatis란?**

------

![mybatis-superbird-small](img/mybatis-superbird-small.png)

- 쿼리 기반 웹 애플리케이션을 개발할 때 가장 많이 사용되는 SQL 매퍼(Mapper) 프레임워크이다.
- 마이바티스를 사용하지 않고 직접 JDBC를 이용할 경우 문제점:
  - 개발자가 반복적으로 작성해야 할 코드가 많고, 서비스 로직 코드와 쿼리를 분리하기가 어렵다.
  - 또한 커넥션 풀의 설정 등 개발자가 신경 써야 할 부분이 많아 여러 가지 어려움이 있다. 
- 따라서, JDBC를 이용해서 직접 개발하기보다는 마이바티스와 같은 프레임워크를 사용하는 게 일반적이다.
- JDBC를 이용하여 프로그래밍을 하는 방식: 
  - 클래스나 JSP와 같은 코드 안에 SQL문을 작성하는 방식
  - 따라서 SQL의 변경 등이 발생할 경우 프로그램을 수정해야 한다.
    - -> 유연하지 않다, 코드가 복잡하게 섞여 있어서 가독성도 떨어짐
- 마이바티스에서는 SQL을 XML 파일에 작성하기 때문에, SQL의 변환이 자유롭고 가독성도 좋다.



**MyBatis의 장점**

1. JPA의 비해 쉽다.

2. SQL의 세부적인 내용 변경 시 좀 더 간편하다.

3. 동적 쿼리 사용 시 JPA보다 간편하게 구현가능하다.
4. 

**MyBatis의 단점**

1. 데이터베이스 설정변경 시 수정할 부분이 많다. 

2. Mapper작성부터 인터페이스 설계까지 JPA보다 많은 설계와 파일, 로직이 필요하다.

3. 특정 DB에 종속적이다.



**MyBatis3의 Databases Access 구조**

![999CFA505BBB65D32C](img/999CFA505BBB65D32C.jpg)

### MyBatis ORM

MyBatis ORM은 데이터베이스 CRUD를 위한 JDBC 자바 코드의 반복적인 저수준의 작업들을 추상화 했다. JDBC 드라이버 로딩, RDBMS 연결, PreparedStatement, SQL Parameter Binding, ResultSet Iteration 그리고 Resource Clean-up 등의 반복적인 코드와 예외처리 그리고 트랜잭션 관리 등을 SqlSession 인터페이스 하나로 추상화 했다. 이 말은 SqlSession 인터페이스만 가지고 모든 작업이 가능하다는 말이다. Session의 의미는

1. 연결
2. 트랜잭션 시작
3. 쿼리 실행
4. 트랜잭션 종료
5. 연결 종료

와 같은 일련의 과정을 일컫는다. 그런데 인터페이스의 이름에 SQL이 아직 남아있다. 이는 MyBatis의 RDBMS 추상화 단계가 SQL까지 감추지 않았다는 의미다. MyBatis ORM에서는 객체를 저장하거나 로딩 또는 변경, 영구적 삭제 등을 위해 SQL를 직접 다루어야 한다. 하지만 SQL를 직접 다루는 자바 표준 기술인 JDBC을 사용하는 자바 코드만큼이나 반복적이거나 복잡하고 상투적이지 않다. 정리하면, MyBatis ORM은

1. SQL를 중심으로 SQL의 파라미터를 자바 객체와 매핑한다.
2. select SQL문의 결과(ResultSet)를 자바 객체와 매핑한다.

이 두 가지 매핑 작업은 Mapper라 불리는 것을 사용한다. Mapper의 사용은 기존의 직접적인 JDBC 자바 코드와 달리 선언적으로 이루어 진다. 그렇기 때문에 덜 복잡하고 직관적이며 SQL를 자바 코드에서 완벽하게 분리한다.



## 매핑(Mapping)

MyBatis는 SQL과 자바 객체와의 매핑에 주력한 ORM 기술로 SQL문의 파라미터와 Select의 결과(ResultSet)를 자바 객체에 매핑한다. 

이 매핑 도구를 Mapper라 부르고 XML 또는 Mapper Interface의 Annotation을 통해 가능하다.

이 두가지 방법에 따라 각각 XML Mpper 또는 Annotation Mapper라 부른다. 개인적으로 XML Mapper를 선호하는 편이지만 Annotation Mapper의 Mpapper Interface는 매핑 방법에 상관없이 매핑된 SQL문 실행에 많은 유연성을 제공하기 때문에 알아 두는 것이 좋다.

SQL문의 실행은 언급한 바, Mpapper Interface를 선호하지만 SqlSession 객체의 메소드 호출이 보다 일반적이라 볼 수 있다. 

**SQL Mapper**

SQL문을 이용하여 RDB에 접근, 데이터를 오브젝트(객체)화 시켜준다.

개발자가 작성한 SQL문으로 해당되는 ROW를 읽어 온 후 결과 값을 오브젝트화 시켜 사용가능하게 만들어준다.

즉, RDB에 따라 SQL 문법이 다르기 때문에 특정 RDB에 종속적이다.





#### Mybatis 설정

1. 라이브러리 추가(pom.xml)

```
<!-- MyBatis -->

<dependency>

<groupId>org.mybatis</groupId>

<artifactId>mybatis</artifactId>

<version>3.2.2</version>

</dependency>

<dependency>

<groupId>org.mybatis</groupId>

<artifactId>mybatis-spring</artifactId>

<version>1.2.0</version>

</dependency>
```



2. SqlSessionFactoryBean 설정(applicationContext.xml)

```
	<!-- MyBatis SqlSessionFactoryBean -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="configLocation" value="classpath:mybatis/configuration.xml" />
	</bean>
```

3. SqlSessionTemplete 설정 ( applicationContext.xml )

```
	<!-- MyBatis SqlSessionTemplate -->
	<bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
		<constructor-arg index="0" ref="sqlSessionFactory" />
	</bean>
```



#### **Mybatis 사용하기** (mysite03에 적용한내용)

![image-20211021182602833](img/image-20211021182602833.png)

4. **mybatis/configuration.xml** (mybatis 설정파일)

```
<?xml version="1.0" encoding="UTF-8" ?>

<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>
	<typeAliases>
		<typeAlias type="com.douzone.mysite.vo.GuestbookVo" alias="guestbookvo"/>
		<typeAlias type="com.douzone.mysite.vo.UserVo" alias="uservo"/>
	</typeAliases>
	
	<mappers>
		<mapper resource="mybatis/mappers/guestbook.xml" />
		<mapper resource="mybatis/mappers/user.xml" />
	</mappers>
</configuration>
```

- Alias 설정: "com.douzone.mysite.vo.GuestbookVo" -> "guestbookvo"
- <mapper resource="mybatis/mappers/guestbook.xml" /> : sql파일과 연결해줌



**mybatis/mappers/guestbook.xml** (SQL파일)

```
<?xml version="1.0" encoding="UTF-8" ?>

<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="guestbook">

	<insert id="insert" parameterType="guestbookvo">
	<![CDATA[
	insert into guestbook values (null, #{name }, #{password}, #{message }, now())
	]]> <!-- CDATA는 character data -->	
	
	<selectKey keyProperty="no" resultType="long" order="AFTER">
		<![CDATA[
		select last_insert_id();
		]]>	
	</selectKey>
	</insert>
	
	
	<delete id="delete" parameterType="guestbookvo">
	<![CDATA[
	delete from guestbook where no=#{no } and password=#{password }
	]]> <!-- CDATA는 character data -->	
	</delete>
	
	<select id="findAll" resultType="guestbookvo">
	<![CDATA[
	select no, name, date_format(reg_date, '%Y/%m/%d %H:%i:%s') as regDate, message from guestbook order by reg_date desc
	]]> <!-- CDATA는 character data -->	
	</select>
</mapper>
```

- CDATA: 태그 안에서는 전부 문자열로 치환시켜버린다.(<, > 와 같은 부등호가 태그로 인식되버린다.)
- selectKey keyProperty="no" resultType="long" order="AFTER" : guestbookvo라는 테이블에서 long타입의 no값을 insert쿼리문 수행후에 알고싶다.(order는 해당 쿼리의 순서를 의미한다.)
- **last_insert_id** 함수는 테이블의 마지막 auto_increment 값을 리턴한다.

**guestbookRepository.java**

```
package com.douzone.mysite.repository;


import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.douzone.mysite.exception.GuestbookRepositoryException;
import com.douzone.mysite.vo.GuestbookVo;

@Repository
public class GuestbookRepository {
	@Autowired
	private SqlSession sqlSession;
	
	public List<GuestbookVo> findAll() throws GuestbookRepositoryException {
		List<GuestbookVo> list = sqlSession.selectList("guestbook.findAll");

		//sqlSession.selectOne("guestbook.findAll");
		
		return list;
	}
	
	public boolean delete(GuestbookVo vo) {
		int count = sqlSession.delete("guestbook.delete", vo);

		return count==1;		
	}
	
	public boolean insert(GuestbookVo vo) {
		int count = sqlSession.insert("guestbook.insert", vo);
		System.out.println(vo);
		
		return count == 1;
	}
}
```

- 	@Autowired
    	private SqlSession sqlSession; 

  : 무조건 선언해주어야 한다.

