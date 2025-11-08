plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.joseluisgs"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    
    // Pebble Template Engine
    implementation("io.pebbletemplates:pebble-spring-boot-starter:3.2.2")
    
    // H2 Database
    runtimeOnly("com.h2database:h2")
    
    // Spring Boot DevTools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // PDF Generation
    implementation("com.lowagie:itext:4.2.2")
    implementation("com.itextpdf:html2pdf:5.0.5")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("walaspringboot.jar")
}
