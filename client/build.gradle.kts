plugins {
	java
	application
}

group = "io.yogurt"
version = "0.0.1-SNAPSHOT"
description = "CLI Client Module"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":common"))
	implementation("org.java-websocket:Java-WebSocket:1.5.3")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}

application {
	mainClass.set("io.yogurt.cli_mini_game.client.ClientApplication")
}