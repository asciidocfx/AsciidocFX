# Copilot providers

JSON catalogs under this folder are loaded at startup. Each file is one provider:

* `kind`: `github-copilot`, `openai-compatible`, `ollama`, or `cli`
* `baseUrl`: OpenAI-style chat completions root (`.../v1`)
* `apiKeyEnv`: environment variable name (Settings can also store the key)
* `models[]`: `id`, `label`, `minRamGb`, `minVramGb`, `stripped`, `notes`

API keys never live in these files. Put keys in Settings / Copilot or in the environment.

On-device models go through [Ollama](https://ollama.com) at `http://127.0.0.1:11434`. AsciidocFX picks a recommended Ollama model from this machine's RAM, leaving about one third free for the editor.
