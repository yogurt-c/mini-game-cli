package io.yogurt.cli_mini_game.client.ui;

import com.googlecode.lanterna.TextColor;

/**
 * 터미널 테마 및 스타일 유틸리티
 * 레트로 터미널 / 해커 스타일 색상 및 아스키 아트
 */
public class TerminalTheme {

    // 색상 팔레트 - 레트로 터미널 스타일
    public static class Colors {
        // 기본
        public static final TextColor BACKGROUND = TextColor.ANSI.BLACK;
        public static final TextColor FOREGROUND = TextColor.ANSI.GREEN_BRIGHT;

        // 강조
        public static final TextColor ACCENT = TextColor.ANSI.CYAN_BRIGHT;
        public static final TextColor WARNING = TextColor.ANSI.YELLOW_BRIGHT;
        public static final TextColor ERROR = TextColor.ANSI.RED_BRIGHT;
        public static final TextColor SUCCESS = TextColor.ANSI.GREEN_BRIGHT;

        // 상태
        public static final TextColor ALIVE = TextColor.ANSI.GREEN;
        public static final TextColor DEAD = TextColor.ANSI.RED;
        public static final TextColor INFO = TextColor.ANSI.CYAN;

        // UI 요소
        public static final TextColor BORDER = TextColor.ANSI.GREEN;
        public static final TextColor TITLE = TextColor.ANSI.GREEN_BRIGHT;
        public static final TextColor TEXT = TextColor.ANSI.WHITE;
        public static final TextColor DIM_TEXT = TextColor.ANSI.WHITE;
    }

    // 박스 스타일 - 다양한 프레임
    public static class BoxStyle {
        // 기본 스타일
        public static final String[] SINGLE = {"┌", "┐", "└", "┘", "─", "│"};
        public static final String[] DOUBLE = {"╔", "╗", "╚", "╝", "═", "║"};
        public static final String[] ROUNDED = {"╭", "╮", "╰", "╯", "─", "│"};
        public static final String[] HEAVY = {"┏", "┓", "┗", "┛", "━", "┃"};
        public static final String[] ASCII = {"+", "+", "+", "+", "-", "|"};

        // 레트로 터미널 스타일
        public static final String[] RETRO = {"╔", "╗", "╚", "╝", "═", "║"};
    }

    // 아이콘 및 심볼
    public static class Symbols {
        public static final String BULLET = "●";
        public static final String HOLLOW_BULLET = "○";
        public static final String SQUARE = "■";
        public static final String HOLLOW_SQUARE = "□";
        public static final String ARROW_RIGHT = "▶";
        public static final String ARROW_LEFT = "◀";
        public static final String TRIANGLE = "▲";
        public static final String CHECK = "✓";
        public static final String CROSS = "✗";
        public static final String STAR = "★";
        public static final String HOLLOW_STAR = "☆";
        public static final String SKULL = "☠";
        public static final String CROWN = "♛";
        public static final String SWORD = "⚔";
        public static final String SHIELD = "🛡";
        public static final String FIRE = "🔥";
        public static final String SKULL_CROSSBONES = "☠";

        // 프로그레스 바
        public static final String PROGRESS_FULL = "█";
        public static final String PROGRESS_EMPTY = "░";
        public static final String PROGRESS_HALF = "▒";
    }

    // ASCII 아트 배너
    public static class Banners {
        public static final String CODE_QUIZ = """
╔═══════════════════════════════════════════════════════════════════╗
║   ██████╗ ██████╗ ██████╗ ███████╗     ██████╗ ██╗   ██╗██╗███████╗
║  ██╔════╝██╔═══██╗██╔══██╗██╔════╝    ██╔═══██╗██║   ██║██║╚══███╔╝
║  ██║     ██║   ██║██║  ██║█████╗      ██║   ██║██║   ██║██║  ███╔╝
║  ██║     ██║   ██║██║  ██║██╔══╝      ██║▄▄ ██║██║   ██║██║ ███╔╝
║  ╚██████╗╚██████╔╝██████╔╝███████╗    ╚██████╔╝╚██████╔╝██║███████╗
║   ╚═════╝ ╚═════╝ ╚═════╝ ╚══════╝     ╚══▀▀═╝  ╚═════╝ ╚═╝╚══════╝
╚═══════════════════════════════════════════════════════════════════╝""";

        public static final String GAME_START = """
╔════════════════════════════════════════╗
║   ▄████  ▄▄▄       ███▄ ▄███▓▓█████     ║
║  ██▒ ▀█▒▒████▄    ▓██▒▀█▀ ██▒▓█   ▀     ║
║ ▒██░▄▄▄░▒██  ▀█▄  ▓██    ▓██░▒███       ║
║ ░▓█  ██▓░██▄▄▄▄██ ▒██    ▒██ ▒▓█  ▄     ║
║ ░▒▓███▀▒ ▓█   ▓██▒▒██▒   ░██▒░▒████▒    ║
║  ░▒   ▒  ▒▒   ▓▒█░░ ▒░   ░  ░░░ ▒░ ░    ║
║   ░   ░   ▒   ▒▒ ░░  ░      ░ ░ ░  ░    ║
║       ░   ░   ▒   ░      ░      ░       ║
║           ░  ░       ░      ░  ░        ║
║   ███████╗████████╗ █████╗ ██████╗████████╗║
║   ██╔════╝╚══██╔══╝██╔══██╗██╔══██╚══██╔══╝║
║   ███████╗   ██║   ███████║██████╔╝  ██║   ║
║   ╚════██║   ██║   ██╔══██║██╔══██╗  ██║   ║
║   ███████║   ██║   ██║  ██║██║  ██║  ██║   ║
║   ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝  ╚═╝   ║
╚════════════════════════════════════════╝""";

