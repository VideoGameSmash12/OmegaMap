package me.videogamesm12.omegamap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.Getter;
import me.videogamesm12.omegamap.util.LogUtil;

import java.io.*;
import java.nio.file.Files;

public class OMConfig
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private static OMConfig instance;
    //--
    @Getter
    private PostgreSQL sql = new PostgreSQL();
    @Getter
    private Render rendering = new Render();

    public static void setup()
    {
        LogUtil.info("Loading configuration...");
        final File file = new File("config.json");
        OMConfig result;

        if (file.exists())
        {
            try
            {
                result = gson.fromJson(new BufferedReader(new FileReader(file)), OMConfig.class);
                LogUtil.info("Configuration successfully loaded.");
            }
            catch (IOException ex)
            {
                LogUtil.error("An error occurred whilst attempting to load the configuration", ex);
                result = new OMConfig();
            }
            catch (JsonParseException ex)
            {
                LogUtil.error("An error occurred whilst attempting to parse the configuration", ex);
                result = new OMConfig();
            }
        }
        else
        {
            LogUtil.info("Generating new configuration...");
            result = new OMConfig();

            try
            {
                LogUtil.info("Writing new configuration to disk...");
                final BufferedWriter writer = Files.newBufferedWriter(file.toPath());
                writer.write(gson.toJson(result));
                writer.close();
                LogUtil.info("Write completed - you won't need to worry about seeing this again.");
            }
            catch (IOException ex)
            {
                LogUtil.info("Failed to write default configuration", ex);
            }
        }

        instance = result;
    }

    @Getter
    public static class PostgreSQL
    {
        private String serverAddress = "localhost";

        private int serverPort = 5432;

        private String serverDatabase = "omegatrack";

        private String username = "alexandria";

        private String password = "alexandria";

        private String world = "minecraft:flatlands";
    }

    @Getter
    public static class Render
    {
        private int imageRadiusHorizontal = 5000;

        private int imageRadiusVertical = 5000;

        private int scale = 16;

        private int startingColor = 50;

        private int incrementColor = 2;
    }
}
