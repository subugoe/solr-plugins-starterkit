package sub.solr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.noggit.JSONUtil;

public class SolrState {

	private SolrClient solrServerClient;
	private String solrQueryString = "";
	private SolrDocumentList docList;
	private Map<String, Map<String, List<String>>> highlightings;

	public SolrState(SolrClient newSolrThing) {
		solrServerClient = newSolrThing;
	}

	public void select(String query) {
		ask(new String[][] {}, query, "/select");
	}

	public void select(String[][] extraParams, String query) {
		ask(extraParams, query, "/select");
	}

	public void selectWithStars(String query) {
		ask(new String[][] {}, query, "/select_with_stars");
	}

	private void ask(String[][] extraParams, String query, String requestHandler) {
		solrQueryString = query;
		SolrQuery solrQuery = new SolrQuery(query);
		solrQuery.setRequestHandler(requestHandler);
		solrQuery.set("rows", "500");
		for (String[] parameter : extraParams) {
			solrQuery.set(parameter[0], parameter[1]);
		}
		QueryResponse response;
		try {
			response = solrServerClient.query(solrQuery);
		} catch (Exception e) {
			throw new RuntimeException("Could not execute '" + query + "'", e);
		}

		docList = response.getResults();
		highlightings = response.getHighlighting();
	}

	public String lemma(int resultNumber) {
		return (String) docList.get(resultNumber - 1).getFieldValue("lemma");
	}

	public String id(int resultNumber) {
		return (String) docList.get(resultNumber - 1).getFieldValue("id");
	}

	public Map<String, Map<String, List<String>>> getHighlightings() {
		if (highlightings == null) {
			highlightings = new HashMap<>();
		}
		return highlightings;
	}

	public String jsonHighlights() {
		return JSONUtil.toJSON(highlightings);
	}

	public long results() {
		return docList.getNumFound();
	}

	public void printResults() {
		System.out.println();
		System.out.println(solrQueryString);
		if (docList != null) {
			System.out.println(docList.getNumFound() + " result(s)");
		}
		if (highlightings != null && !highlightings.isEmpty()) {
			System.out.println("hl:");
			System.out.println(jsonHighlights());
		}
		// System.out.println(JSONUtil.toJSON(docList));
	}

	public void printQueryString() {
		System.out.println(solrQueryString);
	}

	public void addDocument(String[][] documentFields) throws Exception {
		SolrInputDocument newDoc = new SolrInputDocument();
		for (String[] docField : documentFields) {
			newDoc.addField(docField[0], docField[1]);
		}
		if (!newDoc.containsKey("id")) {
			newDoc.addField("id", "1234");
		}
		solrServerClient.add(newDoc);
		solrServerClient.commit();
	}

	public void clean() throws Exception {
		solrServerClient.deleteByQuery("*:*");
		solrServerClient.commit();
	}

}
