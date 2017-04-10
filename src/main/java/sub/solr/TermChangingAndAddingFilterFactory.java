package sub.solr;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;


public class TermChangingAndAddingFilterFactory extends TokenFilterFactory {
	public TermChangingAndAddingFilterFactory(Map<String, String> args) {
		super(args);
	}

	@Override
	public TokenStream create(TokenStream ts) {
		return new TermChangingAndAddingFilter(ts);
	}

}
