plugins {
	java
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.rapeech.vbc"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
    // Gradle 추가
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.0.6")

    // Lombok 추가
	annotationProcessor("org.projectlombok:lombok")

    // Spring-Web Module 추가
    //implementation("org.springframework:spring-web:5.3.13")

    // Servelet 추가
    implementation("javax.servlet:javax.servlet-api:4.0.1")

	// Spring Boot Starter Web 추가
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Jackson 추가 (JSON 데이터 처리)
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Spring Boot DevTools (선택 사항)
    implementation("org.springframework.boot:spring-boot-devtools")

    // Spring Boot Starter Test (테스트용)
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Spring Boot Starter Security (보안)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Boot Starter Actuator (애플리케이션 상태 및 모니터링)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Logback (로깅)
    implementation("ch.qos.logback:logback-classic")

    // Spring Boot Starter Validation (입력 데이터 유효성 검사)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring Boot Starter Test (Mockito) (테스트용)
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
    }
	
}

tasks.withType<Test> {
	useJUnitPlatform()
}
