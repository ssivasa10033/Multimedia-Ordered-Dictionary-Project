# PRD: Multimedia Ordered Dictionary, v2

**Owner:** Seon Sivasathan
**Implementer:** Claude Code, working phase by phase from this document
**Status:** Draft
**Baseline:** tag `v1.0-assignment`: a working BST dictionary plus CLI that scored 10/10 on the course autograder

---

## 1. Purpose

v1 is a correct but course-shaped program: a default package, `int` type codes, one large `processCommand` method, no build tool, no automated tests, and a dependency on instructor-provided classes.

v2 turns it into a portfolio project that a reviewer can open and immediately see:

| Skill | Shown by |
|---|---|
| **OOP** | Generics, interfaces with several implementations, sealed types and records, a design pattern applied where it is actually needed |
| **Software engineering** | Build tool, CI, tests at several levels, coverage gate, static analysis, versioned releases |
| **Software design** | Layered architecture, dependency inversion, clear module boundaries, ADRs explaining the trade-offs |
| **Data** | Reproducible benchmarks, a real dataset, an analysis notebook with charts, conclusions written up in the README |

The audience is internship recruiters and interviewers, who will spend 2–5 minutes on the repo. The README and the architecture diagram have to make the case on their own.

## 2. Guiding constraints (do not violate)

1. **No copying the tree.** Core dictionary operations must work on the tree directly and must not dump nodes into an array, list or other collection to answer a query. This is the original assignment's central rule and the project's main claim. Iterators may walk the tree using parent pointers with O(1) extra space, and range queries yield results lazily.
2. **Keep v1 behaviour.** Every command and output string from the original spec keeps working. The CLI golden tests (Phase 1) lock this in.
3. **Leave v1 in place.** Keep the original classes under `legacy/` (or leave them reachable from the `v1.0-assignment` tag) so the before/after comparison stays visible.
4. **No course material.** Never commit the assignment handout, the instructor's provided classes, or the autograder's test files.
5. **Small, reviewable steps.** One phase = one branch = one pull request, with tests passing in CI before merge.

## 3. Tech choices

| Area | Choice | Why |
|---|---|---|
| Language | Java 21 (LTS) | Records, sealed interfaces, pattern-matching `switch` |
| Build | Gradle (Kotlin DSL), multi-module | Industry standard; modules enforce the layering |
| Tests | JUnit 5, AssertJ, jqwik (property-based) | Example, parameterized and property tests |
| Quality | Spotless (google-java-format), Checkstyle, JaCoCo, Error Prone | Formatting, lint and a coverage gate in CI |
| Benchmarks | JMH | Correct JVM microbenchmarking (warm-up, forks) |
| API | Javalin (lightweight) or Spring Boot | Pick Javalin unless there's a reason not to (see ADR-004) |
| Analysis | Python 3.12, pandas, matplotlib, Jupyter | The data story |
| Frontend (stretch) | React + TypeScript + Vite + Tailwind | Siv's existing stack |
| CI | GitHub Actions | Build, test and coverage on every PR |

## 4. Target architecture

```
multimedia-dictionary/
├── core/            # Pure data structures. No I/O, no dependencies.
│   └── dev.seon.dict.core
│       ├── OrderedDictionary<K,V>        (interface)
│       ├── BstDictionary<K,V>            (unbalanced, v1 algorithm generalised)
│       ├── AvlDictionary<K,V>            (self-balancing)
│       ├── OrderStatisticDictionary      (rank/select via subtree sizes; may be a feature of AVL)
│       ├── TreeStats                     (height, node count, imbalance)
│       └── DictionaryException hierarchy
├── media/           # Domain model + rendering. Depends on core.
│   ├── MediaItem (sealed) → TextItem, AudioItem, ImageItem, HtmlItem (records)
│   ├── MediaType (enum) + MediaTypeDetector
│   ├── MediaRenderer (interface) → ConsoleRenderer, DesktopRenderer, NoOpRenderer
│   └── MediaLibrary (domain service: OrderedDictionary<String, List<MediaItem>>)
├── cli/             # Command-line app. Depends on media.
│   ├── Command (interface) + GetCommand, AddCommand, NextCommand, ...
│   ├── CommandParser, CommandRegistry
│   └── App (main)
├── api/             # REST service. Depends on media. (Phase 4)
├── bench/           # JMH benchmarks + dataset loaders. Depends on core.
├── analysis/        # Python notebook + generated charts. (Phase 5)
├── web/             # React tree visualiser. (Phase 6, stretch)
├── legacy/          # Original v1 classes, unchanged
└── docs/            # PRD, ADRs, architecture diagram
```

