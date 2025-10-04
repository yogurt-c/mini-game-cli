plugins {
	java
	id("org.springframework.boot") version "3.5.6" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "io.yogurt"
version = "0.0.1-SNAPSHOT"
description = "CLI Mini Game Platform"

subprojects {
	apply(plugin = "java")

	repositories {
		mavenCentral()
	}

	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(17)
		}
	}
}
