package kotanchiki;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 12/1/16 12:13 PM
 */
public class Main extends Application {

    public static final double HERO_SIZE = 100;
    public static final double SCREEN_SIZE = 1920;

    private AudioClip chinaClip = new AudioClip(getClass().getResource("/bigchina.mp3").toExternalForm());
    private Image trumpOpen = new Image(getClass().getResource("/trump_open1.png").toString());
    private Image trumpClosed = new Image(getClass().getResource("/trump_close1.png").toString());

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();

        ImageView iv = new ImageView(trumpClosed);
        iv.setFitWidth(HERO_SIZE);
        iv.setFitHeight(HERO_SIZE);

        root.getChildren().add(iv);

        BulletTimer timer = new BulletTimer(root);
        timer.start();

        root.setOnKeyPressed(event -> {
                KeyCode code = event.getCode();
                if(code.equals(KeyCode.SPACE)) {
                    iv.setImage(trumpOpen);

                    double y = iv.getY()+HERO_SIZE/2;
                    double x = iv.getX()+HERO_SIZE/2- BulletView.SIZE/2 + 70*iv.getScaleX();

                    BulletView bullet = new BulletView(x, y, iv.getScaleX() == 1);
                    root.getChildren().add(bullet);
                    timer.addBullet(bullet);

                    chinaClip.play();
                } else if(code.equals(KeyCode.UP)) {
                    iv.setY(iv.getY()-10);
                } else if(code.equals(KeyCode.DOWN)) {
                    iv.setY(iv.getY()+10);
                } else if(code.equals(KeyCode.LEFT)) {
                    iv.setX(iv.getX()-10);
                    iv.setScaleX(-1);
                } else if(code.equals(KeyCode.RIGHT)) {
                    iv.setX(iv.getX()+10);
                    iv.setScaleX(1);
                }
            });

        root.setOnKeyReleased(event -> {
            if(event.getCode().equals(KeyCode.SPACE)) {
                iv.setImage(trumpClosed);
            }
        });

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
        root.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class BulletTimer extends AnimationTimer {

    private List<BulletView> bullets = new ArrayList<>();
    private Pane pane;

    private long was = System.nanoTime();

    public BulletTimer(Pane pane) {
        this.pane = pane;
    }

    @Override
    public void handle(long now) {
        long delta = now - was;
        was = now;

        Iterator<BulletView> it = bullets.iterator();
        while (it.hasNext()) {
            BulletView bullet = it.next();

            double x = bullet.getX() + (delta * BulletView.SPEED / 1000000) * (bullet.isRight() ? 1 : -1);
            bullet.setX(x);

            if (bullet.getX() < -BulletView.SIZE || bullet.getX() > Main.SCREEN_SIZE) {
                pane.getChildren().remove(bullet);
                it.remove();
            }
        }
    }

    public void addBullet(BulletView bullet) {
        bullets.add(bullet);
    }
}

class BulletView extends ImageView {
    public static final double SIZE = 35;
    public static final double SPEED = .4;

    private boolean right;

    private static Image image = new Image(BulletView.class.getResource("/china.png").toExternalForm());

    public BulletView(double x, double y, boolean right) {
        super(image);
        this.right = right;

        setFitHeight(SIZE);
        setFitWidth(SIZE);
        setScaleX(right ? 1 : -1);

        setX(x);
        setY(y);
    }

    public boolean isRight() {
        return right;
    }
}
