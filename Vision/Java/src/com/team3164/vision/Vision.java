package com.team3164.vision;

import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.team3164.vision.GripPipeline; 
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import edu.wpi.first.wpilibj.networktables.NetworkTable;


public class Vision {

	private static GripPipeline visionPipe;
	private static NetworkTable networkTables;
	
	
	public static void main(String[] args) {
		 System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		visionPipe = new GripPipeline();
		
		networkTables.setClientMode();
		//networkTables.setIPAddress("jrue.local");
		networkTables.setIPAddress("roborio-3164-FRC.local");
		
		networkTables.initialize();
		
		NetworkTable table = networkTables.getTable("/grip");
		
		//VideoCapture camera = new VideoCapture(0);
		VideoCapture camera = new VideoCapture();
		camera.open("http://127.0.0.1:8080/?action=stream&type=file.mjpg");
		
		
		//camera.set(propId, value)
		
		
		while(true) {
			Boolean bk = False;
			Mat img = new Mat();
			if(camera.retrieve(img)) {
				visionPipe.process(img);
				
				/*double[] width = new double[visionPipe.filterContoursOutput().size()];
				double[] height = new double[visionPipe.filterContoursOutput().size()];
				double[] area = new double[visionPipe.filterContoursOutput().size()];
				double[] posx = new double[visionPipe.filterContoursOutput().size()];
				double[] posy = new double[visionPipe.filterContoursOutput().size()];
				double[] posxC = new double[visionPipe.filterContoursOutput().size()];
				double[] posyC = new double[visionPipe.filterContoursOutput().size()];*/
				
				for (int i = 0; i < visionPipe.filterContoursOutput().size(); i++) {
					Rect rect = Imgproc.boundingRect(visionPipe.filterContoursOutput().get(i));
					
					/*width[i] = rect.width;
					height[i] = rect.height;
					area[i] = rect.area();
					posx[i] = rect.x;
					posy[i] = rect.y;
					posxC[i] = rect.x + 0.5*(rect.width);
					posyC[i] = rect.y + 0.5*(rect.height);*/
					float x = rect.x;
					float y = rect.y;
					float h = rect.height;
					float w = rect.width;
					for (int k = 0; k < visionPipe.filterContoursOutput().size(); k++) {
						if(k != i) {
						Rect rect2 = Imgproc.boundingRect(visionPipe.filterContoursOutput().get(k));
						float x2 = rect2.x;
						float y2 = rect2.y;
						float h2 = rect2.height;
						float w2 = rect2.width;

						float widthRatioScore = 100 - (100 * abs(1 - (w/w2)));
						float heightRatioScore = 100 - (100 * abs(1 - (h/h2)));
				
						//Get Difference in X/Y, its a percent difference on a 100 scale
						float yDiff = abs(y2 - y) / (y2 + y) * 200;
						float xDiff = abs(x2 - x) / (x2 + x) * 200;
						//If the ratios of the widths and the heights abocve 60%, and 
						//Difference in y value is under twenty percent
						//Not sure about xDiff
						if (widthRatioScore > 60 && heightRatioScore > 60 && yDiff < 35 && xDiff < 100) {
							bk = True;
							break;
						}
					}}
					if (bk) break;
					
				}
				/*table.putString("test", "hello");
				//if (visionPipe.filterContoursOutput().size() > 0) {
					table.putNumberArray("width", width);
					table.putNumberArray("height", height);
					table.putNumberArray("area", area);
					table.putNumberArray("x", posx);
					table.putNumberArray("y", posy);
					table.putNumberArray("centerX", posxC);
					table.putNumberArray("centerY", posyC);*/
				if(bk) {
					table.putBoolean("boxVisible", True);
					if (x > x2) {
						float boxLeftX = x2;
						float boxLeftY = y2;
						float boxLeftW = w2;
						float boxLeftH = h2;
						float boxRightX = x;
						float boxRightY = y;
						float boxRightW = w;
						float boxRightH = h;
					} else {
						float boxLeftX = x;
						float boxLeftY = y;
						float boxLeftW = w;
						float boxLeftH = h;
						float boxRightX = x2;
						float boxRightY = y2;
						float boxRightW = w2;
						float boxRightH = h2;	
					}
					table.putNumber("distLeft", boxLeftX);
					table.putNumber("distRight", (640 - boxRightX - boxRightW));
					bk = False;
				} else {
					table.putBoolean("boxVisible", False);
				}
				//}
			}
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
