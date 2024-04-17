package webserver.view;

public class TemplateViewResolver implements ViewResolver {

    public static final String TEMPLATE_FILE_PATH = "./templates";

    @Override
    public String makeFilePath(String path) {
        if (path.equals("/")) {
            path = "/index.html";
        }
        return TEMPLATE_FILE_PATH + path;
    }
}
