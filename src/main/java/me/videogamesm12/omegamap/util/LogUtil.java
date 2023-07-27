package me.videogamesm12.omegamap.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil
{
    private static final Logger logger = Logger.getLogger("OmegaMap");

    static
    {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT %4$s]: %5$s %n");
    }

    public static void info(String message)
    {
        logger.info(message);
    }

    public static void info(String message, Throwable ex)
    {
        logger.log(Level.INFO, message, ex);
    }

    public static void warn(String message)
    {
        logger.log(Level.WARNING, message);
    }

    public static void warn(String message, Throwable ex)
    {
        logger.log(Level.WARNING, message, ex);
    }

    public static void error(String message)
    {
        logger.log(Level.SEVERE, message);
    }

    public static void error(String message, Throwable ex)
    {
        logger.log(Level.SEVERE, message, ex);
    }
}
