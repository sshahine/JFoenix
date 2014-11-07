package customui.components;

import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class DepthManager {

	private static DropShadow[] depth = new DropShadow[]{null,
	new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.26), 10, 0.12, -1, 2),
	new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.26), 15, 0.16, 0, 4),
	new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.26), 20, 0.19, 0, 6),
	new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.26), 25, 0.25, 0, 8),
	new DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.26), 30, 0.30, 0, 10)};
	
	public static void setDepth(Node control, int level){
		control.setEffect(depth[level]);
	}
	
	public static int getLevels(){
		return depth.length;
	}
	
}
