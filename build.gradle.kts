plugins {
    java
    id("org.springframework.boot") version "3.5.6" // Plugin de Spring Boot
    id("io.spring.dependency-management") version "1.1.7" // Plugin de gestión de dependencias de Spring
}

group = "dev.joseluisgs"
version = "0.0.1-SNAPSHOT"

java {
    // versión de Java
    sourceCompatibility = JavaVersion.VERSION_25 // Sintaxis de Java 25
    targetCompatibility = JavaVersion.VERSION_25 // Versión de Java 25 para ser compilado y ejecutado
    
    toolchain {
        languageVersion = JavaLanguageVersion.of(25) // Versión de Java 25 para el toolchain
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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    
    // Pebble Template Engine
    implementation("io.pebbletemplates:pebble-spring-boot-starter:3.2.4")
    implementation("io.pebbletemplates:pebble:3.2.4")
    
    // H2 Database
    runtimeOnly("com.h2database:h2")
    
    // Spring Boot DevTools
    // developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // PDF Generation
    implementation("com.itextpdf:itextpdf:5.5.13.4")
    implementation("com.itextpdf:html2pdf:5.0.5")
    
    // Image processing
    implementation("org.imgscalr:imgscalr-lib:4.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("walaspringboot.jar")
}
