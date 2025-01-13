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
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.liquibase:liquibase-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
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

        dependencies {
            implementation("io.mockk:mockk:1.13.8")
            implementation("io.kotest:kotest-assertions-core:5.9.1")
            implementation("io.kotest:kotest-runner-junit5:5.9.1")
            implementation("com.ninja-squad:springmockk:4.0.2")
            implementation("org.springframework.boot:spring-boot-starter-test") {
                exclude(module = "mockito-core")
            }
            implementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
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
    dependsOn(tasks.test)
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("reports/jacocoHtml")
        xml.outputLocation = layout.buildDirectory.file("reports/jacocoXml/jacocoTestReport.xml")
    }
}