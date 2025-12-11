from dotenv import load_dotenv

# Load environment variables FIRST before any other imports
load_dotenv()

from langgraph.graph import StateGraph, START, END
from state import LLMState
from nodes import (
    generate_risk_report,
    generate_tabular_report,
    calculate_posture_score,
    compile_results
)

def build_graph():
    
    graph = StateGraph(LLMState)

    # nodes definition
    graph.add_node("risk_report", generate_risk_report)
    graph.add_node("tabular_report", generate_tabular_report)
    graph.add_node("calculate_posture_score", calculate_posture_score)
    graph.add_node("compile_results", compile_results)

    # edges definition
    graph.add_edge(START, "risk_report")
    graph.add_edge(START, "tabular_report")
    graph.add_edge("risk_report", "compile_results")
    graph.add_edge("tabular_report", "calculate_posture_score")
    graph.add_edge("calculate_posture_score", "compile_results")
    graph.add_edge("compile_results", END)

    # Compile the graph
    return graph.compile()

def main():
    
    # Build the graph
    workflow = build_graph()
    
    # Set the initial state
    initial_state = {
        "riskReportFileName": "/Users/vaishnavi.lahoti/Desktop/DevicePosture/src/app/posture-patrol/resources/risk_report.md"
    }
    
    print("Invoking workflow...")
    final_state = workflow.invoke(initial_state)
    
    print("\n--- Workflow Finished ---")
    print("Final State:")
    print(final_state)

if __name__ == "__main__":
    main()