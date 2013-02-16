
import junit.framework.TestCase;


public class LoggerTest extends TestCase {

    private Logger logger;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public LoggerTest(String arg0) {
        super();
        logger = new Logger();
    }

    public final void testAdd() {
        assertEquals(logger.add(20, 30), 50);
    }

    public final void testSubtract() {
        assertEquals(logger.subtract(60, 30), 30);
    }
}