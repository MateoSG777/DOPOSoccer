package com.doposoccer.presentation;

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
 * Sprite cache (Flyweight pattern): every image is read from disk only once
 * and then shared by every object that draws it.
 * Images live in the resources folder "/sprites/&lt;key&gt;.png".
 */
public class SpriteCache {

    /** Folder of the sprites inside the resources. */
    public static final String FOLDER = "/sprites/";

    /** Extension of the sprite files. */
    public static final String EXTENSION = ".png";

    /** Prefix of the keys of the gray (disabled) versions. */
    private static final String GRAY_PREFIX = "gray:";

    /** Images already loaded, indexed by key. */
    private final Map<String, BufferedImage> images = new HashMap<>();

    /** Keys whose file does not exist, so they are not searched again every frame. */
    private final Set<String> missing = new HashSet<>();

    /** Creates an empty cache. */
    public SpriteCache() {
        // Images are loaded on demand.
    }

    /**
     * Returns the image with the given key, loading it the first time.
     *
     * @param key name of the file without extension (for example "korea")
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
     * Returns a gray copy of an image, used for disabled buttons.
     *
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

    /**
     * Reads an image from the resources.
     *
     * @param key name of the file without extension
     * @return the image, or null if it does not exist or cannot be read
     */
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
