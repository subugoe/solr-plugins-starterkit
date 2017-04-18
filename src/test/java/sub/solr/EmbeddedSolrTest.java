package sub.solr;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import sub.solr.SolrWrapper;

public class EmbeddedSolrTest {

	private static SolrWrapper solr;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		CoreContainer container = new CoreContainer("solr");
		container.load();
		EmbeddedSolrServer solrEmbedded = new EmbeddedSolrServer(container, "mycore");
		solr = new SolrWrapper(solrEmbedded);
	}

	@After
	public void afterEach() throws Exception {
		solr.clean();
		solr.printResults();
	}

	@Test
	public void shouldFindChangedTerm() throws Exception {
		String[][] doc = { { "id", "id1" }, { "myfield", "bla" } };
		solr.addDocument(doc);

		solr.select("blachanged");

		assertEquals(1, results());
		assertHighlighted("myfield", "bla");
	}

	@Test
	public void shouldSelectWithStarsBeginning() throws Exception {
		String[][] doc = { { "id", "id1" }, { "myfield", "longtext" } };
		solr.addDocument(doc);

		solr.selectWithStars("long");
		assertEquals(1, results());
		assertHighlighted("myfield", "longtext");
	}

	@Test
	public void shouldSelectWithStarsEnding() throws Exception {
		String[][] doc = { { "id", "id1" }, { "myfield", "longtext" } };
		solr.addDocument(doc);

		solr.selectWithStars("text");
		assertEquals(1, results());
		assertHighlighted("myfield", "longtext");
	}

	@Test
	public void shouldSelectWithStarsMiddle() throws Exception {
		String[][] doc = { { "id", "id1" }, { "myfield", "longtext" } };
		solr.addDocument(doc);

		solr.selectWithStars("ongt");
		assertEquals(1, results());
		assertHighlighted("myfield", "longtext");
	}

	@Test
	public void shouldSelectWithUppercase() throws Exception {
		String[][] doc = { { "id", "id1" }, { "myfield", "ein langer Text" } };
		solr.addDocument(doc);

		solr.selectWithUppercase("langer");
		assertEquals(1, results());
		assertHighlighted("myfield", "LANGER");
	}

	@Test
	public void shouldSelectWithUppercaseTwoHighlights() throws Exception {
		String[][] doc = { { "id", "id1" }, { "myfield", "ein langer, sehr langer Text" } };
		solr.addDocument(doc);

		solr.selectWithUppercase("langer");
		assertEquals(1, results());
		assertHighlighted("myfield", "LANGER");
	}

	private long results() {
		return solr.results();
	}

	private String assertHighlighted(String fieldName, String... words) {
		return assertHighlighted(true, fieldName, words);
	}

	private String assertNotHighlighted(String fieldName, String... words) {
		return assertHighlighted(false, fieldName, words);
	}

	private String assertHighlighted(boolean forReal, String fieldName, String... words) {
		String hlText = solr.getHighlightings().get("id1").get(fieldName).get(0);
		for (String word : words) {
			String hlWord = "<em>" + word + "</em>";
			if (forReal) {
				assertThat(hlText, containsString(hlWord));
			} else {
				assertThat(hlText, not(containsString(hlWord)));
			}
		}
		return hlText;
	}

}
