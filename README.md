# Redis Clone (Java)

A simplified, from-scratch implementation of a Redis-like in-memory key-value store, built in Java using raw TCP sockets, a custom text protocol, and multi-threaded client handling.

## Features

- **Custom protocol**: plain-text command protocol over TCP, similar in spirit to Redis's own RESP protocol
- **Core commands**: `SET`, `GET`, `DEL`, `EXISTS`, `EXPIRE`
- **Multi-client support**: each connection is handled on its own thread, allowing multiple clients to interact with the store simultaneously
- **Thread-safe storage**: all store operations are synchronized to prevent data corruption under concurrent access
- **Lazy key expiration**: `EXPIRE` marks keys with a TTL; expired keys are cleaned up the moment they're next accessed, the same strategy real Redis uses internally

## Architecture

- `Store.java`: the actual key-value storage, backed by a `HashMap`, with a second map tracking expiry timestamps
- `ClientHandler.java`: handles one client connection — reads commands, parses them, and returns responses. Implements `Runnable` so each client runs on its own thread
- `Server.java`: listens on a TCP port and spawns a new `ClientHandler` thread for every incoming connection

## Setup

\`\`\`bash
git clone https://github.com/Ronak07-cell/redis-clone-java.git
cd redis-clone-java
javac -d out src/main/java/redisclone/*.java
\`\`\`

## Usage

Start the server:
\`\`\`bash
java -cp out redisclone.Server
\`\`\`

Connect as a client (in a separate terminal), using `netcat` or any raw TCP client:
\`\`\`bash
nc localhost 6380
\`\`\`

Then issue commands:
\`\`\`
SET name Ronak
GET name
EXISTS name
EXPIRE name 60
DEL name
\`\`\`

## Tech Stack

- Java 20
- Raw `java.net` sockets — no external networking libraries
- `java.util.concurrent`-free multithreading (plain `Thread`/`Runnable`), demonstrating manual thread management and `synchronized` blocks for thread safety

## What I learned

- How to design and parse a simple text-based network protocol
- Java's threading model: `Runnable`, `Thread`, and why `synchronized` is essential when multiple threads share mutable state
- The producer pattern behind server sockets: `accept()` blocks until a connection arrives, then hands it off so the main loop can immediately listen for the next one
- Lazy expiration as a practical alternative to constant background polling
- Debugging real-world file corruption issues during development (large code blocks getting truncated via clipboard, package/folder path mismatches) and building a verification habit (`cat`, `wc -l`, `tail`) to check actual file state rather than trusting an editor's visual display