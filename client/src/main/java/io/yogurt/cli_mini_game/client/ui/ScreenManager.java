package io.yogurt.cli_mini_game.client.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * Lanterna 기반 터미널 화면 관리자
 * 레트로 터미널 스타일 렌더링 지원
 */
public class ScreenManager {

    private Terminal terminal;
    private Screen screen;
    private TextGraphics textGraphics;

    public void initialize() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminal = terminalFactory.createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        textGraphics = screen.newTextGraphics();
    }

    public void clear() {
        screen.clear();
    }

    public void refresh() throws IOException {
        screen.refresh();
    }

    public void close() throws IOException {
        if (screen != null) {
            screen.stopScreen();
        }
        if (terminal != null) {
            terminal.close();
        }
    }

    public void drawText(int col, int row, String text) {
        drawText(col, row, text, TerminalTheme.Colors.FOREGROUND, TerminalTheme.Colors.BACKGROUND);
    }

    public void drawText(int col, int row, String text, TextColor foreground, TextColor background) {
        textGraphics.setForegroundColor(foreground);
        textGraphics.setBackgroundColor(background);
        textGraphics.putString(col, row, text);
    }

    /**
     * 레트로 터미널 스타일 박스 그리기 (이중선 테두리)
     */
    public void drawBox(int startCol, int startRow, int width, int height, String title) {
        String[] box = TerminalTheme.BoxStyle.DOUBLE;

        // Draw top border
        drawText(startCol, startRow, box[0] + box[4].repeat(width - 2) + box[1],
            TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);

        // Draw title if provided
        if (title != null && !title.isEmpty()) {
            String titleText = " " + title + " ";
            int titlePos = startCol + 2;
            drawText(titlePos, startRow, "╣" + titleText + "╠",
                TerminalTheme.Colors.TITLE, TerminalTheme.Colors.BACKGROUND);
        }

        // Draw sides
        for (int i = 1; i < height - 1; i++) {
            drawText(startCol, startRow + i, box[5], TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
            drawText(startCol + width - 1, startRow + i, box[5], TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
        }

        // Draw bottom border
        drawText(startCol, startRow + height - 1, box[2] + box[4].repeat(width - 2) + box[3],
            TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
    }

    /**
     * 단순한 박스 그리기 (가벼운 테두리)
     */
    public void drawLightBox(int startCol, int startRow, int width, int height, String title) {
        String[] box = TerminalTheme.BoxStyle.ROUNDED;

        drawText(startCol, startRow, box[0] + box[4].repeat(width - 2) + box[1],
            TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);

        if (title != null && !title.isEmpty()) {
            String titleText = " " + title + " ";
            int titlePos = startCol + 2;
            drawText(titlePos, startRow, titleText,
                TerminalTheme.Colors.ACCENT, TerminalTheme.Colors.BACKGROUND);
        }

        for (int i = 1; i < height - 1; i++) {
            drawText(startCol, startRow + i, box[5], TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
            drawText(startCol + width - 1, startRow + i, box[5], TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
        }

        drawText(startCol, startRow + height - 1, box[2] + box[4].repeat(width - 2) + box[3],
            TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
    }

    /**
     * 구분선 그리기
     */
    public void drawDivider(int col, int row, int width, DividerStyle style) {
        String divider = switch (style) {
            case SOLID -> TerminalTheme.Dividers.solid(width);
            case DASHED -> TerminalTheme.Dividers.dashed(width);
            case HEAVY -> TerminalTheme.Dividers.heavySolid(width);
            case DOTTED -> TerminalTheme.Dividers.dotted(width);
        };
        drawText(col, row, divider, TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
    }

    /**
     * ASCII 배너 그리기 (여러 줄)
     */
    public void drawBanner(int startCol, int startRow, String banner, TextColor color) {
        String[] lines = banner.split("\n");
        for (int i = 0; i < lines.length; i++) {
            drawText(startCol, startRow + i, lines[i], color, TerminalTheme.Colors.BACKGROUND);
        }
    }

    public enum DividerStyle {
        SOLID, DASHED, HEAVY, DOTTED
    }

    public void fillArea(int startCol, int startRow, int width, int height, char fillChar) {
        String line = String.valueOf(fillChar).repeat(width);
        for (int i = 0; i < height; i++) {
            drawText(startCol, startRow + i, line);
        }
    }

    public TerminalSize getTerminalSize() throws IOException {
        return screen.getTerminalSize();
    }

    public Screen getScreen() {
        return screen;
    }

    public Terminal getTerminal() {
        return terminal;
    }
}
