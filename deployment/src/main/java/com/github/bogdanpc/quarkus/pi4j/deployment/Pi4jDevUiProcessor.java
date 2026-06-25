package com.github.bogdanpc.quarkus.pi4j.deployment;


import com.github.bogdanpc.quarkus.pi4j.devui.Pi4jJsonRpcService;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

public class Pi4jDevUiProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    CardPageBuildItem pages() {
        var card = new CardPageBuildItem();

        card.addPage(Page.webComponentPageBuilder()
                .title("Platform")
                .componentLink("qwc-pi4j-platform.js")
                .icon("font-awesome-solid:microchip")
                .dynamicLabelJsonRPCMethodName("getActivePlatform"));

        card.addPage(Page.webComponentPageBuilder()
                .title("Platforms")
                .componentLink("qwc-pi4j-platforms.js")
                .icon("font-awesome-solid:layer-group")
                .dynamicLabelJsonRPCMethodName("getPlatformCount"));

        card.addPage(Page.webComponentPageBuilder()
                .title("Providers")
                .componentLink("qwc-pi4j-providers.js")
                .icon("font-awesome-solid:plug")
                .dynamicLabelJsonRPCMethodName("getProviderCount"));

        card.addPage(Page.externalPageBuilder("Pi4J Docs")
                .url("https://www.pi4j.com/")
                .icon("font-awesome-solid:book")
                .doNotEmbed());

        card.addLibraryVersion("com.pi4j", "pi4j-core", "Pi4J", "https://www.pi4j.com/documentation/");

        return card;
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    JsonRPCProvidersBuildItem rpcService() {
        return new JsonRPCProvidersBuildItem(Pi4jJsonRpcService.class);
    }

}
