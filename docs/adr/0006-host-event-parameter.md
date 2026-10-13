# Every event says which Host App sent it

All three Host Apps send to the same AIQUA app, and the dashboard only splits activity into Android and iOS, so the
React Native app's events would be indistinguishable from the native apps'. Each app therefore adds a `host` parameter
(`android`, `ios` or `react_native`) to every event it logs itself; in `sharedLogic` it's added once, in
`Aiqua.logEvent`. Events the SDK logs itself don't carry it: `app_launched`, and `qg_inapp_*` for popups and inbox
messages. Inbox events can only come from the native apps, which alone have an Inbox; popup events come from every app.

We rejected a separate AIQUA app for React Native, which would split the very events we want to compare. We also
rejected a profile attribute: all three apps set the same user ID, so they share one AIQUA profile and an attribute
would only say which app was launched last. That's also why the existing `platform` profile attribute can't tell the
apps apart.