Dependency rule: `core` ← `media` ← `cli` / `api`, and `core` ← `bench`. Nothing in `core` imports anything outside the JDK. Gradle module boundaries enforce this; it is not left to convention.

---

## 5. Phases

Each phase lists its **deliverables**, its **acceptance criteria** (definition of done), and **what it demonstrates**. Phases are ordered by value, so stopping after any phase still leaves a coherent project.

### Phase 0: Engineering foundation
**Deliverables**
- Gradle multi-module build; v1 code moved to `legacy/` and compiling there.
- GitHub Actions workflow: `./gradlew build` (compile, tests, Spotless check, Checkstyle, JaCoCo report) on push and PR.
- `.editorconfig`, Spotless and Checkstyle config, PR template, `CONTRIBUTING.md`.
- `docs/adr/` with ADR-000 (template) and ADR-001 (why Gradle multi-module).

**Acceptance criteria**
- A fresh clone builds with `./gradlew build` and nothing else.
- CI is green on `main`, and the README shows the build badge.

**Demonstrates:** SWE fundamentals, reproducible builds, CI.

### Phase 1: Generic core and a test safety net
**Deliverables**
- `OrderedDictionary<K extends Comparable<? super K>, V>` interface: `get`, `put`, `remove`, `contains`, `size`, `isEmpty`, `min`, `max`, `successor(K)`, `predecessor(K)`, `floor(K)`, `ceiling(K)`.
- `BstDictionary<K,V>`: the v1 leaf-sentinel algorithm, generalised. Keep the leaf-sentinel design and document it in ADR-002.
- In-order iterator using parent pointers, with O(1) auxiliary space. Fail-fast on concurrent modification, using a `modCount` like `java.util` does.
- `range(K lo, K hi)` returning a lazy `Iterable` built on `ceiling` plus successor steps, with no buffering.
- Exception hierarchy: `DictionaryException` → `KeyNotFoundException`.
- **Tests:**
  - Unit tests for every method, including edge cases: empty tree, single node, removing the root, removing a node with zero, one or two internal children, duplicate keys, keys absent at both ends.
  - **Property-based tests (jqwik):** random operation sequences run against `java.util.TreeMap` as an oracle. After each step, contents, size, min, max, successor and predecessor must match. This is the single most impressive test in the repo, so make it thorough.
  - **Structural invariant checker** (`TreeInvariants.check(root)`): BST ordering, parent and child links consistent, leaves have no data, size equals the internal-node count. Run it after every operation in the property tests.
  - **CLI golden tests:** every example from the original spec (`get homework`, `next computer 2`, `prev ping 3`, and so on) with exact expected output.
- JaCoCo gate: at least 90% line coverage on `core`.

**Acceptance criteria**
- The property test runs at least 1,000 random sequences with no failures.
- All v1 spec examples pass as golden tests.
- No core method allocates a collection proportional to n (reviewer check plus a short note in ADR-002).

**Demonstrates:** OOP generics, testing maturity, algorithmic correctness.

### Phase 2: Domain model and CLI redesign (OOP and design patterns)
**Deliverables**
- Replace `int type` with `enum MediaType { TEXT, AUDIO, IMAGE, HTML }`.
- `sealed interface MediaItem permits TextItem, AudioItem, ImageItem, HtmlItem`, with each one a `record`.
- `MediaTypeDetector`: extension-based detection (`.wav`/`.mid` → AUDIO, `.jpg`/`.gif` → IMAGE, `.html` → HTML, else TEXT), unit-tested and open to new formats via a registration map.
- `MediaRenderer` interface with an exhaustive pattern-matching `switch` over the sealed type, or a Visitor; ADR-003 explains the choice. Implementations: `ConsoleRenderer` (prints descriptions), `DesktopRenderer` (opens files with `java.awt.Desktop` when available), `NoOpRenderer` (tests).
- **Command pattern for the CLI:** one class per command implementing `Command { String name(); int arity(); String execute(List<String> args, MediaLibrary lib); }`, registered in a `CommandRegistry`. `processCommand` becomes parse → look up → execute. Add `help`, which lists the commands from the registry.
- `MediaLibrary` service sits between the CLI and the dictionary, handling lowercasing and type filtering, so the CLI never touches tree internals.
- Dependency injection by constructor (no framework). `App` is the composition root.

