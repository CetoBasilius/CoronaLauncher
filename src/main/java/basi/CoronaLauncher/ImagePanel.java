package basi.CoronaLauncher;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private static final String FILENAME_ICON_DEFAULT = "/Icon.png";

	public ImagePanel(String filename) {
		try {
			File imageFile = new File(filename);
			if (!imageFile.exists()) {
				filename = FILENAME_ICON_DEFAULT;
				InputStream imageStream = getClass().getResourceAsStream(filename);
				BufferedImage myPicture = ImageIO.read(imageStream);
				JLabel picLabel = new JLabel(new ImageIcon(myPicture));
				add(picLabel);
			} else {
				BufferedImage myPicture = ImageIO.read(imageFile);
				JLabel picLabel = new JLabel(new ImageIcon(myPicture));
				add(picLabel);
			}
		} catch (IOException ex) {

		}
	}
}