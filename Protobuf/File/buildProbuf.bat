@echo on

start ..\Protobuf_Compiler\Windows\bin\protoc.exe --java_out=..\..\Edge-Flex\Shared\src\ .\CommunicationMessages.proto
start ..\Protobuf_Compiler\Windows\bin\protoc.exe --java_out=..\..\Edge-Flex\Shared\src\ .\HardwareInformationMessages.proto

