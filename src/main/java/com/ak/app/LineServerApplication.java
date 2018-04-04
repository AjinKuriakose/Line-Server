package com.ak.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ak.app.resources.LineServer;

@SpringBootApplication
public class LineServerApplication {

	public static void main(String[] args) {

		/*
		 * Setting the system property to pass the input file name argument to the
		 * spring component. A better approach would have been to use the spring
		 * context.
		 */
		if (args == null || args.length == 0) {
			System.out.println("Specify the input file path.Exiting...");
			return;
		}
		System.setProperty(LineServer.FILE_NAME, args[0]);
		SpringApplication.run(LineServerApplication.class, args);
	}
}
