package com.ensoftcorp.open.paper.display;

import com.ensoftcorp.atlas.core.markup.IMarkup;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.query.Query;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.script.CommonQueries;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.atlas.ui.viewer.graph.DisplayUtil;
import com.ensoftcorp.open.paper.markup.PrintMarkup;
import com.ensoftcorp.open.pcg.common.PCG;
import com.ensoftcorp.open.pcg.common.PCGFactory;

public class DisplayUtils {
	
	public static void cfg(Q function) {
		cfg(function, true, true);
	}
	
	public static void cfg(Q function,  boolean extendsContainment, boolean markuplines) {
		cfg(function, null, extendsContainment, markuplines);
	}
	
	public static void cfg(Q function, Q events) {
		cfg(function, events, true, true);
	}
	
	public static void cfg(Q function, Q events, boolean extendsContainment, boolean markupLines) {
		PrintMarkup.MARKUP_LINES = markupLines;
		Q cfg = CommonQueries.cfg(function);
		PCG pcg = PCGFactory.create(cfg, cfg.nodes(XCSG.controlFlowRoot), cfg.nodes(XCSG.controlFlowExitPoint), cfg.nodes(XCSG.ControlFlow_Node));
		cfg = pcg.getPCG();
		if(extendsContainment) {
			cfg = cfg.union(Query.universe().edges(XCSG.Contains).reverseStep(cfg));
		}
		IMarkup markup = PrintMarkup.markup(cfg, events);
		DisplayUtil.displayGraph(markup, cfg.eval(), function.eval().nodes().one().getAttr(XCSG.name).toString());
	}
	
	public static void pcg(Q function, Q events) {
		pcg(function, events, true);
	}
	
	public static void pcg(Q function, Q events, boolean extendsContainment) {
		PCG pcg = PCGFactory.create(events);
		Q pcgQ = pcg.getPCG();
		if(extendsContainment) {
			pcgQ = pcgQ.union(Query.universe().edges(XCSG.Contains).reverse(pcgQ));
		}
		IMarkup markup = PrintMarkup.markup(pcgQ, events);
		DisplayUtil.displayGraph(markup, pcgQ.eval(), function.eval().nodes().one().getAttr(XCSG.name).toString());
	}
	
	public static void acyclicCFG(Q function) {
		acyclicCFG(function, true);
	}
	
	public static void acyclicCFG(Q function, boolean extendsContainment) {
		acyclicCFG(function, null, extendsContainment);
	}
	
	public static void acyclicCFG(Q function, Q events) {
		acyclicCFG(function, events, true);
	}
	
	public static void acyclicCFG(Q function, Q events, boolean extendsContainment) {
		Q cfg = CommonQueries.cfg(function);
		cfg = cfg.differenceEdges(cfg.edges(XCSG.ControlFlowBackEdge));
		IMarkup markup = PrintMarkup.markup(cfg, events);
		cfg = cfg.union(Common.universe().edges(XCSG.Contains).reverse(cfg));
		DisplayUtil.displayGraph(markup, cfg.eval(), function.eval().nodes().one().getAttr(XCSG.name).toString());
	}
	
	public static void styleGraph(Q graph) {
		styleGraph(graph, null, true);
	}
	
	public static void styleGraph(Q graph, Q events) {
		styleGraph(graph, events, true);
	}
	
	public static void styleGraph(Q graph, Q events, boolean extendsContainment) {
		IMarkup markup = PrintMarkup.markup(graph, events);
		if(extendsContainment) {
			graph = graph.union(Common.universe().edges(XCSG.Contains).reverse(graph));
		}
		DisplayUtil.displayGraph(markup, graph.eval());
	}

}
