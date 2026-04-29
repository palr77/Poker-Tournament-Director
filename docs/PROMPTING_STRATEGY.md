# Prompting Strategy for Codex (Context Management)

**Rule**  
Always start a **fresh chat** for each major prompt.

**Workflow**  
1. New chat → paste MASTER_PROJECT_CONTEXT.md  
2. Paste the specific prompt (e.g. 02_DATA_MODELS.md)  
3. Generate → integrate → test → close chat

**Why this works**  
- Context stays < 20% per session  
- Every prompt has full project knowledge  
- No history bloat  

Use this strategy for the rest of the project and all future apps.