package webserver.view;

public class StaticViewResolver implements ViewResolver {

    public static final String STATIC_FILE_PATH = "./static";

    @Override
    public String makeFilePath(String path) {
        return STATIC_FILE_PATH + path;
    }
}
