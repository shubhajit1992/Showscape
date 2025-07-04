import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

group = "com.showscape"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

jacoco {
    toolVersion = "0.8.13"
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Configure test logging and summary
tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
    }

    // Register a Kotlin-compatible test listener for per-test and final summary
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                println("----------------------------------------------------")
                println("TEST RESULTS: ${result.resultType}")
                println("Tests run: ${result.testCount}, Failures: ${result.failedTestCount}, Skipped: ${result.skippedTestCount}")
                println("----------------------------------------------------")
            }
        }

        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
            when (result.resultType) {
                TestResult.ResultType.SUCCESS ->
                    println("✅ TEST PASSED: ${testDescriptor.className}.${testDescriptor.name}")
                TestResult.ResultType.FAILURE ->
                    println("❌ TEST FAILED: ${testDescriptor.className}.${testDescriptor.name}")
                TestResult.ResultType.SKIPPED ->
                    println("⚠️ TEST SKIPPED: ${testDescriptor.className}.${testDescriptor.name}")
                else -> println("❓ TEST UNKNOWN: ${testDescriptor.className}.${testDescriptor.name} - ${result.resultType}") // Handle other cases
            }
        }
    })
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Ensure tests are run before report generation
    reports {
        xml.required.set(true)   // ✅ For CI tools like Codecov/SonarQube
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = 0.30.toBigDecimal() // 30% line coverage
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = 0.90.toBigDecimal() // 90% branch coverage
            }
            limit {
                counter = "METHOD"
                value = "COVEREDRATIO"
                minimum = 0.50.toBigDecimal() // 50% method coverage
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification, tasks.jacocoTestReport)
}
