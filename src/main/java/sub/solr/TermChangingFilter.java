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
	private Queue<String> terms;

	public TermChangingFilter(TokenStream input) {
		super(input);
		finished = false;
		startOffset = 0;
		endOffset = 0;
		posIncr = 1;
		this.terms = new LinkedList<String>();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		finished = false;
		startOffset = 0;
		endOffset = 0;
		posIncr = 1;
		terms.clear();
	}

	@Override
	public boolean incrementToken() throws IOException {
		while (!finished) {
			while (terms.size() > 0) {
				String buffer = terms.poll();

				termAttribute.copyBuffer(buffer.toCharArray(), 0, buffer.length());
				offsetAttribute.setOffset(startOffset, endOffset);
				posIncrAttribute.setPositionIncrement(posIncr);

				posIncr = 0;
				return true;
			}

			if (input.incrementToken()) {
				String currentTerm = termAttribute.toString();
				startOffset = offsetAttribute.startOffset();
				endOffset = offsetAttribute.endOffset();
				posIncr = 1;

				String changedTerm = change(currentTerm);
				terms.add(currentTerm);
				terms.add(changedTerm);
			} else {
				finished = true;
			}
		}
		return false;
	}

	private String change(String currentTerm) {
		return currentTerm + "changed";
	}

}