**Acceptance criteria**
- Adding a new command means adding one class and one registry line. Demonstrate it with `help` and `count k` (the number of items under a key).
- Adding a new media type causes compile errors in every renderer that doesn't handle it, because the sealed switch is exhaustive. Note this in ADR-003.
- All Phase 1 golden tests still pass.

**Demonstrates:** OOP (polymorphism, encapsulation, sealed hierarchies), SOLID (open/closed, dependency inversion), design patterns used on purpose.

### Phase 3: Algorithmic depth
**Deliverables**
- `AvlDictionary<K,V>` implementing the same interface: rotations, height maintenance, rebalancing on insert and on delete.
- **Order statistics:** maintain subtree sizes, then add `rank(K)` (number of keys less than K) and `select(int i)` (the i-th smallest), both O(log n) on AVL.
- `TreeStats`: height, internal-node count, average depth, and a balance-factor histogram.
- Run the full Phase 1 property suite against **every** implementation through a shared abstract test class, so one test suite covers many implementations.
- CLI: `stats` command (height and node count) and `rank k` / `select i`.

**Acceptance criteria**
- The AVL invariant (|balance| ≤ 1 at every node) is checked after each operation in the property tests.
- AVL height stays ≤ 1.44·log₂(n+2) across the property runs.
- Inserting 100,000 sorted keys finishes in under 1 s on AVL. Document the unbalanced BST degrading on the same input; don't hide it.

**Demonstrates:** data structures depth, polymorphic testing, the ability to reason about complexity.

### Phase 4: Persistence and REST API
**Deliverables**
- Save and load the library to JSON (Jackson), with a versioned schema (`"schemaVersion": 1`) and round-trip tests. Loading must go through `put`, so the on-disk format is independent of tree shape.
- `api` module (Javalin):
  - `GET /keys/{k}`: items, or 404 with the nearest predecessor and successor in the body
  - `PUT /keys/{k}/items`, `DELETE /keys/{k}`, `DELETE /keys/{k}/items?type=IMAGE`
  - `GET /keys?from=a&to=m&limit=50`: range query with cursor pagination built on `successor`
  - `GET /stats`
- OpenAPI spec (`docs/openapi.yaml`), request validation and a consistent error format.
- Thread safety: wrap the library in a `ReadWriteLock`. Document why this was chosen over lock-free alternatives (ADR-005).
- Integration tests that start the server on a random port and exercise every endpoint.
- `Dockerfile` (multi-stage) and `docker compose up` that runs the API with a seeded dataset.

**Acceptance criteria**
- `docker compose up` followed by `curl localhost:8080/keys/computer` returns JSON.
- A concurrent stress test (8 threads, mixed reads and writes) passes, with the invariants holding at the end.

**Demonstrates:** API design, layered architecture paying off (the API reuses `media` and `core` unchanged), concurrency, deployment.

