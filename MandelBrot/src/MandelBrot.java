import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;

import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import javafx.beans.binding.Bindings;
 
public class MandelBrot extends Application {
  
	private int steps = 50;
	private int size = 700;
	private static int no = 1;
	private Color color = Color.WHITE;
	private Field[] colors;
	
    @Override
    public void start(Stage primaryStage) {
        VBox pain = new VBox();
        Canvas kannWas = new Canvas(size, size); 
        
        HBox toolbar = new HBox();
        GridPane area = new GridPane();
        
        Label realLbl = new Label("Realteil: ");
        Label imagLbl = new Label("Imaginärteil: ");
        Slider realNum = new Slider(-2,2,0);
        Slider imagNum = new Slider(-2,2,0);
        
        Label realArea = new Label("Realteil von/bis: ");
        Label imagArea = new Label("Imaginärteil von/bis: ");
        
        Slider realMin = new Slider(-2,2,-2);
        Slider realMax = new Slider(-2,2, 2);
        Slider imagMin = new Slider(-2,2,-2);
        Slider imagMax = new Slider(-2,2,2);
        
        TextField realMinTxt = new TextField(realMin.getValue()+"");
        TextField realMaxTxt = new TextField(realMax.getValue()+"");
        TextField imagMinTxt = new TextField(imagMin.getValue()+"");
        TextField imagMaxTxt = new TextField(imagMax.getValue()+"");
        
        StringConverter<Number> converter = new NumberStringConverter();
        Bindings.bindBidirectional(realMinTxt.textProperty(), realMin.valueProperty(), converter);
        Bindings.bindBidirectional(realMaxTxt.textProperty(), realMax.valueProperty(), converter);
        Bindings.bindBidirectional(imagMinTxt.textProperty(), imagMin.valueProperty(), converter);
        Bindings.bindBidirectional(imagMaxTxt.textProperty(), imagMax.valueProperty(), converter);
        
        realMin.setShowTickMarks(true);
        realMax.setShowTickMarks(true);
        imagMin.setShowTickMarks(true);
        imagMax.setShowTickMarks(true);
        
        realMin.setShowTickLabels(true);
        realMax.setShowTickLabels(true);
        imagMin.setShowTickLabels(true);
        imagMax.setShowTickLabels(true);
        
        
        realNum.setShowTickMarks(true);
        realNum.setShowTickLabels(true);
        imagNum.setShowTickMarks(true);
        imagNum.setShowTickLabels(true);
        
        realNum.setDisable(true);
        imagNum.setDisable(true);
        
        Label selectionLbl = new Label("Menge: ");
        ComboBox<String> selection = new ComboBox<String>();
        selection.getItems().addAll("Mandelbrot","Julia");
        selection.setValue("Mandelbrot");
        selection.setOnAction(e -> {
        	if(selection.getValue().equals("Mandelbrot")) {
        		realNum.setDisable(true);
        		imagNum.setDisable(true);
        	}else {
        		realNum.setDisable(false);
        		imagNum.setDisable(false);
        	}
        });
        
        Label stepsLbl = new Label("Steps: ");
        TextField steps = new TextField();
        steps.setText(this.steps+"");
        
        ComboBox<String> colorSelection= new ComboBox<>();
        colors = Color.class.getFields();
        ArrayList<String> paints = new ArrayList<String>();
        
        for(int i=0; i< colors.length; i++) {
        	paints.add(colors[i].getName());
        }
        
        colorSelection.getItems().addAll(paints);
        colorSelection.setOnAction(e -> {
        	this.color = Color.valueOf(colorSelection.getValue());
        });
        colorSelection.setValue("WHITE");
        
        	Button draw = new Button("Zeichnen");
        	draw.setOnAction(e -> {
        		if(selection.getValue().equals("Mandelbrot")) {
	        		int stepsToCheck = Integer.parseInt(steps.getText());
	        		this.steps = stepsToCheck;
	        		paintMandelSet(kannWas.getGraphicsContext2D(),realMin.getValue(),realMax.getValue(),imagMin.getValue(),imagMax.getValue()); //Mandelbrot is only defined in the rect -2,2,-2,2 
        		}else {
        			int stepsToCheck = Integer.parseInt(steps.getText());
	        		this.steps = stepsToCheck;
	        		paintJuliaSet(kannWas.getGraphicsContext2D(),realMin.getValue(),realMax.getValue(),imagMin.getValue(),imagMax.getValue(),realNum.getValue(),imagNum.getValue()); //Mandelbrot is only defined in the rect -2,2,-2,2 
        		}
        	});
        
        Button speichern = new Button("Speichern");
        speichern.setOnAction(e -> {
        	try {
	        	WritableImage writableImage = new WritableImage((int) kannWas.getWidth(), (int) kannWas.getHeight());
	        	WritableImage snapshot = kannWas.snapshot(new SnapshotParameters(), writableImage);
	        	File outFile = new File("./output"+no+++".jpg");
	        	//ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null),"JPEG",outFile); //JPEG format uses wrong color! 
	  
	        	//Workaround mit AWT: 
	        	BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null); 
	        	BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.OPAQUE); 
	        	Graphics2D graphics = imageRGB.createGraphics();
	        	graphics.drawImage(image, 0, 0, null);
	        	ImageIO.write(imageRGB, "JPEG", outFile);
	        	graphics.dispose();
	        	
        	}catch(IOException ohNo) {
        		
        	}
        });
        	
        area.add(realLbl, 0, 0);
        area.add(realNum, 1, 0);
        area.add(imagLbl, 2, 0);
        area.add(imagNum, 3, 0);
        
        area.add(realArea, 0, 1);
        area.add(realMin, 1, 1);
        area.add(realMinTxt, 2, 1);
        area.add(realMax, 3, 1);
        area.add(realMaxTxt, 4, 1);
        
        area.add(imagArea, 0, 2);
        area.add(imagMin, 1, 2);
        area.add(imagMinTxt, 2, 2);
        area.add(imagMax, 3, 2);
        area.add(imagMaxTxt, 4, 2);
        
        toolbar.getChildren().addAll(selectionLbl, selection,colorSelection,draw, speichern, stepsLbl, steps);
        pain.getChildren().addAll(area,toolbar, kannWas);
        Scene scene = new Scene(pain, size, size+150);
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 
    private void paintMandelSet(GraphicsContext context, double realMin, double realMax, double imagMin, double imagMax) {
        double precision = Math.max((realMax - realMin) / size, (imagMax - imagMin) / size);
  
        for (double c = realMin, xR = 0; xR < size; c = c + precision, xR++) {
            for (double ci = imagMin, yR = 0; yR < size; ci = ci + precision, yR++) {
                double convergenceValue = checkInMandelSet(ci, c, steps); //
                double t1 = ((double)convergenceValue / steps);
                double col1 = Math.max(255 * (t1*2 - 1), 0);
                double col2 = Math.min(255* t1, 255);
                double col3 = Math.min(255*2* t1, 255);
      
                if (convergenceValue != size) {
                	if(this.color.getRed() > this.color.getGreen() && this.color.getRed() > this.color.getBlue()) {
                		context.setFill(Color.color(this.color.getRed()*(col3/255), this.color.getGreen()*(col2/255), this.color.getBlue()*(col1/255)));
                	}else if(this.color.getGreen() > this.color.getRed() && this.color.getGreen() > this.color.getBlue()) {
                		context.setFill(Color.color(this.color.getRed()*(col1/255), this.color.getGreen()*(col3/255), this.color.getBlue()*(col2/255)));
                	}else {
                		context.setFill(Color.color(this.color.getRed()*(col1/255), this.color.getGreen()*(col2/255), this.color.getBlue()*(col3/255)));
                	}
                } else {
                    context.setFill(this.color); 
                }
                context.fillRect(xR, yR, 1, 1); //set pixel color
            }
        }
    }
    
    private void paintJuliaSet(GraphicsContext context, double realMin, double realMax, double imagMin, double imagMax, double realc, double imagc) {
        double precision = Math.max((realMax - realMin) / size, (imagMax - imagMin) / size);
  
        for (double c = realMin, xR = 0; xR < size; c = c + precision, xR++) {
            for (double ci = imagMin, yR = 0; yR < size; ci = ci + precision, yR++) {
                double convergenceValue = checkInJuliaSet(ci, c, steps, realc, imagc); //
                double t1 = ((double)convergenceValue / steps);
                double col1 = Math.max(255 * (2 * t1 - 1), 0);
                double col2 = Math.min(255*t1, 255);
                double col3 = Math.min(255*2* t1, 255);
 
                if (convergenceValue != size) {
                	if(this.color.getRed() > this.color.getGreen() && this.color.getRed() > this.color.getBlue()) {
                		context.setFill(Color.color(this.color.getRed()*(col3/255), this.color.getGreen()*(col2/255), this.color.getBlue()*(col1/255)));
                	}else if(this.color.getGreen() > this.color.getRed() && this.color.getGreen() > this.color.getBlue()) {
                		context.setFill(Color.color(this.color.getRed()*(col1/255), this.color.getGreen()*(col3/255), this.color.getBlue()*(col2/255)));
                	}else {
                		context.setFill(Color.color(this.color.getRed()*(col1/255), this.color.getGreen()*(col2/255), this.color.getBlue()*(col3/255)));
                	}
                } else {
                    context.setFill(this.color); 
                }
                context.fillRect(xR, yR, 1, 1); //set pixel color
            }
        }
    }
 
    private int checkInMandelSet(double ci, double c, int steps) {
        double z = 0;
        double zi = 0;
        
        for (int i = 0; i < steps; i++) {
            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);
            z = zT + c;
            zi = ziT + ci;
 
            if (z * z + zi * zi >= 4.0) {
                return i;
            }
        }
        return steps;
    }
    
    private int checkInJuliaSet(double ci, double c, int steps, double realc, double imagc) {
        double z = c;
        double zi = ci;
        
        for (int i = 0; i < steps; i++) {
            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);
            z = zT + realc;
            zi = ziT + imagc;
 
            if (z * z + zi * zi >= 4.0) {
                return i;
            }
        }
        return steps;
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
