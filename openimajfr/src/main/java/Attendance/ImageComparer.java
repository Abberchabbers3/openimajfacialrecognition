package Attendance;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageComparer {

	int[][][]image;
	
	public ImageComparer(BufferedImage picture) {
		image = new int[picture.getWidth(null)][picture.getHeight(null)][3];
	}
	
	public Image detectWhite(BufferedImage i) {
		for(int r=0; r<i.getWidth(null); r++) {
			for(int c=0; c<i.getHeight(null); c++) {
				int p = i.getRGB(r,c);
				for(int x=3;x>1;x--) {
					image[r][c][x-1]=(p>>(x*4) & 255);
				}
				if(image[r][c][0]+image[r][c][1]+image[r][c][2]<400) {
					Color White = new Color(0,255,0);
					int rgb = White.getRGB();
					i.setRGB(r, c, rgb);
				}
			}
			//get red
		    //int r = (p>>16) & 0xff;
		    //get green
		    //int g = (p>>8) & 0xff;
		    //get blue
		    //int b = p & 0xff;
			//int pred = (p>>16) & 0xff;
			//System.out.print(pred);
		}
		return i;
	}
	public int greenCount(BufferedImage i) {
		int count = 0;
		for(int r=0; r<i.getWidth(null); r++) {
			for(int c=0; c<i.getHeight(null); c++) {
				int p = i.getRGB(r,c);
				for(int x=3;x>1;x--) {
					image[r][c][x-1]=(p>>(x*4) & 255);
				}
				if(image[r][c][1]==255) {
					count++;
				}
			}
		}
		return count;
	}
}