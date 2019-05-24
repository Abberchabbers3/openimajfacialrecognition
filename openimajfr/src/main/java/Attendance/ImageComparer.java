package Attendance;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImageComparer {

	int[][][]image;
	
	public ImageComparer(BufferedImage picture) {
		image = new int[picture.getWidth(null)][picture.getHeight(null)][3];
	}
	
	@SuppressWarnings("unused")
	public Image detectWhite(BufferedImage i) {
		int count = 0;
		for(int r=0; r<i.getWidth(null); r++) {
			for(int c=0; c<i.getHeight(null); c++) {
				int p = i.getRGB(r,c);
				for(int x=3;x>0;x--) {
					image[r][c][x-1]=(p>>(x*4) & 0xff);
				}
				if(image[r][c][0]+image[r][c][1]+image[r][c][2]<240) {
					Color GREEN = new Color(0,255,0);
					int rgb = GREEN.getRGB();
					i.setRGB(r, c, rgb);
					count++;
				}
			}
			//get red
		    //int r = (p>>16) & 0xff;
		    //get green
		    //int g = (p>>8) & 0xff;
		    //get blue
		    //int b = p & 0xff;
			//System.out.print(pred);
		}
//		System.out.println(count+" Height"+i.getHeight()+"Width"+i.getWidth()+i);
		return i;
	}
	
	public int greenCount(BufferedImage i,int startrow,int endrow,int startcol,int endcol) {
//		System.out.print("width"+i.getHeight());
//		System.out.print("height"+i.getWidth());
//		System.out.print("rows:"+startrow+"-"+endrow);
//		System.out.print("cols:"+startcol+"-"+endcol);
//		System.out.println();
		int count = 0;
		for(int r=startrow; r<endrow; r++) {
			for(int c=startcol; c<endcol; c++) {
				int p = i.getRGB(r,c);
				if(p==-16711936) {
					count++;
				}
			}
		}
		return count;
	}
	
	public BufferedImage setBackground(BufferedImage i,Color white) {
		ArrayList<Integer> colors = new ArrayList<Integer>();
		for(int r=0; r<i.getWidth(null); r++) {
			for(int c=0; c<i.getHeight(null); c++) {
				int p = i.getRGB(r,c);
				colors.add(p);
				System.out.print("color"+p+"-");
			}
		}
		int cmax = mode(colors);
		System.out.println("best:"+cmax);
		for(int r=0; r<i.getWidth(null); r++) {
			for(int c=0; c<i.getHeight(null); c++) {
				i.setRGB(r, c, cmax);
			}
		}
		return i;

	}
	
	private int mode(ArrayList<Integer> color) {
		int mode = color.get(0);
	    int maxCount = 0;
	    for (int i = 0; i < color.size(); i++) {
	        int value = color.get(i);
	        int count = 1;
	        for (int j = 0; j < color.size(); j++) {
	            if (closeenough(color.get(j),mode))
	                count++;
	            if (count > maxCount) {
	                mode = value;
	                maxCount = count;
	            }
	        }
	    }
	    return mode;
	}

	private boolean closeenough(Integer p,Integer cm) {
		int cr=(p>>16) & 0xff;
		int cg=(p>>8) & 0xff;
		int cb= p & 0xff;
		int mr=(p>>16) & 0xff;
		int mg=(p>>8) & 0xff;
		int mb= p & 0xff;
		if(Math.abs(cr-mr)<=3&&Math.abs(cg-mg)<=3&&Math.abs(cb-mb)<=3) {
			return true;
		}
		return false;
	}
}