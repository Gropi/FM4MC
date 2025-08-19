# Protocol Buffers

This directory contains the message definitions used by Edge-Flex for communication between components.

## Structure
- **File/** – `.proto` files describing communication and hardware information messages.
- **Protobuf_Compiler/** – helper files for compiling the definitions (includes a Windows `protoc` distribution).

## System Requirements
- Protocol Buffers compiler `protoc` (version 3 or newer)

## Generating Java Classes
From within the `File` directory run:
```bash
protoc --java_out=../.. CommunicationMessages.proto HardwareInformationMessages.proto
```
The generated classes can then be used by the Java modules.
