

package ip_project.main;

import java.util.ArrayList;

import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 */
public abstract class MIContainer extends HBox{
	
	private VBox mGContainer = new VBox();
	private HBox mIContainer = new HBox();
	private Pane mCanvas = new Pane();
	private ArrayList<Transition> mAnimation = new ArrayList<Transition>();
	private boolean mIsChanged = false;
	
	public MIContainer(){
		
		//create graph and input bar
		VBox bar = new VBox();
		bar.setPrefWidth(500);
		
		//create graph container
		mGContainer.setBorder(new Border(new BorderStroke(Color.BROWN,  BorderStrokeStyle.SOLID,  CornerRadii.EMPTY, new BorderWidths(2))));
		mGContainer.setAlignment(Pos.CENTER);
		VBox.setVgrow(mGContainer, Priority.ALWAYS);
		
		//create input container
		mIContainer.setPrefHeight(150);
		mIContainer.setAlignment(Pos.CENTER);
		mIContainer.setBorder(new Border(new BorderStroke(Color.GREEN,  BorderStrokeStyle.SOLID,  CornerRadii.EMPTY, new BorderWidths(2))));
		
		//add elements to bar
		bar.getChildren().addAll(mGContainer, mIContainer);
		
		//create animation pane
		mCanvas.setBorder(new Border(new BorderStroke(Color.RED,  BorderStrokeStyle.SOLID,  CornerRadii.EMPTY, new BorderWidths(1))));	
		HBox.setHgrow(mCanvas, Priority.ALWAYS);
		
		//add elements to the container
		this.getChildren().addAll(mCanvas, bar);
	}
	
	public void addInputs(Slider... inputs){
			mIContainer.getChildren().addAll(inputs);
	}
	
	public void addGraphs(Chart... graphs){
		mGContainer.getChildren().addAll(graphs);
	}
	
	public void addAnimationElements(Node... node){
		mCanvas.getChildren().addAll(node);
	}
	
	public void addAnimations(Transition... animations){
		for(Transition animation: animations){
			mAnimation.add(animation);
		}
	}
	
	public void help(){
		//TODO add dialog box (whatever predefined one there is in javafx) and output a help message (written in Res)
	}
	
	public void start(){
		for(Node input: mIContainer.getChildren()){
			input.setDisable(true);
		}
		for(Transition animation : mAnimation){
			animation.playFromStart();
		}
		
		//makes sure that change flag is set to false
		mIsChanged = false;
	}
	
	public void done(){
		for(Node input : mIContainer.getChildren()){
			input.setDisable(false);
			((Slider)input).setValue(((Slider)input).getMin());	//reset to default (min) value
			input.setOnMouseReleased(null);
		}
		for(Transition animation: mAnimation){
			animation.stop();
		}
		
		//TODO clear graphs of all trials
		
		//makes sure that change flag is set to false
		mIsChanged = false;
	}
	
	public void unpause(){	//Note: Could not use "Continue" because clash with reserved syntax
		for(Node input: mIContainer.getChildren()){
			input.setDisable(true);
			input.setOnMouseReleased(null);
		}
		
		if(mIsChanged){
			//TODO create new trial in graphs
			this.start();
		}else{
			for(Transition animation : mAnimation){
				animation.play();
			}
		}
	}
		
	public void pause(){
		for(Node input: mIContainer.getChildren()){
			input.setDisable(true);
		}
		
		for(Transition animation : mAnimation){
			animation.pause();
		}
		
		//makes sure that change flag is set to false
		mIsChanged = false;
	}
	
	public void reset(){ 
		for(Node input : mIContainer.getChildren()){
			input.setDisable(false);
			((Slider)input).setOnMouseReleased(new InputChangeListener(((Slider)input).getValue()));
		}
		for(Transition animation : mAnimation){
			animation.pause();
		}
	}
	
	/**
	 * Listener which watches for changes in input slider values
	 */
	private class InputChangeListener implements EventHandler<MouseEvent>{

		private double mOriginal;
		
		public InputChangeListener(double original){
			mOriginal = original;
		}
		
		@Override
		public void handle(MouseEvent event) {
			//if the slider value is different from the original value then set the changed flag to true
			if(((Slider)event.getSource()).getValue() != mOriginal){
				mIsChanged = true;
			}else{
				mIsChanged = false;
			}
		}
	}
}