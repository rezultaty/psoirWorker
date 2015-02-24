package pl.banachowski.psoir.controller;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javafx.scene.transform.Rotate;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Controller;

@Controller
public class FilterController {
	public BufferedImage scale(BufferedImage buffImg) {
		return Scalr.resize(buffImg, Scalr.Method.SPEED, 100);
	}

    public BufferedImage rotate (BufferedImage bufferedImage) {

        AffineTransform transform = new AffineTransform();
        transform.rotate(3.14, bufferedImage.getWidth()/2, bufferedImage.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        bufferedImage = op.filter(bufferedImage, null);
        return bufferedImage;
    }

}
