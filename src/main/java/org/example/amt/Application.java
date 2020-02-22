package org.example.amt;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import javax.servlet.ServletException;

public class Application {

    private Undertow server;

    public static void main(String[] args) {
        try {
            new Application().start();
        } catch (ServletException e) {
            // simple exception handler for now
            throw new RuntimeException("Cannot start", e);
        }
    }

    public void start() throws ServletException {
        DeploymentInfo servletBuilder = Servlets.deployment() //
                .setClassLoader(getClass().getClassLoader()) //
                .setContextPath("/") //
                .setDeploymentName("bank.war")//
                .addServlets(//
                        Servlets.servlet("BankServlet", org.glassfish.jersey.servlet.ServletContainer.class)//
                                .addInitParam("javax.ws.rs.Application", "org.example.amt.rest.BankApplication")//
                                .addMapping("/*"));

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        PathHandler path = Handlers.path(manager.start());

        server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setIoThreads(2)
                .setWorkerThreads(10)
                .setHandler(path)
                .build();
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
