plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.hibernate.orm") version "7.1.8.Final"
	id("org.graalvm.buildtools.native") version "0.11.3"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "xyz.thewhitedog9487"
version = "0.7.5"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
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
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("org.xerial:sqlite-jdbc")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
	testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.hibernate.orm:hibernate-community-dialects")
	implementation("com.discord4j:discord4j-core:3.3.0")
	implementation("tools.jackson.core:jackson-core:3.0.3")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:3.0.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootJar {
	archiveClassifier.set("boot")
}

//tasks.jar {
//	enabled = false
//}
// https://docs.spring.io/spring-boot/gradle-plugin/packaging.html#packaging-executable.and-plain-archives