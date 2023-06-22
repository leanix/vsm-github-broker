import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
    id("com.expediagroup.graphql") version "6.3.1"
    id("org.cyclonedx.bom") version "1.7.2"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    jacoco
}

group = "net.leanix.vsm"
version = "v1.4.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.security:spring-security-oauth2-client:5.7.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")
    implementation("com.expediagroup:graphql-kotlin-spring-client:6.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.awaitility:awaitility-kotlin:4.2.0")

    developmentOnly("io.netty:netty-resolver-dns-native-macos:4.1.85.Final") {
        artifact {
            classifier = "osx-aarch_64"
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
    }
    dependencies {
        dependency("com.google.guava:guava:30.0-jre")
        dependency("com.google.protobuf:protobuf-java:3.21.7")
        dependency("io.ktor:ktor-client-apache:2.1.3")
        dependency("org.codehaus.plexus:plexus-utils:3.0.24")
        dependency("org.yaml:snakeyaml:1.33")
    }
}

detekt {
    autoCorrect = true
    parallel = true
    buildUponDefaultConfig = true
    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

graphql {
    client {
        schemaFile = file("${project.projectDir}/src/main/resources/schemas/github_schema.graphql")
        packageName = "net.leanix.githubbroker.connector.adapter.graphql.data"
        queryFileDirectory = "${project.projectDir}/src/main/resources/queries"
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.cyclonedxBom {
    setDestination(project.file("."))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        xml.outputLocation.set(File("${project.buildDir}/jacocoXml/jacocoTestReport.xml"))
    }
}

tasks.processResources {
    doLast {
        file("build/resources/main/gradle.properties").writeText("version=${project.version}")
    }
}
