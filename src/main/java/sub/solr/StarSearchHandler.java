package sub.solr;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class StarSearchHandler extends SearchHandler {

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		String oldQuery = req.getParams().get("q");
		
		String newQuery = "*" + oldQuery + "*";
		
		ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
		newParams.set("q", newQuery);
		req.setParams(newParams);

		super.handleRequestBody(req, rsp);
	}
}
