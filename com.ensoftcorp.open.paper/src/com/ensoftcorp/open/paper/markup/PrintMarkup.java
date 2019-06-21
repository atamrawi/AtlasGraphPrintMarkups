package com.ensoftcorp.open.paper.markup;

import java.awt.Color;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.markup.IMarkup;
import com.ensoftcorp.atlas.core.markup.Markup;
import com.ensoftcorp.atlas.core.markup.MarkupProperty;
import com.ensoftcorp.atlas.core.markup.PropertySet;
import com.ensoftcorp.atlas.core.markup.MarkupProperty.LineStyle;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.query.Query;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.pcg.common.PCG;

public class PrintMarkup {
	
	public static final IMarkup BLACK_WHITE_MARKUP = new IMarkup() {
		private final Color NODE_COLOR = Color.WHITE;
		private final Color FOLDER_COLOR = Color.WHITE;
		private final Color DATAFLOW_COLOR = Color.WHITE;
		private final Color CONTROLFLOW_COLOR = Color.WHITE;
		private final Color SHADOW_COLOR = new Color(0, 0, 0, 50);
		
		private final PropertySet NODES = new PropertySet().set(MarkupProperty.NODE_BACKGROUND_COLOR, NODE_COLOR)
				.set(MarkupProperty.NODE_GROUP_COLOR, FOLDER_COLOR)
				.set(MarkupProperty.NODE_BORDER_COLOR, MarkupProperty.Colors.BLACK)
				.set(MarkupProperty.NODE_SHADOW_COLOR, SHADOW_COLOR);
		private final PropertySet DATAFLOW = new PropertySet().set(MarkupProperty.NODE_BACKGROUND_COLOR, DATAFLOW_COLOR)
				.set(MarkupProperty.NODE_GROUP_COLOR, FOLDER_COLOR)
				.set(MarkupProperty.NODE_BORDER_COLOR, MarkupProperty.Colors.BLACK)
				.set(MarkupProperty.NODE_SHADOW_COLOR, SHADOW_COLOR);
		private final PropertySet CONTROLFLOW = new PropertySet()
				.set(MarkupProperty.NODE_BACKGROUND_COLOR, CONTROLFLOW_COLOR)
				.set(MarkupProperty.NODE_GROUP_COLOR, FOLDER_COLOR)
				.set(MarkupProperty.NODE_BORDER_COLOR, MarkupProperty.Colors.BLACK)
				.set(MarkupProperty.NODE_SHADOW_COLOR, SHADOW_COLOR);

		@Override
		public PropertySet get(GraphElement element) {
			if (element.taggedWith(XCSG.ControlFlow_Node) || element.taggedWith(PCG.PCGNode.PCGMasterEntry) || element.taggedWith(PCG.PCGNode.PCGMasterExit)) {
				return CONTROLFLOW;
				// Use for appending line number to node label.
				//SourceCorrespondence sc = (SourceCorrespondence)element.getAttr(XCSG.sourceCorrespondence);
				//if(sc != null) {
				//	return new PropertySet().set(MarkupProperty.LABEL_TEXT, (sc.startLine - 6) + ": " +element.getAttr(XCSG.name));
				//}
			}
			if (element.taggedWith(XCSG.DataFlow_Node)) {
				return DATAFLOW;
			}
			if (element.taggedWith(XCSG.Node)) {
				return NODES;
			}
			if (element instanceof Edge) {
				if (element.taggedWith(XCSG.ControlFlow_Edge) && element.hasAttr(XCSG.conditionValue)) {
					return new PropertySet().set(MarkupProperty.LABEL_TEXT, element.getAttr(XCSG.conditionValue).toString()); 
				}
			}

			return new PropertySet();
		}
	};

	public static IMarkup markup(Q cfg, Q events) {
		Markup printMarkup = new Markup(BLACK_WHITE_MARKUP);

		if(events != null) {
			printMarkup.setNode(events, MarkupProperty.NODE_BACKGROUND_COLOR, Color.LIGHT_GRAY);
		}
		highlightEdges(printMarkup);
		return printMarkup;
	}
	
	public static void highlightEdges(Markup m) {
		Q cfEdge = Query.universe().edges(XCSG.ControlFlow_Edge);
		m.setEdge(cfEdge, MarkupProperty.EDGE_COLOR, Color.BLACK);
		Q cvTrue = Query.universe().selectEdge(XCSG.conditionValue, Boolean.TRUE, "true");
		Q cvFalse = Query.universe().selectEdge(XCSG.conditionValue, Boolean.FALSE, "false");
		m.setEdge(cvTrue, MarkupProperty.EDGE_COLOR, Color.BLACK);
		m.setEdge(cvFalse, MarkupProperty.EDGE_COLOR, Color.BLACK);
		m.setEdge(cvFalse, MarkupProperty.EDGE_STYLE, LineStyle.DASHED);
		m.setEdge(Query.universe().edges(XCSG.ControlFlowBackEdge), MarkupProperty.EDGE_COLOR, Color.BLACK);
	}
	
}