### Phase 5: Data, benchmarks and analysis
**Deliverables**
- **Dataset:** a real word list with definitions, e.g. a public-domain dictionary (Webster's 1913 from Project Gutenberg) or an open WordNet export. Add a loader in `bench/` that turns it into dictionary input. Record the source and licence in `data/README.md`, and download it from a script rather than committing large files.
- **JMH benchmarks**, run for each implementation (BST, AVL, `java.util.TreeMap` as the baseline):
  - insert n keys in random, sorted and nearly-sorted orders
  - lookup hit and miss
  - successor walk over the whole tree
  - range query of k results
  - n ∈ {1k, 10k, 100k, 1M}
- Benchmarks export CSV to `analysis/results/`; a Make or Gradle task runs the whole pipeline.
- **Notebook** `analysis/benchmarks.ipynb`:
  - ops/sec vs n (log-log) per implementation and insertion order
  - tree height vs n against the theoretical bounds (log₂ n, 1.44 log₂ n, n)
  - depth distribution of real dictionary keys
  - a short written conclusion for each chart
- Export the charts as PNGs and embed them in the README under a "Results" section.

**Acceptance criteria**
- One command (`make bench` or `./gradlew benchAll`) reproduces the CSVs; the notebook runs top to bottom from those CSVs.
- The README states at least three quantified findings, e.g. "On sorted input, the unbalanced BST degrades to height n; AVL stays at about 1.0x log₂ n and is N times faster at 100k keys."

**Demonstrates:** data skills in a form that reads as engineering: experiment design, reproducibility, visualisation, drawing honest conclusions.

### Phase 6 (stretch): Tree visualiser
- React + TypeScript app in `web/` that calls the API and animates insert, delete and AVL rotations, with a step-through mode.
- Deploy the frontend to GitHub Pages and the API to a free-tier host, or ship a fully client-side build that ports the core algorithm to TypeScript (ADR-006 decides which).
- A 60–90 s demo GIF in the README.

**Demonstrates:** full-stack range and communicating an algorithm visually. This also fits the YouTube "how I built it" format.

---

## 6. Documentation deliverables
- **README** (rewritten after Phase 5): one-paragraph pitch, architecture diagram (Mermaid), quickstart, feature list, the Results charts, "Design decisions" linking to the ADRs, and the v1 → v2 story.
- **ADRs** (`docs/adr/`), at minimum:
  - 001: Gradle multi-module
  - 002: Leaf-sentinel BST and the no-copy rule
  - 003: Sealed types vs Visitor
  - 004: Javalin vs Spring Boot
  - 005: Concurrency strategy
  - 006: Visualiser hosting
- Javadoc on every public type in `core` and `media`.

## 7. Non-goals
- A production key-value store (no disk-backed B-tree, no networking beyond the demo API).
- Replacing `java.util.TreeMap` for real use. The point is to understand and measure it, not to compete with it.
- Authentication or multi-user features on the API.

## 8. Risks and mitigations
| Risk | Mitigation |
|---|---|
| AVL delete-rebalancing bugs | Property tests with the invariant checker after every operation, plus shrinking (jqwik) to get minimal failing cases |
| Benchmarks that mislead (JIT, GC) | JMH only, ≥ 3 forks, warm-up iterations, error bars on the charts |
| Scope creep stalls the project | Phases are independently shippable; Phase 6 is optional |
| Academic integrity | Repo stays private until the course term ends and the course policy allows it; no course material is ever committed |

## 9. Instructions for Claude Code
1. Read this PRD and the existing `src/` before writing code. Confirm the phase you are working on.
2. Work on a branch named `phase-N-<slug>` and open a PR when done. The PR description lists the acceptance criteria and shows how each is met.
3. Write tests alongside or before the implementation. Don't mark a phase done with failing or skipped tests.
4. Respect section 2's constraints, especially the no-copy rule. If a design seems to need an exception, stop and ask.
5. Prefer clarity over cleverness. Add a short comment for every non-obvious invariant (e.g. why leaves are sentinels, what `modCount` protects).
6. Update the README and any ADRs the phase touches in the same PR.

## 10. Resume bullets this should support (draft, fill in real numbers)
- Built a generic ordered-dictionary library in Java 21 with unbalanced BST and AVL implementations behind a common interface. Validated it with property-based tests against `java.util.TreeMap` (1,000+ randomized operation sequences) at 90%+ coverage.
- Redesigned a monolithic CLI using the Command pattern and sealed record hierarchies, cutting the work to add a command to a single class. Exposed the same domain layer through a Dockerized REST API with cursor-paginated range queries.
- Benchmarked the implementations with JMH on a ~100k-word real dictionary. Showed AVL sustaining O(log n) height while the unbalanced BST degraded to height n on sorted input, which made it **N×** slower. Published reproducible results and charts.
