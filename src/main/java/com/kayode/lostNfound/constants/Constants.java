/**
 *
 */
package com.kayode.lostNfound.constants;

import java.util.ResourceBundle;

/**
 *
 */
public class Constants {
	static ResourceBundle rs = ResourceBundle.getBundle("resources.config");

	public static String APP_BASE_NAME;

	public static String UPLOAD_PATH_DIR;

	static {
		APP_BASE_NAME = rs.getString("app.base.name");
		//
		UPLOAD_PATH_DIR = rs.getString("media.upload.dir.path.mac");
	}

}
