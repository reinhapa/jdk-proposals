package test;

import javax.lang.model.SourceVersion;
import javax.tools.Tool;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

/**
 * Created by pr on 02.05.2017.
 */
public class DummyTool implements Tool {
    @Override
    public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
        return 0;
    }

    @Override
    public Set<SourceVersion> getSourceVersions() {
        return Collections.singleton(SourceVersion.RELEASE_8);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
