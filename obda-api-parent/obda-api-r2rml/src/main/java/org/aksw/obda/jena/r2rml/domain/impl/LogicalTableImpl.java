/**
 * 
 */
package org.aksw.obda.jena.r2rml.domain.impl;

import java.util.Optional;

import org.aksw.jena_sparql_api.rdf.collections.ResourceUtils;
import org.aksw.obda.jena.r2rml.domain.api.LogicalTable;
import org.aksw.obda.jena.r2rml.vocab.RR;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Node;


public class LogicalTableImpl
	extends AbstractMappingComponent
	implements LogicalTable
{
	public LogicalTableImpl(Node node, EnhGraph graph) {
		super(node, graph);
	}
	
	@Override
	public Optional<String> tryGetTableName() {
		Optional<String> result = ResourceUtils.tryGetLiteralPropertyValue(this, RR.tableName, String.class);
		return result;
	}
	
	@Override
	public Optional<String> tryGetQueryString() {
		Optional<String> result = ResourceUtils.tryGetLiteralPropertyValue(this, RR.sqlQuery, String.class);
		return result;
	}

	@Override
	public LogicalTableImpl setTableName(String tableName) {
		removeAll(RR.tableName);
		addLiteral(RR.tableName, tableName);
		return this;
	}
	
	@Override
	public LogicalTableImpl setQueryString(String sqlQuery) {
		removeAll(RR.sqlQuery);
		addLiteral(RR.sqlQuery, sqlQuery);
		return this;
	}

	@Override
	public String toString() {
		return "LogicalTableImpl [tableName=" + tryGetTableName().orElse(null) + ", queryString="
				+ tryGetQueryString().orElse(null) + "]";
	}
}