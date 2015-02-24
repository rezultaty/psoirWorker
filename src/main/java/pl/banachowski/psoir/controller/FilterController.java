package pl.banachowski.psoir.controller;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Controller;

@Controller
public class FilterController {
	public BufferedImage scale(BufferedImage buffImg) {
		return Scalr.resize(buffImg, Scalr.Method.SPEED, 100);
	}

}
