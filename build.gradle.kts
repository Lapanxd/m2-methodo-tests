plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    jacoco
    id("java")
    id("info.solidsoft.pitest") version "1.15.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

configurations {
    val testIntegrationImplementation: Configuration by creating {
        extendsFrom(configurations.implementation.get())
    }

    val testComponentImplementation: Configuration by creating {
        extendsFrom(configurations.testImplementation.get())
        extendsFrom(configurations.implementation.get())
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.postgresql:postgresql")

    testImplementation("io.cucumber:cucumber-java:7.14.0")
    testImplementation("io.cucumber:cucumber-spring:7.14.0")
    testImplementation("io.cucumber:cucumber-junit:7.14.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
    testImplementation("io.rest-assured:rest-assured:5.3.2")
    testImplementation("org.junit.platform:junit-platform-suite:1.10.0")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")

    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.jetbrains.kotlin:kotlin-test-junit5")
    implementation("io.kotest:kotest-runner-junit5:5.9.1")
    implementation("io.kotest:kotest-assertions-core:5.9.1")
    implementation("io.kotest:kotest-property:5.9.1")
    implementation("io.mockk:mockk:1.13.13")
    implementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

testing {
    suites {
        val testIntegration by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testIntegration/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }

        val testComponent by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testComponent/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
        }

        dependencies {
            implementation("io.mockk:mockk:1.13.8")
            implementation("io.kotest:kotest-assertions-core:5.9.1")
            implementation("io.kotest:kotest-runner-junit5:5.9.1")
            implementation("com.ninja-squad:springmockk:4.0.2")
            implementation("org.springframework.boot:spring-boot-starter-test") {
                exclude(module = "mockito-core")
            }
            implementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
            implementation("org.testcontainers:postgresql:1.19.1")
            implementation("org.testcontainers:testcontainers:1.19.1")
            implementation("org.testcontainers:jdbc-test:1.12.0")
            implementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
        }


    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

pitest {
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(listOf("com.example.*"))
    outputFormats.set(listOf("HTML", "XML"))
    threads.set(4)
    timestampedReports.set(false)
    verbose.set(true)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.build {
    dependsOn("pitest")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, tasks.named("testIntegration"))
    executionData.from(
        layout.buildDirectory.file("jacoco/test.exec"),
        layout.buildDirectory.file("jacoco/integrationTest.exec")
    )
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacocoHtml"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacocoXml/jacocoTestReport.xml"))
    }
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.register<Test>("integrationTest") {
    description = "Run integration tests with JaCoCo coverage."
    group = "verification"
    testClassesDirs = fileTree("src/testIntegration/kotlin")
    classpath = sourceSets["testIntegration"].runtimeClasspath
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.register<Test>("componentTest") {
    description = "Run all tests."
    group = "verification"
    testClassesDirs = fileTree("src/testComponent/kotlin")
    classpath = sourceSets["testComponent"].runtimeClasspath
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}