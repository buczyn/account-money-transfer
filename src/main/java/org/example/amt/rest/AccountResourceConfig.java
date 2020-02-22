package org.example.amt.rest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.amt.service.AccountModule;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

class AccountResourceConfig extends ResourceConfig {
    AccountResourceConfig() {
        packages(AccountResourceConfig.class.getPackageName())
                .register(GuiceFeature.class);
    }

    /**
     * A JAX-RS feature that servers as Guice-to-HK2 bridge to allow Jersey (that uses HK2 for DI) see Guice configured
     * dependencies.
     * <p>
     * See details in http://javaee.github.io/hk2/guice-bridge.html#Injecting_Guice_services_into_HK2_services.
     */
    private static class GuiceFeature implements Feature {

        @Override
        public boolean configure(FeatureContext context) {
            ServiceLocator serviceLocator = InjectionManagerProvider.getInjectionManager(context)
                    .getInstance(ServiceLocator.class);
            // step 1: initialize HK2 ServiceLocator with Guice/HK2 Bridge services
            GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);

            // step 2: provide HK2 ServiceLocator all needed Guice injectors to search for dependencies
            Injector injector = Guice.createInjector(new AccountModule());
            GuiceIntoHK2Bridge bridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
            bridge.bridgeGuiceInjector(injector);
            return true;
        }
    }

}
