package dev.mobileopt;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class OptConfig {
    public static boolean noHurtCam = true;
    public static boolean noFog = true;
    public static boolean noVignette = true;
    public static boolean noPotionParticles = true;
    public static boolean fullbright = true;

    public static int mobDistance = 48;
    public static int itemDistance = 24;

    public static final int[] DISTANCES = {0, 16, 24, 32, 48, 64, 96};

    private OptConfig() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("mobileopt.properties");
    }

    public static int nextDistance(int current) {
        int idx = 0;
        for (int i = 0; i < DISTANCES.length; i++) {
            if (DISTANCES[i] == current) {
                idx = i;
                break;
            }
        }
        return DISTANCES[(idx + 1) % DISTANCES.length];
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) {
            save();
            return;
        }
        Properties p = new Properties();
        try (var in = Files.newInputStream(f)) {
            p.load(in);
            noHurtCam = Boolean.parseBoolean(p.getProperty("noHurtCam", "true"));
            noFog = Boolean.parseBoolean(p.getProperty("noFog", "true"));
            noVignette = Boolean.parseBoolean(p.getProperty("noVignette", "true"));
            noPotionParticles = Boolean.parseBoolean(p.getProperty("noPotionParticles", "true"));
            fullbright = Boolean.parseBoolean(p.getProperty("fullbright", "true"));
            mobDistance = parseInt(p.getProperty("mobDistance"), 48);
            itemDistance = parseInt(p.getProperty("itemDistance"), 24);
        } catch (IOException | RuntimeException ignored) {
        }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("noHurtCam", Boolean.toString(noHurtCam));
        p.setProperty("noFog", Boolean.toString(noFog));
        p.setProperty("noVignette", Boolean.toString(noVignette));
        p.setProperty("noPotionParticles", Boolean.toString(noPotionParticles));
        p.setProperty("fullbright", Boolean.toString(fullbright));
        p.setProperty("mobDistance", Integer.toString(mobDistance));
        p.setProperty("itemDistance", Integer.toString(itemDistance));
        try (var out = Files.newOutputStream(file())) {
            p.store(out, "MobileOpt");
        } catch (IOException ignored) {
        }
    }

    private static int parseInt(String s, int def) {
        if (s == null) return def;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
