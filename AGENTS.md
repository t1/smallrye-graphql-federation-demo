# AGENTS.md

## Gateway Selection

The GraphQL federation gateway is chosen at runtime via the `GRAPHQL_GATEWAY` environment variable:

| Value | Gateway | Script |
|-------|---------|--------|
| `wundergraph` (default) | [WunderGraph Router](https://wundergraph.com) | `gateway/wundergraph/router` |
| `the-guild-conductor` | [The Guild Conductor](https://github.com/the-guild-org/conductor) | `gateway/the-guild-conductor/conductor` |

The dispatcher is `gateway/gateway`. Unknown values fail fast with an error message.

## Known Limitation: The Guild Conductor

Conductor (v0.0.6-alpha.5 and v1.0.5) **cannot pass field arguments on root query fields** to subgraphs. Its federation query planner generates sub-queries without arguments (`{ order { ... } }` instead of `{ order(id: "1") { ... } }`). The source code has an explicit `// TODO: add arguments` in `libs/federation_query_planner/src/graphql_query_builder.rs`.

**Impact**: `PrinterIT` fails against Conductor with `FieldUndefined@[order]` only when using the simple `graphql` source type (single subgraph). With the `federation` source type + supergraph SDL, the `order` field is recognized but returns `null` because the `id` argument is dropped.

**Workaround**: Use WunderGraph Router (the default) for the integration test. Conductor's federation support is not yet production-ready for queries with root-field arguments.

## Supergraph SDL for Conductor

`gateway/the-guild-conductor/supergraph.graphql` is an Apollo Federation v2 supergraph SDL composed from the three subgraph `_service.sdl` introspection results using `@apollo/composition`. Subgraph URLs are embedded in the `join__Graph` enum. Regenerate it after schema changes.

## Process Cleanup

`PrinterIT` kills the gateway and all descendants on teardown via `pkill -9 -f` matching the gateway binary name (e.g. `conductor.*config\.yaml` or `router@\d+\.\d+-`). This catches reparented child processes that `ProcessHandle.children()` misses. Debug output goes to `printer/target/debug-log.txt`.
