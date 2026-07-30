import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("kapt") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    kotlin("plugin.jpa") version "2.3.21"
    kotlin("plugin.lombok") version "2.3.21"
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.hibernate.orm") version "7.4.1.Final"
//    id("org.graalvm.buildtools.native") version "1.1.1"
}

group = "xyz.thewhitedog9487"
version = "0.8.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("org.springframework.boot:spring-boot-starter-data-redis")
//	implementation("org.springframework.boot:spring-boot-starter-jdbc")
//	implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-security")
//	implementation("org.springframework.boot:spring-boot-starter-security-oauth2-authorization-server")
//	implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
//	implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
//	implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
//	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
//	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
//	runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.xerial:sqlite-jdbc")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
//	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-authorization-server-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-client-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
//	testImplementation("org.springframework.boot:spring-boot-starter-websocket-test")
//	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//	testCompileOnly("org.projectlombok:lombok")
//	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//	testAnnotationProcessor("org.projectlombok:lombok")
//  testImplementation(kotlin("test"))

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("org.hibernate.orm:hibernate-community-dialects")
    implementation("dev.kord:kord-core:0.18.1")
    implementation("tools.jackson.core:jackson-core")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:3.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.micrometer:context-propagation")
    implementation("org.apache.commons:commons-lang3")
}

hibernate {
    enhancement {
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
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

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

kapt {
    keepJavacAnnotationProcessors = true
}