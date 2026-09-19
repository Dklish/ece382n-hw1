# ece382n-hw1

ECE 382N (Distributed Systems) — Assignment 1, Q5: client/server programming
with TCP and UDP sockets. Theory questions (Q1-Q4) are answered separately in
the PDF submitted to Canvas.

## Q5 — Online Library Book Loan System

A single multithreaded `BookServer` tracks a library's book inventory and
loan records. Any number of `BookClient` processes can talk to it
concurrently over TCP or UDP.

### Files

| File                     | Purpose                                                         |
|--------------------------|------------------------------------------------------------------|
| `BookServer.java`        | Server: reads the inventory file, serves TCP + UDP clients       |
| `BookClient.java`        | Client: reads a command file, writes responses to `out_<id>.txt` |
| `sample-data/books.txt`  | Example inventory file (from the assignment PDF)                 |
| `sample-data/command-file1` | Example command file (from the assignment PDF)                |

Both `BookServer.java` and `BookClient.java` are still skeletons — argument
parsing and command dispatch are wired up, but the actual socket/thread
logic is left as `TODO`s to implement.

### Build

```
javac BookServer.java BookClient.java
```

### Run

```
# terminal 1
java BookServer sample-data/books.txt

# terminal 2 (repeat with a different command-file/id for more clients)
java BookClient sample-data/command-file1 1
```

Client 1 writes its responses to `out_1.txt`. Exiting a client tells the
server to dump the current inventory to `inventory.txt` in the current
directory.

### Commands (client input, one per line)

- `set-mode t|u` — switch to TCP (`t`) or UDP (`u`); default is UDP
- `begin-loan <user-name> <book-name>` — borrow a copy of a book
- `end-loan <loan-id>` — return a borrowed book
- `get-loans <user-name>` — list a user's active loans
- `get-inventory` — list every book and its remaining quantity
- `exit` — stop this client and have the server write `inventory.txt`

### Notes

- Book names are quoted (e.g. `"The Letter"`), user names are single words.
- The server must be multithreaded so it can handle multiple clients'
  commands concurrently, and safely serialize updates to shared state
  (inventory + loan records).
- Final submission must be zipped as `EID1_EID2.zip` with no `package`
  declarations, per the assignment PDF.
