# MCP Code Review AI (Monolith)

An automated, AI-driven code review system built with **Spring Boot**, **Spring AI**, and the **Model Context Protocol (MCP)**. This application leverages a local **Ollama** instance to analyze code, identify bugs, and suggest improvements.

> **Note:** This project is currently implemented as a monolithic architecture, but is designed with modularity in mind to facilitate a future transition into microservices (e.g., separating the API gateway from the AI worker services).

## Features

- **Automated Code Analysis:** Submits code snippets or files to an AI model for deep inspection.
- **Spring AI Integration:** Uses Spring AI's robust abstractions to communicate with local LLMs via Ollama.
- **MCP Tooling:** Integrates Model Context Protocol to give the AI model structured tools to interact with the codebase context.
- **Structured JSON Responses:** The AI service (`ReviewAiService`) parses the model's output into strictly typed Java objects (`ReviewResponse`, `Issue`) for easy downstream consumption.

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring AI**
- **Model Context Protocol (MCP)**
- **Ollama** (for local LLM execution)
- **Maven**

## Prerequisites

Before running the application, ensure you have the following installed:

1. **Java 17 or higher**
2. **Maven**
3. **Ollama**: Download and install [Ollama](https://ollama.ai/). 
   - Pull your preferred model (e.g., `llama3` or `mistral`): 
     ```bash
     ollama run llama3
     ```

## Configuration

Sensitive configurations, such as specific LLM parameters or API keys (if using remote models), are managed via `application.yml`. 

> **Important:** `application.yml` is explicitly ignored in `.gitignore` to prevent leaking sensitive information. 

Create a `src/main/resources/application.yml` file based on your environment:

```yaml
spring:
  application:
    name: mcp-code-review-ai
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3
          temperature: 0.2
```

## Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/0xalit/mcp-code-review-ai.git
   cd "mcp-code-review-ai"
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Project Structure

- `com.projects.code_review_ai.web`: REST Controllers handling incoming review requests.
- `com.projects.code_review_ai.gateway`: Gateway service pattern for routing requests.
- `com.projects.code_review_ai.ai`: Core AI logic (`ReviewAiService`, `ReviewPromptBuilder`) orchestrating the ChatClient and structured outputs.
- `com.projects.code_review_ai.mcp`: MCP tool definitions (`CodeReviewTools`) exposed to the LLM.
- `com.projects.code_review_ai.review`: Domain models representing the requests and parsed issues.

## Future Roadmap

- [ ] Transition from Monolith to Microservices (`review-gateway-service` and `ai-worker-service`).
- [ ] Expand MCP toolset for deeper repository context parsing.
- [ ] Add support for GitHub/GitLab webhook integration.
