from typing import TypedDict

class LLMState(TypedDict):
    riskReportFileName: str
    tabularReport: dict[str, any]
    postureScore: dict[str, float]