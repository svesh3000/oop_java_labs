package com.svesh.course_work.app;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.app.io.AppRunner;
import com.svesh.course_work.ingest.HttpService;
import com.svesh.course_work.ingest.IngestService;
import com.svesh.course_work.ingest.JsonParser;
import com.svesh.course_work.io.filters.FilterFactory;
import com.svesh.course_work.io.storage.StorageFactory;
import com.svesh.course_work.io.viewer.ViewerFactory;
import com.svesh.course_work.app.modes.AutomaticMode;
import com.svesh.course_work.app.modes.InteractiveMode;

public class AppContext {

    private final ApiRegistry apiRegistry;
    private final AppRunner appRunner;
    private final AutomaticMode automaticMode;
    private final InteractiveMode interactiveMode;

    private AppContext() {
        this.apiRegistry = new ApiRegistry();
        this.appRunner = new AppRunner(
                new IngestService(new HttpService(), new JsonParser()),
                new StorageFactory(),
                new ViewerFactory(),
                new FilterFactory()
        );
        this.automaticMode = new AutomaticMode(appRunner, apiRegistry);
        this.interactiveMode = new InteractiveMode(appRunner, apiRegistry);
    }

    public static AppContext create() {
        return new AppContext();
    }

    public ApiRegistry getApiRegistry() {
        return apiRegistry;
    }

    public AppRunner getAppRunner() {
        return appRunner;
    }

    public AutomaticMode getAutomaticMode() {
        return automaticMode;
    }

    public InteractiveMode getInteractiveMode() {
        return interactiveMode;
    }
}
