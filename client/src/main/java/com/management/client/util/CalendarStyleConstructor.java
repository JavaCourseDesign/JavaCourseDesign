package com.management.client.util;

public class CalendarStyleConstructor {
    private static String style=".style1-entry {\n" +
            "    -fx-background-color: -style1-color;\n" +
            "    -fx-border-color: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-entry:selected {\n" +
            "    -fx-background-color: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-entry-time-label, .style1-entry-title-label {\n" +
            "    -fx-text-fill: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-entry-small:selected {\n" +
            "    -fx-background-color: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-entry-small-full-day {\n" +
            "    -fx-background-color: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-entry-small-title-label-full-day {\n" +
            "    -fx-text-fill: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-entry-small-full-day:selected {\n" +
            "    -fx-background-color: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-visibility-checkbox > .box,\n" +
            ".style1-source-grid-item-box {\n" +
            "    -fx-background-color: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-visibility-checkbox:focused > .box {\n" +
            "    -fx-background-color: -style1-color;\n" +
            "}\n" +
            "\n" +
            ".style1-entry-popover-title {\n" +
            "    -fx-text-fill: -style1-color !important;\n" +
            "}\n" +
            "\n" +
            ".style1-calendar-header {\n" +
            "    -fx-background-color: -style1-color;\n" +
            "}";
    public static String stringToStyle(String s) {
        String color = stringToRGBAColor(s, 0.9);
        return style.replace("-style1-color", color);
    }

    public static String stringToStyle(String s, String color) {
        return style.replace("-style1-color", color);
    }

    public static String stringToRGBAColor(String input, double alpha) {
        int hashCode = input.hashCode();
        int positiveHashCode = Math.abs(hashCode);
        int red = positiveHashCode % 256;
        positiveHashCode /= 256;
        int green = positiveHashCode % 256;
        positiveHashCode /= 256;
        int blue = positiveHashCode % 256;
        return "rgba(" + red + ", " + green + ", " + blue + ", " + alpha + ")";
    }
}
