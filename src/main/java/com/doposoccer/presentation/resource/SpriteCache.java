package com.doposoccer.presentation.resource;

import com.doposoccer.domain.Log;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loads each image of the "sprites" folder only once and shares it. Maven packs that folder
 * into the class path, so the images are found both when running from the IDE and from the jar
 */
public class SpriteCache {

    /** Folder of the sprites on the class path. */
    public static final String FOLDER = "/sprites/";

    /** Extension of the sprite files. */
    public static final String EXTENSION = ".png";

    private static final String GRAY_PREFIX = "gray:";

    private final Map<String, BufferedImage> images = new HashMap<>();
    private final Set<String> missing = new HashSet<>();

    /**
     * Returns the image with the given key, loading it the first time
     * @param key name of the file without extension
     * @return the image, or null if the file does not exist or cannot be read
     */
    public synchronized BufferedImage get(String key) {
        BufferedImage image = images.get(key);
        if (image != null || missing.contains(key)) {
            return image;
        }
        image = load(key);
        if (image == null) {
            missing.add(key);
        } else {
            images.put(key, image);
        }
        return image;
    }

    /**
     * Returns a gray copy of an image, used for disabled buttons
     * @param key name of the original image
     * @return the gray image, or null if the original does not exist
     */
    public synchronized BufferedImage getGray(String key) {
        String grayKey = GRAY_PREFIX + key;
        BufferedImage gray = images.get(grayKey);
        if (gray != null) {
            return gray;
        }
        BufferedImage original = get(key);
        if (original == null) {
            return null;
        }
        gray = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ColorConvertOp toGray = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        toGray.filter(original, gray);
        images.put(grayKey, gray);
        return gray;
    }

    private BufferedImage load(String key) {
        String path = FOLDER + key + EXTENSION;
        try (InputStream input = SpriteCache.class.getResourceAsStream(path)) {
            if (input == null) {
                return null;
            }
            return ImageIO.read(input);
        } catch (IOException e) {
            Log.record(e);
            return null;
        }
    }
}
