# Deep Search

Deep Search provides a backend entrypoint for turning a research question into a stable research response while keeping workflow internals behind the domain service boundary.

## Language

**Research Question**:
A caller-supplied question that asks the system to produce a research response. It is the only required input in the first public API contract.
_Avoid_: query, prompt, user message

**Research Response**:
The public result returned for a **Research Question**. In the first API contract, it contains a status and a final report only.
_Avoid_: workflow state, trace result, agent output

**Final Report**:
The caller-facing research answer intended to be rendered or stored as the stable output of a request.
_Avoid_: summary, notes, raw notes

**Research Service**:
The domain-level contract that accepts a **Research Question** and returns a **Research Response**, hiding workflow stages, model calls, tools, and internal state from callers.
_Avoid_: agent, workflow, chain

## Example dialogue

Developer: "Can the API caller send model settings or tool details with the request?"
Domain expert: "No. The caller sends a Research Question and receives a Research Response. Tool and model choices stay behind the Research Service."

Developer: "Should the first response include notes or raw notes?"
Domain expert: "No. Those are workflow internals. The public response only commits to status and Final Report."
