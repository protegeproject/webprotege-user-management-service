package edu.stanford.protege.webprotegeusermanagement;

import edu.stanford.protege.webprotege.ipc.WebProtegeIpcApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(WebProtegeIpcApplication.class)
public class WebprotegeUserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebprotegeUserManagementApplication.class, args);
	}

}
