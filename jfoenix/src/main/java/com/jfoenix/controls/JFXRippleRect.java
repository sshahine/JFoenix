/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public  class JFXRippleRect extends Rectangle {
    /*
     * @author AyushChadha
     * @version 1.0
     * @since 2019-03-21
    */
    private RippleCircle Circ;
    protected boolean rippled=false;
    /*
    @params 
    Paint p for the fill of ripple
    int  duration for the duration of ripple
    */
    public void Ripple(Paint p, int duration){  
        this.setFill(p);
  

            if(rippled==false){
             rippled = true;
            double r = this.getWidth()/2;
            this.Circ = new RippleCircle( r,p,duration);
      
            this.setClip(this.Circ);
            
           this.Circ.ripple();
         
            }else{
               this.Circ.setDuration(duration);
               this.Circ.ripple();
                }
    }



/*
     A handy method to put the  ripplerect to be put in a parent pane with its same dimensions,
     can be used while developing ui in scenebuilder , by putting a pane with dimensions of ripple rect and 
     using this method to put a ripplerect in your ui of the same dimensions that of the parent pane in the controller of
     FXML document.
     
     @params Pane Pane the argument of parent pane
     */

   public  void PutRect(Pane pane){
      

        this.setHeight(pane.getPrefHeight());
        this.setWidth(pane.getPrefWidth());
        pane.getChildren().add(this);


   }
  
  

// <editor-fold defaultstate="collapsed" desc="RippleCircle">
private class RippleCircle extends Circle {

        int dur;
        ScaleTransition scaleTransition;

        RippleCircle(double radius, Paint paint, int d) {
            this.setFill(paint);
            this.setRadius(radius);
            this.dur = d;

            scaleTransition = new ScaleTransition();
            scaleTransition.setDuration(Duration.millis(dur));
            scaleTransition.setNode(RippleCircle.this);
            scaleTransition.setByY(JFXRippleRect.this.getHeight() / 20);
            scaleTransition.setByX(JFXRippleRect.this.getWidth() / 20);
            scaleTransition.setCycleCount(1);
            scaleTransition.setAutoReverse(false);
        }

        void setDuration(int dur) {
            this.dur = dur;
        }

        void ripple() {
            scaleTransition.play();
        }
    }
// </editor-fold> 

}
