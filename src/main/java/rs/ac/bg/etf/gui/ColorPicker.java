package rs.ac.bg.etf.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author sd213335m
 */
public class ColorPicker {

    private final Map<String, Color> colorsMap = new HashMap<>();
    private final Set<Color> alreadyChosenColors = new HashSet<>();
    private final Random random = new Random();

    private static ColorPicker instance = null;

    private ColorPicker() {
    }

    public static ColorPicker getInstance() {
        if (instance == null) {
            instance = new ColorPicker();
        }
        return instance;
    }

    public static Color getColor(String meetingName) {
        ColorPicker colorPicker = ColorPicker.getInstance();
        return colorPicker.colorsMap.computeIfAbsent(meetingName, key -> colorPicker.randomlyChooseColor());
    }

    private Color randomlyChooseColor() {
        Color newColor = getRandomlyNextColor();
        while (alreadyChosenColors.contains(newColor)) {
            newColor = getRandomlyNextColor();
        }
        alreadyChosenColors.add(newColor);
        return newColor;
    }

    private Color getRandomlyNextColor() {
        final float hue = random.nextFloat();
        // Saturation between 0.1 and 0.3
        final float saturation = (random.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        return Color.getHSBColor(hue, saturation, luminance);
    }

}
