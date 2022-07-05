package edu.hawaii.its.groupings.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class PatternPropertyCheckerTest {

    private PatternPropertyChecker patternPropertyChecker;
    private Path resourceDir = Paths.get("src", "test", "resources", "pattern-property-checker");
    private String dirname = resourceDir.toString();

    @Before
    public void setUp() {
        patternPropertyChecker = new PatternPropertyChecker();
    }

    @Test
    public void testNullParam() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(null, null);
        assertTrue(fileLocations.size() == 0);
    }

    @Test
    public void testNoPattern() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname + "/test2", ".txt");
        assertTrue(fileLocations.size() == 0);
    }

    @Test
    public void testNoFile() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname, ".properties");
        assertTrue(fileLocations.size() == 0);
    }

    @Test
    public void testEmptyFile() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname, ".txt");
        assertTrue(fileLocations.size() == 0);
    }

    @Test
    public void testOnePatternFound() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname + "/test1", ".properties");
        assertEquals(1, fileLocations.size());
        assertTrue(fileLocations.get(0).contains(" on line: 2"));
    }

    @Test
    public void testTwoPatternSameFile() {
        // Two instances of pattern in the same file (looks in test2 folder).
        String dirname = Paths.get(resourceDir.toString(), "test2").toString();
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname, ".properties");
        assertEquals(2, fileLocations.size());
        Path path = Paths.get(resourceDir.toString(), "test2", "PatternPropertyCheckerTestFile2.properties");

        assertThat(fileLocations.get(0), endsWith(path.toString() + " on line: 2"));
        assertThat(fileLocations.get(1), endsWith(path.toString() + " on line: 5"));
    }

    @Test
    public void testTwoPatternDiffFile() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname + "/test1", ".txt");
        assertEquals(2, fileLocations.size());
        assertTrue(fileLocations.get(0).contains(" on line: 3"));
        assertTrue(fileLocations.get(1).contains(" on line: 1"));
    }

    @Test
    public void testBadDirectory() {
        List<String> results = patternPropertyChecker.getPatternLocation(null, ".txt");
        assertEquals(0, results.size());

        results = patternPropertyChecker.getPatternLocation("_no_way_", ".txt");
        assertEquals(0, results.size());
    }

    @Test
    public void testBadExtension() {
        List<String> results = patternPropertyChecker.getPatternLocation(dirname + "/test1", null);
        assertEquals(0, results.size());
    }

}