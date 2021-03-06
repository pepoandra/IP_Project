package ip_project.experiments.mechanics;

import java.io.File;

import ip_project.main.MIContainer;
import ip_project.main.Resources;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Projectile Motion Experiment
 */
public class ProjContainer extends MIContainer implements Resources {

	protected XYChart.Series<Number, Number> series1, series2;
	protected LineChart<Number, Number> mGraph1, mGraph2;
	protected Slider mSlider1, mSlider2;
	private TranslateTransition anim1, anim2;
	private ImageView image;

	public ProjContainer() {

		// example of slider set up
		mSlider1 = new Slider(PROJ_SLIDER_MIN_1, PROJ_SLIDER_MAX_1,
				PROJ_SLIDER_DEFAULT_1);
		mSlider1.setShowTickMarks(true);
		mSlider1.setShowTickLabels(true);
		mSlider1.setMajorTickUnit(TICK_UNIT_2);
		mSlider1.setMinorTickCount((int) ZERO_DEFAULT);
		mSlider1.setSnapToTicks(true);
		mSlider1.setId(PROJ_SLIDER_1_ID);
		this.addInputs(mSlider1);

		mSlider2 = new Slider(PROJ_SLIDER_MIN_2, PROJ_SLIDER_MAX_2,
				PROJ_SLIDER_DEFAULT_2);
		mSlider2.setShowTickMarks(true);
		mSlider2.setShowTickLabels(true);
		mSlider2.setMajorTickUnit(TICK_UNIT_2);
		mSlider2.setMinorTickCount((int) ZERO_DEFAULT);
		mSlider2.setSnapToTicks(true);
		mSlider2.setId(PROJ_SLIDER_2_ID);

		this.addInputs(mSlider2);


		// example of graph set up
		NumberAxis xAxis1 = new NumberAxis(PC_X_AXIS, PROJ_AXIS_X_MIN_1,
				PROJ_AXIS_X_MAX_1, PROJ_SPACING_AXIS_X);
		NumberAxis yAxis1 = new NumberAxis(PC_Y_AXIS_1, PROJ_AXIS_Y_MIN_1,
				PROJ_AXIS_Y_MAX_1, PROJ_SPACING_AXIS_Y);

		NumberAxis xAxis2 = new NumberAxis(PC_X_AXIS, PROJ_AXIS_X_MIN_2,
				PROJ_AXIS_X_MAX_2, PROJ_SPACING_AXIS_X_2);
		NumberAxis yAxis2 = new NumberAxis(PC_Y_AXIS_2, PROJ_AXIS_Y_MIN_2,
				PROJ_AXIS_Y_MAX_2, PROJ_SPACING_AXIS_Y_2);

		yAxis1.setAutoRanging(false);
		xAxis1.setAutoRanging(false);
		yAxis2.setAutoRanging(false);
		xAxis2.setAutoRanging(false);

		mGraph1 = new LineChart<Number, Number>(xAxis1, yAxis1);
		mGraph2 = new LineChart<Number, Number>(xAxis2, yAxis2);

		this.addGraphs(mGraph1);
		this.addGraphs(mGraph2);

		Image image1 = new Image(OVNI_PATH);
		image = new ImageView();
		image.setImage(image1);
		image.setFitHeight(this.mCanvas.getHeight() / PC_CONSTANT_1);
		image.setFitWidth(this.mCanvas.getWidth() / PC_CONSTANT_2);
		image.setVisible(false);

		image.setTranslateX(PROJ_X_POSITION_1);
		image.setTranslateY(PROJ_Y_POSITION_1);

		anim1 = new TranslateTransition(Duration.seconds(10), image);
		anim1.setInterpolator(new ProjXInterpolator());
		anim1.setFromX(PROJ_TRANSLATE_FROM_X_1);
		anim1.setCycleCount((int) ONE_DEFAULT);
		anim1.setToX(PROJ_TRANSLATE_TO_X_1);

		anim2 = new TranslateTransition(Duration.seconds(10), image);
		anim2.setInterpolator(new ProjYInterpolator()); // this is where you put
														// in the custom
														// interpolator
		anim2.setFromY(this.mCanvas.getHeight() / HALF_FACTOR); // useless line, values is
														// updated before
														// playing animation
		anim2.setCycleCount((int) ONE_DEFAULT);
		anim2.setToY(PROJ_TRANSLATE_TO_Y_1);

		// set up the animation
		ParallelTransition comboAnim = new ParallelTransition();
		comboAnim.getChildren().addAll(anim1, anim2);

		this.addAnimations(comboAnim);
		this.addAnimationElements(image);

		this.getStyleClass().add("projectile-motion-canvas");

	}

	private class ProjYInterpolator extends Interpolator {

		@Override
		protected double curve(double t) {

			double value = calculateYPosition(mSlider1.getValue(),
					mSlider2.getValue(), t);

			XYChart.Series<Number, Number> series2 = mGraph2.getData().get(
					mGraph2.getData().size() - (int) ONE_DEFAULT);
			
			series2.getData().add(new Data<Number, Number>(t, value));

			return value;
		}
	}

	private class ProjXInterpolator extends Interpolator {

		@Override
		protected double curve(double t) {

			double value = calculateXPosition(mSlider1.getValue(),
					mSlider2.getValue(), t);

			// get top level series
			XYChart.Series<Number, Number> series1 = mGraph1.getData().get(
					mGraph1.getData().size() - (int) ONE_DEFAULT);

			series1.getData().add(new Data<Number, Number>(t, value));


			return t;
		}
	}

	private double calculateXPosition(double velocity, double angle, double time) {
		return ((velocity) * Math.cos(Math.toRadians(angle)) * time);
	}

	private double calculateYPosition(double velocity, double angle, double time) {

		return ((time * velocity * Math.sin(Math.toRadians(angle))) - (ONE_DEFAULT/HALF_FACTOR * Math
				.pow(time, HALF_FACTOR) * GRAVITY_CONSTANT)) / PC_CONSTANT_3;
	}

	private double maxHeight() {
		double time = ((mSlider1.getValue() * Math.sin(Math.toRadians(mSlider2
				.getValue()))) / GRAVITY_CONSTANT);

		return (calculateYPosition(mSlider1.getValue(), mSlider2.getValue(),
				time));
	}

	public void updateValues() {
		anim1.setToX((this.mCanvas.getWidth() / PC_CONSTANT_4)
				* calculateXPosition(mSlider1.getValue(), mSlider2.getValue(),
						ONE_DEFAULT));

		anim2.setFromY(this.mCanvas.getHeight() / HALF_FACTOR);
		anim2.setToY((this.mCanvas.getHeight() / HALF_FACTOR)
				- (this.mCanvas.getHeight() / (HALF_FACTOR)) * maxHeight());
		
		
		// CANNOT PUT PATH IN INTERFACE  BECAUSE OF CONVERSION

		String ovniS = new File("src/ip_project/sounds/ovni.mp3").toURI()
				.toString();

		Media pick = new Media(ovniS);
		MediaPlayer player = new MediaPlayer(pick);

		player.play();

		anim2.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				player.stop();
				image.setVisible(false);

			}
		});

		image.setFitHeight(this.mCanvas.getHeight() / PC_CONSTANT_2);
		image.setFitWidth(this.mCanvas.getWidth() / PC_CONSTANT_2);
		image.setVisible(true);

	}

	public String getHelp() {
		return PROJ_HELP;
	}

	public String getTitle() {
		return PROJ_TITLE;
	}
}
