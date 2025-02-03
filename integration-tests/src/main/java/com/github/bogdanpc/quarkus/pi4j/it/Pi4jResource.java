package com.github.bogdanpc.quarkus.pi4j.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import com.pi4j.context.Context;

@Path("/pi4j")
@ApplicationScoped
public class Pi4jResource {
    @Inject
    Context context;

    @GET
    public String hello() {
        return "Hello " + context.getPlatform().description();
    }
}
