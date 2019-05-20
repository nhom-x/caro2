
package caro;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


public class Cell extends Rectangle {
   public Cell(double width, double height){
        super(width, height, Color.BLUE);
    }
   public Cell(double width, double height, Paint paint){
        super(width, height, paint);
    }
}
