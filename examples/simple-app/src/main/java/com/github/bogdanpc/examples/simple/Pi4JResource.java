package com.github.bogdanpc.examples.simple;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/api/pi4j")
public class Pi4JResource {
    private final Pi4JService pi4JService;

    public Pi4JResource(Pi4JService pi4JService) {
        this.pi4JService = pi4JService;
    }

    @PUT
    @Path("/led/{state}")
    public Boolean modifyLedStatus(@PathParam("state") boolean state) {
        return pi4JService.ledToggle(state);
    }
}
