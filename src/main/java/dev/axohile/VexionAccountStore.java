package dev.axohile;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class VexionAccountStore {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("vexionclient-accounts.txt");
    private static final List<String> ACCOUNTS = new ArrayList<>();
    private static String selected = "";

    private VexionAccountStore() {}

    public static void load() {
        ACCOUNTS.clear();
        try {
            if (Files.exists(FILE)) {
                for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                    String value = line.trim();
                    if (value.matches("[A-Za-z0-9_]{3,16}") && !ACCOUNTS.contains(value)) {
                        ACCOUNTS.add(value);
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("[VexionClient] Hesap listesi okunamadı: " + exception.getMessage());
        }
        if (!ACCOUNTS.contains(selected)) {
            selected = ACCOUNTS.isEmpty() ? "" : ACCOUNTS.get(0);
        }
    }

    public static List<String> getAccounts() {
        return List.copyOf(ACCOUNTS);
    }

    public static String getSelected() {
        return selected;
    }

    public static void select(String name) {
        if (ACCOUNTS.contains(name)) {
            selected = name;
            save();
        }
    }

    public static boolean add(String name) {
        String value = name == null ? "" : name.trim();
        if (!value.matches("[A-Za-z0-9_]{3,16}") || ACCOUNTS.contains(value)) {
            return false;
        }
        ACCOUNTS.add(value);
        selected = value;
        save();
        return true;
    }

    public static void deleteSelected() {
        if (!selected.isEmpty()) {
            ACCOUNTS.remove(selected);
            selected = ACCOUNTS.isEmpty() ? "" : ACCOUNTS.get(0);
            save();
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.write(FILE, ACCOUNTS, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("[VexionClient] Hesap listesi kaydedilemedi: " + exception.getMessage());
        }
    }
}
