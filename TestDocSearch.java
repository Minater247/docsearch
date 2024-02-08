import static org.junit.Assert.*;
import java.io.File;
import org.junit.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class TestDocSearch {
    @Test
    public void testIndex() throws URISyntaxException, IOException {
        Handler h = new Handler("./technical/");
        URI rootPath = new URI("http://localhost/");
        assertEquals("There are 1391 total files to search.", h.handleRequest(rootPath));
    }

    @Test
    public void testSearch() throws URISyntaxException, IOException {
        Handler h = new Handler("./technical/");
        String sep = File.separator;
        URI rootPath = new URI("http://localhost/search?q=Resonance");
        String expect = String.format(
                "Found 2 paths:\n.%stechnical%sbiomed%sar615.txt\n.%stechnical%splos%sjournal.pbio.0020150.txt", sep,
                sep, sep, sep, sep, sep);
        assertEquals(expect, h.handleRequest(rootPath));
    }

    @Test
    public void testSearchTitleAndQuery() throws URISyntaxException, IOException {
        Handler h = new Handler("./technical/");
        String sep = File.separator;
        URI rootPath = new URI("http://localhost/search?q=have%20the%20same%20value%20scaled%20by&title=research");
        String expect = String.format("Found 1 paths:\n.%stechnical%sbiomed%sgb-2001-3-1-research0001.txt", sep, sep, sep);
        assertEquals(expect, h.handleRequest(rootPath));
    }

    @Test
    public void testTitle() throws URISyntaxException, IOException {
        Handler h = new Handler("./technical/");
        URI rootPath = new URI("http://localhost/search?title=cvm-2");
        String sep = File.separator;
        String[] paths = new String[] {
            String.format(".%stechnical%sbiomed%scvm-2-1-038.txt", sep, sep, sep),
            String.format(".%stechnical%sbiomed%scvm-2-4-180.txt", sep, sep, sep),
            String.format(".%stechnical%sbiomed%scvm-2-4-187.txt", sep, sep, sep),
            String.format(".%stechnical%sbiomed%scvm-2-6-278.txt", sep, sep, sep),
            String.format(".%stechnical%sbiomed%scvm-2-6-286.txt", sep, sep, sep)
        };
        String expect = String.format("Found 5 paths:\n%s\n%s\n%s\n%s\n%s", paths[0], paths[1], paths[2], paths[3], paths[4]);
        assertEquals(expect, h.handleRequest(rootPath));
    }
}
