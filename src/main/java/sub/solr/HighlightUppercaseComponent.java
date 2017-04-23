package sub.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.response.SolrQueryResponse;

public class HighlightUppercaseComponent extends SearchComponent {

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		SolrQueryResponse response = rb.rsp;
		@SuppressWarnings("unchecked")
		SimpleOrderedMap<Object> highlightedDocs = (SimpleOrderedMap<Object>) response.getValues().get("highlighting");
		if (highlightedDocs != null) {
			for (int i = 0; i < highlightedDocs.size(); i++) {
				@SuppressWarnings("unchecked")
				SimpleOrderedMap<Object> currentDoc = (SimpleOrderedMap<Object>) highlightedDocs.getVal(i);
				String[] originalHighlightSnippet = (String[]) currentDoc.get("myfield");
				String[] uppercasedHighlightSnippet = toUppercase(originalHighlightSnippet);
				currentDoc.remove("myfield");
				currentDoc.add("myfield", uppercasedHighlightSnippet);
			}
		}
	}

	private String[] toUppercase(String[] originalHighlight) {
		String firstSnippet = originalHighlight[0];
		String allUppercased = firstSnippet;
		List<String> allHighlights = extractUsingRegex("<em>(.*?)</em>", firstSnippet);
		for (String hl : allHighlights) {
			allUppercased = allUppercased.replace("<em>" + hl + "</em>", "<em>" + hl.toUpperCase() + "</em>");
		}
		return new String[]{ allUppercased };
	}

	private List<String> extractUsingRegex(String regex, String s) {
		List<String> results = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			results.add(matcher.group(1));
		}

		if (results.isEmpty()) {
			results.add("");
		}
		return results;
	}

	@Override
	public String getDescription() {
		return "Modifier for highlighted text";
	}

}
