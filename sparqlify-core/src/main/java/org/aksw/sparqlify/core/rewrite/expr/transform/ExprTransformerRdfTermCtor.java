package org.aksw.sparqlify.core.rewrite.expr.transform;

import java.util.ArrayList;
import java.util.List;

import org.aksw.jena_sparql_api.views.E_RdfTerm;
import org.aksw.jena_sparql_api.views.ExprCopy;
import org.aksw.jena_sparql_api.views.SqlTranslationUtils;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprFunction;

public class ExprTransformerRdfTermCtor
	implements ExprTransformer
{
	
	boolean hasRdfTermCtorArgument(ExprFunction fn) {
		return hasRdfTermCtorArgument(fn.getArgs());
	}
	
	boolean hasRdfTermCtorArgument(Iterable<Expr> exprs) {
		for(Expr expr : exprs) {
			if(expr instanceof E_RdfTerm) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public E_RdfTerm transform(Expr orig, List<E_RdfTerm> terms) {
		List<Expr> args = new ArrayList<Expr>(terms.size());
		for(E_RdfTerm term : terms) {
			// TODO We need to do more checks and logic here
			Expr tmp = term.getLexicalValue();
			args.add(tmp);
		}

		Expr newExpr = ExprCopy.getInstance().copy(orig, args);
		
		E_RdfTerm result = SqlTranslationUtils.expandRdfTerm(newExpr);
		
		return result;
	}
}