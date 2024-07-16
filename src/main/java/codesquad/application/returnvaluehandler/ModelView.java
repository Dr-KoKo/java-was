package codesquad.application.returnvaluehandler;

import java.util.Map;

public class ModelView {
    private final String filePath;
    private final Map<String, Object> model;

    public ModelView(String filePath, Map<String, Object> model) {
        this.filePath = filePath;
        this.model = model;
    }

    public String getFilePath() {
        return filePath;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
