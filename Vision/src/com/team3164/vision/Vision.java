package com.team3164.vision;

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
		networkTables.setIPAddress("jrue.local");//Change to roborio-3164-FRC.local
		
		networkTables.initialize();
		
		NetworkTable table = networkTables.getTable("/grip");
		
		VideoCapture camera = new VideoCapture(0);
		
		
		
		//camera.set(propId, value)
		
		
		while(true) {
			Mat img =new Mat();
			if(camera.read(img)) {
				visionPipe.process(img);
				
				/*double[] width = null;
				double[] height = null;
				double[] area = null;
				double[] posx = null;
				double[] posy = null;
				
				for (int i = 0; i < visionPipe.filterContoursOutput().size(); i++) {
					
					width[i] = visionPipe.filterContoursOutput().get(i).width();
					height[i] = visionPipe.filterContoursOutput().get(i).height();
					area[i] = Imgproc.contourArea(visionPipe.filterContoursOutput().get(i));
					posx[i] = visionPipe.filterContoursOutput().get(i).rows();
					posy[i] = visionPipe.filterContoursOutput().get(i).cols();
					
				}*/
				table.putString("test", "hello");
				/*table.putNumberArray("width", width);
				table.putNumberArray("height", height);
				table.putNumberArray("area", area);
				table.putNumberArray("posx", posx);
				table.putNumberArray("posy", posy);*/
			}
		}

	}

}
