# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.6 application for creating CLI-based mini games, using Java 17 and Gradle with Kotlin DSL.

**Important**: The package name is `io.yogurt.cli_mini_game` (with underscores), not `io.yogurt.cli-mini-game` (with hyphens).

### Architecture Note
이 프로젝트는 **멀티 게임 플랫폼**으로 설계되어 있습니다. 첫 번째 게임 완성 후, 동일한 프로젝트에 새로운 게임들을 하나씩 추가하는 구조입니다.

#### Multi-Module Structure
프로젝트는 3개의 모듈로 구성됩니다:
- **server**: 게임 서버 (Spring Boot + WebSocket)
- **client**: CLI 클라이언트
- **common**: 서버-클라이언트 공통 모듈 (DTO, 상수 등)

#### Design Principles
- 게임별로 독립적인 패키지 구조 유지
- 공통 기능은 common 모듈로 분리
- 각 게임은 독립적인 WebSocket endpoint 사용

## Build System

This project uses Gradle with Kotlin DSL (`build.gradle.kts`).

### Common Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests io.yogurt.cli_mini_game.CliMiniGameApplicationTests

# Run a specific test method
./gradlew test --tests io.yogurt.cli_mini_game.ClassName.methodName

# Clean build artifacts
./gradlew clean

# Build without running tests
./gradlew build -x test
```

## Project Structure

- **Main application**: `src/main/java/io/yogurt/cli_mini_game/CliMiniGameApplication.java`
- **Base package**: `io.yogurt.cli_mini_game`
- **Resources**: `src/main/resources/`
- **Tests**: `src/test/java/io/yogurt/cli_mini_game/`

## Technology Stack

- **Java**: 17 (configured via toolchain)
- **Spring Boot**: 3.5.6
- **Build Tool**: Gradle 8.x with Kotlin DSL
- **Testing**: JUnit Platform with Spring Boot Test
- **WebSocket**: STOMP over WebSocket for real-time communication

## Game Requirements

### Game Overview
CLI 기반 멀티플레이어 코드 퀴즈 게임 - 코드의 틀린 라인을 찾아 상대방에게 장애물을 보내는 게임

### Core Mechanics
1. **코드 문제 제시**: 서버가 코드를 플레이어들에게 제시
2. **오류 찾기**: 플레이어가 코드에서 틀린 라인(line number)을 찾아 제출
3. **장애물 처리**:
   - 내게 장애물이 있는 경우: 장애물 1개 제거
   - 내게 장애물이 없는 경우: 상대방에게 장애물 1개 전송
4. **패배 조건**: 장애물이 특정 높이(개수)를 넘어가면 패배

### Domain Models
- **Game**: 게임 세션 관리 (gameId, players, status, currentQuestion)
- **Player**: 플레이어 정보 (sessionId, nickname, obstacleCount, alive)
- **CodeQuestion**: 코드 문제 (code, incorrectLines, explanation, difficulty)
- **Obstacle**: 장애물 높이 관리

### WebSocket Communication
- **Endpoint**: `/game-websocket`
- **Prefixes**:
  - Application: `/app`
  - Broker: `/topic` (broadcasts), `/queue` (personal)

### Client Initial Flow
클라이언트 접속 시 초기 흐름:
1. **게임 종류 선택**: 사용 가능한 게임 목록에서 플레이할 게임 선택
2. **방 찾기 / 방 만들기 선택**:
   - 방 찾기: 기존 대기 중인 방 목록 조회 및 입장
   - 방 만들기: 새로운 게임 방 생성
3. **방 입장**: 선택한 방에 입장 (대기실)
4. **게임 시작**: 모든 플레이어 준비 완료 시 게임 시작

### Game Flow (코드 퀴즈 게임)
1. 게임 시작 → 코드 문제 제시
2. 플레이어 답안(틀린 라인 번호) 제출
3. 정답 확인:
   - 정답인 경우:
     - 내 장애물 > 0: 장애물 1개 제거
     - 내 장애물 = 0: 상대에게 장애물 1개 전송
   - 오답인 경우: 아무 일도 일어나지 않음 (또는 페널티)
4. 장애물 높이 체크:
   - 특정 높이 초과 시 해당 플레이어 패배
5. 게임 계속 (새로운 코드 문제 제시)
6. 승자 결정 시 게임 종료

### Game Configuration
- **MAX_OBSTACLE_HEIGHT**: 장애물 패배 기준 (예: 5개)