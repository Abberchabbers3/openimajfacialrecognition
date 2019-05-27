package Attendance;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImageComparer {

	int[][][]image;
	
	public ImageComparer(BufferedImage picture) {
		image = new int[picture.getHeight(null)][picture.getWidth(null)][3];
	}
	
	@SuppressWarnings("unused")
	public Image detectWhite(BufferedImage i,boolean change) {
		int count = 0;
		for(int r=0; r<i.getHeight(null); r++) {
			for(int c=0; c<i.getWidth(null); c++) {
				int p = i.getRGB(c,r);
				for(int x=3;x>0;x--) {
					image[r][c][x-1]=(p>>(x*4) & 0xff);
				}
				if(change&&image[r][c][0]+image[r][c][1]+image[r][c][2]<240) {
					Color GREEN = new Color(0,255,0);
					int rgb = GREEN.getRGB();
					i.setRGB(c, r, rgb);
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
	
	public BufferedImage getedeges(BufferedImage i) {
		i = detectedges(i,100);
		return i;

	}
	public BufferedImage setBackground(BufferedImage i,int ox,int oy,int wr,int hr,Color bg) {
		boolean inface=true;
		boolean magenta=false;
		Graphics2D g = i.createGraphics();
		g.setColor(Color.magenta);
		System.out.println(g.getStroke());
		g.drawOval(ox, oy, wr, hr);
		for(int r=0;r<i.getHeight();r++) {
			for(int c=0;c<i.getWidth();c++) {
				int rgb=i.getRGB(c, r);
				Color clr = new Color((rgb>>16) & 0xff,(rgb>>8) & 0xff,rgb & 0xff);
				if(clr.equals(Color.magenta)) {
					if(magenta==false) {
						inface = !inface;
						magenta=true;
					}
					rgb = bg.getRGB();
					i.setRGB(c, r, rgb);
				}
				else if(!clr.equals(Color.magenta)) {
					magenta=false;
				}
				if(!inface) {
					rgb = bg.getRGB();
					i.setRGB(c, r, rgb);
				}
			}
		}
		return i;

	}
	
	private BufferedImage detectedges(BufferedImage i,int edgeDist) {
		int leftPixel;
	    int rightPixel;
	    int[][] pixels = image2D(i);
	    for (int r = 0;r<pixels.length;r++) {
	    	for (int c = 0;c<pixels[r].length;c++){
	    		leftPixel=pixels[r][c] ;
	    		if(c==pixels[r].length-1) {
	    			rightPixel=pixels[r][c-1];
	    		}
	    		else {
	    			rightPixel=pixels[r][c+1];
	    		}
	    		double dist = getdistance(rightPixel,leftPixel);
	    		System.out.println(" distance:"+dist);
	    		if (dist > edgeDist) {
	    			int rgb= new Color(0,0,0).getRGB();
	    			i.setRGB(c, r, rgb);
	    			pixels[r][c]=rgb;
	    		}
	    		else {
	    			int rgb= new Color(255,255,255).getRGB();
	    			i.setRGB(c, r, rgb);
	    			pixels[r][c]=rgb;
	    		}
	    	}
	    }
	    return i;
	}

	private double getdistance(int rightPixel, int leftPixel) {
		int r1=(leftPixel>>16) & 0xff;
		int g1=(leftPixel>>8) & 0xff;
		int b1= leftPixel & 0xff;
		System.out.print("1:red:"+r1+" blue:"+b1+" green:"+g1);
		int r2=(rightPixel>>16) & 0xff;
		int g2=(rightPixel>>8) & 0xff;
		int b2= rightPixel & 0xff;
		System.out.print(" 2:red:"+r2+" blue:"+b2+" green:"+g2);
		int redDifference = r2 - r1;
		int greenDifference = g2 - g1;
		int blueDifference = b2 - b1;
		return Math.sqrt(redDifference * redDifference + greenDifference * greenDifference + blueDifference * blueDifference);
	}

	private int[][] image2D(BufferedImage i) {
		this.detectWhite(i, false);
		int[][] pixels = new int[image.length][image[0].length];
		for (int r = 0;r<pixels.length;r++) {
	    	for (int c = 0;c<pixels[r].length;c++){
	    		Color color = new Color(image[r][c][0],image[r][c][1],image[r][c][2]);
				int rgb = color.getRGB();
	    		pixels[r][c]=rgb;
	    	}
		}
		return pixels;
	}

//	private int mode(ArrayList<Integer> color) {
//		int mode = color.get(0);
//	    int maxCount = 0;
//	    for (int i = 0; i < color.size(); i++) {
//	        int value = color.get(i);
//	        int count = 1;
//	        for (int j = 0; j < color.size(); j++) {
//	            if (closeenough(color.get(j),mode))
//	                count++;
//	            if (count > maxCount) {
//	                mode = value;
//	                maxCount = count;
//	            }
//	        }
//	    }
//	    return mode;
//	}

//	private boolean closeenough(Integer p,Integer cm) {
//		int cr=(p>>16) & 0xff;
//		int cg=(p>>8) & 0xff;
//		int cb= p & 0xff;
//		int mr=(p>>16) & 0xff;
//		int mg=(p>>8) & 0xff;
//		int mb= p & 0xff;
//		if(Math.abs(cr-mr)<=3&&Math.abs(cg-mg)<=3&&Math.abs(cb-mb)<=3) {
//			return true;
//		}
//		return false;
//	}
}