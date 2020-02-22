package org.example.amt;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private final Logger log = LoggerFactory.getLogger(Application.class);

    private Undertow server;

    public static void main(String[] args) {
            new Application().start();
    }

    public void start()  {
        if (server == null) {
            DeploymentInfo servletBuilder = Servlets.deployment() //
                    .setClassLoader(getClass().getClassLoader()) //
                    .setContextPath("/") //
                    .setDeploymentName("bank.war")//
                    .addServlets(//
                            Servlets.servlet("BankServlet", org.glassfish.jersey.servlet.ServletContainer.class)//
                                    .addInitParam("javax.ws.rs.Application", "org.example.amt.rest.AccountResourceConfig")//
                                    .addMapping("/*"));

            DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
            manager.deploy();

            try {
                PathHandler path = Handlers.path(manager.start());

                server = Undertow.builder()
                        .addHttpListener(8080, "localhost")
                        .setIoThreads(2)
                        .setWorkerThreads(10)
                        .setHandler(path)
                        .build();
                log.info("Starting server.");
                server.start();
            } catch (Exception e) {
                log.error("Cannot start serer.", e);
            }
        } else {
            log.warn("Server is already running.");
        }
    }

    public void stop() {
        if (server == null) {
            log.warn("Server is not running.");
        } else {
            log.info("Stopping server.");
            server.stop();
        }
    }
}
