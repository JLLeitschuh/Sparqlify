package org.aksw.sparqlify.core.algorithms;

import java.util.HashSet;
import java.util.Set;

import org.aksw.jena_sparql_api.restriction.RestrictionManagerImpl;
import org.aksw.jena_sparql_api.views.VarsMentioned;
import org.aksw.sparqlify.core.domain.input.Mapping;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpExt;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.sparql.util.NodeIsomorphismMap;

public class OpMapping
    extends OpExt
    implements VarsMentioned
{
    private Mapping mapping;
    private RestrictionManagerImpl restrictions;

    public OpMapping(Mapping mapping, RestrictionManagerImpl restrictions) {
        super("mapping");
        this.mapping = mapping;
        this.restrictions = restrictions;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public RestrictionManagerImpl getRestrictions() {
        return restrictions;
    }

    @Override
    public Op effectiveOp() {
        throw new RuntimeException("Do not call");
    }

    @Override
    public QueryIterator eval(QueryIterator input, ExecutionContext execCxt) {
        throw new RuntimeException("Do not call");	}

    @Override
    public void outputArgs(IndentedWriter out, SerializationContext sCxt) {
        out.print(mapping + " " + restrictions);
        //out.print(join.getViewNames() + " " + join.getRestrictions());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mapping == null) ? 0 : mapping.hashCode());
        result = prime * result
                + ((restrictions == null) ? 0 : restrictions.hashCode());
        return result;
    }

    @Override
    public boolean equalTo(Op obj, NodeIsomorphismMap labelMap) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        OpMapping other = (OpMapping) obj;
        if (mapping == null) {
            if (other.mapping != null)
                return false;
        } else if (!mapping.equals(other.mapping))
            return false;
        if (restrictions == null) {
            if (other.restrictions != null)
                return false;
        } else if (!restrictions.equals(other.restrictions))
            return false;
        return true;
    }

    @Override
    public Set<Var> varsMentioned() {
      Set<Var> result = new HashSet<Var>(getMapping().getVarDefinition().getMap().keySet());

      return result;
    }

}
