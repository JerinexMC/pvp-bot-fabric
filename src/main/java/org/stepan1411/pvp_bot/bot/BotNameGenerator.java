package org.stepan1411.pvp_bot.bot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BotNameGenerator {
    
    private static final List<String> PREFIXES = new ArrayList<>();
    private static final List<String> SUFFIXES = new ArrayList<>();
    private static final Random random = new Random();
    

    private static final String SPECIAL_NAMES_URL = "https://stepan1411.github.io/pvp-bot-fabric/special_names.json";
    

    private static List<String> specialNames = new ArrayList<>();
    private static int spawnChance = 10;
    private static boolean specialNamesLoaded = false;
    private static long lastLoadTime = 0;
    private static final long RELOAD_INTERVAL = 60000;
    

    private static final Pattern CAMEL_CASE = Pattern.compile("([A-Z][a-z0-9]+)");
    

    private static final String[] BASE_NAMES = {
    "BLMcheat",
    "CORZZ",
    "FoodChuckster123",
    "mario",
    "Legendarryy",
    "Deuey",
    "Gingerds",
    "itchyworms",
    "sweatgod",
    "Rozacks",
    "knqckback",
    "Rivise",
    "HellSinky",
    "SuperSaguate",
    "Prusso",
    "TsundereAsikuu",
    "ninorc15",
    "STORMPLAY",
    "LeARoyJames",
    "ShadowZeuss",
    "XTkidler",
    "DIEGOANRSE",
    "Dtrminacion",
    "SWORDARTONLINE",
    "DivineRevival",
    "Ibrahimblaze",
    "AdamAdiss",
    "EDERMILITAO",
    "sthefano",
    "QuiltedSun",
    "Anderryuu",
    "EeFwOn",
    "zzOrionn",
    "ClownMayhem",
    "SoyAlvaro_",
    "RealCracker",
    "jvbz",
    "Flonzy_",
    "Pxrfect_",
    "pinkscars",
    "Z3irq",
    "Crozzcr",
    "Hype__2",
    "TRSDE",
    "GiuseppeZannoti",
    "SnupySG",
    "Proffik",
    "ImDyro",
    "SOWAOTS",
    "Jasonjade09",
    "Yxuz",
    "AwsomebirdXD",
    "DIDDYVAUGHN",
    "AlongTheChaos",
    "Henr_yy",
    "sharpifymc",
    "DaddyCapit",
    "MaximoPrro",
    "Dolp2inX",
    "_Mikusch",
    "Slupsi",
    "clapped5998",
    "Angelman21",
    "J3efis",
    "Veldron_",
    "HAGNBATOP300"
};
    
    static {
        initializeParts();
        loadSpecialNames();
    }
    
    
    private static void loadSpecialNames() {

        long currentTime = System.currentTimeMillis();
        if (specialNamesLoaded && (currentTime - lastLoadTime) < RELOAD_INTERVAL) {
            return;
        }
        

        try {
            System.out.println("[PVP Bot] Trying to load special names from URL: " + SPECIAL_NAMES_URL);
            String json = downloadJson(SPECIAL_NAMES_URL);
            if (json != null && parseSpecialNames(json)) {
                System.out.println("[PVP Bot] Successfully loaded special names from URL");
                specialNamesLoaded = true;
                lastLoadTime = currentTime;
                return;
            }
        } catch (Exception e) {
            System.out.println("[PVP Bot] Failed to load from URL: " + e.getMessage());
        }
        

        if (!specialNamesLoaded) {
            try {
                System.out.println("[PVP Bot] Loading special names from local resources");
                InputStream stream = BotNameGenerator.class.getResourceAsStream("/special_names.json");
                if (stream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }
                    reader.close();
                    
                    if (parseSpecialNames(json.toString())) {
                        System.out.println("[PVP Bot] Successfully loaded special names from resources");
                        specialNamesLoaded = true;
                        lastLoadTime = currentTime;
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("[PVP Bot] Failed to load from resources: " + e.getMessage());
            }
            

            System.out.println("[PVP Bot] Using default special names");
            specialNames.add("nantag");
            specialNames.add("Stepan1411");
            spawnChance = 10;
            specialNamesLoaded = true;
            lastLoadTime = currentTime;
        }
    }
    
    
    private static String downloadJson(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "PVP-Bot-Fabric");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            }
        } catch (Exception e) {

        }
        return null;
    }
    
    
    private static boolean parseSpecialNames(String json) {
        try {
            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            
            if (obj.has("special_names") && obj.get("special_names").isJsonArray()) {
                specialNames.clear();
                obj.getAsJsonArray("special_names").forEach(element -> {
                    String name = element.getAsString();
                    if (name != null && !name.isEmpty() && name.length() <= 16) {
                        specialNames.add(name);
                    }
                });
            }
            
            if (obj.has("spawn_chance")) {
                spawnChance = obj.get("spawn_chance").getAsInt();
                if (spawnChance < 0) spawnChance = 0;
                if (spawnChance > 100) spawnChance = 100;
            }
            
            return !specialNames.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    
    public static List<String> getSpecialNames() {
        return new ArrayList<>(specialNames);
    }
    
    
    public static int getSpawnChance() {
        return spawnChance;
    }
    
    
    private static void initializeParts() {
        Set<String> prefixSet = new HashSet<>();
        Set<String> suffixSet = new HashSet<>();
        
        for (String name : BASE_NAMES) {
            List<String> parts = splitCamelCase(name);
            if (parts.size() >= 2) {
                prefixSet.add(parts.get(0));
                suffixSet.add(parts.get(parts.size() - 1));
            } else if (parts.size() == 1 && parts.get(0).length() > 4) {

                prefixSet.add(parts.get(0));
            }
        }
        
        PREFIXES.addAll(prefixSet);
        SUFFIXES.addAll(suffixSet);
    }
    
    
    private static List<String> splitCamelCase(String name) {
        List<String> parts = new ArrayList<>();
        Matcher matcher = CAMEL_CASE.matcher(name);
        while (matcher.find()) {
            parts.add(matcher.group(1));
        }
        return parts;
    }
    
    
    public static String generateUniqueName() {

        boolean useSpecialNames = org.stepan1411.pvp_bot.bot.BotSettings.get().isUseSpecialNames();
        
        loadSpecialNames();
        
        Set<String> existingBots = BotManager.getAllBots();
        

        if (useSpecialNames && !specialNames.isEmpty() && random.nextInt(100) < spawnChance) {
            String specialName = specialNames.get(random.nextInt(specialNames.size()));
            if (!existingBots.contains(specialName)) {
                return specialName;
            }
        }
        
        for (int attempt = 0; attempt < 100; attempt++) {
            String name = generateName();

            if (name.length() <= 16 && !existingBots.contains(name)) {
                return name;
            }
        }
        

        for (int num = 1; num < 1000; num++) {
            String name = "Bot" + num;
            if (!existingBots.contains(name)) {
                return name;
            }
        }
        return "Bot" + System.currentTimeMillis() % 10000;
    }
    
    
    private static String generateName() {
        for (int i = 0; i < 20; i++) {
            String prefix = PREFIXES.get(random.nextInt(PREFIXES.size()));
            String suffix = SUFFIXES.get(random.nextInt(SUFFIXES.size()));
            String name = prefix + suffix;
            if (name.length() <= 16) {
                return name;
            }
        }

        String prefix = PREFIXES.get(random.nextInt(PREFIXES.size()));
        if (prefix.length() > 12) prefix = prefix.substring(0, 12);
        return prefix + random.nextInt(1000);
    }
}