        public static final String GAME_OVER = """
╔═══════════════════════════════════════════╗
║   ██████╗  █████╗ ███╗   ███╗███████╗     ║
║  ██╔════╝ ██╔══██╗████╗ ████║██╔════╝     ║
║  ██║  ███╗███████║██╔████╔██║█████╗       ║
║  ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝       ║
║  ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗     ║
║   ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ║
║   ██████╗ ██╗   ██╗███████╗██████╗        ║
║  ██╔═══██╗██║   ██║██╔════╝██╔══██╗       ║
║  ██║   ██║██║   ██║█████╗  ██████╔╝       ║
║  ██║   ██║╚██╗ ██╔╝██╔══╝  ██╔══██╗       ║
║  ╚██████╔╝ ╚████╔╝ ███████╗██║  ██║       ║
║   ╚═════╝   ╚═══╝  ╚══════╝╚═╝  ╚═╝       ║
╚═══════════════════════════════════════════╝""";

        public static final String VICTORY = """
    ██╗   ██╗██╗ ██████╗████████╗ ██████╗ ██████╗ ██╗   ██╗
    ██║   ██║██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗╚██╗ ██╔╝
    ██║   ██║██║██║        ██║   ██║   ██║██████╔╝ ╚████╔╝
    ╚██╗ ██╔╝██║██║        ██║   ██║   ██║██╔══██╗  ╚██╔╝
     ╚████╔╝ ██║╚██████╗   ██║   ╚██████╔╝██║  ██║   ██║
      ╚═══╝  ╚═╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝   ╚═╝   """;

        public static final String DEFEAT = """
    ██████╗ ███████╗███████╗███████╗ █████╗ ████████╗
    ██╔══██╗██╔════╝██╔════╝██╔════╝██╔══██╗╚══██╔══╝
    ██║  ██║█████╗  █████╗  █████╗  ███████║   ██║
    ██║  ██║██╔══╝  ██╔══╝  ██╔══╝  ██╔══██║   ██║
    ██████╔╝███████╗██║     ███████╗██║  ██║   ██║
    ╚═════╝ ╚══════╝╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   """;

        public static final String WAITING = """
┌──────────────────────────────────┐
│  ⏳ WAITING FOR PLAYERS...      │
└──────────────────────────────────┘""";
    }

    // 상태 표시 템플릿
    public static class Templates {
        public static String playerStatus(String nickname, int score, int obstacles, boolean alive) {
            String status = alive ? "[ALIVE]" : "[DEAD]";
            String icon = alive ? Symbols.BULLET : Symbols.SKULL;
            String bar = Symbols.PROGRESS_FULL.repeat(obstacles) +
                        Symbols.PROGRESS_EMPTY.repeat(5 - obstacles);

            return String.format("%s %s %-15s │ SCORE: %03d │ OBSTACLES: %s %d/5",
                icon, status, nickname, score, bar, obstacles);
        }

        public static String header(String title, int width) {
            String border = BoxStyle.DOUBLE[4].repeat(width - 2);
            String titlePadded = " " + title + " ";
            int leftPad = (width - titlePadded.length()) / 2;
            int rightPad = width - titlePadded.length() - leftPad;

            return BoxStyle.DOUBLE[0] + border + BoxStyle.DOUBLE[1] + "\n" +
                   BoxStyle.DOUBLE[5] + " ".repeat(leftPad) + titlePadded + " ".repeat(rightPad) + BoxStyle.DOUBLE[5] + "\n" +
                   BoxStyle.DOUBLE[2] + border + BoxStyle.DOUBLE[3];
        }

        public static String progressBar(int current, int max, int width) {
            int filled = (current * width) / max;
            int empty = width - filled;
            return "[" + Symbols.PROGRESS_FULL.repeat(filled) +
                   Symbols.PROGRESS_EMPTY.repeat(empty) + "]";
        }
    }

    // 애니메이션 프레임
    public static class Animations {
        public static final String[] LOADING = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        public static final String[] DOTS = {"   ", ".  ", ".. ", "..."};
        public static final String[] BLOCKS = {"▁", "▂", "▃", "▄", "▅", "▆", "▇", "█"};
    }

    // 구분선 및 장식
    public static class Dividers {
        public static String solid(int width) {
            return "═".repeat(width);
        }

        public static String dashed(int width) {
            return "─".repeat(width);
        }

        public static String dotted(int width) {
            return "·".repeat(width);
        }

        public static String wave(int width) {
            return "~".repeat(width);
        }

        public static String heavySolid(int width) {
            return "━".repeat(width);
        }
    }
}
