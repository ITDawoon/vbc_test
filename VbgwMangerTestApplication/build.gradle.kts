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
    // Swagger
    implementation("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")
    implementation("org.bitbucket.tek-nik:spring-swagger-simplified:1.0.2")

    // Logger
    implementation("ch.qos.logback:logback-classic")
    implementation("ch.qos.logback:logback-core")
    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:jcl-over-slf4j")
    implementation("org.codehaus.janino:janino")
    implementation("org.tuxdude.logback.extensions:logback-colorizer:1.0.1")
    
    // Apache Commons
    implementation("commons-io:commons-io:2.7")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.apache.commons:commons-math3:3.2")

    // Lombok 추가
	annotationProcessor("org.projectlombok:lombok")

    // Servelet 추가
    implementation("javax.servlet:javax.servlet-api:4.0.1")

	// Spring Boot Starter Web 추가
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Data JPA 추가
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Jackson 추가 (JSON 데이터 처리)
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Spring Boot DevTools (선택 사항)
    implementation("org.springframework.boot:spring-boot-devtools")

    // Spring Boot Starter Test (테스트용)
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Spring Boot Starter Security (보안)
    //implementation("org.springframework.boot:spring-boot-starter-security")

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
