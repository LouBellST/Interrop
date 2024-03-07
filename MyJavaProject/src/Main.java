
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import org.h2.tools.SimpleResultSet;

import fc.Database.JdbcUtils.*;

public class Main{
    public static void main(String[] args){
        String connUrl = "jdbc:h2:./h2database";
        String username = "sa";
        String password = "";
        // Le fichier créé va être "h2database.mv.db" dans le repertoire de l'application Java
        try (Connection conn = DriverManager.getConnection(connUrl, username,password))
        {

            // EXERCICE 1
            Statement stat = conn.createStatement();
            stat.execute("CREATE ALIAS GAUSS FOR \"Main.getGaussiennne\""); 
            
            PreparedStatement prep = conn.prepareStatement("SELECT * FROM GAUSS(5) ORDER BY X,Y");
            //((java.sql.PreparedStatement) prep).setInt(1, 2);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "/" + rs.getInt(2)+ "/" + rs.getInt(3));
            }
            prep.close(); 

            System.out.println("\n");
            PreparedStatement prep2 = conn.prepareStatement("SELECT * FROM GAUSS(7) ORDER BY X,Y");
            //((java.sql.PreparedStatement) prep).setInt(1, 2);
            ResultSet rs2 = prep2.executeQuery();
            while (rs2.next()) {
                System.out.println(rs2.getInt(1) + "/" + rs2.getInt(2) + "/" + rs2.getInt(3));
            }
            prep2.close(); 


            // EXERCICE 2
            stat.execute("CREATE ALIAS RGBIMAGE FOR \"Main.getRGB_Image\""); 
            
            PreparedStatement rgbImage = conn.prepareStatement("SELECT * FROM RGBIMAGE(\'MyJavaProject/src/building.png\') ORDER BY X,Y");
            //((java.sql.PreparedStatement) prep).setInt(1, 2);
            ResultSet rs3 = rgbImage.executeQuery();
            while (rs3.next()) {
                System.out.println(rs3.getInt(1) + "/" + rs3.getInt(2) + " RGB:" + rs3.getInt(3) + "/" + rs3.getInt(4) + "/" + rs3.getInt(5));
            }
            rgbImage.close(); 
        }
        catch (Exception e){
            e.printStackTrace(System.err);
        }

        File file = new File("h2database.mv.db");
        file.delete();
    }

    /* 
    public void runSQL(String fileName){
        // Tout nom de fichier relatif DOIT commencer par ./ avec H2, sinon le driver refuse la chaine
     
        String connUrl = "jdbc:h2:./h2database";
        String username = "sa";
        String password = "";
    
        // Le fichier créé va être "h2database.mv.db" dans le repertoire de l'application Java
    
        try (Connection conn = DriverManager.getConnection(connUrl, username, password))
        {
            ScriptRunner runner = new ScriptRunner(conn, true, true);
            FileReader file = new FileReader(fileName);
            runner.runScript(new BufferedReader(file));		
    
            System.out.println();
    
            try (Statement st = conn.createStatement()) {
                st.execute("SHUTDOWN");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }
    */

    public static ResultSet getGaussiennne(Connection conn, Integer size) throws SQLException{
        SimpleResultSet rs = new SimpleResultSet();
        // CREATION DES TABLES SQL avec nom, type, et le reste pas important 
        rs.addColumn("X", Types.INTEGER, 10, 0); 
        rs.addColumn("Y", Types.INTEGER, 10, 0); 
        rs.addColumn("valeur", Types.DOUBLE, 10, 0); 
        //String url = conn.getMetaData().getURL();
        //if (url.equals("jdbc:columnlist:connection")) {
        // return rs;
        //}
        // Ajoute des lignes (rangees, occurrences de la table)

        for (int x = -size/2; x < size/2+1; x++) {
            for (int y = -size/2; y < size/2+1; y++) {
                double v = size * Math.exp(-(x*x)-(y*y)); // fonction gaussienne
                rs.addRow(x, y, v); // AJOUT D'UNE LIGNE A LA TABLE AVEC LES ATTRIBUTS EN PARAM
            }
        }
        return rs;
    } 
    
    public static ResultSet getRGB_Image(Connection conn, String path) throws SQLException, IOException{
        SimpleResultSet rs = new SimpleResultSet();
        // AJOUT DES TABLES SQL 
        rs.addColumn("X", Types.INTEGER, 10, 0);
        rs.addColumn("Y", Types.INTEGER, 10, 0); 
        rs.addColumn("R", Types.INTEGER, 10, 0);
        rs.addColumn("G", Types.INTEGER, 10, 0); 
        rs.addColumn("B", Types.INTEGER, 10, 0);
        //String url = conn.getMetaData().getURL();
        //if (url.equals("jdbc:columnlist:connection")) {
        // return rs;
        //}
        // Ajoute des lignes (rangees, occurrences de la table)
        File file = new File(path);
        BufferedImage img = ImageIO.read(file);

        for (int x=0; x < img.getWidth(); x++) {
            for (int y=0; y < img.getHeight(); y++) {
                int color = img.getRGB(x, y);

                // Components will be in the range of 0..255:
                int blue = color & 0xff;
                int green = (color & 0xff00) >> 8;
                int red = (color & 0xff0000) >> 16; 
                rs.addRow(x, y, red, green, blue);  // AJOUT D'UNE LIGNE A LA TABLE AVEC LES X,Y et R,G,B DE CHAQUE PX DE L'IMAGE
            }
        }
        return rs;
    } 
}

