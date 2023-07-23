package me.videogamesm12.omegamap;

import me.videogamesm12.omegamap.config.OMConfig;
import me.videogamesm12.omegamap.util.LogUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Main
{
    private static Connection connection;

    public static void main(String[] args) throws SQLException
    {
        LogUtil.info("-- == ΩmegaΜap v1.0 == --");
        LogUtil.info("Written by videogamesm12 and Alco_Rs11");

        OMConfig.setup();

        final OMConfig config = OMConfig.getInstance();

        try
        {
            LogUtil.info("Connecting to PostgreSQL server...");
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    String.format(
                            "jdbc:postgresql://%s:%s/omegatrack",
                            config.getSql().getServerAddress(),
                            config.getSql().getServerPort()
                    ),
                    config.getSql().getUsername(),
                    config.getSql().getPassword());

            LogUtil.info("Successfully connected");
        }
        catch (ClassNotFoundException exception)
        {
            LogUtil.error("Failed to load PostgreSQL driver", exception);
            return;
        }
        catch (SQLException exception)
        {
            LogUtil.error("Failed to connect to SQL server", exception);
            return;
        }

        try
        {
            generateHeatMap(config.getRendering().getImageRadius());
            LogUtil.info("Successfully generated the heatmap!");
        }
        catch (Throwable ex)
        {
            LogUtil.error("Failed to generate heatmap", ex);
        }

        LogUtil.info("Shutting down SQL connection...");
        connection.close();

        LogUtil.info("Shutdown completed, have a nice day!");
    }

    public static void generateHeatMap(int radius) throws SQLException, IOException
    {
        // Get the data first
        LogUtil.info("Sending SQL query for data...");
        final PreparedStatement statement = connection.prepareStatement("SELECT x, z FROM coordinates;");
        final ResultSet set = statement.executeQuery();
        LogUtil.info("Query completed");
        //--
        LogUtil.info("Building image foundation");
        final BufferedImage output = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_BYTE_GRAY);
        final WritableRaster raster = output.getRaster();
        final SampleModel model = raster.getSampleModel();
        final DataBuffer buffer = raster.getDataBuffer();
        //--
        final int scale = OMConfig.getInstance().getRendering().getScale();
        final int startingColor = OMConfig.getInstance().getRendering().getStartingColor();
        final int incrementColor = OMConfig.getInstance().getRendering().getIncrementColor();

        LogUtil.info("Building image");
        int i = 0;
        while (set.next())
        {
            if (++i % 1000000 == 0)
            {
                System.out.println(i);
            }

            int posX = set.getInt(1);
            int posY = set.getInt(2);

            int chunkX = ((Double) Math.floor((double) posX / scale)).intValue();
            int chunkY = ((Double) Math.floor((double) posY / scale)).intValue();

            int imageX = chunkX + radius;
            int imageY = chunkY + radius;

            if (imageX < 0 || imageX >= 2 * radius || imageY < 0 || imageY >= 2 * radius)
            {
                // Code commented out because it slowed things down significantly
                //LogUtil.info("Coordinate set (" + posX + ", " + posY + ") was not drawn because it is outside of the radius even with the scale (" + imageX + ", " + imageY + ")");
                continue;
            }

            int color = model.getSample(imageX, imageY, 0, buffer);

            if (color == 0)
            {
                color = startingColor;
            }
            else if (color >= 255)
            {
                color = 255;
            }
            else
            {
                color = color + incrementColor;
            }

            model.setSample(imageX, imageY, 0, color, buffer);
        }

        LogUtil.info("Writing image to disk...");
        ImageIO.write(output, "png", new File("heatmap_flatlands.png"));
    }
}