plugins {
	java
}

group = "io.yogurt"
version = "0.0.1-SNAPSHOT"
description = "Common Module (Shared DTOs and Constants)"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}