package sub.solr;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public final class TermChangingFilter extends TokenFilter {
	private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
	private final PositionIncrementAttribute posIncrAttribute = addAttribute(PositionIncrementAttribute.class);
	private final OffsetAttribute offsetAttribute = addAttribute(OffsetAttribute.class);
	private boolean finished;
	private int startOffset;
	private int endOffset;
	private int posIncr;
	private Queue<String> currentTerms;

	public TermChangingFilter(TokenStream input) {
		super(input);
		finished = false;
		startOffset = 0;
		endOffset = 0;
		posIncr = 1;
		this.currentTerms = new LinkedList<String>();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		finished = false;
		startOffset = 0;
		endOffset = 0;
		posIncr = 1;
		currentTerms.clear();
	}

	@Override
	public boolean incrementToken() throws IOException {
		while (!finished) {
			while (thereAreTerms()) {
				String term = currentTerms.poll();

				addToIndex(term);

				posIncr = 0;
				return true;
			}

			if (newToken()) {
				String tokenTerm = termAttribute.toString(); // zB "test"
				startOffset = offsetAttribute.startOffset();
				endOffset = offsetAttribute.endOffset();
				posIncr = 1;

				String changedTerm = change(tokenTerm); // zB "testchanged"

				currentTerms.add(tokenTerm);
				currentTerms.add(changedTerm);
			} else {
				finished = true;
			}
		}
		return false;
	}

	private String change(String term) {
		return term + "changed";
	}

	private boolean thereAreTerms() {
		return currentTerms.size() > 0;
	}

	private boolean newToken() throws IOException {
		return input.incrementToken();
	}
	
	private void addToIndex(String term) {
		termAttribute.copyBuffer(term.toCharArray(), 0, term.length());
		offsetAttribute.setOffset(startOffset, endOffset);
		posIncrAttribute.setPositionIncrement(posIncr);
	}
}
