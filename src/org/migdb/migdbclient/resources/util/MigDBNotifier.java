/**
 * 
 */
package org.migdb.migdbclient.resources.util;

import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 * @author KANI
 *
 */
public class MigDBNotifier {

	private String title;
	private String message;
	private String rectangleFillColor;
	private String imagePath;
	private AnimationType animationType;
	private NotificationType notificationType;
	private int showTime;

	/**
	 * Constructor for create to custom notification
	 * 
	 * @param title
	 * @param message
	 * @param rectangleFill
	 * @param image
	 * @param showTime
	 */
	public MigDBNotifier(String title, String message, String rectangleFillColor, String imagePath,
			AnimationType animationType, int showTime) {
		super();
		this.title = title;
		this.message = message;
		this.rectangleFillColor = rectangleFillColor;
		this.imagePath = imagePath;
		this.animationType = animationType;
		this.showTime = showTime;
	}

	/**
	 * Constructor for create to defined notification
	 * 
	 * @param title
	 * @param message
	 * @param animationType
	 * @param notificationType
	 * @param showTime
	 */
	public MigDBNotifier(String title, String message, AnimationType animationType, NotificationType notificationType,
			int showTime) {
		super();
		this.title = title;
		this.message = message;
		this.animationType = animationType;
		this.notificationType = notificationType;
		this.showTime = showTime;
	}

	/**
	 * Defined notification create method
	 */
	public void createDefinedNotification() {
		TrayNotification tray = new TrayNotification();
		tray.setTitle(title);
		tray.setMessage(message);
		tray.setNotificationType(notificationType);
		tray.setAnimationType(animationType);
		tray.showAndDismiss(Duration.seconds(showTime));
	}

	/**
	 * Custom notification create method
	 */
	public void createCustomNotification() {
		Image trayIcon = new Image(imagePath);
		TrayNotification tray = new TrayNotification();
		tray.setTitle(title);
		tray.setMessage(message);
		tray.setRectangleFill(Paint.valueOf(rectangleFillColor));
		tray.setImage(trayIcon);
		tray.setNotificationType(NotificationType.CUSTOM);
		tray.setAnimationType(animationType);
		tray.showAndDismiss(Duration.seconds(showTime));
	}

}
