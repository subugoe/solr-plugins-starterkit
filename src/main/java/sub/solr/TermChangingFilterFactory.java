package sub.solr;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;


public class TermChangingFilterFactory extends TokenFilterFactory {
	public TermChangingFilterFactory(Map<String, String> args) {
		super(args);
	}

	@Override
	public TokenStream create(TokenStream ts) {
		return new TermChangingFilter(ts);
	}

}
