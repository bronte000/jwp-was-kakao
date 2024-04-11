package webserver.view;

public class StaticViewResolver implements ViewResolver {

    @Override
    public String makeFilePath(String path) {
        return "./static" + path;
    }
}
