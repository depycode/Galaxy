package org.m2sec.modules.fuzz.menu;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.InvocationType;
import org.m2sec.GalaxyMain;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.Log;
import org.m2sec.common.utils.AsyncExecuteUtil;
import org.m2sec.common.utils.HttpUtil;
import org.m2sec.modules.fuzz.intruder.FuzzSensitivePathGeneratorProviderProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class FuzzSensitivePathMenuItem extends AbstractMenuItem {

    public static final String[] STATIC_FILE_SUFFIX_ARRAY = new String[]{".js", ".css", ".html", ".jpg", ".png",
        ".pdf", ".docx"};

    private static final Log log = new Log(FuzzSensitivePathMenuItem.class);

    @Override
    public String displayName() {
        return "FuzzSensitivePath";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.isFrom(InvocationType.SITE_MAP_TABLE);
    }

    @Override
    public void action(ContextMenuEvent event) {
        List<HttpRequestResponse> requestResponses = event.selectedRequestResponses();
        if (!requestResponses.isEmpty()) {
            String domainUrl = HttpUtil.getDomainUrl(requestResponses.get(0).httpService());
            Set<String> evilPathSet = getEvilPathSet(requestResponses);
            log.infoEvent("%s get %d url. Please wait for execution.", displayName(), evilPathSet.size());
            List<Runnable> workRunnables = evilPathSet.stream().map(path -> (Runnable) () -> {
                String url = domainUrl + path;
                HttpRequest request = HttpRequest.httpRequestFromUrl(url);
                try {
                    HttpRequestResponse requestResponse = GalaxyMain.burpApi.http().sendRequest(request);
                    GalaxyMain.burpApi.organizer().sendToOrganizer(requestResponse);
                } catch (Exception e) {
                    log.exception(e, "send request fail. request: %s, message: %s", request, e.getMessage());
                }
            }).toList();
            AsyncExecuteUtil.execute(workRunnables, () -> log.infoEvent("Fuzz Sensitive Path execute complete."));
        }
    }

    private Set<String> getEvilPathSet(List<HttpRequestResponse> requestResponses) {
        Set<String> evilUrlSet = new HashSet<>();
        if (!requestResponses.isEmpty()) {
            for (HttpRequestResponse requestResponse : requestResponses) {
                evilUrlSet.addAll(getEvilPathSet(requestResponse));
            }
        }
        return evilUrlSet;
    }

    protected Set<String> getEvilPathSet(HttpRequestResponse requestResponse) {
        Set<String> evilPathSet = new HashSet<>();
        String path = requestResponse.request().path().split("\\?")[0];
        if (Arrays.stream(STATIC_FILE_SUFFIX_ARRAY).anyMatch(path::endsWith)) {
            return evilPathSet;
        }
        evilPathSet.addAll(FuzzSensitivePathGeneratorProviderProvider.generatePayloadSet(path, isBypass()));
        return evilPathSet;
    }

    public boolean isBypass() {
        return false;
    }
}
