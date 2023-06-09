plugins {
    idea
    java
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.6.0"
    jacoco
}

group = "poc"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

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
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.github.daggerok:liquibase-r2dbc-spring-boot-starter:2.1.9")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.1.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation(platform("io.cucumber:cucumber-bom:7.11.2"))

    testImplementation("io.cucumber:cucumber-java8")
    testImplementation("io.cucumber:cucumber-spring")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
}

tasks.test {
    useJUnitPlatform()

    /*
      Work around. Gradle does not include enough information to disambiguate
      between different examples and scenarios.
     */
    systemProperty("cucumber.junit-platform.naming-strategy", "long")

    // report is always generated after tests run
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    // tests are required to run before generating the report
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(true)
    }
}

openApi {
    apiDocsUrl.set("http://localhost:8081/v3/api-docs")
}