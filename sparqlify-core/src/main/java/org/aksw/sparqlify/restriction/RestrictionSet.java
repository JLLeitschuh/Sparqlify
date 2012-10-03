package org.aksw.sparqlify.restriction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.aksw.sparqlify.config.lang.PrefixSet;
import org.apache.commons.lang.NotImplementedException;

import com.hp.hpl.jena.graph.Node;
import com.karneim.util.collection.regex.PatternPro;



/**
 * A disjunction of restrictions.
 * 
 * Only becomes unsatisfiable if all members are unsatisfiable
 * 
 * @author Claus Stadler <cstadler@informatik.uni-leipzig.de>
 *
 */
public class RestrictionSet
{
	private List<RestrictionImpl> restrictions;// = new ArrayList<? extends IRestriction>();
	
	
	public PrefixSet getUriPrefixes() {
		PrefixSet result = null;
		for(RestrictionImpl r : restrictions) {
			if(result == null) {
				result = r.getUriPrefixes();
			} else {
				PrefixSet tmp = r.getUriPrefixes();
				result.addAll(tmp);
			}
		}

		return result;		
	}
	
	public Type getType() {
		Type t = null;
		for(RestrictionImpl r : restrictions) {
			if(t == null) {
				t = r.getType();
			} else if(r.getType() != t) {
				return Type.UNKNOWN;
			}
		}
		
		if(t == null) {
			return Type.UNKNOWN;
		}
		return t;
	}
	
	public Collection<RestrictionImpl> getRestrictions() {
		return restrictions;
	}
	
	
	public boolean addAlternatives(RestrictionSet rs) {
		if(rs.restrictions == null) {
			return rs.addAlternative(new RestrictionImpl());
		}
			
		boolean result = false;
		for(RestrictionImpl r : rs.restrictions) {
			boolean change = addAlternative(r);
			result = result || change;
		}
		
		return result;
	}
	
	
	public boolean addAlternative(RestrictionImpl r) {
		if(r.getSatisfiability() == Boolean.TRUE) {
			return false;
		}
		
		// TODO Check subsumption, and possibly merge restrictions
		// For now just append the new element
		if(!r.isUnsatisfiable()) {
			if(restrictions == null) {
				restrictions = new ArrayList<RestrictionImpl>();
			}
		
			// Remove all subsumed items
			Iterator<RestrictionImpl> it = restrictions.iterator();
			while(it.hasNext()) {
				RestrictionImpl x = it.next();

				if(x.subsumesOrIsEqual(r)) {
					// since subsumesOrIsEqual is transitive, it means
					// that r cannot subsume anything, as x would
					// already do so
					return false;
				}

				if(r.subsumesOrIsEqual(x)) {
					it.remove();	
				}
			}
			
			restrictions.add(r);
			return true;
		}
		
		return false;
		/*
		RestrictionClass c = r.getRestrictionClass();
		
		classToRestrictions.get(c);*/
	}
	
	//private boolean isUnsatisfiable = false;
	//public Restriction()
	
	public RestrictionSet() {
		restrictions = null; // means 'true'
	}
	
	public RestrictionSet(boolean value) {
		restrictions = (value == false)
				? new ArrayList<RestrictionImpl>()
				: null;
	}
	
	public RestrictionSet(RestrictionImpl restriction) {
		if(restriction.isUnsatisfiable()) {
			restrictions = Collections.emptyList();
		} else {
			restrictions = new ArrayList<RestrictionImpl>();
			restrictions.add(restriction);
		}
	}
	
	public RestrictionSet(List<RestrictionImpl> restrictions) {
		Iterator<RestrictionImpl> it = restrictions.iterator();
		while(it.hasNext()) {
			if(it.next().isUnsatisfiable()) {
				it.remove();
			}
		}
		
		this.restrictions = restrictions;
	}
	
	public RestrictionSet(RestrictionSet other) {
		this.restrictions = new ArrayList<RestrictionImpl>(other.restrictions);
	}
	

	public boolean stateRestriction(RestrictionImpl other) {
		if(restrictions == null) {
			restrictions = new ArrayList<RestrictionImpl>();
			restrictions.add(other.clone());
			
			return true;
		}
		
		boolean result = false;
		Iterator<RestrictionImpl> it = restrictions.iterator();
		while(it.hasNext()) {
			RestrictionImpl r = it.next();
			
			boolean change = r.stateRestriction(other);
			result = result || change;
			if(r.isUnsatisfiable()) {
				it.remove();
			}
		}
		
		return result;
	}

	public boolean stateRestriction(RestrictionSet other) {
		
		if(other.restrictions == null) {
			return false;
		}
		else if(this.restrictions == null) {
			restrictions = new ArrayList<RestrictionImpl>();
						
			for(RestrictionImpl _a : other.restrictions) {
				restrictions.add(_a.clone());
			}

			return true;
		} else {
				
			List<RestrictionImpl> joined = new ArrayList<RestrictionImpl>();
			boolean result = false;
			
			for(RestrictionImpl _a : this.restrictions) {
				
				for(RestrictionImpl b : other.restrictions) {
					RestrictionImpl a  = _a.clone();
					boolean change = a.stateRestriction(b);
					result = result || change;
					
					if(!a.isUnsatisfiable()) {
						joined.add(a);
					}
				}
			}
			
			this.restrictions = joined;
			return result;
		}
	}


	public boolean isUnsatisfiable() {
		return restrictions != null && restrictions.isEmpty();
	}
	
	
	public RestrictionSet clone() {
		if(restrictions == null) {
			return new RestrictionSet();
		}
		
		List<RestrictionImpl> copy = new ArrayList<RestrictionImpl>();
				
		for(RestrictionImpl r : restrictions) {
			copy.add(r.clone());
		}
		
		//Collections.copy(copy, restrictions);		
		
		return new RestrictionSet(copy);
	}
	

	public boolean stateType(Type newType) {
		if(restrictions == null) {
			return stateRestriction(new RestrictionImpl(newType));
		}
		
		
		Iterator<? extends IRestriction> it = restrictions.iterator();
		
		boolean result = false;
		while(it.hasNext()) {
			IRestriction r = it.next();
			boolean change = r.stateType(newType);
			result = result || change;
			
			if(r.isUnsatisfiable()) {
				it.remove();
			}
		}
		
		return result;
	}

	public boolean stateNode(Node newNode) {
		if(restrictions == null) {
			return stateRestriction(new RestrictionImpl(newNode));
		}

		Iterator<? extends IRestriction> it = restrictions.iterator();
		
		boolean result = false;
		while(it.hasNext()) {
			IRestriction r = it.next();
			boolean change = r.stateNode(newNode);
			result = result || change;
			
			if(r.isUnsatisfiable()) {
				it.remove();
			}
		}
		
		return result;
	}

	
	
	public boolean stateUriPrefixes(PrefixSet prefixes) {
		if(restrictions == null) {
			return stateRestriction(new RestrictionImpl(prefixes));
		}
		
		Iterator<? extends IRestriction> it = restrictions.iterator();
		
		boolean result = false;
		while(it.hasNext()) {
			IRestriction r = it.next();
			boolean change = r.stateUriPrefixes(prefixes);
			result = result || change;
			
			if(r.isUnsatisfiable()) {
				it.remove();
			}
		}
		
		return result;
	}

	public void statePattern(PatternPro pattern) {
		throw new NotImplementedException();
	}

	
	public String toString()
	{
		return (restrictions == null)
			? "true"
			: (restrictions.isEmpty()) ? "false" : restrictions.toString();
	}
}

