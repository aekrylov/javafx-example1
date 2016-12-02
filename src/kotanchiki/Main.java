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

    private AudioClip china = new AudioClip(getClass().getResource("/bigchina.mp3").toExternalForm());

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();

        Image open = new Image(getClass().getResource("/trump_open1.png").toString());
        Image closed = new Image(getClass().getResource("/trump_close1.png").toURI().toURL().toString());

        ImageView iv = new ImageView(closed);
        iv.setFitWidth(HERO_SIZE);
        iv.setFitHeight(HERO_SIZE);

        root.getChildren().add(iv);

        BulletTimer timer = new BulletTimer();
        timer.start();

        root.setOnKeyPressed(event -> {
                KeyCode code = event.getCode();
                if(code.equals(KeyCode.SPACE)) {
                    iv.setImage(open);

                    double y = iv.getY()+HERO_SIZE/2;
                    double x = iv.getX()+HERO_SIZE/2-Bullet.SIZE/2 + 70*iv.getScaleX();

                    Bullet bullet = new Bullet(x, y, iv.getScaleX() == 1, root);
                    timer.addBullet(bullet);

                    china.play();
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
                iv.setImage(closed);
            }
        });

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
        primaryStage.getScene().getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class BulletTimer extends AnimationTimer {

    private List<Bullet> bullets = new ArrayList<>();

    private long was = System.nanoTime();

    @Override
    public void handle(long now) {
        long delta = now - was;
        was = now;

        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();

            double x = bullet.getX() + (delta * Bullet.SPEED / 1000000) * (bullet.isRight() ? 1 : -1);
            bullet.setX(x);

            if (bullet.getX() < -Bullet.SIZE || bullet.getX() > Main.SCREEN_SIZE) {
                bullet.remove();
                it.remove();
            }
        }
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }
}

class Bullet {
    public static final double SIZE = 35;
    public static final double SPEED = .4;

    private boolean right;
    private ImageView view;
    private Pane pane;

    private static Image image = new Image(Bullet.class.getResource("/china.png").toExternalForm());

    public Bullet(double x, double y, boolean right, Pane pane) {
        this.right = right;

        view = new ImageView(image);
        view.setFitHeight(SIZE);
        view.setFitWidth(SIZE);
        view.setScaleX(right ? 1 : -1);

        view.setX(x);
        view.setY(y);

        this.pane = pane;
        pane.getChildren().add(view);
    }

    public void remove() {
        pane.getChildren().remove(view);
    }

    public boolean isRight() {
        return right;
    }

    public double getX() {
        return view.getX();
    }

    public void setX(double x) {
        view.setX(x);
    }

    public double getY() {
        return view.getY();
    }

    public void setY(double y) {
        view.setY(y);
    }
}
