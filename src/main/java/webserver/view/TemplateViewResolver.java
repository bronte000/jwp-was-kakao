package webserver.view;

public class TemplateViewResolver implements ViewResolver {

    @Override
    public String makeFilePath(String path) {
        return "./templates" + path;
    }
}
