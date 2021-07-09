package com.ensoftcorp.open.paper.markup;

import java.awt.Color;

import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.index.common.SourceCorrespondence;
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
	
	public static boolean MARKUP_LINES = false;
	
	public static IMarkup BLACK_WHITE_MARKUP = new IMarkup() {
		private final Color NODE_COLOR = Color.WHITE;
		private final Color FOLDER_COLOR = Color.WHITE;
		private final Color DATAFLOW_COLOR = Color.WHITE;
		private final Color CONTROLFLOW_COLOR = Color.WHITE;
		private final Color NODE_BORDER_COLOR = Color.BLACK;
		private final Color SHADOW_COLOR = new Color(0, 0, 0, 50);
		private final Color EDGE_COLOR = Color.BLACK;
		
		private final PropertySet NODES = new PropertySet()
				.set(MarkupProperty.NODE_BACKGROUND_COLOR, NODE_COLOR)
				.set(MarkupProperty.NODE_GROUP_COLOR, FOLDER_COLOR)
				.set(MarkupProperty.NODE_BORDER_COLOR, NODE_BORDER_COLOR)
				.set(MarkupProperty.NODE_SHADOW_COLOR, SHADOW_COLOR);
		
		private final PropertySet DATAFLOW = new PropertySet()
				.set(MarkupProperty.NODE_BACKGROUND_COLOR, DATAFLOW_COLOR)
				.set(MarkupProperty.NODE_GROUP_COLOR, FOLDER_COLOR)
				.set(MarkupProperty.NODE_BORDER_COLOR, NODE_BORDER_COLOR)
				.set(MarkupProperty.NODE_SHADOW_COLOR, SHADOW_COLOR);
		
		private final PropertySet CONTROLFLOW = new PropertySet()
				.set(MarkupProperty.NODE_BACKGROUND_COLOR, CONTROLFLOW_COLOR)
				.set(MarkupProperty.NODE_GROUP_COLOR, FOLDER_COLOR)
				.set(MarkupProperty.NODE_BORDER_COLOR, NODE_BORDER_COLOR)
				.set(MarkupProperty.NODE_SHADOW_COLOR, SHADOW_COLOR);
		
		private final PropertySet CALL_EDGES = new PropertySet()
				.set(MarkupProperty.EDGE_COLOR, EDGE_COLOR);
		
		private final PropertySet CONTROLFLOW_EDGE = new PropertySet()
				.set(MarkupProperty.EDGE_COLOR, EDGE_COLOR);

		private final PropertySet CONTROLFLOW_BACK_EDGE = new PropertySet()
				.set(MarkupProperty.EDGE_COLOR, EDGE_COLOR)
				.set(MarkupProperty.EDGE_STYLE, LineStyle.DASHED);
		
		private final PropertySet CONTROLFLOW_BACK_TRUE_EDGE = new PropertySet()
				.set(MarkupProperty.EDGE_COLOR, EDGE_COLOR)
				.set(MarkupProperty.EDGE_STYLE, LineStyle.DASHED)
				.set(MarkupProperty.LABEL_TEXT, "true");
		
		private final PropertySet CONTROLFLOW_BACK_FALSE_EDGE = new PropertySet()
				.set(MarkupProperty.EDGE_COLOR, EDGE_COLOR)
				.set(MarkupProperty.EDGE_STYLE, LineStyle.DASHED)
				.set(MarkupProperty.LABEL_TEXT, "false");
		
		private final PropertySet CONTROLFLOW_TRUE_EDGE = new PropertySet()
				.set(MarkupProperty.EDGE_COLOR, EDGE_COLOR)
				.set(MarkupProperty.LABEL_TEXT, "true");
		
		private final PropertySet CONTROLFLOW_FALSE_EDGE = new PropertySet()
				.set(MarkupProperty.EDGE_COLOR, EDGE_COLOR)
				.set(MarkupProperty.LABEL_TEXT, "false");

		@Override
		public PropertySet get(GraphElement element) {
			if (element.taggedWith(PCG.PCGNode.PCGMasterEntry) || element.taggedWith(PCG.PCGNode.PCGMasterExit)) {
				return CONTROLFLOW;
			}
			if(element.taggedWith(XCSG.ControlFlow_Node)) {
				SourceCorrespondence sc = (SourceCorrespondence)element.getAttr(XCSG.sourceCorrespondence);
				if(MARKUP_LINES && sc != null) {
					PropertySet propertySet = new PropertySet();
					propertySet.set(MarkupProperty.NODE_BACKGROUND_COLOR, CONTROLFLOW_COLOR);
					propertySet.set(MarkupProperty.NODE_GROUP_COLOR, FOLDER_COLOR);
					propertySet.set(MarkupProperty.NODE_BORDER_COLOR, NODE_BORDER_COLOR);
					propertySet.set(MarkupProperty.NODE_SHADOW_COLOR, SHADOW_COLOR);
					propertySet.set(MarkupProperty.LABEL_TEXT, (sc.startLine) + ": " +element.getAttr(XCSG.name));
					return propertySet;
				}
				return CONTROLFLOW;
			}
			if (element.taggedWith(XCSG.DataFlow_Node)) {
				return DATAFLOW;
			}
			if (element.taggedWith(XCSG.Node)) {
				return NODES;
			}
			
			if (element.taggedWith(XCSG.ControlFlow_Edge)) {
				if(element.hasAttr(XCSG.conditionValue)) {
					if(Boolean.TRUE.equals(element.getAttr(XCSG.conditionValue))) {
						if(element.taggedWith(XCSG.ControlFlowBackEdge)) {
							return CONTROLFLOW_BACK_TRUE_EDGE;
						}
						return CONTROLFLOW_TRUE_EDGE;
					}
					if(Boolean.FALSE.equals(element.getAttr(XCSG.conditionValue))) {
						if(element.taggedWith(XCSG.ControlFlowBackEdge)) {
							return CONTROLFLOW_BACK_FALSE_EDGE;
						}
						return CONTROLFLOW_FALSE_EDGE;
					}
					PropertySet propertySet = new PropertySet();
					propertySet.set(MarkupProperty.LABEL_TEXT, element.getAttr(XCSG.conditionValue).toString());
					return propertySet.set(MarkupProperty.EDGE_COLOR, EDGE_COLOR);
				}
				if(element.taggedWith(XCSG.ControlFlowBackEdge)) {
					return CONTROLFLOW_BACK_EDGE;
				}
				return CONTROLFLOW_EDGE;
			}
			
			if (element.taggedWith(XCSG.Call)) {
				return CALL_EDGES;
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
		//m.setEdge(cvFalse, MarkupProperty.EDGE_STYLE, LineStyle.DASHED);
		m.setEdge(Query.universe().edges(XCSG.ControlFlowBackEdge), MarkupProperty.EDGE_COLOR, Color.BLACK);
	}
	
}
