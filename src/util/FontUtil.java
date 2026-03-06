/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author ransel
 */
import java.awt.Font;
import java.io.InputStream;

public class FontUtil {
    private static Font kinetika;

    static {
        try {
            InputStream is = FontUtil.class.getResourceAsStream("/fonts/kinetika-bold.ttf");
            kinetika = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
            kinetika = new Font("SansSerif", Font.BOLD, 16); // fallback
        }
    }

    public static Font getKinetikaFont(float size) {
        return kinetika.deriveFont(size);
    }
}