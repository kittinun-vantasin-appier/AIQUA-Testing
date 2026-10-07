# Repositories hold state; Services are stateless

ViewModels talk only to Repositories (`HomeRepository`, `CartRepository`). Each Repository is the single owner of its
state, exposed as a `StateFlow`, and of the rules that change it. Repositories call Services (`CatalogService`,
`OrderService`), which only fetch or send data and remember nothing. Both layers are interfaces, so a fake can stand in
for either one in unit tests, and a Service can later point at a real backend without touching anything above it.
`HomeRepository` is named after the screen rather than the Catalog because Home is expected to grow beyond Products:
it will become refreshable and may show other, possibly personalised, content.
