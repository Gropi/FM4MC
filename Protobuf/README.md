# Protobuf (optional)

This directory contains Protocol Buffers (`.proto`) definitions used by the artifact where applicable.

Most reviewers do not need to regenerate protobuf code; the standard build (Docker or Gradle) uses the code as provided.
Use this directory only if you modify `.proto` files and need to regenerate bindings.

---

## Requirements (only if regenerating)
- Protocol Buffers compiler `protoc` (version 3+)

---

## Example: generate Java sources
From `Protobuf/File/`:

```bash
protoc --java_out=../.. CommunicationMessages.proto HardwareInformationMessages.proto
```
